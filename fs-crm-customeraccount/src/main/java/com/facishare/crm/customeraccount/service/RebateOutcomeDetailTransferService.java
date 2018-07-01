package com.facishare.crm.customeraccount.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.constants.TransferConstants;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountConfigManager;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.predefine.service.dto.EmptyResult;
import com.facishare.crm.customeraccount.util.ConfigCenter;
import com.facishare.crm.customeraccount.util.InitUtil;
import com.facishare.crm.describebuilder.ObjectReferenceFieldDescribeBuilder;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.condition.TermConditions;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.search.SearchQuery;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.fxiaoke.transfer.dto.OpType;
import com.fxiaoke.transfer.dto.Record;
import com.fxiaoke.transfer.dto.RequestData;
import com.fxiaoke.transfer.dto.SourceData;
import com.fxiaoke.transfer.dto.TableSchema;
import com.fxiaoke.transfer.dto.columns.StringColumn;
import com.fxiaoke.transfer.utils.ConverterUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2018/1/24.
 */
@Slf4j
@Component
@ServiceModule("rebateOutcomeDetailTransfer")
public class RebateOutcomeDetailTransferService extends CommonTransferService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private IObjectDescribeService objectDescribeService;

    @Autowired
    private CustomerAccountConfigManager customerAccountConfigManager;

    @Override
    protected List<Record> parseRecord(SourceData sourceData, TableSchema tableSchema) {
        List<Record> recordList = Lists.newArrayList();
        String id = ConverterUtil.convert2String(sourceData.getData().get("id").toString());
        Record upsertRecord = new Record();
        upsertRecord.addUpsertColumnName(RebateOutcomeDetailConstants.Field.OrderPayment.apiName);
        upsertRecord.setOpType(OpType.UPSERT);
        upsertRecord.setTable(TransferConstants.REBATE_OUTCOME_DETAIL_TABLE);
        upsertRecord.addIdColumn(new StringColumn("id", id));
        upsertRecord.addStringColumn("tenant_id", sourceData.getTenantId()).addStringColumn(RebateOutcomeDetailConstants.Field.OrderPayment.apiName, ConverterUtil.convert2String(sourceData.getData().get(RebateOutcomeDetailConstants.Field.Payment.apiName)));
        recordList.add(upsertRecord);
        return recordList;
    }

    public List<IObjectData> getOnePageRequestData(User user, String tenantId, int offset, int limit) {
        String fieldApiName = SystemConstants.Field.TennantID.apiName;
        String fieldValue = tenantId;
        TermConditions condition = new TermConditions();
        condition.addCondition(fieldApiName, fieldValue);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addCondition(condition);
        searchQuery.setOffset(offset);
        searchQuery.setLimit(limit);
        QueryResult<IObjectData> dataResult = serviceFacade.findBySearchQuery(user, RebateOutcomeDetailConstants.API_NAME, searchQuery);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(dataResult.getData())) {
            return Lists.newArrayList();
        } else {
            return dataResult.getData();
        }
    }

    /**
     * serviceContext 必须插入tenantId和user<br>
     * @param serviceContext
     */
    @ServiceMethod("processTransfer")
    public EmptyResult processTransfer(ServiceContext serviceContext) {
        log.debug("begin transfer prepayDetail ,for serviceContext:{}", serviceContext);
        String tenantId = serviceContext.getTenantId();

        CustomerAccountType.CustomerAccountEnableSwitchStatus customerAccountEnableStatus = customerAccountConfigManager.getStatus(tenantId);
        if (CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE.getValue() != customerAccountEnableStatus.getValue()) {
            log.info("客户账户未开始，其开启开关的状态为:{}", customerAccountEnableStatus.getLabel());
            return new EmptyResult();
        }

        updateDescribeAndLayout(serviceContext);
        User user = serviceContext.getUser();
        int currentPageNumber = 0;
        int offset = 0;
        int limit = ConfigCenter.batchCreateSize;
        int fetchSize = 0;
        do {
            log.debug("begin processTransfer->current page:{},offset:{},limit:{}", currentPageNumber, offset, limit);
            try {
                List<IObjectData> onepageResultList = getOnePageRequestData(user, tenantId, offset, limit);
                if (null != onepageResultList) {
                    fetchSize = onepageResultList.size();
                }
                RequestData requestData = buildRequestData(tenantId, TransferConstants.REBATE_OUTCOME_DETAIL_TABLE, onepageResultList);
                log.info("requestData=====>{}", requestData);
                transfer(requestData);
            } catch (Exception e) {
                log.warn("error occure when transfer,for user:{},tenantId:{},offset:{}", user, tenantId, offset, e);
                throw new CustomerAccountBusinessException(CustomerAccountErrorCode.REBATE_OUTCOME_TRANSFER_ERROR, e.getMessage());
            }
            offset += limit;
            currentPageNumber++;
        } while (fetchSize == limit);
        deletePaymentFieldOfRebateOutcome(serviceContext.getUser());
        return new EmptyResult();
    }

    @Override
    protected void updateDescribeAndLayout(ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        try {
            String tenantId = user.getTenantId();
            //返利增加orderPayment字段
            IObjectDescribe rebateOutcomeDetailDescribe = serviceFacade.findObject(tenantId, RebateOutcomeDetailConstants.API_NAME);
            IFieldDescribe dbOrderPaymentFieldDescribe = rebateOutcomeDetailDescribe.getFieldDescribe(RebateOutcomeDetailConstants.Field.OrderPayment.apiName);
            if (dbOrderPaymentFieldDescribe == null) {
                List<IFieldDescribe> fieldDescribesList = new ArrayList<>();

                ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OrderPayment.apiName).label(PrepayDetailConstants.Field.OrderPayment.label).required(false).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(PrepayDetailConstants.Field.OrderPayment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();
                fieldDescribesList.add(customerObjectReferenceFieldDescribe);

                objectDescribeService.addCustomFieldDescribe(rebateOutcomeDetailDescribe, fieldDescribesList);

                ILayout rebateOutcomeDefaultLayout = serviceFacade.findLayoutByApiName(user, RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME, rebateOutcomeDetailDescribe.getApiName());
                rebateOutcomeDefaultLayout = InitUtil.updateRebateOutcomeLayoutForOrderPaymentReplace(user, rebateOutcomeDefaultLayout);
                serviceFacade.updateLayout(user, rebateOutcomeDefaultLayout);
            }
        } catch (MetadataServiceException e) {
            log.warn("addOrderPaymentField->for user:{}", user, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.REBATE_OUTCOME_TRANSFER_ERROR, e.getErrorCode().getMessage());
        }
    }

    //删除payment字段
    private void deletePaymentFieldOfRebateOutcome(User user) {
        log.debug("data  is transfer completed and delete the payment field,for user:{}", user);

        IObjectDescribe rebateOutcomeDetailDescribe = serviceFacade.findObject(user.getTenantId(), RebateOutcomeDetailConstants.API_NAME);
        try {
            IFieldDescribe dbPaymentFieldDescribe = rebateOutcomeDetailDescribe.getFieldDescribe(RebateOutcomeDetailConstants.Field.Payment.apiName);
            if (dbPaymentFieldDescribe != null) {
                ObjectReferenceFieldDescribe paymentFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.Payment.apiName).label(RebateOutcomeDetailConstants.Field.Payment.label).required(false).targetApiName(SystemConstants.PaymentApiName).targetRelatedListName(RebateOutcomeDetailConstants.Field.Payment.targetRelatedListName).targetRelatedListLabel(RebateOutcomeDetailConstants.Field.Payment.targetRelatedListLabel).build();
                List<IFieldDescribe> describeListTobeDeleted = new ArrayList<>();
                describeListTobeDeleted.add(paymentFieldDescribe);
                objectDescribeService.deleteCustomFieldDescribe(rebateOutcomeDetailDescribe, describeListTobeDeleted);
            }
        } catch (MetadataServiceException e) {
            log.warn("deletePaymentFieldOfRebateOutcome user:{}", user, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.REBATE_OUTCOME_TRANSFER_ERROR, e.getErrorCode().getMessage());
        }
    }
}