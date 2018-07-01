package com.facishare.crm.goal.controller;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.constant.GoalRuleApplyCircleObj;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.crm.goal.service.GoalRuleCommonService;
import com.facishare.crm.goal.service.GoalValueCommonService;
import com.facishare.crm.privilege.service.UserInfoService;
import com.facishare.paas.appframework.common.util.DocumentBaseEntity;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import com.facishare.paas.metadata.impl.ui.layout.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zhaopx on 2018/4/13.
 */
public class GoalValueListHeaderController extends StandardListHeaderController {

    private final GoalRuleCommonService goalRuleCommonService = (GoalRuleCommonService) SpringUtil.getContext().getBean
            ("goalRuleCommonService");
    private final UserInfoService userInfoService = (UserInfoService) SpringUtil.getContext().getBean("userInfoService");
    private final GoalValueCommonService goalValueCommonService = (GoalValueCommonService) SpringUtil.getContext().getBean
            ("goalValueCommonService");
    private Integer startMonth = 0;
    private final List<String> showFields = Lists.newArrayList(
            GoalValueConstants.ID
            , GoalValueConstants.ANNUAL_VALUE
            , GoalValueConstants.CHECK_OBJECT_ID
            , GoalValueConstants.FISCAL_YEAR
    );

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        if (arg.getGoalRuleId().isEmpty()) {
            throw new ValidateException("goalRuleId不能为空");
        }
        IObjectData data = goalRuleCommonService.findGoalRule(controllerContext.getUser(), arg.getGoalRuleId());
        startMonth = data.get(GoalRuleObj.START_MONTH, Integer.class);
        if(startMonth == null){
            startMonth = 1;
        }
    }

    @Override
    protected List<IButton> getButtons(){
        List<IButton> retainButtons = Lists.newArrayList();
        List<IButton> buttons = super.getButtons();

        if(!CollectionUtils.isEmpty(buttons)){
            List<String> retainButtonNames = Lists.newArrayList(ObjectAction.BATCH_IMPORT.getActionCode(),ObjectAction.BATCH_EXPORT.getActionCode());
            retainButtons = buttons.stream().filter(button -> retainButtonNames.contains(button.getAction())).collect(Collectors.toList());
        }

        return retainButtons;
    }

    @Override
    protected Result after(Arg arg, Result result) {
        //todo 需返回锁定,编辑的button
        Result rst = super.after(arg, result);
        List<DocumentBaseEntity> fieldList = reorderFieldList(rst.getFieldList(), startMonth);
        rst.setFieldList(fieldList);
        fillSpecialButtons(arg, result);
        return rst;
    }

    private void fillSpecialButtons(Arg arg, Result result){
        Boolean isAdmin = Boolean.FALSE;
        try {
            isAdmin = userInfoService.isAdmin(controllerContext.getTenantId(), controllerContext.getUser().getUserId());
        } catch (CrmCheckedException e) {
            log.error("goalValueListHeader->isAdmin error", e);
        }

        List<String> applyCircleIds = Lists.newArrayList();
        List<IObjectData> applyCircleData = goalRuleCommonService.findGoalRuleApplyCircle(controllerContext.getUser(), arg.getGoalRuleId());
        if(!CollectionUtils.isEmpty(applyCircleData)){
            applyCircleIds = applyCircleData.stream()
                    .map(data -> String.valueOf(data.get(GoalRuleApplyCircleObj.FIELD_APPLY_CIRCLE_ID)))
                    .collect(Collectors.toList());
        }

        List<IButton> buttons = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(applyCircleIds)){
            boolean isLock = goalValueCommonService.isLock(controllerContext.getUser(),arg.getGoalRuleId()
                    ,arg.getGoalRuleDetailId(),arg.getFiscalYear(), GoalEnum.GoalTypeValue.CIRCLE.getValue(),applyCircleIds.get(0));

            IButton editButton = new Button();
            editButton.setLabel(ObjectAction.UPDATE.getActionLabel());
            editButton.setAction("BulkCreate");

            if(isAdmin || !isLock) {
                buttons.add(editButton);
            }

            if(isAdmin){
                if(isLock){
                    IButton unlockButton = new Button();
                    unlockButton.setLabel(ObjectAction.UNLOCK.getActionLabel());
                    unlockButton.setAction(ObjectAction.UNLOCK.getActionCode());
                    buttons.add(unlockButton);
                }else{
                    IButton lockButton = new Button();
                    lockButton.setLabel(ObjectAction.LOCK.getActionLabel());
                    lockButton.setAction(ObjectAction.LOCK.getActionCode());
                    buttons.add(lockButton);
                }
            }
            buttons.addAll(result.getLayout().toLayout().getButtons());
        }
        result.getLayout().toLayout().setButtons(buttons);
    }

    private List<DocumentBaseEntity> reorderFieldList(List<DocumentBaseEntity> fieldList, Integer startMonth) {
        List<DocumentBaseEntity> rst = Lists.newArrayList();
        if (CollectionUtils.isEmpty(fieldList)) {
            return rst;
        }
        Map<Integer, String> data = goalRuleCommonService.getMonthData();
        Map<Integer, String> beforeData = new HashMap<>();
        Map<Integer, String> afterData = new HashMap<>();

        data.forEach((k, v) -> {
            if (k >= startMonth)
                beforeData.put(k, v);
            else
                afterData.put(k, v);
        });

        for (DocumentBaseEntity entity : fieldList) {
            String fieldName = getFieldName(entity);

            if (showFields.contains(fieldName)) {
                rst.add(entity);
            }
//            重新排序
            if (fieldName.equals(GoalValueConstants.ANNUAL_VALUE)) {
                List<Map<String, Object>> beforeList = Lists.newArrayList();
                beforeData.forEach((k, v) -> {
                    Map<String, Object> tmp = new HashMap<>();
                    tmp.put(v, true);
                    beforeList.add(tmp);
                });
                rst.addAll(beforeList.stream().map(DocumentBaseEntity::new).collect(Collectors.toList()));
                List<Map<String, Object>> afterList = Lists.newArrayList();
                afterData.forEach((k, v) -> {
                    Map<String, Object> tmp = new HashMap<>();
                    tmp.put(v, true);
                    afterList.add(tmp);
                });
                rst.addAll(afterList.stream().map(DocumentBaseEntity::new).collect(Collectors.toList()));
            }
        }
        return rst;
    }

    private String getFieldName(DocumentBaseEntity entity) {
        String fieldName = "";
        for (String s : entity.keySet()) {
            fieldName = s;
            break;
        }
        return fieldName;
    }
}
