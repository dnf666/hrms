package com.facishare.crm.payment.service;


import com.alibaba.fastjson.JSON;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.service.dto.PaymentPlanResult;
import com.facishare.paas.appframework.common.mq.RocketMQMessageSender;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.Count;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.util.SpringUtil;
import com.fxiaoke.paas.gnomon.api.entity.NomonMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@ServiceModule("paymentPlan")
@Component
@Slf4j
public class PaymentPlanService {
  @Autowired
  private ServiceFacade serviceFacade;

  private static final String biz = "payment_plan_overdue_biz";

  private String callArg = "{\"tenantId\":\"%s\",\"dataId\":\"%s\"}";

  @POST
  @ServiceMethod("sum_plan_payment_amount")
  public PaymentPlanResult.Result getSumPlanPaymentAmount(ServiceContext context,
                                                          PaymentPlanResult.Arg arg) {
    BigDecimal sum = BigDecimal.ZERO;
    if (StringUtils.isBlank(arg.getOrderId())) {
      return PaymentPlanResult.Result.builder().planPaymentAmount(sum).build();
    }

    SearchTemplateQuery query = new SearchTemplateQuery();
    Filter filter = new Filter();
    filter.setFieldValues(Lists.newArrayList(arg.getOrderId()));
    filter.setOperator(Operator.EQ);
    filter.setFieldName(PaymentPlanObj.FIELD_ORDER_ID);
    query.addFilters(Lists.newArrayList(filter));
    query.setLimit(100);
    query.setOffset(0);
    query.setPermissionType(0);
    while (true) {
      QueryResult<IObjectData> queryResult = serviceFacade
              .findBySearchQuery(context.getUser(), PaymentObject.PAYMENT_PLAN.getApiName(), query);
      List<IObjectData> list = queryResult.getData();
      if (CollectionUtils.isEmpty(list)) {
        break;
      }
      for (IObjectData x : list) {
        Object o = x.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT);
        Object life = x.get(CrmPackageObjectConstants.FIELD_LIFE_STATUS);
        if (o != null && life != null && !life.toString().equals("invalid")) {
          sum = sum.add(BigDecimal.valueOf(Double.valueOf(o.toString())));
        }
      }
      query.setOffset(query.getOffset() + query.getLimit());
    }
    return PaymentPlanResult.Result.builder().planPaymentAmount(sum).build();
  }


  public void updatePaymentPlanStatus(List<IObjectData> objectDataList, User user) {


    for (IObjectData playObjectData : objectDataList) {
      //实际回款金额是统计字段，需要实时计算
//      BigDecimal actualPaymentAmount =
//              new BigDecimal(playObjectData.get(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT).toString());

      IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), PaymentObject.PAYMENT_PLAN.getApiName());
      List<Count> counts = ObjectDescribeExt.of(describe).getCountFields(PaymentObject.ORDER_PAYMENT.getApiName());
      Map<String, Object> amount = serviceFacade.calculateCountField(user, PaymentObject.PAYMENT_PLAN.getApiName(), playObjectData.getId(), counts);
      BigDecimal actualPaymentAmount = new BigDecimal(amount.get(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT).toString());

      BigDecimal planPaymentAmount =
              new BigDecimal(playObjectData.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT).toString());
      Long planPaymentTime =
              Long.valueOf(playObjectData.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME).toString());
      String planPaymentStatus;

      int result = actualPaymentAmount.compareTo(planPaymentAmount);
      if (result < 0) {
        planPaymentStatus = System.currentTimeMillis() > planPaymentTime ?
                PaymentPlanObj.PlanPaymentStatus.OVERDUE.getName() :
                PaymentPlanObj.PlanPaymentStatus.INCOMPLETE.getName();
      } else {
        planPaymentStatus = PaymentPlanObj.PlanPaymentStatus.COMPLETED.getName();
      }
      playObjectData.set(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, planPaymentStatus);
      Map paramMap = Maps.newHashMap();
      paramMap.put(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, planPaymentStatus);
      serviceFacade.updateWithMap(user, playObjectData, paramMap);
    }
    //serviceFacade.parallelBulkUpdateObjectData(user,objectDataList,true, Lists.newArrayList(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS));
  }

  public void createOverdueTask(String tenantId, String dataId, Long overdueTimeMillis) throws ParseException {

    if (System.currentTimeMillis() > overdueTimeMillis)
      return;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = format.format(overdueTimeMillis);
    Date overdueTime = format.parse(time);

    RocketMQMessageSender sender = SpringUtil.getContext()
            .getBean("paymentPlanOverdueMQSender", RocketMQMessageSender.class);
    NomonMessage message = NomonMessage
            .builder()
            .biz(biz)
            .tenantId(tenantId)
            .dataId(dataId)
            .executeTime(overdueTime)
            .callArg(String.format(callArg, tenantId, dataId))
            .build();

    try {
      sender.sendMessage(JSON.toJSONBytes(message));
    } catch (Exception e) {
      log.error(e.getMessage());
    }

  }
}
