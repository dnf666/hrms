package com.facishare.crm.erpstock.predefine.manager;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.crm.erpstock.constants.ErpWarehouseConstants;
import com.facishare.crm.erpstock.enums.ErpWarehouseEnableEnum;
import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.privilege.userdefobj.DefObjConstants;
import com.facishare.crm.util.ObjectUtil;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.appframework.privilege.DataPrivilegeServiceImpl;
import com.facishare.paas.appframework.privilege.dto.ObjectDataPermissionInfo;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.*;
import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/5/8
 */
@Service
@Slf4j(topic = "erpStockAccess")
public class ErpStockInitManager {
    @Resource
    private IObjectDescribeService objectDescribeService;

    @Resource
    private DescribeLogicService describeLogicService;

    @Resource
    private DataPrivilegeServiceImpl dataPrivilegeService;

    /**
     * 校验待初始化的对象是否已重名
     *
     * @param tenantId
     * @return
     */
    public Set<String> checkExistDisplayName(String tenantId) throws MetadataServiceException {

        Set<String> existDisplayNames = Sets.newHashSet();

        //校验ERP库存对象
        List<String> existStockApiNames = objectDescribeService.checkDisplayNameExist(tenantId, ErpStockConstants.DISPLAY_NAME, "CRM");
        existStockApiNames.forEach(x -> {
            if (!ErpStockConstants.API_NAME.equals(x)) {
                existDisplayNames.add(ErpStockConstants.DISPLAY_NAME);
            }
        });
        
        //校验ERP仓库对象
        List<String> existWarehouseApiNames = objectDescribeService.checkDisplayNameExist(tenantId, ErpWarehouseConstants.DISPLAY_NAME, "CRM");
        existWarehouseApiNames.forEach(x -> {
            if (!ErpWarehouseConstants.API_NAME.equals(x)) {
                existDisplayNames.add(ErpWarehouseConstants.DISPLAY_NAME);
            }
        });
        log.debug("checkDisplayName tenantId:{}, Result:{}", tenantId, existDisplayNames);
        return existDisplayNames;
    }


    /**
     * ERP库存初始化
     *
     * @param user
     * @return
     */
    public boolean init(User user) {
        String tenantId = user.getTenantId();
        String fsUserId = user.getUserId();
        try {
            Set<String> apiNames = Sets.newHashSet(ErpStockConstants.API_NAME, ErpWarehouseConstants.API_NAME);
            Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);

            //初始化Erp仓库
            if (!describeMap.containsKey(ErpWarehouseConstants.API_NAME)) {
                createErpWarehouseDescribeAndLayout(tenantId, fsUserId);
                dataPrivilegeService.addCommonPrivilegeListResult(user, Arrays.asList(new ObjectDataPermissionInfo(ErpWarehouseConstants.API_NAME, ErpWarehouseConstants.DISPLAY_NAME, DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PUBLIC_READONLY.getValue())));
            }

            //初始化ERP库存
            if (!describeMap.containsKey(ErpStockConstants.API_NAME)) {
                createErpStockDescribeAndLayout(tenantId, fsUserId);
                dataPrivilegeService.addCommonPrivilegeListResult(user, Arrays.asList(new ObjectDataPermissionInfo(ErpStockConstants.API_NAME, ErpStockConstants.DISPLAY_NAME, DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PUBLIC_READONLY.getValue())));
            }
        } catch (Exception e) {
            log.warn("InitDescribe error,user:{}", user, e);
            throw new ErpStockBusinessException(ErpStockErrorCode.INIT_ERROR, e.getMessage());
        }
        return true;
    }

    //创建库存
    private DescribeResult createErpStockDescribeAndLayout(String tenantId, String userId) {
        IObjectDescribe objectDescribeDraft = generateErpStockDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateStockDetailLayout(tenantId, userId);
        ILayout listLayout = generateErpStockListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createErpStockDescribeAndLayout. user[{}], describeResult[{}]", user, describeResult);
        return describeResult;
    }

    private IObjectDescribe generateErpStockDescribeDraft(String tenantId, String userId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //库存ID  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.Name.apiName).label(ErpStockConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("SK{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //产品名称
        ObjectReferenceFieldDescribe productObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.Product.apiName).label(ErpStockConstants.Field.Product.label).targetApiName(Utils.PRODUCT_API_NAME).targetRelatedListLabel(ErpStockConstants.Field.Product.targetRelatedListLabel).targetRelatedListName(ErpStockConstants.Field.Product.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(productObjectReferenceFieldDescribe);

        //产品状态
        QuoteFieldDescribe productStatusQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.Product_Status.apiName).label(ErpStockConstants.Field.Product_Status.label).unique(false).required(false).quoteField(ErpStockConstants.Field.Product.apiName.concat("__r.product_status")).quoteFieldType("select_one").build();
        fieldDescribeList.add(productStatusQuoteFieldDescribe);

        //是否赠品
        QuoteFieldDescribe isGiftQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.Is_Give_Away.apiName).label(ErpStockConstants.Field.Is_Give_Away.label).unique(false).required(false).quoteField(ErpStockConstants.Field.Product.apiName.concat("__r.is_giveaway")).quoteFieldType("select_one").build();
        fieldDescribeList.add(isGiftQuoteFieldDescribe);

        //规格
        QuoteFieldDescribe specificationQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.Specs.apiName).label(ErpStockConstants.Field.Specs.label).unique(false).required(false).quoteField(ErpStockConstants.Field.Product.apiName.concat("__r.product_spec")).quoteFieldType("text").build();
        fieldDescribeList.add(specificationQuoteFieldDescribe);

        //单位
        QuoteFieldDescribe unitQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.Unit.apiName).label(ErpStockConstants.Field.Unit.label).unique(false).required(false).quoteField(ErpStockConstants.Field.Product.apiName.concat("__r.unit")).quoteFieldType("select_one").build();
        fieldDescribeList.add(unitQuoteFieldDescribe);

        //产品分类
        QuoteFieldDescribe categoryQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.Category.apiName).label(ErpStockConstants.Field.Category.label).unique(false).required(false).quoteField(ErpStockConstants.Field.Product.apiName.concat("__r.category")).quoteFieldType("select_one").build();
        fieldDescribeList.add(categoryQuoteFieldDescribe);

        //实际库存
        NumberFieldDescribe realStockNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.RealStock.apiName).label(ErpStockConstants.Field.RealStock.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(10).build();
        fieldDescribeList.add(realStockNumberFieldDescribe);

        //可用库存
        NumberFieldDescribe availableStockNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.AvailableStock.apiName).label(ErpStockConstants.Field.AvailableStock.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(10).build();
        fieldDescribeList.add(availableStockNumberFieldDescribe);

        //所属仓库
        ObjectReferenceFieldDescribe warehouseObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(ErpStockConstants.Field.ErpWarehouse.apiName).label(ErpStockConstants.Field.ErpWarehouse.label).targetApiName(ErpWarehouseConstants.API_NAME).targetRelatedListLabel(ErpStockConstants.Field.ErpWarehouse.targetRelatedListLabel).targetRelatedListName(ErpStockConstants.Field.ErpWarehouse.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(warehouseObjectReferenceFieldDescribe);

        //预设字段配置
        Map<String, Object> configMap = ObjectUtil.buildConfigMap(true, false, true, false, false);

        return ObjectDescribeBuilder.builder().apiName(ErpStockConstants.API_NAME).displayName(ErpStockConstants.DISPLAY_NAME).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).config(configMap).storeTableName(ErpStockConstants.STORE_TABLE_NAME).iconIndex(ErpStockConstants.ICON_INDEX).build();
    }

    private ILayout generateStockDetailLayout(String tenantId, String userId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        boolean readOnly = false;
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.Name.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.Product.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.Product_Status.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.Is_Give_Away.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.Specs.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.Unit.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.Category.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());

        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.RealStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.AvailableStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpStockConstants.Field.ErpWarehouse.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());

        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Employee.renderType).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(userId).displayName(ErpStockConstants.DETAIL_LAYOUT_DISPLAY_NAME).name(ErpStockConstants.DETAIL_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(ErpStockConstants.API_NAME).components(components).build();
    }

    private ILayout generateErpStockListLayout(String tenantId, String userId) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.Name.apiName).lableName(ErpStockConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.Product.apiName).lableName(ErpStockConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.Product_Status.apiName).lableName(ErpStockConstants.Field.Product_Status.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.Specs.apiName).lableName(ErpStockConstants.Field.Specs.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.Unit.apiName).lableName(ErpStockConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.Category.apiName).lableName(ErpStockConstants.Field.Category.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.RealStock.apiName).lableName(ErpStockConstants.Field.RealStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.AvailableStock.apiName).lableName(ErpStockConstants.Field.AvailableStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpStockConstants.Field.ErpWarehouse.apiName).lableName(ErpStockConstants.Field.ErpWarehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(ErpStockConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(ErpStockConstants.LIST_LAYOUT_API_NAME).refObjectApiName(ErpStockConstants.API_NAME).displayName(ErpStockConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();
    }

    //创建仓库
    private DescribeResult createErpWarehouseDescribeAndLayout(String tenantId, String userId) {
        IObjectDescribe objectDescribeDraft = generateErpWarehouseDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateErpWarehouseDetailLayout(tenantId, userId);
        ILayout listLayout = generateErpWarehouseListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createWarehouseDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    //创建仓库描述
    private IObjectDescribe generateErpWarehouseDescribeDraft(String tenantId, String userId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //仓库名称  主属性
        TextFieldDescribe nameTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Name.apiName).label(ErpWarehouseConstants.Field.Name.label).required(true).unique(true).build();
        fieldDescribeList.add(nameTextFieldDescribe);

        //地区定位
        AreaFieldDescribe areaFieldDescribe = AreaFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Area.apiName).label(ErpWarehouseConstants.Field.Area.label).areaCountry(ErpWarehouseConstants.Field.Country.apiName).areaProvince(ErpWarehouseConstants.Field.Province.apiName).areaCity(ErpWarehouseConstants.Field.City.apiName).areaDistrict(ErpWarehouseConstants.Field.District.apiName).areaDetailAddress(ErpWarehouseConstants.Field.Address.apiName).areaLocation(ErpWarehouseConstants.Field.Location.apiName)
                .build();
        fieldDescribeList.add(areaFieldDescribe);

        //定位
        LocationFieldDescribe locationFieldDescribe = LocationFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Location.apiName).label(ErpWarehouseConstants.Field.Location.label).active(false).build();
        fieldDescribeList.add(locationFieldDescribe);

        //国家
        CountryFieldDescribe countryFieldDescribe = CountryFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Country.apiName).label(ErpWarehouseConstants.Field.Country.label).build();
        fieldDescribeList.add(countryFieldDescribe);

        //省
        ProvinceFieldDescribe provinceFieldDescribe = ProvinceFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Province.apiName).label(ErpWarehouseConstants.Field.Province.label).cascadeParentApiName(ErpWarehouseConstants.Field.Country.apiName).build();
        fieldDescribeList.add(provinceFieldDescribe);

        //市
        CityFiledDescribe cityFiledDescribe = CityFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.City.apiName).label(ErpWarehouseConstants.Field.City.label).cascadeParentApiName(ErpWarehouseConstants.Field.Province.apiName).build();
        fieldDescribeList.add(cityFiledDescribe);

        //区
        DistrictFieldDescribe districtFieldDescribe = DistrictFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.District.apiName).label(ErpWarehouseConstants.Field.District.label).cascadeParentApiName(ErpWarehouseConstants.Field.City.apiName).build();
        fieldDescribeList.add(districtFieldDescribe);

        //详细地址
        TextFieldDescribe addressTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Address.apiName).label(ErpWarehouseConstants.Field.Address.label).build();
        fieldDescribeList.add(addressTextFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Remark.apiName).label(ErpWarehouseConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        Map<String, Object> configMap = ObjectUtil.buildConfigMap(true, false, true, false, false);

        //启用状态
        List<ISelectOption> selectOptions = Arrays.stream(ErpWarehouseEnableEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe selectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(ErpWarehouseConstants.Field.Is_Enable.apiName).label(ErpWarehouseConstants.Field.Is_Enable.label).selectOptions(selectOptions).defaultValud("1").required(true).build();
        fieldDescribeList.add(selectOneFieldDescribe);

        return ObjectDescribeBuilder.builder().apiName(ErpWarehouseConstants.API_NAME).displayName(ErpWarehouseConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(ErpWarehouseConstants.STORE_TABLE_NAME).iconIndex(ErpWarehouseConstants.ICON_INDEX).build();
    }

    //创建仓库layout
    private ILayout generateErpWarehouseDetailLayout(String tenantId, String userId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.Name.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.Is_Enable.apiName).readOnly(false).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).renderType(SystemConstants.RenderType.Employee.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.LongText.renderType).required(false).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();

        List<IFormField> addressFields = Lists.newArrayList();
        addressFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.Country.apiName).readOnly(false).renderType(SystemConstants.RenderType.Country.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.Province.apiName).readOnly(false).renderType(SystemConstants.RenderType.Province.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.City.apiName).readOnly(false).renderType(SystemConstants.RenderType.City.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.District.apiName).readOnly(false).renderType(SystemConstants.RenderType.District.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.Address.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(ErpWarehouseConstants.Field.Location.apiName).readOnly(false).renderType(SystemConstants.RenderType.Location.renderType).required(false).build());

        FieldSection addressSection = FieldSectionBuilder.builder().name(ErpWarehouseConstants.Field.Area.apiName).header(ErpWarehouseConstants.ADDRESS_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(addressFields).build();

        fieldSections.add(fieldSection);
        fieldSections.add(addressSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(ErpWarehouseConstants.DETAIL_LAYOUT_API_NAME).displayName(ErpWarehouseConstants.DETAIL_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(ErpWarehouseConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    private ILayout generateErpWarehouseListLayout(String tenantId, String userId) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(ErpWarehouseConstants.Field.Name.apiName).lableName(ErpWarehouseConstants.Field.Name.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpWarehouseConstants.Field.Country.apiName).lableName(ErpWarehouseConstants.Field.Country.label).renderType(SystemConstants.RenderType.Country.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpWarehouseConstants.Field.Province.apiName).lableName(ErpWarehouseConstants.Field.Province.label).renderType(SystemConstants.RenderType.Province.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpWarehouseConstants.Field.City.apiName).lableName(ErpWarehouseConstants.Field.City.label).renderType(SystemConstants.RenderType.City.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpWarehouseConstants.Field.District.apiName).lableName(ErpWarehouseConstants.Field.District.label).renderType(SystemConstants.RenderType.District.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(ErpWarehouseConstants.Field.Address.apiName).lableName(ErpWarehouseConstants.Field.Address.label).renderType(SystemConstants.RenderType.Text.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(ErpWarehouseConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(ErpWarehouseConstants.LIST_LAYOUT_API_NAME).refObjectApiName(ErpWarehouseConstants.API_NAME).displayName(ErpWarehouseConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();
    }
}
