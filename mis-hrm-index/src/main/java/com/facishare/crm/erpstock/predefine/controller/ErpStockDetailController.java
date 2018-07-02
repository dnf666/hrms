package com.facishare.crm.erpstock.predefine.controller;

import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;

import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author linchf
 * @date 2018/5/14
 */
@Slf4j(topic = "erpStockAccess")
public class ErpStockDetailController extends StandardDetailController {

    private ErpStockManager erpStockManager = (ErpStockManager) SpringUtil.getContext().getBean("erpStockManager");

    @Override
    protected void before(Arg arg) {
        erpStockManager.checkErpStockEnable(this.controllerContext.getTenantId());
        super.before(arg);
    }
}
