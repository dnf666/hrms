package com.facishare.crm.goal.service;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.crm.goal.service.dto.*;
import com.facishare.crm.goal.utils.SpecialSql;
import com.facishare.paas.appframework.common.service.dto.*;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectLockStatus;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@ServiceModule("goal_value")
@Component
@Slf4j
public class GoalValueService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private ObjectDataServiceImpl objectDataService;
    @Autowired
    private GoalRuleCommonService goalRuleCommonService;
    @Autowired
    private GoalValueCommonService goalValueCommonService;

    @ServiceMethod("rule_filter")
    public GetRuleFilterModel.Result getRuleFilter(ServiceContext context)
            throws MetadataServiceException, CrmCheckedException {
        List<String> deptIds = goalValueCommonService.getVisibleDeptIds(context.getUser(), Boolean.TRUE)
                .getVisibleDeptIds();

        if(CollectionUtils.empty(deptIds)){
            return GetRuleFilterModel.Result
                    .builder()
                    .ruleFilterList(Lists.newArrayList())
                    .build();
        }

        String sql = SpecialSql.getQuerySql("RuleFilter");
        String selSql = MessageFormat.format(sql,
                new Object[]{String.format("'%s'", StringEscapeUtils.escapeSql(context.getTenantId())),
                        String.join(",", deptIds)});

        QueryResult<IObjectData> queryResult = objectDataService.findBySql(selSql,
                context.getTenantId(),
                GoalObject.GOAL_RULE.getApiName());

        List<GetRuleFilterModel.RuleFilterEntity> ruleFilterEntities = handleGoalRules(queryResult.getData());
        return GetRuleFilterModel.Result
                .builder()
                .ruleFilterList(ruleFilterEntities)
                .build();
    }

    private List<GetOrganizationModel.OrganizationEntity> getSubordinateGoalValues(User user,
                                                                                   GetOrganizationModel.Arg arg,
                                                                                   List<String> applyCircleIds,
                                                                                   List<String> existUserIds) {
        List<GetOrganizationModel.OrganizationEntity> organizationEntities = Lists.newArrayList();

        if(!existUserIds.contains(user.getUserId())) {
            GetOrganizationModel.OrganizationEntity organizationEntity = handlePersonalValue(user,
                    arg,
                    applyCircleIds,
                    Boolean.TRUE,
                    null);
            if (organizationEntity != null) {
                organizationEntities.add(organizationEntity);
            }
        }
        List<QuerySubordinatesByUserId.UserInfo> subordinates = serviceFacade.getSubordinatesByUserId(user.getTenantId(),
                user.getUserId(),
                user.getUserId(),
                false);
        if (CollectionUtils.notEmpty(subordinates)) {
            for (QuerySubordinatesByUserId.UserInfo subordinate : subordinates) {
                if(!existUserIds.contains(subordinate.getId())) {
                    User subUser = new User(user.getTenantId(), subordinate.getId());
                    subUser.setUserName(subordinate.getNickname());
                    GetOrganizationModel.OrganizationEntity orgEntity = handlePersonalValue(subUser,
                            arg,
                            applyCircleIds,
                            Boolean.FALSE,
                            user.getUserId());
                    if (orgEntity != null) {
                        organizationEntities.add(orgEntity);
                    }
                }
            }
        }
        return organizationEntities;
    }

    private List<GetOrganizationModel.OrganizationEntity> getResponsibleDeptGoalValues(User user,
                                                                                       GetOrganizationModel.Arg arg,
                                                                                       List<String> applyCircleIds,
                                                                                       List<String> responsibleDeptIds) {
        List<GetOrganizationModel.OrganizationEntity> organizationEntities = Lists.newArrayList();

        List<QueryDeptByName.DeptInfo> allSubResponsibleDeptsWithSelf = Lists.newArrayList();
        Map<String, List<QueryDeptByName.DeptInfo>> subDeptsMap = serviceFacade.getSubDeptsByDeptIds(user.getTenantId(),
                user.getUserId(),
                Lists.newArrayList(responsibleDeptIds),
                null);
        subDeptsMap.values().forEach(depts -> allSubResponsibleDeptsWithSelf.addAll(depts));
        List<QueryDeptByName.DeptInfo> distinctSubResponsibleDeptsWithSelf = allSubResponsibleDeptsWithSelf.stream()
                .distinct()
                .collect(Collectors.toList());
        List<String> allSubResponsibleDeptIds = distinctSubResponsibleDeptsWithSelf.stream()
                .map(deptInfo -> deptInfo.getId())
                .distinct()
                .collect(Collectors.toList());
        allSubResponsibleDeptIds.retainAll(applyCircleIds);
        if (CollectionUtils.notEmpty(allSubResponsibleDeptIds)) {
            List<GetOrganizationModel.OrganizationEntity> entities = handleOrgTree(user,
                    distinctSubResponsibleDeptsWithSelf,
                    allSubResponsibleDeptIds,
                    arg);
            if (CollectionUtils.notEmpty(entities)) {
                organizationEntities.addAll(entities);
            }
        }

        return organizationEntities;
    }

    private void setCurrentUserName(User user) {
        List<QueryUserInfoByIds.UserInfo> userInfos = serviceFacade.getUserNameByIds(user.getTenantId(),
                user.getUserId(),
                Lists.newArrayList(user.getUserId()));
        if (CollectionUtils.notEmpty(userInfos)) {
            user.setUserName(userInfos.get(0).getName());
        }
    }

    private List<GetOrganizationModel.OrganizationEntity> getVisibleGoalValues(User user,
                                                                               GetOrganizationModel.Arg arg,
                                                                               List<String> applyCircleIds,
                                                                               List<String> responsibleDeptIds)
            throws CrmCheckedException {
        List<GetOrganizationModel.OrganizationEntity> organizationEntities = Lists.newArrayList();
        List<String> existUserIds = Lists.newArrayList();

        if(CollectionUtils.notEmpty(responsibleDeptIds)){
            List<GetOrganizationModel.OrganizationEntity> deptEntities = getResponsibleDeptGoalValues(user,
                    arg,
                    applyCircleIds,
                    responsibleDeptIds);
            if (CollectionUtils.notEmpty(deptEntities)) {
                organizationEntities.addAll(deptEntities);
                for(GetOrganizationModel.OrganizationEntity organizationEntity : deptEntities){
                    if(organizationEntity.getGoalType().equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())){
                        existUserIds.add(organizationEntity.getCheckObjectId());
                    }
                }
            }
        }

        setCurrentUserName(user);
        List<GetOrganizationModel.OrganizationEntity> subordinateEntities = getSubordinateGoalValues(user,
                arg,
                applyCircleIds,
                existUserIds);
        if (CollectionUtils.notEmpty(subordinateEntities)) {
            organizationEntities.addAll(subordinateEntities);
        }
        return organizationEntities.stream().distinct().collect(Collectors.toList());
    }

    private GetOrganizationModel.OrganizationEntity getSpecifiedPersonalGoalValue(User user,
                                                                                  GetOrganizationModel.Arg arg,
                                                                                  List<String> applyCircleIds) {
        List<QueryUserInfoByIds.UserInfo> userInofs = serviceFacade.getUserNameByIds(user.getTenantId(),
                user.getUserId(),
                Lists.newArrayList(arg.getCheckObjectId()));
        User newUser = new User(user.getTenantId(), arg.getCheckObjectId());
        if (CollectionUtils.notEmpty(userInofs)) {
            newUser.setUserName(userInofs.get(0).getName());
        }
        return handlePersonalValue(user, arg, applyCircleIds, Boolean.TRUE, null);
    }

    @ServiceMethod("organization_tree")
    public GetOrganizationModel.Result getOrganizationTree(ServiceContext context, GetOrganizationModel.Arg arg)
            throws CrmCheckedException {
        List<GetOrganizationModel.OrganizationEntity> organizationEntities = Lists.newArrayList();

        IObjectData ruleData = validateRule(context.getUser(),
                arg.getGoalRuleId(),
                arg.getFiscalYear(),
                arg.getContainGoalValue());

        List<String> checkCircleIds = getApplyCircleIds(context.getUser(), arg.getGoalRuleId());
        validateDetailRule(context.getUser(), arg.getGoalRuleId(), arg.getGoalRuleDetailId());
        VisibleDeptModel visibleDeptModel = goalValueCommonService.getVisibleDeptIds(context.getUser(), Boolean.FALSE);
        validateInApplyCircle(checkCircleIds, visibleDeptModel.getVisibleDeptIds());

        if (StringUtils.isNullOrEmpty(arg.getGoalType()) || StringUtils.isNullOrEmpty(arg.getCheckObjectId())) {
            List<GetOrganizationModel.OrganizationEntity> visibleGoalValues = getVisibleGoalValues(context.getUser(),
                    arg,
                    checkCircleIds,
                    visibleDeptModel.getResponsibleDeptIds());
            if (CollectionUtils.notEmpty(visibleGoalValues)) {
                organizationEntities.addAll(visibleGoalValues);
            }
        } else {
            validateInResponsible(visibleDeptModel.getResponsibleDeptIds(),
                    visibleDeptModel.getAllSubordinateIds(),
                    arg.getGoalType(),
                    arg.getCheckObjectId());
            if (arg.getGoalType().equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())) {
                GetOrganizationModel.OrganizationEntity organizationEntity = getSpecifiedPersonalGoalValue(context.getUser(),
                        arg,
                        checkCircleIds);
                if (organizationEntity != null) {
                    organizationEntities.add(organizationEntity);
                }
            } else if (arg.getGoalType().equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())) {
                List<GetOrganizationModel.OrganizationEntity> deptGoalValues = getResponsibleDeptGoalValues(context.getUser(),
                        arg,
                        checkCircleIds,
                        Lists.newArrayList(arg.getCheckObjectId()));
                if (CollectionUtils.notEmpty(deptGoalValues)) {
                    organizationEntities.addAll(deptGoalValues);
                }
            }
        }

        Boolean lock = Boolean.FALSE;
        Boolean unlock = Boolean.FALSE;
        if (serviceFacade.isAdmin(context.getUser())) {
            boolean isLock = goalValueCommonService.isLock(context.getUser(),
                    arg.getGoalRuleId(),
                    arg.getGoalRuleDetailId(),
                    arg.getFiscalYear(),
                    GoalEnum.GoalTypeValue.CIRCLE.getValue(),
                    checkCircleIds.get(0));
            lock = !isLock;
            unlock = isLock;
        }
        Boolean allowPersonalModify = String.valueOf(ruleData.get(GoalRuleObj.ALLOW_PERSONAL_MODIFY_GOAL)).equals("1");
        return GetOrganizationModel.Result
                .builder()
                .result(organizationEntities)
                .lockable(lock)
                .unlockable(unlock)
                .allowPersonalModify(allowPersonalModify)
                .build();
    }

    private IObjectData validateRule(User user, String ruleId, String fiscalYear, boolean validateFiscalYear) {
        IObjectData ruleData = goalRuleCommonService.findGoalRule(user, ruleId);
        if (ruleData.isDeleted()) {
            throw new ValidateException("当前目标规则已删除！");
        }
        if (!String.valueOf(ruleData.get(GoalRuleObj.STATUS)).equals("1")) {
            throw new ValidateException("当前目标规则未启用！");
        }
        if (validateFiscalYear) {
            if (!ruleData.get(GoalRuleObj.COUNT_FISCAL_YEAR).toString().contains(fiscalYear)) {
                throw new ValidateException("财年超出当前目标规则统计财年范围！");
            }
        }

        return ruleData;
    }

    private void validateDetailRule(User user, String ruleId, String detailId) {
        List<IObjectData> detailRules = goalRuleCommonService.findGoalRules(user, ruleId);
        if (CollectionUtils.empty(detailRules)) {
            if (!Strings.isNullOrEmpty(detailId)) {
                throw new ValidateException("子目标不属于当前目标规则！");
            }
        } else {
            if (Strings.isNullOrEmpty(detailId)) {
                throw new ValidateException("子目标不能为空！");
            }
            Optional<IObjectData> dataOptional = detailRules.stream()
                    .filter(detailRule -> detailRule.getId().equals(detailId))
                    .findFirst();
            if (!dataOptional.isPresent()) {
                throw new ValidateException("子目标不属于当前目标规则！");
            }
        }
    }

    private List<String> getApplyCircleIds(User user, String ruleId) {
        List<String> applyCircleIds = Lists.newArrayList();
        List<IObjectData> circleDatas = goalRuleCommonService.findGoalRuleApplyCircle(user, ruleId);
        if (CollectionUtils.empty(circleDatas)) {
            throw new ValidateException("目标无适用考核部门！");
        }
        applyCircleIds = circleDatas.stream()
                .map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                .collect(Collectors.toList());
        return applyCircleIds;
    }

    private void validateInApplyCircle(List<String> applyCircleIds, List<String> visibleDeptIds) {
        if (CollectionUtils.empty(visibleDeptIds)) {
            throw new ValidateException("不在目标适用考核部门！");
        }
        visibleDeptIds.retainAll(applyCircleIds);

        if (CollectionUtils.empty(visibleDeptIds)) {
            throw new ValidateException("不在目标适用考核部门！");
        }
    }

    private void validateInResponsible(List<String> responsibleDeptIds, List<String> subordianteIds, String goalType,
                                       String checkObjectId) {
        if (goalType.equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())) {
            if (CollectionUtils.empty(responsibleDeptIds) || !responsibleDeptIds.contains(checkObjectId)) {
                throw new ValidateException("部门超出负责范围！");
            }
        }

        if (goalType.equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())) {
            if (CollectionUtils.empty(subordianteIds) || !subordianteIds.contains(checkObjectId)) {
                throw new ValidateException("人员超出负责范围！");
            }
        }
    }

    private void validateVisibleScope(User user, String ruleId, String goalType, String checkObjectId) {
        List<String> applyCircleIds = getApplyCircleIds(user, ruleId);
        VisibleDeptModel visibleDeptModel = new VisibleDeptModel();
        try {
            visibleDeptModel = goalValueCommonService.getVisibleDeptIds(user, Boolean.FALSE);
        } catch (CrmCheckedException e) {
            e.printStackTrace();
        }
        validateInApplyCircle(applyCircleIds, visibleDeptModel.getVisibleDeptIds());
        validateInResponsible(visibleDeptModel.getResponsibleDeptIds(),
                visibleDeptModel.getAllSubordinateIds(),
                goalType,
                checkObjectId);
    }

    @ServiceMethod("detail")
    public GetDetailModel.Result getDetail(ServiceContext context, GetDetailModel.Arg arg) throws CrmCheckedException {
        IObjectData ruleData = validateRule(context.getUser(), arg.getGoalRuleId(), arg.getFiscalYear(), true);
        validateDetailRule(context.getUser(), arg.getGoalRuleId(), arg.getGoalRuleDetailId());
        validateVisibleScope(context.getUser(), arg.getGoalRuleId(), arg.getGoalType(), arg.getCheckObjectId());
        List<String> userIds = Lists.newArrayList(arg.getCheckObjectId());
        IObjectData data = new ObjectData();
        Integer startMonth = 1;
        String firstMonth = "";
        boolean editable = false;
        List<IObjectData> objectDataList = goalValueCommonService.findGoalValues(context.getUser(),
                arg.getGoalRuleId(),
                arg.getGoalRuleDetailId(),
                arg.getFiscalYear(),
                arg.getGoalType(),
                userIds);
        if (CollectionUtils.notEmpty(objectDataList)) {
            IObjectData objectData = objectDataList.get(0);
            Object oMonth = ruleData.get(GoalRuleObj.START_MONTH);
            if (oMonth != null) {
                startMonth = Integer.valueOf(oMonth.toString());
            }
            Map<Integer, String> monthMapping = goalRuleCommonService.getMonthData();

            data.setId(objectData.getId());
            Object oAnnualValue = objectData.get(GoalValueConstants.ANNUAL_VALUE);
            if (oAnnualValue != null) {
                data.set(GoalValueConstants.ANNUAL_VALUE, oAnnualValue.toString());
            } else {
                data.set(GoalValueConstants.ANNUAL_VALUE, "");
            }

            //处理月度目标值顺序
            for (int i = 0; i < 12; i++) {
                Integer month = startMonth + i;
                if (month > 12) {
                    month -= 12;
                }
                Object oMonthValue = objectData.get(monthMapping.get(month));
                if (oMonthValue != null) {
                    data.set(monthMapping.get(month), oMonthValue.toString());
                } else {
                    data.set(monthMapping.get(month), "");
                }
            }

            firstMonth = monthMapping.get(startMonth);
            String lockStatus = objectData.get("lock_status").toString();
            if (lockStatus.equals(ObjectLockStatus.LOCK.getStatus())) {
                editable = serviceFacade.isAdmin(context.getUser());
            } else {
                editable = true;
            }
        }

        return GetDetailModel.Result
                .builder()
                .data(ObjectDataDocument.of(data))
                .startMonth(firstMonth)
                .editable(editable)
                .build();
    }

    @ServiceMethod("lock")
    public LockGoalRuleModel.Result lockGoalRule(ServiceContext context, LockGoalRuleModel.Arg arg) throws CrmCheckedException {
        if (!serviceFacade.isAdmin(context.getUser())) {
            throw new ValidateException("无权操作！");
        }
        IObjectData ruleData = validateRule(context.getUser(), arg.getGoalRuleId(), arg.getFiscalYear(), true);
        List<String> applyCircleIds = getApplyCircleIds(context.getUser(), arg.getGoalRuleId());
        validateDetailRule(context.getUser(), arg.getGoalRuleId(), arg.getGoalRuleDetailId());

        boolean isLock = goalValueCommonService.isLock(context.getUser(),
                arg.getGoalRuleId(),
                arg.getGoalRuleDetailId(),
                arg.getFiscalYear(),
                GoalEnum.GoalTypeValue.CIRCLE.getValue(),
                applyCircleIds.get(0));

        if (isLock == arg.getLock()) {
            if (arg.getLock()) {
                throw new ValidateException("目标值已被锁定！");
            } else {
                throw new ValidateException("目标值已被解锁！");
            }
        }

        Boolean lockable = Boolean.FALSE;
        Boolean unlockable = Boolean.FALSE;
        goalValueCommonService.lockGoalValue(context.getUser(),
                arg.getGoalRuleId(),
                arg.getGoalRuleDetailId(),
                arg.getFiscalYear(),
                arg.getLock());
        if (arg.getLock()) {
            unlockable = Boolean.TRUE;
        } else {
            lockable = Boolean.TRUE;
        }

        return LockGoalRuleModel.Result.builder()
                .success(Boolean.TRUE)
                .lockable(lockable)
                .unlockable(unlockable)
                .build();
    }

    private GetOrganizationModel.OrganizationEntity handlePersonalValue(User user,
                                                                        GetOrganizationModel.Arg arg,
                                                                        List<String> checkCircleIds,
                                                                        Boolean parentLeaf,
                                                                        String parentId) {
        GetOrganizationModel.OrganizationEntity organizationEntity = null;
        List<String> mainDeptIds = goalValueCommonService.getBelong2DeptIds(user, Lists.newArrayList(user.getUserId()));
        if (CollectionUtils.notEmpty(mainDeptIds)) {
            mainDeptIds.retainAll(checkCircleIds);
            if (CollectionUtils.notEmpty(mainDeptIds)) {
                List<IObjectData> myValueDatas = Lists.newArrayList();
                if(arg.getContainGoalValue()){
                    myValueDatas = goalValueCommonService.findGoalValues(user,
                            arg.getGoalRuleId(),
                            arg.getGoalRuleDetailId(),
                            arg.getFiscalYear(),
                            GoalEnum.GoalTypeValue.EMPLOYEE.getValue(),
                            Lists.newArrayList(user.getUserId()));
                }

                organizationEntity = new GetOrganizationModel.OrganizationEntity();
                IObjectData myData = myValueDatas.size() > 0 ? myValueDatas.get(0) : null;
                organizationEntity.setId(myData == null ? "" : myData.getId());
                organizationEntity.setCheckObjectId(user.getUserId());
                organizationEntity.setGoalType(GoalEnum.GoalTypeValue.EMPLOYEE.getValue());
                if (arg.getContainGoalValue()) {
                    organizationEntity.setGoalValue(myData == null ? "" : getMonthlyValue(myData, arg.getMonth()));
                    if (!Strings.isNullOrEmpty(parentId)) {
                        organizationEntity.setParentId(parentId);
                    }
                } else {
                    organizationEntity.setParentId(mainDeptIds.get(0));
                }
                organizationEntity.setCheckObjectName(user.getUserName());
                organizationEntity.setParentLeaf(parentLeaf);
            }
        }

        return organizationEntity;
    }

    private List<GetOrganizationModel.OrganizationEntity> handleOrgTree(User user,
                                                                        List<QueryDeptByName.DeptInfo> allDeptInfos,
                                                                        List<String> checkDeptIds,
                                                                        GetOrganizationModel.Arg arg) {
        List<QueryDeptByName.DeptInfo> checkDeptInofs = allDeptInfos.stream()
                .filter(deptInfo -> checkDeptIds.contains(deptInfo.getId()))
                .collect(Collectors.toList());
        List<String> highestDeptIds = getHighestDeptIds(checkDeptInofs);
        Map<String, List<QueryMemberInfosByDeptIds.Member>> memberMap = serviceFacade.getMemberInfoMapByDeptIds(user,
                checkDeptIds,
                Boolean.FALSE,
                null,
                1);
        List<String> deptMemberIds = Lists.newArrayList();
        if (CollectionUtils.notEmpty(memberMap)) {
            memberMap.values().forEach(members -> members.forEach(member -> deptMemberIds.add(member.getId())));
        }
        List<String> memberIds = deptMemberIds.stream().distinct().collect(Collectors.toList());
        List<IObjectData> allDeptGoalValues = Lists.newArrayList();
        List<IObjectData> allMemberGoalValues = Lists.newArrayList();

        if (arg.getContainGoalValue()) {
            allDeptGoalValues = goalValueCommonService.findGoalValues(user,
                    arg.getGoalRuleId(),
                    arg.getGoalRuleDetailId(),
                    arg.getFiscalYear(),
                    GoalEnum.GoalTypeValue.CIRCLE.getValue(),
                    checkDeptIds);
            allMemberGoalValues = goalValueCommonService.findGoalValues(user,
                    arg.getGoalRuleId(),
                    arg.getGoalRuleDetailId(),
                    arg.getFiscalYear(),
                    GoalEnum.GoalTypeValue.EMPLOYEE.getValue(),
                    memberIds);
        }

        List<GetOrganizationModel.OrganizationEntity> organizationEntities = Lists.newArrayList();
        for (String deptId : highestDeptIds) {
            List<GetOrganizationModel.OrganizationEntity> entities = handleDeptValue(allDeptInfos,
                    deptId,
                    memberMap,
                    allDeptGoalValues,
                    allMemberGoalValues,
                    arg.getContainGoalValue(),
                    arg.getMonth(),
                    Boolean.TRUE);
            if (CollectionUtils.notEmpty(entities)) {
                organizationEntities.addAll(entities);
            }
        }

        return organizationEntities;
    }

    private List<GetOrganizationModel.OrganizationEntity> handleDeptValue(List<QueryDeptByName.DeptInfo> allDeptInfos,
                                                                          String deptId,
                                                                          Map<String, List<QueryMemberInfosByDeptIds.Member>> deptMemberMap,
                                                                          List<IObjectData> deptValues,
                                                                          List<IObjectData> memberValues,
                                                                          Boolean containValue,
                                                                          String month,
                                                                          Boolean parentLeaf) {
        List<GetOrganizationModel.OrganizationEntity> organizationEntities = Lists.newArrayList();

        List<IObjectData> deptGoalValues = deptValues.stream()
                .filter(deptValue -> deptValue.get(GoalValueConstants.CHECK_OBJECT_ID).toString().equals(deptId))
                .collect(Collectors.toList());
        GetOrganizationModel.OrganizationEntity organizationEntity = new GetOrganizationModel.OrganizationEntity();
        IObjectData deptValue = deptGoalValues.size() > 0 ? deptGoalValues.get(0) : null;
        organizationEntity.setId(deptValue == null ? "" : deptValue.getId());
        organizationEntity.setCheckObjectId(deptId);
        organizationEntity.setGoalType(GoalEnum.GoalTypeValue.CIRCLE.getValue());
        if (containValue) {
            organizationEntity.setGoalValue(deptValue == null ? "" : getMonthlyValue(deptValue, month));
        }
        List<QueryDeptByName.DeptInfo> deptInfos = allDeptInfos.stream()
                .filter(deptInfo -> deptInfo.getId().equals(deptId))
                .collect(Collectors.toList());
        if (CollectionUtils.notEmpty(deptInfos)) {
            organizationEntity.setParentId(String.valueOf(deptInfos.get(0).getParentId()));
            organizationEntity.setCheckObjectName(deptInfos.get(0).getName());
        } else {
            organizationEntity.setParentId("");
        }
        organizationEntity.setParentLeaf(parentLeaf);

        organizationEntities.add(organizationEntity);

        List<QueryMemberInfosByDeptIds.Member> members = deptMemberMap.get(deptId);
        if (CollectionUtils.notEmpty(members)) {
            for (QueryMemberInfosByDeptIds.Member member : members) {
                List<IObjectData> memberGoalVlues = memberValues.stream()
                        .filter(memberValue ->
                                memberValue.get(GoalValueConstants.CHECK_OBJECT_ID).toString().equals(member.getId()))
                        .collect(Collectors.toList());
                GetOrganizationModel.OrganizationEntity entity = new GetOrganizationModel.OrganizationEntity();
                IObjectData memberValue = memberGoalVlues.size() > 0 ? memberGoalVlues.get(0) : null;
                entity.setId(memberValue == null ? "" : memberValue.getId());
                entity.setCheckObjectId(member.getId());
                entity.setGoalType(GoalEnum.GoalTypeValue.EMPLOYEE.getValue());
                if (containValue) {
                    entity.setGoalValue(memberValue == null ? "" : getMonthlyValue(memberValue, month));
                }
                entity.setParentId(deptId);
                entity.setCheckObjectName(member.getNickname());
                entity.setParentLeaf(Boolean.FALSE);
                organizationEntities.add(entity);
            }
        }

        List<QueryDeptByName.DeptInfo> subDepts = allDeptInfos.stream()
                .filter(deptInfo ->
                        deptInfo.getAncestors().size() > 0
                                && deptInfo.getAncestors().indexOf(deptId) == deptInfo.getAncestors().size() - 1)
                .collect(Collectors.toList());
        for (QueryDeptByName.DeptInfo subDeptInfo : subDepts) {
            organizationEntities.addAll(handleDeptValue(allDeptInfos,
                    subDeptInfo.getId(),
                    deptMemberMap,
                    deptValues,
                    memberValues,
                    containValue,
                    month,
                    Boolean.FALSE));
        }

        return organizationEntities;
    }

    private String getMonthlyValue(IObjectData objectData, String month) {
        if (StringUtils.isNullOrEmpty(month)) {
            Object oValue = objectData.get(GoalValueConstants.ANNUAL_VALUE);
            return oValue == null ? "" : oValue.toString();
        }

        Map<Integer, String> monthMapping = goalRuleCommonService.getMonthData();

        Object monthValue = objectData.get(monthMapping.get(Integer.valueOf(month)));
        return monthValue == null ? "" : monthValue.toString();
    }

    private List<String> getHighestDeptIds(List<QueryDeptByName.DeptInfo> deptInfos) {
        if (CollectionUtils.empty(deptInfos)) {
            return Lists.newArrayList();
        }

        List<String> highestDeptIds = Lists.newArrayList();
        if (deptInfos.stream().anyMatch(deptInfo -> deptInfo.getId().equals("999999"))) {
            highestDeptIds.add("999999");
            return highestDeptIds;
        }

        List<String> allDeptIds = deptInfos.stream().map(dept -> dept.getId()).collect(Collectors.toList());
        for (QueryDeptByName.DeptInfo deptInfo : deptInfos) {
            if (deptInfo.getAncestors().stream().anyMatch(d -> allDeptIds.contains(d))) {
                allDeptIds.remove(deptInfo.getId());
            }
        }

        return allDeptIds;
    }

    private List<GetRuleFilterModel.RuleFilterEntity> handleGoalRules(List<IObjectData> objectDataList) {
        List<GetRuleFilterModel.RuleFilterEntity> ruleFilterEntities = Lists.newArrayList();

        if (CollectionUtils.notEmpty(objectDataList)) {
            List<String> ruleIds = objectDataList.stream()
                    .map(rule -> rule.get(GoalValueConstants.GOAL_RULE_ID).toString())
                    .distinct()
                    .collect(Collectors.toList());

            for (String id : ruleIds) {
                GetRuleFilterModel.RuleFilterEntity entity = new GetRuleFilterModel.RuleFilterEntity();
                entity.setValue(id);
                List<GetRuleFilterModel.RuleFilterEntity> children = Lists.newArrayList();
                List<IObjectData> details = objectDataList.stream()
                        .filter(rule -> rule.get(GoalValueConstants.GOAL_RULE_ID).toString().equals(id))
                        .collect(Collectors.toList());
                for (int i = 0; i < details.size(); i++) {
                    if (i == 0) {
                        entity.setLabel(details.get(i).get("goal_rule_name").toString());
                        entity.setFiscalYear(details.get(i).get(GoalValueConstants.FISCAL_YEAR).toString());
                    }

                    Object oDetailId = details.get(i).get(GoalValueConstants.GOAL_RULE_DETAIL_ID);
                    if (oDetailId != null && !Strings.isNullOrEmpty(oDetailId.toString())) {
                        GetRuleFilterModel.RuleFilterEntity child = new GetRuleFilterModel.RuleFilterEntity();
                        child.setValue(oDetailId.toString());
                        child.setLabel(details.get(i).get("goal_rule_detail_name").toString());
                        children.add(child);
                    }
                }
                entity.setChildren(children);
                ruleFilterEntities.add(entity);
            }
        }

        return ruleFilterEntities;
    }
}
