package com.facishare.crm.goal.service;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.crm.goal.service.dto.VisibleDeptModel;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.service.OrgService;
import com.facishare.paas.appframework.common.service.dto.*;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectLockStatus;
import com.facishare.paas.appframework.common.util.Tuple;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by renlb on 2018/4/23.
 */
@Service("goalValueCommonService")
@Slf4j
public class GoalValueCommonService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private GoalRuleCommonService goalRuleCommonService;
    @Autowired
    private OrgService orgService;

    public List<IObjectData> findGoalValues(User user,
                                            String goalRuleId,
                                            String goalRuleDetailId,
                                            String fiscalYear,
                                            String goalType,
                                            List<String> checkIds) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setLimit(1000);
        searchTemplateQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalRuleObj.GOAL_RULE_ID, goalRuleId);

        if (!StringUtils.isNullOrEmpty(goalRuleDetailId)) {
            SearchUtil.fillFilterEq(filters, GoalRuleObj.GOAL_RULE_DETAIL_ID, goalRuleDetailId);
        }

        SearchUtil.fillFilterEq(filters, GoalValueConstants.FISCAL_YEAR, fiscalYear);
        SearchUtil.fillFilterEq(filters, GoalValueConstants.GOAL_TYPE, goalType);

        if (CollectionUtils.notEmpty(checkIds)) {
            SearchUtil.fillFilterIn(filters, GoalValueConstants.CHECK_OBJECT_ID, checkIds);
        }

        searchTemplateQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_VALUE.getApiName(),
                searchTemplateQuery);
        return queryResult.getData();
    }

    public Boolean existGoalValue(User user, String goalRuleId) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setLimit(1);
        searchTemplateQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalRuleObj.GOAL_RULE_ID, goalRuleId);
        SearchUtil.fillFilterNotEq(filters, GoalValueConstants.ANNUAL_VALUE, 0);

        searchTemplateQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user,
                GoalObject.GOAL_VALUE.getApiName(),
                searchTemplateQuery);
        return CollectionUtils.notEmpty(queryResult.getData()) ? Boolean.TRUE : Boolean.FALSE;
    }

    public void lockGoalValue(User user, String goalRuleId, String goalRuleDetailId, String fiscalYear, Boolean lock) {
        int offSet = 0;
        while (true) {
            int r = batchLockGoalValue(user, goalRuleId, goalRuleDetailId, fiscalYear, lock, offSet);
            offSet = r;
            if (0 == r) {
                break;
            }
        }
    }

    public List<String> getUserIdsByDeptIds(User user, List<String> deptIds) {
        List<String> userIds = Lists.newArrayList();

        //获取主属部门员工
        Map<String, List<QueryMemberInfosByDeptIds.Member>> memberMap = serviceFacade
                .getMemberInfoMapByDeptIds(user,
                        deptIds,
                        Boolean.FALSE,
                        null,
                        1);

        if (CollectionUtils.notEmpty(memberMap)) {
            memberMap.values().forEach(members ->
                    members.forEach(member -> userIds.add(member.getId())));
        }

        return userIds.stream().distinct().collect(Collectors.toList());
    }

    public VisibleDeptModel getVisibleDeptIds(User user, Boolean needQuotationMarks) throws CrmCheckedException {
        VisibleDeptModel visibleDeptModel = new VisibleDeptModel();
        List<String> deptIds = Lists.newArrayList();
        List<String> allSubordinateIds = Lists.newArrayList(user.getUserId());
        //负责的部门
        List<String> responsibleDeptIds = getResponsibleDeptIds(user);
        deptIds.addAll(responsibleDeptIds);
        visibleDeptModel.setResponsibleDeptIds(responsibleDeptIds);

        if (CollectionUtils.notEmpty(responsibleDeptIds)) {
            allSubordinateIds.addAll(getUserIdsByDeptIds(user, responsibleDeptIds));
        }

        //所在的主属部门
        List<String> belong2DeptIds = getBelong2DeptIds(user, Lists.newArrayList(user.getUserId()));
        deptIds.addAll(belong2DeptIds);
        visibleDeptModel.setBelong2DeptIds(belong2DeptIds);

        //直属下级
        List<QuerySubordinatesByUserId.UserInfo> directSubordinates = serviceFacade.getSubordinatesByUserId(user
                .getTenantId(), user.getUserId(), user.getUserId(), false);
        if (CollectionUtils.notEmpty(directSubordinates)) {
            List<String> subordinateIds = directSubordinates.stream()
                    .map(subordinate -> subordinate.getId())
                    .distinct()
                    .collect(Collectors.toList());
            visibleDeptModel.setDirectSubordinateIds(subordinateIds);
            allSubordinateIds.addAll(subordinateIds);

            //下级所在主属部门
            List<String> subordinateDeptIds = getBelong2DeptIds(user, subordinateIds);
            deptIds.addAll(subordinateDeptIds);
            visibleDeptModel.setSubordinateBelong2DeptIds(subordinateDeptIds);
        }

        visibleDeptModel.setAllSubordinateIds(allSubordinateIds.stream().distinct().collect(Collectors.toList()));
        List<String> distinctDeptIds = deptIds.stream().distinct().collect(Collectors.toList());

        if (needQuotationMarks) {
            List<String> markDeptIds = Lists.newArrayList();
            distinctDeptIds.forEach(id -> markDeptIds.add(addQuotationMarks(id)));
            visibleDeptModel.setVisibleDeptIds(markDeptIds.stream().distinct().collect(Collectors.toList()));
        } else {
            visibleDeptModel.setVisibleDeptIds(distinctDeptIds);
        }

        return visibleDeptModel;
    }

    public List<String> getSubordinateBelong2DeptIds(User user) {
        List<String> deptIds = Lists.newArrayList();

        List<String> subordinateIds = getSubordinateIds(user);

        //取直属下级所在部门
        if (CollectionUtils.notEmpty(subordinateIds)) {
            Map<String, QueryDeptInfoByUserIds.MainDeptInfo> subMapDeptInfos = serviceFacade.getMainDeptInfo(user
                    .getTenantId(), user.getUserId(), subordinateIds);
            if (CollectionUtils.notEmpty(subMapDeptInfos)) {
                deptIds.addAll(subMapDeptInfos.values().stream().map(dept -> dept.getDeptId()).collect(Collectors
                        .toList()));
            }
        }

        return deptIds;
    }

    public List<String> getSubordinateIds(User user) {
        List<String> subordinateIds = Lists.newArrayList();
        List<QuerySubordinatesByUserId.UserInfo> subordinates = serviceFacade.getSubordinatesByUserId(user
                .getTenantId(), user.getUserId(), user.getUserId(), false);
        if (CollectionUtils.notEmpty(subordinates)) {
            subordinateIds = subordinates.stream().map(subordinate -> subordinate.getId()).collect(Collectors.toList());
        }
        return subordinateIds;
    }

    public List<String> getBelong2DeptIds(User user, List<String> userIds) {
        List<String> belong2DeptIds = Lists.newArrayList();

        Map<String, QueryDeptInfoByUserIds.MainDeptInfo> mainDeptInfoMap = serviceFacade.getMainDeptInfo(
                user.getTenantId(), user.getUserId(), userIds);

        if (CollectionUtils.notEmpty(mainDeptInfoMap)) {
            mainDeptInfoMap.values().stream()
                    .forEach(mainDeptInfo ->
                            belong2DeptIds.add(Strings.isNullOrEmpty(mainDeptInfo.getDeptId()) ? "999999" : mainDeptInfo.getDeptId()));
        }

        /*List<QueryDeptInfoByUserId.DeptInfo> belongDepts = serviceFacade.getDeptInfoByUserId(user.getTenantId(),
                user.getUserId(),
                user.getUserId());
        if (CollectionUtils.notEmpty(belongDepts)) {
            belong2DeptIds.addAll(belongDepts.stream()
                    .map(deptInfo -> deptInfo.getDeptId())
                    .collect(Collectors.toList()));
        }*/

        return belong2DeptIds;
    }

    public List<String> getResponsibleDeptIds(User user) throws CrmCheckedException {
        List<String> responsibleDeptIds = Lists.newArrayList();

        //如果是CRM管理员，负责部门默认全公司
        if (serviceFacade.isAdmin(user)) {
            responsibleDeptIds.add("999999");
        } else {
            List<QueryResponsibleDeptsByUserIds.DeptInfo> deptInfos = serviceFacade.getResponsibleDeptsByUserIds(user
                            .getTenantId(), user.getUserId()
                    , Lists.newArrayList(user.getUserId()), 0);
            if (CollectionUtils.notEmpty(deptInfos)) {
                responsibleDeptIds.addAll(deptInfos.stream().map(deptInfo -> deptInfo.getId()).collect(Collectors
                        .toList()));
            }
        }

        if (CollectionUtils.notEmpty(responsibleDeptIds)) {
            Map<String, List<QueryDeptByName.DeptInfo>> subDeptsMap = serviceFacade.getSubDeptsByDeptIds(user
                    .getTenantId(), user.getUserId(), new ArrayList<>(responsibleDeptIds), null);
            if (CollectionUtils.notEmpty(subDeptsMap)) {
                responsibleDeptIds.clear();
                subDeptsMap.values().forEach(depts -> responsibleDeptIds.addAll(depts.stream().map(dept -> dept.getId
                        ()).collect(Collectors.toList())));
            }
        }

        return responsibleDeptIds;
    }

    public boolean isLock(User user, String goalRuleId, String goalRuleDetailId, String fiscalYear, String goalType,
                          String checkId) {
        List<IObjectData> datas = findGoalValues(user, goalRuleId, goalRuleDetailId, fiscalYear, goalType, Lists
                .newArrayList(checkId));

        if (CollectionUtils.notEmpty(datas)) {
            return datas.get(0).get("lock_status").toString().equals(ObjectLockStatus.LOCK.getStatus());
        }

        return false;
    }

    public Map<Integer, String> getFieldsMap() {
        Map<Integer, String> fieldsMap = Maps.newHashMap();
        fieldsMap.putAll(getBaseFields());
        Integer index = fieldsMap.size();
        Map<Integer, String> monthMap = goalRuleCommonService.getMonthData();
        for (Map.Entry<Integer, String> entry : monthMap.entrySet()) {
            fieldsMap.put(index, entry.getValue());
            index++;
        }

        return fieldsMap;
    }

    public Map<Integer, String> getFieldsMap(Integer startMonth, boolean containAnnualValue) {
        if (startMonth < 1 || startMonth > 12) {
            startMonth = 1;
        }
        Map<Integer, String> fieldsMap = Maps.newHashMap();
        fieldsMap.putAll(getBaseFields());
        Integer index = fieldsMap.size();
        if (containAnnualValue) {
            fieldsMap.put(index, GoalValueConstants.ANNUAL_VALUE);
            index++;
        }
        Map<Integer, String> monthMap = goalRuleCommonService.getMonthData();
        Integer size = monthMap.size();
        for (Integer i = 0; i < size; i++) {
            if (startMonth + i > 12) {
                fieldsMap.put(index, monthMap.get(startMonth + i - 12));
            } else {
                fieldsMap.put(index, monthMap.get(startMonth + i));
            }
            index++;
        }

        return fieldsMap;
    }

    public Map<Integer, String> getBaseFields() {
        Map<Integer, String> fieldsMap = Maps.newHashMap();
        fieldsMap.put(0, GoalValueConstants.GOAL_RULE_ID);
        fieldsMap.put(1, GoalValueConstants.GOAL_RULE_DETAIL_ID);
        fieldsMap.put(2, GoalValueConstants.FISCAL_YEAR);
        fieldsMap.put(3, GoalValueConstants.GOAL_TYPE);
        fieldsMap.put(4, GoalValueConstants.CHECK_OBJECT_ID);
        return fieldsMap;
    }

    private String addQuotationMarks(String str) {
        if (str == null || str.isEmpty())
            return str;
        return String.format("'%s'", str);
    }

    public String generateGoalValueName(@NotNull String ruleId,
                                        @NotNull String detailRuleId,
                                        @NotNull String fiscalYear,
                                        @NotNull String goalType,
                                        @NotNull String checkObjectId) {
        return String.join("_", Lists.newArrayList(checkObjectId, goalType, fiscalYear, ruleId, detailRuleId));
    }

    private int batchLockGoalValue(User user, String goalRuleId, String goalRuleDetailId, String fiscalYear, Boolean
            lock, int offSet) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setLimit(500);
        searchTemplateQuery.setOffset(offSet);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, GoalValueConstants.GOAL_RULE_ID, goalRuleId);
        SearchUtil.fillFilterEq(filters, GoalValueConstants.FISCAL_YEAR, fiscalYear);
        if (!Strings.isNullOrEmpty(goalRuleDetailId)) {
            SearchUtil.fillFilterEq(filters, GoalValueConstants.GOAL_RULE_DETAIL_ID, goalRuleDetailId);
        }
        searchTemplateQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, GoalObject.GOAL_VALUE.getApiName
                (), searchTemplateQuery);

        List<IObjectData> objectDataList = queryResult.getData();
        if (CollectionUtils.empty(objectDataList)) {
            return 0;
        }

        serviceFacade.bulkLockObjectData(objectDataList, lock, GoalValueConstants.DEFAULT_LOCK_RULE, user);

        return searchTemplateQuery.getOffset() + searchTemplateQuery.getLimit();
    }

    /**
     * 启用目标规则时同步更新目标值
     *
     * @param user
     * @param goalRuleId
     */
    public void syncGoalValue(User user, String goalRuleId) {
        Set<String> goalRuleApplyCircleIds = goalRuleCommonService.findGoalRuleApplyCircle(user, goalRuleId)
                .stream().map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                .collect(Collectors.toSet());
        if (CollectionUtils.empty(goalRuleApplyCircleIds)) {
            return;
        }

        IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), GoalObject.GOAL_VALUE.getApiName());
        if (describe == null) {
            throw new ValidateException("describe is null！");
        }

        Map<String, String> circleOwnerMap = getCircleOwnerMap(user, goalRuleApplyCircleIds);
        //获取部门下的直属员工
        Map<String, List<QueryMemberInfosByDeptIds.Member>> memberMap = serviceFacade.getMemberInfoMapByDeptIds(user,
                Lists.newArrayList(goalRuleApplyCircleIds), Boolean.FALSE, null, 1);

        IObjectData goalRule = goalRuleCommonService.findGoalRule(user, goalRuleId);

        //根据目标规则ID获取子目标规则ID
        List<Tuple<String, String>> goalRuleId2detailId = getGoalRuleId2detailId(user, goalRuleId);

        List<IObjectData> dataToCreate = Lists.newArrayList();
        //规则
        goalRuleId2detailId.forEach(tpl -> {
            List<String> fiscalYears = goalRule.get(GoalRuleObj.COUNT_FISCAL_YEAR, ArrayList.class);
            //财年
            fiscalYears.forEach(fiscalYear -> {
                List<IObjectData> goalValuesInDB = findGoalValues(user, tpl.getKey(), tpl.getValue(), fiscalYear,
                        GoalEnum.GoalTypeValue.CIRCLE.getValue(), Lists.newArrayList());
                //部门
                goalRuleApplyCircleIds.forEach(circleId -> {
                    if (!isExistInGoalValues(goalValuesInDB, circleId)) {
                        dataToCreate.add(buildGoalValueData(user, describe, fiscalYear, tpl.getKey(), tpl.getValue(),
                                GoalEnum.GoalTypeValue.CIRCLE, circleId, circleOwnerMap.get(circleId)));

                        //员工
                        List<QueryMemberInfosByDeptIds.Member> members = memberMap.get(circleId);
                        if (members != null) {
                            members.forEach(member -> {
                                String name = generateGoalValueName(tpl.getKey(), tpl.getValue(), fiscalYear,
                                        GoalEnum.GoalTypeValue.EMPLOYEE.getValue(), member.getId());
                                if (!dataToCreate.stream()
                                        .anyMatch(data -> data.get(GoalValueConstants.NAME, String.class).equals(name))) {
                                    dataToCreate.add(buildGoalValueData(user, describe, fiscalYear,
                                            tpl.getKey(), tpl.getValue(),
                                            GoalEnum.GoalTypeValue.EMPLOYEE, member.getId(), member.getId()));
                                }
                            });
                        }
                    }
                });

            });
        });

        if (CollectionUtils.notEmpty(dataToCreate)) {
            serviceFacade.bulkSaveObjectData(dataToCreate, user);
            log.info("syncGoalValue finish,tenantId:{},goalRuleId:{}", user.getTenantId(), goalRuleId);
        }
    }

    private List<Tuple<String, String>> getGoalRuleId2detailId(User user, String goalRuleId) {
        List<Tuple<String, String>> goalRuleId2detailId = Lists.newArrayList();
        List<IObjectData> goalRuleDetails = goalRuleCommonService.findGoalRules(user, goalRuleId);
        if (CollectionUtils.notEmpty(goalRuleDetails)) {
            goalRuleId2detailId.addAll(goalRuleDetails.stream()
                    .map(data -> Tuple.of(String.valueOf(data.get(GoalRuleObj.GOAL_RULE_ID)), data.getId()))
                    .collect(Collectors.toList()));
        } else {
            goalRuleId2detailId.add(Tuple.of(goalRuleId, ""));
        }

        return goalRuleId2detailId;
    }

    private Map<String, String> getCircleOwnerMap(User user, Set<String> circleIds) {
        List<QueryDeptInfoByDeptIds.DeptInfo> deptInfos = orgService.getDeptInfoNameByIds(user.getTenantId(), user
                        .getUserId(),
                Lists.newArrayList(circleIds));
        return deptInfos.stream().collect(Collectors.toMap(QueryDeptInfoByDeptIds.DeptInfo::getDeptId,
                x -> x.getLeaderUserId() == null ? "" : x.getLeaderUserId()));
    }

    private IObjectData buildGoalValueData(User user, IObjectDescribe describe,
                                           String fiscalYear, String goalRuleId, String goalRuleDetailId,
                                           GoalEnum.GoalTypeValue goalType, String checkObjectId,
                                           String ownerId) {
        IObjectData objectData = new ObjectData();
        objectData.set(GoalValueConstants.NAME, generateGoalValueName(goalRuleId, goalRuleDetailId, fiscalYear,
                goalType.getValue(), checkObjectId));
        objectData.set(GoalValueConstants.FISCAL_YEAR, fiscalYear);
        objectData.set(GoalValueConstants.GOAL_RULE_ID, goalRuleId);
        objectData.set(GoalValueConstants.GOAL_RULE_DETAIL_ID, goalRuleDetailId);
        objectData.set(GoalValueConstants.GOAL_TYPE, goalType.getValue());
        objectData.set(GoalValueConstants.CHECK_OBJECT_ID, checkObjectId);
        objectData.set(GoalValueConstants.OWNER, Strings.isNullOrEmpty(ownerId) ?
                Lists.newArrayList() : Lists.newArrayList(String.valueOf(ownerId)));
        objectData.setTenantId(user.getTenantId());
        objectData.setCreatedBy(user.getUserId());
        objectData.setLastModifiedBy(user.getUserId());
        objectData.set(IObjectData.DESCRIBE_ID, describe.getId());
        objectData.set(IObjectData.DESCRIBE_API_NAME, describe.getApiName());
        objectData.set(IObjectData.PACKAGE, "CRM");
        objectData.set(IObjectData.VERSION, describe.getVersion());

        return objectData;
    }

    private boolean isExistInGoalValues(List<IObjectData> goalValuesInDB, String circleId) {
        return goalValuesInDB.stream().filter(data ->
                data.get(GoalValueConstants.GOAL_TYPE, String.class)
                        .equals(GoalEnum.GoalTypeValue.CIRCLE.getValue()) &&
                        data.get(GoalValueConstants.CHECK_OBJECT_ID, String.class).equals(circleId))
                .findAny().isPresent();
    }
}
