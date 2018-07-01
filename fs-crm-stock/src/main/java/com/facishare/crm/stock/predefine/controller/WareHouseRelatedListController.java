package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Created by linchf on 2018/1/25.
 */
@Component
@Slf4j(topic = "stockAccess")
public class WareHouseRelatedListController extends StandardRelatedListController {

    private WareHouseManager wareHouseManager = (WareHouseManager) SpringUtil.getContext().getBean("wareHouseManager");

    private String accountId = null;

    private String filterEnable = null;

    private final String OUTBOUND_DELIVERY_NOTE_TARGET_RELATED_LIST_NAME = "target_related_list_outbound_wh_wh";

    private static final String REQUISITION_NOTE_TRANSFER_OUT_WAREHOUSE = "target_related_list_tow";
    private static final String REQUISITION_NOTE_TRANSFER_IN_WAREHOUSE = "target_related_list_tiw";

    @Override
    protected void doFunPrivilegeCheck() {
        initObjectData();
        //有客户id时，不需要校验功能权限
        if (StringUtils.isBlank(accountId) && StringUtils.isBlank(filterEnable)) {
            super.doFunPrivilegeCheck();
        }
    }

    @Override
    protected Result doService(StandardRelatedListController.Arg arg) {
        List<ILayout> layouts = super.findMobileLayouts();
        SearchTemplateQuery query = this.buildSearchTemplateQuery();
        QueryResult<IObjectData> queryResult = new QueryResult<>();
        if (StringUtils.isNotBlank(accountId)) {
            List<IObjectData> data = wareHouseManager.queryValidByAccountId(controllerContext.getUser(), accountId, query);
            queryResult.setData(data);
            queryResult.setTotalNumber(data.size());
        } else if (StringUtils.isNotBlank(filterEnable)) {
            List<IObjectData> data = wareHouseManager.queryEnable(controllerContext.getUser(), query);
            queryResult.setData(data);
            queryResult.setTotalNumber(data.size());
        } else {
            queryResult = serviceFacade.findBySearchQuery(getControllerContext().getUser(), WarehouseConstants.API_NAME, query);
        }
        return this.buildResult(layouts, query, queryResult);
    }

    @Override
    protected SearchTemplateQuery buildSearchTemplateQuery() {
        SearchTemplateQuery query = this.serviceFacade.getSearchTemplateQuery(this.controllerContext.getUser(), this.objectDescribe, this.getSearchTemplateId(), this.getSearchQueryInfo());
        this.handleFilters(query);
        return query;
    }

    private void initObjectData() {
        log.info("WareHouseRelatedList arg[{}]", arg);
        if (StringUtils.isNotBlank(arg.getRelatedListName())) {
            if (Objects.equals(arg.getRelatedListName(), StockUtils.WAREHOUSE_RELATED_SALES_ORDER)) {
                if (arg.getObjectData().containsKey("account_id")) {
                    accountId = Objects.nonNull(arg.getObjectData().get("account_id")) ? arg.getObjectData().get("account_id").toString() : null;
                }
            }
            if (Objects.equals(arg.getRelatedListName(), StockUtils.WAREHOUSE_RELATED_RETURN_ORDER)
                    || Objects.equals(arg.getRelatedListName(), GoodsReceivedNoteConstants.Field.Warehouse.targetRelatedListName)
                    || Objects.equals(arg.getRelatedListName(),  DeliveryNoteObjConstants.Field.DeliveryWarehouseId.targetRelatedListName)
                    || Objects.equals(arg.getRelatedListName(), OUTBOUND_DELIVERY_NOTE_TARGET_RELATED_LIST_NAME)
                    || Objects.equals(arg.getRelatedListName(), REQUISITION_NOTE_TRANSFER_OUT_WAREHOUSE)
                    || Objects.equals(arg.getRelatedListName(), REQUISITION_NOTE_TRANSFER_IN_WAREHOUSE)) {
                filterEnable = "true";
            }
        }
    }
}
