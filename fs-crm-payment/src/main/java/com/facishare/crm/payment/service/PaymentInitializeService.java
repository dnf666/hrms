package com.facishare.crm.payment.service;

import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.initialize.CustomerPaymentObjectDescribe;
import com.facishare.crm.payment.initialize.ObjectDescribeInitializer;
import com.facishare.crm.payment.initialize.OrderPaymentObjectDescribe;
import com.facishare.crm.payment.initialize.PaymentPlanObjectDescribe;
import com.facishare.crm.payment.service.dto.PaymentInitialize;
import com.facishare.crm.payment.service.dto.PaymentInitialize.InitializeMode;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServiceModule("payment")
public class PaymentInitializeService {

  @Autowired
  private ServiceFacade serviceFacade;
  @Autowired
  private IObjectDescribeService describeService;

  private ObjectDescribeInitializer describeInitializer;

  @PostConstruct
  public void InitializeParameters() {
    this.describeInitializer = new ObjectDescribeInitializer(serviceFacade, describeService);
  }

  @ServiceMethod("initialize")
  public PaymentInitialize.Result initialize(PaymentInitialize.Arg arg) {
    List<String> fails = new ArrayList<>();
    List<String> tenantIds = Arrays.asList(arg.getTenantIds().split(","));
    if (null == arg.getMode()) {
      arg.setMode(InitializeMode.ALL);
    }
    switch (arg.getMode()) {
      case ALL:
        fails = initializeAll(tenantIds);
        break;
      case NEW:
        fails = initializeNew(tenantIds);
        break;
      case CUSTOMER_PAYMENT:
        fails = initializeCustomerPayment(tenantIds);
        break;
      case ORDER_PAYMENT:
        fails = initializeOrderPayment(tenantIds);
        break;
      case PAYMENT_PLAN:
        fails = initializePaymentPlan(tenantIds);
        break;
      default:
        return PaymentInitialize.Result.builder().fails(fails).build();
    }
    return PaymentInitialize.Result.builder().fails(fails).build();
  }

  private List<String> initializeAll(List<String> tenantIds) {
    Set<String> fails = new HashSet<>();
    fails.addAll(initializePaymentPlan(tenantIds));
    fails.addAll(initializePayment(tenantIds));
    return new ArrayList<>(fails);
  }

  private List<String> initializeNew(List<String> tenantIds) {
    Set<String> fails = new HashSet<>();
    fails.addAll(initializePaymentPlan(tenantIds));
    fails.addAll(initializeOrderPayment(tenantIds));
    return new ArrayList<>(fails);
  }

  private List<String> initializePaymentPlan(List<String> tenantIds) {
    Set<String> fails = new HashSet<>();
    for (String tenantId : tenantIds) {
      if (!describeInitializer.initialize(tenantId, new PaymentPlanObjectDescribe())) {
        fails.add(tenantId);
      }
    }
    return new ArrayList<>(fails);
  }

  private List<String> initializeCustomerPayment(List<String> tenantIds) {
    Set<String> fails = new HashSet<>();
    for (String tenantId : tenantIds) {
      if (!describeInitializer.transfer(tenantId, new CustomerPaymentObjectDescribe(),
          PaymentObject.ORDER_PAYMENT.getApiName(), CustomerPaymentObjectDescribe.ACTION_MAPPING)) {
        fails.add(tenantId);
      }
    }
    return new ArrayList<>(fails);
  }

  private List<String> initializeOrderPayment(List<String> tenantIds) {
    Set<String> fails = new HashSet<>();
    for (String tenantId : tenantIds) {
      if (!describeInitializer.initialize(tenantId, new OrderPaymentObjectDescribe())) {
        fails.add(tenantId);
      }
    }
    return new ArrayList<>(fails);
  }

  private List<String> initializePayment(List<String> tenantIds) {
    Set<String> fails = new HashSet<>();
    for (String tenantId : tenantIds) {
      if (!describeInitializer.transfer(tenantId, new CustomerPaymentObjectDescribe(),
          PaymentObject.ORDER_PAYMENT.getApiName(), CustomerPaymentObjectDescribe.ACTION_MAPPING)) {
        fails.add(tenantId);
      }
      if (!describeInitializer.initialize(tenantId, new OrderPaymentObjectDescribe())) {
        fails.add(tenantId);
      }
      if (!describeInitializer.transferFunctions(tenantId)) {
        fails.add(tenantId);
      }
    }
    return new ArrayList<>(fails);
  }
}
