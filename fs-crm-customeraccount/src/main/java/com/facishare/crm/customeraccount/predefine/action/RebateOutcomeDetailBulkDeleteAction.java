package com.facishare.crm.customeraccount.predefine.action;

import java.util.List;
import java.util.stream.Collectors;

import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Joiner;

public class RebateOutcomeDetailBulkDeleteAction extends StandardBulkDeleteAction {
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    private CrmManager crmManager;

    @Override
    protected void doFunPrivilegeCheck() {

    }

    @Override
    protected void before(Arg arg) {
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
            throw new ValidateException(String.format("返利支出{%s}有关联回款，请删除关联对象，回款编号{%s}", rebateOutcomeNameMessage, orderPaymentNamesMessage));
        }
    }
}
