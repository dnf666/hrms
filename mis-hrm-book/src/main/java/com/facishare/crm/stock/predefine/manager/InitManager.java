package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.privilege.userdefobj.DefObjConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum;
import com.facishare.crm.stock.enums.GoodsReceivedTypeEnum;
import com.facishare.crm.stock.enums.WarehouseIsEnableEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.crm.stock.util.HttpUtil;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.crm.util.ObjectUtil;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.RecordTypeAuthProxy;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.appframework.metadata.dto.auth.*;
import com.facishare.paas.appframework.privilege.DataPrivilegeServiceImpl;
import com.facishare.paas.appframework.privilege.dto.ObjectDataPermissionInfo;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.ILayoutService;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
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
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.facishare.paas.metadata.api.describe.IFieldDescribe.CONFIG;


@Service
@Slf4j(topic = "stockAccess")
public class InitManager {
    @Resource
    private IObjectDescribeService objectDescribeService;

    @Resource
    private DescribeLogicService describeLogicService;

    @Autowired
    private RecordTypeAuthProxy recordTypeAuthApi;

    @Resource
    private ILayoutService layoutService;

    @Resource
    private StockManager stockManager;

    @Resource
    private GoodsReceivedNoteManager goodsReceivedNoteManager;

    @Resource
    private DataPrivilegeServiceImpl dataPrivilegeService;

    private static final String REQUISITION_DISPLAY_NAME = "调拨单";
    private static final String REQUISITION_API_NAME = "RequisitionNoteObj";
    private static final String REQUISITION_PRODUCT_DISPLAY_NAME = "调拨单产品";
    private static final String REQUISITION_PRODUCT_API_NAME = "RequisitionNoteProductObj";

    private static final String OUTBOUND_DELIVERY_NOTE_DISPLAY_NAME = "出库单";
    private static final String OUTBOUND_DELIVERY_NOTE_API_NAME = "OutboundDeliveryNoteObj";
    private static final String OUTBOUND_DELIVERY_NOTE_PRODUCT_DISPLAY_NAME = "出库单产品";
    private static final String OUTBOUND_DELIVERY_NOTE_PRODUCT_API_NAME = "OutboundDeliveryNoteProductObj";

    /**
     * 校验待初始化的对象是否已重名
     *
     * @param tenantId
     * @return
     */
    public Set<String> checkExistDisplayName(String tenantId) throws MetadataServiceException {
        try {
            //校验仓库对象
            Set<String> existDisplayNames = Sets.newHashSet();
            List<String> existWarehouseApiNames = objectDescribeService.checkDisplayNameExist(tenantId, WarehouseConstants.DISPLAY_NAME, "CRM");
            existWarehouseApiNames.forEach(x -> {
                if (!WarehouseConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(WarehouseConstants.DISPLAY_NAME);
                }
            });

            //校验库存对象
            List<String> existStockApiNames = objectDescribeService.checkDisplayNameExist(tenantId, StockConstants.DISPLAY_NAME, "CRM");
            existStockApiNames.forEach(x -> {
                if (!StockConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(StockConstants.DISPLAY_NAME);
                }
            });

            //校验入库单对象
            List<String> existGoodsReceivedNoteApiNames = objectDescribeService.checkDisplayNameExist(tenantId, GoodsReceivedNoteConstants.DISPLAY_NAME, "CRM");
            existGoodsReceivedNoteApiNames.forEach(x -> {
                if (!GoodsReceivedNoteConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(GoodsReceivedNoteConstants.DISPLAY_NAME);
                }
            });

            //校验入库单产品对象
            List<String> existGoodsReceivedNoteProductApiNames = objectDescribeService.checkDisplayNameExist(tenantId, GoodsReceivedNoteProductConstants.DISPLAY_NAME, "CRM");
            existGoodsReceivedNoteProductApiNames.forEach(x -> {
                if (!GoodsReceivedNoteProductConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(GoodsReceivedNoteProductConstants.DISPLAY_NAME);
                }
            });

            //校验调拨单对象
            List<String> existRequisitionApiNames = objectDescribeService.checkDisplayNameExist(tenantId, REQUISITION_DISPLAY_NAME, "CRM");
            existRequisitionApiNames.forEach(x -> {
                if (!REQUISITION_API_NAME.equals(x)) {
                    existDisplayNames.add(REQUISITION_DISPLAY_NAME);
                }
            });

            //校验调拨单产品对象
            List<String> existRequisitionProductApiNames = objectDescribeService.checkDisplayNameExist(tenantId, REQUISITION_PRODUCT_DISPLAY_NAME, "CRM");
            existRequisitionProductApiNames.forEach(x -> {
                if (!REQUISITION_PRODUCT_API_NAME.equals(x)) {
                    existDisplayNames.add(REQUISITION_PRODUCT_DISPLAY_NAME);
                }
            });

            //校验出库单对象
            List<String> existOutboundNoteApiNames = objectDescribeService.checkDisplayNameExist(tenantId, OUTBOUND_DELIVERY_NOTE_DISPLAY_NAME, "CRM");
            existOutboundNoteApiNames.forEach(x -> {
                if (!OUTBOUND_DELIVERY_NOTE_API_NAME.equals(x)) {
                    existDisplayNames.add(OUTBOUND_DELIVERY_NOTE_DISPLAY_NAME);
                }
            });

            //校验出库单产品对象
            List<String> existOutboundNoteProductApiNames = objectDescribeService.checkDisplayNameExist(tenantId, OUTBOUND_DELIVERY_NOTE_PRODUCT_DISPLAY_NAME, "CRM");
            existOutboundNoteProductApiNames.forEach(x -> {
                if (!OUTBOUND_DELIVERY_NOTE_PRODUCT_API_NAME.equals(x)) {
                    existDisplayNames.add(OUTBOUND_DELIVERY_NOTE_PRODUCT_DISPLAY_NAME);
                }
            });


            log.debug("checkDisplayName tenantId:{}, Result:{}", tenantId, existDisplayNames);
            return existDisplayNames;
        } catch (MetadataServiceException e) {
            log.warn("checkDisplayName error,tenantId:{}", tenantId, e);
            throw e;
        }
    }

    /**
     * 库存初始化
     *
     * @param user
     * @return
     */
    public boolean init(User user) {
        String tenantId = user.getTenantId();
        String fsUserId = user.getUserId();
        try {
            Set<String> apiNames = Sets.newHashSet(WarehouseConstants.API_NAME, StockConstants.API_NAME, GoodsReceivedNoteConstants.API_NAME, GoodsReceivedNoteProductConstants.API_NAME);
            Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);

            //初始化仓库
            if (!describeMap.containsKey(WarehouseConstants.API_NAME)) {
                createWarehouseDescribeAndLayout(tenantId, fsUserId);
                dataPrivilegeService.addCommonPrivilegeListResult(user, Arrays.asList(new ObjectDataPermissionInfo(WarehouseConstants.API_NAME, WarehouseConstants.DISPLAY_NAME, DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PUBLIC_READONLY.getValue())));
            }

            //初始化库存
            if (!describeMap.containsKey(StockConstants.API_NAME)) {
                createStockDescribeAndLayout(tenantId, fsUserId);
                dataPrivilegeService.addCommonPrivilegeListResult(user, Arrays.asList(new ObjectDataPermissionInfo(StockConstants.API_NAME, StockConstants.DISPLAY_NAME, DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PUBLIC_READONLY.getValue())));
            }

            //初始化调拨单
            initRequisition(user);

            //初始化入库单
            if (!describeMap.containsKey(GoodsReceivedNoteConstants.API_NAME)) {
                createGoodsReceivedNoteDescribeAndLayout(tenantId, fsUserId);
                //增加业务类型
                for(GoodsReceivedNoteRecordTypeEnum recordTypeEnum : GoodsReceivedNoteRecordTypeEnum.values()) {
                    if (!Objects.equals(recordTypeEnum.apiName, GoodsReceivedNoteRecordTypeEnum.DefaultGoodsReceivedNote.apiName)) {
                        initNoteRecordType(user, GoodsReceivedNoteConstants.API_NAME, recordTypeEnum.apiName, GoodsReceivedNoteConstants.DETAIL_LAYOUT_API_NAME, Lists.newArrayList());
                    }
                }
            }

            //初始化入库单产品
            if (!describeMap.containsKey(GoodsReceivedNoteProductConstants.API_NAME)) {
                createGoodsReceivedNoteProductDescribeAndLayout(tenantId, fsUserId);
            }
        } catch (Exception e) {
            log.warn("InitDescribe error,user:{}", user, e);
            throw new StockBusinessException(StockErrorCode.INIT_ERROR, e.getMessage());
        }
        return true;
    }

    public Boolean initOutboundDeliveryNote(ServiceContext serviceContext) {
        String initOutboundDeliveryNoteUrl = ConfigCenter.PAAS_FRAMEWORK_URL + "outbound_delivery_note/service/enable_outbound_delivery_note";
        try {
            Map<String, String> headers = StockUtils.getHeaders(serviceContext.getTenantId(), serviceContext.getUser().getUserId());
            headers.put("Content-Type", "application/json");
            StockType.EnableOutboundDeliveryNoteResult result = HttpUtil.post(initOutboundDeliveryNoteUrl, headers, null, StockType.EnableOutboundDeliveryNoteResult.class);
            log.info("initOutboundDeliveryNote, headers:{}, result:{}", headers, result);
            if (!Objects.equals(result.getEnableStatus(), 2)) {
                return false;
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initNoteRecordType(User user, String objectApiName, String recordTypeId, String viewId, List<RoleInfoPojo> roleInfoPojos) {
        initAssignRecord(user, roleInfoPojos, objectApiName, Lists.newArrayList(recordTypeId));
        Map<String, String> recordViewMap = Maps.newHashMap();
        recordViewMap.put(recordTypeId, viewId);
        initAssignLayout(user, roleInfoPojos, objectApiName, recordViewMap);
    }

    private void initAssignRecord(User user, List<RoleInfoPojo> roleInfoPojos, String entityId, List<String> recordTypeIds) {
        AddRoleRecordTypeModel.Result result;
        RecordTypePojo recordTypePojo;
        for (String recordTypeId : recordTypeIds) {
            List<RecordTypePojo> recordTypePojos = Lists.newArrayList();
            for (RoleInfoPojo roleInfoPojo : roleInfoPojos) {
                recordTypePojo = new RecordTypePojo();
                recordTypePojo.setAppId("CRM");
                recordTypePojo.setEntityId(entityId);
                recordTypePojo.setTenantId(user.getTenantId());
                recordTypePojo.setRecordTypeId(recordTypeId);
                recordTypePojo.setRoleCode(roleInfoPojo.getRoleCode());
                recordTypePojo.setDefaultType(false);
                recordTypePojos.add(recordTypePojo);
            }
            AddRoleRecordTypeModel.Arg arg = new AddRoleRecordTypeModel.Arg();
            arg.setRecordTypePojos(recordTypePojos);
            arg.setRecordTypeId(recordTypeId);
            arg.setEntityId(entityId);
            arg.setAuthContext(user);
            result = recordTypeAuthApi.addRoleRecordType(arg);
            log.info("entityId:{},result{}", entityId, result);
        }
    }

    private AddRoleViewModel.Result initAssignLayout(User user, List<RoleInfoPojo> roleInfoPojos, String entityId, Map<String, String> recordTypeViewMap) {
        List<RoleViewPojo> roleViewPojos = Lists.newArrayList();
        RoleViewPojo roleViewPojo;
        for (RoleInfoPojo roleInfoPojo : roleInfoPojos) {
            for (Map.Entry<String, String> entry : recordTypeViewMap.entrySet()) {
                roleViewPojo = new RoleViewPojo();
                roleViewPojo.setAppId("CRM");
                roleViewPojo.setEntityId(entityId);
                roleViewPojo.setTenantId(user.getTenantId());
                roleViewPojo.setRecordTypeId(entry.getKey());
                roleViewPojo.setRoleCode(roleInfoPojo.getRoleCode());
                roleViewPojo.setViewId(entry.getValue());
                roleViewPojos.add(roleViewPojo);
            }
        }
        AddRoleViewModel.Arg arg = new AddRoleViewModel.Arg();
        arg.setRoleViewPojos(roleViewPojos);
        arg.setAuthContext(user);
        AddRoleViewModel.Result result = recordTypeAuthApi.addRoleView(arg);
        return result;
    }


    //创建仓库
    private DescribeResult createWarehouseDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateWarehouseDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateWarehouseDetailLayout(tenantId, userId);
        ILayout listLayout = generateWarehouseListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createWarehouseDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    //创建仓库描述
    private IObjectDescribe generateWarehouseDescribeDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //仓库名称  主属性
        TextFieldDescribe nameTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Name.apiName).label(WarehouseConstants.Field.Name.label).required(true).unique(true).build();
        fieldDescribeList.add(nameTextFieldDescribe);

        //仓库编号
        TextFieldDescribe numberTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Number.apiName).label(WarehouseConstants.Field.Number.label).required(true).build();
        fieldDescribeList.add(numberTextFieldDescribe);

        //地区定位
        AreaFieldDescribe areaFieldDescribe = AreaFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Area.apiName).label(WarehouseConstants.Field.Area.label).areaCountry(WarehouseConstants.Field.Country.apiName).areaProvince(WarehouseConstants.Field.Province.apiName).areaCity(WarehouseConstants.Field.City.apiName).areaDistrict(WarehouseConstants.Field.District.apiName).areaDetailAddress(WarehouseConstants.Field.Address.apiName).areaLocation(WarehouseConstants.Field.Location.apiName).build();
        fieldDescribeList.add(areaFieldDescribe);

        //定位
        LocationFieldDescribe locationFieldDescribe = LocationFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Location.apiName).label(WarehouseConstants.Field.Location.label).active(false).build();
        fieldDescribeList.add(locationFieldDescribe);

        //国家
        CountryFieldDescribe countryFieldDescribe = CountryFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Country.apiName).label(WarehouseConstants.Field.Country.label).build();
        fieldDescribeList.add(countryFieldDescribe);

        //省
        ProvinceFieldDescribe provinceFieldDescribe = ProvinceFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Province.apiName).label(WarehouseConstants.Field.Province.label).cascadeParentApiName(WarehouseConstants.Field.Country.apiName).build();
        fieldDescribeList.add(provinceFieldDescribe);

        //市
        CityFiledDescribe cityFiledDescribe = CityFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.City.apiName).label(WarehouseConstants.Field.City.label).cascadeParentApiName(WarehouseConstants.Field.Province.apiName).build();
        fieldDescribeList.add(cityFiledDescribe);

        //区
        DistrictFieldDescribe districtFieldDescribe = DistrictFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.District.apiName).label(WarehouseConstants.Field.District.label).cascadeParentApiName(WarehouseConstants.Field.City.apiName).build();
        fieldDescribeList.add(districtFieldDescribe);

        //详细地址
        TextFieldDescribe addressTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Address.apiName).label(WarehouseConstants.Field.Address.label).build();
        fieldDescribeList.add(addressTextFieldDescribe);

        //是否默认仓
        BooleanFieldDescribe boolFieldDescribe = BooleanFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Is_Default.apiName).label(WarehouseConstants.Field.Is_Default.label).required(true).defaultValue(false).build();
        fieldDescribeList.add(boolFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Remark.apiName).label(WarehouseConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        //适用客户
        UseScopeFieldDescribe customerRangeUseScopeFieldDescribe = UseScopeFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Account_range.apiName).label(WarehouseConstants.Field.Account_range.label).targetApiName(StockUtils.ACCOUNT_API_NAME).defaultValue(WarehouseConstants.ACCOUNT_RANGE_DEFAULT_VALUE).expressionType(WarehouseConstants.ACCOUNT_RANGE_EXPRESSION_TYPE).build();
        fieldDescribeList.add(customerRangeUseScopeFieldDescribe);

        //启用状态
        List<ISelectOption> selectOptions = Arrays.stream(WarehouseIsEnableEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe selectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(WarehouseConstants.Field.Is_Enable.apiName).label(WarehouseConstants.Field.Is_Enable.label).selectOptions(selectOptions).defaultValud("1").required(true).build();
        fieldDescribeList.add(selectOneFieldDescribe);

        return ObjectDescribeBuilder.builder().apiName(WarehouseConstants.API_NAME).displayName(WarehouseConstants.DISPLAY_NAME).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(WarehouseConstants.STORE_TABLE_NAME).iconIndex(WarehouseConstants.ICON_INDEX).build();
    }

    //创建仓库layout
    private ILayout generateWarehouseDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Name.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Number.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Is_Enable.apiName).readOnly(false).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).renderType(SystemConstants.RenderType.Employee.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.LongText.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Is_Default.apiName).readOnly(false).renderType(SystemConstants.RenderType.TrueOrFalse.renderType).required(true).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();

        List<IFormField> addressFields = Lists.newArrayList();
        addressFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Country.apiName).readOnly(false).renderType(SystemConstants.RenderType.Country.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Province.apiName).readOnly(false).renderType(SystemConstants.RenderType.Province.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.City.apiName).readOnly(false).renderType(SystemConstants.RenderType.City.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.District.apiName).readOnly(false).renderType(SystemConstants.RenderType.District.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Address.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(false).build());
        addressFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Location.apiName).readOnly(false).renderType(SystemConstants.RenderType.Location.renderType).required(false).build());

        FieldSection addressSection = FieldSectionBuilder.builder().name(WarehouseConstants.Field.Area.apiName).header(WarehouseConstants.ADDRESS_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(addressFields).build();

        List<IFormField> customerRangeFormFields = Lists.newArrayList();
        customerRangeFormFields.add(FormFieldBuilder.builder().fieldName(WarehouseConstants.Field.Account_range.apiName).readOnly(false).renderType(SystemConstants.RenderType.UseScope.renderType).required(true).build());
        FieldSection customerRangeFieldSection = FieldSectionBuilder.builder().header(WarehouseConstants.Field.Account_range.label).name(WarehouseConstants.CUSTOMER_RANGE_SECTION_API_NAME).showHeader(true).fields(customerRangeFormFields).build();

        fieldSections.add(fieldSection);
        fieldSections.add(addressSection);
        fieldSections.add(customerRangeFieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(WarehouseConstants.DETAIL_LAYOUT_API_NAME).displayName(WarehouseConstants.DETAIL_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(WarehouseConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    private ILayout generateWarehouseListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(WarehouseConstants.Field.Name.apiName).lableName(WarehouseConstants.Field.Name.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(WarehouseConstants.Field.Number.apiName).lableName(WarehouseConstants.Field.Number.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(WarehouseConstants.Field.Country.apiName).lableName(WarehouseConstants.Field.Country.label).renderType(SystemConstants.RenderType.Country.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(WarehouseConstants.Field.Province.apiName).lableName(WarehouseConstants.Field.Province.label).renderType(SystemConstants.RenderType.Province.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(WarehouseConstants.Field.City.apiName).lableName(WarehouseConstants.Field.City.label).renderType(SystemConstants.RenderType.City.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(WarehouseConstants.Field.District.apiName).lableName(WarehouseConstants.Field.District.label).renderType(SystemConstants.RenderType.District.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(WarehouseConstants.Field.Address.apiName).lableName(WarehouseConstants.Field.Address.label).renderType(SystemConstants.RenderType.Text.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(WarehouseConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(WarehouseConstants.LIST_LAYOUT_API_NAME).refObjectApiName(WarehouseConstants.API_NAME).displayName(WarehouseConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();
    }

    //创建入库单
    private DescribeResult createGoodsReceivedNoteDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateGoodsReceivedNoteDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateGoodsReceivedNoteDetailLayout(tenantId, userId);
        ILayout listLayout = generateGoodsReceivedNoteListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        return describeResult;
    }

    private IObjectDescribe generateGoodsReceivedNoteDescribeDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //入库单编号  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.Field.Name.apiName).label(GoodsReceivedNoteConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("GRN{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //调拨单编号
        ObjectReferenceFieldDescribe requisitionObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName).label(GoodsReceivedNoteConstants.Field.RequisitionNote.label).
                targetApiName(REQUISITION_API_NAME).targetRelatedListLabel(GoodsReceivedNoteConstants.Field.RequisitionNote.targetRelatedListLabel).targetRelatedListName(GoodsReceivedNoteConstants.Field.RequisitionNote.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(requisitionObjectReferenceFieldDescribe);

        //入库日期
        DateFieldDescribe dateTimeFieldDescribe = DateFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName).label(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.label).required(true).unique(false).format("yyyy-MM-dd").build();
        fieldDescribeList.add(dateTimeFieldDescribe);

        //所属仓库 查找关联过滤停用
        ObjectReferenceFieldDescribe warehouseObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.Field.Warehouse.apiName).label(GoodsReceivedNoteConstants.Field.Warehouse.label).targetApiName(WarehouseConstants.API_NAME).targetRelatedListLabel(GoodsReceivedNoteConstants.Field.Warehouse.targetRelatedListLabel).targetRelatedListName(GoodsReceivedNoteConstants.Field.Warehouse.targetRelatedListName).unique(false).required(true)
                .wheres(StockUtils.getEnableWarehouseWheres()).build();
        fieldDescribeList.add(warehouseObjectReferenceFieldDescribe);

        //入库类型
        List<ISelectOption> typeSelectOptions = Arrays.stream(GoodsReceivedTypeEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe selectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName).label(GoodsReceivedNoteConstants.Field.GoodsReceivedType.label).required(true).selectOptions(typeSelectOptions).build();
        fieldDescribeList.add(selectOneFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.Field.Remark.apiName).label(GoodsReceivedNoteConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        //业务类型
        List<IRecordTypeOption> recordTypeOptions = Arrays.stream(GoodsReceivedNoteRecordTypeEnum.values()).map(recordType -> RecordTypeOptionBuilder.builder().apiName(recordType.apiName).label(recordType.label).build()).collect(Collectors.toList());
        recordTypeOptions.forEach(recordTypeOption -> recordTypeOption.set(CONFIG, ObjectUtil.buildFieldOptionConfigMap()));
        RecordTypeFieldDescribe recordTypeSelectOneFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeSelectOneFieldDescribe);

        return ObjectDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.API_NAME).displayName(GoodsReceivedNoteConstants.DISPLAY_NAME).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(GoodsReceivedNoteConstants.STORE_TABLE_NAME).iconIndex(GoodsReceivedNoteConstants.ICON_INDEX).build();
    }

    private ILayout generateGoodsReceivedNoteDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.Name.apiName).readOnly(false).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName).readOnly(false).renderType(SystemConstants.RenderType.Date.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.Warehouse.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName).readOnly(false).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).renderType(SystemConstants.RenderType.Employee.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.LongText.renderType).required(false).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);
        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(GoodsReceivedNoteConstants.DETAIL_LAYOUT_API_NAME).displayName(GoodsReceivedNoteConstants.DETAIL_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(GoodsReceivedNoteConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    private ILayout generateGoodsReceivedNoteListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.Name.apiName).lableName(GoodsReceivedNoteConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName).lableName(GoodsReceivedNoteConstants.Field.GoodsReceivedType.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.Warehouse.apiName).lableName(GoodsReceivedNoteConstants.Field.Warehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName).lableName(GoodsReceivedNoteConstants.Field.RequisitionNote.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.apiName).lableName(GoodsReceivedNoteConstants.Field.GoodsReceivedDate.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(GoodsReceivedNoteConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(GoodsReceivedNoteConstants.LIST_LAYOUT_API_NAME).refObjectApiName(GoodsReceivedNoteConstants.API_NAME).displayName(GoodsReceivedNoteConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();

    }

    //创建入库单产品
    private DescribeResult createGoodsReceivedNoteProductDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateGoodsReceivedNoteProductDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateGoodsReceivedNoteProductDetailLayout(tenantId, userId);
        ILayout listLayout = generateGoodsReceivedNoteProductListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createGoodsReceivedNoteProductDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    private IObjectDescribe generateGoodsReceivedNoteProductDescribeDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //入库单产品ID  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteProductConstants.Field.Name.apiName).label(GoodsReceivedNoteProductConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("GRNP{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //入库单编号
        MasterDetailFieldDescribe goodsReceivedNoteMasterDetailFieldDescribe = MasterDetailFieldDescribeBuilder.builder().isCreateWhenMasterCreate(true).isRequiredWhenMasterCreate(false).apiName(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.apiName).label(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.label).index(true).required(true).targetApiName(GoodsReceivedNoteConstants.API_NAME).unique(false)
                .targetRelatedListName(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.targetRelatedListName).targetRelatedListLabel(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.targetRelatedListLabel).build();
        fieldDescribeList.add(goodsReceivedNoteMasterDetailFieldDescribe);

        //产品名称
        ObjectReferenceFieldDescribe productObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder()
                .apiName(GoodsReceivedNoteProductConstants.Field.Product.apiName)
                .label(GoodsReceivedNoteProductConstants.Field.Product.label)
                .targetApiName(Utils.PRODUCT_API_NAME)
                .targetRelatedListLabel(GoodsReceivedNoteProductConstants.Field.Product.targetRelatedListLabel)
                .targetRelatedListName(GoodsReceivedNoteProductConstants.Field.Product.targetRelatedListName)
                .unique(false).required(true).wheres(StockUtils.getOnSaleProductWheres()).build();
        fieldDescribeList.add(productObjectReferenceFieldDescribe);

        //是否赠品 TODO现在产品没有是否赠品字段
        QuoteFieldDescribe isGiftQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteProductConstants.Field.IsGiveAway.apiName).label(GoodsReceivedNoteProductConstants.Field.IsGiveAway.label).unique(false).required(false).quoteField(GoodsReceivedNoteProductConstants.Field.Product.apiName.concat("__r.is_giveaway")).quoteFieldType("select_one").build();
        fieldDescribeList.add(isGiftQuoteFieldDescribe);

        //规格
        QuoteFieldDescribe specificationQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteProductConstants.Field.Specs.apiName).label(GoodsReceivedNoteProductConstants.Field.Specs.label).unique(false).required(false).quoteField(GoodsReceivedNoteProductConstants.Field.Product.apiName.concat("__r.product_spec")).quoteFieldType("text").build();
        fieldDescribeList.add(specificationQuoteFieldDescribe);

        //单位
        QuoteFieldDescribe unitQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteProductConstants.Field.Unit.apiName).label(GoodsReceivedNoteProductConstants.Field.Unit.label).unique(false).required(false).quoteField(GoodsReceivedNoteProductConstants.Field.Product.apiName.concat("__r.unit")).quoteFieldType("select_one").build();
        fieldDescribeList.add(unitQuoteFieldDescribe);

        //入库数量
        NumberFieldDescribe amountNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName).label(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(2).build();
        fieldDescribeList.add(amountNumberFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteProductConstants.Field.Remark.apiName).label(GoodsReceivedNoteProductConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        //预设字段配置
        Map<String, Object> configMap = ObjectUtil.buildConfigMap();

        return ObjectDescribeBuilder.builder().apiName(GoodsReceivedNoteProductConstants.API_NAME).displayName(GoodsReceivedNoteProductConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(GoodsReceivedNoteProductConstants.STORE_TABLE_NAME).iconIndex(GoodsReceivedNoteProductConstants.ICON_INDEX).build();
    }

    private ILayout generateGoodsReceivedNoteProductDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.Name.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.Product.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.IsGiveAway.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.Specs.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.Unit.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(GoodsReceivedNoteProductConstants.Field.Remark.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.LongText.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).required(false).renderType(SystemConstants.RenderType.Employee.renderType).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(userId).displayName(GoodsReceivedNoteProductConstants.DETAIL_LAYOUT_DISPLAY_NAME).name(GoodsReceivedNoteProductConstants.DETAIL_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(GoodsReceivedNoteProductConstants.API_NAME).components(components).build();
    }

    private ILayout generateGoodsReceivedNoteProductListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.Name.apiName).lableName(GoodsReceivedNoteProductConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.apiName).lableName(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.Product.apiName).lableName(GoodsReceivedNoteProductConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.IsGiveAway.apiName).lableName(GoodsReceivedNoteProductConstants.Field.IsGiveAway.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.Specs.apiName).lableName(GoodsReceivedNoteProductConstants.Field.Specs.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.Unit.apiName).lableName(GoodsReceivedNoteProductConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName).lableName(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(GoodsReceivedNoteProductConstants.Field.Remark.apiName).lableName(GoodsReceivedNoteProductConstants.Field.Remark.label).renderType(SystemConstants.RenderType.LongText.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(GoodsReceivedNoteProductConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(GoodsReceivedNoteProductConstants.LIST_LAYOUT_API_NAME).refObjectApiName(GoodsReceivedNoteProductConstants.API_NAME).displayName(GoodsReceivedNoteProductConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();

    }

    //创建库存
    private DescribeResult createStockDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateStockDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateStockDetailLayout(tenantId, userId);
        ILayout listLayout = generateStockListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createStockDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    private IObjectDescribe generateStockDescribeDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //库存ID  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(StockConstants.Field.Name.apiName).label(StockConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("SK{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //产品名称
        ObjectReferenceFieldDescribe productObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(StockConstants.Field.Product.apiName).label(StockConstants.Field.Product.label).targetApiName(Utils.PRODUCT_API_NAME).targetRelatedListLabel(StockConstants.Field.Product.targetRelatedListLabel).targetRelatedListName(StockConstants.Field.Product.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(productObjectReferenceFieldDescribe);

        //产品状态
        QuoteFieldDescribe productStatusQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(StockConstants.Field.Product_Status.apiName).label(StockConstants.Field.Product_Status.label).unique(false).required(false).quoteField(StockConstants.Field.Product.apiName.concat("__r.product_status")).quoteFieldType("select_one").build();
        fieldDescribeList.add(productStatusQuoteFieldDescribe);

        //是否赠品 TODO现在产品没有是否赠品字段
        QuoteFieldDescribe isGiftQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(StockConstants.Field.Is_Give_Away.apiName).label(StockConstants.Field.Is_Give_Away.label).unique(false).required(false).quoteField(StockConstants.Field.Product.apiName.concat("__r.is_giveaway")).quoteFieldType("select_one").build();
        fieldDescribeList.add(isGiftQuoteFieldDescribe);

        //规格
        QuoteFieldDescribe specificationQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(StockConstants.Field.Specs.apiName).label(StockConstants.Field.Specs.label).unique(false).required(false).quoteField(StockConstants.Field.Product.apiName.concat("__r.product_spec")).quoteFieldType("text").build();
        fieldDescribeList.add(specificationQuoteFieldDescribe);

        //单位
        QuoteFieldDescribe unitQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(StockConstants.Field.Unit.apiName).label(StockConstants.Field.Unit.label).unique(false).required(false).quoteField(StockConstants.Field.Product.apiName.concat("__r.unit")).quoteFieldType("select_one").build();
        fieldDescribeList.add(unitQuoteFieldDescribe);

        //产品分类
        QuoteFieldDescribe categoryQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(StockConstants.Field.Category.apiName).label(StockConstants.Field.Category.label).unique(false).required(false).quoteField(StockConstants.Field.Product.apiName.concat("__r.category")).quoteFieldType("select_one").build();
        fieldDescribeList.add(categoryQuoteFieldDescribe);

        //实际库存
        NumberFieldDescribe realStockNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(StockConstants.Field.RealStock.apiName).label(StockConstants.Field.RealStock.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(2).build();
        fieldDescribeList.add(realStockNumberFieldDescribe);

        //冻结库存
        NumberFieldDescribe blockedStockNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(StockConstants.Field.BlockedStock.apiName).label(StockConstants.Field.BlockedStock.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(2).build();
        fieldDescribeList.add(blockedStockNumberFieldDescribe);

        //可用库存
        NumberFieldDescribe availableStockNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(StockConstants.Field.AvailableStock.apiName).label(StockConstants.Field.AvailableStock.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(2).defaultIsExpression(true).defaultValue("$" + StockConstants.Field.RealStock.apiName + "$-$" + StockConstants.Field.BlockedStock.apiName + "$").build();
        fieldDescribeList.add(availableStockNumberFieldDescribe);

        //安全库存
        NumberFieldDescribe safetyStockNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(StockConstants.Field.SafetyStock.apiName).label(StockConstants.Field.SafetyStock.label).length(12).maxLength(14).decimalPalces(2).required(false).roundMode(4).build();
        fieldDescribeList.add(safetyStockNumberFieldDescribe);

        //所属仓库
        ObjectReferenceFieldDescribe warehouseObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(StockConstants.Field.Warehouse.apiName).label(StockConstants.Field.Warehouse.label).targetApiName(WarehouseConstants.API_NAME).targetRelatedListLabel(StockConstants.Field.Warehouse.targetRelatedListLabel).targetRelatedListName(StockConstants.Field.Warehouse.targetRelatedListName).unique(false).required(true).wheres(StockUtils.getEnableWarehouseWheres()).build();
        fieldDescribeList.add(warehouseObjectReferenceFieldDescribe);

        //预设字段配置
        Map<String, Object> configMap = ObjectUtil.buildConfigMap();

        return ObjectDescribeBuilder.builder().apiName(StockConstants.API_NAME).displayName(StockConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(StockConstants.STORE_TABLE_NAME).iconIndex(StockConstants.ICON_INDEX).build();
    }

    private ILayout generateStockDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        boolean readOnly = false;
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Name.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Product.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Product_Status.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Is_Give_Away.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Specs.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Unit.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Category.apiName).readOnly(readOnly).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());

        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.RealStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.BlockedStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.AvailableStock.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.SafetyStock.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(StockConstants.Field.Warehouse.apiName).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());

        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(readOnly).required(true).renderType(SystemConstants.RenderType.Employee.renderType).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(userId).displayName(StockConstants.DETAIL_LAYOUT_DISPLAY_NAME).name(StockConstants.DETAIL_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(StockConstants.API_NAME).components(components).build();
    }

    private ILayout generateStockListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Name.apiName).lableName(StockConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Product.apiName).lableName(StockConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Product_Status.apiName).lableName(StockConstants.Field.Product_Status.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Specs.apiName).lableName(StockConstants.Field.Specs.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Unit.apiName).lableName(StockConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Category.apiName).lableName(StockConstants.Field.Category.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.RealStock.apiName).lableName(StockConstants.Field.RealStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.AvailableStock.apiName).lableName(StockConstants.Field.AvailableStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.BlockedStock.apiName).lableName(StockConstants.Field.BlockedStock.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(StockConstants.Field.Warehouse.apiName).lableName(StockConstants.Field.Warehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(StockConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(StockConstants.LIST_LAYOUT_API_NAME).refObjectApiName(StockConstants.API_NAME).displayName(StockConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();
    }

    public StockType.EnableRequisitionResult initRequisition(User user) {
        String initRequisitionUrl = ConfigCenter.PAAS_FRAMEWORK_URL + "requisition_note/service/enable_requisition";
        try {
            Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), user.getUserId());
            headers.put("Content-Type", "application/json");
            StockType.EnableRequisitionResult result = HttpUtil.post(initRequisitionUrl, headers, null, StockType.EnableRequisitionResult.class);
            log.info("initRequisition,  headers:{}, result:{}", headers, result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public IObjectDescribe findObjectDescribeByTenantIdAndDescribeApiName(String tenantId, String describeApiName) {
        IObjectDescribe objectDescribe;
        try {
            objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, describeApiName);
            log.info("objectDescribeService.findByTenantIdAndDescribeApiName success, tenantId:{}, objectDescribeApiName:{}, result:{}", tenantId, describeApiName, objectDescribe);
        } catch (MetadataServiceException e) {
            log.warn("objectDescribeService.findByTenantIdAndDescribeApiName failed ,tenantId:{}, objectDescribeApiName:{}", tenantId, describeApiName, e);
            throw new StockBusinessException(() -> e.getErrorCode().getCode(), "查询定义信息失败，" + e.getMessage());
        }
        return objectDescribe;
    }

    /**
     * 更新定义
     */
    public void replaceObjectDescribe(IObjectDescribe objectDescribe, boolean active, boolean isAllowLabelRepeat) {
        try {
            IObjectDescribe result = objectDescribeService.replace(objectDescribe, isAllowLabelRepeat);
            log.info("objectDescribeService.replaceObjectDescribe success, objectDescribe:{}, active:{}, isAllowLabelRepeat:{}, result:{}", objectDescribe, true, false, result);
        } catch (MetadataServiceException e) {
            log.warn("objectDescribeService.replaceObjectDescribe failed, objectDescribe:{} ,active:{} ,isAllowLabelRepeat:{}", objectDescribe, true, false, e);
            throw new StockBusinessException(() -> e.getErrorCode().getCode(), "更新对象定义信息失败，" + e.getMessage());
        } catch (Exception e) {
            log.warn("objectDescribeService.replaceObjectDescribe failed, objectDescribe:{} ,active:{} ,isAllowLabelRepeat:{}", objectDescribe, true, false, e);
            throw e;
        }
    }

    /**
     * 是否存在某个字段的定义
     */
    public boolean hasFieldDescribe(IObjectDescribe objectDescribe, String fieldApiName) {
        //获取字段定义
        List<IFieldDescribe> fieldDescribes = null;

        fieldDescribes = objectDescribe.getFieldDescribes();


        //判断是否有字段
        for (IFieldDescribe f : fieldDescribes) {
            if (Objects.equals(f.getApiName(), fieldApiName)) {
                return true;
            }
        }
        return false;
    }

    public void addFieldDescribes(IObjectDescribe objectDescribe, String objectApiName, List<String> fieldApiNames) {
        if (CollectionUtils.isEmpty(fieldApiNames)) {
            return;
        }

        //原来的describe是否有要添加的所有字段
        boolean hasAllField = true;
        for (String fieldApiName : fieldApiNames) {
            boolean hasFieldDescribe = hasFieldDescribe(objectDescribe, fieldApiName);
            if (!hasFieldDescribe) {
                hasAllField = false;
                if (Objects.equals(objectApiName, StockConstants.API_NAME)) {
                    objectDescribe.addFieldDescribe(getFieldForStock(fieldApiName));
                } else if (Objects.equals(objectApiName, GoodsReceivedNoteConstants.API_NAME)) {
                    objectDescribe.addFieldDescribe(getFieldForGoodsReceivedNote(fieldApiName));
                }
            }
        }

        //要加的字段中，有的原来的describe是没有的，则更新describe
        if (!hasAllField) {
            replaceObjectDescribe(objectDescribe, true, false);
        }
    }

    /**
     * 获取库存字段定义
     */
    public IFieldDescribe getFieldForStock(String fieldApiName) {
        if (Objects.equals(fieldApiName, StockConstants.Field.Category.apiName)) {
            //产品分类
            QuoteFieldDescribe categoryQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(StockConstants.Field.Category.apiName).label(StockConstants.Field.Category.label).unique(false).required(false).quoteField(StockConstants.Field.Product.apiName.concat("__r.category")).quoteFieldType("select_one").build();
            return categoryQuoteFieldDescribe;
        } else if (Objects.equals(fieldApiName, StockConstants.Field.Product_Status.apiName)) {
            QuoteFieldDescribe productStatusQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(StockConstants.Field.Product_Status.apiName).label(StockConstants.Field.Product_Status.label).unique(false).required(false).quoteField(StockConstants.Field.Product.apiName.concat("__r.product_status")).quoteFieldType("select_one").build();
            return productStatusQuoteFieldDescribe;
        } else {
            return null;
        }
    }

    /**
     * 获取入库单字段定义
     */
    public IFieldDescribe getFieldForGoodsReceivedNote(String fieldApiName) {
        if (Objects.equals(fieldApiName, GoodsReceivedNoteConstants.Field.RequisitionNote.apiName)) {
            //调拨单编号
            return ObjectReferenceFieldDescribeBuilder.builder().apiName(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName).label(GoodsReceivedNoteConstants.Field.RequisitionNote.label).
                    targetApiName(REQUISITION_API_NAME).targetRelatedListLabel(GoodsReceivedNoteConstants.Field.RequisitionNote.targetRelatedListLabel).targetRelatedListName(GoodsReceivedNoteConstants.Field.RequisitionNote.targetRelatedListName).unique(false).required(false).build();
        } else {
            return null;
        }
    }


    public void replaceObjectLayout(ILayout layout) {
        try {
            ILayout result = layoutService.replace(layout);
            log.debug("layoutService.replaceObjectDescribe success, layout:{}, result:{}", layout, result);
        } catch (MetadataServiceException e) {
            log.warn("layoutService.replaceObjectDescribe failed, layout:{}", layout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "更新layout信息失败，" + e.getMessage());
        } catch (Exception e) {
            log.warn("layoutService.replaceObjectDescribe failed, layout:{}", layout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, StockErrorCode.BUSINESS_ERROR.getMessage() + e.getMessage());
        }
    }

    public List<ILayout> findLayoutByObjectDescribeApiNameAndTenantId(String objectDescribeApiName, String tenantId) {
        List<ILayout> layouts;
        try {
            layouts = layoutService.findByObjectDescribeApiNameAndTenantId(objectDescribeApiName, tenantId);
        } catch (MetadataServiceException e) {
            log.warn("layoutService.findByObjectDescribeApiNameAndTenantId failed, objectDescribeApiName:{}, tenantId:{}", objectDescribeApiName, tenantId, e);
            throw new StockBusinessException(() -> e.getErrorCode().getCode(), "查询layout信息失败，" + e.getMessage());
        }
        return layouts;
    }

    /**
     * 对象objectApiName添加字段fieldApiNames
     */
    public void addFieldLayouts(String tenantId, String objectApiName, List<String> fieldApiNames, boolean isAddDefaultLayout, boolean isAddListLayout) {
        //1、查询layout
        List<ILayout> layouts = findLayoutByObjectDescribeApiNameAndTenantId(objectApiName, tenantId);

        //2、区分defaultLayout、listLayout
        ILayout defaultLayout = null;
        ILayout listLayout = null;
        for (ILayout layout : layouts) {
            if (Objects.equals(layout.getName(), objectApiName + "_default_layout__c")) {
                defaultLayout = layout;
            } else if (Objects.equals(layout.getName(), objectApiName + "_list_layout__c")) {
                listLayout = layout;
            }
        }

        if (defaultLayout == null || listLayout == null) {
            log.warn("defaultLayout:{}, listLayout:{}", defaultLayout, listLayout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "查询Layout异常");
        }

        //3、describe+layout：添加字段（添加之前，判断是否已存在对应的字段）
        if (isAddDefaultLayout) {
            defaultLayoutAddFields(defaultLayout, objectApiName, fieldApiNames);
        }
        if (isAddListLayout) {
            listLayoutAddFields(listLayout, objectApiName, fieldApiNames);
        }
    }

    /**
     * detailLayout添加字段
     */
    public void defaultLayoutAddFields(ILayout layout, String objectApiName, List<String> fieldApiNames) {
        if (CollectionUtils.isEmpty(fieldApiNames)) {
            return;
        }

        boolean hasAllFields = true;
        for (String fieldApiName : fieldApiNames) {
            boolean hasField = isDefaultLayoutHasField(layout, fieldApiName);
            if (!hasField) {
                hasAllFields = false;
                break;
            }
        }

        if (!hasAllFields) {
            layout = updateDetailLayoutField(layout, objectApiName);
            replaceObjectLayout(layout);
        }
    }

    /**
     * listLayout添加字段
     */
    public void listLayoutAddFields(ILayout layout, String objectApiName, List<String> fieldApiNames) {
        if (CollectionUtils.isEmpty(fieldApiNames)) {
            return;
        }

        boolean hasAllFields = true;
        for (String fieldApiName : fieldApiNames) {
            boolean hasField = isListLayoutHasField(layout, fieldApiName);
            if (!hasField) {
                hasAllFields = false;
                break;
            }
        }

        if (!hasAllFields) {
            layout = updateListLayoutField(layout, objectApiName);
            replaceObjectLayout(layout);
        }
    }

    /**
     * defaultLayout是否存在某个字段
     */
    public boolean isDefaultLayoutHasField(ILayout layout, String fieldApiName) {
        //1、获取FormComponent
        List<IComponent> components = null;
        FormComponent formComponent = null;
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", layout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "layout获取Component信息错误," + e.getMessage());
        }
        for (IComponent iComponent : components) {
            if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)) {
                formComponent = (FormComponent) iComponent;
                break;
            }
        }

        //2、获取formFields
        IFieldSection baseFieldSection = null;
        List<IFieldSection> fieldSections = formComponent.getFieldSections();
        for (IFieldSection fieldSection : fieldSections) {
            if (Objects.equals(fieldSection.getName(), LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                baseFieldSection = fieldSection;
                break;
            }
        }

        //3、是否字段
        List<IFormField> fields = baseFieldSection.getFields();
        if (CollectionUtils.isEmpty(fields)) {
            return false;
        }
        for (IFormField field : fields) {
            if (Objects.equals(field.getFieldName(), fieldApiName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * listLayout是否存在某个字段
     */
    public boolean isListLayoutHasField(ILayout layout, String fieldApiName) {
        //1、获取tableColumns
        List<IComponent> components = null;
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", layout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "layout获取Component信息错误," + e.getMessage());
        }
        TableComponent tableComponent = (TableComponent) components.get(0);

        //2、是否字段
        List<ITableColumn> fields = tableComponent.getIncludeFields();
        if (CollectionUtils.isEmpty(fields)) {
            return false;
        }
        for (ITableColumn field : fields) {
            if (Objects.equals(field.getName(), fieldApiName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 更新detailLayout的field到最新的
     */
    public ILayout updateDetailLayoutField(ILayout layout, String objectApiName) {
        //1、获取FormComponent
        List<IComponent> components = null;
        FormComponent formComponent = null;
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", layout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "layout获取Component信息错误," + e.getMessage());
        }
        for (IComponent iComponent : components) {
            if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)) {
                formComponent = (FormComponent) iComponent;
                break;
            }
        }

        //2、获取formFields
        IFieldSection baseFieldSection = null;
        List<IFieldSection> fieldSections = formComponent.getFieldSections();
        for (IFieldSection fieldSection : fieldSections) {
            if (Objects.equals(fieldSection.getName(), LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                baseFieldSection = fieldSection;
                break;
            }
        }

        //3、添加新字段
        List<IFormField> newFormFields = Lists.newArrayList();
        List<IFormField> oldFormFields = baseFieldSection.getFields();
        if (Objects.equals(objectApiName, StockConstants.API_NAME)) {
            newFormFields = stockManager.getFormFields();
            newFormFields.forEach(field -> oldFormFields.removeIf(oldField -> field.getFieldName().equals(oldField.getFieldName())));
            newFormFields.addAll(oldFormFields);
        }

        if (Objects.equals(objectApiName, GoodsReceivedNoteConstants.API_NAME)) {
            newFormFields = goodsReceivedNoteManager.getFormFields();
            newFormFields.forEach(field -> oldFormFields.removeIf(oldField -> field.getFieldName().equals(oldField.getFieldName())));
            newFormFields.addAll(oldFormFields);
            newFormFields.removeIf(field -> field.getFieldName().equals(GoodsReceivedNoteConstants.Field.Remark.apiName));
            IFormField remarkField = FormFieldBuilder.builder().fieldName(GoodsReceivedNoteConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.LongText.renderType).required(false).build();
            newFormFields.add(remarkField);
        }
        baseFieldSection.setFields(newFormFields);
        return layout;
    }

    /**
     * 更新listLayout的field到最新的
     */
    public ILayout updateListLayoutField(ILayout layout, String objectApiName) {
        //1、获取tableColumns
        List<IComponent> components = null;
        try {
            components = layout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", layout);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "layout获取Component信息错误," + e.getMessage());
        }
        TableComponent tableComponent = (TableComponent) components.get(0);

        //2、添加字段
        List<ITableColumn> columns = Lists.newArrayList();
        List<ITableColumn> oldColumns = tableComponent.getIncludeFields();
        if (Objects.equals(objectApiName, StockConstants.API_NAME)) {
            columns = stockManager.getTableColumns();
            columns.forEach(oldColumns::remove);
            columns.forEach(column -> oldColumns.removeIf(oldColumn -> column.getName().equals(oldColumn.getName())));
            columns.addAll(oldColumns);
        } else if (Objects.equals(objectApiName, GoodsReceivedNoteConstants.API_NAME)) {
            columns = goodsReceivedNoteManager.getTableColumns();
            columns.forEach(column -> oldColumns.removeIf(oldColumn -> column.getName().equals(oldColumn.getName())));
            columns.addAll(oldColumns);
        }
        tableComponent.setIncludeFields(columns);
        return layout;
    }

    public void updateGoodsReceivedNoteField(User user, String objectApiName, String fieldApiName) {
        //查describe
        IObjectDescribe objectDescribe = findObjectDescribeByTenantIdAndDescribeApiName(user.getTenantId(), objectApiName);

        //找字段
        IFieldDescribe fieldDescribe = null;

        fieldDescribe = objectDescribe.getFieldDescribe(fieldApiName);


        try {
            //更新
            if(fieldDescribe.getApiName().equals(GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName)) {
                //入库类型
                List<ISelectOption> typeSelectOptions = Arrays.stream(GoodsReceivedTypeEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
                SelectOneFieldDescribe selectOneFieldDescribe = (SelectOneFieldDescribe)fieldDescribe;
                selectOneFieldDescribe.setSelectOptions(typeSelectOptions);
                objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(selectOneFieldDescribe));
            } else if (fieldDescribe.getApiName().equals(SystemConstants.Field.RecordType.apiName)) {
                //业务类型
                List<IRecordTypeOption> recordTypeOptions = Arrays.stream(GoodsReceivedNoteRecordTypeEnum.values()).map(recordType -> RecordTypeOptionBuilder.builder().apiName(recordType.apiName).label(recordType.label).build()).collect(Collectors.toList());
                recordTypeOptions.forEach(recordTypeOption -> recordTypeOption.set(CONFIG, ObjectUtil.buildFieldOptionConfigMap()));
                RecordTypeFieldDescribe recordTypeSelectOneFieldDescribe = (RecordTypeFieldDescribe) fieldDescribe;
                recordTypeSelectOneFieldDescribe.setRecordTypeOptions(recordTypeOptions);
                objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(recordTypeSelectOneFieldDescribe));
            }
        } catch (MetadataServiceException e) {
            log.warn("updateFieldDescribe failed,, objectDescribe:{}, fieldDescribeList:{}", objectDescribe, Lists.newArrayList(fieldDescribe), e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "更新字段定义失败, " + e);
        }
    }

    public void updateDescribeObjectConfig(User user, String apiName) {
        //查describe
        IObjectDescribe objectDescribe = findObjectDescribeByTenantIdAndDescribeApiName(user.getTenantId(), apiName);

        try {
            //更新config
            objectDescribe.setConfig(ObjectUtil.buildConfigMap());
            objectDescribeService.updateDescribe(objectDescribe);
        } catch (MetadataServiceException e) {
            log.warn("updateFieldDescribe failed,, objectDescribe:{}, apiName:{}", objectDescribe, apiName, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "更新字段定义失败, " + e);
        }
    }
}