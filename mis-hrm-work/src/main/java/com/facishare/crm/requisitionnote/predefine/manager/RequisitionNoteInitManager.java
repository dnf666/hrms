package com.facishare.crm.requisitionnote.predefine.manager;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.util.ObjectUtil;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.crm.rest.TemplateApi;
import com.facishare.crm.util.StockUtil;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.*;
import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author liangk
 * @date 13/03/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
@Service
public class RequisitionNoteInitManager {
    @Autowired
    private TemplateApi templateApi;

    @Resource
    private DescribeLogicService describeLogicService;

    /**
     * 调拨单初始化
     *
     * @param user 身份信息
     * @return 是否初始化成功
     */
    public boolean init(User user) {
        String tenantId = user.getTenantId();
        String fsUserId = user.getUserId();
        try {
            Set<String> apiNames = Sets.newHashSet(RequisitionNoteConstants.API_NAME, RequisitionNoteProductConstants.API_NAME);
            Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);

            Boolean isCreate = false;

            if (!describeMap.containsKey(RequisitionNoteConstants.API_NAME)) {
                //数据权限默认私有
                createRequisitionDescribeAndLayout(tenantId, fsUserId);
                isCreate = true;
            }

            if (!describeMap.containsKey(RequisitionNoteProductConstants.API_NAME)) {
                //数据权限默认私有
                createRequisitionProductDescribeAndLayout(tenantId, fsUserId);
                isCreate = true;
            }

            if (isCreate) {
                //初始化打印模板
                initPrintTemplate(user);
            }
        } catch (Exception e) {
            log.warn("InitDescribe error,user:{}", user, e);
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.INIT_ERROR, e.getMessage());
        }
        return true;
    }

    public void initPrintTemplate(User user) {
        Map headers = Maps.newHashMap();
        headers.put("x-tenant-id", user.getTenantId());
        headers.put("x-user-id", User.SUPPER_ADMIN_USER_ID);
        headers.put("Content-Type", "application/json");

        Map pathMap = Maps.newHashMap();
        pathMap.put("tenantId", user.getTenantId());

        Map queryMap = Maps.newHashMap();
        queryMap.put("initDescribeApiNames", RequisitionNoteConstants.API_NAME);

        try {
            Object result = templateApi.init(pathMap, queryMap, headers);
            log.info("templateApi.init (pathMap:{}, queryMap:{}, headers:{}, result:{}", pathMap, queryMap, headers, result);
        } catch (Exception e) {
            log.warn("templateApi.init (pathMap:{}, queryMap:{}, headers:{}", pathMap, queryMap, headers, e);
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.INIT_ERROR, "调拨单打印模板初始化失败");
        }
    }

    /**
     * 创建调拨单
     */
    private DescribeResult createRequisitionDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateRequisitionDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateRequisitionDetailLayout(tenantId, userId);
        ILayout listLayout = generateRequisitionListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        return describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
    }

    private IObjectDescribe generateRequisitionDescribeDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //调拨单编号  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(RequisitionNoteConstants.Field.Name.apiName).label(RequisitionNoteConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("RN{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //调拨日期
        DateFieldDescribe dateTimeFieldDescribe = DateFieldDescribeBuilder.builder().apiName(RequisitionNoteConstants.Field.RequisitionDate.apiName).label(RequisitionNoteConstants.Field.RequisitionDate.label).required(true).unique(false).format("yyyy-MM-dd").build();
        fieldDescribeList.add(dateTimeFieldDescribe);

        //调出仓库 过滤停用仓库
        ObjectReferenceFieldDescribe transferOutWarehouseObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName).label(RequisitionNoteConstants.Field.TransferOutWarehouse.label).targetApiName(Utils.WAREHOUSE_API_NAME).targetRelatedListLabel(RequisitionNoteConstants.Field.TransferOutWarehouse.targetRelatedListLabel).targetRelatedListName(RequisitionNoteConstants.Field.TransferOutWarehouse.targetRelatedListName).unique(false).required(true)
                .wheres(StockUtil.getEnableWarehouseWheres()).build();
        fieldDescribeList.add(transferOutWarehouseObjectReferenceFieldDescribe);

        //调入仓库 过滤停用仓库
        ObjectReferenceFieldDescribe transferInWarehouseObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RequisitionNoteConstants.Field.TransferInWarehouse.apiName).label(RequisitionNoteConstants.Field.TransferInWarehouse.label).targetApiName(Utils.WAREHOUSE_API_NAME).targetRelatedListLabel(RequisitionNoteConstants.Field.TransferInWarehouse.targetRelatedListLabel).targetRelatedListName(RequisitionNoteConstants.Field.TransferInWarehouse.targetRelatedListName).unique(false).required(true)
                .wheres(StockUtil.getEnableWarehouseWheres()).build();
        fieldDescribeList.add(transferInWarehouseObjectReferenceFieldDescribe);

        //是否已确认入库
        BooleanFieldDescribe booleanFieldDescribe = BooleanFieldDescribeBuilder.builder().apiName(RequisitionNoteConstants.Field.InboundConfirmed.apiName).label(RequisitionNoteConstants.Field.InboundConfirmed.label).required(false).unique(false).defaultValue(false).build();
        fieldDescribeList.add(booleanFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(RequisitionNoteConstants.Field.Remark.apiName).label(RequisitionNoteConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        return ObjectDescribeBuilder.builder().apiName(RequisitionNoteConstants.API_NAME).displayName(RequisitionNoteConstants.DISPLAY_NAME).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(RequisitionNoteConstants.STORE_TABLE_NAME).iconIndex(RequisitionNoteConstants.ICON_INDEX).build();
    }

    private ILayout generateRequisitionDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteConstants.Field.Name.apiName).readOnly(false).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteConstants.Field.RequisitionDate.apiName).readOnly(false).renderType(SystemConstants.RenderType.Date.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteConstants.Field.TransferInWarehouse.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteConstants.Field.InboundConfirmed.apiName).readOnly(true).renderType(SystemConstants.RenderType.TrueOrFalse.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.LongText.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).renderType(SystemConstants.RenderType.Employee.renderType).required(false).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);
        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(RequisitionNoteConstants.DETAIL_LAYOUT_API_NAME).displayName(RequisitionNoteConstants.DETAIL_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(RequisitionNoteConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    private ILayout generateRequisitionListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteConstants.Field.Name.apiName).lableName(RequisitionNoteConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteConstants.Field.InboundConfirmed.apiName).lableName(RequisitionNoteConstants.Field.InboundConfirmed.label).renderType(SystemConstants.RenderType.TrueOrFalse.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName).lableName(RequisitionNoteConstants.Field.TransferOutWarehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteConstants.Field.TransferInWarehouse.apiName).lableName(RequisitionNoteConstants.Field.TransferInWarehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteConstants.Field.RequisitionDate.apiName).lableName(RequisitionNoteConstants.Field.RequisitionDate.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(RequisitionNoteConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(RequisitionNoteConstants.LIST_LAYOUT_API_NAME).refObjectApiName(RequisitionNoteConstants.API_NAME).displayName(RequisitionNoteConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();

    }


    /**
     * 创建调拨单产品
     */
    private DescribeResult createRequisitionProductDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateRequisitionProductDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateRequisitionProductDetailLayout(tenantId, userId);
        ILayout listLayout = generateRequisitionProductListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createRequisitionProductDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    private IObjectDescribe generateRequisitionProductDescribeDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //调拨单产品ID  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(RequisitionNoteProductConstants.Field.Name.apiName).label(RequisitionNoteProductConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("RPN{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //调拨单编号
        MasterDetailFieldDescribe requisitionMasterDetailFieldDescribe = MasterDetailFieldDescribeBuilder.builder().isCreateWhenMasterCreate(true).isRequiredWhenMasterCreate(false).apiName(RequisitionNoteProductConstants.Field.Requisition.apiName).label(RequisitionNoteProductConstants.Field.Requisition.label).index(true).required(true).targetApiName(RequisitionNoteConstants.API_NAME).unique(false)
                .targetRelatedListName(RequisitionNoteProductConstants.Field.Requisition.targetRelatedListName).targetRelatedListLabel(RequisitionNoteProductConstants.Field.Requisition.targetRelatedListLabel).build();
        fieldDescribeList.add(requisitionMasterDetailFieldDescribe);

        //产品名称
        ObjectReferenceFieldDescribe productObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder()
                .apiName(RequisitionNoteProductConstants.Field.Product.apiName)
                .label(RequisitionNoteProductConstants.Field.Product.label)
                .targetApiName(Utils.PRODUCT_API_NAME)
                .targetRelatedListLabel(RequisitionNoteProductConstants.Field.Product.targetRelatedListLabel)
                .targetRelatedListName(RequisitionNoteProductConstants.Field.Product.targetRelatedListName)
                .unique(false).required(false).wheres(StockUtil.getOnSaleProductWheres()).build();
        fieldDescribeList.add(productObjectReferenceFieldDescribe);

        //规格
        QuoteFieldDescribe specificationQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(RequisitionNoteProductConstants.Field.Specs.apiName).label(RequisitionNoteProductConstants.Field.Specs.label).unique(false).required(false).quoteField(RequisitionNoteProductConstants.Field.Product.apiName.concat("__r.product_spec")).quoteFieldType("text").build();
        fieldDescribeList.add(specificationQuoteFieldDescribe);

        //单位
        QuoteFieldDescribe unitQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(RequisitionNoteProductConstants.Field.Unit.apiName).label(RequisitionNoteProductConstants.Field.Unit.label).unique(false).required(false).quoteField(RequisitionNoteProductConstants.Field.Product.apiName.concat("__r.unit")).quoteFieldType("select_one").build();
        fieldDescribeList.add(unitQuoteFieldDescribe);

        //库存
        ObjectReferenceFieldDescribe stockObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder()
                .apiName(RequisitionNoteProductConstants.Field.Stock.apiName)
                .label(RequisitionNoteProductConstants.Field.Stock.label)
                .targetApiName(Utils.STOCK_API_NAME)
                .targetRelatedListLabel(RequisitionNoteProductConstants.Field.Stock.targetRelatedListLabel)
                .targetRelatedListName(RequisitionNoteProductConstants.Field.Stock.targetRelatedListName)
                .unique(false).required(true).build();
        fieldDescribeList.add(stockObjectReferenceFieldDescribe);

        //可用库存
        QuoteFieldDescribe availableQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(RequisitionNoteProductConstants.Field.AvailableStock.apiName).label(RequisitionNoteProductConstants.Field.AvailableStock.label).unique(false).required(false).quoteField(RequisitionNoteProductConstants.Field.Stock.apiName.concat("__r.available_stock")).quoteFieldType("number").build();
        fieldDescribeList.add(availableQuoteFieldDescribe);

        //调拨数量
        NumberFieldDescribe amountNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName).label(RequisitionNoteProductConstants.Field.RequisitionProductAmount.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(2).build();
        fieldDescribeList.add(amountNumberFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(RequisitionNoteProductConstants.Field.Remark.apiName).label(RequisitionNoteProductConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        //预设字段配置
        Map<String, Object> configMap = ObjectUtil.buildConfigMap();

        return ObjectDescribeBuilder.builder().apiName(RequisitionNoteProductConstants.API_NAME).
                displayName(RequisitionNoteProductConstants.DISPLAY_NAME).config(configMap).
                tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(RequisitionNoteProductConstants.STORE_TABLE_NAME).iconIndex(RequisitionNoteProductConstants.ICON_INDEX).build();
    }

    private ILayout generateRequisitionProductDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.Name.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.Requisition.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.Product.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.Specs.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.Unit.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.Stock.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.AvailableStock.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RequisitionNoteProductConstants.Field.Remark.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.LongText.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).required(false).renderType(SystemConstants.RenderType.Employee.renderType).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(userId).displayName(RequisitionNoteProductConstants.DETAIL_LAYOUT_DISPLAY_NAME).name(RequisitionNoteProductConstants.DETAIL_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(RequisitionNoteProductConstants.API_NAME).components(components).build();
    }

    private ILayout generateRequisitionProductListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.Name.apiName).lableName(RequisitionNoteProductConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.Requisition.apiName).lableName(RequisitionNoteProductConstants.Field.Requisition.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.Product.apiName).lableName(RequisitionNoteProductConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.Stock.apiName).lableName(RequisitionNoteProductConstants.Field.Stock.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.AvailableStock.apiName).lableName(RequisitionNoteProductConstants.Field.AvailableStock.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.Specs.apiName).lableName(RequisitionNoteProductConstants.Field.Specs.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.Unit.apiName).lableName(RequisitionNoteProductConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName).lableName(RequisitionNoteProductConstants.Field.RequisitionProductAmount.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RequisitionNoteProductConstants.Field.Remark.apiName).lableName(RequisitionNoteProductConstants.Field.Remark.label).renderType(SystemConstants.RenderType.LongText.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(RequisitionNoteProductConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(RequisitionNoteProductConstants.LIST_LAYOUT_API_NAME).refObjectApiName(RequisitionNoteProductConstants.API_NAME).displayName(RequisitionNoteProductConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();

    }

}
