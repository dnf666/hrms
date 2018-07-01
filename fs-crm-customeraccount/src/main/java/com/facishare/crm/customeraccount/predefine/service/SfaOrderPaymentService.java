package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.BatchGetRebateAmountModel;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateUseRuleValidateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaOrderPaymentModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * Created by xujf on 2018/1/4.
 */
@ServiceModule("order_payment")
public interface SfaOrderPaymentService {

    @ServiceMethod("create")
    SfaOrderPaymentModel.CreateResult create(SfaOrderPaymentModel.CreateArg arg, ServiceContext serviceContext);

    @ServiceMethod("flow_complete")
    SfaOrderPaymentModel.Result flowComplete(SfaOrderPaymentModel.FlowCompleteArg arg, ServiceContext serviceContext);

    @ServiceMethod("invalid")
    SfaOrderPaymentModel.Result invalid(SfaOrderPaymentModel.InvalidArg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_invalid")
    SfaOrderPaymentModel.Result bulkInvalid(SfaOrderPaymentModel.BulkInvalidArg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_recover")
    SfaOrderPaymentModel.Result bulkRecover(SfaOrderPaymentModel.BulkRecoverArg arg, ServiceContext serviceContext);

    @ServiceMethod("bulk_delete")
    SfaOrderPaymentModel.Result bulkDelete(SfaOrderPaymentModel.BulkDeleteArg arg, ServiceContext serviceContext);

    @ServiceMethod("edit")
    SfaOrderPaymentModel.Result edit(SfaOrderPaymentModel.EditArgNew arg, ServiceContext serviceContext);

    @ServiceMethod("get_relative_names_by_order_payment_id")
    SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdResult getRelativeNamesByOrderPaymentId(SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdArg arg, ServiceContext serviceContext);

    @ServiceMethod("get_order_payment_cost_by_order_payment_id")
    SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdResult getOrderPaymentCostByOrderPaymentId(SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdArg arg, ServiceContext serviceContext);

    @ServiceMethod("validate_rebate_use_rule")
    RebateUseRuleValidateModel.Result validateRebateUseRule(RebateUseRuleValidateModel.Arg arg, ServiceContext serviceContext);

    @ServiceMethod("batch_get_rebate_amount_by_order_payment_ids")
    BatchGetRebateAmountModel.Result batchGetRebateAmountByOrderPaymentIds(BatchGetRebateAmountModel.Arg arg, ServiceContext serviceContext);

}
