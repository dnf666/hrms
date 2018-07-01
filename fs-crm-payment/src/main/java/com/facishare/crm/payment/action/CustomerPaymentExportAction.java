package com.facishare.crm.payment.action;

import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.CustomerPaymentService;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardExportAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.ActionContext;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerPaymentExportAction extends StandardExportAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPaymentExportAction.class);

    private IObjectDescribe orderPaymentDescribe;

    private CustomerPaymentService service = SpringUtil.getContext()
            .getBean(CustomerPaymentService.class);

    @Override
    protected Result doAct(Arg arg) {
        LOGGER.debug("CustomerPayment export arg: {}", arg);
        return super.doAct(arg);
    }

    @Override
    protected int validateThrottle() {
        orderPaymentDescribe = serviceFacade
                .findObject(actionContext.getTenantId(), PaymentObject.ORDER_PAYMENT.getApiName());
        int count = super.validateThrottle();
        QueryResult<IObjectData> orderPaymentData = findObjectByQuery(actionContext.getUser(),
                orderPaymentDescribe, searchQuery);
        int orderPaymentCount = orderPaymentData.getTotalNumber();
        if (count + orderPaymentCount > EXPORT_ROWS_THROTTLE) {
            throw new ValidateException("导出回款自动导出回款下的回款明细，单次导出：回款+回款明细的总行数不能超过10万条。请筛选后重新导出。");
        }
        return count + orderPaymentCount;
    }

    @Override
    protected Map<String, List<IObjectData>> generateDataMap() {
        Map<String, List<IObjectData>> dataMap = super.generateDataMap();
        List<IObjectData> paymentData=parseOrderNames(dataMap.get(PaymentObject.CUSTOMER_PAYMENT.getApiName()));
        dataMap.put(PaymentObject.CUSTOMER_PAYMENT.getApiName(),
                parseDateTime(objectDescribe,paymentData));
        List<IObjectData> orderPaymentData = findTotalDataList(actionContext.getUser(),
                orderPaymentDescribe, searchQuery);
        dataMap.put(orderPaymentDescribe.getApiName(),parseDateTime(orderPaymentDescribe,orderPaymentData));
        return dataMap;
    }

    @Override
    protected QueryResult<IObjectData> findObjectByQuery(User user, IObjectDescribe describe,
                                                         SearchTemplateQuery query) {
        if (PaymentObject.CUSTOMER_PAYMENT.getApiName().equals(describe.getApiName())) {
            return super.findObjectByQuery(user, describe, query);
        }
        IActionContext context = new ActionContext();
        context.setEnterpriseId(user.getTenantId());
        context.setUserId(user.getUserId());
        SearchTemplateQuery newQuery = JSON.parseObject(query.toJsonString(), SearchTemplateQuery.class);
        newQuery.resetFilters(Lists.newArrayList(query.getFilters()));
        if (newQuery.getDataRightsParameter() != null) {
            newQuery.getDataRightsParameter().setIsDetailObject(true);
            newQuery.getDataRightsParameter().setMasterIdFieldApiName(OrderPaymentObj.FIELD_PAYMENT_ID);
            newQuery.getDataRightsParameter().setMasterObjectApiName(PaymentObject.CUSTOMER_PAYMENT.getApiName());
        }
        return serviceFacade
                .findDetailDataBySearchQuery(user.getTenantId(), describe.getApiName(), newQuery, context);
    }

    private List<IObjectData> parseOrderNames(List<IObjectData> data) {
        return ObjectDataDocument
                .ofDataList(service.parseOrderNames(actionContext.getUser(), data.stream().map(
                        ObjectDataDocument::of).collect(Collectors.toList())));
    }

    private List<IObjectData> parseDateTime(IObjectDescribe objectDescribe, List<IObjectData> data) {
        return ObjectDataDocument
                .ofDataList(service.parseDateTime(objectDescribe, data.stream().map(
                        ObjectDataDocument::of).collect(Collectors.toList())));
    }
}
