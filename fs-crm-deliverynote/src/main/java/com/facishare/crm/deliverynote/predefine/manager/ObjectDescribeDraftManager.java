package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.enums.ExpressOrgEnum;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.util.ObjectUtil;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.describe.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 对象定义draft
 * Created by chenzs on 2018/1/9.
 */
@Service
@Slf4j
public class ObjectDescribeDraftManager {
    /**
     * 生成deliveryNoteObj的draft
     */
    public IObjectDescribe generateDeliveryNoteDescribeDraft(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        AutoNumberFieldDescribe name = AutoNumberFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.Name.apiName).label(DeliveryNoteObjConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("{yyyy}{mm}{dd}_").postfix("").required(true).unique(true).index(true).build();
        fieldDescribeList.add(name);

        ObjectReferenceFieldDescribe salesOrderId = ObjectReferenceFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.SalesOrderId.apiName).label(DeliveryNoteObjConstants.Field.SalesOrderId.label).targetApiName(Utils.SALES_ORDER_API_NAME).targetRelatedListLabel(DeliveryNoteObjConstants.Field.SalesOrderId.targetRelatedListLabel).targetRelatedListName(DeliveryNoteObjConstants.Field.SalesOrderId.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(salesOrderId);

        DateFieldDescribe deliveryDate = DateFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.DeliveryDate.apiName).label(DeliveryNoteObjConstants.Field.DeliveryDate.label).index(true).required(true).unique(false).build();
        fieldDescribeList.add(deliveryDate);

        List<ISelectOption> expressOrgSelectOptions = Arrays.stream(ExpressOrgEnum.values()).map(codeEnum -> SelectOptionBuilder.builder().value(codeEnum.getCode()).label(codeEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe expressOrg = SelectOneFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.ExpressOrg.apiName).label(DeliveryNoteObjConstants.Field.ExpressOrg.label).selectOptions(expressOrgSelectOptions).required(false).build();
        fieldDescribeList.add(expressOrg);

        TextFieldDescribe expressOrderId = TextFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.ExpressOrderId.apiName).label(DeliveryNoteObjConstants.Field.ExpressOrderId.label).maxLength(1000).build();
        fieldDescribeList.add(expressOrderId);

        fieldDescribeList.add(getFieldForDeliveryNote(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName));

        LongTextFieldDescribe remark = LongTextFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.Remark.apiName).label(DeliveryNoteObjConstants.Field.Remark.label).maxLength(2000).build();
        fieldDescribeList.add(remark);

        fieldDescribeList.add(getFieldForDeliveryNote(DeliveryNoteObjConstants.Field.ReceiveDate.apiName));
        fieldDescribeList.add(getFieldForDeliveryNote(DeliveryNoteObjConstants.Field.ReceiveRemark.apiName));

        List<ISelectOption> statusSelectOptions = Arrays.stream(DeliveryNoteObjStatusEnum.values()).map(statusEnum -> SelectOptionBuilder.builder().value(statusEnum.getStatus()).label(statusEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe status = SelectOneFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.Status.apiName).label(DeliveryNoteObjConstants.Field.Status.label).selectOptions(statusSelectOptions).required(false).build();
        fieldDescribeList.add(status);

        //发货仓库要等开启库存才能加

        return ObjectDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.API_NAME).displayName(DeliveryNoteObjConstants.DISPLAY_NAME).tenantId(tenantId).createBy(fsUserId).fieldDescribes(fieldDescribeList).storeTableName(DeliveryNoteObjConstants.STORE_TABLE_NAME).iconIndex(DeliveryNoteObjConstants.ICON_INDEX).build();
    }

    /**
     * 生成deliveryNoteProductObj的draft
     */
    public IObjectDescribe generateDeliveryNoteProductDescribeDraft(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        AutoNumberFieldDescribe name = AutoNumberFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.Name.apiName).label(DeliveryNoteProductObjConstants.Field.Name.label).required(true).serialNumber(10).startNumber(1).prefix("").postfix("").required(true).unique(true).index(true).build();
        fieldDescribeList.add(name);

        MasterDetailFieldDescribe deliveryNoteId = MasterDetailFieldDescribeBuilder.builder().isCreateWhenMasterCreate(true).isRequiredWhenMasterCreate(false)
                .apiName(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName).label(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.label).index(true).required(true).targetApiName(DeliveryNoteObjConstants.API_NAME).unique(false).targetRelatedListName(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.targetRelatedListName)
                .targetRelatedListLabel(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.targetRelatedListLabel).build();
        fieldDescribeList.add(deliveryNoteId);

        ObjectReferenceFieldDescribe salesOrderId = ObjectReferenceFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.SalesOrderId.apiName).label(DeliveryNoteProductObjConstants.Field.SalesOrderId.label).targetApiName(Utils.SALES_ORDER_API_NAME).targetRelatedListLabel(DeliveryNoteProductObjConstants.Field.SalesOrderId.targetRelatedListLabel).targetRelatedListName(DeliveryNoteProductObjConstants.Field.SalesOrderId.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(salesOrderId);

        ObjectReferenceFieldDescribe productId = ObjectReferenceFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.ProductId.apiName).label(DeliveryNoteProductObjConstants.Field.ProductId.label).targetApiName(Utils.PRODUCT_API_NAME).targetRelatedListLabel(DeliveryNoteProductObjConstants.Field.ProductId.targetRelatedListLabel).targetRelatedListName(DeliveryNoteProductObjConstants.Field.ProductId.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(productId);

        QuoteFieldDescribe specs = QuoteFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.Specs.apiName).label(DeliveryNoteProductObjConstants.Field.Specs.label).unique(false).required(false).quoteField(DeliveryNoteProductObjConstants.Field.ProductId.apiName.concat("__r.product_spec")).quoteFieldType("text").build();
        fieldDescribeList.add(specs);

        QuoteFieldDescribe unit = QuoteFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.Unit.apiName).label(DeliveryNoteProductObjConstants.Field.Unit.label).unique(false).required(false).quoteField(DeliveryNoteProductObjConstants.Field.ProductId.apiName.concat("__r.unit")).quoteFieldType("select_one").build();
        fieldDescribeList.add(unit);

        NumberFieldDescribe orderProductAmount = NumberFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName).label(DeliveryNoteProductObjConstants.Field.OrderProductAmount.label).decimalPalces(2).length(12).maxLength(14).build();
        fieldDescribeList.add(orderProductAmount);

        NumberFieldDescribe hasDeliveriedNum = NumberFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName).label(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.label).decimalPalces(2).length(12).maxLength(14).build();
        fieldDescribeList.add(hasDeliveriedNum);

        NumberFieldDescribe deliveryNum = NumberFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName).label(DeliveryNoteProductObjConstants.Field.DeliveryNum.label).decimalPalces(2).length(12).maxLength(14).build();
        fieldDescribeList.add(deliveryNum);

        fieldDescribeList.add(getFieldForDeliveryNoteProduct(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName));
        fieldDescribeList.add(getFieldForDeliveryNoteProduct(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName));

        /**
         * Stock("stock", "产品库存"),
         * RealStock("real_stock", "实际库存")
         * 后面开了"库存"按钮才添加
         */
        fieldDescribeList.add(getFieldForDeliveryNoteProduct(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName));
        fieldDescribeList.add(getFieldForDeliveryNoteProduct(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName));

        LongTextFieldDescribe remark = LongTextFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.Remark.apiName).label(DeliveryNoteProductObjConstants.Field.Remark.label).maxLength(2000).build();
        fieldDescribeList.add(remark);

        //预设字段配置（发货单产品的业务类型，去掉'分配业务类型'、'新建'按钮），从对象才需要
        Map<String, Object> configMap = ObjectUtil.buildConfigMap();

        return ObjectDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.API_NAME).displayName(DeliveryNoteProductObjConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(fsUserId).fieldDescribes(fieldDescribeList).storeTableName(DeliveryNoteProductObjConstants.STORE_TABLE_NAME).iconIndex(DeliveryNoteProductObjConstants.ICON_INDEX).build();
    }

    /**
     * 获取发货单字段定义
     */
    public IFieldDescribe getFieldForDeliveryNote(String fieldApiName) {
        if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName)) {
            return NumberFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName).label(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.label).decimalPalces(2).length(12).maxLength(14).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.ReceiveDate.apiName)) {
            return DateFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.ReceiveDate.apiName).label(DeliveryNoteObjConstants.Field.ReceiveDate.label).index(true).required(false).unique(false).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteObjConstants.Field.ReceiveRemark.apiName)) {
            return LongTextFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.ReceiveRemark.apiName).label(DeliveryNoteObjConstants.Field.ReceiveRemark.label).maxLength(2000).build();
        }
        else {
            return null;
        }
    }

    /**
     * 获取发货单产品字段定义
     */
    public IFieldDescribe getFieldForDeliveryNoteProduct(String fieldApiName) {
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.AvgPrice.apiName)) {
            return NumberFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName).label(DeliveryNoteProductObjConstants.Field.AvgPrice.label).decimalPalces(2).length(12).maxLength(14).build();
        }
        if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName)) {
            return NumberFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName).label(DeliveryNoteProductObjConstants.Field.DeliveryMoney.label).decimalPalces(2).length(12).maxLength(14).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName)) {
            return NumberFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName).label(DeliveryNoteProductObjConstants.Field.RealReceiveNum.label).decimalPalces(2).length(12).maxLength(14).build();
        }
        else if (Objects.equals(fieldApiName, DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName)) {
            return LongTextFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName).label(DeliveryNoteProductObjConstants.Field.ReceiveRemark.label).maxLength(2000).build();
        }
        else {
            return null;
        }
    }
}