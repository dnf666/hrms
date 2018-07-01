package com.facishare.crm.payment.service;

import static com.facishare.crm.payment.constant.CustomerPaymentObj.FIELD_LIFE_STATUS;
import static com.facishare.crm.payment.constant.CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT;
import static com.facishare.crm.payment.constant.CustomerPaymentObj.PAYMENT_METHOD_DNR;
import static com.facishare.crm.payment.constant.CustomerPaymentObj.PAYMENT_METHOD_REBATE;
import static com.facishare.crm.payment.constant.CustomerPaymentObj.PaymentMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.predefine.service.CustomerAccountService;
import com.facishare.crm.customeraccount.predefine.service.SfaOrderPaymentService;
import com.facishare.crm.customeraccount.predefine.service.dto.BatchGetRebateAmountModel;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateUseRuleValidateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaOrderPaymentModel;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.constant.CustomerPaymentObj;
import com.facishare.crm.payment.constant.OrderPaymentObj;
import com.facishare.crm.payment.service.dto.Args;
import com.facishare.crm.payment.service.dto.PaymentDhtMq;
import com.facishare.crm.payment.utils.FieldUtils;
import com.facishare.paas.appframework.common.mq.RocketMQMessageSender;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ActionContext;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.INameCache;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDataProxyService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.paas.metadata.impl.describe.QuoteFieldDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.ws.rs.POST;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@ServiceModule("customer_payment")
@Component
@Slf4j
public class CustomerPaymentService {

  @Autowired
  private ServiceFacade serviceFacade;
  @Autowired
  private IObjectDataProxyService proxyService;
  @Autowired
  private ObjectDataServiceImpl objectDataService;
  @Autowired
  private SfaOrderPaymentService orderPaymentService;
  @Autowired
  private CustomerAccountService customerAccountService;

  @ReloadableProperty("openPayQrCodeUrl")
  private String openPayQrCodeUrl = "https://www.fxiaoke.com/open/fe-pay/pay-qrcode-apply/index.html?busiNo=%s&busiCode=1009&orderName=%s&amount=%.2f&payerEnterpriseName=客户&alone=1&apiName=PaymentObj";

  private PaymentPlanService paymentPlanService =
          SpringUtil.getContext().getBean(PaymentPlanService.class);

  public String generateOpenPayQrCodeUrl(User user, String customerPaymentId) {
    IObjectData customerPayment = serviceFacade
        .findObjectData(user, customerPaymentId, PaymentObject.CUSTOMER_PAYMENT.getApiName());
    if (customerPayment == null) {
      return "";
    }
    return String
        .format(openPayQrCodeUrl, customerPayment.getId(), customerPayment.getName(),
            ConverterUtil
                .convert2Double(customerPayment.get(CustomerPaymentObj.FIELD_PAYMENT_AMOUNT)) * 100);
  }

  public ObjectDataDocument fillWithDetails(RequestContext context, String describeApiName,
      ObjectDataDocument data) {
    List<IObjectDescribe> detailDescribeList = serviceFacade
        .findDetailDescribes(context.getTenantId(), describeApiName);
    Map<String, List<IObjectData>> details = serviceFacade
        .findDetailObjectDataList(detailDescribeList, data.toObjectData(), context.getUser());
    data.put("details", details);
    return data;
  }

  private List<IObjectData> findOrderPayments(User user, String customerPaymentId) {
    SearchTemplateQuery query = new SearchTemplateQuery();
    Filter filter = new Filter();
    filter.setFieldValues(Lists.newArrayList(customerPaymentId));
    filter.setOperator(Operator.EQ);
    filter.setFieldName(OrderPaymentObj.FIELD_PAYMENT_ID);
    query.setFilters(Lists.newArrayList(filter));
    query.setPermissionType(0);
    return findOrderPayments(user, query);
  }

  private List<IObjectData> findOrderPayments(User user, SearchTemplateQuery query) {
    log.debug("Finding order payments: {}", query.toJsonString());
    IObjectDescribe orderPaymentDescribe = serviceFacade
        .findObject(user.getTenantId(), PaymentObject.ORDER_PAYMENT.getApiName());
    int totalCount;
    int offset = 0;
    List<IObjectData> data = new ArrayList<>();
    do {
      query.setLimit(500);
      query.setOffset(offset);
      QueryResult<IObjectData> queryResult = serviceFacade
          .findBySearchQueryWithDeleted(user, orderPaymentDescribe, query);
      totalCount = queryResult.getTotalNumber();
      offset += 500;
      data.addAll(queryResult.getData());
    } while (offset < totalCount);
    return data;
  }

  public List<IObjectData> findOrderPaymentIsDeletedList(User user, String customerPaymentId) {
    SearchTemplateQuery query = new SearchTemplateQuery();
    Filter filter = new Filter();
    filter.setFieldValues(Lists.newArrayList(customerPaymentId));
    filter.setOperator(Operator.EQ);
    filter.setFieldName(OrderPaymentObj.FIELD_PAYMENT_ID);

    Filter delFilter = new Filter();
    delFilter.setFieldValues(Lists.newArrayList("-1"));
    delFilter.setOperator(Operator.EQ);
    delFilter.setFieldName(CrmPackageObjectConstants.FIELD_IS_DELETED);

    query.setFilters(Lists.newArrayList(filter, delFilter));
    query.setPermissionType(0);
    return findOrderPayments(user, query);
  }

  public List<Map> queryOrderPaymentList(ServiceContext context, String customerPaymentId) {
    log.debug("queryOrderPaymentList customerPaymentId:" + customerPaymentId);
    List<Map> dataList = Lists.newArrayList();
    if (StringUtils.isBlank(customerPaymentId)) {
      return dataList;
    }

    SearchTemplateQuery query = new SearchTemplateQuery();
    query.addFilters(Lists.newArrayList(
            FieldUtils.buildFilter(OrderPaymentObj.FIELD_PAYMENT_ID, Lists.newArrayList(customerPaymentId), Operator.IN, 0),
            FieldUtils.buildFilter(OrderPaymentObj.FIELD_IS_DELETED, Lists.newArrayList("0"), Operator.IN, 0))
    );


    query.setLimit(100);
    query.setOffset(0);
    query.setPermissionType(0);
    IObjectDescribe orderPaymentDescribe = serviceFacade
        .findObject(context.getTenantId(), PaymentObject.ORDER_PAYMENT.getApiName());
    while (true) {
      QueryResult<IObjectData> queryResult = serviceFacade
          .findBySearchQueryWithDeleted(context.getUser(), orderPaymentDescribe, query);
      List<IObjectData> list = queryResult.getData();
      if (CollectionUtils.isEmpty(list)) {
        break;
      }
      dataList
          .addAll(list.stream().map(x -> ObjectDataExt.of(x).toMap()).collect(Collectors.toList()));
      query.setOffset(query.getOffset() + query.getLimit());
    }
    return dataList;
  }

  public BaseObjectSaveAction.Arg modifyArg(ActionContext action, BaseObjectSaveAction.Arg arg) {
    ObjectDataDocument objectData = arg.getObjectData();
    if (objectData == null) {
      throw new ValidateException("回款数据不能为空");
    }
    Map<String, List<ObjectDataDocument>> details = arg.getDetails();
    if (details == null) {
      throw new ValidateException("明细不能为空");
    }
    checkCustomerAccountInfo(action, arg);
    IObjectDescribe describe = serviceFacade
        .findObject(action.getTenantId(), action.getObjectApiName());
    if (describe == null) {
      throw new ValidateException("查询不到对象");
    }
    if (arg.getObjectData().get(CrmPackageObjectConstants.FIELD_DESCRIBE_ID) == null) {
      objectData.put(CrmPackageObjectConstants.FIELD_DESCRIBE_ID, describe.getId());
      objectData.put(CrmPackageObjectConstants.FIELD_DESCRIBE_API_NAME, describe.getApiName());
      arg.setObjectData(objectData);
    }

    Set<String> set = Sets.newHashSet();
    List<ObjectDataDocument> orderPaymentDocuments = details
        .getOrDefault(PaymentObject.ORDER_PAYMENT.getApiName(), Lists.newArrayList());
    for (ObjectDataDocument od : orderPaymentDocuments) {
      String orderId = (String) od.getOrDefault(OrderPaymentObj.FIELD_ORDER_ID, "");
      if (StringUtils.isNotBlank(orderId)) {
        set.add(orderId);
      }
      if (od.get(CrmPackageObjectConstants.FIELD_DESCRIBE_ID) == null) {
        IObjectDescribe detailObject = serviceFacade
            .findObject(action.getTenantId(), PaymentObject.ORDER_PAYMENT.getApiName());
        if (detailObject == null) {
          throw new ValidateException("回款明细对象不存在.");
        }
        od.put(CrmPackageObjectConstants.FIELD_DESCRIBE_ID, detailObject.getId());
        od.put(CrmPackageObjectConstants.FIELD_DESCRIBE_API_NAME, detailObject.getApiName());
      }
      od.put(OrderPaymentObj.FIELD_ACCOUNT_ID, objectData.get(CustomerPaymentObj.FIELD_ACCOUNT_ID));
      copyOrderPaymentCustomFieldData(describe, objectData, od);
    }

    if (CollectionUtils.isNotEmpty(set)) {
      Joiner joiner = Joiner.on(",");
      String orderContext = joiner.join(set);
      objectData.put(CustomerPaymentObj.FIELD_ORDER_ID, orderContext);
    }
    log.debug("modifyArg->return arg:{}", arg);
    return arg;
  }

  private void copyOrderPaymentCustomFieldData(IObjectDescribe masterDescribe,
      ObjectDataDocument masterData, ObjectDataDocument detailData) {
    List<IFieldDescribe> fields = ObjectDescribeExt.of(masterDescribe).getFieldDescribesSilently()
        .stream()
        .filter(f -> "custom".equals(f.getDefineType()) && Boolean.TRUE.equals(f.isActive()))
        .collect(Collectors.toList());
    for (IFieldDescribe field : fields) {
      Object value = masterData.get(field.getApiName());
      if (null != value) {
        detailData.putIfAbsent(field.getApiName(), value);
      }
    }
  }

  private void checkCustomerAccountInfo(ActionContext action, BaseObjectSaveAction.Arg arg) {
    String customerId = (String) arg.getObjectData().get(CustomerPaymentObj.FIELD_ACCOUNT_ID);
    if (StringUtils.isBlank(customerId)) {
      return;
    }
    String option = (String) arg.getObjectData().get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
    if (StringUtils.isBlank(option)) {
      return;
    }
    ArrayList<String> options = Lists.newArrayList(CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT,
        CustomerPaymentObj.PAYMENT_METHOD_REBATE, CustomerPaymentObj.PAYMENT_METHOD_DNR);
    if (!options.contains(option)) {
      return;
    }
    boolean enable = getCustomerAccountEnable(action);
    Double prepaySum = 0D;
    Double rebateSum = 0D;
    Map<String, BigDecimal> orderIdRebateAmountMap=Maps.newHashMap();
    if (enable) {
      //统计返利总额，预存款总额
      for (Map.Entry<String, List<ObjectDataDocument>> entry : arg.getDetails().entrySet()) {
        if (CollectionUtils.isEmpty(entry.getValue())) {
          throw new ValidateException("明细不能为空");
        }
        for (ObjectDataDocument od : entry.getValue()) {
          Object prepay = od.get(OrderPaymentObj.PREPAY);
          Object rebate = od.get(OrderPaymentObj.REBATE_OUTCOME);
          Object paymentAmount = od.get(OrderPaymentObj.FIELD_PAYMENT_AMOUNT);
          if (option.equals(CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT)) {
            if (paymentAmount == null || StringUtils.isBlank(paymentAmount.toString())) {
              throw new ValidateException("预存款不能为空");
            }
            prepay = paymentAmount.toString();
          }
          if (option.equals(CustomerPaymentObj.PAYMENT_METHOD_REBATE)) {
            if (paymentAmount == null || StringUtils.isBlank(paymentAmount.toString())) {
              throw new ValidateException("返利不能为空");
            }
            rebate = paymentAmount.toString();
          }
          if (option.equals(CustomerPaymentObj.PAYMENT_METHOD_DNR)) {
            if (rebate == null || StringUtils.isBlank(rebate.toString())) {
              throw new ValidateException("返利不能为空");
            }
            if (prepay == null || StringUtils.isBlank(prepay.toString())) {
              throw new ValidateException("预存款不能为空");
            }
          }
          if (null != prepay && StringUtils.isNotBlank(prepay.toString())) {
            prepaySum += Double.valueOf(prepay.toString());
          }
          if (null != rebate && StringUtils.isNotBlank(rebate.toString())) {
            rebateSum += Double.valueOf(rebate.toString());
          }
        }
      }

      //统计订单返利总额
      for (Map.Entry<String, List<ObjectDataDocument>> entry : arg.getDetails().entrySet()) {
        for (ObjectDataDocument od : entry.getValue()) {
          Object rebate = od.get(OrderPaymentObj.REBATE_OUTCOME);
          Object paymentAmount = od.get(OrderPaymentObj.FIELD_PAYMENT_AMOUNT);
          if (option.equals(CustomerPaymentObj.PAYMENT_METHOD_REBATE)) {
            rebate = paymentAmount.toString();
          }
          Object orderId=od.get(OrderPaymentObj.FIELD_ORDER_ID);
          if (orderId == null || StringUtils.isBlank(orderId.toString())) {
            throw new ValidateException("订单编号不能为空");
          }
          if (null != rebate && StringUtils.isNotBlank(rebate.toString())) {
            if(orderIdRebateAmountMap.containsKey(orderId.toString())){
              orderIdRebateAmountMap.put(orderId.toString(),
                      orderIdRebateAmountMap.get(orderId.toString()).add(new BigDecimal(rebate.toString())));
            }else{
              orderIdRebateAmountMap.put(orderId.toString(),new BigDecimal(rebate.toString()));
            }
          }
        }
      }
    }

    ServiceContext context = new ServiceContext(action.getRequestContext(), null, null);
    CustomerAccountType.PaymentArg paymentArg = new CustomerAccountType.PaymentArg();
    paymentArg.setPrepayToPay(prepaySum);
    paymentArg.setRebateToPay(rebateSum);
    paymentArg.setCustomerId(customerId);
    log.debug("customerAccountService->isBalanceEnough Arg:{}", paymentArg);
    CustomerAccountType.BalanceEnoughResult balanceEnough = customerAccountService
        .isBalanceEnough(context, paymentArg);
    log.debug("customerAccountService->isBalanceEnough result:{}", balanceEnough);
    if (!balanceEnough.isPrepayEnough()) {
      throw new ValidateException("预存款余额不足");
    }
    if (!balanceEnough.isRebateEnough()) {
      throw new ValidateException("返利余额不足");
    }

    //校验返利
    if(orderIdRebateAmountMap.isEmpty()){
      return;
    }
    RebateUseRuleValidateModel.Arg validateArg=new RebateUseRuleValidateModel.Arg();
    validateArg.setCustomerId(customerId);
    validateArg.setOrderIdRebateAmountMap(orderIdRebateAmountMap);
    log.debug("customerAccountService->RebateUseRuleValidate Arg:{}", validateArg);
    RebateUseRuleValidateModel.Result validateResult = orderPaymentService.validateRebateUseRule(validateArg,context);
    log.debug("customerAccountService->RebateUseRuleValidate result:{}", validateResult);
    validateResult.getOrderIdValidateResultMap().forEach((k,v)->{
      if(!v.getCanUseRebate()){
        IObjectData objectData = serviceFacade.findObjectData(action.getUser(),k,"SalesOrderObj");
        throw new ValidateException(MessageFormat.format("订单{0}的本次可使用返利最大金额为{1}元",objectData.getName(),v.getMaxRebateAmountToUse()));
      }
    });
  }

  public void addSyncAccountInfo(ActionContext context, IObjectData objectData,
                                 Map<String, List<JSONObject>> details) {
    boolean enable = getCustomerAccountEnable(context);
    if (!enable) {
      return;
    }
    List<IObjectData> detailObjectDataList = serviceFacade
        .findDetailObjectDataListIgnoreFormula(objectData, context.getUser());
    log.debug("addSyncAccountInfo find findDetailObjectDataList:", detailObjectDataList);
    if (CollectionUtils.isEmpty(detailObjectDataList)) {
      return;
    }

    if (details ==null || details.isEmpty()) {
      return;
    }

    Map<String, SfaOrderPaymentModel.CreateArgDetail> createMap = Maps.newHashMap();
    String method = (String) objectData.get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
    if (StringUtils.isNotBlank(method) && PAYMENT_METHOD_DEPOSIT.equals(method)) {

      detailObjectDataList.forEach(orderData -> {
        ObjectDataDocument prepayData = getPrepayData(orderData, objectData);
        prepayData.put(PrepayDetailConstants.Field.Amount.apiName,
            Double.valueOf(orderData.get(OrderPaymentObj.FIELD_PAYMENT_AMOUNT).toString()));
        prepayData.put(PrepayDetailConstants.Field.OrderPayment.apiName,
            orderData.get(OrderPaymentObj.FIELD_ID).toString());
        SfaOrderPaymentModel.CreateArgDetail argDetail = new SfaOrderPaymentModel.CreateArgDetail();
        argDetail.setPrepayDetailData(prepayData);
        createMap.put(orderData.get(OrderPaymentObj.FIELD_ID).toString(), argDetail);
      });
    }

    if (StringUtils.isNotBlank(method) && PAYMENT_METHOD_REBATE.equals(method)) {
      detailObjectDataList.forEach(orderData -> {
        ObjectDataDocument rebateData = getRebateOutcomeData(orderData, objectData);
        rebateData.put(RebateOutcomeDetailConstants.Field.Amount.apiName,
            Double.valueOf(orderData.get(OrderPaymentObj.FIELD_PAYMENT_AMOUNT).toString()));
        rebateData.put(RebateOutcomeDetailConstants.Field.OrderPayment.apiName,
            orderData.get(OrderPaymentObj.FIELD_ID).toString());
        SfaOrderPaymentModel.CreateArgDetail argDetail = new SfaOrderPaymentModel.CreateArgDetail();
        argDetail.setRebateOutcomeDetailData(rebateData);
        createMap.put(orderData.get(OrderPaymentObj.FIELD_ID).toString(), argDetail);
      });
    }

    if (StringUtils.isNotBlank(method) && PAYMENT_METHOD_DNR.equals(method)) {
      detailObjectDataList.forEach(orderData -> {
        ObjectDataDocument prepayData = getPrepayData(orderData, objectData);
        ObjectDataDocument rebateData = getRebateOutcomeData(orderData, objectData);

        details.values().forEach(rx -> rx.forEach(ry -> {
          String dOrderId = (String) orderData.get(OrderPaymentObj.FIELD_ORDER_ID);
          if (StringUtils.isBlank(dOrderId)) {
            dOrderId = "";
          }
          String dPlanId = (String) orderData.get(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID);
          if (StringUtils.isBlank(dPlanId)) {
            dPlanId = "";
          }
          String rOrderId = (String) ry.get(OrderPaymentObj.FIELD_ORDER_ID);
          if (StringUtils.isBlank(rOrderId)) {
            rOrderId = "";
          }
          String rPlanId = (String) ry.get(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID);
          if (StringUtils.isBlank(rPlanId)) {
            rPlanId = "";
          }
          if ((dOrderId + dPlanId).equals((rOrderId + rPlanId))) {
            prepayData.put(PrepayDetailConstants.Field.Amount.apiName,
                Double.valueOf(ry.get(OrderPaymentObj.PREPAY).toString()));
            prepayData.put(PrepayDetailConstants.Field.OrderPayment.apiName,
                orderData.get(OrderPaymentObj.FIELD_ID).toString());
            rebateData.put(RebateOutcomeDetailConstants.Field.Amount.apiName,
                Double.valueOf(ry.get(OrderPaymentObj.REBATE_OUTCOME).toString()));
            rebateData.put(RebateOutcomeDetailConstants.Field.OrderPayment.apiName,
                orderData.get(OrderPaymentObj.FIELD_ID).toString());
            SfaOrderPaymentModel.CreateArgDetail argDetail = new SfaOrderPaymentModel.CreateArgDetail();
            argDetail.setPrepayDetailData(prepayData);
            argDetail.setRebateOutcomeDetailData(rebateData);
            createMap.put(orderData.get(OrderPaymentObj.FIELD_ID).toString(), argDetail);
          }
        }));
      });
    }

    SfaOrderPaymentModel.CreateArg createArg = new SfaOrderPaymentModel.CreateArg();
    createArg.setOrderPaymentMap(createMap);
    createArg.setPaymentId(objectData.get(CustomerPaymentObj.FIELD_ID).toString());
    syncAddCustomerAccountInfo(createArg, context);
  }

  public void editSyncAccountInfo(ActionContext context, IObjectData objectData) {
    Object option = objectData.get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
    if (option == null) {
      return;
    }
    ArrayList<String> options = Lists.newArrayList(CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT,
        CustomerPaymentObj.PAYMENT_METHOD_REBATE, CustomerPaymentObj.PAYMENT_METHOD_DNR);
    if (!options.contains(option.toString())) {
      return;
    }
    Map<String, String> details = Maps.newHashMap();
    String paymentId = (String) objectData.get(CustomerPaymentObj.FIELD_ID);
    List<IObjectData> orderPaymentObjects =
        serviceFacade.findDetailObjectDataListIgnoreFormula(objectData, context.getUser());
    orderPaymentObjects.forEach(x -> {
      details
          .put(x.getId(), (String) x.get(CrmPackageObjectConstants.FIELD_LIFE_STATUS));
    });
    SfaOrderPaymentModel.EditArgNew editArg = new SfaOrderPaymentModel.EditArgNew();
    editArg.setDataMap(details);
    editArg.setPaymentId(paymentId);
    editArg.setApprovalType(ApprovalFlowTriggerType.UPDATE.getId());
    syncEditCustomerAccountInfo(editArg, context);
  }

  public void flowCompletedSyncAccountInfo(ActionContext context,
      StandardFlowCompletedAction.Arg arg, List<IObjectData> orderPaymentObjects) {
    ApprovalFlowTriggerType type = ApprovalFlowTriggerType.getType(arg.getTriggerType());
    List<String> orderPaymentIds = Lists.newArrayList();
    String dataId = arg.getDataId();
    IObjectData objectData = serviceFacade
        .findObjectDataIncludeDeleted(context.getUser(), dataId, context.getObjectApiName());
    if (objectData == null) {
      return;
    }
    if (type != ApprovalFlowTriggerType.INVALID) {
      orderPaymentObjects = serviceFacade.findDetailObjectDataListIgnoreFormula(objectData, context.getUser());
    }
    if (CollectionUtils.isNotEmpty(orderPaymentObjects)) {
      orderPaymentIds = orderPaymentObjects.stream().map(o -> o.getId())
          .collect(Collectors.toList());
    }
    SfaOrderPaymentModel.FlowCompleteArg flowCompleteArg = new SfaOrderPaymentModel.FlowCompleteArg();
    flowCompleteArg.setPaymentId(arg.getDataId());
    flowCompleteArg.setLifeStatus(objectData.get(CustomerPaymentObj.FIELD_LIFE_STATUS).toString());
    ServiceContext serviceContext = new ServiceContext(context.getRequestContext(), null, null);
    flowCompleteArg.setDataIds(orderPaymentIds);
    flowCompleteArg.setApprovalType(type.getId());
    log.debug("sfaOrderPaymentService->flowComplete arg:{}", flowCompleteArg);
    try {
      orderPaymentService.flowComplete(flowCompleteArg, serviceContext);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  public SfaOrderPaymentModel.BulkDeleteArg buildBulkDeleteSyncAccountInfoArg(ActionContext context,
      StandardBulkDeleteAction.Arg arg) {
    Map<String, List<String>> map = Maps.newHashMap();
    List<IObjectData> data = serviceFacade
        .findObjectDataByIdsIncludeDeleted(context.getUser(), arg.getIdList(),
            arg.getDescribeApiName());
    for (IObjectData x : data) {
      String cpId = (String) x.get(OrderPaymentObj.FIELD_PAYMENT_ID);
      if (StringUtils.isNotEmpty(cpId)) {
        List<String> orderIds = map.get(cpId);
        if (CollectionUtils.isEmpty(orderIds)) {
          orderIds = Lists.newArrayList();
        }
        String id = (String) x.get(OrderPaymentObj.FIELD_ID);
        orderIds.add(id);
        map.put(cpId, orderIds);
      }
    }
    SfaOrderPaymentModel.BulkDeleteArg deleteArg = new SfaOrderPaymentModel.BulkDeleteArg();
    deleteArg.setOrderPaymentMap(map);
    return deleteArg;
  }

  public void bulkDelete(SfaOrderPaymentModel.BulkDeleteArg deleteArg,
      ActionContext actionContext) {
    ServiceContext serviceContext = new ServiceContext(actionContext.getRequestContext(), null,
        null);
    log.debug("sfaOrderPaymentService->bulkDelete arg:{}", deleteArg);
    orderPaymentService.bulkDelete(deleteArg, serviceContext);
  }

  private boolean getCustomerAccountEnable(ActionContext context) {

    ServiceContext serviceContext = new ServiceContext(context.getRequestContext(), null, null);
    CustomerAccountType.IsCustomerAccountEnableResult customerAccount = customerAccountService
        .isCustomerAccountEnable(serviceContext);
    return customerAccount.isEnable();
  }

  private void syncAddCustomerAccountInfo(SfaOrderPaymentModel.CreateArg createArg,
      ActionContext context) {
    ServiceContext serviceContext = new ServiceContext(context.getRequestContext(), null, null);
    log.debug("sfaOrderPaymentService->create arg:{}", createArg);
    orderPaymentService.create(createArg, serviceContext);
  }

  private void syncEditCustomerAccountInfo(SfaOrderPaymentModel.EditArgNew editArg,
      ActionContext context) {
    ServiceContext serviceContext = new ServiceContext(context.getRequestContext(), null, null);
    log.debug("sfaOrderPaymentService->edit arg:{}", editArg);
    orderPaymentService.edit(editArg, serviceContext);
  }

  private ObjectDataDocument getPrepayData(IObjectData orderData, IObjectData paymentData) {
    ObjectDataDocument prepayData = new ObjectDataDocument();
    prepayData.put(CrmPackageObjectConstants.FIELD_RECORD_TYPE,
        PrepayDetailConstants.RecordType.OutcomeRecordType.apiName);
    prepayData.put(PrepayDetailConstants.Field.Customer.apiName,
        orderData.get(OrderPaymentObj.FIELD_ACCOUNT_ID).toString());
    prepayData.put(PrepayDetailConstants.Field.OutcomeType.apiName, "1");
    prepayData.put(PrepayDetailConstants.Field.TransactionTime.apiName,
        Long.valueOf(paymentData.get(CustomerPaymentObj.FIELD_PAYMENT_TIME).toString()));
    prepayData.put(PrepayDetailConstants.Field.Payment.apiName,
        paymentData.get(CustomerPaymentObj.FIELD_ID).toString());
    prepayData.put(CrmPackageObjectConstants.FIELD_LIFE_STATUS,
        paymentData.get(CustomerPaymentObj.FIELD_LIFE_STATUS).toString());
    return prepayData;
  }

  private ObjectDataDocument getRebateOutcomeData(IObjectData orderData, IObjectData paymentData) {
    ObjectDataDocument rebateData = new ObjectDataDocument();
    rebateData.put(RebateOutcomeDetailConstants.Field.Customer.apiName,
        orderData.get(OrderPaymentObj.FIELD_ACCOUNT_ID).toString());
    rebateData.put(RebateOutcomeDetailConstants.Field.TransactionTime.apiName,
        Long.valueOf(paymentData.get(CustomerPaymentObj.FIELD_PAYMENT_TIME).toString()));
    rebateData.put(RebateOutcomeDetailConstants.Field.Payment.apiName,
        paymentData.get(CustomerPaymentObj.FIELD_ID).toString());
    rebateData.put(CrmPackageObjectConstants.FIELD_LIFE_STATUS,
        paymentData.get(CustomerPaymentObj.FIELD_LIFE_STATUS).toString());
    return rebateData;
  }

  @POST
  @ServiceMethod("calculate_customers_payment_money")
  public Map<String, BigDecimal> calculateCustomersPaymentMoney(ServiceContext context,
      List<String> ids) {
    log.debug("calculateCustomersPaymentMoney ids:{}", ids);
    Map<String, BigDecimal> map = Maps.newHashMap();
    if (CollectionUtils.isEmpty(ids)) {
      return map;
    }
    ids.forEach(x -> map.put(x, BigDecimal.ZERO));
    SearchTemplateQuery query = new SearchTemplateQuery();
    query.addFilters(Lists.newArrayList(
        FieldUtils.buildFilter(CustomerPaymentObj.FIELD_ACCOUNT_ID, ids, Operator.IN, 0),
        FieldUtils.buildFilter(CustomerPaymentObj.FIELD_LIFE_STATUS,
            Lists.newArrayList("normal", "in_change"), Operator.IN, 0)));
    query.setLimit(100);
    query.setOffset(0);
    query.setPermissionType(0);
    while (true) {
      QueryResult<IObjectData> queryResult = serviceFacade
          .findBySearchQuery(context.getUser(), PaymentObject.CUSTOMER_PAYMENT.getApiName(), query);
      List<IObjectData> list = queryResult.getData();
      if (CollectionUtils.isEmpty(list)) {
        break;
      }
      list.forEach(x -> {
        Object o = x.get(CustomerPaymentObj.FIELD_PAYMENT_AMOUNT);
        if (o == null) {
          return;
        }
        BigDecimal amount = BigDecimal.valueOf(Double.valueOf(o.toString()));
        String accountId = x.get(CustomerPaymentObj.FIELD_ACCOUNT_ID, String.class);
        map.put(accountId, map.getOrDefault(accountId, BigDecimal.ZERO).add(amount));
      });
      query.setOffset(query.getOffset() + query.getLimit());
    }
    return map;
  }

  @POST
  @ServiceMethod("get_order_rebate_money")
  public Map<String, BigDecimal> getOrderRebateMoney(ServiceContext context,
                                                                 List<String> orderIds) {
    //获取订单下所有符合条件的回款明细
    SearchTemplateQuery query = new SearchTemplateQuery();
    List<IFilter> filters=Lists.newArrayList();
    filters.add(FieldUtils.buildFilter(OrderPaymentObj.FIELD_ORDER_ID,orderIds,Operator.IN,0));
    filters.add(
            FieldUtils.buildFilter(OrderPaymentObj.FIELD_PAYMENT_METHOD,Lists.newArrayList(
                    CustomerPaymentObj.PAYMENT_METHOD_DNR,
                    CustomerPaymentObj.PAYMENT_METHOD_REBATE
            ),Operator.IN, 0));
    filters.add(
            FieldUtils.buildFilter(OrderPaymentObj.FIELD_LIFE_STATUS,Lists.newArrayList(
                    UdobjConstants.LIFE_STATUS_VALUE_UNDER_REVIEW,
                    UdobjConstants.LIFE_STATUS_VALUE_NORMAL,
                    UdobjConstants.LIFE_STATUS_VALUE_IN_CHANGE
            ),Operator.IN, 0));
    query.addFilters(filters);
    query.setPermissionType(0);
    query.setLimit(100);
    query.setOffset(0);

    IActionContext newContext = new com.facishare.paas.metadata.api.action.ActionContext();
    newContext.setDoCalculate(false);
    newContext.setEnterpriseId(context.getTenantId());
    newContext.setUserId(context.getUser().getUserId());

    Map<String,String> orderPaymentIdOrderIdMap=Maps.newHashMap(); //回款明细id  订单id map关系
    Map<String,BigDecimal> orderPaymentIdRebateMap=Maps.newHashMap(); //回款明细id 返利金额
    Set<String> orderPaymentIdDnrSet=Sets.newHashSet();  //回款明细id

    while (true) {
      QueryResult< IObjectData > queryResult = serviceFacade.findBySearchQuery(newContext, PaymentObject.ORDER_PAYMENT.getApiName(), query);
      List< IObjectData > list = queryResult.getData();
      if (CollectionUtils.isEmpty(list)) {
        break;
      }
      for (IObjectData objectData : list) {
        Object method = objectData.get(OrderPaymentObj.FIELD_PAYMENT_METHOD);
        Object amountObj=objectData.get(CustomerPaymentObj.FIELD_PAYMENT_AMOUNT);
        if(amountObj!=null) {
          BigDecimal amount = BigDecimal.valueOf(Double.valueOf(amountObj.toString()));
          String orderId = objectData.get(CustomerPaymentObj.FIELD_ORDER_ID, String.class);
          String id = objectData.get(CustomerPaymentObj.FIELD_ID, String.class);
          orderPaymentIdOrderIdMap.put(id,orderId);
          //返利
          if(method!=null&&method.toString().equals(CustomerPaymentObj.PAYMENT_METHOD_REBATE)) {
            if(orderPaymentIdRebateMap.containsKey(id)){
              orderPaymentIdRebateMap.put(id,orderPaymentIdRebateMap.get(id).add(amount));
            }else{
              orderPaymentIdRebateMap.put(id,amount);
            }
          }
          //预存款+返利
          if(method!=null&&method.toString().equals(CustomerPaymentObj.PAYMENT_METHOD_DNR)) {
            orderPaymentIdDnrSet.add(id);
          }
        }
      }
      query.setOffset(query.getOffset() + query.getLimit());
    }

    //获取预存款+返利中的返利金额
    BatchGetRebateAmountModel.Arg arg=new BatchGetRebateAmountModel.Arg();
    arg.setOrderPaymentIds(Lists.newArrayList(orderPaymentIdDnrSet));
    BatchGetRebateAmountModel.Result batchResult = orderPaymentService.batchGetRebateAmountByOrderPaymentIds(arg, context);
    if(batchResult!=null&&batchResult.getOrderPaymentIdRebateAmountMap()!=null){
      orderPaymentIdRebateMap.putAll(batchResult.getOrderPaymentIdRebateAmountMap());
    }

    Map<String, BigDecimal> result=Maps.newHashMap();
    orderPaymentIdRebateMap.forEach((k,v)->{
      if(orderPaymentIdOrderIdMap.containsKey(k)){
        String orderId=orderPaymentIdOrderIdMap.get(k);
        if(result.containsKey(orderId)){
          result.put(orderId,v.add(result.get(orderId)));
        } else {
          result.put(orderId,v);
        }
      }
    });

    return result;
  }

  public void sendMessage(String messageType, Set<String> orderPaymentIds, String tenantId) {
    RocketMQMessageSender sender = SpringUtil.getContext()
        .getBean("confirmedPaymentMQSender", RocketMQMessageSender.class);
    CustomerPaymentObj.PaymentMessage message = new CustomerPaymentObj.PaymentMessage();
    message.setMessageType(messageType);
    message.setObjectIds(orderPaymentIds);
    message.setTenantId(tenantId);
    Schema<PaymentMessage> schema = RuntimeSchema
        .getSchema(CustomerPaymentObj.PaymentMessage.class);
    byte[] bytes = ProtobufIOUtil.toByteArray(message, schema, LinkedBuffer.allocate(256));
    sender.sendMessage(bytes);

  }

  public void bulkRecoverSyncAccountInfo(ActionContext actionContext,
      StandardBulkRecoverAction.Arg arg, StandardBulkRecoverAction.Result result) {
    if (!result.getSuccess()) {
      return;
    }
    ServiceContext serviceContext = new ServiceContext(actionContext.getRequestContext(), null,
        null);
    List<String> idList = arg.getIdList();
    Map<String, List<String>> map = Maps.newHashMap();
    idList.forEach(x -> {
      List<Map> maps = queryOrderPaymentList(serviceContext, x);
      List<String> ids = maps.stream().filter(d -> "false".equals(d.get("is_deleted").toString()))
          .map(y -> y.get(OrderPaymentObj.FIELD_ID).toString()).collect(Collectors.toList());
      map.put(x, ids);
    });
    SfaOrderPaymentModel.BulkRecoverArg bulkRecoverArg = new SfaOrderPaymentModel.BulkRecoverArg();
    bulkRecoverArg.setOrderPaymentMap(map);
    log.debug("sfaOrderPaymentService->bulkRecover arg:{}", bulkRecoverArg);
    try {
      orderPaymentService.bulkRecover(bulkRecoverArg, serviceContext);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  @POST
  @ServiceMethod("merge_customer_payment")
  @Transactional
  public boolean mergeCustomerPayment(Args.MergeCustomerPayment arg, ServiceContext context) {
    log.debug("mergeCustomerPayment arg:{}", arg);
    String sourceIds = "";
    for (String x : arg.getSourceCustomerId()) {
      sourceIds += String.format("'%s',", x);
    }
    sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
    String cSql = "UPDATE payment_customer SET account_id = '" + arg.getTargetCustomerId()
        + "' WHERE account_id IN (" + sourceIds + ")";
    String oSql = "UPDATE payment_order SET account_id = '" + arg.getTargetCustomerId()
        + "' WHERE account_id IN (" + sourceIds + ")";
    String pSql = "UPDATE payment_plan SET account_id = '" + arg.getTargetCustomerId()
        + "' WHERE account_id IN (" + sourceIds + ")";
    try {
      objectDataService
          .findBySql(cSql, context.getTenantId(), PaymentObject.CUSTOMER_PAYMENT.getApiName());
      objectDataService
          .findBySql(oSql, context.getTenantId(), PaymentObject.ORDER_PAYMENT.getApiName());
      objectDataService
          .findBySql(pSql, context.getTenantId(), PaymentObject.PAYMENT_PLAN.getApiName());
    } catch (MetadataServiceException e) {
      log.debug("mergeCustomerPayment fail:" + e.toString());
      return false;
    }
    return true;
  }

  public void invalidCustomerAccount(RequestContext requestContext,
      List<IObjectData> customerPayments) {
    log.debug("Syncing invalid payments: {}", customerPayments);
    if (CollectionUtils.isEmpty(customerPayments)) {
      return;
    }
    ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
    SfaOrderPaymentModel.BulkInvalidArg bulkArg = new SfaOrderPaymentModel.BulkInvalidArg();
    List<SfaOrderPaymentModel.InvalidArg> argList = new ArrayList<>();
    for (IObjectData cp : customerPayments) {
      SfaOrderPaymentModel.InvalidArg arg = new SfaOrderPaymentModel.InvalidArg();
      arg.setPaymentId(cp.getId());
      arg.setLifeStatus(cp.get(CustomerPaymentObj.FIELD_LIFE_STATUS).toString());
      List<IObjectData> orderPayments = findOrderPayments(requestContext.getUser(),
          cp.getId());
      arg.setDataIds(
          orderPayments.stream().map(d -> d.getId()).collect(Collectors.toList()));
      argList.add(arg);
    }
    bulkArg.setInvalidArgs(argList);
    log.debug("sfaOrderPaymentService->invalid arg: {}", bulkArg);
    orderPaymentService.bulkInvalid(bulkArg, serviceContext);
  }

  public void updateOrderPayment(IObjectData objectData, User user) {
    if (objectData == null) {
      return;
    }
    List<IObjectData> detailObjectDataList = serviceFacade
        .findDetailIncludeInvalidObjectDataListIgnoreFormula(objectData, user);
    if (CollectionUtils.isEmpty(detailObjectDataList)) {
      return;
    }
    String status = objectData.get(FIELD_LIFE_STATUS).toString();
    log.debug("updateOrderPaymentLifeStatus life_status: {}", status);
    log.debug("updateOrderPaymentLifeStatus objectData: {}", objectData);
    log.debug("updateOrderPaymentLifeStatus detailObjectDataList: {}", detailObjectDataList);
    serviceFacade.bulkUpdateObjectDataOneField(CrmPackageObjectConstants.FIELD_LIFE_STATUS,
        detailObjectDataList, status, user);

    //更新回款明细lookup的所有回款计划的状态
    Set<String> planIds = Sets.newHashSet();
    detailObjectDataList.forEach(x->{
        if(x.get(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID)!=null) {
            planIds.add(x.get(OrderPaymentObj.FIELD_PAYMENT_PLAN_ID).toString());
        }
    });
    List<IObjectData> playObjectDataList=serviceFacade.findObjectDataByIds(user.getTenantId(),new ArrayList<>(planIds),PaymentObject.PAYMENT_PLAN.getApiName());
    paymentPlanService.updatePaymentPlanStatus(playObjectDataList,user);
  }

  public List<ObjectDataDocument> parseOrderNames(User user, List<ObjectDataDocument> documents) {
    StringBuilder ids = new StringBuilder();
    documents.forEach(d -> ids.append(ConverterUtil.convert2String(d.get("order_id"))).append(","));
    List<String> idList = Arrays.stream(ids.toString().split(",")).distinct()
            .collect(Collectors.toList());
    try {
      Map<String, String> orderNames = new HashMap<>();
      List<INameCache> nameCaches = proxyService
              .findRecordName(ActionContextExt.of(user).getContext(), "SalesOrderObj", idList);
      nameCaches.forEach(n -> orderNames.put(n.getId(),
              org.apache.commons.lang.StringUtils.isNotBlank(n.getName()) ? n.getName()
                      : n.getId()));
      documents.forEach(d -> {
        String orderId = ConverterUtil.convert2String(d.get("order_id"));
        if (org.apache.commons.lang.StringUtils.isNotBlank(orderId)) {
          String updatedOrderId = Arrays.stream(orderId.split(","))
                  .map(o -> orderNames.getOrDefault(o, o)).collect(Collectors.joining(","));
          d.put("order_id", updatedOrderId);
        }
        d.put("order_data_id", orderId);
      });
      return documents;
    } catch (MetadataServiceException ex) {
      log.error(ex.getMessage(), ex);
      return documents;
    }
  }

  public List<ObjectDataDocument> parseDateTime(IObjectDescribe objectDescribe, List<ObjectDataDocument> documents) {
    Map<String, IFieldDescribe> fieldDescribeMap = ObjectDescribeExt.of(objectDescribe).getFieldDescribeMap();
    for (ObjectDataDocument dataDocument:documents){
      dataDocument.forEach((f,v)->{
        IFieldDescribe fieldDescribe = fieldDescribeMap.get(f);
        if(fieldDescribe!=null){
          if(fieldDescribe.getType().equals("date")||fieldDescribe.getType().equals("date_time")||fieldDescribe.getType().equals("time")){
            if (v!=null&&v.toString().equals("946656000000")){
              dataDocument.put(f,null);
            }
          }
          if(fieldDescribe.getType().equals("quote")){
            QuoteFieldDescribe describe=(QuoteFieldDescribe)fieldDescribe;
            if(describe.getQuoteFieldType().equals("date")||describe.getQuoteFieldType().equals("date_time")||describe.getQuoteFieldType().equals("time")){
              if (v!=null&&v.toString().equals("946656000000")){
                dataDocument.put(f,null);
              }
            }
          }
        }
      });
    }
    return documents;
  }

  public void deletePaymentByEditPayment(ActionContext actionContext, IObjectData data) {

    String method = (String) data.get(CustomerPaymentObj.FIELD_PAYMENT_METHOD);
    ArrayList<String> options = Lists.newArrayList(CustomerPaymentObj.PAYMENT_METHOD_DEPOSIT,
        CustomerPaymentObj.PAYMENT_METHOD_REBATE, CustomerPaymentObj.PAYMENT_METHOD_DNR);
    if (!options.contains(method)) {
      return;
    }

    List<IObjectData> deletedList = findOrderPaymentIsDeletedList(actionContext.getUser(),
        data.getId());
    log.debug("deletePaymentByEditPayment deletedList: {}", deletedList);
    if (CollectionUtils.isNotEmpty(deletedList)) {
      Map<String, List<String>> map = Maps.newHashMap();
      for (IObjectData x : deletedList) {
        String p = (String) x.get(OrderPaymentObj.FIELD_PAYMENT_ID);
        List<String> ids = map.get(p);
        if (CollectionUtils.isEmpty(ids)) {
          ids = Lists.newArrayList();
        }
        String id = (String) x.get(OrderPaymentObj.FIELD_ID);
        ids.add(id);
        map.put(p, ids);
      }
      SfaOrderPaymentModel.BulkDeleteArg deleteArg = new SfaOrderPaymentModel.BulkDeleteArg();
      deleteArg.setOrderPaymentMap(map);
      deleteArg.setApprovalType(ApprovalFlowTriggerType.UPDATE.getId());
      bulkDelete(deleteArg, actionContext);
    }
  }

  public void sendDHTMq(User user,String status,String paymentId){
    List<IObjectData> orderPayments = findOrderPayments(user,paymentId);
    if(orderPayments.isEmpty()){
      return;
    }
    RocketMQMessageSender sender = SpringUtil.getContext()
            .getBean("paymentDHTMQSender", RocketMQMessageSender.class);
    PaymentDhtMq dhtMq= PaymentDhtMq
            .builder()
            .status(status)
            .paymentOrderIds(orderPayments.stream().map(IObjectData::getId).collect(Collectors.toList()))
            .build();

    try {
      sender.sendMessage(JSON.toJSONBytes(dhtMq));
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
