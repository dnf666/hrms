package com.facishare.crm.goal.action;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleDetailObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.crm.goal.service.GoalRuleCommonService;
import com.facishare.crm.goal.service.GoalValueCommonService;
import com.facishare.crm.goal.service.dto.VisibleDeptModel;
import com.facishare.paas.appframework.common.service.CRMNotificationServiceImpl;
import com.facishare.paas.appframework.common.service.dto.QueryDeptInfoByDeptIds;
import com.facishare.paas.appframework.common.service.dto.QueryUserInfoByIds;
import com.facishare.paas.appframework.common.service.model.CRMNotification;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardBulkSaveAction;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by renlb on 2018/4/13.
 */
@Slf4j
public class GoalValueBulkCreateAction extends StandardBulkSaveAction {

    private CRMNotificationServiceImpl crmNotificationService = (CRMNotificationServiceImpl) SpringUtil.getContext()
            .getBean("crmNotificationService");
    private GoalRuleCommonService goalRuleCommonService = (GoalRuleCommonService) SpringUtil.getContext()
            .getBean("goalRuleCommonService");
    private GoalValueCommonService goalValueCommonService = (GoalValueCommonService) SpringUtil.getContext()
            .getBean("goalValueCommonService");
    private List<IObjectData> toAddList = Lists.newArrayList();
    private List<IObjectData> toUpdateList = Lists.newArrayList();
    private List<String> deptIds = Lists.newArrayList();
    private List<String> userIds = Lists.newArrayList();
    private String ruleId = "";
    private String detailId = "";
    private IObjectData ruleData = null;
    private List<String> applyCircleIds = Lists.newArrayList();
    private String fiscalYear = null;
    private String detailRuleName = "无";
    private List<QueryDeptInfoByDeptIds.DeptInfo> deptInfos = Lists.newArrayList();

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        if (CollectionUtils.empty(objectDatas)) {
            return;
        }

        //校验数据
        validateData();
        //填充name等字段
        fillSpecialFields();
    }

    private void validateData() {
        validateRule();
        validateApplyCircle();
        validateDetailRule();
        validateFiscalYear();

        try {
            validateLockStatus();
            validateDeptAndEmployee();
        } catch (CrmCheckedException e) {
            //e.printStackTrace();
            log.error("GoalValueBulkCreate->validateData error", e);
        }
    }

    private void validateRule() {
        List<Object> ruleIds = objectDatas.stream()
                .map(data -> data.get(GoalRuleObj.GOAL_RULE_ID))
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.empty(ruleIds)) {
            throw new ValidateException("目标参数不能为空！");
        }
        if (ruleIds.size() > 1) {
            throw new ValidateException("目标参数必须唯一！");
        }
        Object oRuleId = ruleIds.get(0);
        if (oRuleId == null) {
            throw new ValidateException("目标参数不能为空！");
        }
        ruleId = oRuleId.toString();
        ruleData = goalRuleCommonService.findGoalRule(actionContext.getUser(), ruleId);
        if (ruleData.isDeleted()) {
            throw new ValidateException("当前目标规则已删除，请联系CRM管理员重新配置目标规则！");
        }
        if (!String.valueOf(ruleData.get(GoalRuleObj.STATUS)).equals("1")) {
            throw new ValidateException("当前目标规则未启用，请联系CRM管理员开启后再编辑目标值！");
        }
    }

    private void validateApplyCircle() {
        List<IObjectData> circleDatas = goalRuleCommonService.findGoalRuleApplyCircle(actionContext.getUser(), ruleId);

        if (CollectionUtils.empty(circleDatas)) {
            throw new ValidateException("目标无适用考核部门，无法设置目标值！");
        }

        applyCircleIds = circleDatas.stream()
                .map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                .collect(Collectors.toList());
    }

    private void validateDetailRule() {
        List<Object> detailIds = objectDatas.stream()
                .map(data -> data.get(GoalRuleObj.GOAL_RULE_DETAIL_ID))
                .distinct().collect(Collectors.toList());
        detailIds.remove(null);
        List<IObjectData> detailRules = goalRuleCommonService.findGoalRules(actionContext.getUser(), ruleId);

        if (CollectionUtils.empty(detailIds)) {
            if (CollectionUtils.notEmpty(detailRules)) {
                throw new ValidateException("子目标参数不能为空！");
            }
        } else {
            if (detailIds.size() > 1) {
                throw new ValidateException("子目标参数必须唯一！");
            }

            detailId = detailIds.get(0).toString();
            if (CollectionUtils.empty(detailRules)) {
                throw new ValidateException("子目标不属于当前目标规则！");
            } else {
                Optional<IObjectData> dataOptional = detailRules.stream()
                        .filter(detailRule -> detailRule.getId().equals(detailId))
                        .findFirst();
                if (!dataOptional.isPresent()) {
                    throw new ValidateException("子目标不属于当前目标规则！");
                }
                detailRuleName = dataOptional.get().get(GoalRuleDetailObj.SUBGOAL_NAME).toString();
            }
        }
    }

    private void validateFiscalYear() {
        List<Object> fiscalYears = objectDatas.stream()
                .map(data -> data.get(GoalValueConstants.FISCAL_YEAR))
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.empty(fiscalYears)) {
            throw new ValidateException("财年参数不能为空！");
        } else {
            if (fiscalYears.size() > 1) {
                throw new ValidateException("财年参数必须唯一！");
            }

            fiscalYear = fiscalYears.get(0).toString();

            if (!ruleData.get(GoalRuleObj.COUNT_FISCAL_YEAR).toString().contains(fiscalYear)) {
                throw new ValidateException("财年参数超出当前目标规则统计财年范围！");
            }
        }
    }

    private void validateLockStatus() throws CrmCheckedException {
        boolean isLock = goalValueCommonService.isLock(actionContext.getUser()
                , ruleId
                , detailId
                , fiscalYear
                , GoalEnum.GoalTypeValue.CIRCLE.getValue()
                , applyCircleIds.get(0));
        if (isLock && !serviceFacade.isAdmin(actionContext.getUser())) {
            throw new ValidateException("目标值已被锁定，无法编辑！");
        }
    }

    private void validateDeptAndEmployee() throws CrmCheckedException {
        VisibleDeptModel visibleDeptModel = goalValueCommonService.getVisibleDeptIds(actionContext.getUser()
                , Boolean.FALSE);
        validateDeptAndEmployeeInApplyCircle(applyCircleIds, visibleDeptModel.getVisibleDeptIds());
        validateDeptAndEmployeeIsResponsible(visibleDeptModel.getResponsibleDeptIds()
                , visibleDeptModel.getAllSubordinateIds());
    }

    private void validateDeptAndEmployeeInApplyCircle(List<String> applyCircleIds, List<String> visibleDeptIds)
            throws CrmCheckedException {
        visibleDeptIds.retainAll(applyCircleIds);

        if (CollectionUtils.empty(visibleDeptIds)) {
            throw new ValidateException("不在目标适用考核部门，无法设置目标值！");
        }

        validateDeptInApplyCircles(applyCircleIds);
        validateEmployeeInApplyCircle(applyCircleIds);
    }

    private void fillSpecialFields() {
        List<IObjectData> personalDatas = objectDatas.stream()
                .filter(x -> String.valueOf(x.get(GoalValueConstants.GOAL_TYPE))
                        .equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue()))
                .collect(Collectors.toList());
        fillPersonalFields(personalDatas, objectDescribe.getId());

        List<IObjectData> deptDatas = objectDatas.stream()
                .filter(x -> String.valueOf(x.get(GoalValueConstants.GOAL_TYPE))
                        .equals(GoalEnum.GoalTypeValue.CIRCLE.getValue()))
                .collect(Collectors.toList());
        fillDeptFields(deptDatas, objectDescribe.getId());
    }

    private void fillDeptFields(List<IObjectData> deptDatas, String describeId) {
        if (CollectionUtils.notEmpty(deptDatas)) {
            deptInfos = serviceFacade.getDeptInfoNameByIds(
                    actionContext.getTenantId(),
                    actionContext.getUser().getUserId(),
                    deptIds);
            for (IObjectData objectData : deptDatas) {
                Optional<QueryDeptInfoByDeptIds.DeptInfo> deptInfoOptional = deptInfos.stream()
                        .filter(deptInfo -> deptInfo.getDeptId()
                                .equals(objectData.get(GoalValueConstants.CHECK_OBJECT_ID).toString()))
                        .findFirst();
                if (deptInfoOptional.isPresent()) {
                    if (!Strings.isNullOrEmpty(deptInfoOptional.get().getLeaderUserId())) {
                        objectData.set("owner", Lists.newArrayList(deptInfoOptional.get().getLeaderUserId()));
                    } else {
                        objectData.set("owner", Lists.newArrayList());
                    }
                } else {
                    objectData.set("owner", Lists.newArrayList());
                }
                objectData.setTenantId(actionContext.getTenantId());
                objectData.setDescribeId(describeId);
                objectData.setName(
                        goalValueCommonService.generateGoalValueName(ruleId,
                                detailId,
                                fiscalYear,
                                GoalEnum.GoalTypeValue.CIRCLE.getValue(),
                                objectData.get(GoalValueConstants.CHECK_OBJECT_ID).toString()));
            }
        }
    }

    private void fillPersonalFields(List<IObjectData> personalDatas, String describeId) {
        if (CollectionUtils.notEmpty(personalDatas)) {
            for (IObjectData objectData : personalDatas) {
                objectData.setTenantId(actionContext.getTenantId());
                objectData.set("owner",
                        Lists.newArrayList(objectData.get(GoalValueConstants.CHECK_OBJECT_ID).toString()));
                objectData.setDescribeId(describeId);
                objectData.setName(
                        goalValueCommonService.generateGoalValueName(ruleId,
                                detailId,
                                fiscalYear,
                                GoalEnum.GoalTypeValue.EMPLOYEE.getValue(),
                                objectData.get(GoalValueConstants.CHECK_OBJECT_ID).toString()));
            }
        }
    }

    private void validateDeptAndEmployeeIsResponsible(List<String> responsibleDeptIds, List<String> subordianteIds)
            throws CrmCheckedException {
        validateDeptResponsible(responsibleDeptIds);
        validateEmployeeResponsible(responsibleDeptIds, subordianteIds);
    }

    private void validateDeptResponsible(List<String> responsibleDeptIds) {
        if (CollectionUtils.notEmpty(deptIds)) {
            if (CollectionUtils.notEmpty(responsibleDeptIds)) {
                List<String> newDeptIds = Lists.newArrayList(deptIds);
                newDeptIds.removeAll(responsibleDeptIds);
                if (CollectionUtils.notEmpty(newDeptIds)) {
                    throw new ValidateException("部门超出负责范围，无法对其设置目标值！");
                }
            } else {
                throw new ValidateException("部门超出负责范围，无法对其设置目标值！");
            }
        }
    }

    private void validateEmployeeResponsible(List<String> responsibleDeptIds, List<String> subordianteIds) {
        if (CollectionUtils.notEmpty(userIds)) {
            List<String> newUserIds = Lists.newArrayList(userIds);
            newUserIds.removeAll(subordianteIds);

            if (CollectionUtils.notEmpty(newUserIds)) {
                throw new ValidateException("员工超出负责范围，无法对其设置目标值！");
            }

            if (CollectionUtils.empty(responsibleDeptIds) && userIds.contains(actionContext.getUser().getUserId())) {
                Boolean isAllow = String.valueOf(ruleData.get(GoalRuleObj.ALLOW_PERSONAL_MODIFY_GOAL)).equals("1");
                if (!isAllow) {
                    throw new ValidateException("不允许个人修改目标值！");
                }
            }
        }
    }

    private void validateDeptInApplyCircles(List<String> applyCircleIds) {
        deptIds = objectDatas.stream()
                .filter(x -> String.valueOf(x.get(GoalValueConstants.GOAL_TYPE))
                        .equals(GoalEnum.GoalTypeValue.CIRCLE.getValue()))
                .map(x -> x.get(GoalValueConstants.CHECK_OBJECT_ID).toString())
                .collect(Collectors.toList());

        if (CollectionUtils.notEmpty(deptIds)) {
            List<String> newDeptIds = Lists.newArrayList(deptIds);
            newDeptIds.removeAll(applyCircleIds);
            if (CollectionUtils.notEmpty(newDeptIds)) {
                throw new ValidateException("部门不在目标适用考核部门范围之内，无法对其设置目标值！");
            }
        }
    }

    private void validateEmployeeInApplyCircle(List<String> applyCircleIds) {
        userIds = objectDatas.stream()
                .filter(x -> String.valueOf(x.get(GoalValueConstants.GOAL_TYPE))
                        .equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue()))
                .map(x -> x.get(GoalValueConstants.CHECK_OBJECT_ID).toString())
                .collect(Collectors.toList());

        if (CollectionUtils.notEmpty(userIds)) {
            List<String> memberIds = serviceFacade.getMembersByDeptIds(actionContext.getUser(), applyCircleIds);
            List<String> newUserIds = Lists.newArrayList(userIds);
            newUserIds.removeAll(memberIds);
            if (CollectionUtils.notEmpty(newUserIds)) {
                throw new ValidateException("员工不在目标适用考核部门范围之内，无法对其设置目标值！");
            }
        }
    }

    @Override
    protected Result doAct(Arg arg) {
        if (CollectionUtils.empty(objectDatas)) {
            return Result.builder()
                    .objectDatas(Lists.newArrayList())
                    .build();
        }

        for (IObjectData objectData : objectDatas) {
            if (objectData.getId() == null || StringUtils.isNullOrEmpty(objectData.getId())) {
                toAddList.add(objectData);
            } else {
                toUpdateList.add(objectData);
            }
        }

        List<IObjectData> result = Lists.newArrayList();

        if (CollectionUtils.notEmpty(toAddList)) {
            result.addAll(saveValue(actionContext.getUser(), toAddList));
        }

        if (CollectionUtils.notEmpty(toUpdateList)) {
            result.addAll(updateValue(actionContext.getUser(), toUpdateList));
        }

        List<ObjectDataDocument> resultDocument = result.stream().map(data -> ObjectDataDocument.of(data))
                .collect(Collectors.toList());

        return Result.builder()
                .objectDatas(resultDocument)
                .build();
    }

    private List<IObjectData> saveValue(User user, List<IObjectData> objectDataList) {
        List<IObjectData> result = serviceFacade.bulkSaveObjectData(objectDataList, user);
        return result;
    }

    private List<IObjectData> updateValue(User user, List<IObjectData> objectDataList) {
        List<IObjectData> result = serviceFacade.batchUpdate(objectDataList, user);
        return result;
    }

    private List<QueryUserInfoByIds.UserInfo> getUserInfos() {
        userIds.add(actionContext.getUser().getUserId());
        List<QueryUserInfoByIds.UserInfo> userInfos = serviceFacade.getUserNameByIds(actionContext.getTenantId(),
                actionContext.getUser().getUserId(),
                userIds);
        return userInfos;
    }

    private String getCurrentUserName(List<QueryUserInfoByIds.UserInfo> userInfos) {
        String currentUserName = "";
        Optional<QueryUserInfoByIds.UserInfo> userInfoOptional = userInfos.stream()
                .filter(userInfo -> userInfo.getId().equals(actionContext.getUser().getUserId()))
                .findFirst();

        if (userInfoOptional.isPresent()) {
            currentUserName = userInfoOptional.get().getName();
        }

        return currentUserName;
    }

    @Override
    protected void recordLog() {
        if (CollectionUtils.empty(objectDatas)) {
            return;
        }

        List<QueryUserInfoByIds.UserInfo> userInfos = getUserInfos();
        String currentUserName = getCurrentUserName(userInfos);
        String ruleName = ruleData.getName();
        String dataId = "";

        if (Strings.isNullOrEmpty(detailId)) {
            dataId = ruleId.concat(fiscalYear);
        } else {
            dataId = detailId.concat(fiscalYear);
        }

        for (IObjectData objectData : objectDatas) {
            String goalType = objectData.get(GoalValueConstants.GOAL_TYPE).toString();
            String id = objectData.get(GoalValueConstants.CHECK_OBJECT_ID).toString();
            String name = "";
            Set<Integer> receiverIds = new HashSet<>();
            String remindContent = "";

            if (goalType.equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())) {
                Optional<QueryDeptInfoByDeptIds.DeptInfo> deptInfo = deptInfos.stream()
                        .filter(r -> r.getDeptId().equals(id))
                        .findFirst();
                if (deptInfo.isPresent()) {
                    name = deptInfo.get().getDeptName();
                    if (null != deptInfo.get().getLeaderUserId()) {
                        receiverIds.add(Integer.valueOf(deptInfo.get().getLeaderUserId()));
                    }
                    remindContent = String.format("目标值被修改，目标：%s，子目标：%s，考核部门：%s",
                            ruleName,
                            detailRuleName,
                            name);
                }
            }

            if (goalType.equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())) {
                Optional<QueryUserInfoByIds.UserInfo> userInfo = userInfos.stream()
                        .filter(r -> r.getId().equals(id))
                        .findFirst();
                if (userInfo.isPresent()) {
                    name = userInfo.get().getName();
                    receiverIds.add(Integer.valueOf(id));
                    remindContent = String.format("目标值被修改，目标：%s，子目标：%s，考核人员：%s",
                            ruleName,
                            detailRuleName,
                            name);
                }
            }

            if (!StringUtils.isNullOrEmpty(name)) {
                ///自定义修改记录
                recordCustomLog(currentUserName, dataId, objectData.getName(), name);
                ///发通知给部门负责人或者员工
                sendNotification(receiverIds, remindContent, "目标值变更提醒");
            }
        }

    }

    private void sendNotification(Set<Integer> receiverIds, String remindContent, String title) {
        if (CollectionUtils.notEmpty(receiverIds)) {
            CRMNotification crmNotification = CRMNotification.builder()
                    .sender(actionContext.getUser().getUserId())
                    .remindRecordType(92)
                    .title(title)
                    .content(remindContent)
                    .dataId("")
                    .content2Id(actionContext.getUser().getUserId())
                    .receiverIds(receiverIds)
                    .build();
            crmNotificationService.sendCRMNotification(actionContext.getUser(), crmNotification);
        }
    }

    private void recordCustomLog(String currentUserName, String dataId, String goalValueName, String checkObjectName) {
        IObjectData newData = new ObjectData();
        newData.setId(dataId);
        newData.setName(goalValueName);
        serviceFacade.logCustomMessageOnly(actionContext.getUser(), EventType.MODIFY, ActionType.Empty,
                objectDescribe, newData, String.format("%s 编辑了 %s 的目标值。", currentUserName, checkObjectName));
    }
}
