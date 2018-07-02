package com.facishare.crm.stock.task;

import com.facishare.crm.stock.predefine.manager.SaleOrderManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author linchf
 * @date 2018/4/18
 */
@Slf4j(topic = "stockAccess")
public class ModifySalesOrderLayoutTask implements Runnable {

    private User user;

    public ModifySalesOrderLayoutTask(User user) {
        this.user = user;
    }

    @Override
    public void run() {
        //修改订单订货仓库layout为非必填
        log.warn("modifySalesOrderWarehouseNotRequired start");
        SaleOrderManager saleOrderManager = SpringUtil.getContext().getBean(SaleOrderManager.class);
        saleOrderManager.modifySalesOrderWarehouseNotRequired(user);
        log.warn("modifySalesOrderWarehouseNotRequired end");
    }
}
