package com.facishare.crm.goal.service;

import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleDetailObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("goalRuleCommonService")
@Slf4j
public class GoalRuleCommonService {

    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private ObjectDataServiceImpl objectDataService;

    public IObjectData findGoalRule(User user, String ruleId) {
        IObjectData goalRuleData = serviceFacade.findObjectData(user, ruleId, GoalObject.GOAL_RULE.getApiName());
        if (goalRuleData == null) {
            throw new ValidateException("数据不存在！");
        }
        return goalRuleData;
    }


    public List<IObjectData> findGoalRuleByIds(User user, List<String> ruleIds) {
        //获取传入的目标规则ID
        List<IObjectData> goalRuleData = serviceFacade.findObjectDataByIdsIncludeDeleted(user, ruleIds, GoalObject.GOAL_RULE.getApiName());
        if (goalRuleData.size() == 0) {
            throw new ValidateException("数据不存在！");
        }
        return goalRuleData;
    }

    public Map<Integer, String> getMonthData() {
        Map<Integer, String> data = new HashMap<>();

        data.put(1, GoalValueConstants.JANUARY_VALUE);
        data.put(2, GoalValueConstants.FEBRUARY_VALUE);
        data.put(3, GoalValueConstants.MARCH_VALUE);
        data.put(4, GoalValueConstants.APRIL_VALUE);
        data.put(5, GoalValueConstants.MAY_VALUE);
        data.put(6, GoalValueConstants.JUNE_VALUE);
        data.put(7, GoalValueConstants.JULY_VALUE);
        data.put(8, GoalValueConstants.AUGUST_VALUE);
        data.put(9, GoalValueConstants.SEPTEMBER_VALUE);
        data.put(10, GoalValueConstants.OCTOBER_VALUE);
        data.put(11, GoalValueConstants.NOVEMBER_VALUE);
        data.put(12, GoalValueConstants.DECEMBER_VALUE);
        return data;
    }

    public IObjectData findDetailRuleById(User user, String detailRuleId) {
        if (StringUtils.isNullOrEmpty(detailRuleId)) {
            return null;
        }

        IObjectData detailRuleData = serviceFacade.findObjectData(user, detailRuleId, GoalObject.GOAL_RULE_DETAIL
                .getApiName());

        if (detailRuleData == null) {
            throw new ValidateException("数据不存在！");
        }

        return detailRuleData;
    }

    public List<IObjectData> findGoalRuleApplyCircle(User user, String ruleId) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalRuleObj.GOAL_RULE_ID, ruleId);
        SearchUtil.fillFilterEq(filters, GoalRuleObj.IS_DELETED, "0");
        searchQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_RULE_APPLY_CIRCLE.getApiName(), searchQuery);
        return queryResult.getData();
    }

    public List<IObjectData> findGoalRules(User user, String ruleId) {
        //获取目标子规则
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalRuleObj.GOAL_RULE_ID, ruleId);
        SearchUtil.fillFilterEq(filters, GoalRuleObj.IS_DELETED, "0");
        searchQuery.setFilters(filters);
        return serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_RULE_DETAIL.getApiName(), searchQuery)
                .getData();
    }


    public List<IObjectData> findGoalRulesByIds(User user, List<String> ruleIds) {
        //获取传入的目标子规则ID
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterIn(filters, GoalRuleObj.GOAL_RULE_ID, ruleIds);
        searchQuery.setFilters(filters);
        return serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_RULE_DETAIL.getApiName(), searchQuery)
                .getData();
    }

    public void deleteGoalRule(User user, IObjectData objectData) {
        try {
            objectDataService.invalid(objectData, ActionContextExt.of(user).getContext());
            objectDataService.delete(objectData, ActionContextExt.of(user).getContext());
        } catch (MetadataServiceException e) {
            log.error("deleteGoalRule error ,tenantId {}", user.getTenantId(), e);
            throw new MetaDataBusinessException(e.getMessage());
        }
    }

    public void deleteGoalRuleDetailByRuleId(User user, String ruleId) {
        SearchTemplateQuery query = new SearchTemplateQuery();
        SearchUtil.fillFilterEq(query.getFilters(), GoalRuleObj.GOAL_RULE_ID, ruleId);
        this.deleteBySearchTemplate(user, GoalObject.GOAL_RULE_DETAIL.getApiName(), query);
    }

    public void deleteGoalRuleApplyCircle(User user, String ruleId) {
        SearchTemplateQuery query = new SearchTemplateQuery();
        SearchUtil.fillFilterEq(query.getFilters(), GoalRuleObj.GOAL_RULE_ID, ruleId);
        this.deleteBySearchTemplate(user, GoalObject.GOAL_RULE_APPLY_CIRCLE.getApiName(), query);
    }

    public void deleteGoalValue(User user, String ruleId) {
        SearchTemplateQuery query = new SearchTemplateQuery();
        SearchUtil.fillFilterEq(query.getFilters(), GoalRuleObj.GOAL_RULE_ID, ruleId);
        this.deleteBySearchTemplate(user, GoalObject.GOAL_VALUE.getApiName(), query);
    }

    public Map<String,String> getRuleNameIdMap(User user, List<String> ruleNameList){
        Map<String, String> nameIdMap = Maps.newHashMap();

        List<IObjectData> objectDataList = getRulesByNames(user, ruleNameList);

        if (CollectionUtils.notEmpty(objectDataList)) {
            for (String name : ruleNameList) {
                List<IObjectData> matchData = objectDataList.stream()
                        .filter(objectData -> objectData.getName().equals(name))
                        .collect(Collectors.toList());
                if(CollectionUtils.notEmpty(matchData)){
                    nameIdMap.put(name, matchData.get(0).getId());
                }
            }
        }

        return nameIdMap;
    }

    public List<IObjectData> getRulesByNames(User user, List<String> ruleNameList) {
        List<IObjectData> objectDataList = Lists.newArrayList();

        if (CollectionUtils.empty(ruleNameList)) {
            return objectDataList;
        }

        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(ruleNameList.size());
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalRuleObj.IS_DELETED, "0");
        SearchUtil.fillFilterIn(filters, IObjectData.NAME, ruleNameList);
        searchQuery.setFilters(filters);

        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_RULE.getApiName(), searchQuery);

        if (queryResult != null) {
            objectDataList = queryResult.getData();
        }

        return objectDataList;
    }

    public Map<String,String> getSubGoalNameIdMap(User user, List<String> subGoalNameList){
        Map<String, String> nameIdMap = Maps.newHashMap();

        List<IObjectData> objectDataList = getDetailRulesByNames(user, subGoalNameList);

        if (CollectionUtils.notEmpty(objectDataList)) {
            for (String name : subGoalNameList) {
                List<IObjectData> matchData = objectDataList.stream()
                        .filter(objectData -> objectData.get(GoalRuleDetailObj.SUBGOAL_NAME).toString().equals(name))
                        .collect(Collectors.toList());
                if(CollectionUtils.notEmpty(matchData)){
                    nameIdMap.put(name, matchData.get(0).getId());
                }
            }
        }

        return nameIdMap;
    }

    public Map<String, List<IObjectData>> getSubGoalNameDataMap(User user, List<String> subGoalNameList) {
        Map<String, List<IObjectData>> map = Maps.newHashMap();

        List<IObjectData> objectDataList = getDetailRulesByNames(user, subGoalNameList);

        if (CollectionUtils.notEmpty(objectDataList)) {
            for (String name : subGoalNameList) {
                List<IObjectData> objectDatas = objectDataList.stream()
                        .filter(objectData -> objectData.get(GoalRuleDetailObj.SUBGOAL_NAME).toString().equals(name))
                        .collect(Collectors.toList());
                if (CollectionUtils.notEmpty(objectDatas)) {
                    map.put(name, objectDatas);
                }
            }
        }

        return map;
    }

    public List<IObjectData> getDetailRulesByNames(User user, List<String> subGoalNameList) {
        List<IObjectData> objectDataList = Lists.newArrayList();

        if (CollectionUtils.empty(subGoalNameList)) {
            return objectDataList;
        }

        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(0);
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalRuleObj.IS_DELETED, "0");
        SearchUtil.fillFilterIn(filters, GoalRuleDetailObj.SUBGOAL_NAME, subGoalNameList);
        searchQuery.setFilters(filters);

        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_RULE_DETAIL.getApiName(), searchQuery);

        if (queryResult != null) {
            objectDataList = queryResult.getData();
        }

        return objectDataList;
    }

    private void deleteBySearchTemplate(User user, String apiName, SearchTemplateQuery searchTemplateQuery) {
        try {
            objectDataService.deleteBySearchTemplate(user.getTenantId(), apiName, searchTemplateQuery,
                    ActionContextExt.of(user).getContext());
        } catch (MetadataServiceException e) {
            log.error("deleteBySearchTemplate error ,tenantId {},apiName {},filters {}", user.getTenantId(), apiName,
                    searchTemplateQuery, e);
            throw new MetaDataBusinessException(e.getMessage());
        }
    }
}
