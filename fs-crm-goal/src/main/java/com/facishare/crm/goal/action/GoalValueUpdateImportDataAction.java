package com.facishare.crm.goal.action;

import java.util.*;
import java.util.stream.Collectors;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.crm.goal.service.GoalRuleCommonService;
import com.facishare.crm.goal.service.GoalValueCommonService;
import com.facishare.crm.goal.service.dto.VisibleDeptModel;
import com.facishare.paas.appframework.common.service.CRMNotificationServiceImpl;
import com.facishare.paas.appframework.common.service.dto.QueryDeptByName;
import com.facishare.paas.appframework.common.service.dto.QueryUserByName;
import com.facishare.paas.appframework.common.service.model.CRMNotification;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.predef.action.StandardUpdateImportDataAction;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by renlb on 2018/5/8.
 */
@Slf4j
public class GoalValueUpdateImportDataAction extends StandardUpdateImportDataAction {
    private GoalRuleCommonService goalRuleCommonService = (GoalRuleCommonService) SpringUtil.getContext()
            .getBean("goalRuleCommonService");
    private GoalValueCommonService goalValueCommonService = (GoalValueCommonService) SpringUtil.getContext()
            .getBean("goalValueCommonService");
    private CRMNotificationServiceImpl crmNotificationService = (CRMNotificationServiceImpl) SpringUtil.getContext()
            .getBean("crmNotificationService");
    private Map<String, String> ruleNameIdMap = Maps.newHashMap();
    private Map<String, String> ruleIdNameMap = Maps.newHashMap();
    private Map<String, String> detailIdNameMap = Maps.newHashMap();
    private List<QueryDeptByName.DeptInfo> deptInfos = Lists.newArrayList();
    private List<QueryUserByName.UserInfo> userInfos = Lists.newArrayList();

    @Override
    protected void customFillFields(List<ImportData> dataList) {
        if (CollectionUtils.empty(dataList)) {
            return;
        }
        fillGoalRuleId(dataList);
        fillGoalRuleDetailId(dataList);
        //fillGoalType(dataList);
        fillCheckObjectId(dataList);
        fillAnnualValue(dataList);
        fillName(dataList);
    }

    private void fillGoalRuleId(List<ImportData> dataList) {
        List<IObjectData> objectDataList = dataList.stream()
                .map(importData -> importData.getData())
                .collect(Collectors.toList());
        if (CollectionUtils.empty(objectDataList)) {
            return;
        }

        List<String> goalNames = Lists.newArrayList();
        for(IObjectData objectData : objectDataList){
            String name = getStringValue(objectData, GoalRuleObj.GOAL_RULE_ID);;
            if(!Strings.isNullOrEmpty(name) && !goalNames.contains(name)){
                goalNames.add(name);
            }
        }
        ruleNameIdMap = goalRuleCommonService.getRuleNameIdMap(actionContext.getUser(), goalNames);
        for (ImportData importData : dataList) {
            String name = getStringValue(importData.getData(), GoalRuleObj.GOAL_RULE_ID);
            if (!Strings.isNullOrEmpty(name) && ruleNameIdMap.containsKey(name)) {
                ruleIdNameMap.put(ruleNameIdMap.get(name), name);
            }
        }
    }

    private void fillGoalRuleDetailId(List<ImportData> dataList) {
        List<IObjectData> objectDataList = dataList.stream()
                .map(importData -> importData.getData())
                .collect(Collectors.toList());
        if (CollectionUtils.empty(objectDataList)) {
            return;
        }

        List<String> subGoalNames = Lists.newArrayList();
        for(IObjectData objectData : objectDataList){
            String name = getStringValue(objectData, GoalRuleObj.GOAL_RULE_DETAIL_ID);;
            if(!Strings.isNullOrEmpty(name) && !subGoalNames.contains(name)){
                subGoalNames.add(name);
            }
        }

        Map<String, List<IObjectData>> nameDataMap = Maps.newHashMap();
        if(CollectionUtils.notEmpty(subGoalNames)){
            nameDataMap = goalRuleCommonService.getSubGoalNameDataMap(actionContext.getUser(), subGoalNames);
        }

        for(ImportData importData : dataList){
            String ruleName = getStringValue(importData.getData(), GoalRuleObj.GOAL_RULE_ID);
            if(!Strings.isNullOrEmpty(ruleName) && ruleNameIdMap.containsKey(ruleName)){
                String subName = getStringValue(importData.getData(), GoalRuleObj.GOAL_RULE_DETAIL_ID);
                if(!Strings.isNullOrEmpty(subName)){
                    Optional<IObjectData> objectData = nameDataMap.get(subName).stream()
                            .filter(data -> String.valueOf(data.get(GoalRuleObj.GOAL_RULE_ID)).equals(ruleNameIdMap.get(ruleName)))
                            .findFirst();
                    if(objectData.isPresent()){
                        importData.getData().set(GoalRuleObj.GOAL_RULE_DETAIL_ID, objectData.get().getId());
                        detailIdNameMap.put(objectData.get().getId(), subName);
                        continue;
                    }
                }
            }
            importData.getData().set(GoalRuleObj.GOAL_RULE_DETAIL_ID, "");
        }

    }

    private void fillGoalType(List<ImportData> dataList) {
        List<IObjectData> objectDataList = dataList.stream()
                .map(importData -> importData.getData())
                .collect(Collectors.toList());
        if (CollectionUtils.empty(objectDataList)) {
            return;
        }

        for (ImportData importData : dataList) {
            Object oGoalType = importData.getData().get(GoalValueConstants.GOAL_TYPE);
            if (oGoalType != null) {
                if (oGoalType.toString().equals(GoalEnum.GoalTypeValue.CIRCLE.getLabel())) {
                    importData.getData().set(GoalValueConstants.GOAL_TYPE, GoalEnum.GoalTypeValue.CIRCLE.getValue());
                }
                if (oGoalType.toString().equals(GoalEnum.GoalTypeValue.EMPLOYEE.getLabel())) {
                    importData.getData().set(GoalValueConstants.GOAL_TYPE, GoalEnum.GoalTypeValue.EMPLOYEE.getValue());
                }
            } else {
                importData.getData().set(GoalValueConstants.GOAL_TYPE, "");
            }
        }
    }

    private void fillCheckObjectId(List<ImportData> dataList) {
        List<IObjectData> objectDataList = dataList.stream()
                .map(importData -> importData.getData())
                .collect(Collectors.toList());
        if (CollectionUtils.empty(objectDataList)) {
            return;
        }

        deptInfos = getDeptInfos(objectDataList);
        userInfos = getUserInfos(objectDataList);

        for (ImportData importData : dataList) {
            if (importData.getData().get(GoalValueConstants.GOAL_TYPE).toString()
                    .equals(GoalEnum.GoalTypeValue.CIRCLE.getLabel())) {
                Optional<QueryDeptByName.DeptInfo> matchDept = deptInfos.stream()
                        .filter(deptInfo ->
                                deptInfo.getName().equals(String.valueOf(importData.getData().get(GoalValueConstants.CHECK_OBJECT_ID))))
                        .findFirst();
                if (matchDept.isPresent()) {
                    importData.getData().set(GoalValueConstants.CHECK_OBJECT_ID, matchDept.get().getId());
                } else {
                    importData.getData().set(GoalValueConstants.CHECK_OBJECT_ID, "");
                }
            }

            if (importData.getData().get(GoalValueConstants.GOAL_TYPE).toString()
                    .equals(GoalEnum.GoalTypeValue.EMPLOYEE.getLabel())) {
                Optional<QueryUserByName.UserInfo> matchUser = userInfos.stream()
                        .filter(userInfo ->
                                userInfo.getName().equals(String.valueOf(importData.getData().get(GoalValueConstants.CHECK_OBJECT_ID))))
                        .findFirst();
                if (matchUser.isPresent()) {
                    importData.getData().set(GoalValueConstants.CHECK_OBJECT_ID, matchUser.get().getId());
                } else {
                    importData.getData().set(GoalValueConstants.CHECK_OBJECT_ID, "");
                }
            }
        }
    }

    private List<QueryDeptByName.DeptInfo> getDeptInfos(List<IObjectData> objectDataList) {
        List<QueryDeptByName.DeptInfo> deptInfos = Lists.newArrayList();

        List<String> deptNames = objectDataList.stream()
                .filter(objectData -> objectData.get(GoalValueConstants.GOAL_TYPE).toString()
                        .equals(GoalEnum.GoalTypeValue.CIRCLE.getLabel()))
                .map(data -> data.get(GoalValueConstants.CHECK_OBJECT_ID).toString())
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.notEmpty(deptNames)) {
            deptInfos = serviceFacade.getDeptByName(actionContext.getTenantId(),
                    actionContext.getUser().getUserId(),
                    deptNames);
        }

        return deptInfos;
    }

    private List<QueryUserByName.UserInfo> getUserInfos(List<IObjectData> objectDataList) {
        List<QueryUserByName.UserInfo> userInfos = Lists.newArrayList();

        List<String> userNames = objectDataList.stream()
                .filter(objectData -> objectData.get(GoalValueConstants.GOAL_TYPE).toString()
                        .equals(GoalEnum.GoalTypeValue.EMPLOYEE.getLabel()))
                .map(data -> data.get(GoalValueConstants.CHECK_OBJECT_ID).toString())
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.notEmpty(userNames)) {
            userInfos = serviceFacade.getUserByName(actionContext.getTenantId(),
                    actionContext.getUser().getUserId(),
                    userNames);
        }

        return userInfos;
    }

    private void fillAnnualValue(List<ImportData> dataList) {
        if (CollectionUtils.empty(dataList)) {
            return;
        }

        Map<Integer, String> monthMap = goalRuleCommonService.getMonthData();

        for (ImportData importData : dataList) {
            IObjectData objectData = importData.getData();
            Double annaulValue = 0d;
            for (String month : monthMap.values()) {
                Object oMonthValue = objectData.get(month);
                if (oMonthValue != null && !Strings.isNullOrEmpty(oMonthValue.toString())) {
                    annaulValue += Double.valueOf(oMonthValue.toString());
                }
            }

            importData.getData().set(GoalValueConstants.ANNUAL_VALUE, annaulValue.toString());
        }
    }

    private void fillName(List<ImportData> dataList) {
        for (ImportData importData : dataList) {
            IObjectData data = importData.getData();
            String goalType = "";
            String ruleId = "";

            if (importData.getData().get(GoalValueConstants.GOAL_TYPE).toString()
                    .equals(GoalEnum.GoalTypeValue.CIRCLE.getLabel())) {
                goalType = GoalEnum.GoalTypeValue.CIRCLE.getValue();
            }
            if (importData.getData().get(GoalValueConstants.GOAL_TYPE).toString()
                    .equals(GoalEnum.GoalTypeValue.EMPLOYEE.getLabel())) {
                goalType = GoalEnum.GoalTypeValue.EMPLOYEE.getValue();
            }

            if (ruleNameIdMap.containsKey(data.get(GoalValueConstants.GOAL_RULE_ID).toString())) {
                ruleId = ruleNameIdMap.get(data.get(GoalValueConstants.GOAL_RULE_ID).toString());
            }

            importData.getData().set(IObjectData.NAME,
                    goalValueCommonService.generateGoalValueName(ruleId,
                            data.get(GoalValueConstants.GOAL_RULE_DETAIL_ID).toString(),
                            data.get(GoalValueConstants.FISCAL_YEAR).toString(),
                            goalType,
                            data.get(GoalValueConstants.CHECK_OBJECT_ID).toString()));
        }
    }

    @Override
    protected void customValidate(List<ImportData> dataList) {
        super.customValidate(dataList);
        validateData(dataList);
    }

    private void validateData(List<ImportData> dataList) {
        List<ImportError> errList = Lists.newArrayList();
        List<String> responsibleDeptIds = Lists.newArrayList();
        List<String> subordinateIds = Lists.newArrayList();

        try {
            VisibleDeptModel visibleDeptModel = goalValueCommonService.getVisibleDeptIds(actionContext.getUser(), Boolean.FALSE);
            if (CollectionUtils.notEmpty(visibleDeptModel.getResponsibleDeptIds())) {
                responsibleDeptIds = visibleDeptModel.getResponsibleDeptIds();
            }
            subordinateIds.addAll(visibleDeptModel.getAllSubordinateIds());
        } catch (CrmCheckedException e) {
            log.error("GoalValueUpdateImportDataAction->validateData->getVisibleDeptIds error", e);
        }
        boolean hasResponsibleDepts = responsibleDeptIds.size() > 0;

        for (ImportData importData : dataList) {
            String ruleId = importData.getData().get(GoalValueConstants.GOAL_RULE_ID).toString();
            String detailRuleId = importData.getData().get(GoalValueConstants.GOAL_RULE_DETAIL_ID).toString();
            String fiscalYear = importData.getData().get(GoalValueConstants.FISCAL_YEAR).toString();
            String goalType = importData.getData().get(GoalValueConstants.GOAL_TYPE).toString();
            String checkObjectId = importData.getData().get(GoalValueConstants.CHECK_OBJECT_ID).toString();
            if (Strings.isNullOrEmpty(ruleId)
                    || Strings.isNullOrEmpty(fiscalYear)
                    || Strings.isNullOrEmpty(goalType)
                    || Strings.isNullOrEmpty(checkObjectId)) {
                continue;
            }

            String errMsg = validateRule(ruleId, fiscalYear, hasResponsibleDepts, goalType, checkObjectId);
            if (!Strings.isNullOrEmpty(errMsg)) {
                errList.add(new ImportError(importData.getRowNo(), errMsg));
                continue;
            }

            errMsg = validateDetailRule(ruleId, detailRuleId);
            if (!Strings.isNullOrEmpty(errMsg)) {
                errList.add(new ImportError(importData.getRowNo(), errMsg));
                continue;
            }

            errMsg = validateApplyCircle(ruleId, goalType, checkObjectId);
            if (!Strings.isNullOrEmpty(errMsg)) {
                errList.add(new ImportError(importData.getRowNo(), errMsg));
                continue;
            }

            errMsg = validateVisibleScope(goalType, checkObjectId, responsibleDeptIds, subordinateIds);
            if (!Strings.isNullOrEmpty(errMsg)) {
                errList.add(new ImportError(importData.getRowNo(), errMsg));
            }
        }

        mergeErrorList(errList);
    }

    private String validateVisibleScope(String goalType,
                                        String checkObjectId,
                                        List<String> responsibleDeptIds,
                                        List<String> subordinateIds) {
        if (goalType.equals(GoalEnum.GoalTypeValue.CIRCLE.getValue()) && !responsibleDeptIds.contains(checkObjectId)) {
            return "部门超出负责范围";
        }
        if (goalType.equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue()) && !subordinateIds.contains(checkObjectId)) {
            return "人员超出负责范围";
        }

        return "";
    }

    private String validateApplyCircle(String ruleId, String goalType, String checkObjectId) {
        List<String> applyCircleIds = Lists.newArrayList();
        List<IObjectData> circleDatas = goalRuleCommonService.findGoalRuleApplyCircle(actionContext.getUser(), ruleId);
        if (CollectionUtils.empty(circleDatas)) {
            return "目标规则未设置考核部门";
        }
        applyCircleIds = circleDatas.stream()
                .map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                .collect(Collectors.toList());

        if (CollectionUtils.empty(applyCircleIds)) {
            return "目标规则未设置考核部门";
        }

        if (goalType.equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())) {
            if (!applyCircleIds.contains(checkObjectId)) {
                return "部门超出目标规则设置的考核范围";
            }
        }

        if (goalType.equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())) {
            List<String> mainDeptIds = goalValueCommonService.getBelong2DeptIds(actionContext.getUser(), Lists.newArrayList(checkObjectId));

            if (CollectionUtils.empty(mainDeptIds)) {
                return "人员超出目标规则设置的考核范围";
            }

            mainDeptIds.retainAll(applyCircleIds);
            if (CollectionUtils.empty(mainDeptIds)) {
                return "人员超出目标规则设置的考核范围";
            }
        }

        return "";
    }

    private String validateDetailRule(String ruleId, String detailRuleId) {
        List<IObjectData> detailRules = goalRuleCommonService.findGoalRules(actionContext.getUser(), ruleId);
        if (Strings.isNullOrEmpty(detailRuleId)) {
            if (CollectionUtils.notEmpty(detailRules)) {
                return "子目标规则不能为空";
            }
        } else {
            if (CollectionUtils.empty(detailRules)
                    || detailRules.stream().noneMatch(detailRule -> detailRule.getId().equals(detailRuleId))) {
                return "子目标规则不存在";
            }
        }

        return "";
    }

    private String validateRule(String ruleId,
                                String fiscalYear,
                                boolean hasResponsibleDepts,
                                String goalType,
                                String checkObjectId) {
        IObjectData objectData = serviceFacade.findObjectData(actionContext.getUser(),
                ruleId,
                GoalObject.GOAL_RULE.getApiName());

        if (objectData == null) {
            return "目标规则不存在";
        }

        if (objectData.isDeleted()) {
            return "目标规则已删除";
        }

        Object ruleStatus = objectData.get(GoalRuleObj.STATUS);
        if (ruleStatus == null || !ruleStatus.toString().equals("1")) {
            return "目标规则未启用";
        }

        if (!objectData.get(GoalRuleObj.COUNT_FISCAL_YEAR).toString().contains(fiscalYear)) {
            return "超出目标规则下统计财年范围";
        }

        boolean isAllowPersonalModify = objectData.get(GoalRuleObj.ALLOW_PERSONAL_MODIFY_GOAL).toString().equals("1");

        if (!isAllowPersonalModify
                && !hasResponsibleDepts
                && goalType.equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())
                && checkObjectId.equals(actionContext.getUser().getUserId())) {
            return "目标规则不允许个人修改目标值";
        }

        return "";
    }

    @Override
    protected void customAfterImport(List<IObjectData> actualList) {
        recordImportLog(actualList);
    }

    private void recordImportLog(List<IObjectData> actualList) {
        if (CollectionUtils.empty(actualList)) {
            return;
        }
        String userName = serviceFacade.getUserNameByIds(actionContext.getTenantId(),
                actionContext.getUser().getUserId(),
                Lists.newArrayList(actionContext.getUser().getUserId())).get(0).getName();

        for (IObjectData objectData : actualList) {
            String remindContent = "";
            IObjectData newData = new ObjectData();
            if (Strings.isNullOrEmpty(objectData.get(GoalValueConstants.GOAL_RULE_DETAIL_ID).toString())) {
                newData.setId(objectData.get(GoalValueConstants.GOAL_RULE_ID).toString()
                        .concat(objectData.get(GoalValueConstants.FISCAL_YEAR).toString()));
            } else {
                newData.setId(objectData.get(GoalValueConstants.GOAL_RULE_DETAIL_ID).toString()
                        .concat(objectData.get(GoalValueConstants.FISCAL_YEAR).toString()));
            }
            newData.setName(objectData.getName());
            String name = "";
            String detailRuleName = "无";
            Set<Integer> receiverIds = new HashSet<>();

            if (!Strings.isNullOrEmpty(objectData.get(GoalValueConstants.GOAL_RULE_DETAIL_ID).toString())) {
                detailRuleName = detailIdNameMap.get(objectData.get(GoalValueConstants.GOAL_RULE_DETAIL_ID).toString());
            }

            if (objectData.get(GoalValueConstants.GOAL_TYPE).toString()
                    .equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())) {
                Optional<QueryDeptByName.DeptInfo> matchDept = deptInfos.stream()
                        .filter(deptInfo ->
                                deptInfo.getId().equals(objectData.get(GoalValueConstants.CHECK_OBJECT_ID).toString()))
                        .findFirst();
                if (matchDept.isPresent()) {
                    name = matchDept.get().getName();
                    remindContent = String.format("目标值被修改，目标：%s，子目标：%s，考核部门：%s",
                            ruleIdNameMap.get(objectData.get(GoalValueConstants.GOAL_RULE_ID).toString()),
                            detailRuleName,
                            name);
                    if (null != matchDept.get().getManagerId()) {
                        receiverIds.add(Integer.valueOf(matchDept.get().getManagerId()));
                    }
                }
            }

            if (objectData.get(GoalValueConstants.GOAL_TYPE).toString().equals(GoalEnum.GoalTypeValue.EMPLOYEE.getValue())) {
                Optional<QueryUserByName.UserInfo> matchUser = userInfos.stream()
                        .filter(deptInfo ->
                                deptInfo.getId().equals(objectData.get(GoalValueConstants.CHECK_OBJECT_ID).toString()))
                        .findFirst();
                if (matchUser.isPresent()) {
                    name = matchUser.get().getName();
                    remindContent = String.format("目标值被修改，目标：%s，子目标：%s，考核人员：%s",
                            ruleIdNameMap.get(objectData.get(GoalValueConstants.GOAL_RULE_ID).toString()),
                            detailRuleName,
                            name);
                    receiverIds.add(Integer.valueOf(matchUser.get().getId()));
                }
            }

            serviceFacade.logCustomMessageOnly(actionContext.getUser(),
                    EventType.MODIFY,
                    ActionType.Empty,
                    objectDescribe,
                    newData,
                    String.format("%s 编辑了 %s 的目标值。", userName, name));
            CRMNotification crmNotification = CRMNotification.builder()
                    .sender(actionContext.getUser().getUserId())
                    .remindRecordType(92)
                    .title("目标值变更提醒")
                    .content(remindContent)
                    .dataId("")
                    .content2Id(actionContext.getUser().getUserId())
                    .receiverIds(receiverIds)
                    .build();
            crmNotificationService.sendCRMNotification(actionContext.getUser(), crmNotification);
        }
    }
}
