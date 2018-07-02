package com.facishare.crm.outbounddeliverynote.predefine.manager;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.crm.outbounddeliverynote.enums.OutboundTypeEnum;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteErrorCode;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteException;
import com.facishare.crm.rest.TemplateApi;
import com.facishare.crm.util.ObjectUtil;
import com.facishare.crm.util.StockUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.RecordTypeAuthProxy;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.appframework.metadata.dto.auth.*;
import com.facishare.paas.metadata.api.IRecordTypeOption;
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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.facishare.paas.metadata.api.describe.IFieldDescribe.CONFIG;

/**
 * @author linchf
 * @date 2018/3/14
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
@Service
public class OutboundDeliveryNoteInitManager {
    @Resource
    private IObjectDescribeService objectDescribeService;

    @Resource
    private DescribeLogicService describeLogicService;

    @Autowired
    private RecordTypeAuthProxy recordTypeAuthApi;

    @Autowired
    private TemplateApi templateApi;

    @Resource
    private ServiceFacade serviceFacade;

    private final static String WAREHOUSE_API_NAME = "WarehouseObj";
    private final static String DELIVERY_NOTE_API_NAME = "DeliveryNoteObj";
    private final static String REQUISITION_API_NAME = "RequisitionNoteObj";
    private final static String STOCK_API_NAME = "StockObj";
    /**
     * 校验待初始化的对象是否已重名
     *
     * @param tenantId
     * @return
     */
    public Set<String> checkExistDisplayName(String tenantId) throws MetadataServiceException {
        try {
            Set<String> existDisplayNames = Sets.newHashSet();

            //校验出库单对象
            List<String> existNoteApiNames = objectDescribeService.checkDisplayNameExist(tenantId, OutboundDeliveryNoteConstants.DISPLAY_NAME, "CRM");
            existNoteApiNames.forEach(x -> {
                if (!OutboundDeliveryNoteConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(OutboundDeliveryNoteConstants.DISPLAY_NAME);
                }
            });

            //校验出库单产品对象
            List<String> existProductApiNames = objectDescribeService.checkDisplayNameExist(tenantId, OutboundDeliveryNoteProductConstants.DISPLAY_NAME, "CRM");
            existProductApiNames.forEach(x -> {
                if (!OutboundDeliveryNoteProductConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(OutboundDeliveryNoteProductConstants.DISPLAY_NAME);
                }
            });

            log.debug("checkDisplayName tenantId:{}, Result:{}", tenantId, existDisplayNames);
            return existDisplayNames;
        } catch (MetadataServiceException e) {
            log.warn("checkDisplayName error,tenantId:{}", tenantId, e);
            throw e;
        }
    }

    public boolean init(User user) {
        String tenantId = user.getTenantId();
        String fsUserId = user.getUserId();
        try {
            Set<String> apiNames = Sets.newHashSet(OutboundDeliveryNoteConstants.API_NAME, OutboundDeliveryNoteProductConstants.API_NAME);
            Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);

            Boolean isCreated = false;
            if (!describeMap.containsKey(OutboundDeliveryNoteConstants.API_NAME)) {
                createOutboundDeliveryNoteDescribeAndLayout(tenantId, fsUserId);
                for(OutboundDeliveryNoteRecordTypeEnum recordTypeEnum : OutboundDeliveryNoteRecordTypeEnum.values()) {
                    if (!Objects.equals(recordTypeEnum.apiName, OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName)) {
                        initNoteRecordType(user, OutboundDeliveryNoteConstants.API_NAME, recordTypeEnum.apiName, OutboundDeliveryNoteConstants.DETAIL_LAYOUT_API_NAME, Lists.newArrayList());
                    }
                }
                isCreated = true;
            }

            if (!describeMap.containsKey(OutboundDeliveryNoteProductConstants.API_NAME)) {
                createOutboundDeliveryNoteProductDescribeAndLayout(tenantId, fsUserId);
                isCreated = true;
            }

            if (isCreated) {
                //发货单权限与出库单权限对齐（创建、作废发货单会同时操作出库单，对旧数据功能权限补齐）
                addFuncAccess(user.getTenantId());

                //初始化出库单打印模板
                initPrintTemplate(user);
            }
        } catch (Exception e) {
            log.warn("InitDescribe error,user:{}", user, e);
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.INIT_ERROR, e.getMessage());
        }
        return true;
    }

    public void initPrintTemplate(User user) {
        Map headers = new HashMap();
        headers.put("x-tenant-id", user.getTenantId());
        headers.put("x-user-id", User.SUPPER_ADMIN_USER_ID);
        headers.put("Content-Type", "application/json");

        Map pathMap = new HashMap();
        pathMap.put("tenantId", user.getTenantId());

        Map queryMap = new HashMap();
        queryMap.put("initDescribeApiNames", OutboundDeliveryNoteConstants.API_NAME);

        try {
            Object result = templateApi.init(pathMap, queryMap, headers);
            log.info("templateApi.init (pathMap:{}, queryMap:{}, headers:{}, result:{}", pathMap, queryMap, headers, result);
        } catch (Exception e) {
            log.warn("templateApi.init (pathMap:{}, queryMap:{}, headers:{}", pathMap, queryMap, headers, e);
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.INIT_ERROR, "出库单打印模板初始化失败");
        }
    }

    //发货单权限与出库单权限对齐
    public void addFuncAccess(String tenantId) {
        User superAdmin = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
        //出库单新建权限
        List<String> createFuncRoles = serviceFacade.getHavePrivilegeRolesByActionCode(superAdmin, DeliveryNoteObjConstants.API_NAME, ObjectAction.CREATE.getActionCode());
        serviceFacade.rolesAddFuncAccess(superAdmin, OutboundDeliveryNoteConstants.API_NAME, ObjectAction.CREATE.getActionCode(), createFuncRoles);

        //出库单作废权限
        List<String> invalidFuncRoles = serviceFacade.getHavePrivilegeRolesByActionCode(superAdmin, DeliveryNoteObjConstants.API_NAME, ObjectAction.INVALID.getActionCode());
        serviceFacade.rolesAddFuncAccess(superAdmin, OutboundDeliveryNoteConstants.API_NAME, ObjectAction.INVALID.getActionCode(), invalidFuncRoles);

        //出库单产品新建
        serviceFacade.rolesAddFuncAccess(superAdmin, OutboundDeliveryNoteProductConstants.API_NAME, ObjectAction.CREATE.getActionCode(), createFuncRoles);

        //出库单产品没有作废权限
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



    private DescribeResult createOutboundDeliveryNoteDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateOutboundDeliveryNoteDraft(tenantId, userId);
        ILayout detailLayout = generateOutboundDeliveryNoteDetailLayout(tenantId, userId);
        ILayout listLayout = generateOutboundDeliveryNoteListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createOutboundDeliveryNoteDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    //创建出库单
    private IObjectDescribe generateOutboundDeliveryNoteDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        //入库单编号  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.Field.Name.apiName).label(OutboundDeliveryNoteConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("ODN{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //出库日期
        DateFieldDescribe dateFieldDescribe = DateFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.Field.Outbound_Date.apiName).label(OutboundDeliveryNoteConstants.Field.Outbound_Date.label).required(true).unique(false).format("yyyy-MM-dd").build();
        fieldDescribeList.add(dateFieldDescribe);

        //所属仓库
        ObjectReferenceFieldDescribe warehouseObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.Field.Warehouse.apiName).label(OutboundDeliveryNoteConstants.Field.Warehouse.label).targetApiName(WAREHOUSE_API_NAME).targetRelatedListLabel(OutboundDeliveryNoteConstants.Field.Warehouse.targetRelatedListLabel).targetRelatedListName(OutboundDeliveryNoteConstants.Field.Warehouse.targetRelatedListName).unique(false).required(true).wheres(StockUtil.getEnableWarehouseWheres()).build();
        fieldDescribeList.add(warehouseObjectReferenceFieldDescribe);

        //出库类型
        List<ISelectOption> typeSelectOptions = Arrays.stream(OutboundTypeEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe selectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.Field.Outbound_Type.apiName).label(OutboundDeliveryNoteConstants.Field.Outbound_Type.label).required(true).selectOptions(typeSelectOptions).build();
        fieldDescribeList.add(selectOneFieldDescribe);

        //发货单编号
        ObjectReferenceFieldDescribe deliveryNoteObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName).label(OutboundDeliveryNoteConstants.Field.Delivery_Note.label).targetApiName(DELIVERY_NOTE_API_NAME).targetRelatedListLabel(OutboundDeliveryNoteConstants.Field.Delivery_Note.targetRelatedListLabel).targetRelatedListName(OutboundDeliveryNoteConstants.Field.Delivery_Note.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(deliveryNoteObjectReferenceFieldDescribe);

        //调拨单编号
        ObjectReferenceFieldDescribe requisitionNoteObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName).label(OutboundDeliveryNoteConstants.Field.Requisition_Note.label).targetApiName(REQUISITION_API_NAME).targetRelatedListLabel(OutboundDeliveryNoteConstants.Field.Requisition_Note.targetRelatedListLabel).targetRelatedListName(OutboundDeliveryNoteConstants.Field.Requisition_Note.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(requisitionNoteObjectReferenceFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.Field.Remark.apiName).label(OutboundDeliveryNoteConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        List<IRecordTypeOption> recordTypeOptions = Arrays.stream(OutboundDeliveryNoteRecordTypeEnum.values()).map(recordType -> RecordTypeOptionBuilder.builder().apiName(recordType.apiName).label(recordType.label).build()).collect(Collectors.toList());
        recordTypeOptions.forEach(recordTypeOption -> recordTypeOption.set(CONFIG, ObjectUtil.buildFieldOptionConfigMap()));
        RecordTypeFieldDescribe recordTypeSelectOneFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeSelectOneFieldDescribe);

        return ObjectDescribeBuilder.builder().apiName(OutboundDeliveryNoteConstants.API_NAME).displayName(OutboundDeliveryNoteConstants.DISPLAY_NAME).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(OutboundDeliveryNoteConstants.STORE_TABLE_NAME).iconIndex(OutboundDeliveryNoteConstants.ICON_INDEX).build();
    }

    private ILayout generateOutboundDeliveryNoteDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteConstants.Field.Name.apiName).readOnly(false).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteConstants.Field.Outbound_Date.apiName).readOnly(false).renderType(SystemConstants.RenderType.Date.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteConstants.Field.Warehouse.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteConstants.Field.Outbound_Type.apiName).readOnly(false).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName).readOnly(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).build());

        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteConstants.Field.Remark.apiName).readOnly(false).renderType(SystemConstants.RenderType.LongText.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).renderType(SystemConstants.RenderType.Employee.renderType).required(false).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);
        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(userId).tenantId(tenantId).name(OutboundDeliveryNoteConstants.DETAIL_LAYOUT_API_NAME).displayName(OutboundDeliveryNoteConstants.DETAIL_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(OutboundDeliveryNoteConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    private ILayout generateOutboundDeliveryNoteListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteConstants.Field.Name.apiName).lableName(OutboundDeliveryNoteConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteConstants.Field.Outbound_Date.apiName).lableName(OutboundDeliveryNoteConstants.Field.Outbound_Date.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteConstants.Field.Outbound_Type.apiName).lableName(OutboundDeliveryNoteConstants.Field.Outbound_Type.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteConstants.Field.Warehouse.apiName).lableName(OutboundDeliveryNoteConstants.Field.Warehouse.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName).lableName(OutboundDeliveryNoteConstants.Field.Delivery_Note.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName).lableName(OutboundDeliveryNoteConstants.Field.Requisition_Note.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());

        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(OutboundDeliveryNoteConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(OutboundDeliveryNoteConstants.LIST_LAYOUT_API_NAME).refObjectApiName(OutboundDeliveryNoteConstants.API_NAME).displayName(OutboundDeliveryNoteConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();
    }

    //创建出库单产品
    private DescribeResult createOutboundDeliveryNoteProductDescribeAndLayout(String tenantId, String userId) throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = generateOutboundDeliveryNoteProductDescribeDraft(tenantId, userId);
        ILayout detailLayout = generateOutboundDeliveryNoteProductDetailLayout(tenantId, userId);
        ILayout listLayout = generateOutboundDeliveryNoteProductListLayout(tenantId, userId);
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, userId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createGoodsReceivedNoteProductDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    private IObjectDescribe generateOutboundDeliveryNoteProductDescribeDraft(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        //出库单产品ID  主属性
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteProductConstants.Field.Name.apiName).label(OutboundDeliveryNoteProductConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("ODNP{yyyy}-{mm}-{dd}_").postfix("").unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        //出库单编号
        MasterDetailFieldDescribe outboundDeliveryNoteMasterDetailFieldDescribe = MasterDetailFieldDescribeBuilder.builder().isCreateWhenMasterCreate(true).isRequiredWhenMasterCreate(false).apiName(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName).label(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.label).index(true).required(true).targetApiName(OutboundDeliveryNoteConstants.API_NAME).unique(false)
                .targetRelatedListName(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.targetRelatedListName).targetRelatedListLabel(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.targetRelatedListLabel).build();
        fieldDescribeList.add(outboundDeliveryNoteMasterDetailFieldDescribe);

        //产品名称
        ObjectReferenceFieldDescribe productObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder()
                .apiName(OutboundDeliveryNoteProductConstants.Field.Product.apiName)
                .label(OutboundDeliveryNoteProductConstants.Field.Product.label)
                .targetApiName(Utils.PRODUCT_API_NAME)
                .targetRelatedListLabel(OutboundDeliveryNoteProductConstants.Field.Product.targetRelatedListLabel)
                .targetRelatedListName(OutboundDeliveryNoteProductConstants.Field.Product.targetRelatedListName)
                .unique(false).required(false).build();
        fieldDescribeList.add(productObjectReferenceFieldDescribe);

        //规格
        QuoteFieldDescribe specificationQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteProductConstants.Field.Specs.apiName).label(OutboundDeliveryNoteProductConstants.Field.Specs.label).unique(false).required(false).quoteField(OutboundDeliveryNoteProductConstants.Field.Product.apiName.concat("__r.product_spec")).quoteFieldType("text").build();
        fieldDescribeList.add(specificationQuoteFieldDescribe);

        //单位
        QuoteFieldDescribe unitQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteProductConstants.Field.Unit.apiName).label(OutboundDeliveryNoteProductConstants.Field.Unit.label).unique(false).required(false).quoteField(OutboundDeliveryNoteProductConstants.Field.Product.apiName.concat("__r.unit")).quoteFieldType("select_one").build();
        fieldDescribeList.add(unitQuoteFieldDescribe);

        //出库数量
        NumberFieldDescribe amountNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName).label(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.label).length(12).maxLength(14).required(true).roundMode(4).decimalPalces(2).build();
        fieldDescribeList.add(amountNumberFieldDescribe);

        //库存
        ObjectReferenceFieldDescribe stockObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder()
                .apiName(OutboundDeliveryNoteProductConstants.Field.Stock.apiName)
                .label(OutboundDeliveryNoteProductConstants.Field.Stock.label)
                .targetApiName(STOCK_API_NAME)
                .targetRelatedListLabel(OutboundDeliveryNoteProductConstants.Field.Stock.targetRelatedListLabel)
                .targetRelatedListName(OutboundDeliveryNoteProductConstants.Field.Stock.targetRelatedListName)
                .unique(false).required(false).build();
        fieldDescribeList.add(stockObjectReferenceFieldDescribe);

        //可用库存
        QuoteFieldDescribe availableStockQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteProductConstants.Field.Available_stock.apiName).label(OutboundDeliveryNoteProductConstants.Field.Available_stock.label).unique(false).required(false).quoteField(OutboundDeliveryNoteProductConstants.Field.Stock.apiName.concat("__r.available_stock")).quoteFieldType("number").build();
        fieldDescribeList.add(availableStockQuoteFieldDescribe);

        //备注
        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(OutboundDeliveryNoteProductConstants.Field.Remark.apiName).label(OutboundDeliveryNoteProductConstants.Field.Remark.label).build();
        fieldDescribeList.add(longTextFieldDescribe);

        Map<String, Object> configMap = ObjectUtil.buildConfigMap();
        return ObjectDescribeBuilder.builder().apiName(OutboundDeliveryNoteProductConstants.API_NAME).displayName(OutboundDeliveryNoteProductConstants.DISPLAY_NAME).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(OutboundDeliveryNoteProductConstants.STORE_TABLE_NAME).config(configMap).iconIndex(OutboundDeliveryNoteProductConstants.ICON_INDEX).build();
    }

    private ILayout generateOutboundDeliveryNoteProductDetailLayout(String tenantId, String userId) throws MetadataServiceException {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Name.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Product.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Specs.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Unit.apiName).readOnly(true).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Stock.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Available_stock.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(OutboundDeliveryNoteProductConstants.Field.Remark.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.LongText.renderType).build());

        formFields.add(FormFieldBuilder.builder().fieldName(LayoutConstants.OWNER_API_NAME).readOnly(false).required(false).renderType(SystemConstants.RenderType.Employee.renderType).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(userId).displayName(OutboundDeliveryNoteProductConstants.DETAIL_LAYOUT_DISPLAY_NAME).name(OutboundDeliveryNoteProductConstants.DETAIL_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(OutboundDeliveryNoteProductConstants.API_NAME).components(components).build();
    }

    private ILayout generateOutboundDeliveryNoteProductListLayout(String tenantId, String userId) throws MetadataServiceException {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Name.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Product.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Specs.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Specs.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Unit.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Stock.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Stock.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Available_stock.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Available_stock.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(OutboundDeliveryNoteProductConstants.Field.Remark.apiName).lableName(OutboundDeliveryNoteProductConstants.Field.Remark.label).renderType(SystemConstants.RenderType.LongText.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(OutboundDeliveryNoteProductConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(OutboundDeliveryNoteProductConstants.LIST_LAYOUT_API_NAME).refObjectApiName(OutboundDeliveryNoteProductConstants.API_NAME).displayName(OutboundDeliveryNoteProductConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(userId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();
    }
}
