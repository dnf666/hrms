package com.facishare.crm.goal.service;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleDetailObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.service.dto.*;
import com.facishare.crm.goal.utils.CheckIndexMapping;
import com.facishare.crm.goal.utils.SpecialSql;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.appframework.metadata.restdriver.CRMRemoteService;
import com.facishare.paas.appframework.metadata.restdriver.dto.FindEnumsByFieldName;
import com.facishare.paas.metadata.api.INameCache;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDataProxyService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 目标规则配置服务类
 */
@ServiceModule("goal_rule")
@Component
@Slf4j
public class GoalRuleService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private IObjectDataProxyService proxyService;
    @Autowired
    private GoalRuleSaveActionService goalRuleActionService;
    @Autowired
    private GoalRuleCommonService goalRuleCommonService;
    @Autowired
    private ObjectDataServiceImpl objectDataService;
    @Autowired
    private GoalValueCommonService goalValueCommonService;
    @Autowired
    private CRMRemoteService crmRemoteService;

    /**
     * 获取目标规则列表(管理后台)
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("findRuleList")
    public FindRuleList.Result findRuleList(ServiceContext context, FindRuleList.Arg arg) {

        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(context.getUser(),
                GoalObject.GOAL_RULE.getApiName(), arg.buildSearchQuery());

        List<IObjectData> objectDataList = queryResult.getData();
        fillOthersForRuleList(objectDataList);

        return FindRuleList.Result.builder()

                .pageCount(arg.getPageSize())
                .totalCount(queryResult.getTotalNumber())
                .dataList(ObjectDataDocument.ofList(objectDataList))
                .build();
    }

    /**
     * 获取目标规则列表(前端应用)
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("findList")
    public FindRuleList.Result findList(ServiceContext context, FindRuleList.Arg arg) throws
            MetadataServiceException, CrmCheckedException {
        FindRuleList.Result rst = FindRuleList.Result.builder()
                .pageCount(arg.getPageSize())
                .dataList(Lists.newArrayList())
                .totalCount(0).build();
        List<String> deptIds = goalValueCommonService.getVisibleDeptIds(context.getUser(), Boolean.TRUE)
                .getVisibleDeptIds();

        if (CollectionUtils.empty(deptIds)) {
            return rst;
        }

        String sqlWherePartion = "";
        String sqlOrder = " ORDER BY a.last_modified_time DESC ";
        String sqlLimit = String.format("LIMIT %d OFFSET %d", arg.getPageSize(), arg.buildSearchQuery().getOffset());
        if (!arg.getGoalRuleId().isEmpty()) {
            sqlWherePartion += String.format(" AND a.id = '%s' ", arg.getGoalRuleId());
        }
        if (!arg.getGoalRuleDetailId().isEmpty()) {
            sqlWherePartion = String.format(" AND b.id = '%s'", arg.getGoalRuleDetailId());
        }
        //拼装搜索关键字
        if (!Strings.isNullOrEmpty(arg.getName())) {
            sqlWherePartion += String.format(" AND a.name LIKE '%%%s%%'", arg.getName());
        }

        String dataSql = MessageFormat.format(SpecialSql.getQuerySql("GetGoalRuleListSql")
                , String.join(",", deptIds)
                , addQuotationMarks(context.getTenantId())
                , sqlWherePartion, sqlOrder, sqlLimit);

        QueryResult<IObjectData> dataQueryResult = objectDataService.findBySql(dataSql, context.getTenantId(),
                GoalObject
                        .GOAL_RULE
                        .getApiName());

        if (dataQueryResult == null || CollectionUtils.empty(dataQueryResult.getData())) {
            return rst;
        }
        Optional<IObjectData> data = dataQueryResult.getData().stream().findFirst();
        Integer totalNumber = (Integer) data.map(x -> x.get("total_number")).orElse("0");

        return FindRuleList.Result.builder()
                .pageCount(arg.getPageSize())
                .totalCount(totalNumber)
                .dataList(ObjectDataDocument.ofList(dataQueryResult.getData()))
                .build();
    }

    private String addQuotationMarks(String str) {
        if (str == null || str.isEmpty())
            return str;
        return String.format("'%s'", str);
    }

    private void fillOthersForRuleList(List<IObjectData> objectDataList) {
        objectDataList.forEach(objectData -> {
            GoalCheckIndex goalCheckIndex = CheckIndexMapping.getGoalCheckIndex(String.valueOf(objectData.get
                    (GoalRuleObj.CHECK_OBJECT_API_NAME)));
            Optional<GoalCheckIndexItem> goalCheckIndexItem = goalCheckIndex.getItems().stream().filter(x ->
                    x.getCheckFieldApiName().equals(String.valueOf(objectData.get(GoalRuleObj.CHECK_FIELD_API_NAME))))
                    .findAny();
            if (goalCheckIndexItem.isPresent()) {
                objectData.set(GoalRuleObj.CHECK_FIELD_LABEL, goalCheckIndexItem.get().getCheckFieldLabel());
            }

        });
    }

    /**
     * 获取目标考核指标
     *
     * @return
     */
    @ServiceMethod("findGoalCheckIndex")
    public FindGoalCheckIndex.Result findGoalCheckIndex() {

        return FindGoalCheckIndex.Result.builder()
                .dataList(CheckIndexMapping.getCheckIndices())
                .build();
    }


    /**
     * 新增或修改目标规则
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("createOrUpdateRule")
    public CreateOrUpdateRule.Result createOrUpdateRule(ServiceContext context, CreateOrUpdateRule.Arg arg) {

        IObjectData goalRule = arg.getGoalRule().toObjectData();
        sortYear(goalRule);
        List<IObjectData> goalRuleDetails = arg.getGoalRuleDetails() != null ?
                arg.getGoalRuleDetails().stream()
                        .map(ObjectDataDocument::toObjectData)
                        .collect(Collectors.toList()) : Lists.newArrayList();

        String ruleId = goalRuleActionService.createOrUpdateRule(
                context.getUser(),
                goalRule, goalRuleDetails,
                arg.getCheckCircleIds());

        return CreateOrUpdateRule.Result.builder().ruleId(ruleId).build();
    }

    /**
     * 年份排序
     *
     * @param goalRule
     */
    private void sortYear(IObjectData goalRule) {
        List<String> year = goalRule.get(GoalRuleObj.COUNT_FISCAL_YEAR, ArrayList.class);
        List<Integer> iYear = year.stream()
                .map(x -> Integer.parseInt(x))
                .collect(Collectors.toList());
        Collections.sort(iYear);
        year = iYear.stream().map(x -> String.valueOf(x)).collect(Collectors.toList());
        goalRule.set(GoalRuleObj.COUNT_FISCAL_YEAR, year);
    }


    /**
     * 获取规则详情
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("findById")
    public FindById.Result findById(ServiceContext context, FindById.Arg arg) {

        IObjectData goalRuleData = goalRuleCommonService.findGoalRule(context.getUser(), arg.getRuleId());

        List<IObjectData> goalRuleDetailDatas = goalRuleCommonService.findGoalRules(context.getUser(), arg.getRuleId());

        List<String> checkCircleIds = goalRuleCommonService.findGoalRuleApplyCircle(context.getUser(), arg.getRuleId())
                .stream().map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                .collect(Collectors.toList());

        return FindById.Result.builder()
                .goalRule(ObjectDataDocument.of(goalRuleData))
                .goalRuleDetails(ObjectDataDocument.ofList(goalRuleDetailDatas))
                .checkCircleIds(checkCircleIds)
                .subGoalValueNames(buildSubGoalValueName(context, goalRuleData, goalRuleDetailDatas))
                .deletedOptions(buildDeletedOptions(context,goalRuleData))
                .build();
    }


    /**
     * 重新触发目标规则MQ给BI(一个或者多个目标规则ID)
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("secondSendMessage")
    public SecondSendMessage.Result secondSendMessage(ServiceContext context, SecondSendMessage.Arg arg) {
        List<IObjectData> goalRuleDataList = goalRuleCommonService.findGoalRuleByIds(context.getUser(), arg
                .getRuleIds());
        List<IObjectData> goalRuleDetailDataList = goalRuleCommonService.findGoalRulesByIds(context.getUser(), arg
                .getRuleIds());

        goalRuleDataList.forEach(goalRuleData -> {
            String ruleId = goalRuleData.getId();
            List<IObjectData> goalRuleDetailData = goalRuleDetailDataList.stream()
                    .filter(detailData -> detailData.get(GoalRuleObj.GOAL_RULE_ID, String.class).equals(ruleId))
                    .collect(Collectors.toList());
            goalRuleActionService.sendMessage(ObjectAction.CREATE, goalRuleData, goalRuleDetailData);
        });
        return SecondSendMessage.Result.builder().success(true).build();
    }

    /**
     * 当考核维度是客户名称或者产品名称时，返回子目标维度值对应的主属性
     *
     * @param context
     * @param goalRuleData
     * @param goalRuleDetailDatas
     * @return
     */
    @Nullable
    private Map buildSubGoalValueName(ServiceContext context,
                                      IObjectData goalRuleData,
                                      List<IObjectData> goalRuleDetailDatas) {
        String subgoalObjectApiName = goalRuleData.get(GoalRuleObj.SUBGOAL_OBJECT_API_NAME,String.class);
        if(!Strings.isNullOrEmpty(subgoalObjectApiName)){
            String subgoalFieldApiName = goalRuleData.get(GoalRuleObj.SUBGOAL_FIELD_API_NAME,String.class);
            IObjectDescribe objectDescribe = serviceFacade.findObject(context.getTenantId(), subgoalObjectApiName);
            IFieldDescribe fieldDescribe = objectDescribe.getFieldDescribe(subgoalFieldApiName);
            if (fieldDescribe != null && IFieldType.OBJECT_REFERENCE.equals(fieldDescribe.getType())) {
                Set<String> ids = Sets.newHashSet();
                goalRuleDetailDatas.forEach(goalRuleDetail -> ids.addAll(goalRuleDetail.get(GoalRuleDetailObj
                        .SUBGOAL_VALUE, ArrayList.class)));

                try {
                    List<INameCache> nameCaches = proxyService.findRecordName(
                            ActionContextExt.of(context.getUser()).getContext(),
                            ((ObjectReferenceFieldDescribe)fieldDescribe).getTargetApiName(), new ArrayList<>(ids));
                    if(CollectionUtils.notEmpty(nameCaches))
                        return nameCaches.stream()
                            .collect(Collectors.toMap(p -> p.getId(), INameCache::getName));

                } catch (MetadataServiceException e) {
                    log.error("fillSubGoalValueName->findRecordName error", e);
                }
            }
        }

        return null;
    }

    /**
     * 获取已经删除的选项
     * @param context
     * @param goalRuleData
     * @return
     */
    private Map buildDeletedOptions(ServiceContext context,
                                    IObjectData goalRuleData) {
        String subgoalObjectApiName = goalRuleData.get(GoalRuleObj.SUBGOAL_OBJECT_API_NAME,String.class);
        if(!Strings.isNullOrEmpty(subgoalObjectApiName)){
            String subgoalFieldApiName = goalRuleData.get(GoalRuleObj.SUBGOAL_FIELD_API_NAME,String.class);
            if (!Strings.isEmpty(subgoalObjectApiName)) {
                IObjectDescribe objectDescribe = serviceFacade.findObject(context.getTenantId(), subgoalObjectApiName);
                IFieldDescribe fieldDescribe = objectDescribe.getFieldDescribe(subgoalFieldApiName);
                if (fieldDescribe != null && IFieldType.SELECT_ONE.equals(fieldDescribe.getType())) {
                    List<FindEnumsByFieldName.EnumInfo> enumInfos = crmRemoteService.findEnumsByFieldName(
                            context.getTenantId(), subgoalObjectApiName, subgoalFieldApiName, true);
                    if (CollectionUtils.notEmpty(enumInfos)) {
                        return enumInfos.stream()
                                .filter(x -> x.getIsDeleted())
                                .collect(Collectors.toMap(FindEnumsByFieldName.EnumInfo::getItemCode,
                                        FindEnumsByFieldName.EnumInfo::getItemName));
                    }

                }
            }
        }
        return null;
    }

    /**
     * 启用规则
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("enableRule")
    public EnableRule.Result enableRule(ServiceContext context, EnableRule.Arg arg) {
        IObjectData goalRuleData = goalRuleCommonService.findGoalRule(context.getUser(), arg.getRuleId());
        List<IObjectData> goalRuleDetailDatas = goalRuleCommonService.findGoalRules(context.getUser(), arg.getRuleId());
        Map updateMap = Maps.newHashMap();
        updateMap.put(GoalRuleObj.STATUS, "1");
        serviceFacade.updateWithMap(context.getUser(), goalRuleData, updateMap);

        syncGoalValue(context.getUser(), arg.getRuleId());
        goalRuleActionService.sendMessage(ObjectAction.RECOVER, goalRuleData, goalRuleDetailDatas);
        return EnableRule.Result.builder().success(true).build();
    }

    private void syncGoalValue(User user, String ruleId) {
        ParallelUtils.ParallelTask task = ParallelUtils.createParallelTask();
        task.submit(() -> goalValueCommonService.syncGoalValue(user, ruleId));
        try {
            task.run();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }


    /**
     * 编辑时校验当前规则是否配置目标值
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("checkEdit")
    public CheckEdit.Result checkEdit(ServiceContext context, CheckEdit.Arg arg) {
        goalRuleCommonService.findGoalRule(context.getUser(), arg.getRuleId());
        Boolean flag = goalValueCommonService.existGoalValue(context.getUser(), arg.getRuleId());
        return CheckEdit.Result.builder().value(flag).build();
    }

    /**
     * 禁用规则
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("disableRule")
    public DisableRule.Result disableRule(ServiceContext context, DisableRule.Arg arg) {
        IObjectData goalRuleData = goalRuleCommonService.findGoalRule(context.getUser(), arg.getRuleId());
        List<IObjectData> goalRuleDetailDatas = goalRuleCommonService.findGoalRules(context.getUser(), arg.getRuleId());
        Map updateMap = Maps.newHashMap();
        updateMap.put(GoalRuleObj.STATUS, "0");
        serviceFacade.updateWithMap(context.getUser(), goalRuleData, updateMap);
        goalRuleActionService.sendMessage(ObjectAction.INVALID, goalRuleData, goalRuleDetailDatas);
        return DisableRule.Result.builder().success(true).build();
    }

    /**
     * 删除规则
     *
     * @param context
     * @param arg
     * @return
     */
    @ServiceMethod("deleteRule")
    public DeleteRule.Result deleteRule(ServiceContext context, DeleteRule.Arg arg) {
        goalRuleActionService.deleteRule(context.getUser(), arg.getRuleId());

        return DeleteRule.Result.builder().success(true).build();
    }

    /**
     * 企业注册后初始化默认规则
     *
     * @param context
     * @return
     */
    @ServiceMethod("initDefaultRule")
    public InitDefaultRule.Result initDefaultRule(ServiceContext context) {
        goalRuleActionService.initDefaultRule(context.getUser());

        return InitDefaultRule.Result.builder().success(true).build();
    }
}
