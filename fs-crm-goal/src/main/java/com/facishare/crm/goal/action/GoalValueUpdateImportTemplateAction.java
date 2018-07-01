package com.facishare.crm.goal.action;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.GoalObject;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleDetailObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.service.GoalRuleCommonService;
import com.facishare.crm.goal.service.GoalValueCommonService;
import com.facishare.crm.goal.service.dto.VisibleDeptModel;
import com.facishare.paas.appframework.common.service.dto.QueryDeptInfoByDeptIds;
import com.facishare.paas.appframework.common.service.dto.QueryMemberInfosByDeptIds;
import com.facishare.paas.appframework.common.service.dto.QueryUserInfoByIds;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseImportTemplateAction;
import com.facishare.paas.appframework.core.predef.action.BaseUpdateImportTemplateAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by renlb on 2018/5/4.
 */
@Slf4j
public class GoalValueUpdateImportTemplateAction extends BaseUpdateImportTemplateAction<GoalValueUpdateImportTemplateAction.GoalValueTemplateArg> {

    private GoalRuleCommonService goalRuleCommonService = (GoalRuleCommonService) SpringUtil.getContext()
            .getBean("goalRuleCommonService");
    private GoalValueCommonService goalValueCommonService = (GoalValueCommonService) SpringUtil.getContext()
            .getBean("goalValueCommonService");
    private String ruleName = "";
    private String detailRuleName = "";
    private List<String> userIds = Lists.newArrayList();

    @Override
    protected List<IFieldDescribe> sortHeader(List<IFieldDescribe> validFieldList) {
        List<IFieldDescribe> result = Lists.newArrayList();
        Integer startMonth = getStartMonth(arg.getGoalRuleId());
        Map<Integer, String> fieldsMap = goalValueCommonService.getFieldsMap(startMonth, false);
        for (Map.Entry entry : fieldsMap.entrySet()) {
            List<IFieldDescribe> fields = validFieldList.stream()
                    .filter(field -> field.getApiName().equals(entry.getValue()))
                    .collect(Collectors.toList());
            if (CollectionUtils.notEmpty(fields)) {
                result.add((int) entry.getKey(), fields.get(0));
            }
        }
        return result;
    }

    private Integer getStartMonth(String goalRuleId) {
        if (!Strings.isNullOrEmpty(goalRuleId)) {
            IObjectData ruleData = serviceFacade.findObjectData(actionContext.getUser()
                    , goalRuleId, GoalObject.GOAL_RULE.getApiName());
            if (ruleData != null) {
                Object oStartMonth = ruleData.get(GoalRuleObj.START_MONTH);
                if (oStartMonth != null && !Strings.isNullOrEmpty(oStartMonth.toString())) {
                    return Integer.valueOf(oStartMonth.toString());
                }
            }
        }
        return 1;
    }

    @Override
    protected List<List<String>> customSampleList(List<List<String>> sampleList) {

        List<List<String>> result = Lists.newArrayList();

        try {
            result = fillGoalValues(arg.getGoalRuleId(), arg.getGoalRuleDetailId(), arg.getFiscalYear());
        } catch (CrmCheckedException e) {
            log.error("GoalValueUpdateImportTemplateAction->customSampleList error", e);
        } catch (ParseException e) {
            log.error("GoalValueUpdateImportTemplateAction->customSampleList error", e);
        }

        return result;
    }

    private List<List<String>> fillGoalValues(String goalRuleId, String goalRuleDetailId, String fiscalYear) throws CrmCheckedException, ParseException {
        if (Strings.isNullOrEmpty(goalRuleId)
                || !validRule(actionContext.getUser(), goalRuleId, fiscalYear)
                || !validDetailRule(actionContext.getUser(), goalRuleId, goalRuleDetailId)) {
            return Lists.newArrayList();
        }

        List<String> applyCircleIds = goalRuleCommonService.findGoalRuleApplyCircle(actionContext.getUser(), goalRuleId)
                .stream().map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                .collect(Collectors.toList());

        if (CollectionUtils.empty(applyCircleIds)) {
            return Lists.newArrayList();
        }

        VisibleDeptModel visibleDeptModel = goalValueCommonService.getVisibleDeptIds(actionContext.getUser(), Boolean.FALSE);
        List<String> deptIds = visibleDeptModel.getVisibleDeptIds();
        deptIds.retainAll(applyCircleIds);

        if (CollectionUtils.empty(deptIds)) {
            return Lists.newArrayList();
        }

        deptIds.clear();
        if (CollectionUtils.notEmpty(visibleDeptModel.getResponsibleDeptIds())) {
            deptIds.addAll(visibleDeptModel.getResponsibleDeptIds());
            deptIds.retainAll(applyCircleIds);
        }

        userIds.add(actionContext.getUser().getUserId());

        if (CollectionUtils.notEmpty(visibleDeptModel.getDirectSubordinateIds())) {
            userIds.addAll(visibleDeptModel.getDirectSubordinateIds());
        }

        List<String> distinctResponsibleDeptIds = deptIds.stream().distinct().collect(Collectors.toList());

        List<List<String>> result = Lists.newArrayList();
        result.addAll(getDeptValues(distinctResponsibleDeptIds, fiscalYear));

        List<String> distinctUserIds = userIds.stream().distinct().collect(Collectors.toList());
        result.addAll(getPersonalValues(distinctUserIds, fiscalYear));

        return result;
    }

    private List<List<String>> getPersonalValues(List<String> distinctUserIds, String fiscalYear) {
        List<List<String>> result = Lists.newArrayList();
        if (CollectionUtils.notEmpty(distinctUserIds)) {
            List<QueryUserInfoByIds.UserInfo> userInfos = serviceFacade
                    .getUserNameByIds(actionContext.getTenantId()
                            , actionContext.getUser().getUserId()
                            , distinctUserIds);
            for (String userId : distinctUserIds) {
                List<String> row = Lists.newArrayList();
                row.add(ruleName);
                row.add(detailRuleName);
                row.add(fiscalYear);
                row.add(GoalEnum.GoalTypeValue.EMPLOYEE.getLabel());
                if (userInfos.stream().anyMatch(d -> d.getId().equals(userId))) {
                    row.add(userInfos.stream()
                            .filter(r -> r.getId().equals(userId)).collect(Collectors.toList()).get(0).getName());
                } else {
                    row.add("");
                }
                result.add(row);
            }
        }

        return result;
    }

    private List<List<String>> getDeptValues(List<String> distinctResponsibleDeptIds, String fiscalYear) {
        List<List<String>> result = Lists.newArrayList();
        if (CollectionUtils.notEmpty(distinctResponsibleDeptIds)) {
            Map<String, List<QueryMemberInfosByDeptIds.Member>> deptMemberMap = serviceFacade
                    .getMemberInfoMapByDeptIds(actionContext.getUser()
                            , distinctResponsibleDeptIds
                            , Boolean.FALSE
                            , null
                            , 1);
            if (CollectionUtils.notEmpty(deptMemberMap)) {
                deptMemberMap.values().forEach(members -> members.stream().forEach(member -> userIds.add(member.getId())));
            }
            List<QueryDeptInfoByDeptIds.DeptInfo> deptInfos = serviceFacade
                    .getDeptInfoNameByIds(actionContext.getTenantId()
                            , actionContext.getUser().getUserId()
                            , distinctResponsibleDeptIds);
            for (String deptId : distinctResponsibleDeptIds) {
                List<String> row = Lists.newArrayList();
                row.add(ruleName);
                row.add(detailRuleName);
                row.add(fiscalYear);
                row.add(GoalEnum.GoalTypeValue.CIRCLE.getLabel());
                if (deptInfos.stream().anyMatch(d -> d.getDeptId().equals(deptId))) {
                    row.add(deptInfos.stream()
                            .filter(r -> r.getDeptId().equals(deptId))
                            .collect(Collectors.toList()).get(0).getDeptName());
                } else {
                    row.add("");
                }
                result.add(row);
            }
        }
        return result;
    }

    private boolean validRule(User user, String ruleId, String fiscalYear) {
        IObjectData ruleData = serviceFacade.findObjectData(user, ruleId, GoalObject.GOAL_RULE.getApiName());

        if (ruleData == null || ruleData.isDeleted()) {
            return false;
        }

        if (!ruleData.get(GoalRuleObj.STATUS).toString().equals("1")) {
            return false;
        }

        if (!ruleData.get(GoalRuleObj.COUNT_FISCAL_YEAR).toString().contains(fiscalYear)) {
            return false;
        }

        ruleName = ruleData.getName();

        return true;
    }

    private boolean validDetailRule(User user, String ruleId, String detailRuleId) {
        List<IObjectData> detailRules = goalRuleCommonService.findGoalRules(user, ruleId);
        if (Strings.isNullOrEmpty(detailRuleId)) {
            if (CollectionUtils.notEmpty(detailRules)) {
                return false;
            }
        } else {
            if (CollectionUtils.empty(detailRules) ||
                    detailRules.stream().noneMatch(detailRule -> detailRule.getId().equals(detailRuleId))) {
                return false;
            } else {
                List<IObjectData> matchDetailRules = detailRules.stream()
                        .filter(detail -> detail.getId().equals(detailRuleId)).collect(Collectors.toList());
                if (CollectionUtils.empty(matchDetailRules)) {
                    return false;
                }
                detailRuleName = matchDetailRules.get(0).get(GoalRuleDetailObj.SUBGOAL_NAME).toString();
            }
        }

        return true;
    }

    private Map<Integer, String> getChineseMonths() throws ParseException {
        Map<Integer, String> data = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            data.put(i, getChineseMonth(i));
        }
        return data;
    }

    private String getChineseMonth(int month) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        Date date = sdf.parse(String.valueOf(month));
        sdf = new SimpleDateFormat("MMMMM", Locale.CHINA);
        return sdf.format(date).toLowerCase();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalValueTemplateArg extends BaseImportTemplateAction.Arg {
        private String goalRuleId;
        private String goalRuleDetailId;
        private String fiscalYear;
    }
}
