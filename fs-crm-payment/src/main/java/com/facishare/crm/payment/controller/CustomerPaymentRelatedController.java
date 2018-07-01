package com.facishare.crm.payment.controller;

import static com.facishare.paas.appframework.metadata.LayoutExt.PAYMENT_DESCRIBE_API_NAME;

import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.payment.dto.PaymentRecord;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerPaymentRelatedController extends StandardRelatedController {

  @Override
  protected Result doService(Arg arg) {
    Result result = super.doService(arg);
    result.getRefObjects().add(generateOpenPayRecords());
    return result;
  }

  private Map<String, Object> generateOpenPayRecords() {
    IObjectDescribe objectDescribe = serviceFacade
        .findObject(controllerContext.getTenantId(), arg.getObjectDescribeApiName());
    IObjectData objectData = serviceFacade
        .findObjectData(controllerContext.getUser(), arg.getObjectDataId(), objectDescribe);
    stopWatch.lap("findObjectLayoutWithType");
    //调用支付服务获取数据
    List<PaymentRecord> paymentList = serviceFacade
        .findPaymentList(controllerContext.getUser(), objectDescribe, objectData);
    stopWatch.lap("findPaymentList");

    Map<String, Object> map = Maps.newHashMap();
    map.put("api_name", PAYMENT_DESCRIBE_API_NAME);

    ObjectDescribeExt describe = ObjectDescribeExt.buildPaymentDescribe();

    int totalNumber = 0;
    if (CollectionUtils.notEmpty(paymentList)) {
      totalNumber = paymentList.size();
    }
    map.put("describe", describe.toMap());
    map.put("total", totalNumber);
    map.put("offset", 0);
    map.put("limit", 2);
    map.put("related_list_name", PAYMENT_DESCRIBE_API_NAME + "_LIST");

    if (CollectionUtils.notEmpty(paymentList)) {
      map.put("data", paymentList.stream().limit(2).map(data -> {
        Map<String, Object> oneData = Maps.newHashMap();
        Map<String, Object> dataDetail = Maps.newHashMap();
        dataDetail.put("amount", data.getAmount());
        dataDetail.put("fee", data.getFee());
        dataDetail.put("payEnterpriseName", data.getPayEnterpriseName());
        dataDetail.put("remark", data.getRemark());
        dataDetail.put("payType", data.getPayType());
        dataDetail.put("finishTime", data.getFinishTime());
        dataDetail.put("transTime", data.getTransTime());
        dataDetail.put("relatedObject", data.getRelatedObject());
        dataDetail.put("relatedObjectName", data.getRelatedObjectName());
        dataDetail.put("payStatus", data.getPayStatus());
        dataDetail.put("detailUrl", data.getDetailUrl());
        dataDetail.put("name", data.getOrderNo());
        dataDetail.put(IObjectData.ID, data.getOrderNo());
        oneData.put("data", dataDetail);
        return oneData;
      }).collect(Collectors.toList()));
    } else {
      map.put("data", Lists.newArrayList());
    }
    return map;
  }
}
