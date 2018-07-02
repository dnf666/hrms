package com.facishare.crm.promotion.predefine.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.AutoNumberFieldDescribeBuilder;
import com.facishare.crm.describebuilder.BooleanFieldDescribeBuilder;
import com.facishare.crm.describebuilder.CurrencyFieldDescribeBuilder;
import com.facishare.crm.describebuilder.DateFieldDescribeBuilder;
import com.facishare.crm.describebuilder.FieldSectionBuilder;
import com.facishare.crm.describebuilder.FormComponentBuilder;
import com.facishare.crm.describebuilder.FormFieldBuilder;
import com.facishare.crm.describebuilder.ImageFieldDescribeBuilder;
import com.facishare.crm.describebuilder.LayoutBuilder;
import com.facishare.crm.describebuilder.MasterDetailFieldDescribeBuilder;
import com.facishare.crm.describebuilder.NumberFieldDescribeBuilder;
import com.facishare.crm.describebuilder.ObjectDescribeBuilder;
import com.facishare.crm.describebuilder.ObjectReferenceFieldDescribeBuilder;
import com.facishare.crm.describebuilder.PercentileFieldDescribeBuilder;
import com.facishare.crm.describebuilder.QuoteFieldDescribeBuilder;
import com.facishare.crm.describebuilder.RecordTypeFieldDescribeBuilder;
import com.facishare.crm.describebuilder.RecordTypeOptionBuilder;
import com.facishare.crm.describebuilder.SelectOneFieldDescribeBuilder;
import com.facishare.crm.describebuilder.SelectOptionBuilder;
import com.facishare.crm.describebuilder.TableColumnBuilder;
import com.facishare.crm.describebuilder.TableComponentBuilder;
import com.facishare.crm.describebuilder.TextFieldDescribeBuilder;
import com.facishare.crm.describebuilder.UrlFieldDescribeBuilder;
import com.facishare.crm.describebuilder.UseScopeFieldDescribeBuilder;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.promotion.constants.AdvertisementConstants;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.AdvertisementStatusEnum;
import com.facishare.crm.promotion.enums.GiftTypeEnum;
import com.facishare.crm.promotion.enums.JumpTypeEnum;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.crm.promotion.enums.PromotionRuleRecordTypeEnum;
import com.facishare.crm.promotion.enums.PromotionTypeEnum;
import com.facishare.crm.promotion.exception.PromotionBusinessException;
import com.facishare.crm.promotion.exception.PromotionErrorCode;
import com.facishare.crm.promotion.predefine.service.PromotionInitService;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.RecordTypeAuthProxy;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.appframework.metadata.dto.RuleResult;
import com.facishare.paas.appframework.metadata.dto.auth.AddRoleRecordTypeModel;
import com.facishare.paas.appframework.metadata.dto.auth.AddRoleViewModel;
import com.facishare.paas.appframework.metadata.dto.auth.RecordTypePojo;
import com.facishare.paas.appframework.metadata.dto.auth.RoleInfoModel;
import com.facishare.paas.appframework.metadata.dto.auth.RoleInfoPojo;
import com.facishare.paas.appframework.metadata.dto.auth.RoleViewPojo;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.IRule;
import com.facishare.paas.metadata.impl.LayoutRuleInfo;
import com.facishare.paas.metadata.impl.Rule;
import com.facishare.paas.metadata.impl.describe.AutoNumberFieldDescribe;
import com.facishare.paas.metadata.impl.describe.BooleanFieldDescribe;
import com.facishare.paas.metadata.impl.describe.CurrencyFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DateFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ImageFieldDescribe;
import com.facishare.paas.metadata.impl.describe.MasterDetailFieldDescribe;
import com.facishare.paas.metadata.impl.describe.NumberFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.describe.PercentileFieldDescribe;
import com.facishare.paas.metadata.impl.describe.QuoteFieldDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.TextFieldDescribe;
import com.facishare.paas.metadata.impl.describe.URLFieldDescribe;
import com.facishare.paas.metadata.impl.describe.UseScopeFieldDescribe;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PromotionInitServiceImpl implements PromotionInitService {
    @Autowired
    private DescribeLogicService describeLogicService;
    @Autowired
    private RecordTypeAuthProxy recordTypeAuthApi;
    @Autowired
    private ServiceFacade serviceFacade;

    @Override
    public boolean init(User user) {
        Set<String> apiNames = Sets.newHashSet(PromotionConstants.API_NAME, PromotionRuleConstants.API_NAME, PromotionProductConstants.API_NAME, AdvertisementConstants.API_NAME);
        Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(user.getTenantId(), apiNames);
        log.info("findByApiNames,user:{},apiNames:{},result:{}", user, JsonUtil.toJson(apiNames), describeMap);
        RoleInfoModel.Result result = roleInfo(user);
        if (!result.isSuccess()) {
            log.warn("getRoleInfo error,user:{},result:{}", user, result);
            throw new PromotionBusinessException(PromotionErrorCode.QUERY_ROLE_ERROR, "角色查询异常");
        }
        if (!describeMap.containsKey(PromotionConstants.API_NAME)) {
            IObjectDescribe promotionDescribeDraft = generatePromotionDescsribeDraft(user.getTenantId(), user.getUserId());
            ILayout defaultLayout = generatePromotionDefaultLayout(user.getTenantId(), user.getUserId());
            ILayout listLayout = generatePromotionListLayout(user.getTenantId(), user.getUserId());
            DescribeResult describeResult = describeLogicService.createDescribe(user, promotionDescribeDraft.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
            log.info("user:{},PromotionObj describeResult:{}", user, describeResult.toString());
            //业务类型
            initProductRecordType(user, PromotionConstants.API_NAME, PromotionRecordTypeEnum.ProductPromotion.apiName, PromotionConstants.DEFAULT_LAYOUT_API_NAME, result.getResult().getRoles());
            //校验规则
            initPromotionRule(user);
        }
        if (!describeMap.containsKey(PromotionProductConstants.API_NAME)) {
            IObjectDescribe promotionDescribeDraft = generatePromotionProductDescribeDraft(user.getTenantId(), user.getUserId());
            ILayout defaultLayout = generatePromotionProductDefaultLayout(user.getTenantId(), user.getUserId());
            ILayout listLayout = generatePromotionProductListLayout(user.getTenantId(), user.getUserId());
            DescribeResult describeResult = describeLogicService.createDescribe(user, promotionDescribeDraft.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
            log.info("user:{},PromotionProductObj describeResult:{}", user, describeResult.toString());
        }
        if (!describeMap.containsKey(PromotionRuleConstants.API_NAME)) {
            IObjectDescribe promotionDescribeDraft = generatePromotionRuleDescribeDraft(user.getTenantId(), user.getUserId());
            ILayout defaultLayout = generatePromotionRuleDefaultLayout(user.getTenantId(), user.getUserId());
            ILayout listLayout = generatePromotionRuleListLayout(user.getTenantId(), user.getUserId());
            DescribeResult describeResult = describeLogicService.createDescribe(user, promotionDescribeDraft.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
            log.info("user:{},PromotionRuleObj describeResult:{}", user, describeResult.toString());
            //业务类型
            initProductRecordType(user, PromotionRuleConstants.API_NAME, PromotionRuleRecordTypeEnum.ProductPromotion.apiName, PromotionRuleConstants.DEFAULT_LAYOUT_API_NAME, result.getResult().getRoles());
        }
        if (!describeMap.containsKey(AdvertisementConstants.API_NAME)) {
            initAdvertisement(user);
        }

        return true;
    }

    @Override
    public DescribeResult initAdvertisement(User user) {
        IObjectDescribe advertisementDescribe = generateAdvertisementDescribe(user.getTenantId(), user.getUserId());
        ILayout defaultLayout = generateAdvertisementDefaultLayout(user.getTenantId(), user.getUserId());
        ILayout listLayout = generateAdvertisementListLayout(user.getTenantId(), user.getUserId());
        DescribeResult describeResult = describeLogicService.createDescribe(user, advertisementDescribe.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
        log.info("user:{},advertisement describeResult:{}", user, describeResult);
        try {
            initAdvertisementLayoutRule(user);
        } catch (Exception e) {
            log.warn("init advertisement layoutrule fail,user:{}", user, e);
        }
        return describeResult;
    }

    @Override
    public void initAdvertisementLayoutRule(User user) {
        Map<String, Object> layoutRuleMap = Maps.newHashMap();
        layoutRuleMap.put("api_name", "advertisement_default_layout_rule__c");
        layoutRuleMap.put("description", "");
        layoutRuleMap.put("label", "预设布局规则");
        layoutRuleMap.put("layout_api_name", AdvertisementConstants.DEFAULT_LAYOUT_API_NAME);
        layoutRuleMap.put("main_field", AdvertisementConstants.Field.JumpType.apiName);
        List<Map<String, Object>> mainFieldBranches = Lists.newArrayList();
        mainFieldBranches.add(getJumpTypeBranch(JumpTypeEnum.PromotionDetail.value, Lists.newArrayList(AdvertisementConstants.Field.Promotion.apiName), Lists.newArrayList(AdvertisementConstants.Field.Promotion.apiName)));
        mainFieldBranches.add(getJumpTypeBranch(JumpTypeEnum.ProductDetail.value, Lists.newArrayList(AdvertisementConstants.Field.Product.apiName), Lists.newArrayList(AdvertisementConstants.Field.Product.apiName)));
        mainFieldBranches.add(getJumpTypeBranch(JumpTypeEnum.ExternalLink.value, Lists.newArrayList(AdvertisementConstants.Field.JumpAddress.apiName), Lists.newArrayList(AdvertisementConstants.Field.JumpAddress.apiName)));
        layoutRuleMap.put("main_field_branches", mainFieldBranches);
        layoutRuleMap.put("object_describe_api_name", AdvertisementConstants.API_NAME);
        LayoutRuleInfo layoutRuleInfo = new LayoutRuleInfo(layoutRuleMap);
        serviceFacade.createLayoutRule(user, layoutRuleInfo);
    }

    private Map<String, Object> getJumpTypeBranch(String value, List<String> requiredField, List<String> showField) {
        Map<String, Object> jumpTypeBranch = Maps.newHashMap();
        Map<String, Object> branchMap = Maps.newHashMap();
        Map<String, Object> branchResultMap = Maps.newHashMap();
        branchResultMap.put("required_field", requiredField.stream().map(field -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("field_api_name", field);
            return map;
        }).collect(Collectors.toList()));
        branchResultMap.put("show_field", showField.stream().map(field -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("field_api_name", field);
            return map;
        }).collect(Collectors.toList()));
        branchMap.put("conditions", Lists.newArrayList());
        branchMap.put("result", branchResultMap);
        List<Map<String, Object>> branches = Lists.newArrayList();
        branches.add(branchMap);
        jumpTypeBranch.put("branches", branches);
        Map<String, Object> mainFieldFilter = Maps.newHashMap();
        mainFieldFilter.put("field_name", AdvertisementConstants.Field.JumpType.apiName);
        mainFieldFilter.put("field_values", Lists.newArrayList(value));
        mainFieldFilter.put("operator", Operator.EQ);
        mainFieldFilter.put("value_type", 0);
        jumpTypeBranch.put("main_field_filter", mainFieldFilter);
        return jumpTypeBranch;
    }

    private void initPromotionRule(User user) {
        IRule rule = new Rule();
        rule.setApiName(PromotionConstants.START_END_TIME_RULE_API_NAME);
        rule.setDescribeApiName(PromotionConstants.API_NAME);
        rule.setRuleName(PromotionConstants.START_END_TIME_RULE_DISPLAY_NAME);
        rule.setCondition(String.format("$%s$>$%s$", PromotionConstants.Field.StartTime.apiName, PromotionConstants.Field.EndTime.apiName));
        rule.setIsActive(true);
        rule.setTenantId(user.getTenantId());
        rule.setCreatedBy(user.getUserId());
        long curTime = new Date().getTime();
        rule.setCreateTime(curTime);
        rule.setLastModifiedBy(user.getUserId());
        rule.setLastModifiedTime(curTime);
        rule.setDescription(PromotionConstants.START_END_TIME_RULE_DESCRIPTION);
        rule.setMessage(PromotionConstants.START_END_TIME_RULE_DESCRIPTION);
        rule.setDefaultToZero(true);
        rule.setScene(Lists.newArrayList("create", "update"));
        try {
            RuleResult result = serviceFacade.create(rule);
            if (!result.isSuccess()) {
                log.warn("initPromotionRule user:{},rule:{},result:{} ", user, rule, result);
            }
        } catch (MetadataServiceException | CrmCheckedException e) {
            log.warn("initPromotionRule user:{},rule:{}", user, rule, e);
        }
    }

    @Override
    public void initProductRecordType(User user, String objectApiName, String recordTypeId, String viewId, List<RoleInfoPojo> roleInfoPojos) {
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

    private RoleInfoModel.Result roleInfo(User user) {
        RoleInfoModel.Arg roleInfoModelArg = new RoleInfoModel.Arg();
        roleInfoModelArg.setAuthContext(user);
        RoleInfoModel.Result result = recordTypeAuthApi.roleInfo(roleInfoModelArg);
        return result;
    }

    private IObjectDescribe generatePromotionDescsribeDraft(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        TextFieldDescribe nameFieldDescribe = TextFieldDescribeBuilder.builder().apiName(PromotionConstants.Field.Name.apiName).label(PromotionConstants.Field.Name.label).maxLength(1000).build();
        fieldDescribeList.add(nameFieldDescribe);

        DateFieldDescribe startTimeFieldDescribe = DateFieldDescribeBuilder.builder().apiName(PromotionConstants.Field.StartTime.apiName).label(PromotionConstants.Field.StartTime.label).index(true).required(true).unique(false).build();
        fieldDescribeList.add(startTimeFieldDescribe);

        DateFieldDescribe endTimeFieldDescribe = DateFieldDescribeBuilder.builder().apiName(PromotionConstants.Field.EndTime.apiName).label(PromotionConstants.Field.EndTime.label).index(true).required(true).unique(false).build();
        fieldDescribeList.add(endTimeFieldDescribe);

        ImageFieldDescribe imageFieldDescribe = ImageFieldDescribeBuilder.builder().apiName(PromotionConstants.Field.Images.apiName).label(PromotionConstants.Field.Images.label).fileAmountLimit(1).required(false).build();
        fieldDescribeList.add(imageFieldDescribe);

        BooleanFieldDescribe statusBooleanFieldDescribe = BooleanFieldDescribeBuilder.builder().apiName(PromotionConstants.Field.Status.apiName).label(PromotionConstants.Field.Status.label).defaultValue(true).required(true).unique(false).index(true).build();
        fieldDescribeList.add(statusBooleanFieldDescribe);

        List<ISelectOption> typeSelectOptions = Arrays.stream(PromotionTypeEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe typeSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(PromotionConstants.Field.Type.apiName).label(PromotionConstants.Field.Type.label).selectOptions(typeSelectOptions).required(true).build();
        fieldDescribeList.add(typeSelectOneFieldDescribe);

        UseScopeFieldDescribe customerRangeUseScopeFieldDescribe = UseScopeFieldDescribeBuilder.builder().apiName(PromotionConstants.Field.CustomerRange.apiName).label(PromotionConstants.Field.CustomerRange.label).targetApiName(Utils.ACCOUNT_API_NAME).defaultValue(PromotionConstants.customerRangeDefaultValue).expressionType(PromotionConstants.expressionType).build();
        fieldDescribeList.add(customerRangeUseScopeFieldDescribe);

        List<IRecordTypeOption> recordTypeOptions = Arrays.stream(PromotionRecordTypeEnum.values()).map(recordType -> RecordTypeOptionBuilder.builder().apiName(recordType.apiName).label(recordType.label).build()).collect(Collectors.toList());
        RecordTypeFieldDescribe recordTypeSelectOneFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeSelectOneFieldDescribe);

        Map<String, Object> config = getDisableRecordTypeLayoutConfig();
        return ObjectDescribeBuilder.builder().apiName(PromotionConstants.API_NAME).displayName(PromotionConstants.DISPLAY_NAME).tenantId(tenantId).createBy(fsUserId).fieldDescribes(fieldDescribeList).storeTableName(PromotionConstants.STORE_TABLE_NAME).iconIndex(PromotionConstants.ICON_INDEX).config(config).build();
    }

    private ILayout generatePromotionDefaultLayout(String tenantId, String fsUserId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionConstants.Field.Name.apiName).readOnly(false).renderType(SystemConstants.RenderType.Text.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionConstants.Field.StartTime.apiName).readOnly(false).renderType(SystemConstants.RenderType.Date.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionConstants.Field.EndTime.apiName).readOnly(false).renderType(SystemConstants.RenderType.Date.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionConstants.Field.Status.apiName).readOnly(false).renderType(SystemConstants.RenderType.TrueOrFalse.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.Employee.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionConstants.Field.Images.apiName).readOnly(false).renderType(SystemConstants.RenderType.Image.renderType).required(false).build());
        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();

        List<IFormField> typeFormFields = Lists.newArrayList();
        typeFormFields.add(FormFieldBuilder.builder().fieldName(PromotionConstants.Field.Type.apiName).readOnly(false).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).build());
        FieldSection typeFieldSection = FieldSectionBuilder.builder().header(PromotionConstants.Field.Type.label).showHeader(true).name(PromotionConstants.TYPE_SECTION_API_NAME).fields(typeFormFields).build();

        List<IFormField> customerRangeFormFields = Lists.newArrayList();
        customerRangeFormFields.add(FormFieldBuilder.builder().fieldName(PromotionConstants.Field.CustomerRange.apiName).readOnly(false).renderType(SystemConstants.RenderType.UseScope.renderType).required(true).build());
        FieldSection customerRangeFieldSection = FieldSectionBuilder.builder().header(PromotionConstants.Field.CustomerRange.label).name(PromotionConstants.CUSTOMER_RANGE_SECTION_API_NAME).showHeader(true).fields(customerRangeFormFields).build();

        fieldSections.add(fieldSection);
        fieldSections.add(typeFieldSection);
        fieldSections.add(customerRangeFieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(fsUserId).tenantId(tenantId).name(PromotionConstants.DEFAULT_LAYOUT_API_NAME).displayName(PromotionConstants.DEFAULT_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(PromotionConstants.API_NAME).components(components).layoutType(SystemConstants.LayoutType.Detail.layoutType).build();
    }

    private ILayout generatePromotionListLayout(String tenantId, String fsUserId) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(PromotionConstants.Field.Name.apiName).lableName(PromotionConstants.Field.Name.label).renderType(SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.RecordType.apiName).lableName(SystemConstants.Field.RecordType.label).renderType(SystemConstants.RenderType.RecordType.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionConstants.Field.Type.apiName).lableName(PromotionConstants.Field.Type.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionConstants.Field.StartTime.apiName).lableName(PromotionConstants.Field.StartTime.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionConstants.Field.EndTime.apiName).lableName(PromotionConstants.Field.EndTime.label).renderType(SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionConstants.Field.Status.apiName).lableName(PromotionConstants.Field.Status.label).renderType(SystemConstants.RenderType.TrueOrFalse.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(PromotionConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().name(PromotionConstants.LIST_LAYOUT_API_NAME).refObjectApiName(PromotionConstants.API_NAME).displayName(PromotionConstants.LIST_LAYOUT_DISPLAY_NAME).tenantId(tenantId).createBy(fsUserId).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).agentType(LayoutConstants.AGENT_TYPE).isShowFieldName(true).components(components).build();
    }

    private IObjectDescribe generatePromotionProductDescribeDraft(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.Name.apiName).label(PromotionProductConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("PP{yyyy}-{mm}-{dd}_").postfix("").required(true).unique(true).index(true).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);
        MasterDetailFieldDescribe promotionMasterDetailFieldDescribe = MasterDetailFieldDescribeBuilder.builder().isCreateWhenMasterCreate(true).isRequiredWhenMasterCreate(false).apiName(PromotionProductConstants.Field.Promotion.apiName).label(PromotionProductConstants.Field.Promotion.label).index(true).required(true).targetApiName(PromotionConstants.API_NAME).unique(false).targetRelatedListName(PromotionProductConstants.Field.Promotion.targetRelatedListName)
                .targetRelatedListLabel(PromotionProductConstants.Field.Promotion.targetRelatedListLabel).build();
        fieldDescribeList.add(promotionMasterDetailFieldDescribe);

        /*List<LinkedHashMap> wheres = Lists.newArrayList();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("connector", Where.CONN.OR);
        List<Map> filters = Lists.newArrayList();
        Map<String, Object> filter = Maps.newHashMap();
        filter.put("field_name", "is_giveaway");
        filter.put("operator", Operator.N.name());
        filter.put("field_values", Lists.newArrayList("1"));//1表示非赠品
        filters.add(filter);
        map.put("filters", filters);
        wheres.add(map);*/
        //6.3 wheres开启的促销 产品不再添加是否赠品字段，去掉促销产品只能选择非赠品的过滤
        ObjectReferenceFieldDescribe productObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.Product.apiName).label(PromotionProductConstants.Field.Product.label).targetApiName(Utils.PRODUCT_API_NAME).targetRelatedListLabel(PromotionProductConstants.Field.Product.targetRelatedListLabel).targetRelatedListName(PromotionProductConstants.Field.Product.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(productObjectReferenceFieldDescribe);

        QuoteFieldDescribe priceQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.Price.apiName).label(PromotionProductConstants.Field.Price.label).unique(false).required(false).quoteField(PromotionProductConstants.Field.Product.apiName.concat("__r.price")).quoteFieldType("currency").build();
        fieldDescribeList.add(priceQuoteFieldDescribe);
        //6.3
        NumberFieldDescribe quotaNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.Quota.apiName).label(PromotionProductConstants.Field.Quota.label).defaultValue("0").decimalPalces(0).length(12).maxLength(14).build();
        fieldDescribeList.add(quotaNumberFieldDescribe);

        QuoteFieldDescribe specificationQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.Specification.apiName).label(PromotionProductConstants.Field.Specification.label).unique(false).required(false).quoteField(PromotionProductConstants.Field.Product.apiName.concat("__r.product_spec")).quoteFieldType("text").build();
        fieldDescribeList.add(specificationQuoteFieldDescribe);

        QuoteFieldDescribe unitQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.Unit.apiName).label(PromotionProductConstants.Field.Unit.label).unique(false).required(false).quoteField(PromotionProductConstants.Field.Product.apiName.concat("__r.unit")).quoteFieldType("select_one").build();
        fieldDescribeList.add(unitQuoteFieldDescribe);

        QuoteFieldDescribe statusQuoteFieldDescribe = QuoteFieldDescribeBuilder.builder().apiName(PromotionProductConstants.Field.ProductStatus.apiName).label(PromotionProductConstants.Field.ProductStatus.label).unique(false).required(false).quoteField(PromotionProductConstants.Field.Product.apiName.concat("__r.product_status")).quoteFieldType("select_one").build();
        fieldDescribeList.add(statusQuoteFieldDescribe);

        Map<String, Object> config = getDisableRecordTypeLayoutConfig();
        return ObjectDescribeBuilder.builder().apiName(PromotionProductConstants.API_NAME).displayName(PromotionProductConstants.DISPLAY_NAME).createBy(fsUserId).tenantId(tenantId).iconIndex(PromotionProductConstants.ICON_INDEX).storeTableName(PromotionProductConstants.STORE_TABLE_NAME).fieldDescribes(fieldDescribeList).config(config).build();
    }

    private ILayout generatePromotionProductDefaultLayout(String tenantId, String fsUserId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(PromotionProductConstants.Field.Name.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionProductConstants.Field.Product.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionProductConstants.Field.ProductStatus.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionProductConstants.Field.Price.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionProductConstants.Field.Specification.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionProductConstants.Field.Unit.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Quote.renderType).build());
        //6.3
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionProductConstants.Field.Quota.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Number.renderType).build());
        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).displayName(PromotionProductConstants.DEFAULT_LAYOUT_DISPLAY_NAME).name(PromotionProductConstants.DEFAULT_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(PromotionProductConstants.API_NAME).components(components).build();
    }

    private ILayout generatePromotionProductListLayout(String tenantId, String fsUserId) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(PromotionProductConstants.Field.Name.apiName).lableName(PromotionProductConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionProductConstants.Field.Promotion.apiName).lableName(PromotionProductConstants.Field.Promotion.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionProductConstants.Field.Product.apiName).lableName(PromotionProductConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionProductConstants.Field.ProductStatus.apiName).lableName(PromotionProductConstants.Field.ProductStatus.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionProductConstants.Field.Specification.apiName).lableName(PromotionProductConstants.Field.Specification.label).renderType(SystemConstants.RenderType.Quote.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionProductConstants.Field.Unit.apiName).lableName(PromotionProductConstants.Field.Unit.label).renderType(SystemConstants.RenderType.Quote.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(PromotionProductConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).refObjectApiName(PromotionProductConstants.API_NAME).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).name(PromotionProductConstants.LIST_LAYOUT_API_NAME).displayName(PromotionProductConstants.LIST_LAYOUT_DISPLAY_NAME).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    private IObjectDescribe generatePromotionRuleDescribeDraft(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.Name.apiName).label(PromotionRuleConstants.Field.Name.label).index(true).unique(true).required(true).prefix("PR{yyyy}-{mm}-{dd}_").postfix("").startNumber(1).serialNumber(4).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        MasterDetailFieldDescribe promotionMasterDetailFieldDescribe = MasterDetailFieldDescribeBuilder.builder().isCreateWhenMasterCreate(true).isRequiredWhenMasterCreate(true).apiName(PromotionRuleConstants.Field.Promotion.apiName).label(PromotionRuleConstants.Field.Promotion.label).index(true).required(true).targetApiName(PromotionConstants.API_NAME).unique(false).targetRelatedListName(PromotionRuleConstants.Field.Promotion.targetRelatedListName)
                .targetRelatedListLabel(PromotionRuleConstants.Field.Promotion.targetRelatedListLabel).build();
        fieldDescribeList.add(promotionMasterDetailFieldDescribe);

        NumberFieldDescribe purchaseNumNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.PurchaseNum.apiName).label(PromotionRuleConstants.Field.PurchaseNum.label).decimalPalces(0).length(12).maxLength(14).build();
        fieldDescribeList.add(purchaseNumNumberFieldDescribe);

        CurrencyFieldDescribe fixedPriceCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.FixedPrice.apiName).label(PromotionRuleConstants.Field.FixedPrice.label).currencyUnit("￥").decimalPlaces(2).length(12).maxLength(14).required(false).roundMode(4).build();
        fieldDescribeList.add(fixedPriceCurrencyFieldDescribe);

        CurrencyFieldDescribe derateMoneyCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.DerateMoney.apiName).label(PromotionRuleConstants.Field.DerateMoney.label).currencyUnit("￥").decimalPlaces(2).length(12).maxLength(14).required(false).roundMode(4).build();
        fieldDescribeList.add(derateMoneyCurrencyFieldDescribe);

        PercentileFieldDescribe priceDiscountPrecentileFieldDescribe = PercentileFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.PriceDiscount.apiName).label(PromotionRuleConstants.Field.PriceDiscount.label).build();
        fieldDescribeList.add(priceDiscountPrecentileFieldDescribe);

        CurrencyFieldDescribe orderMoneyCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.OrderMoney.apiName).label(PromotionRuleConstants.Field.OrderMoney.label).currencyUnit("￥").decimalPlaces(2).length(12).maxLength(14).required(false).roundMode(4).build();
        fieldDescribeList.add(orderMoneyCurrencyFieldDescribe);

        PercentileFieldDescribe orderDiscountPrecentileFieldDescribe = PercentileFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.OrderDiscount.apiName).label(PromotionRuleConstants.Field.OrderDiscount.label).build();
        fieldDescribeList.add(orderDiscountPrecentileFieldDescribe);

        CurrencyFieldDescribe orderDerateMoneyCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.OrderDerateMoney.apiName).label(PromotionRuleConstants.Field.OrderDerateMoney.label).currencyUnit("￥").decimalPlaces(2).length(12).maxLength(14).required(false).roundMode(4).build();
        fieldDescribeList.add(orderDerateMoneyCurrencyFieldDescribe);

        /*List<LinkedHashMap> wheres = Lists.newArrayList();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("connector", Where.CONN.OR);
        List<Map> filters = Lists.newArrayList();
        Map<String, Object> filter = Maps.newHashMap();
        filter.put("field_name", "is_giveaway");
        filter.put("operator", Operator.EQ.name());
        filter.put("field_values", Lists.newArrayList("1"));//1表示是赠品
        filters.add(filter);
        map.put("filters", filters);
        wheres.add(map);*/

        //6.3
        List<ISelectOption> giftTypeSelectOptions = Arrays.stream(GiftTypeEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe giftTypeSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.GiftType.apiName).label(PromotionRuleConstants.Field.GiftType.label).selectOptions(giftTypeSelectOptions).required(false).build();
        fieldDescribeList.add(giftTypeSelectOneFieldDescribe);

        //6.3去掉wheres，赠品可以是非赠品
        ObjectReferenceFieldDescribe giftProductObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.GiftProduct.apiName).label(PromotionRuleConstants.Field.GiftProduct.label).targetApiName(Utils.PRODUCT_API_NAME).targetRelatedListLabel(PromotionRuleConstants.Field.GiftProduct.targetRelatedListLabel).targetRelatedListName(PromotionRuleConstants.Field.GiftProduct.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(giftProductObjectReferenceFieldDescribe);

        NumberFieldDescribe giftProductNumNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(PromotionRuleConstants.Field.GiftProductNum.apiName).label(PromotionRuleConstants.Field.GiftProductNum.label).decimalPalces(0).length(12).maxLength(14).build();
        fieldDescribeList.add(giftProductNumNumberFieldDescribe);

        List<IRecordTypeOption> recordTypeOptions = Arrays.stream(PromotionRuleRecordTypeEnum.values()).map(recordType -> RecordTypeOptionBuilder.builder().apiName(recordType.apiName).label(recordType.label).build()).collect(Collectors.toList());
        RecordTypeFieldDescribe recordTypeSelectOneFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeSelectOneFieldDescribe);

        //禁止添加字段
        //        Map<String, Object> configMap = Maps.newHashMap();
        Map<String, Object> fieldConfigMap = Maps.newHashMap();
        fieldConfigMap.put("add", 0);
        //        configMap.put("fields", fieldConfigMap);
        Map<String, Object> config = getDisableRecordTypeLayoutConfig();
        config.put("fields", fieldConfigMap);
        return ObjectDescribeBuilder.builder().tenantId(tenantId).createBy(fsUserId).apiName(PromotionRuleConstants.API_NAME).displayName(PromotionRuleConstants.DISPLAY_NAME).fieldDescribes(fieldDescribeList).storeTableName(PromotionRuleConstants.STORE_TABLE_NAME).iconIndex(PromotionRuleConstants.ICON_INDEX).config(config).build();
    }

    private ILayout generatePromotionRuleDefaultLayout(String tenantId, String fsUserId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.Name.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.Promotion.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.PurchaseNum.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Number.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.FixedPrice.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Currency.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.DerateMoney.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Currency.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.PriceDiscount.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Percentile.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.OrderMoney.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Currency.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.OrderDiscount.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Percentile.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.OrderDerateMoney.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Currency.renderType).build());
        //6.3
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.GiftType.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.GiftProduct.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(PromotionRuleConstants.Field.GiftProductNum.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Number.renderType).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).displayName(PromotionRuleConstants.DEFAULT_LAYOUT_DISPLAY_NAME).name(PromotionRuleConstants.DEFAULT_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(PromotionRuleConstants.API_NAME).components(components).build();

    }

    private ILayout generatePromotionRuleListLayout(String tenantId, String fsUserId) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.Name.apiName).lableName(PromotionRuleConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.Promotion.apiName).lableName(PromotionRuleConstants.Field.Promotion.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.PurchaseNum.apiName).lableName(PromotionRuleConstants.Field.PurchaseNum.label).renderType(SystemConstants.RenderType.Number.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.FixedPrice.apiName).lableName(PromotionRuleConstants.Field.FixedPrice.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.DerateMoney.apiName).lableName(PromotionRuleConstants.Field.DerateMoney.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.PriceDiscount.apiName).lableName(PromotionRuleConstants.Field.PriceDiscount.label).renderType(SystemConstants.RenderType.Percentile.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.OrderMoney.apiName).lableName(PromotionRuleConstants.Field.OrderMoney.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.OrderDiscount.apiName).lableName(PromotionRuleConstants.Field.OrderDiscount.label).renderType(SystemConstants.RenderType.Percentile.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.OrderDerateMoney.apiName).lableName(PromotionRuleConstants.Field.OrderDerateMoney.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.GiftProduct.apiName).lableName(PromotionRuleConstants.Field.GiftProduct.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PromotionRuleConstants.Field.GiftProductNum.apiName).lableName(PromotionRuleConstants.Field.GiftProductNum.label).renderType(SystemConstants.RenderType.Number.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(PromotionRuleConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).refObjectApiName(PromotionRuleConstants.API_NAME).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).name(PromotionRuleConstants.LIST_LAYOUT_API_NAME).displayName(PromotionRuleConstants.LIST_LAYOUT_DISPLAY_NAME).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    private IObjectDescribe generateAdvertisementDescribe(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        AutoNumberFieldDescribe nameAutoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.Name.apiName).label(AdvertisementConstants.Field.Name.label).index(true).unique(true).required(true).prefix("AD{yyyy}-{mm}-{dd}_").postfix("").startNumber(1).serialNumber(4).build();
        fieldDescribeList.add(nameAutoNumberFieldDescribe);

        Map<String, Object> config = Maps.newHashMap();
        Map<String, Object> attrMap = Maps.newHashMap();
        attrMap.put("is_required", 0);
        attrMap.put("is_readonly", 0);
        attrMap.put("default_value", 1);
        config.put("attrs", attrMap);
        ImageFieldDescribe imageFieldDescribe = ImageFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.AdPictures.apiName).label(AdvertisementConstants.Field.AdPictures.label).config(config).fileAmountLimit(1).required(true).build();
        fieldDescribeList.add(imageFieldDescribe);

        List<ISelectOption> jumpTypeSelectOptions = Arrays.stream(JumpTypeEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe jumpTypeSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.JumpType.apiName).label(AdvertisementConstants.Field.JumpType.label).selectOptions(jumpTypeSelectOptions).required(true).build();
        fieldDescribeList.add(jumpTypeSelectOneFieldDescribe);

        URLFieldDescribe jumpAddressFieldDescribe = UrlFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.JumpAddress.apiName).label(AdvertisementConstants.Field.JumpAddress.label).build();
        fieldDescribeList.add(jumpAddressFieldDescribe);

        ObjectReferenceFieldDescribe productObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.Product.apiName).label(AdvertisementConstants.Field.Product.label).targetApiName(Utils.PRODUCT_API_NAME).targetRelatedListLabel(AdvertisementConstants.Field.Product.targetRelatedListLabel).targetRelatedListName(AdvertisementConstants.Field.Product.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(productObjectReferenceFieldDescribe);

        ObjectReferenceFieldDescribe promotionObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.Promotion.apiName).label(AdvertisementConstants.Field.Promotion.label).targetApiName(PromotionConstants.API_NAME).targetRelatedListLabel(AdvertisementConstants.Field.Promotion.targetRelatedListLabel).targetRelatedListName(AdvertisementConstants.Field.Promotion.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(promotionObjectReferenceFieldDescribe);

        List<ISelectOption> statusSelectOptions = Arrays.stream(AdvertisementStatusEnum.values()).map(typeEnum -> SelectOptionBuilder.builder().value(typeEnum.value).label(typeEnum.label).build()).collect(Collectors.toList());
        SelectOneFieldDescribe statusSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.Status.apiName).label(AdvertisementConstants.Field.Status.label).selectOptions(statusSelectOptions).required(true).build();
        fieldDescribeList.add(statusSelectOneFieldDescribe);

        NumberFieldDescribe sortNumberFieldDescribe = NumberFieldDescribeBuilder.builder().apiName(AdvertisementConstants.Field.Sort.apiName).label(AdvertisementConstants.Field.Sort.label).decimalPalces(0).length(12).maxLength(14).build();
        fieldDescribeList.add(sortNumberFieldDescribe);

        return ObjectDescribeBuilder.builder().apiName(AdvertisementConstants.API_NAME).displayName(AdvertisementConstants.DISPLAY_NAME).tenantId(tenantId).createBy(fsUserId).fieldDescribes(fieldDescribeList).storeTableName(AdvertisementConstants.STORE_TABLE_NAME).iconIndex(AdvertisementConstants.ICON_INDEX).build();
    }

    private ILayout generateAdvertisementDefaultLayout(String tenantId, String fsUserId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();

        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.Name.apiName).readOnly(false).required(true).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.AdPictures.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Image.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.JumpType.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.JumpAddress.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Url.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.Promotion.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.Product.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.Status.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(AdvertisementConstants.Field.Sort.apiName).readOnly(false).required(false).renderType(SystemConstants.RenderType.Number.renderType).build());

        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
        fieldSections.add(fieldSection);

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).displayName(AdvertisementConstants.DEFAULT_LAYOUT_DISPLAY_NAME).name(AdvertisementConstants.DEFAULT_LAYOUT_API_NAME).isDefault(true).layoutType(SystemConstants.LayoutType.Detail.layoutType).refObjectApiName(AdvertisementConstants.API_NAME).components(components).build();
    }

    private ILayout generateAdvertisementListLayout(String tenantId, String fsUserId) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.Name.apiName).lableName(AdvertisementConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        //        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.AdPictures.apiName).lableName(AdvertisementConstants.Field.AdPictures.label).renderType(SystemConstants.RenderType.Image.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.JumpType.apiName).lableName(AdvertisementConstants.Field.JumpType.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.JumpAddress.apiName).lableName(AdvertisementConstants.Field.JumpAddress.label).renderType(SystemConstants.RenderType.Url.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.Promotion.apiName).lableName(AdvertisementConstants.Field.Promotion.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.Product.apiName).lableName(AdvertisementConstants.Field.Product.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.Status.apiName).lableName(AdvertisementConstants.Field.Status.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(AdvertisementConstants.Field.Sort.apiName).lableName(AdvertisementConstants.Field.Sort.label).renderType(SystemConstants.RenderType.Number.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(AdvertisementConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);

        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).refObjectApiName(AdvertisementConstants.API_NAME).layoutType(SystemConstants.LayoutType.List.layoutType).isDefault(false).name(AdvertisementConstants.LIST_LAYOUT_API_NAME).displayName(AdvertisementConstants.LIST_LAYOUT_DISPLAY_NAME).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    private Map<String, Object> getDisableRecordTypeLayoutConfig() {
        Map<String, Object> config = Maps.newHashMap();
        Map<String, Object> layoutConfig = Maps.newHashMap();
        Map<String, Object> recordConfig = Maps.newHashMap();
        layoutConfig.put("add", 0);
        recordConfig.put("add", 0);
        config.put("layout", layoutConfig);
        config.put("record_type", recordConfig);
        return config;
    }
}
