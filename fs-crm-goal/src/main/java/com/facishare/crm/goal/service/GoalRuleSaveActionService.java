package com.facishare.crm.goal.service;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.service.dto.CreateOrUpdateRule;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.mq.RocketMQMessageSender;
import com.facishare.paas.appframework.common.service.OrgService;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.StopWatch;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.dto.SaveMasterAndDetailData;
import com.facishare.paas.common.util.BulkOpResult;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.DocumentBasedBean;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.common.Strings;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service("goalRuleActionService")
@Slf4j
public class GoalRuleSaveActionService {

    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private GoalRuleCommonService goalRuleCommonService;
    @Autowired
    private RocketMQMessageSender goalRuleChangeMessageSender;
    @Autowired
    private GoalValueCommonService goalValueCommonService;

    @Transactional
    public String createOrUpdateRule(User user, IObjectData goalRule,
                                     List<IObjectData> goalRuleDetails,
                                     List<String> checkCircleIds) {

        StopWatch stopWatch = StopWatch.create("GoalRuleActionService->createOrUpdateRule");
        IObjectDescribe goalRuledescribe = serviceFacade.findObject(user.getTenantId(), GoalObject.GOAL_RULE.getApiName());
        IObjectDescribe goalRuleDetaildescribe = serviceFacade.findObject(user.getTenantId(), GoalObject.GOAL_RULE_DETAIL.getApiName());

        setDefaultValueBeforeSave(user, goalRule, goalRuleDetails, goalRuledescribe, goalRuleDetaildescribe);

        Boolean isUpdate = false;
        String ruleId;
        if (goalRule.getId() == null || StringUtils.isNullOrEmpty(goalRule.getId())) {
            ruleId = createRule(user, goalRule, goalRuleDetails, goalRuledescribe, goalRuleDetaildescribe);
        } else {
            ruleId = updateRule(user, goalRule, goalRuleDetails, goalRuleDetaildescribe);
            isUpdate = true;
        }

        saveRuleApplyCircle(user, ruleId, checkCircleIds, isUpdate);

        stopWatch.logSlow(2000);
        return ruleId;
    }

    @Transactional
    public void deleteRule(User user, String ruleId) {
        IObjectData goalRuleData = goalRuleCommonService.findGoalRule(user, ruleId);
        if (String.valueOf(goalRuleData.get(GoalRuleObj.STATUS)).equals("1")) {
            throw new ValidateException("已启用的目标规则不允许删除！");
        }

        List<IObjectData> goalRuleDetails = goalRuleCommonService.findGoalRules(user, ruleId);

        goalRuleCommonService.deleteGoalRule(user, goalRuleData);
        goalRuleCommonService.deleteGoalRuleDetailByRuleId(user, ruleId);
        goalRuleCommonService.deleteGoalRuleApplyCircle(user, ruleId);
        goalRuleCommonService.deleteGoalValue(user, ruleId);

        sendMessage(ObjectAction.UPDATE, goalRuleData, goalRuleDetails);
    }

    public void initDefaultRule(User user) {
        if (existDefaultRule(user)) {
            return;
        }

        String jsonRule = loadJsonFromResource();
        CreateOrUpdateRule.Arg arg = JSON.parseObject(jsonRule, CreateOrUpdateRule.Arg.class);
        IObjectData goalRule = arg.getGoalRule().toObjectData();
        List<IObjectData> goalRuleDetails = arg.getGoalRuleDetails() != null ?
                arg.getGoalRuleDetails().stream()
                        .map(data -> data.toObjectData())
                        .collect(Collectors.toList()) : Lists.newArrayList();

        //设置统计财年
        goalRule.set(GoalRuleObj.COUNT_FISCAL_YEAR, getFiscalYears());

        createOrUpdateRule(user, goalRule, goalRuleDetails, arg.getCheckCircleIds());
    }

    private boolean existDefaultRule(User user) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1);
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalRuleObj.DEFINE_TYPE, "system");
        searchQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_RULE.getApiName(), searchQuery);
        return CollectionUtils.notEmpty(queryResult.getData()) ? true : false;
    }

    private List<String> getFiscalYears() {
        List<String> years = Lists.newArrayList();
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        for (int year = currentYear - 1; year <= currentYear + 1; year++) {
            years.add(String.valueOf(year));
        }

        return years;
    }

    private String loadJsonFromResource() {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            return IOUtils.toString(classLoader.getResource("default_goal_rule.json"), "UTF-8");
        } catch (IOException e) {
            log.error("loadJsonFromResource error");
            return "";
        }
    }

    private void setDefaultValueBeforeSave(User user, IObjectData goalRule,
                                           List<IObjectData> goalRuleDetails,
                                           IObjectDescribe goalRuledescribe,
                                           IObjectDescribe goalRuleDetaildescribe
    ) {
        setDefaultValue(user, goalRule, goalRuledescribe);

        goalRuleDetails.forEach(data ->
                setDefaultValue(user, data, goalRuleDetaildescribe)
        );
    }

    private void setDefaultValue(User user, IObjectData objectData,
                                 IObjectDescribe describe
    ) {
        objectData.setTenantId(user.getTenantId());
        objectData.setDescribeApiName(describe.getApiName());
        objectData.setDescribeId(describe.getId());
    }

    private String createRule(User user, IObjectData goalRule,
                              List<IObjectData> goalRuleDetails,
                              IObjectDescribe goalRuledescribe,
                              IObjectDescribe goalRuleDetaildescribe
    ) {

        Map<String, IObjectDescribe> objectDescribesMap = new HashMap<>();
        objectDescribesMap.put(GoalObject.GOAL_RULE.getApiName(), goalRuledescribe);
        objectDescribesMap.put(GoalObject.GOAL_RULE_DETAIL.getApiName(), goalRuleDetaildescribe);

        //set id
        String id = serviceFacade.generateId();
        goalRule.setId(id);
        setMasterId(goalRuleDetails, id);

        Map<String, List<IObjectData>> detailObjectDataMap = new HashMap<>();
        detailObjectDataMap.put(GoalObject.GOAL_RULE_DETAIL.getApiName(), goalRuleDetails);
        SaveMasterAndDetailData.Arg arg = SaveMasterAndDetailData.Arg.builder()
                .masterObjectData(goalRule)
                .detailObjectData(detailObjectDataMap)
                .objectDescribes(objectDescribesMap)
                .build();
        SaveMasterAndDetailData.Result saveResult = serviceFacade.saveMasterAndDetailData(user, arg);

        sendMessage(ObjectAction.CREATE, saveResult.getMasterObjectData(),
                saveResult.getDetailObjectData().get(GoalObject.GOAL_RULE_DETAIL.getApiName()));
        return id;
    }

    private void setMasterId(List<IObjectData> goalRuleDetails, String id) {
        if (CollectionUtils.notEmpty(goalRuleDetails)) {
            for (IObjectData goalRuleDetail : goalRuleDetails) {
                goalRuleDetail.set(GoalRuleObj.GOAL_RULE_ID, id);
            }
        }
    }


    private String updateRule(User user, IObjectData goalRule,
                              List<IObjectData> goalRuleDetails,
                              IObjectDescribe goalRuleDetaildescribe) {

        if(goalValueCommonService.existGoalValue(user,goalRule.getId())){
            throw new ValidateException("该目标规则下已经配置了目标值，不允许修改目标规则！");
        }

        //更新主目标
        IObjectData masterObjectData = serviceFacade.updateObjectData(user, goalRule);

        if (String.valueOf(masterObjectData.get(GoalRuleObj.STATUS)).equals("1")) {
            throw new ValidateException("已启用的目标规则不允许修改！");
        }

        //新增子目标数据
        List<IObjectData> resultCreateObjectDataList = Lists.newArrayList();
        List<IObjectData> dataToCreate = goalRuleDetails.stream().filter(x -> Objects.isNull(x.getId())
                || Strings.isNullOrEmpty(x.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.notEmpty(dataToCreate)) {
            setMasterId(dataToCreate, goalRule.getId());
            resultCreateObjectDataList = serviceFacade.bulkSaveObjectData(dataToCreate, user);
        }

        //删除子目标数据
        List<IObjectData> oldDetailDataList = serviceFacade.findDetailObjectDataList(goalRuleDetaildescribe, goalRule, user);
        Set<String> newIdList = goalRuleDetails.stream()
                .filter(it -> it.getId() != null)
                .map(IObjectData::getId)
                .collect(Collectors.toSet());
        List<IObjectData> dataToDelete = oldDetailDataList.stream()
                .filter(x -> !newIdList.contains(x.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.notEmpty(dataToDelete)) {
            serviceFacade.bulkInvalidAndDeleteWithSuperPrivilege(dataToDelete, user);
        }

        //更新子目标数据
        Set<String> oldIdList = oldDetailDataList.stream()
                .filter(it -> it.getId() != null)
                .map(IObjectData::getId)
                .collect(Collectors.toSet());
        List<IObjectData> dataToUpdate = Lists.newArrayList();
        goalRuleDetails.stream().filter(x -> Objects.nonNull(x.getId()) && oldIdList.contains(x.getId())).forEach(x -> {
            dataToUpdate.add(x);
        });
        if (CollectionUtils.notEmpty(dataToUpdate)) {
            BulkOpResult bulkOpResult = serviceFacade.parallelBulkUpdateObjectData(user, dataToUpdate, false, null);
            bulkOpResult.getSuccessObjectDataList();
        }

        List<IObjectData> resultObjectDataList = Lists.newArrayList();
        resultObjectDataList.addAll(resultCreateObjectDataList);
        resultObjectDataList.addAll(dataToUpdate);

        sendMessage(ObjectAction.UPDATE, masterObjectData, resultObjectDataList);
        return goalRule.getId();
    }

    private void saveRuleApplyCircle(User user, String ruleId, List<String> checkCircleIds, Boolean isUpdate) {
        IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), GoalObject.GOAL_RULE_APPLY_CIRCLE.getApiName());

        if (isUpdate) {
            goalRuleCommonService.deleteGoalRuleApplyCircle(user, ruleId);
        }

        List<IObjectData> dataToCreate = Lists.newArrayList();
        checkCircleIds.forEach(circleId -> {
            IObjectData goalRuleApplyCircleObj = new ObjectData();

            goalRuleApplyCircleObj.set(GoalRuleObj.GOAL_RULE_ID, ruleId);
            goalRuleApplyCircleObj.set(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID, circleId);
            goalRuleApplyCircleObj.setTenantId(user.getTenantId());
            goalRuleApplyCircleObj.setCreatedBy(user.getUserId());
            goalRuleApplyCircleObj.setLastModifiedBy(user.getUserId());
            goalRuleApplyCircleObj.set(IObjectData.DESCRIBE_ID, describe.getId());
            goalRuleApplyCircleObj.set(IObjectData.DESCRIBE_API_NAME, describe.getApiName());
            goalRuleApplyCircleObj.set(IObjectData.PACKAGE, "CRM");
            goalRuleApplyCircleObj.set(IObjectData.VERSION, describe.getVersion());

            dataToCreate.add(goalRuleApplyCircleObj);
        });

        if (CollectionUtils.notEmpty(dataToCreate)) {
            serviceFacade.bulkSaveObjectData(dataToCreate, user);
        }
    }

    /**
     * 给BI发送目标规则变动消息
     *
     * @param action
     * @param goalRule
     * @param goalRuleDetails
     */
    public void sendMessage(ObjectAction action, IObjectData goalRule, List<IObjectData> goalRuleDetails) {
        if (goalRule == null) {
            return;
        }

        goalRule.set("action", action.getActionCode());
        if (CollectionUtils.notEmpty(goalRuleDetails)) {
            List<Map> subGoals = goalRuleDetails.stream()
                    .map(x -> ((DocumentBasedBean) x).getContainerDocument())
                    .collect(Collectors.toList());
            goalRule.set("subgoals", subGoals);
        }

        byte[] bytes = JSON.toJSONBytes(goalRule);
        if (bytes != null) {
            log.debug("send goal rule change message,tenantId:{},message:{}", goalRule.getTenantId(), goalRule.toJsonString());
            goalRuleChangeMessageSender.sendMessage(bytes);
        }
    }
}
