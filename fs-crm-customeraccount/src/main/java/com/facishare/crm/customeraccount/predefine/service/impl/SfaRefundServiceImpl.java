package com.facishare.crm.customeraccount.predefine.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.RebateIncomeTypeEnum;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.action.PrepayDetailFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.action.RebateIncomeDetailBulkInvalidAction;
import com.facishare.crm.customeraccount.predefine.action.RebateIncomeDetailFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.service.CommonService;
import com.facishare.crm.customeraccount.predefine.service.SfaRefundService;
import com.facishare.crm.customeraccount.predefine.service.dto.BulkInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaBulkInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaEditModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaFlowCompleteModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaInvalidModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaRefundCreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaRefundDeleteModel;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaRefundRecoverModel;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.core.predef.service.ObjectRecycleBinService;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SfaRefundServiceImpl extends CommonService implements SfaRefundService {
    @Autowired
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    @Autowired
    private PrepayDetailManager prepayDetailManager;
    @Autowired
    private ObjectRecycleBinService objectRecycleBinService;

    @Override
    public SfaFlowCompleteModel.Result flowComplete(SfaFlowCompleteModel.Arg arg, ServiceContext serviceContext) {
        SfaFlowCompleteModel.Result result = new SfaFlowCompleteModel.Result();
        String refundId = arg.getDataId();
        String approvalType = arg.getApprovalType();
        String lifeStatus = arg.getLifeStatus();
        ApprovalFlowTriggerType approvalFlowTriggerType = getApprovalFlowTriggerTypeInstance(approvalType);

        /**
         * 编辑时重新触发的审批流<br>
         */
        if (approvalType.equals(ApprovalFlowTriggerType.UPDATE.getId())) {
            handleRefundEdit(arg, serviceContext);

            result.setSuccess(true);
            return result;
        }

        if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus) || SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
            result.setSuccess(true);
            log.info("nochange flowComplete, arg={}", arg);
            return result;
        }
        IObjectData prepayObjectData = prepayDetailManager.getByRefundId(serviceContext.getUser(), refundId);
        if (Objects.nonNull(prepayObjectData)) {
            prepayFlowComplete(serviceContext, lifeStatus, approvalFlowTriggerType, prepayObjectData);
        } else {
            IObjectData rebateIncomeObjectData = rebateIncomeDetailManager.getByRefundId(serviceContext.getUser(), refundId);
            if (Objects.nonNull(rebateIncomeObjectData)) {
                rebateIncomeFlowComplete(rebateIncomeObjectData.getId(), lifeStatus, approvalFlowTriggerType, serviceContext);
            } else {
                log.warn("无关联数据,arg:{}", arg);
            }
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 编辑退款，退款状态变更了需要同步变更明细<br>
     * 注意事项：编辑退款不允许该退款方式，所以不会出现新建一条明细情况.<br>
     * @param arg
     * @param serviceContext
     */
    private void handleRefundEdit(SfaFlowCompleteModel.Arg arg, ServiceContext serviceContext) {
        String refundId = arg.getDataId();
        String lifeStatus = arg.getLifeStatus();
        //编辑触发审批流只会传underReview过来
        if (!SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus) && !SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
            log.warn("wrong payment life status,trigger by payment,for arg:{}", arg);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.WRONG_LIEF_STATUS_WHEN_EDIT, CustomerAccountErrorCode.WRONG_LIEF_STATUS_WHEN_EDIT.getMessage());
        }

        IObjectData prepayObj = prepayDetailManager.getByRefundId(serviceContext.getUser(), refundId);
        if (null != prepayObj) {
            log.debug("begin edit prepayObj,prepayObj:{}", prepayObj);
            prepayObj.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
            if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                prepayObj.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
            }
            IObjectData editResultObj = serviceFacade.updateObjectData(serviceContext.getUser(), prepayObj);
            //更新预付款余额<br>
            prepayDetailManager.updateBalance(serviceContext.getUser(), prepayObj, SystemConstants.LifeStatus.Ineffective.value);
            log.debug("after edit,the editResultObj:{}", editResultObj);
            //退款要么退款到预付款要么返利，所以这里直接返回。
            return;
        }

        IObjectData rebateIncomeObj = rebateIncomeDetailManager.getByRefundId(serviceContext.getUser(), refundId);

        if (null != rebateIncomeObj) {
            log.debug("begin edit rebateObj,rebateObj:{}", rebateIncomeObj);
            rebateIncomeObj.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
            if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                rebateIncomeObj.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.Locked.value);
            }
            //更新返利收入
            IObjectData updatedRebateIncomeObj = serviceFacade.updateObjectData(serviceContext.getUser(), rebateIncomeObj);

            Date startTime = updatedRebateIncomeObj.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
            Date endTime = updatedRebateIncomeObj.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
            if (ObjectDataUtil.isCurrentTimeActive(startTime, endTime)) {
                rebateIncomeDetailManager.updateBalanceForLifeStatus(serviceContext.getUser(), updatedRebateIncomeObj, SystemConstants.LifeStatus.Ineffective.value, lifeStatus);
            }
            log.debug("after edit rebateObj,the editResultObj:{}", updatedRebateIncomeObj);
        }
    }

    @Override
    public SfaInvalidModel.Result invalid(SfaInvalidModel.Arg sfaInvalidArg, ServiceContext serviceContext) {
        String refundId = sfaInvalidArg.getDataId();
        String lifeStatus = sfaInvalidArg.getLifeStatus();
        String objectApiName = null;
        String id = null;
        IObjectData prepayDetailData = prepayDetailManager.getByRefundId(serviceContext.getUser(), refundId);
        if (Objects.nonNull(prepayDetailData)) {
            id = prepayDetailData.getId();
            objectApiName = PrepayDetailConstants.API_NAME;
        } else {
            IObjectData rebateIncomeData = rebateIncomeDetailManager.getByRefundId(serviceContext.getUser(), refundId);
            if (Objects.isNull(rebateIncomeData)) {
                throw new ValidateException("无关联的预存款或返利收入");
            }
            if (Objects.isNull(lifeStatus)) {
                throw new ValidateException("LifeStatus不能为空");
            }
            Set<String> lifeStatusValues = Sets.newHashSet();
            for (SystemConstants.LifeStatus lifeStatusEnum : SystemConstants.LifeStatus.values()) {
                lifeStatusValues.add(lifeStatusEnum.value);
            }
            if (!lifeStatusValues.contains(lifeStatus)) {
                throw new ValidateException(String.format("LifeStatus[{%s}]不存在", lifeStatus));
            }
            id = rebateIncomeData.getId();
            objectApiName = RebateIncomeDetailConstants.API_NAME;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("lifeStatus", lifeStatus);
        ObjectDataDocument result = this.triggerInvalidAction(serviceContext, objectApiName, id, params);
        log.debug("Invalid PrepayDetail refundId:{},id:{},Result:{}", refundId, prepayDetailData.getId(), JsonUtil.toJson(result));
        return new SfaInvalidModel.Result();
    }

    @Override
    public SfaEditModel.Result edit(SfaEditModel.Arg arg, ServiceContext serviceContext) {
        return null;
    }

    @Override
    public SfaBulkInvalidModel.Result bulkInvalid(SfaBulkInvalidModel.Arg arg, ServiceContext serviceContext) {
        SfaBulkInvalidModel.Result result = new SfaBulkInvalidModel.Result();
        boolean hasRealateData = false;
        //预存款作废
        List<IObjectData> prepayDetailList = prepayDetailManager.listByRefundIds(serviceContext.getUser(), arg.getDataIds());
        Set<String> relateWithRebateIncomePaymentIds = Sets.newHashSet(arg.getDataIds());
        List<BulkInvalidModel.InvalidArg> bulkInvalidArgs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(prepayDetailList)) {
            hasRealateData = true;
            prepayDetailList.stream().forEach(prepay -> {
                BulkInvalidModel.InvalidArg invalidArg = new BulkInvalidModel.InvalidArg();
                invalidArg.setId(prepay.getId());
                invalidArg.setLifeStatus(arg.getLifeStatus());
                invalidArg.setObjectDescribeApiName(PrepayDetailConstants.API_NAME);
                bulkInvalidArgs.add(invalidArg);
                String orderPaymentId = ObjectDataUtil.getReferenceId(prepay, PrepayDetailConstants.Field.OrderPayment.apiName);
                relateWithRebateIncomePaymentIds.remove(orderPaymentId);
            });
            BulkInvalidModel.Arg prepayBulkInvalidArg = new BulkInvalidModel.Arg();
            prepayBulkInvalidArg.setDataList(bulkInvalidArgs);
            List<ObjectDataDocument> prepayResult = bulkInvalidPrepayDetail(prepayBulkInvalidArg, serviceContext);
            log.debug("BulkInvalid PrepayDetail,Arg:{},Result:{}", JsonUtil.toJson(prepayBulkInvalidArg), JsonUtil.toJson(prepayResult));
        }
        if (CollectionUtils.isNotEmpty(relateWithRebateIncomePaymentIds)) {
            log.debug("BulkInvalid Refund,To BulkInvalid RebateIncomeIds'PaymentIds:{}", relateWithRebateIncomePaymentIds);
            hasRealateData = true;
            //返利收入作废
            BulkInvalidModel.Arg rebateOutcomeBulkInvalidArg = new BulkInvalidModel.Arg();
            List<BulkInvalidModel.InvalidArg> rebateOutcomeInvalidArgList = Lists.newArrayList();
            List<IObjectData> rebateIncomeDetailList = rebateIncomeDetailManager.listByRefundIds(serviceContext.getUser(), Lists.newArrayList(relateWithRebateIncomePaymentIds));
            if (CollectionUtils.isNotEmpty(rebateIncomeDetailList)) {
                rebateIncomeDetailList.forEach(rebateIncome -> {
                    BulkInvalidModel.InvalidArg invalidArg = new BulkInvalidModel.InvalidArg();
                    invalidArg.setId(rebateIncome.getId());
                    invalidArg.setLifeStatus(arg.getLifeStatus());
                    invalidArg.setObjectDescribeApiName(RebateIncomeDetailConstants.API_NAME);
                    rebateOutcomeInvalidArgList.add(invalidArg);
                });
                rebateOutcomeBulkInvalidArg.setDataList(rebateOutcomeInvalidArgList);
                List<ObjectDataDocument> rebateIncomeResult = bulkInvalidRebateIncomeDetail(rebateOutcomeBulkInvalidArg, serviceContext);
                log.debug("BulkiInvalid RebateOutcome,Arg:{},Result:{}", JsonUtil.toJson(rebateOutcomeBulkInvalidArg), JsonUtil.toJson(rebateIncomeResult));
            }
        }
        if (!hasRealateData) {
            log.warn("无关联数据,arg:{}", arg);
        }
        return result;
    }

    @Override
    public SfaRefundRecoverModel.Result bulkRecover(SfaRefundRecoverModel.Arg arg, ServiceContext serviceContext) {
        List<String> refundIds = arg.getRefundIds();
        List<IObjectData> prepayDatas = prepayDetailManager.listInvalidDataByRefundIds(serviceContext.getUser(), refundIds);
        if (CollectionUtils.isNotEmpty(prepayDatas)) {
            List<String> prepayIds = prepayDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            StandardBulkRecoverAction.Arg prepayRecoverArg = new StandardBulkRecoverAction.Arg();
            prepayRecoverArg.setObjectDescribeAPIName(PrepayDetailConstants.API_NAME);
            prepayRecoverArg.setIdList(prepayIds);
            this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.BulkRecover.name(), prepayRecoverArg);
        } else {
            List<IObjectData> rebateIncomeDatas = rebateIncomeDetailManager.listInvalidDataByRefundIds(serviceContext.getUser(), refundIds);
            if (CollectionUtils.isNotEmpty(rebateIncomeDatas)) {
                List<String> rebateIncomeIds = rebateIncomeDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
                StandardBulkRecoverAction.Arg rebateIncomeRecoverArg = new StandardBulkRecoverAction.Arg();
                rebateIncomeRecoverArg.setIdList(rebateIncomeIds);
                rebateIncomeRecoverArg.setObjectDescribeAPIName(RebateIncomeDetailConstants.API_NAME);
                this.triggerAction(serviceContext, RebateIncomeDetailConstants.API_NAME, StandardAction.BulkRecover.name(), rebateIncomeRecoverArg);
            }
        }
        SfaRefundRecoverModel.Result result = new SfaRefundRecoverModel.Result();
        return result;
    }

    @Override
    public SfaRefundDeleteModel.Result bulkDelete(SfaRefundDeleteModel.Arg arg, ServiceContext serviceContext) {
        List<String> refuendIds = arg.getRefundIds();
        List<IObjectData> prepayDatas = prepayDetailManager.listInvalidDataByRefundIds(serviceContext.getUser(), refuendIds);
        if (CollectionUtils.isNotEmpty(prepayDatas)) {
            List<String> prepayIds = prepayDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg prepayRecycleDeleteArg = new com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg();
            prepayRecycleDeleteArg.setIdList(prepayIds);
            prepayRecycleDeleteArg.setObjectDescribeAPIName(PrepayDetailConstants.API_NAME);
            objectRecycleBinService.bulkDeleteData(prepayRecycleDeleteArg, serviceContext);
        } else {
            List<IObjectData> rebateIncomeDatas = rebateIncomeDetailManager.listInvalidDataByRefundIds(serviceContext.getUser(), refuendIds);
            if (CollectionUtils.isNotEmpty(rebateIncomeDatas)) {
                List<String> rebateIncomeIds = rebateIncomeDatas.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
                com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg rebateIncomeRecycleDeleteArg = new com.facishare.paas.appframework.core.predef.service.dto.recycleBin.BulkDeleteData.Arg();
                rebateIncomeRecycleDeleteArg.setObjectDescribeAPIName(RebateIncomeDetailConstants.API_NAME);
                rebateIncomeRecycleDeleteArg.setIdList(rebateIncomeIds);
                objectRecycleBinService.bulkDeleteData(rebateIncomeRecycleDeleteArg, serviceContext);
            }
        }
        SfaRefundDeleteModel.Result result = new SfaRefundDeleteModel.Result();
        return result;
    }

    private boolean rebateIncomeFlowComplete(String rebateIncomeId, String lifeStatus, ApprovalFlowTriggerType approvalFlowTriggerType, ServiceContext serviceContext) {
        String passStatus = RebateIncomeDetailFlowCompletedAction.Arg.PASS;
        ApprovalFlowTriggerType approvalFlowTriggerType1 = null;
        if (ApprovalFlowTriggerType.CREATE == approvalFlowTriggerType) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                passStatus = "notPass";
            } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                passStatus = "pass";
            }
            approvalFlowTriggerType1 = ApprovalFlowTriggerType.CREATE;
        } else if (ApprovalFlowTriggerType.INVALID == approvalFlowTriggerType) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                passStatus = "notPass";
            } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                passStatus = "pass";
            } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                passStatus = "notPass";
            }
            approvalFlowTriggerType1 = ApprovalFlowTriggerType.INVALID;
        } else {
            throw new ValidateException(String.format("ApprovalType[%s]无效", approvalFlowTriggerType));
        }
        StandardFlowCompletedAction.Arg arg1 = new StandardFlowCompletedAction.Arg();
        arg1.setDataId(rebateIncomeId);
        arg1.setDescribeApiName(RebateIncomeDetailConstants.API_NAME);
        arg1.setTenantId(serviceContext.getTenantId());
        arg1.setUserId(serviceContext.getUser().getUserId());
        arg1.setTriggerType(approvalFlowTriggerType1.getTriggerTypeCode());
        arg1.setStatus(passStatus);
        StandardFlowCompletedAction.Result flowResult = this.triggerAction(serviceContext, RebateIncomeDetailConstants.API_NAME, "FlowCompleted", arg1);
        log.debug("SfaRefund FlowComplete->RebateIncome Update,RebateIncomdeId:{},approvalType:{},paas:{},result:{}", rebateIncomeId, approvalFlowTriggerType.name(), passStatus, flowResult.getSuccess());
        return flowResult.getSuccess();
    }

    private boolean prepayFlowComplete(ServiceContext serviceContext, String lifeStatus, ApprovalFlowTriggerType approvalType, IObjectData prepayObjectData) {
        String passStatus = PrepayDetailFlowCompletedAction.Arg.PASS;
        ApprovalFlowTriggerType approvalFlowTriggerType = null;
        if (ApprovalFlowTriggerType.CREATE == approvalType) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                passStatus = "notpass";
            }
            approvalFlowTriggerType = ApprovalFlowTriggerType.CREATE;
        } else if (ApprovalFlowTriggerType.INVALID == approvalType) {
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                //对未生效的数据作废，进入审批，被驳回或者撤回
                passStatus = "notpass";
            } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {

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

    private List<ObjectDataDocument> bulkInvalidPrepayDetail(BulkInvalidModel.Arg bulkInvalidArg, ServiceContext serviceContext) {
        Map<String, String> lifeStatusMap = bulkInvalidArg.getDataList().stream().collect(Collectors.toMap(BulkInvalidModel.InvalidArg::getId, BulkInvalidModel.InvalidArg::getLifeStatus));
        StandardBulkInvalidAction.Arg arg = new StandardBulkInvalidAction.Arg();
        arg.setJson(JsonUtil.toJson(bulkInvalidArg));
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataIdLifeStatusMap", lifeStatusMap);
        StandardBulkInvalidAction.Result result = this.triggerAction(serviceContext, PrepayDetailConstants.API_NAME, StandardAction.BulkInvalid.name(), arg, params);
        return result.getObjectDataList();
    }

    private List<ObjectDataDocument> bulkInvalidRebateIncomeDetail(BulkInvalidModel.Arg bulkInvalidRebateArg, ServiceContext serviceContext) {
        RebateIncomeDetailBulkInvalidAction.Arg arg = new RebateIncomeDetailBulkInvalidAction.Arg();
        arg.setJson(JsonUtil.toJson(bulkInvalidRebateArg));
        Map<String, String> lifeStatusMap = bulkInvalidRebateArg.getDataList().stream().collect(Collectors.toMap(BulkInvalidModel.InvalidArg::getId, BulkInvalidModel.InvalidArg::getLifeStatus));
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataIdLifeStatusMap", lifeStatusMap);
        StandardBulkInvalidAction.Result result = this.triggerAction(serviceContext, RebateIncomeDetailConstants.API_NAME, StandardAction.BulkInvalid.name(), arg, params);
        return result.getObjectDataList();
    }

    @Override
    public SfaRefundCreateModel.Result create(SfaRefundCreateModel.Arg arg, ServiceContext serviceContext) {
        SfaRefundCreateModel.Result result = new SfaRefundCreateModel.Result();
        ObjectDataDocument prepayObjectData = arg.getPrepayDetailData();
        ObjectDataDocument rebateIncomeData = arg.getRebateIncomeDetailData();
        if (Objects.nonNull(prepayObjectData)) {
            checkCreateData(prepayObjectData);
            prepayObjectData.put(SystemConstants.ObjectDescribeApiName, PrepayDetailConstants.API_NAME);
            prepayObjectData.put(SystemConstants.Field.RecordType.apiName, PrepayDetailConstants.RecordType.IncomeRecordType.apiName);
            prepayObjectData.put(PrepayDetailConstants.Field.IncomeType.apiName, PrepayIncomeTypeEnum.OrderRefund.getValue());
            IObjectDescribe describe = serviceFacade.findObject(serviceContext.getTenantId(), PrepayDetailConstants.API_NAME);
            prepayObjectData.put(SystemConstants.ObjectDescribeId, describe.getId());
            ObjectDataDocument prepayDataResult = this.triggerAddAction(serviceContext, PrepayDetailConstants.API_NAME, prepayObjectData);
            result.setPrepayDetailData(prepayDataResult);
        }
        if (Objects.nonNull(rebateIncomeData)) {
            checkCreateData(rebateIncomeData);
            rebateIncomeData.put(SystemConstants.ObjectDescribeApiName, RebateIncomeDetailConstants.API_NAME);
            IObjectDescribe describe = serviceFacade.findObject(serviceContext.getTenantId(), RebateIncomeDetailConstants.API_NAME);
            rebateIncomeData.put(SystemConstants.ObjectDescribeId, describe.getId());
            rebateIncomeData.put(RebateIncomeDetailConstants.Field.IncomeType.apiName, RebateIncomeTypeEnum.OrderRefund.getValue());
            ObjectDataDocument newObjectData = this.triggerAddAction(serviceContext, RebateIncomeDetailConstants.API_NAME, rebateIncomeData);
            result.setRebateIncomeDetailData(newObjectData);
        }
        return result;
    }

    private void checkCreateData(ObjectDataDocument objectData) {
        String lifeStatus = (String) objectData.get(SystemConstants.Field.LifeStatus.apiName);
        String errorMessage = "";
        if (StringUtils.isEmpty(lifeStatus)) {
            errorMessage += "LifeStatus不能为空 ";
        }
        if (!errorMessage.isEmpty()) {
            throw new ValidateException(errorMessage);
        }
    }
}
