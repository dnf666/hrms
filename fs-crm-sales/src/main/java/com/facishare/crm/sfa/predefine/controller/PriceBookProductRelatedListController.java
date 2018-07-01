package com.facishare.crm.sfa.predefine.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.service.PriceBookService;
import com.facishare.crm.sfa.predefine.service.ProductCommonService;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.github.autoconf.ConfigFactory;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

@Component
public class PriceBookProductRelatedListController extends StandardRelatedListController {
    private static Set<String> tenantIdsOrderByName = Sets.newHashSet();

    static {
        ConfigFactory.getConfig("fs-crm-java-config", config -> {
            String tenantIdsOrderByNameString = config.get("tenantIdsOrderByName");
            if (!Strings.isNullOrEmpty(tenantIdsOrderByNameString)) {
                tenantIdsOrderByName = Sets.newHashSet(tenantIdsOrderByNameString.split(","));
            }

        });
    }


    PriceBookService priceBookService = (PriceBookService) SpringUtil.getContext().getBean("priceBookService");
    ProductCommonService productCommonService = (ProductCommonService) SpringUtil.getContext().getBean("productCommonService");
    private boolean isPriceBookSelect = false;

    protected void doFunPrivilegeCheck() {
        verifyIsPriceBookSelect();
        if (isPriceBookSelect) {
            return;
        }
        super.doFunPrivilegeCheck();
    }

    @Override
    protected QueryResult<IObjectData> getQueryResult(SearchTemplateQuery query) {
        //Start 价目表明细列表 安卓与ios默认按照产品名称排序
        String tenantId = controllerContext.getTenantId();
        String clientInfo = controllerContext.getRequestContext().getClientInfo();
        List<OrderBy> orderBys = query.getOrders();
        if (clientInfo != null && tenantIdsOrderByName.contains(tenantId) &&
                (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX))) {
            orderBys.clear();

            OrderBy orderBy = new OrderBy("product_name", true);
            orderBy.setIndexName(null);
            orderBy.setReference(false);
            orderBys.add(orderBy);

        }
        // web端中的产品排序是按照id排序的 现在改成根据name排序
        orderBys.forEach(o -> {
            if ("product_id".equals(o.getFieldName())) {
                o.setFieldName("product_name");
            }
        });

        QueryResult<IObjectData> queryResult = super.getQueryResult(query);
        //特殊给订单选择价目表产品时，填充产品数据
        if (isPriceBookSelect) {
            productCommonService.fillDataWithProduct(this.getControllerContext().getUser(), queryResult.getData());
        }
        return queryResult;
    }

    /**
     * 1、支持产品分类树的特殊查询
     * 2、如果是价目表关联价目表产品时，过滤数据权限
     */
    protected void handleFilters(SearchTemplateQuery query) {
        super.handleFilters(query);
        if (isPriceBookSelect) {
            query.setPermissionType(0);
        }
        priceBookService.specialProductFilters(this.getControllerContext().getUser(), query.getFilters());
    }

    /**
     * 判断入口是价目表时
     */
    private void verifyIsPriceBookSelect() {
        //终端会传递价目表的apiname，web端会在object_data中添加pricebook_id字段标识是从价目表选择
        if (Utils.PRICE_BOOK_API_NAME.equals(arg.getTargetObjectApiName()) || (arg.getObjectData() != null && arg.getObjectData().get("pricebook_id") != null)) {
            isPriceBookSelect = true;
        }
    }

}
