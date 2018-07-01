package com.facishare.crm.goal.action;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleDetailObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.crm.goal.service.GoalRuleCommonService;
import com.facishare.crm.goal.service.GoalValueCommonService;
import com.facishare.crm.goal.service.dto.VisibleDeptModel;
import com.facishare.paas.appframework.common.service.dto.QueryDeptInfoByDeptIds;
import com.facishare.paas.appframework.common.service.dto.QueryUserInfoByIds;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardExportAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.common.Strings;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by renlb on 2018/5/10.
 */
public class GoalValueExportAction extends StandardExportAction {
    private GoalRuleCommonService goalRuleCommonService = (GoalRuleCommonService) SpringUtil.getContext()
            .getBean("goalRuleCommonService");
    private GoalValueCommonService goalValueCommonService = (GoalValueCommonService) SpringUtil.getContext()
            .getBean("goalValueCommonService");
    private List<String> deptIdList = Lists.newArrayList();
    private List<String> userIdList = Lists.newArrayList();
    private Integer startMonth = 0;

    @Override
    protected List<IObjectData> findTotalDataList(User user, IObjectDescribe describe, SearchTemplateQuery query) {
        List<IObjectData> objectDataList = findObjectByQuery(user,describe,query).getData();

        handleSubGoal(objectDataList);
        handleCheckObject(objectDataList);
        handleGoalType(objectDataList);

        return objectDataList;
    }

    @Override
    protected List<IFieldDescribe> findFields(String describeApiName, String recordType) {
        List<IFieldDescribe> fieldDescribeList= serviceFacade.getUpdateImportTemplateField(
                actionContext.getUser(),
                objectDescribe);

        if(CollectionUtils.notEmpty(fieldDescribeList)){
            Map<Integer,String> fieldMap = goalValueCommonService.getFieldsMap(startMonth, true);

            List<IFieldDescribe> resultList = Lists.newArrayList();

            for(String field : fieldMap.values()){
                for (IFieldDescribe fieldDescribe : fieldDescribeList){
                    if(fieldDescribe.getApiName().equals(field)){
                        resultList.add(fieldDescribe);
                    }
                }
            }

            return resultList;
        }

        return fieldDescribeList;
    }

    @Override
    protected QueryResult<IObjectData> findObjectByQuery(User user, IObjectDescribe describe, SearchTemplateQuery query) {
        QueryResult<IObjectData> queryResult = new QueryResult<IObjectData>();

        if(CollectionUtils.empty(query.getFilters())){
            return queryResult;
        }
        String ruleId = getFilterValue(query.getFilters(),GoalValueConstants.GOAL_RULE_ID);
        if(Strings.isNullOrEmpty(ruleId)){
            return queryResult;
        }
        String fiscalYear = getFilterValue(query.getFilters(),GoalValueConstants.FISCAL_YEAR);
        if(Strings.isNullOrEmpty(fiscalYear)){
            return queryResult;
        }
        if(!validRule(actionContext.getUser(),ruleId,fiscalYear)){
            return queryResult;
        }
        String detailRuleId = getFilterValue(query.getFilters(),GoalValueConstants.GOAL_RULE_DETAIL_ID);

        if(!validDetailRule(actionContext.getUser(),ruleId,detailRuleId)){
            return queryResult;
        }

        Map<String,List<String>> checkObjectMap = getDeptUserMap(actionContext.getUser(), ruleId);

        if(CollectionUtils.empty(checkObjectMap)){
            return queryResult;
        }

        List<IObjectData> objectDataList = getAllDatas(actionContext.getUser(),ruleId,
                detailRuleId,fiscalYear,checkObjectMap);

        queryResult.setData(objectDataList);
        queryResult.setTotalNumber(objectDataList.size());
        return queryResult;
    }

    private void handleSubGoal(List<IObjectData> objectDataList){
        if(CollectionUtils.empty(objectDataList)){
            return;
        }
        Object oDetailId = objectDataList.get(0).get(GoalValueConstants.GOAL_RULE_DETAIL_ID);
        if(oDetailId == null || Strings.isNullOrEmpty(oDetailId.toString())){
            return;
        }
        IObjectData detailRuleData = serviceFacade.findObjectData(actionContext.getUser()
                ,oDetailId.toString(), GoalObject.GOAL_RULE_DETAIL.getApiName());

        String subGoalName = "";
        if(detailRuleData != null){
            Object oName = detailRuleData.get(GoalRuleDetailObj.SUBGOAL_NAME);
            if(oName != null && !Strings.isNullOrEmpty(oName.toString())){
                subGoalName = oName.toString();
            }
        }

        for(IObjectData objectData : objectDataList){
            objectData.set(GoalValueConstants.GOAL_RULE_DETAIL_ID,subGoalName);
        }
    }

    private void handleGoalType(List<IObjectData> objectDataList){
        if(CollectionUtils.empty(objectDataList)){
            return;
        }
        for(IObjectData objectData : objectDataList){
            Object oGoalType = objectData.get(GoalValueConstants.GOAL_TYPE);
            String goalType = "";
            if(oGoalType != null){
                if(oGoalType.toString().equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())){
                    goalType = GoalEnum.GoalTypeValue.CIRCLE.getLabel();
                }
                if(oGoalType.toString().equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())){
                    goalType = GoalEnum.GoalTypeValue.EMPLOYEE.getLabel();
                }
            }

            objectData.set(GoalValueConstants.GOAL_TYPE, goalType);
        }
    }

    private void handleCheckObject(List<IObjectData> objectDataList){
        if(CollectionUtils.empty(objectDataList)){
            return;
        }

        List<QueryDeptInfoByDeptIds.DeptInfo> deptInfos = getDeptInofs(actionContext.getTenantId(),
                actionContext.getUser().getUserId(),deptIdList);
        List<QueryUserInfoByIds.UserInfo> userInfos = getUserInfos(actionContext.getTenantId(),
                actionContext.getUser().getUserId(),userIdList);

        for(IObjectData objectData : objectDataList){
            Object oGoalType = objectData.get(GoalValueConstants.GOAL_TYPE);
            String goalType = String.valueOf(oGoalType);
            Object oCheckObjectId = objectData.get(GoalValueConstants.CHECK_OBJECT_ID);
            String checkObjectId = String.valueOf(oCheckObjectId);
            String checkObjectName = "";
            if(goalType.equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())){
                List<QueryDeptInfoByDeptIds.DeptInfo> matchDepts = deptInfos.stream()
                        .filter(deptInfo -> deptInfo.getDeptId().equals(checkObjectId))
                        .collect(Collectors.toList());
                if(CollectionUtils.notEmpty(matchDepts)){
                    checkObjectName = matchDepts.get(0).getDeptName();
                }
            }
            if(goalType.equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())){
                List<QueryUserInfoByIds.UserInfo> matchUsers = userInfos.stream()
                        .filter(userInfo -> userInfo.getId().equals(checkObjectId))
                        .collect(Collectors.toList());
                if(CollectionUtils.notEmpty(matchUsers)){
                    checkObjectName = matchUsers.get(0).getName();
                }
            }

            objectData.set(GoalValueConstants.CHECK_OBJECT_ID, checkObjectName);
        }
    }

    private List<QueryDeptInfoByDeptIds.DeptInfo> getDeptInofs(String tenantId,String userId,List<String> deptIds){
        List<QueryDeptInfoByDeptIds.DeptInfo> deptInfos = Lists.newArrayList();

        if(CollectionUtils.notEmpty(deptIds)){
            deptInfos = serviceFacade.getDeptInfoNameByIds(tenantId,userId,deptIds);
        }

        return deptInfos;
    }

    private List<QueryUserInfoByIds.UserInfo> getUserInfos(String tenantId,String userId,List<String> userIds){
        List<QueryUserInfoByIds.UserInfo> deptInfos = Lists.newArrayList();

        if(CollectionUtils.notEmpty(userIds)){
            deptInfos = serviceFacade.getUserNameByIds(tenantId,userId,userIds);
        }

        return deptInfos;
    }

    private List<IObjectData> getAllDatas(User user, String ruleId, String detailRuleId,
                                          String fiscalYear, Map<String, List<String>> deptUserMap) {
        List<IObjectData> objectDataList = Lists.newArrayList();
        List<String> deptIds = deptUserMap.get(GoalEnum.GoalTypeValue.CIRCLE.getValue());
        List<String> userIds = deptUserMap.get(GoalEnum.GoalTypeValue.EMPLOYEE.getValue());
        if (CollectionUtils.notEmpty(deptIds)) {
            objectDataList.addAll(goalValueCommonService.findGoalValues(user, ruleId, detailRuleId,
                    fiscalYear, GoalEnum.GoalTypeValue.CIRCLE.getValue(), deptIds));
        }
        if (CollectionUtils.notEmpty(userIds)) {
            objectDataList.addAll(goalValueCommonService.findGoalValues(user, ruleId, detailRuleId,
                    fiscalYear, GoalEnum.GoalTypeValue.EMPLOYEE.getValue(), userIds));
        }
        return objectDataList;
    }

    private Map<String, List<String>> getDeptUserMap(User user,String ruleId){
        Map<String,List<String>> resultMap = Maps.newHashMap();
        List<IObjectData> applyCircleDatas = goalRuleCommonService.findGoalRuleApplyCircle(user,ruleId);
        if(CollectionUtils.empty(applyCircleDatas)){
            return resultMap;
        }
        List<String> applyCircleIds = applyCircleDatas.stream()
                .map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                .distinct()
                .collect(Collectors.toList());
        VisibleDeptModel visibleDeptModel = null;
        try {
            visibleDeptModel = goalValueCommonService.getVisibleDeptIds(actionContext.getUser(),
                    Boolean.FALSE);
        }catch (CrmCheckedException e){

        }

        if(visibleDeptModel == null || CollectionUtils.empty(visibleDeptModel.getVisibleDeptIds())){
            return resultMap;
        }

        List<String> visibleDeptIds = visibleDeptModel.getVisibleDeptIds();
        visibleDeptIds.retainAll(applyCircleIds);

        if(CollectionUtils.empty(visibleDeptIds)){
            return resultMap;
        }

        List<String> userIds = Lists.newArrayList(actionContext.getUser().getUserId());
        if(CollectionUtils.notEmpty(visibleDeptModel.getDirectSubordinateIds())){
            userIds.addAll(visibleDeptModel.getDirectSubordinateIds());
        }
        List<String> responsibleDeptIds = visibleDeptModel.getResponsibleDeptIds();
        responsibleDeptIds.retainAll(applyCircleIds);
        if(CollectionUtils.notEmpty(responsibleDeptIds)){
            userIds.addAll(goalValueCommonService.getUserIdsByDeptIds(user,responsibleDeptIds));
        }
        resultMap.put(GoalEnum.GoalTypeValue.CIRCLE.getValue(),responsibleDeptIds);
        resultMap.put(GoalEnum.GoalTypeValue.EMPLOYEE.getValue(),
                userIds.stream().distinct().collect(Collectors.toList()));
        deptIdList.addAll(responsibleDeptIds);
        userIdList.addAll(userIds.stream().distinct().collect(Collectors.toList()));

        return resultMap;
    }

    private boolean validDetailRule(User user, String ruleId, String detailRuleId){
        List<IObjectData> detailRules = goalRuleCommonService.findGoalRules(user,ruleId);
        if(Strings.isNullOrEmpty(detailRuleId)){
            if(CollectionUtils.notEmpty(detailRules)){
                return false;
            }
        }else{
            if(CollectionUtils.empty(detailRules) ||
                    detailRules.stream().noneMatch(detailRule -> detailRule.getId().equals(detailRuleId))){
                return false;
            }
        }

        return true;
    }

    private boolean validRule(User user, String ruleId, String fiscalYear){
        IObjectData ruleData = serviceFacade.findObjectData(user, ruleId, GoalObject.GOAL_RULE.getApiName());

        if(ruleData == null || ruleData.isDeleted()){
            return false;
        }

        if(!ruleData.get(GoalRuleObj.STATUS).toString().equals("1")){
            return false;
        }

        if(!ruleData.get(GoalRuleObj.COUNT_FISCAL_YEAR).toString().contains(fiscalYear)){
            return false;
        }

        Object oMonth = ruleData.get(GoalRuleObj.START_MONTH);
        if(oMonth != null){
            startMonth = Integer.valueOf(oMonth.toString());
        }

        return true;
    }

    private String getFilterValue(List<IFilter> filters, String fieldApiName){
        List<IFilter> ruleFilters = filters.stream().filter(
                filter -> filter.getFieldName().equals(fieldApiName))
                .collect(Collectors.toList());

        if(CollectionUtils.notEmpty(ruleFilters)){
            List<String> filterValues = ruleFilters.get(0).getFieldValues();
            if(CollectionUtils.notEmpty(filterValues)){
                return filterValues.get(0);
            }
        }

        return "";
    }
}
