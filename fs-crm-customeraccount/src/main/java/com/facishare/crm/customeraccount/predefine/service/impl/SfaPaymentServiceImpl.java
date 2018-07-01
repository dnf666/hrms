package com.facishare.crm.customeraccount.predefine.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.action.PrepayDetailFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.predefine.service.CommonService;
import com.facishare.crm.customeraccount.predefine.service.SfaPaymentService;
import com.facishare.crm.customeraccount.predefine.service.dto.BulkInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateIncomeModle;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaBulkInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaCreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaEditModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaFlowCompleteModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaPaymentDeleteModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaPaymentRecoverModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaRelativeModel;
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

@Slf4j
@Component
public class SfaPaymentServiceImpl extends CommonService implements SfaPaymentService {
    @Autowired
    private PrepayDetailManager prepayDetailManager;
    @Autowired
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    @Autowired
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    @Autowired
    private CustomerAccountManager customerAccountManager;
    @Autowired
    private ObjectRecycleBinService objectRecycleBinService;

    @Override
    public SfaCreateModel.Result create(SfaCreateModel.Arg arg, ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        if (RequestUtil.isOutUser(user)) {
            //外部联系人调用替换成系统用户
            user = new User(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
            RequestContext newRequestContext = RequestContext.builder().requestSource(RequestSource.INNER).postId(serviceContext.getPostId()).tenantId(user.getTenantId()).user(Optional.of(user)).build();
            serviceContext = new ServiceContext(newRequestContext, serviceContext.getServiceMethod(), serviceContext.getServiceName());
        }
        SfaCreateModel.Result result = new SfaCreateModel.Result();
        ObjectDataDocument prepayDataResult = null;
        List<ObjectDataDocument> rebateOutcomeDataResults = Lists.newArrayList();
        IObjectData prepayObjectData = arg.getPrepayDetailData() != null ? arg.getPrepayDetailData().toObjectData() : null;
        IObjectData rebateOutcomeTotalData = arg.getRebateOutcomeDetailData() != null ? arg.getRebateOutcomeDetailData().toObjectData() : null;
        if (Objects.nonNull(prepayObjectData)) {
            prepayObjectData.setDescribeApiName(PrepayDetailConstants.API_NAME);
            String recordType = prepayObjectData.get(SystemConstants.Field.RecordType.apiName, String.class);
            if (!PrepayDetailConstants.RecordType.IncomeRecordType.apiName.equals(recordType)) {
                //兼容订货通 创建预存款
                checkCreateData(prepayObjectData);
                prepayObjectData.set(SystemConstants.Field.RecordType.apiName, PrepayDetailConstants.RecordType.OutcomeRecordType.apiName);
            }
            IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), PrepayDetailConstants.API_NAME);
            prepayObjectData.setDescribeId(describe.getId());
            prepayDataResult = this.triggerAddAction(serviceContext, PrepayDetailConstants.API_NAME, ObjectDataDocument.of(prepayObjectData));
        }
        if (Objects.nonNull(rebateOutcomeTotalData)) {
            //校验
            rebateOutcomeTotalData.setDescribeApiName(RebateOutcomeDetailConstants.API_NAME);
            checkCreateData(rebateOutcomeTotalData);
            BigDecimal totalAmountToPay = rebateOutcomeTotalData.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
            String customerId = ObjectDataUtil.getReferenceId(rebateOutcomeTotalData, "customer_id");
            IObjectData customerAccountObj = customerAccountManager.getCustomerAccountByCustomerId(user, customerId);
            BigDecimal rebateAvailable = customerAccountObj.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class);
            if (rebateAvailable.compareTo(totalAmountToPay) == -1) {
                log.info("customerAccountObj info:{}", customerAccountObj);
                throw new ValidateException("返利可用余额不足");
            }
            //获取可用返利收入
            List<RebateIncomeModle.PayForOutcomeModel> incomeObjectDataListToPay = rebateIncomeDetailManager.obtainRebateIncomeToPayList(user, totalAmountToPay, customerId);
            log.debug("incomeObjectDataListToPay:{}", incomeObjectDataListToPay);
            //扣减返利收入和创建支出
            for (RebateIncomeModle.PayForOutcomeModel payForOutcome : incomeObjectDataListToPay) {
                IObjectData rebateOutcome = new ObjectData();
                rebateOutcome.setDescribeApiName(RebateOutcomeDetailConstants.API_NAME);
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.TransactionTime.apiName, rebateOutcomeTotalData.get(RebateOutcomeDetailConstants.Field.TransactionTime.apiName));
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, payForOutcome.getRebateIncomeObj().getId());
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.Amount.apiName, payForOutcome.getPayAmount());
                rebateOutcome.set(SystemConstants.Field.LifeStatus.apiName, rebateOutcomeTotalData.get(SystemConstants.Field.LifeStatus.apiName));
                rebateOutcome.set(RebateOutcomeDetailConstants.Field.Payment.apiName, rebateOutcomeTotalData.get(RebateOutcomeDetailConstants.Field.Payment.apiName));
                IObjectData resultData = rebateOutcomeDetailManager.createRebateOutcomeAndUpdateBalance(user, rebateOutcome);
                rebateOutcomeDataResults.add(ObjectDataDocument.of(resultData));
            }
        }
        result.setPrepayDetailData(prepayDataResult);
        result.setRebateOutcomeDetailDatas(rebateOutcomeDataResults);
        return result;
    }

    /**
     * 1.通过回款分别查询出预付款、返利明细。<br>
     * 2.分别变更其状态。<br>
     * @param arg
     * @param serviceContext
     * @return
     */
    @Override
    public SfaEditModel.Result edit(SfaEditModel.Arg arg, ServiceContext serviceContext) {
        SfaEditModel.Result result = new SfaEditModel.Result();
        String paymentId = arg.getDataId();
        IObjectData prepayObj = prepayDetailManager.getByPaymentId(serviceContext.getUser(), paymentId);
        String lifeStatus = arg.getLifeStatus();
        prepayObj.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);

        //编辑触发审批流只会传underReview过来
        if (!SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
            log.warn("wrong payment life status,trigger by payment,for paymentId:{}", arg.getDataId());
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.WRONG_LIEF_STATUS_WHEN_EDIT, CustomerAccountErrorCode.WRONG_LIEF_STATUS_WHEN_EDIT.getMessage());
        }

        //TODO  临时把edit逻辑写到flowComplete()
        return null;
    }

    @Override
    public SfaRelativeModel.Result getRelativeNamesByPaymentId(SfaRelativeModel.Arg arg, ServiceContext serviceContext) {
        IObjectData objectData = prepayDetailManager.getByPaymentId(serviceContext.getUser(), arg.getPaymentId());
        List<IObjectData> rebateOutcomeObjectDataList = rebateOutcomeDetailManager.listByPaymentIds(serviceContext.getUser(), Lists.newArrayList(arg.getPaymentId()));
        SfaRelativeModel.Result result = new SfaRelativeModel.Result();
        if (objectData != null) {
            result.setPrepayName(objectData.get(PrepayDetailConstants.Field.Name.apiName, String.class));
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

    private void checkCreateData(IObjectData objectData) {
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
    public SfaFlowCompleteModel.Result flowComplete(SfaFlowCompleteModel.Arg arg, ServiceContext serviceContext) {
        SfaFlowCompleteModel.Result result = new SfaFlowCompleteModel.Result();
        String paymentId = arg.getDataId();
        String lifeStatus = arg.getLifeStatus();
        ApprovalFlowTriggerType approvalType = getApprovalFlowTriggerTypeInstance(arg.getApprovalType());
        if (approvalType == null) {
            throw new ValidateException(String.format("ApprovalType[%s]无效", approvalType));
        }

        /**
         * 编辑时重新触发的审批流<br>
         */
        if (approvalType.getId().equals(ApprovalFlowTriggerType.UPDATE.getId())) {
            //有审批流传underReview过来，没有审批流传normal过来<br>
            if (!SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus) && !SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                log.warn("wrong payment life status,trigger by payment,for arg:{}", arg);
                throw new CustomerAccountBusinessException(CustomerAccountErrorCode.WRONG_LIEF_STATUS_WHEN_EDIT, CustomerAccountErrorCode.WRONG_LIEF_STATUS_WHEN_EDIT.getMessage());
            }
            handlerPaymentEdit(arg, serviceContext);
            result.setSuccess(true);
            return result;
        }

        if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus) || SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
            result.setSuccess(true);
            log.info("nochange flowComplete, arg={}", arg);
            return result;
        }
        boolean hasRelateData = false;
        IObjectData prepayObjectData = prepayDetailManager.getByPaymentId(serviceContext.getUser(), paymentId);
        if (Objects.nonNull(prepayObjectData)) {
            hasRelateData = true;
            prepayFlowComplete(serviceContext, lifeStatus, approvalType, prepayObjectData);
        }
        List<IObjectData> rebateOutcomeObjectDatas = rebateOutcomeDetailManager.getByPaymentId(serviceContext.getUser(), paymentId);
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
            log.warn("无关联数据");
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 处理回款编辑过来的动作<br>
     * 场景：对回款进行编辑时会触发审批流<br>
     * @param arg
     * @param serviceContext
    
     */
    private void handlerPaymentEdit(SfaFlowCompleteModel.Arg arg, ServiceContext serviceContext) {
        String paymentId = arg.getDataId();
        String lifeStatus = arg.getLifeStatus();

        IObjectData prepayObj = prepayDetailManager.getByPaymentId(serviceContext.getUser(), paymentId);

        log.debug("begin edit prepayDetail,the prepayObj:{}", prepayObj);
        if (null != prepayObj) {
            prepayObj.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
            if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                //underReview说明有审批流需要锁定<br>
                prepayObj.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
            }

            IObjectData editResultObj = serviceFacade.updateObjectData(serviceContext.getUser(), prepayObj);
            //更新预付款余额<br>
            prepayDetailManager.updateBalance(serviceContext.getUser(), prepayObj, SystemConstants.LifeStatus.Ineffective.value);
            log.debug("after edit prepayDetail,the rebate editResultObj:{}", editResultObj);
        }

        List<IObjectData> rebateObjList = rebateOutcomeDetailManager.getByPaymentId(serviceContext.getUser(), paymentId);
        if (CollectionUtils.isNotEmpty(rebateObjList)) {
            for (IObjectData rebateObj : rebateObjList) {
                String oldLifeStatus = rebateObj.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                log.debug("begin edit rebateDetailObj,,the rebateObj:{}", rebateObj);
                rebateObj.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                    rebateObj.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
                }
                IObjectData updateResultObj = rebateOutcomeDetailManager.editRebateOutcomeAndUpdateBalanceFromSfa(serviceContext.getUser(), rebateObj, oldLifeStatus);
                log.debug("after edit rebateDetailObj,,the rebate updateResultObj:{}", updateResultObj);
            }
        }
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
    public SfaInvalidModel.Result invalid(SfaInvalidModel.Arg sfaInvalidArg, ServiceContext serviceContext) {
        String lifeStatus = sfaInvalidArg.getLifeStatus();
        if (Objects.isNull(lifeStatus)) {
            throw new ValidateException("While Invalid prepay id is null");
        }
        String paymentId = sfaInvalidArg.getDataId();
        String id;
        boolean hasRelateData = false;
        IObjectData prepayData = prepayDetailManager.getByPaymentId(serviceContext.getUser(), paymentId);
        if (Objects.nonNull(prepayData)) {
            hasRelateData = true;
            id = prepayData.getId();
            Map<String, Object> params = Maps.newHashMap();
            params.put("lifeStatus", lifeStatus);
            ObjectDataDocument result = this.triggerInvalidAction(serviceContext, PrepayDetailConstants.API_NAME, id, params);
            log.debug("Invalid PrepayDetail paymentId:{},id:{},Result:{}", paymentId, id, JsonUtil.toJson(result));
        }
        List<IObjectData> rebateOutcomes = rebateOutcomeDetailManager.getByPaymentId(serviceContext.getUser(), paymentId);
        if (CollectionUtils.isNotEmpty(rebateOutcomes)) {
            hasRelateData = true;
            log.debug("bulkInvalid RebateOutcomes paymentId:{},rebateOutconmes:{}", paymentId, rebateOutcomes);
            rebateOutcomeDetailManager.bulkInvalid(serviceContext.getUser(), rebateOutcomes, lifeStatus);
        }
        if (!hasRelateData) {
            throw new ValidateException("无关联的预存款或返利支出");
        }
        SfaInvalidModel.Result invalidResult = new SfaInvalidModel.Result();
        return invalidResult;
    }

    @Override
    public SfaBulkInvalidModel.Result bulkInvalid(SfaBulkInvalidModel.Arg arg, ServiceContext serviceContext) {
        SfaBulkInvalidModel.Result result = new SfaBulkInvalidModel.Result();
        //预存款作废
        BulkInvalidModel.Arg prepayBulkInvalidArg = new BulkInvalidModel.Arg();
        List<BulkInvalidModel.InvalidArg> prepayInvalidArgList = Lists.newArrayList();
        List<String> paymentIds = arg.getDataIds();
        boolean hasRelateData = false;
        List<IObjectData> prepayDetailList = prepayDetailManager.listByOrderPaymentIds(serviceContext.getUser(), paymentIds);
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
        List<IObjectData> rebateOutcomeDetailList = rebateOutcomeDetailManager.listByPaymentIds(serviceContext.getUser(), paymentIds);
        if (CollectionUtils.isNotEmpty(rebateOutcomeDetailList)) {
            hasRelateData = true;
            rebateOutcomeDetailManager.bulkInvalid(serviceContext.getUser(), rebateOutcomeDetailList, arg.getLifeStatus());
        }
        if (!hasRelateData) {
            log.warn("无关联数据,arg:{}", arg);
        }
        return result;
    }

    @Override
    public SfaPaymentRecoverModel.Result bulkRecover(SfaPaymentRecoverModel.Arg arg, ServiceContext serviceContext) {
        List<String> paymentIds = arg.getPaymentIds();
        List<IObjectData> prepayDatas = prepayDetailManager.listInvalidDataByPaymentIds(serviceContext.getUser(), paymentIds);
        if (CollectionUtils.isNotEmpty(prepayDatas)) {
            List<String> prepayIds = prepayDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            StandardBulkRecoverAction.Arg prepayRecoverArg = new StandardBulkRecoverAction.Arg();
            prepayRecoverArg.setIdList(prepayIds);
            prepayRecoverArg.setObjectDescribeAPIName(PrepayDetailConstants.API_NAME);
            this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.BulkRecover.name(), prepayRecoverArg);
        }
        List<IObjectData> rebateOutcomeDatas = rebateOutcomeDetailManager.listInvalidDataByPaymentIds(serviceContext.getUser(), paymentIds);
        if (CollectionUtils.isNotEmpty(rebateOutcomeDatas)) {
            List<String> rebateOutcomeIds = rebateOutcomeDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            StandardBulkRecoverAction.Arg rebateOutcomeRecoverArg = new StandardBulkRecoverAction.Arg();
            rebateOutcomeRecoverArg.setIdList(rebateOutcomeIds);
            rebateOutcomeRecoverArg.setObjectDescribeAPIName(RebateOutcomeDetailConstants.API_NAME);
            this.triggerAction(serviceContext, RebateOutcomeDetailConstants.API_NAME, StandardAction.BulkRecover.name(), rebateOutcomeRecoverArg);
        }
        SfaPaymentRecoverModel.Result result = new SfaPaymentRecoverModel.Result();
        return result;
    }

    @Override
    public SfaPaymentDeleteModel.Result bulkDelete(SfaPaymentDeleteModel.Arg arg, ServiceContext serviceContext) {
        List<String> paymentIds = arg.getPaymentIds();
        List<IObjectData> prepayDatas = prepayDetailManager.listInvalidDataByPaymentIds(serviceContext.getUser(), paymentIds);
        if (CollectionUtils.isNotEmpty(prepayDatas)) {
            List<String> prepayIds = prepayDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg prepayRecycleDeleteArg = new com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg();
            prepayRecycleDeleteArg.setIdList(prepayIds);
            prepayRecycleDeleteArg.setObjectDescribeAPIName(PrepayDetailConstants.API_NAME);
            objectRecycleBinService.bulkDeleteData(prepayRecycleDeleteArg, serviceContext);
        }
        List<IObjectData> rebateOutcomeDatas = rebateOutcomeDetailManager.listInvalidDataByPaymentIds(serviceContext.getUser(), paymentIds);
        if (CollectionUtils.isNotEmpty(rebateOutcomeDatas)) {
            List<String> rebateOutcomeIds = rebateOutcomeDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg rebateOutcomeRecycleDeleteArg = new com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg();
            rebateOutcomeRecycleDeleteArg.setIdList(rebateOutcomeIds);
            rebateOutcomeRecycleDeleteArg.setObjectDescribeAPIName(RebateOutcomeDetailConstants.API_NAME);
            objectRecycleBinService.bulkDeleteData(rebateOutcomeRecycleDeleteArg, serviceContext);
        }
        SfaPaymentDeleteModel.Result result = new SfaPaymentDeleteModel.Result();
        return result;
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
}
