package com.facishare.crm.erpstock.predefine.controller;

import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;

import com.facishare.paas.metadata.api.search.Wheres;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;

import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.impl.search.Where;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


/**
 * @author linchf
 * @date 2018/5/10
 */

@Slf4j(topic = "erpStockAccess")
public class ErpStockListController extends StandardListController {
    private ErpStockManager erpStockManager = (ErpStockManager) SpringUtil.getContext().getBean("erpStockManager");


    @Override
    protected void before(Arg arg) {
        erpStockManager.checkErpStockEnable(this.controllerContext.getTenantId());
        super.before(arg);
    }

    @Override
    protected QueryResult<IObjectData> getQueryResult(SearchTemplateQuery query) {
        if (erpStockManager.isNotShowZeroStock(controllerContext.getTenantId())) {
            //过滤可用库存和实际库存为0的数据
            if (erpStockManager.isNotShowZeroStock(controllerContext.getTenantId())) {
                //N有问题  这里用NIN
                Filter availableStockFilter = new Filter();
                availableStockFilter.setFieldName(ErpStockConstants.Field.AvailableStock.apiName);
                availableStockFilter.setOperator(Operator.NIN);
                availableStockFilter.setFieldValueType("number");
                availableStockFilter.setFieldValues(Arrays.asList("0"));
                availableStockFilter.setConnector(Where.CONN.OR.toString());

                Wheres availableStockWheres = new Wheres();
                availableStockWheres.setConnector(Where.CONN.OR.toString());
                availableStockWheres.setFilters(Arrays.asList(availableStockFilter));


                Filter realStockFilter = new Filter();
                realStockFilter.setFieldName(ErpStockConstants.Field.RealStock.apiName);
                realStockFilter.setOperator(Operator.NIN);
                realStockFilter.setFieldValueType("number");
                realStockFilter.setFieldValues(Arrays.asList("0"));
                realStockFilter.setConnector(Where.CONN.OR.toString());

                Wheres realStockWheres = new Wheres();
                realStockWheres.setConnector(Where.CONN.OR.toString());
                realStockWheres.setFilters(Arrays.asList(realStockFilter));

                query.setWheres(Arrays.asList(availableStockWheres, realStockWheres));
            }

        }
        QueryResult<IObjectData> result = super.getQueryResult(query);
        return result;
    }
}
