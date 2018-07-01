package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.api.IObjectData;

import org.springframework.stereotype.Component;

/**
 * Created by luxin on 2018/1/31.
 */
@Component
public class ReturnedGoodsInvoiceProductDetailController extends StandardDetailController {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        //退货单产品默认没有主属性，设置产品名称作为主属性显示
        this.data.set(IObjectData.NAME, this.data.get("product_id__r"));
    }
}
