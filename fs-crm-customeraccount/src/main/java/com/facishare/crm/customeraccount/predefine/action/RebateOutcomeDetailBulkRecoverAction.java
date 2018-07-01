package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Joiner;

public class RebateOutcomeDetailBulkRecoverAction extends StandardBulkRecoverAction {
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    private CrmManager crmManager;

    @Override
    public void before(Arg arg) {
        super.before(arg);
        rebateOutcomeDetailManager = SpringUtil.getContext().getBean(RebateOutcomeDetailManager.class);
        crmManager = SpringUtil.getContext().getBean(CrmManager.class);
        if (!RequestUtil.isFromInner(actionContext)) {
            List<IObjectData> rebateOutcomeDatas = rebateOutcomeDetailManager.listInvalidDataByIds(actionContext.getUser(), arg.getIdList());
            List<String> rebateOutcomeNames = rebateOutcomeDatas.stream().map(IObjectData::getName).collect(Collectors.toList());
            String rebateOutcomeNameMessage = Joiner.on(",").join(rebateOutcomeNames);
            List<String> orderPaymentIds = rebateOutcomeDatas.stream().map(ob -> ObjectDataUtil.getReferenceId(ob, RebateOutcomeDetailConstants.Field.OrderPayment.apiName)).collect(Collectors.toList());
            List<IObjectData> orderPaymentDatas = crmManager.listInvalidOrderPaymentByIds(actionContext.getUser(), orderPaymentIds);
            List<String> orderPaymentNames = orderPaymentDatas.stream().map(IObjectData::getName).collect(Collectors.toList());
            String orderPaymentNamesMessage = Joiner.on(",").join(orderPaymentNames);
            throw new ValidateException(String.format("返利支出{%s}有关联回款，请恢复关联对象，回款编号{%s}", rebateOutcomeNameMessage, orderPaymentNamesMessage));
        }
    }

    @Override
    public Result after(Arg arg, Result result) {
        super.after(arg, result);
        String objectApiName = arg.getObjectDescribeAPIName();
        List<String> ids = arg.getIdList();
        List<IObjectData> rebateOutcomeDatas = serviceFacade.findObjectDataByIds(actionContext.getTenantId(), ids, objectApiName);
        for (IObjectData outcome : rebateOutcomeDatas) {
            String incomeId = outcome.get(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, String.class);
            String outcomeId = outcome.getId();
            BigDecimal amount = outcome.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
            String lifeStatus = outcome.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            rebateOutcomeDetailManager.updateBalanceForOutcome(actionContext.getUser(), incomeId, outcomeId, amount, SystemConstants.LifeStatus.Invalid.value, lifeStatus);
        }
        return result;
    }

    @Override
    protected void doFunPrivilegeCheck() {

    }
}
