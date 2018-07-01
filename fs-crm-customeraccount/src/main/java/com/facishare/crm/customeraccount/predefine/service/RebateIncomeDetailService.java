package com.facishare.crm.customeraccount.predefine.service;

import com.facishare.crm.customeraccount.predefine.service.dto.CreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.EditModel;
import com.facishare.crm.customeraccount.predefine.service.dto.GetByRefundIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * 返利相关操作<br>
 * Created by xujf on 2017/9/26.
 */
@ServiceModule("rebate_income_detail")
public interface RebateIncomeDetailService {

    /**
     * 使用场景：a).新建订单 选择返利 自动创建返利收支明细
     * @param
     */
    @ServiceMethod("create")
    CreateModel.Result create(CreateModel.Arg arg, ServiceContext serviceContext);

    /**
     * 通过退款id查询返利交易明细<br>
     * 使用场景：a).退款详情页需要展示返利交易明细<br>
     * @param
     * @return
     */
    @ServiceMethod("get_by_refund_id")
    GetByRefundIdModel.Result getByRefundId(GetByRefundIdModel.Arg arg, ServiceContext serviceContext);

    /**
     * 根据客户获取预存款交易明细列表<br>
     * 使用场景：订货通H5页面显示该客户账户对应的预存款明细列表<br>
     *
     * @return
     */
    @ServiceMethod("list_by_customer_id")
    ListByIdModel.Result listByCustomerId(ListByIdModel.RebateArg arg, ServiceContext serviceContext);

    @ServiceMethod("list_by_customer_account_id")
    ListByIdModel.Result listByCustomerIdCompatible(ListByIdModel.RebateArg arg, ServiceContext serviceContext);

    @ServiceMethod("update")
    EditModel.Result update(EditModel.Arg arg, ServiceContext serviceContext);
}
