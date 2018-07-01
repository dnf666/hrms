package com.facishare.crm.customeraccount.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
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
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchQuery;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
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
@ServiceModule("prepayDetailTransfer")
public class PrepayDetailTransferService extends CommonTransferService {
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
        upsertRecord.addUpsertColumnName(PrepayDetailConstants.Field.OrderPayment.apiName);
        upsertRecord.setOpType(OpType.UPSERT);
        upsertRecord.setTable(TransferConstants.PREPAY_DETAIL_TABLE);
        upsertRecord.addIdColumn(new StringColumn("id", id));
        String orderPaymentId = ConverterUtil.convert2String(sourceData.getData().get(PrepayDetailConstants.Field.Payment.apiName));
        upsertRecord.addStringColumn("tenant_id", sourceData.getTenantId()).addStringColumn(PrepayDetailConstants.Field.OrderPayment.apiName, orderPaymentId);
        recordList.add(upsertRecord);
        return recordList;
    }

    public List<IObjectData> getOnePageRequestData(User user, String tenantId, int offset, int limit) {
        String fieldApiName = SystemConstants.Field.TennantID.apiName;
        String fieldValue = tenantId;
        TermConditions condition = new TermConditions();
        condition.addCondition(fieldApiName, fieldValue);

        //增加order_payment_id == null && payment_id  != null 的判断<br>
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addCondition(condition);
        searchQuery.setOffset(offset);
        searchQuery.setLimit(limit);

        QueryResult<IObjectData> dataResult = serviceFacade.findBySearchQuery(user, PrepayDetailConstants.API_NAME, searchQuery);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(dataResult.getData())) {
            return Lists.newArrayList();
        } else {
            return dataResult.getData();
        }
    }

    /**
     * 查询 paymentId != null && orderPaymentId == null的值<br>
     * @param user
     * @param tenantId
     * @param offset
     * @param limit
     * @return
     */
    public List<IObjectData> getOnePageRequestDataByPaymentAndOrderPaymentCondition(User user, String tenantId, int offset, int limit) {
        String fieldApiName = SystemConstants.Field.TennantID.apiName;
        String fieldValue = tenantId;

        IFilter tenantIdfilter = new Filter();
        tenantIdfilter.setOperator(Operator.IN);
        tenantIdfilter.setFieldName(fieldApiName);
        tenantIdfilter.setFieldValues(Lists.newArrayList(fieldValue));

        IFilter orderPaymentNullFilter = new Filter();
        orderPaymentNullFilter.setOperator(Operator.IS);
        orderPaymentNullFilter.setFieldName(PrepayDetailConstants.Field.OrderPayment.apiName);
        orderPaymentNullFilter.setFieldValues(Lists.newArrayList("NULL"));

        //        IFilter paymentNotNullFilter = new Filter();
        //        paymentNotNullFilter.setOperator(Operator.ISN);
        //        paymentNotNullFilter.setFieldName(PrepayDetailConstants.Field.Payment.apiName);
        //        paymentNotNullFilter.setFieldValues(Lists.newArrayList("NULL"));

        List<IFilter> filters = Lists.newArrayList();
        filters.add(tenantIdfilter);
        filters.add(orderPaymentNullFilter);
        //        filters.add(paymentNotNullFilter);

        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);

        QueryResult<IObjectData> dataResult = serviceFacade.findBySearchQuery(user, PrepayDetailConstants.API_NAME, searchTemplateQuery);
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

        //更新描述
        updateDescribeAndLayout(serviceContext);
        User user = serviceContext.getUser();
        int currentPageNumber = 0;
        int offset = 0;
        int limit = ConfigCenter.batchCreateSize;
        int fetchSize = 0;
        do {
            log.debug("begin processTransfer->current page number:{},offset:{},limit:{}", currentPageNumber, offset, limit);
            try {
                List<IObjectData> onepageResultList = getOnePageRequestData(user, tenantId, offset, limit);
                if (null != onepageResultList) {
                    fetchSize = onepageResultList.size();
                }
                RequestData requestData = buildRequestData(tenantId, TransferConstants.PREPAY_DETAIL_TABLE, onepageResultList);
                log.info("requestData=====>{}", requestData);
                transfer(requestData);
            } catch (Exception e) {
                log.warn("error occure when transfer,for user:{},tenantId:{},offset:{}", user, tenantId, offset, e);
                throw new CustomerAccountBusinessException(CustomerAccountErrorCode.PREPAY_TRANSFER_ERROR, e.getMessage());
            }
            offset += limit;
            currentPageNumber++;
        } while (fetchSize == limit);
        deletePaymentFieldOfPrepayDetail(serviceContext.getUser());
        return new EmptyResult();
    }

    @Override
    protected void updateDescribeAndLayout(ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        String tenantId = user.getTenantId();
        try {
            //预存款增加orderPayment 字段
            IObjectDescribe prepayDetailDescribe = serviceFacade.findObject(tenantId, PrepayDetailConstants.API_NAME);
            IFieldDescribe dbOrderPaymentFieldDescribe = prepayDetailDescribe.getFieldDescribe(PrepayDetailConstants.Field.OrderPayment.apiName);
            if (dbOrderPaymentFieldDescribe == null) {
                List<IFieldDescribe> fieldDescribesList = new ArrayList<>();

                ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OrderPayment.apiName).label(PrepayDetailConstants.Field.OrderPayment.label).required(false).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(PrepayDetailConstants.Field.OrderPayment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();
                fieldDescribesList.add(customerObjectReferenceFieldDescribe);
                //传true 不进行循环引用校验<br>
                objectDescribeService.addCustomFieldDescribe(prepayDetailDescribe, fieldDescribesList);

                ILayout prepayOutcomeLayout = serviceFacade.findLayoutByApiName(user, PrepayDetailConstants.OUTCOME_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
                prepayOutcomeLayout = InitUtil.updatePrepayDetailLayoutForOrderPaymentReplace(user, prepayOutcomeLayout);
                serviceFacade.updateLayout(user, prepayOutcomeLayout);

                ILayout prepayDefaultLayout = serviceFacade.findLayoutByApiName(user, PrepayDetailConstants.DEFAULT_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
                prepayDefaultLayout = InitUtil.updatePrepayDetailLayoutForOrderPaymentReplace(user, prepayDefaultLayout);
                serviceFacade.updateLayout(user, prepayDefaultLayout);
            }
        } catch (MetadataServiceException e) {
            log.warn("addOrderPaymentField->for user:{}", user, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.PREPAY_TRANSFER_ERROR, e.getErrorCode().getMessage());
        }
    }

    //删除payment字段
    private void deletePaymentFieldOfPrepayDetail(User user) {
        log.debug("data  is transfer completed and delete the payment field,for user:{}", user);
        IObjectDescribe prepayDetailDescribe = serviceFacade.findObject(user.getTenantId(), PrepayDetailConstants.API_NAME);
        try {
            IFieldDescribe dbPaymentFieldDescribe = prepayDetailDescribe.getFieldDescribe(PrepayDetailConstants.Field.Payment.apiName);
            if (dbPaymentFieldDescribe != null) {
                ObjectReferenceFieldDescribe paymentFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Payment.apiName).label(PrepayDetailConstants.Field.Payment.label).required(false).targetApiName(SystemConstants.PaymentApiName).targetRelatedListName(PrepayDetailConstants.Field.Payment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.Payment.targetRelatedListLabel).build();
                List<IFieldDescribe> describeListTobeDeleted = new ArrayList<>();
                describeListTobeDeleted.add(paymentFieldDescribe);
                objectDescribeService.deleteCustomFieldDescribe(prepayDetailDescribe, describeListTobeDeleted);
            }
        } catch (MetadataServiceException e) {
            log.warn("deletePaymentFieldOfPrepayDetail user:{}", user, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.PREPAY_TRANSFER_ERROR, e.getErrorCode().getMessage());
        }
    }
}