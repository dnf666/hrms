package com.facishare.crm.payment.action;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportDataAction;
import com.facishare.paas.metadata.api.IObjectData;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderPaymentInsertImportDataAction extends StandardInsertImportDataAction {

    @Override
    protected void customAfterImport(List<IObjectData> actualList) {
        log.debug("OrderPaymentInsertImportDataAction customAfterImport actualList:{}", actualList);
        super.customAfterImport(actualList);
        actualList.forEach(x -> {
            String paymentId = (String) x.get(OrderPaymentObj.FIELD_PAYMENT_ID);
            String orderId = (String) x.get(OrderPaymentObj.FIELD_ORDER_ID);
            if (StringUtils.isNotEmpty(paymentId)) {
                IObjectData data = serviceFacade
                        .findObjectData(actionContext.getUser(), paymentId, PaymentObject.CUSTOMER_PAYMENT.getApiName());
                if (data != null) {
                    String ids = getOrderList(data);
                    if (StringUtils.isNotEmpty(orderId)) {
                        ids += "," + orderId;
                    }
                    Map paramMap = Maps.newHashMap();
                    paramMap.put(CustomerPaymentObj.FIELD_ORDER_ID, ids);
                    //update by quzf,父类有批量插入异步操作，所以此处更新时不关注版本号，防止和父类同时保存主数据版本号不一致报错
                    serviceFacade.updateWithMap(actionContext.getUser(), data, paramMap);
                }
            }
        });
    }


    private String getOrderList(IObjectData data) {
        String str = "";
        List<IObjectData> dataList =
                serviceFacade.findDetailObjectDataListIgnoreFormula(data, actionContext.getUser());
        if (CollectionUtils.isNotEmpty(dataList)) {
            List<String> s = Lists.newArrayList();
            dataList.forEach(x -> {
                s.add((String) x.get(OrderPaymentObj.FIELD_ORDER_ID));
            });
            str = Joiner.on(",").join(s);
        }
        return str;
    }
}
