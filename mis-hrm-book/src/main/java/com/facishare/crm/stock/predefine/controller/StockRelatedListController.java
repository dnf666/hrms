package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.predefine.manager.StockWarningJobManager;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author linchf
 * @date 2018/3/6
 */
@Component
@Slf4j(topic = "stockAccess")
public class StockRelatedListController extends StandardRelatedListController {
    private StockManager stockManager = (StockManager) SpringUtil.getContext().getBean("stockManager");
    private StockWarningJobManager stockWarningJobManager = (StockWarningJobManager) SpringUtil.getContext().getBean("stockWarningJobManager");


    boolean isStockWarning = false;
    @Override
    protected void doFunPrivilegeCheck() {
        initObjectData();
        super.doFunPrivilegeCheck();
//        //检查库存时不用校验权限
//        if (!Objects.equals(arg.getRelatedListName(), StockUtils.CHECK_STOCK_WARNING)) {
//            super.doFunPrivilegeCheck();
//        }
    }

    @Override
    protected Result doService(StandardRelatedListController.Arg arg) {
        Result result;
        QueryResult<IObjectData> queryResult = new QueryResult<>();
        result = super.doService(arg);
        if (!isStockWarning) {
            List<ObjectDataDocument> dataList = result.getDataList();
            if (!CollectionUtils.isEmpty(dataList)) {
                result.setDataList(stockManager.fillSafetyStock(controllerContext.getUser(), dataList));
            }
        }
        return result;
    }

    @Override
    protected QueryResult<IObjectData> getQueryResult(SearchTemplateQuery query) {
        if (isStockWarning) {
            QueryResult queryResult = new QueryResult();
            List<IObjectData> objectDataList = stockManager.queryStocksNotEnough(controllerContext.getTenantId(), query.getFilters(), query.getOrders());
            queryResult.setData(objectDataList);
            queryResult.setTotalNumber(objectDataList.size());
            this.stopWatch.lap("findData");

            //清空飘数
            stockWarningJobManager.setRecordRemind(controllerContext.getUser(), 0, Arrays.asList(controllerContext.getUser().getUserId()));
            if(org.apache.commons.collections4.CollectionUtils.isEmpty(queryResult.getData())) {
                return queryResult;
            } else {
                Map refObjectDataMap = this.serviceFacade.findRefObjectDataIfHasQuoteField(this.controllerContext.getUser(), queryResult.getData(), this.objectDescribe);
                this.stopWatch.lap("findRefObjectDatas");
                this.fillQuoteFieldValue(queryResult.getData(), refObjectDataMap);
                this.stopWatch.lap("fillQuoteFieldValue");
                this.fillRefObjectName(queryResult.getData(), refObjectDataMap);
                this.stopWatch.lap("fillRefObjectName");
                return queryResult;
            }
        } else {
            return super.getQueryResult(query);
        }
    }

    private void initObjectData() {
        log.info("StockRelatedList arg[{}]", arg);
        if (StringUtils.isNotBlank(arg.getRelatedListName())) {
            if (Objects.equals(arg.getRelatedListName(), StockUtils.CHECK_STOCK_WARNING)) {
               isStockWarning = true;
            }
        }
    }


}
