package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Created by linchf on 2018/1/19.
 */
@Slf4j(topic = "stockAccess")
public class StockDescribeLayoutController extends StandardDescribeLayoutController {
    @Override
    protected void before(StandardDescribeLayoutController.Arg arg) {
        super.before(arg);
        if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Add.layoutType)) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存不能新建");
        }

        if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存不能编辑");
        }
    }
}
