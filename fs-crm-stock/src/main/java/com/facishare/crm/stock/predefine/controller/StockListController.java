package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.search.Wheres;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.impl.search.Where;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linchf on 2018/1/17.
 */
@Slf4j(topic = "stockAccess")
public class StockListController extends StandardListController {
    private StockManager stockManager = (StockManager) SpringUtil.getContext().getBean("stockManager");

    @Override
    public Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        List<LayoutDocument> layoutDocuments =  result.getListLayouts();
        if (CollectionUtils.isEmpty(layoutDocuments)) {
            return result;
        }

        LayoutDocument layoutDocument = layoutDocuments.get(0);
        boolean hasGoodsReceivedNoteAddPrivilege = stockManager.checkGoodsReceivedNoteAddPrivilege(controllerContext.getUser());
        if (hasGoodsReceivedNoteAddPrivilege) {
            Map buttonMap = new HashMap<>();
            buttonMap.put("action_type", "default");
            buttonMap.put("api_name", "Add_button_default");
            buttonMap.put("action", "Add");
            buttonMap.put("label", "新建入库单");
            layoutDocument.put("buttons", Arrays.asList(buttonMap));
        }

        result.setDataList(stockManager.fillSafetyStock(controllerContext.getUser(), result.getDataList()));
        return result;
    }

    @Override
    protected QueryResult<IObjectData> getQueryResult(SearchTemplateQuery query) {
        //过滤可用库存和实际库存为0的数据
        if (stockManager.isNotShowZeroStock(controllerContext.getTenantId())) {

            //N有问题  这里用NIN
            Filter availableStockFilter = new Filter();
            availableStockFilter.setFieldName(StockConstants.Field.AvailableStock.apiName);
            availableStockFilter.setOperator(Operator.NIN);
            availableStockFilter.setFieldValueType("number");
            availableStockFilter.setFieldValues(Arrays.asList("0"));
            availableStockFilter.setConnector(Where.CONN.OR.toString());

            Wheres availableStockWheres = new Wheres();
            availableStockWheres.setConnector(Where.CONN.OR.toString());
            availableStockWheres.setFilters(Arrays.asList(availableStockFilter));


            Filter realStockFilter = new Filter();
            realStockFilter.setFieldName(StockConstants.Field.RealStock.apiName);
            realStockFilter.setOperator(Operator.NIN);
            realStockFilter.setFieldValueType("number");
            realStockFilter.setFieldValues(Arrays.asList("0"));
            realStockFilter.setConnector(Where.CONN.OR.toString());

            Wheres realStockWheres = new Wheres();
            realStockWheres.setConnector(Where.CONN.OR.toString());
            realStockWheres.setFilters(Arrays.asList(realStockFilter));


            query.setWheres(Arrays.asList(availableStockWheres, realStockWheres));

        }
        QueryResult<IObjectData> result = super.getQueryResult(query);
        return result;
    }
}
