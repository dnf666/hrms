package com.facishare.crm.customeraccount.predefine.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.action.PrepayDetailFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountConfigManager;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateUseRuleManager;
import com.facishare.crm.customeraccount.predefine.service.CommonService;
import com.facishare.crm.customeraccount.predefine.service.SfaOrderPaymentService;
import com.facishare.crm.customeraccount.predefine.service.dto.BatchGetRebateAmountModel;
import com.facishare.crm.customeraccount.predefine.service.dto.BulkInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateIncomeModle;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateUseRuleValidateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaOrderPaymentModel;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContext.RequestSource;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.appframework.core.predef.service.ObjectRecycleBinService;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2018/1/4.
 */
@Slf4j
@Component
public class SfaOrderPaymentServiceImpl extends CommonService implements SfaOrderPaymentService {
    @Autowired
    CustomerAccountManager customerAccountManager;
    @Autowired
    PrepayDetailManager prepayDetailManager;
    @Autowired
    RebateIncomeDetailManager rebateIncomeDetailManager;
    @Autowired
    RebateOutcomeDetailManager rebateOutcomeDetailManager;
    @Autowired
    ObjectRecycleBinService objectRecycleBinService;
    @Autowired
    private RebateUseRuleManager rebateUseRuleManager;
    @Autowired
    CustomerAccountConfigManager customerAccountConfigManager;

    @Override
    public SfaOrderPaymentModel.CreateResult create(SfaOrderPaymentModel.CreateArg arg, ServiceContext serviceContext) {

        if (!customerAccountConfigManager.isCustomerAccountEnable(serviceContext.getTenantId())) {
            log.debug("customer account not enable,for tenantId:{}", serviceContext.getTenantId());
            return new SfaOrderPaymentModel.CreateResult();
        }

        log.debug("createArg:{}", arg);
        User user = serviceContext.getUser();
        if (RequestUtil.isOutUser(user)) {
            //外部联系人调用替换成系统用户
            user = new User(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
            RequestContext newRequestContext = RequestContext.builder().requestSource(RequestSource.INNER).postId(serviceContext.getPostId()).tenantId(user.getTenantId()).user(Optional.of(user)).build();
            serviceContext = new ServiceContext(newRequestContext, serviceContext.getServiceMethod(), serviceContext.getServiceName());
        }
        SfaOrderPaymentModel.CreateResult result = new SfaOrderPaymentModel.CreateResult();
        Map<String, SfaOrderPaymentModel.CreateResultDetail> orderPaymentMapResult = Maps.newHashMap();

        Map<String, SfaOrderPaymentModel.CreateArgDetail> orderPaymentMap = arg.getOrderPaymentMap();
        for (Map.Entry<String, SfaOrderPaymentModel.CreateArgDetail> entry : orderPaymentMap.entrySet()) {
            Map<String, SfaOrderPaymentModel.CreateResultDetail> oneResult = createOneOrderPayment(entry.getKey(), entry.getValue().getPrepayDetailData(), entry.getValue().getRebateOutcomeDetailData(), serviceContext);
            orderPaymentMapResult.putAll(oneResult);
        }
        result.setOrderPaymentMap(orderPaymentMapResult);
        return result;
    }

    /**
     * 回款id修改成回款明细Id<br>
     * @param orderPaymentId
     * @param prepayObjectDocument
     * @param rebateOutcomeTotalDocument
     * @param serviceContext
     * @return
     */
    private Map<String, SfaOrderPaymentModel.CreateResultDetail> createOneOrderPayment(String orderPaymentId, ObjectDataDocument prepayObjectDocument, ObjectDataDocument rebateOutcomeTotalDocument, ServiceContext serviceContext) {
        log.debug("createOneOrderPayment====>prepayObjectData:{},rebateOutcomeTotalData:{}", prepayObjectDocument, rebateOutcomeTotalDocument);
        Map<String, SfaOrderPaymentModel.CreateResultDetail> result = Maps.newHashMap();
        User user = serviceContext.getUser();
        SfaOrderPaymentModel.CreateResultDetail resultDetail = new SfaOrderPaymentModel.CreateResultDetail();
        if (Objects.nonNull(prepayObjectDocument)) {
            IObjectData prepayObjectData = prepayObjectDocument.toObjectData();
            prepayObjectData.setDescribeApiName(PrepayDetailConstants.API_NAME);
            checkCreateData(prepayObjectData);
            prepayObjectData.set(SystemConstants.Field.RecordType.apiName, PrepayDetailConstants.RecordType.OutcomeRecordType.apiName);
            IObjectDescribe describe = serviceFacade.findObject(serviceContext.getTenantId(), PrepayDetailConstants.API_NAME);
            prepayObjectData.setDescribeId(describe.getId());
            ObjectDataDocument prepayDataResult = this.triggerAddAction(serviceContext, PrepayDetailConstants.API_NAME, ObjectDataDocument.of(prepayObjectData));
            resultDetail.setPrepayDetailData(prepayDataResult);
        }
        if (Objects.nonNull(rebateOutcomeTotalDocument)) {
            IObjectData rebateOutcomeTotalData = rebateOutcomeTotalDocument.toObjectData();
            //校验
            rebateOutcomeTotalData.setDescribeApiName(RebateOutcomeDetailConstants.API_NAME);
            checkCreateData(rebateOutcomeTotalData);
            BigDecimal totalAmountToPay = rebateOutcomeTotalData.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
            String customerId = ObjectDataUtil.getReferenceId(rebateOutcomeTotalData, "customer_id");
            //6.3
            Optional<IObjectData> rebateUseRuleObjectDataOption = rebateUseRuleManager.getRebateUseRuleByCustomerId(user, customerId);
            String rebateUseRuleId = null;
            if (rebateUseRuleObjectDataOption.isPresent()) {
                rebateUseRuleId = rebateUseRuleObjectDataOption.get().getId();
            }
            IObjectData customerAccountObj = customerAccountManager.getCustomerAccountByCustomerId(user, customerId);
            BigDecimal rebateAvailable = customerAccountObj.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class);
            if (rebateAvailable.compareTo(totalAmountToPay) < 0) {
                log.info("customerAccountObj info:{}", customerAccountObj);
                throw new ValidateException("返利可用余额不足");
            }
            //获取可用返利收入
            List<RebateIncomeModle.PayForOutcomeModel> incomeObjectDataListToPay = rebateIncomeDetailManager.obtainRebateIncomeToPayList(user, totalAmountToPay, customerId);
            List<ObjectDataDocument> rebateOutcomeDataResults = new ArrayList<>();
            //扣减返利收入和创建支出
            for (RebateIncomeModle.PayForOutcomeModel payForOutcome : incomeObjectDataListToPay) {
                IObjectData rebateOutcome = new ObjectData();
                rebateOutcome.setDescribeApiName(RebateOutcomeDetailConstants.API_NAME);
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.TransactionTime.apiName, rebateOutcomeTotalData.get(RebateOutcomeDetailConstants.Field.TransactionTime.apiName));
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, payForOutcome.getRebateIncomeObj().getId());
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.Amount.apiName, payForOutcome.getPayAmount());
                rebateOutcome.set(SystemConstants.Field.LifeStatus.apiName, rebateOutcomeTotalData.get(SystemConstants.Field.LifeStatus.apiName));
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.RebateUseRule.apiName, rebateUseRuleId);
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.OrderPayment.apiName, rebateOutcomeTotalData.get(RebateOutcomeDetailConstants.Field.OrderPayment.apiName));
                IObjectData resultData = rebateOutcomeDetailManager.createRebateOutcomeAndUpdateBalance(user, rebateOutcome);
                rebateOutcomeDataResults.add(ObjectDataDocument.of(resultData));
            }
            resultDetail.setRebateOutcomeDetailDatas(rebateOutcomeDataResults);
        }
        result.put(orderPaymentId, resultDetail);
        return result;
    }

    private void checkCreateData(IObjectData objectData) {
        log.debug("checkCreateData->objectData:{}", objectData);
        String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        String customerId = ObjectDataUtil.getReferenceId(objectData, "customer_id");
        String paymentId = ObjectDataUtil.getReferenceId(objectData, "payment_id");
        String refundId = ObjectDataUtil.getReferenceId(objectData, "refund_id");
        String incomeType = objectData.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
        String outcomeType = objectData.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
        String errorMessage = "";
        if (StringUtils.isEmpty(lifeStatus)) {
            errorMessage += "LifeStatus不能为空 ";
        }
        if (PrepayDetailConstants.API_NAME.equals(objectData.getDescribeApiName())) {
            if (StringUtils.isNotEmpty(outcomeType)) {
                if (StringUtils.isEmpty(paymentId)) {
                    errorMessage += "PaymentId不能为空 ";
                }
            } else if (StringUtils.isNotEmpty(incomeType)) {
                if (StringUtils.isEmpty(refundId)) {
                    errorMessage += "RefundId不能为空 ";
                }
            }
        } else if (RebateOutcomeDetailConstants.API_NAME.equals(objectData.getDescribeApiName())) {
            if (StringUtils.isEmpty(paymentId)) {
                errorMessage += "PaymentId不能为空 ";
            }
        }
        if (StringUtils.isEmpty(customerId)) {
            errorMessage += "CustomerId不能为空";
        }
        if (!errorMessage.isEmpty()) {
            throw new ValidateException(errorMessage);
        }
    }

    @Override
    public SfaOrderPaymentModel.Result flowComplete(SfaOrderPaymentModel.FlowCompleteArg arg, ServiceContext serviceContext) {
        if (!customerAccountConfigManager.isCustomerAccountEnable(serviceContext.getTenantId())) {
            log.debug("customer account not enable,for tenantId:{}", serviceContext.getTenantId());
            return new SfaOrderPaymentModel.Result();
        }

        SfaOrderPaymentModel.Result result = new SfaOrderPaymentModel.Result();
        List<String> orderPaymentIds = arg.getDataIds();
        String lifeStatus = arg.getLifeStatus();
        ApprovalFlowTriggerType approvalType = getApprovalFlowTriggerTypeInstance(arg.getApprovalType());
        if (approvalType == null) {
            throw new ValidateException(String.format("ApprovalType[%s]无效", approvalType));
        }
        if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus) || SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
            result.setSuccess(true);
            log.info("nochange flowComplete, arg={}", arg);
            return result;
        }
        orderPaymentIds.forEach(orderPaymentId -> handleOneOrderPaymentFlowComplete(serviceContext, orderPaymentId, lifeStatus, approvalType));
        result.setSuccess(true);
        return result;
    }

    private boolean handleOneOrderPaymentFlowComplete(ServiceContext serviceContext, String orderPaymentId, String lifeStatus, ApprovalFlowTriggerType approvalType) {
        boolean hasRelateData = false;
        IObjectData prepayObjectData = prepayDetailManager.getByOrderPaymentId(serviceContext.getUser(), orderPaymentId);
        if (Objects.nonNull(prepayObjectData)) {
            hasRelateData = true;
            prepayFlowComplete(serviceContext, lifeStatus, approvalType, prepayObjectData);
        }
        List<IObjectData> rebateOutcomeObjectDatas = rebateOutcomeDetailManager.getByOrderPaymentId(serviceContext.getUser(), orderPaymentId);
        if (CollectionUtils.isNotEmpty(rebateOutcomeObjectDatas)) {
            hasRelateData = true;
            for (IObjectData outcome : rebateOutcomeObjectDatas) {
                String incomeId = outcome.get(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, String.class);
                String outcomeId = outcome.getId();
                BigDecimal amount = outcome.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
                String oldOutcomeLifeStatus = outcome.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                outcome.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                rebateOutcomeDetailManager.update(serviceContext.getUser(), outcome);
                if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                    serviceFacade.invalid(outcome, serviceContext.getUser());
                }
                rebateOutcomeDetailManager.updateBalanceForOutcome(serviceContext.getUser(), incomeId, outcomeId, amount, oldOutcomeLifeStatus, lifeStatus);
            }
        }
        if (!hasRelateData) {
            log.warn("OrderPaymentFlowComplete:{},lifeStatus:{},approvalType:{}", String.format("回款明细{%s}没有关联的交易明细", orderPaymentId), lifeStatus, approvalType);
        }
        return hasRelateData;
    }

    private boolean prepayFlowComplete(ServiceContext serviceContext, String lifeStatus, ApprovalFlowTriggerType approvalType, IObjectData prepayObjectData) {
        String passStatus = PrepayDetailFlowCompletedAction.Arg.PASS;
        ApprovalFlowTriggerType approvalFlowTriggerType = null;
        if (ApprovalFlowTriggerType.CREATE == approvalType) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                passStatus = "notpass";
            } else if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                return true;
            }
            approvalFlowTriggerType = ApprovalFlowTriggerType.CREATE;
        } else if (ApprovalFlowTriggerType.INVALID == approvalType) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                //对未生效的数据作废，进入审批，被驳回或者撤回
                passStatus = "notpass";
            } else if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                return true;
            } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                passStatus = "notpass";
            }
            approvalFlowTriggerType = ApprovalFlowTriggerType.INVALID;
        } else if (ApprovalFlowTriggerType.UPDATE == approvalType) {
            approvalFlowTriggerType = ApprovalFlowTriggerType.UPDATE;
        }
        PrepayDetailFlowCompletedAction.Arg prepayDetailFlowCompletedActionArg = new PrepayDetailFlowCompletedAction.Arg();
        prepayDetailFlowCompletedActionArg.setTriggerType(approvalFlowTriggerType.getTriggerTypeCode());
        prepayDetailFlowCompletedActionArg.setDescribeApiName(PrepayDetailConstants.API_NAME);
        prepayDetailFlowCompletedActionArg.setDataId(prepayObjectData.getId());
        prepayDetailFlowCompletedActionArg.setTenantId(serviceContext.getTenantId());
        prepayDetailFlowCompletedActionArg.setUserId(serviceContext.getUser().getUserId());
        prepayDetailFlowCompletedActionArg.setStatus(passStatus);
        PrepayDetailFlowCompletedAction.Result flowResult = this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, "FlowCompleted", prepayDetailFlowCompletedActionArg);
        log.debug("PaymentFlowComplete->PrepayLifeStatus Update,ApprovalType:{},dateId:{},Result:{}", approvalFlowTriggerType.getTriggerTypeName(), prepayObjectData.getId(), flowResult.getSuccess());
        return flowResult.getSuccess();
    }

    @Override
    public SfaOrderPaymentModel.Result invalid(SfaOrderPaymentModel.InvalidArg arg, ServiceContext serviceContext) {

        if (!customerAccountConfigManager.isCustomerAccountEnable(serviceContext.getTenantId())) {
            log.debug("customer account not enable,for tenantId:{}", serviceContext.getTenantId());
            return new SfaOrderPaymentModel.Result();
        }

        String lifeStatus = arg.getLifeStatus();
        if (Objects.isNull(lifeStatus)) {
            throw new ValidateException("While Invalid prepay id is null");
        }
        List<String> orderPaymentIds = arg.getDataIds();
        orderPaymentIds.stream().forEach(orderPaymentId -> {
            invalidOneOrderPayment(serviceContext, lifeStatus, orderPaymentId);
        });
        SfaOrderPaymentModel.Result invalidResult = new SfaOrderPaymentModel.Result();
        invalidResult.setSuccess(true);
        return invalidResult;
    }

    private void invalidOneOrderPayment(ServiceContext serviceContext, String lifeStatus, String orderPaymentId) {
        boolean hasRelateData = false;
        String id;
        IObjectData prepayData = prepayDetailManager.getByOrderPaymentId(serviceContext.getUser(), orderPaymentId);
        if (Objects.nonNull(prepayData)) {
            hasRelateData = true;
            id = prepayData.getId();
            Map<String, Object> params = Maps.newHashMap();
            params.put("lifeStatus", lifeStatus);
            ObjectDataDocument result = this.triggerInvalidAction(serviceContext, PrepayDetailConstants.API_NAME, id, params);
            log.debug("Invalid PrepayDetail paymentId:{},id:{},Result:{}", orderPaymentId, id, JsonUtil.toJson(result));
        }
        List<IObjectData> rebateOutcomes = rebateOutcomeDetailManager.getByOrderPaymentId(serviceContext.getUser(), orderPaymentId);
        if (CollectionUtils.isNotEmpty(rebateOutcomes)) {
            hasRelateData = true;
            log.debug("bulkInvalid RebateOutcomes paymentId:{},rebateOutconmes:{}", orderPaymentId, rebateOutcomes);
            rebateOutcomeDetailManager.bulkInvalid(serviceContext.getUser(), rebateOutcomes, lifeStatus);
        }
        if (!hasRelateData) {
            log.warn("无关联数据,orderPaymentId:{},lifeStatus:{}", orderPaymentId, lifeStatus);
        }
    }

    @Override
    public SfaOrderPaymentModel.Result bulkInvalid(SfaOrderPaymentModel.BulkInvalidArg arg, ServiceContext serviceContext) {

        if (!customerAccountConfigManager.isCustomerAccountEnable(serviceContext.getTenantId())) {
            log.debug("customer account not enable,for tenantId:{}", serviceContext.getTenantId());
            return new SfaOrderPaymentModel.Result();
        }

        SfaOrderPaymentModel.Result result = new SfaOrderPaymentModel.Result();
        //预存款作废

        List<SfaOrderPaymentModel.InvalidArg> invalidArgs = arg.getInvalidArgs();
        invalidArgs.forEach(invalidArg -> {
            invalidAccordPayment(invalidArg, serviceContext);
        });
        result.setSuccess(true);
        return result;
    }

    /**
     * 作废一个回款下的预存款和返利<br>
     * @param arg
     * @param serviceContext
     */
    private void invalidAccordPayment(SfaOrderPaymentModel.InvalidArg arg, ServiceContext serviceContext) {
        List<String> orderPaymentIds = arg.getDataIds();
        BulkInvalidModel.Arg prepayBulkInvalidArg = new BulkInvalidModel.Arg();
        List<BulkInvalidModel.InvalidArg> prepayInvalidArgList = Lists.newArrayList();
        boolean hasRelateData = false;
        List<IObjectData> prepayDetailList = prepayDetailManager.listByOrderPaymentIds(serviceContext.getUser(), orderPaymentIds);
        if (CollectionUtils.isNotEmpty(prepayDetailList)) {
            hasRelateData = true;
            prepayDetailList.stream().forEach(prepay -> {
                BulkInvalidModel.InvalidArg invalidArg = new BulkInvalidModel.InvalidArg();
                invalidArg.setId(prepay.getId());
                invalidArg.setLifeStatus(arg.getLifeStatus());
                invalidArg.setObjectDescribeApiName(PrepayDetailConstants.API_NAME);
                prepayInvalidArgList.add(invalidArg);
            });
            prepayBulkInvalidArg.setDataList(prepayInvalidArgList);
            log.debug("BulkInvalid PrepayDetail Arg:{},SfaArg:{}", JsonUtil.toJson(prepayBulkInvalidArg), JsonUtil.toJson(arg));
            List<ObjectDataDocument> prepayResult = bulkInvalidPrepayDetail(prepayBulkInvalidArg, serviceContext);
            log.debug("BulkInvalid PrepayDetail,Arg:{},Result:{}", JsonUtil.toJson(prepayBulkInvalidArg), JsonUtil.toJson(prepayResult));
        }
        //返利支出作废
        List<IObjectData> rebateOutcomeDetailList = rebateOutcomeDetailManager.listByOrderPaymentIds(serviceContext.getUser(), orderPaymentIds);
        if (CollectionUtils.isNotEmpty(rebateOutcomeDetailList)) {
            hasRelateData = true;
            rebateOutcomeDetailManager.bulkInvalid(serviceContext.getUser(), rebateOutcomeDetailList, arg.getLifeStatus());
        }
        if (!hasRelateData) {
            log.warn("无关联数据,arg:{}", arg);
        }
    }

    private List<ObjectDataDocument> bulkInvalidPrepayDetail(BulkInvalidModel.Arg bulkInvalidArg, ServiceContext serviceContext) {
        Map<String, String> lifeStatusMap = bulkInvalidArg.getDataList().stream().collect(Collectors.toMap(BulkInvalidModel.InvalidArg::getId, BulkInvalidModel.InvalidArg::getLifeStatus));
        StandardBulkInvalidAction.Arg arg = new StandardBulkInvalidAction.Arg();
        arg.setJson(JsonUtil.toJson(bulkInvalidArg));
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataIdLifeStatusMap", lifeStatusMap);
        StandardBulkInvalidAction.Result result = this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.BulkInvalid.name(), arg, params);
        return result.getObjectDataList();
    }

    @Override
    public SfaOrderPaymentModel.Result bulkRecover(SfaOrderPaymentModel.BulkRecoverArg arg, ServiceContext serviceContext) {

        if (!customerAccountConfigManager.isCustomerAccountEnable(serviceContext.getTenantId())) {
            log.debug("customer account not enable,for tenantId:{}", serviceContext.getTenantId());
            return new SfaOrderPaymentModel.Result();
        }

        Map<String, List<String>> orderPaymentMap = arg.getOrderPaymentMap();

        List<String> orderPaymentIds = new ArrayList<String>();
        for (Map.Entry<String, List<String>> entry : orderPaymentMap.entrySet()) {
            orderPaymentIds.addAll(entry.getValue());
        }

        List<IObjectData> prepayDatas = prepayDetailManager.listInvalidDataByOrderPaymentIds(serviceContext.getUser(), orderPaymentIds);
        if (CollectionUtils.isNotEmpty(prepayDatas)) {
            List<String> prepayIds = prepayDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            StandardBulkRecoverAction.Arg prepayRecoverArg = new StandardBulkRecoverAction.Arg();
            prepayRecoverArg.setIdList(prepayIds);
            prepayRecoverArg.setObjectDescribeAPIName(PrepayDetailConstants.API_NAME);
            this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.BulkRecover.name(), prepayRecoverArg);
        }

        List<IObjectData> rebateOutcomeDatas = rebateOutcomeDetailManager.listInvalidDataByOrderPaymentIds(serviceContext.getUser(), orderPaymentIds);
        if (CollectionUtils.isNotEmpty(rebateOutcomeDatas)) {
            List<String> rebateOutcomeIds = rebateOutcomeDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            StandardBulkRecoverAction.Arg rebateOutcomeRecoverArg = new StandardBulkRecoverAction.Arg();
            rebateOutcomeRecoverArg.setIdList(rebateOutcomeIds);
            rebateOutcomeRecoverArg.setObjectDescribeAPIName(RebateOutcomeDetailConstants.API_NAME);
            this.triggerAction(serviceContext, RebateOutcomeDetailConstants.API_NAME, StandardAction.BulkRecover.name(), rebateOutcomeRecoverArg);
        }
        SfaOrderPaymentModel.Result result = new SfaOrderPaymentModel.Result();
        result.setSuccess(true);
        return result;
    }

    @Override
    public SfaOrderPaymentModel.Result bulkDelete(SfaOrderPaymentModel.BulkDeleteArg arg, ServiceContext serviceContext) {
        if (!customerAccountConfigManager.isCustomerAccountEnable(serviceContext.getTenantId())) {
            log.debug("customer account not enable,for tenantId:{}", serviceContext.getTenantId());
            return new SfaOrderPaymentModel.Result();
        }
        SfaOrderPaymentModel.Result result = new SfaOrderPaymentModel.Result();
        result.setSuccess(true);
        //需要增加编辑的参数，因为编辑回款引起的删除对余额会有变更。
        Map<String, List<String>> orderPaymentMap = arg.getOrderPaymentMap();
        List<String> orderPaymentIds = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : orderPaymentMap.entrySet()) {
            orderPaymentIds.addAll(entry.getValue());
        }

        boolean deleteByUpdate = ApprovalFlowTriggerType.UPDATE.getId().equals(arg.getApprovalType());
        List<IObjectData> prepayDatas = prepayDetailManager.listInvalidDataByOrderPaymentIds(serviceContext.getUser(), orderPaymentIds);
        if (CollectionUtils.isNotEmpty(prepayDatas)) {
            if (deleteByUpdate) {
                serviceFacade.bulkInvalidAndDeleteWithSuperPrivilege(prepayDatas, serviceContext.getUser());
                /**
                 * 编辑回款，会直接删除下面对应的回款明细。此时需要也应该变更余额。 
                 */
                for (IObjectData prepayData : prepayDatas) {
                    try {
                        log.debug("after delete prepaydate and then update balance,for prepaydata;{}", prepayData);
                        String oldLifeStatus = prepayData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                        prepayDetailManager.updateBalanceWhenDeleteByOrderPayment(serviceContext.getUser(), prepayData, oldLifeStatus);
                    } catch (Exception e) {
                        log.error("exception error when updateBalance,for prepayDataId:{}", prepayData.getId());
                    }
                }
            } else {
                List<String> prepayIds = prepayDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
                com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction.Arg actionArg = new com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction.Arg();
                actionArg.setIdList(prepayIds);
                actionArg.setDescribeApiName(PrepayDetailConstants.API_NAME);
                this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.BulkDelete.name(), actionArg);
            }
        }
        List<IObjectData> rebateOutcomeDatas = rebateOutcomeDetailManager.listInvalidDataByOrderPaymentIds(serviceContext.getUser(), orderPaymentIds);
        if (CollectionUtils.isNotEmpty(rebateOutcomeDatas)) {
            if (deleteByUpdate) {
                serviceFacade.bulkInvalidAndDeleteWithSuperPrivilege(rebateOutcomeDatas, serviceContext.getUser());
                /**
                 * 编辑回款，会直接删除下面对应的回款明细。此时需要也应该变更余额。
                 */
                for (IObjectData rebatedata : rebateOutcomeDatas) {
                    try {
                        log.debug("after delete rebatedata and then update balance,for rebatedata;{}", rebatedata);
                        String oldLifeStatus = rebatedata.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                        String incomeId = rebatedata.get(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, String.class);
                        String outcomeId = rebatedata.getId();
                        BigDecimal amount = rebatedata.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
                        rebateOutcomeDetailManager.updateBalanceForOutcomeWhenOrderPaymentDelete(serviceContext.getUser(), incomeId, outcomeId, amount, oldLifeStatus);
                    } catch (Exception e) {
                        log.error("exception error when updateBalance,for prepayDataId:{}", rebatedata.getId());
                    }
                }
            } else {
                List<String> rebateOutcomeIds = rebateOutcomeDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
                com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction.Arg actionArg = new com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction.Arg();
                actionArg.setDescribeApiName(RebateOutcomeDetailConstants.API_NAME);
                actionArg.setIdList(rebateOutcomeIds);
                this.triggerAction(serviceContext, RebateOutcomeDetailConstants.API_NAME, StandardAction.BulkDelete.name(), actionArg);
            }
        }

        return result;
    }

    /**
     * 1.编辑有 有审批流和无审批流的情况<br>
     * 无审批流的情况：由于回款页面只能够删除回款明细，对于这种情况直接调用 bulkdelete接口。 orderPayment 由老状态变成了 删除状态<br>
     * 对于有审批流的情况：
     * 1）.先调用edit接口，此时明细的状态为inchange状态。<br>
     * 2).如果编辑审批流回调（回款编辑确认，对于删除的回款明细需要调用bulkdelete接口），此时回款明细就由inchange->变成了delete状态。
     * 
     * @param arg
     * @param serviceContext
     * @return
     */
    @Override
    public SfaOrderPaymentModel.Result edit(SfaOrderPaymentModel.EditArgNew arg, ServiceContext serviceContext) {

        if (!customerAccountConfigManager.isCustomerAccountEnable(serviceContext.getTenantId())) {
            log.debug("customer account not enable,for tenantId:{}", serviceContext.getTenantId());
            return new SfaOrderPaymentModel.Result();
        }

        SfaOrderPaymentModel.Result result = new SfaOrderPaymentModel.Result();

        Map<String, String> orderPaymentMap = arg.getDataMap();

        ApprovalFlowTriggerType approvalType = getApprovalFlowTriggerTypeInstance(arg.getApprovalType());
        if (approvalType == null) {
            throw new ValidateException(String.format("ApprovalType[%s]无效", approvalType));
        }

        if (approvalType.getId().equals(ApprovalFlowTriggerType.UPDATE.getId())) {
            log.debug("SfaOrderPaymentServiceImpl.edit.arg:{}", arg);
            for (Map.Entry<String, String> entry : orderPaymentMap.entrySet()) {
                handleOneOrderPaymentEdit(serviceContext, entry.getValue(), entry.getKey());
            }
        }
        return result;
    }

    /**
     * 编辑回款 会触发 删除回款明细 如果有审批流那么传过来的状态为inchange。<br>
     * 编辑回款删除回款明细   无审批流和 审批确认两种方式都是调用bulkDeleted()接口<br>
     *  对于不动的明细：
     *  a).未生效 -> 审批中<br>
     *  b).  status1 -> status1 状态不变不进行处理<br>
     * @param serviceContext
     * @param lifeStatus
     * @param orderPaymentId
     */
    private void handleOneOrderPaymentEdit(ServiceContext serviceContext, String lifeStatus, String orderPaymentId) {

        IObjectData prepayObj = prepayDetailManager.getByOrderPaymentId(serviceContext.getUser(), orderPaymentId);

        String prepayOldLifeStatus = prepayObj.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.debug("begin edit prepayDetail,the prepayObj:{}", prepayObj);

        if (null != prepayObj) {
            prepayObj.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
            if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                //underReview说明有审批流需要锁定<br>
                prepayObj.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
            }

            if (!prepayOldLifeStatus.equals(lifeStatus)) {
                IObjectData editResultObj = serviceFacade.updateObjectData(serviceContext.getUser(), prepayObj);
                //更新预付款余额<br>
                log.debug("after edit  prepaydate and then update balance,for prepaydata;{}", editResultObj);

                prepayDetailManager.updateBalance(serviceContext.getUser(), prepayObj, prepayOldLifeStatus);
                log.debug("after edit prepayDetail,the rebate editResultObj:{},prepayOldLifestatus:{},newLifestatus:{}", editResultObj, prepayOldLifeStatus, lifeStatus);
            }

        }

        List<IObjectData> rebateObjList = rebateOutcomeDetailManager.getByOrderPaymentId(serviceContext.getUser(), orderPaymentId);
        if (CollectionUtils.isNotEmpty(rebateObjList)) {
            for (IObjectData rebateObj : rebateObjList) {
                String rebateOldLifeStatus = rebateObj.get(SystemConstants.Field.LifeStatus.apiName, String.class);

                if (!lifeStatus.equals(rebateOldLifeStatus)) {
                    log.debug("begin edit rebateDetailObj,,the rebateObj:{}", rebateObj);
                    rebateObj.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);

                    if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                        rebateObj.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
                    }
                    IObjectData updateResultObj = rebateOutcomeDetailManager.editRebateOutcomeAndUpdateBalanceFromSfa(serviceContext.getUser(), rebateObj, rebateOldLifeStatus);
                    log.debug("after edit rebateDetailObj,,the rebate updateResultObj:{},for rebateOldLifeStatus:{},newLifestatus:{}", updateResultObj, rebateOldLifeStatus, lifeStatus);
                }

            }
        }
    }

    @Override
    public SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdResult getRelativeNamesByOrderPaymentId(SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdArg arg, ServiceContext serviceContext) {
        IObjectData prepayObjectData = prepayDetailManager.getByOrderPaymentId(serviceContext.getUser(), arg.getOrderPaymentId());
        List<IObjectData> rebateOutcomeObjectDataList = rebateOutcomeDetailManager.listByOrderPaymentIds(serviceContext.getUser(), Lists.newArrayList(arg.getOrderPaymentId()));
        SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdResult result = new SfaOrderPaymentModel.GetRelativeNameByOrderPaymentIdResult();
        if (prepayObjectData != null) {
            result.setPrepayName(prepayObjectData.get(PrepayDetailConstants.Field.Name.apiName, String.class));
        }
        List<String> rebateNames = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rebateOutcomeObjectDataList)) {
            rebateOutcomeObjectDataList.forEach(data -> {
                rebateNames.add(data.get(RebateOutcomeDetailConstants.Field.Name.apiName, String.class));
            });
            result.setRebateOutcomeNames(rebateNames);
        }
        return result;
    }

    @Override
    public SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdResult getOrderPaymentCostByOrderPaymentId(SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdArg arg, ServiceContext serviceContext) {
        IObjectData prepayObjectData = prepayDetailManager.getByOrderPaymentId(serviceContext.getUser(), arg.getOrderPaymentId());
        List<IObjectData> rebateOutcomeObjectDataList = rebateOutcomeDetailManager.listByOrderPaymentIds(serviceContext.getUser(), Lists.newArrayList(arg.getOrderPaymentId()));
        SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdResult result = new SfaOrderPaymentModel.GetOrderPaymentCostByOrderPaymentIdResult();
        if (prepayObjectData != null) {
            result.setPrepayAmount(prepayObjectData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class));
        }
        BigDecimal rebateAmounts = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(rebateOutcomeObjectDataList)) {
            for (IObjectData data : rebateOutcomeObjectDataList) {
                rebateAmounts = rebateAmounts.add(data.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class));
            }
            result.setRebateOutcomeAmount(rebateAmounts);
        }
        return result;
    }

    @Override
    public RebateUseRuleValidateModel.Result validateRebateUseRule(RebateUseRuleValidateModel.Arg arg, ServiceContext serviceContext) {
        RebateUseRuleValidateModel.Result result = new RebateUseRuleValidateModel.Result();
        Map<String, IObjectDescribe> objectDescribeMap = serviceFacade.findObjects(serviceContext.getTenantId(), Lists.newArrayList(RebateUseRuleConstants.API_NAME));
        if (!objectDescribeMap.containsKey(RebateUseRuleConstants.API_NAME)) {
            Map<String, RebateUseRuleValidateModel.RebateUseRuleValidateResult> resultMap = arg.getOrderIdRebateAmountMap().keySet().stream().collect(Collectors.toMap(orderId -> orderId, x -> {
                RebateUseRuleValidateModel.RebateUseRuleValidateResult temp = new RebateUseRuleValidateModel.RebateUseRuleValidateResult();
                temp.setCanUseRebate(true);
                return temp;
            }));
            result.setOrderIdValidateResultMap(resultMap);
            return result;
        }
        Map<String, RebateUseRuleValidateModel.RebateUseRuleValidateResult> orderValidateResultMap = rebateUseRuleManager.validate(serviceContext.getUser(), arg.getCustomerId(), arg.getOrderIdRebateAmountMap());
        result.setOrderIdValidateResultMap(orderValidateResultMap);
        return result;
    }

    @Override
    public BatchGetRebateAmountModel.Result batchGetRebateAmountByOrderPaymentIds(BatchGetRebateAmountModel.Arg arg, ServiceContext serviceContext) {
        BatchGetRebateAmountModel.Result result = new BatchGetRebateAmountModel.Result();
        List<String> orderPaymentIds = arg.getOrderPaymentIds();
        if (CollectionUtils.isEmpty(orderPaymentIds)) {
            result.setOrderPaymentIdRebateAmountMap(Maps.newHashMap());
            return result;
        }
        List<IObjectData> rebateOutcomeObjectData = rebateOutcomeDetailManager.listInvalidDataByOrderPaymentIds(serviceContext.getUser(), orderPaymentIds);
        Map<String, BigDecimal> orderPaymentIdAmountMap = rebateOutcomeObjectData.stream().collect(Collectors.toMap(objectData -> objectData.get(RebateOutcomeDetailConstants.Field.OrderPayment.apiName, String.class), data -> ObjectDataUtil.getBigDecimal(data, RebateOutcomeDetailConstants.Field.Amount.apiName)));
        result.setOrderPaymentIdRebateAmountMap(orderPaymentIdAmountMap);
        return result;
    }
}
