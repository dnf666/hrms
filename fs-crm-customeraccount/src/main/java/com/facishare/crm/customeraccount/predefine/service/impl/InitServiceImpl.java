package com.facishare.crm.customeraccount.predefine.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.PrepayOutcomeTypeEnum;
import com.facishare.crm.customeraccount.enums.RebateIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.SettleTypeEnum;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.service.InitService;
import com.facishare.crm.customeraccount.util.InitUtil;
import com.facishare.crm.describebuilder.AutoNumberFieldDescribeBuilder;
import com.facishare.crm.describebuilder.BooleanFieldDescribeBuilder;
import com.facishare.crm.describebuilder.CurrencyFieldDescribeBuilder;
import com.facishare.crm.describebuilder.DateFieldDescribeBuilder;
import com.facishare.crm.describebuilder.DateTimeFieldDescribeBuilder;
import com.facishare.crm.describebuilder.FieldConfig;
import com.facishare.crm.describebuilder.FieldSectionBuilder;
import com.facishare.crm.describebuilder.FileAttachmentFieldDescribeBuilder;
import com.facishare.crm.describebuilder.FormComponentBuilder;
import com.facishare.crm.describebuilder.FormFieldBuilder;
import com.facishare.crm.describebuilder.LayoutBuilder;
import com.facishare.crm.describebuilder.LongTextFieldDescribeBuilder;
import com.facishare.crm.describebuilder.MasterDetailFieldDescribeBuilder;
import com.facishare.crm.describebuilder.ObjectDescribeBuilder;
import com.facishare.crm.describebuilder.ObjectReferenceFieldDescribeBuilder;
import com.facishare.crm.describebuilder.OptionConfig;
import com.facishare.crm.describebuilder.PercentileFieldDescribeBuilder;
import com.facishare.crm.describebuilder.RecordTypeFieldDescribeBuilder;
import com.facishare.crm.describebuilder.SelectManyFieldDescribeBuilder;
import com.facishare.crm.describebuilder.SelectOneFieldDescribeBuilder;
import com.facishare.crm.describebuilder.TableColumnBuilder;
import com.facishare.crm.describebuilder.TableComponentBuilder;
import com.facishare.crm.describebuilder.TextFieldDescribeBuilder;
import com.facishare.crm.describebuilder.UseScopeFieldDescribeBuilder;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.rest.ApprovalInitProxy;
import com.facishare.crm.rest.dto.ApprovalInitModel;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.LayoutLogicService;
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
import com.facishare.paas.metadata.impl.Rule;
import com.facishare.paas.metadata.impl.describe.AutoNumberFieldDescribe;
import com.facishare.paas.metadata.impl.describe.BooleanFieldDescribe;
import com.facishare.paas.metadata.impl.describe.CurrencyFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DateFieldDescribe;
import com.facishare.paas.metadata.impl.describe.DateTimeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.FileAttachmentFieldDescribe;
import com.facishare.paas.metadata.impl.describe.LongTextFieldDescribe;
import com.facishare.paas.metadata.impl.describe.MasterDetailFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.describe.PercentileFieldDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeOption;
import com.facishare.paas.metadata.impl.describe.SelectManyFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOption;
import com.facishare.paas.metadata.impl.describe.TextFieldDescribe;
import com.facishare.paas.metadata.impl.describe.UseScopeFieldDescribe;
import com.facishare.paas.metadata.impl.ui.layout.FieldSection;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InitServiceImpl implements InitService {
    @Autowired
    private RecordTypeAuthProxy recordTypeAuthApi;
    @Autowired
    private DescribeLogicService describeLogicService;
    @Autowired
    private LayoutLogicService layoutLogicService;
    @Autowired
    private ApprovalInitProxy approvalInitProxy;
    @Autowired
    private ServiceFacade serviceFacade;

    //回款财务
    private String paymentFinacailRole = "00000000000000000000000000000002";
    //销售人员
    private String salesRole = "00000000000000000000000000000015";
    //CRM管理员
    private String crmManagerRole = "00000000000000000000000000000006";

    @Override
    public void initPrepayLayoutRecordType(ServiceContext serviceContext) {
        RoleInfoModel.Arg roleInfoModelArg = new RoleInfoModel.Arg();
        roleInfoModelArg.setAuthContext(serviceContext.getUser());
        RoleInfoModel.Result roleInfoModelResult = roleInfo(roleInfoModelArg);
        List<RoleInfoPojo> roleInfoPojos = roleInfoModelResult.getResult().getRoles();

        initAssignRecord(serviceContext, roleInfoPojos, PrepayDetailConstants.API_NAME, Lists.newArrayList("income_record_type__c"), "default__c");
        List<RecordViewVo> prepayTransactionDetailRecordViewVos = Lists.newArrayList();
        prepayTransactionDetailRecordViewVos.add(new RecordViewVo("default__c", PrepayDetailConstants.OUTCOME_LAYOUT_API_NAME));
        prepayTransactionDetailRecordViewVos.add(new RecordViewVo("income_record_type__c", PrepayDetailConstants.INCOME_LAYOUT_API_NAME));
        initAssignLayout(serviceContext, roleInfoPojos, PrepayDetailConstants.API_NAME, prepayTransactionDetailRecordViewVos);
        log.info("");
    }

    @Override
    public boolean initStartAndEndTimeRule(User user, String objectApiName, String ruleApiName, String ruleDisplayName, String startTimeFieldApiName, String endTimeFieldApiName, String ruleDescription) throws CrmCheckedException, MetadataServiceException {
        IRule rule = new Rule();
        rule.setApiName(ruleApiName);
        rule.setDescribeApiName(objectApiName);
        rule.setRuleName(ruleDisplayName);
        rule.setCondition(String.format("$%s$>$%s$", startTimeFieldApiName, endTimeFieldApiName));
        rule.setIsActive(true);
        rule.setTenantId(user.getTenantId());
        rule.setCreatedBy(user.getUserId());
        rule.setCreateTime(System.currentTimeMillis());
        rule.setLastModifiedBy(user.getUserId());
        rule.setLastModifiedTime(System.currentTimeMillis());
        rule.setDescription(ruleDescription);
        rule.setMessage(ruleDescription);
        rule.setDefaultToZero(true);
        rule.setScene(Lists.newArrayList("create", "update"));
        RuleResult result = serviceFacade.create(rule);
        log.debug("initRebateIncomeRule user:{},rule:{},result:{} ", user, rule, result);
        return result.isSuccess();
    }

    @Override
    public Boolean init(ServiceContext serviceContext) {
        String tenantId = serviceContext.getTenantId();
        String fsUserId = serviceContext.getUser().getUserId();
        boolean containsAll = true;
        try {
            Set<String> apiNames = Sets.newHashSet(CustomerAccountConstants.API_NAME, PrepayDetailConstants.API_NAME, RebateIncomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.API_NAME, RebateUseRuleConstants.API_NAME);
            Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);
            if (!describeMap.containsKey(CustomerAccountConstants.API_NAME)) {
                createCustomerAccountDescribeAndLayout(tenantId, fsUserId);
                containsAll = false;
            }
            if (!describeMap.containsKey(PrepayDetailConstants.API_NAME)) {
                createPrepayDetailDescribeAndLayout(tenantId, fsUserId);
                containsAll = false;
            }
            //返利使用规则必须在
            if (!describeMap.containsKey(RebateUseRuleConstants.API_NAME)) {
                initRebateUseRule(new User(tenantId, fsUserId));
                containsAll = false;
            }
            if (!describeMap.containsKey(RebateIncomeDetailConstants.API_NAME)) {
                createRebateIncomeDetailDescribeAndLayout(tenantId, fsUserId);
                containsAll = false;
            }
            if (!describeMap.containsKey(RebateOutcomeDetailConstants.API_NAME)) {
                createRebateOutcomeDetailDescribeAndLayout(tenantId, fsUserId);
                containsAll = false;
            }
        } catch (Exception e) {
            log.warn("InitDescribe error,user:{}", serviceContext.getUser(), e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.DESCRIBE_INIT_ERROR, e.getMessage());
        }
        RoleInfoModel.Arg roleInfoModelArg = new RoleInfoModel.Arg();
        roleInfoModelArg.setAuthContext(serviceContext.getUser());
        RoleInfoModel.Result roleInfoModelResult = roleInfo(roleInfoModelArg);
        if (roleInfoModelResult.isSuccess()) {
            List<RoleInfoPojo> roleInfoPojos = roleInfoModelResult.getResult().getRoles();
            initAssignRecord(serviceContext, roleInfoPojos, PrepayDetailConstants.API_NAME, Lists.newArrayList("income_record_type__c"), "default__c");
            List<RecordViewVo> prepayTransactionDetailRecordViewVos = Lists.newArrayList();
            prepayTransactionDetailRecordViewVos.add(new RecordViewVo("default__c", PrepayDetailConstants.OUTCOME_LAYOUT_API_NAME));
            prepayTransactionDetailRecordViewVos.add(new RecordViewVo("income_record_type__c", PrepayDetailConstants.INCOME_LAYOUT_API_NAME));
            initAssignLayout(serviceContext, roleInfoPojos, PrepayDetailConstants.API_NAME, prepayTransactionDetailRecordViewVos);
        } else {
            log.warn("roleInfo error,roleInfo:{}", roleInfoModelResult);
        }
        if (containsAll) {
            return Boolean.TRUE;
        }
        //初始化默认流程
        Map<String, String> approvalInitHeaders = getApprovalInitHeaders(tenantId, fsUserId);
        try {
            initApproval(PrepayDetailConstants.API_NAME, approvalInitHeaders);
            initApproval(RebateIncomeDetailConstants.API_NAME, approvalInitHeaders);
        } catch (Exception e) {
            log.warn("初始化审批流失败,headers:{}", approvalInitHeaders, e);
        }
        return Boolean.TRUE;
    }

    public DescribeResult createCustomerAccountDescribeAndLayout(String tenantId, String fsUserId) {
        IObjectDescribe objectDescribe = generateCustomerAccountDescribe(tenantId, fsUserId);
        ILayout detailLayout = InitUtil.generateCustomerAccountDetailLayout(tenantId, fsUserId);
        ILayout listLayout = generateCustomerAccountListLayout(tenantId, fsUserId);
        String describeJson = objectDescribe.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, fsUserId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("createCustomerAccountDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        return describeResult;
    }

    public DescribeResult createPrepayDetailDescribeAndLayout(String tenantId, String fsUserId) {
        IObjectDescribe objectDescribe = generatePrepayDetailDescribe(tenantId, fsUserId);
        ILayout defaultLayout = InitUtil.generatePrepayDetailDefaultLayout(tenantId, fsUserId);
        ILayout listLayout = generatePrepayDetailListLayout(tenantId, fsUserId);
        ILayout incomeLayout = InitUtil.generatePrepayIncomeLayout(tenantId, fsUserId);
        ILayout outcomeLayout = InitUtil.generatePrepayOutcomeLayout(tenantId, fsUserId);
        User user = new User(tenantId, fsUserId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, objectDescribe.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
        log.info("createPrepayDetailDescribeAndLayout user:{},describeResult:{}", user, describeResult);
        incomeLayout = layoutLogicService.createLayout(user, incomeLayout);
        log.info("createPrepayIncomeLayout,user:{},layout:{}", user, incomeLayout);
        outcomeLayout = layoutLogicService.createLayout(user, outcomeLayout);
        log.info("createPrepayOutcomeLayout,user:{},layout:{}", user, outcomeLayout);
        return describeResult;
    }

    public DescribeResult createRebateIncomeDetailDescribeAndLayout(String tenantId, String fsUserId) {
        IObjectDescribe objectDescribe = generateRebateIncomeDetailDescribe(tenantId, fsUserId);
        ILayout defaultLayout = InitUtil.generateRebateIncomeDetailDefaultLayout(tenantId, fsUserId);
        ILayout listLayout = generateRebateIncomeDetailListLayout(tenantId, fsUserId);
        User user = new User(tenantId, fsUserId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, objectDescribe.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
        log.info("createRebateIncomeDetailDescribeAndLayout user:{},decribeResult:{}", user, describeResult);
        try {
            initStartAndEndTimeRule(user, RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.START_END_TIME_RULE_API_NAME, RebateIncomeDetailConstants.START_END_TIME_RULE_DISPLAY_NAME, RebateIncomeDetailConstants.Field.StartTime.apiName, RebateIncomeDetailConstants.Field.EndTime.apiName, RebateIncomeDetailConstants.START_END_TIME_RULE_DESCRIPTION);
        } catch (Exception e) {
            log.warn("init RebateIncome Rule error", e);
        }
        return describeResult;
    }

    public DescribeResult createRebateOutcomeDetailDescribeAndLayout(String tenantId, String fsUserId) {
        IObjectDescribe objectDescribe = generateRebateOutcomeDetailDescribe(tenantId, fsUserId);
        ILayout defaultLayout = InitUtil.generateRebateOutcomeDetailDefaultLayout(tenantId, fsUserId);
        ILayout listLayout = generateRebateOutcomeDetailListLayout(tenantId, fsUserId);
        User user = new User(tenantId, fsUserId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, objectDescribe.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
        log.info("createRebateOutcomeDetailDescribeAndLayout user:{},decribeResult:{}", user, describeResult);
        return describeResult;
    }

    @Override
    public RoleInfoModel.Result roleInfo(RoleInfoModel.Arg arg) {
        RoleInfoModel.Result result = recordTypeAuthApi.roleInfo(arg);
        return result;
    }

    @Override
    public AddRoleViewModel.Result saveLayoutAssign(AddRoleViewModel.Arg arg) {
        return recordTypeAuthApi.addRoleView(arg);
    }

    @Override
    public AddRoleRecordTypeModel.Result addRoleRecordType(AddRoleRecordTypeModel.Arg arg) {
        return recordTypeAuthApi.addRoleRecordType(arg);
    }

    private int initAssignRecord(ServiceContext serviceContext, List<RoleInfoPojo> roleInfoPojos, String entityId, List<String> recordTypeIds, String defaultRecordTypeId) {
        AddRoleRecordTypeModel.Result result;
        RecordTypePojo recordTypePojo;
        for (String recordTypeId : recordTypeIds) {
            List<RecordTypePojo> recordTypePojos = Lists.newArrayList();
            for (RoleInfoPojo roleInfoPojo : roleInfoPojos) {
                recordTypePojo = new RecordTypePojo();
                recordTypePojo.setAppId("CRM");
                recordTypePojo.setEntityId(entityId);
                recordTypePojo.setTenantId(serviceContext.getTenantId());
                recordTypePojo.setRecordTypeId(recordTypeId);
                recordTypePojo.setRoleCode(roleInfoPojo.getRoleCode());
                recordTypePojo.setDefaultType(recordTypeId.equals(defaultRecordTypeId));
                recordTypePojos.add(recordTypePojo);
            }
            AddRoleRecordTypeModel.Arg arg = new AddRoleRecordTypeModel.Arg();
            arg.setRecordTypePojos(recordTypePojos);
            arg.setRecordTypeId(recordTypeId);
            arg.setEntityId(entityId);
            arg.setAuthContext(serviceContext.getUser());
            result = addRoleRecordType(arg);
            if (recordTypeId.equals(recordTypeIds.get(recordTypeIds.size() - 1))) {
                return result.isSuccess() ? 0 : 1;
            }
        }
        return CollectionUtils.isNotEmpty(recordTypeIds) ? 1 : 0;
    }

    private AddRoleViewModel.Result initAssignLayout(ServiceContext serviceContext, List<RoleInfoPojo> roleInfoPojos, String entityId, List<RecordViewVo> recordViewVos) {
        List<RoleViewPojo> roleViewPojos = Lists.newArrayList();
        RoleViewPojo roleViewPojo;
        for (RoleInfoPojo roleInfoPojo : roleInfoPojos) {
            for (RecordViewVo recordViewVo : recordViewVos) {
                roleViewPojo = new RoleViewPojo();
                roleViewPojo.setAppId("CRM");
                roleViewPojo.setEntityId(entityId);
                roleViewPojo.setTenantId(serviceContext.getTenantId());
                roleViewPojo.setRecordTypeId(recordViewVo.getRecordTypeId());
                roleViewPojo.setRoleCode(roleInfoPojo.getRoleCode());
                roleViewPojo.setViewId(recordViewVo.getViewId());
                roleViewPojos.add(roleViewPojo);
            }
        }
        AddRoleViewModel.Arg arg = new AddRoleViewModel.Arg();
        arg.setRoleViewPojos(roleViewPojos);
        arg.setAuthContext(serviceContext.getUser());
        AddRoleViewModel.Result result = saveLayoutAssign(arg);
        return result;
    }

    private IObjectDescribe generateCustomerAccountDescribe(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        AutoNumberFieldDescribe autoNumberFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.Name.apiName).label(CustomerAccountConstants.Field.Name.label).serialNumber(4).startNumber(1).prefix("CA{yyyy}-{mm}-{dd}_").postfix("").defaultValue("01").required(true).unique(true).build();
        fieldDescribeList.add(autoNumberFieldDescribe);

        MasterDetailFieldDescribe masterDetailFieldDescribe = MasterDetailFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.Customer.apiName).label(CustomerAccountConstants.Field.Customer.label).targetApiName(SystemConstants.AccountApiName).targetRelatedListName(CustomerAccountConstants.Field.Customer.targetRelatedListName).targetRelatedListLabel(CustomerAccountConstants.Field.Customer.targetRelatedListLabel).isCreateWhenMasterCreate(false).isRequiredWhenMasterCreate(false)
                .required(true).unique(true).index(true).build();
        fieldDescribeList.add(masterDetailFieldDescribe);

        CurrencyFieldDescribe prepayBalanceCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.PrepayBalance.apiName).label(CustomerAccountConstants.Field.PrepayBalance.label).required(true).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(prepayBalanceCurrencyFieldDescribe);

        CurrencyFieldDescribe prepayAvailableBalanceCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName).label(CustomerAccountConstants.Field.PrepayAvailableBalance.label).required(false).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(prepayAvailableBalanceCurrencyFieldDescribe);

        CurrencyFieldDescribe prepayLockedBalanceCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.PrepayLockedBalance.apiName).label(CustomerAccountConstants.Field.PrepayLockedBalance.label).required(false).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(prepayLockedBalanceCurrencyFieldDescribe);

        CurrencyFieldDescribe rebateBalanceCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.RebateBalance.apiName).label(CustomerAccountConstants.Field.RebateBalance.label).required(true).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(rebateBalanceCurrencyFieldDescribe);

        CurrencyFieldDescribe rebateAvailableBalanceCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.RebateAvailableBalance.apiName).label(CustomerAccountConstants.Field.RebateAvailableBalance.label).required(true).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(rebateAvailableBalanceCurrencyFieldDescribe);

        CurrencyFieldDescribe rebateLockedBalanceCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.RebateLockedBalance.apiName).label(CustomerAccountConstants.Field.RebateLockedBalance.label).required(true).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(rebateLockedBalanceCurrencyFieldDescribe);

        List<ISelectOption> selectOptions = Lists.newArrayList();
        for (SettleTypeEnum settleTypeEnum : SettleTypeEnum.values()) {
            selectOptions.add(getSelectOption(settleTypeEnum.getLabel(), settleTypeEnum.getValue(), settleTypeEnum.getNotUsable()));
        }
        Map<String, Object> settleTypeFieldConfig = FieldConfig.builder().attrs(Lists.newArrayList("help_text", "default_value"), 1).build();
        SelectManyFieldDescribe selectManyFieldDescribe = SelectManyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.SettleType.apiName).label(CustomerAccountConstants.Field.SettleType.label).selectOptions(selectOptions).config(settleTypeFieldConfig).helpText("预付：有客户账户余额（包括预存款和返利）才能下单，可同时校验信用。" + "现付：下单后付款，不检验信用。" + "赊销：启用信用后可用，下单校验可用信用").build();
        fieldDescribeList.add(selectManyFieldDescribe);

        Map<String, Object> creditQuotaFieldConfig = FieldConfig.builder().attrs(Lists.newArrayList("help_text", "default_value", "decimal_places", "max_length"), 1).build();
        CurrencyFieldDescribe creditQuotaFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(CustomerAccountConstants.Field.CreditQuota.apiName).label(CustomerAccountConstants.Field.CreditQuota.label).required(false).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).config(creditQuotaFieldConfig).build();
        fieldDescribeList.add(creditQuotaFieldDescribe);

        fieldDescribeList.add(getLifeStatusFieldDescribe(CustomerAccountConstants.LIFE_STATUS_HELP_TEXT));

        return ObjectDescribeBuilder.builder().tenantId(tenantId).createBy(fsUserId).apiName(CustomerAccountConstants.API_NAME).displayName(CustomerAccountConstants.DISPLAY_NAME).fieldDescribes(fieldDescribeList).storeTableName(CustomerAccountConstants.STORE_TABLE_NAME).iconIndex(CustomerAccountConstants.ICON_INDEX).build();
    }

    private IObjectDescribe generatePrepayDetailDescribe(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        AutoNumberFieldDescribe nameAutoFieldDescribe = AutoNumberFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Name.apiName).label(PrepayDetailConstants.Field.Name.label).serialNumber(4).startNumber(1).prefix("PT{yyyy}-{mm}-{dd}_").defaultValue("01").unique(true).required(true).build();
        fieldDescribeList.add(nameAutoFieldDescribe);

        ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Customer.apiName).label(PrepayDetailConstants.Field.Customer.label).required(true).targetApiName(SystemConstants.AccountApiName).targetRelatedListName(PrepayDetailConstants.Field.Customer.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.Customer.targetRelatedListLabel).build();
        fieldDescribeList.add(customerObjectReferenceFieldDescribe);

        ObjectReferenceFieldDescribe customerAccountObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.CustomerAccount.apiName).label(PrepayDetailConstants.Field.CustomerAccount.label).required(true).targetApiName(CustomerAccountConstants.API_NAME).targetRelatedListName(PrepayDetailConstants.Field.CustomerAccount.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.CustomerAccount.targetRelatedListLabel).build();
        fieldDescribeList.add(customerAccountObjectReferenceFieldDescribe);

        CurrencyFieldDescribe amountCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Amount.apiName).label(PrepayDetailConstants.Field.Amount.label).decimalPlaces(2).maxLength(14).length(12).currencyUnit("￥").required(true).roundMode(4).build();
        fieldDescribeList.add(amountCurrencyFieldDescribe);

        DateTimeFieldDescribe transactionDateTimeFieldDescribe = DateTimeFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.TransactionTime.apiName).label(PrepayDetailConstants.Field.TransactionTime.label).dateFormat("yyyy-MM-dd HH:mm:ss").required(true).unique(false).build();
        fieldDescribeList.add(transactionDateTimeFieldDescribe);

        ObjectReferenceFieldDescribe refundObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Refund.apiName).label(PrepayDetailConstants.Field.Refund.label).required(false).unique(false).targetApiName(SystemConstants.RefundApiName).targetRelatedListName(PrepayDetailConstants.Field.Refund.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.Refund.targetRelatedListLabel).build();
        fieldDescribeList.add(refundObjectReferenceFieldDescribe);

        List<ISelectOption> incomeSelectOptions = Lists.newArrayList();
        for (PrepayIncomeTypeEnum prepayIncomeTypeEnum : PrepayIncomeTypeEnum.values()) {
            ISelectOption selectOption = getSelectOption(prepayIncomeTypeEnum.getLabel(), prepayIncomeTypeEnum.getValue(), prepayIncomeTypeEnum.getNotUsable());
            if (!Lists.newArrayList(PrepayIncomeTypeEnum.OrderRefund.getValue(), PrepayIncomeTypeEnum.OnlineCharge.getValue()).contains(selectOption.getValue())) {
                selectOption.set("config", OptionConfig.builder().edit(1).enable(1).remove(1).build());
            }
            incomeSelectOptions.add(selectOption);
        }
        Map<String, Object> incomeTypeFieldConfig = FieldConfig.builder().edit(1).add(1).attrs(Lists.newArrayList("options", "default_value"), 1).build();
        SelectOneFieldDescribe incomeSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.IncomeType.apiName).label(PrepayDetailConstants.Field.IncomeType.label).required(false).selectOptions(incomeSelectOptions).helpText(null).defaultValud(null).config(incomeTypeFieldConfig).build();
        fieldDescribeList.add(incomeSelectOneFieldDescribe);

        TextFieldDescribe onlineChargeNoTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OnlineChargeNo.apiName).label(PrepayDetailConstants.Field.OnlineChargeNo.label).maxLength(100).build();
        fieldDescribeList.add(onlineChargeNoTextFieldDescribe);

        ObjectReferenceFieldDescribe orderPaymentObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OrderPayment.apiName).label(PrepayDetailConstants.Field.OrderPayment.label).required(false).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(PrepayDetailConstants.Field.OrderPayment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();
        fieldDescribeList.add(orderPaymentObjectReferenceFieldDescribe);

        List<ISelectOption> outcomeSelectOptions = Lists.newArrayList();
        for (PrepayOutcomeTypeEnum prepayOutComeTypeEnum : PrepayOutcomeTypeEnum.values()) {
            ISelectOption selectOption = getSelectOption(prepayOutComeTypeEnum.getLabel(), prepayOutComeTypeEnum.getValue(), prepayOutComeTypeEnum.getNotUsable());
            if (!PrepayOutcomeTypeEnum.OffsetOrder.getValue().equals(selectOption.getValue())) {
                selectOption.set("config", OptionConfig.builder().remove(1).enable(1).edit(1).build());
            }
            outcomeSelectOptions.add(selectOption);
        }
        Map<String, Object> outcomeTypeFieldConfig = FieldConfig.builder().edit(1).add(1).attrs(Lists.newArrayList("options", "default_value"), 1).build();
        SelectOneFieldDescribe outcomeTypeSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OutcomeType.apiName).label(PrepayDetailConstants.Field.OutcomeType.label).selectOptions(outcomeSelectOptions).required(false).config(outcomeTypeFieldConfig).build();
        fieldDescribeList.add(outcomeTypeSelectOneFieldDescribe);

        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Remark.apiName).label(PrepayDetailConstants.Field.Remark.label).maxLength(1000).pattern(null).build();
        fieldDescribeList.add(longTextFieldDescribe);

        FileAttachmentFieldDescribe fileAttachmentFieldDescribe = FileAttachmentFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Attach.apiName).label(PrepayDetailConstants.Field.Attach.label).fileAmountLimit(10).fileSizeLimit(10485760L).build();
        fieldDescribeList.add(fileAttachmentFieldDescribe);

        List<IRecordTypeOption> recordTypeOptions = Lists.newArrayList();
        RecordTypeOption outcomeRecordTypeOption = getRecordTypeOption(PrepayDetailConstants.RecordType.OutcomeRecordType.apiName, PrepayDetailConstants.RecordType.OutcomeRecordType.label, true);
        RecordTypeOption incomeRecordTypeOption = getRecordTypeOption(PrepayDetailConstants.RecordType.IncomeRecordType.apiName, PrepayDetailConstants.RecordType.IncomeRecordType.label, true);
        recordTypeOptions.add(outcomeRecordTypeOption);
        recordTypeOptions.add(incomeRecordTypeOption);
        RecordTypeFieldDescribe recordTypeFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeFieldDescribe);

        fieldDescribeList.add(getLifeStatusFieldDescribe(PrepayDetailConstants.LIFE_STATUS_HELP_TEXT));
        return ObjectDescribeBuilder.builder().tenantId(tenantId).createBy(fsUserId).apiName(PrepayDetailConstants.API_NAME).displayName(PrepayDetailConstants.DISPLAY_NAME).fieldDescribes(fieldDescribeList).storeTableName(PrepayDetailConstants.STORE_TABLE_NAME).iconIndex(PrepayDetailConstants.ICON_INDEX).build();
    }

    private SelectOneFieldDescribe getLifeStatusFieldDescribe(String helpText) {
        List<ISelectOption> lifeStatusSelectOptions = Lists.newArrayList();
        ISelectOption ineffectiveOption = getSelectOption(SystemConstants.LifeStatus.Ineffective.label, SystemConstants.LifeStatus.Ineffective.value, false);
        ISelectOption underReviewOption = getSelectOption(SystemConstants.LifeStatus.UnderReview.label, SystemConstants.LifeStatus.UnderReview.value, false);
        ISelectOption normalOption = getSelectOption(SystemConstants.LifeStatus.Normal.label, SystemConstants.LifeStatus.Normal.value, false);
        ISelectOption inChangeOption = getSelectOption(SystemConstants.LifeStatus.InChange.label, SystemConstants.LifeStatus.InChange.value, false);
        ISelectOption invalidOption = getSelectOption(SystemConstants.LifeStatus.Invalid.label, SystemConstants.LifeStatus.Invalid.value, false);
        lifeStatusSelectOptions.add(ineffectiveOption);
        lifeStatusSelectOptions.add(underReviewOption);
        lifeStatusSelectOptions.add(normalOption);
        lifeStatusSelectOptions.add(inChangeOption);
        lifeStatusSelectOptions.add(invalidOption);
        return SelectOneFieldDescribeBuilder.builder().apiName(SystemConstants.Field.LifeStatus.apiName).label(SystemConstants.Field.LifeStatus.label).defaultValud(SystemConstants.LifeStatus.Normal.value).selectOptions(lifeStatusSelectOptions).required(true).helpText(helpText).build();
    }

    private IObjectDescribe generateRebateIncomeDetailDescribe(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        AutoNumberFieldDescribe nameAutoNubmerFieldDescirbe = AutoNumberFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.Name.apiName).label(RebateIncomeDetailConstants.Field.Name.label).serialNumber(4).startNumber(1).prefix("RTI{yyyy}-{mm}-{dd}_").defaultValue("01").build();
        fieldDescribeList.add(nameAutoNubmerFieldDescirbe);

        ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.Customer.apiName).label(RebateIncomeDetailConstants.Field.Customer.label).required(true).targetApiName(SystemConstants.AccountApiName).targetRelatedListName(RebateIncomeDetailConstants.Field.Customer.targetRelatedListName).targetRelatedListLabel(RebateIncomeDetailConstants.Field.Customer.targetRelatedListLabel).build();
        fieldDescribeList.add(customerObjectReferenceFieldDescribe);

        ObjectReferenceFieldDescribe customerAccountObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.CustomerAccount.apiName).label(RebateIncomeDetailConstants.Field.CustomerAccount.label).required(true).targetApiName(CustomerAccountConstants.API_NAME).targetRelatedListName(RebateIncomeDetailConstants.Field.CustomerAccount.targetRelatedListName)
                .targetRelatedListLabel(RebateIncomeDetailConstants.Field.CustomerAccount.targetRelatedListLabel).build();
        fieldDescribeList.add(customerAccountObjectReferenceFieldDescribe);

        CurrencyFieldDescribe amountCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.Amount.apiName).label(RebateIncomeDetailConstants.Field.Amount.label).required(true).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(amountCurrencyFieldDescribe);

        DateTimeFieldDescribe transactionDateTimeFieldDescribe = DateTimeFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.TransactionTime.apiName).label(RebateIncomeDetailConstants.Field.TransactionTime.label).dateFormat("yyyy-MM-dd HH:mm:ss").required(true).unique(false).build();
        fieldDescribeList.add(transactionDateTimeFieldDescribe);

        DateFieldDescribe startTimeDateFieldDescribe = DateFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.StartTime.apiName).label(RebateIncomeDetailConstants.Field.StartTime.label).required(true).build();
        fieldDescribeList.add(startTimeDateFieldDescribe);

        DateFieldDescribe endTimeDateFieldDescribe = DateFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.EndTime.apiName).label(RebateIncomeDetailConstants.Field.EndTime.label).required(true).build();
        fieldDescribeList.add(endTimeDateFieldDescribe);

        CurrencyFieldDescribe usedRebateCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.UsedRebate.apiName).label(RebateIncomeDetailConstants.Field.UsedRebate.label).required(false).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(usedRebateCurrencyFieldDescribe);

        CurrencyFieldDescribe availableRebateCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName).label(RebateIncomeDetailConstants.Field.AvailableRebate.label).required(false).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(availableRebateCurrencyFieldDescribe);

        //6.3订单返利收入类型
        List<ISelectOption> incomeSelectOptions = Lists.newArrayList();
        for (RebateIncomeTypeEnum rebateIncomeTypeEnum : RebateIncomeTypeEnum.values()) {
            ISelectOption selectOption = getSelectOption(rebateIncomeTypeEnum.getLabel(), rebateIncomeTypeEnum.getValue(), rebateIncomeTypeEnum.getNotUsable());
            if (!Lists.newArrayList(RebateIncomeTypeEnum.OrderRefund.getValue(), RebateIncomeTypeEnum.OrderRebate.getValue()).contains(selectOption.getValue())) {
                selectOption.set("config", OptionConfig.builder().edit(1).remove(1).enable(1).build());
            }
            incomeSelectOptions.add(selectOption);
        }
        Map<String, Object> incomeTypeFieldConfig = FieldConfig.builder().edit(1).add(1).attrs(Lists.newArrayList("options", "default_value"), 1).build();
        SelectOneFieldDescribe incomeTypeSelectOneFieldDescribe = SelectOneFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.IncomeType.apiName).label(RebateIncomeDetailConstants.Field.IncomeType.label).required(true).selectOptions(incomeSelectOptions).config(incomeTypeFieldConfig).build();
        fieldDescribeList.add(incomeTypeSelectOneFieldDescribe);
        //6.3新增字段
        ObjectReferenceFieldDescribe orderObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.SalesOrder.apiName).label(RebateIncomeDetailConstants.Field.SalesOrder.label).required(false).targetApiName(SystemConstants.SalesOrderApiName).targetRelatedListName(RebateIncomeDetailConstants.Field.SalesOrder.targetRelatedListName).targetRelatedListLabel(RebateIncomeDetailConstants.Field.SalesOrder.targetRelatedListLabel).build();
        fieldDescribeList.add(orderObjectReferenceFieldDescribe);

        ObjectReferenceFieldDescribe refundObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.Refund.apiName).label(RebateIncomeDetailConstants.Field.Refund.label).required(false).targetApiName(SystemConstants.RefundApiName).targetRelatedListName(RebateIncomeDetailConstants.Field.Refund.targetRelatedListName).targetRelatedListLabel(RebateIncomeDetailConstants.Field.Refund.targetRelatedListLabel).build();
        fieldDescribeList.add(refundObjectReferenceFieldDescribe);

        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.Remark.apiName).label(RebateIncomeDetailConstants.Field.Remark.label).maxLength(1000).build();
        fieldDescribeList.add(longTextFieldDescribe);

        FileAttachmentFieldDescribe fileAttachmentFieldDescribe = FileAttachmentFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.Attach.apiName).label(RebateIncomeDetailConstants.Field.Attach.label).supportFileTypes(Lists.newArrayList()).fileAmountLimit(10).build();
        fieldDescribeList.add(fileAttachmentFieldDescribe);

        SelectOneFieldDescribe lifeStatus = getLifeStatusFieldDescribe(RebateIncomeDetailConstants.LIFE_STATUS_HELP_TEXT);
        fieldDescribeList.add(lifeStatus);

        return ObjectDescribeBuilder.builder().tenantId(tenantId).createBy(fsUserId).apiName(RebateIncomeDetailConstants.API_NAME).displayName(RebateIncomeDetailConstants.DISPLAY_NAME).fieldDescribes(fieldDescribeList).storeTableName(RebateIncomeDetailConstants.STORE_TABLE_NAME).iconIndex(RebateIncomeDetailConstants.ICON_INDEX).build();
    }

    private IObjectDescribe generateRebateOutcomeDetailDescribe(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        AutoNumberFieldDescribe nameAutoNubmerFieldDescirbe = AutoNumberFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.Name.apiName).label(RebateOutcomeDetailConstants.Field.Name.label).serialNumber(4).startNumber(1).prefix("RTO{yyyy}-{mm}-{dd}_").defaultValue("01").required(true).unique(true).build();
        fieldDescribeList.add(nameAutoNubmerFieldDescirbe);

        MasterDetailFieldDescribe rebateIncomeDetailMasterDetailFieldDescribe = MasterDetailFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName).label(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.label).targetApiName(RebateIncomeDetailConstants.API_NAME).targetRelatedListName(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.targetRelatedListName)
                .targetRelatedListLabel(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.targetRelatedListLabel).isCreateWhenMasterCreate(false).isRequiredWhenMasterCreate(false).unique(false).required(true).index(true).build();
        fieldDescribeList.add(rebateIncomeDetailMasterDetailFieldDescribe);

        CurrencyFieldDescribe amountCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.Amount.apiName).label(RebateOutcomeDetailConstants.Field.Amount.label).required(true).maxLength(14).length(12).decimalPlaces(2).currencyUnit("￥").roundMode(4).build();
        fieldDescribeList.add(amountCurrencyFieldDescribe);

        DateTimeFieldDescribe transactionTimeDateTimeFieldDescribe = DateTimeFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.TransactionTime.apiName).label(RebateOutcomeDetailConstants.Field.TransactionTime.label).required(true).unique(false).dateFormat("yyyy-MM-dd HH:mm:ss").build();
        fieldDescribeList.add(transactionTimeDateTimeFieldDescribe);

        ObjectReferenceFieldDescribe orderPaymentobjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.OrderPayment.apiName).label(RebateOutcomeDetailConstants.Field.OrderPayment.label).required(false).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(RebateOutcomeDetailConstants.Field.OrderPayment.targetRelatedListName)
                .targetRelatedListLabel(RebateOutcomeDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();
        fieldDescribeList.add(orderPaymentobjectReferenceFieldDescribe);

        //6.3 返利使用规则字段
        ObjectReferenceFieldDescribe rebateUseRuleObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.RebateUseRule.apiName).label(RebateOutcomeDetailConstants.Field.RebateUseRule.label).required(false).targetApiName(RebateUseRuleConstants.API_NAME).targetRelatedListName(RebateOutcomeDetailConstants.Field.RebateUseRule.targetRelatedListName)
                .targetRelatedListLabel(RebateOutcomeDetailConstants.Field.RebateUseRule.targetRelatedListLabel).build();
        fieldDescribeList.add(rebateUseRuleObjectReferenceFieldDescribe);

        SelectOneFieldDescribe lifeStatus = getLifeStatusFieldDescribe(RebateOutcomeDetailConstants.LIFE_STATUS_HELP_TEXT);
        fieldDescribeList.add(lifeStatus);

        return ObjectDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.API_NAME).displayName(RebateOutcomeDetailConstants.DISPLAY_NAME).fieldDescribes(fieldDescribeList).tenantId(tenantId).createBy(fsUserId).storeTableName(RebateOutcomeDetailConstants.STORE_TABLE_NAME).build();
    }

    private ILayout generateCustomerAccountListLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();

        List<ITableColumn> tableColumns = Lists.newArrayList();

        tableColumns.add(TableColumnBuilder.builder().name(CustomerAccountConstants.Field.Name.apiName).lableName(CustomerAccountConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(CustomerAccountConstants.Field.Customer.apiName).lableName(CustomerAccountConstants.Field.Customer.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(CustomerAccountConstants.Field.PrepayBalance.apiName).lableName(CustomerAccountConstants.Field.PrepayBalance.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(CustomerAccountConstants.Field.RebateBalance.apiName).lableName(CustomerAccountConstants.Field.RebateBalance.label).renderType(SystemConstants.RenderType.Currency.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(CustomerAccountConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        components.add(tableComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).components(components).agentType(LayoutConstants.AGENT_TYPE).isDefault(false).layoutType(SystemConstants.LayoutType.List.layoutType).name(CustomerAccountConstants.LIST_LAYOUT_API_NAME).displayName(CustomerAccountConstants.LIST_LAYOUT_DISPLAY_NAME).refObjectApiName(CustomerAccountConstants.API_NAME).isShowFieldName(true).build();

    }

    private ILayout generatePrepayDetailListLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(PrepayDetailConstants.Field.Customer.apiName).lableName(PrepayDetailConstants.Field.Customer.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PrepayDetailConstants.Field.Amount.apiName).lableName(PrepayDetailConstants.Field.Amount.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PrepayDetailConstants.Field.IncomeType.apiName).lableName(PrepayDetailConstants.Field.IncomeType.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PrepayDetailConstants.Field.OutcomeType.apiName).lableName(PrepayDetailConstants.Field.OutcomeType.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(PrepayDetailConstants.Field.TransactionTime.apiName).lableName(PrepayDetailConstants.Field.TransactionTime.label).renderType(SystemConstants.RenderType.DateTime.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(PrepayDetailConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        components.add(tableComponent);
        return LayoutBuilder.builder().name(PrepayDetailConstants.LIST_LAYOUT_API_NAME).displayName(PrepayDetailConstants.LIST_LAYOUT_DISPLAY_NAME).refObjectApiName(PrepayDetailConstants.API_NAME).tenantId(tenantId).createBy(fsUserId).isShowFieldName(true).layoutType(SystemConstants.LayoutType.List.layoutType).agentType(LayoutConstants.AGENT_TYPE).isDefault(false).components(components).build();
    }

    private ILayout generateRebateIncomeDetailListLayout(String tenantId, String fsUserId) {
        List<IComponent> components = Lists.newArrayList();

        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(RebateIncomeDetailConstants.Field.Customer.apiName).lableName(RebateIncomeDetailConstants.Field.Customer.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateIncomeDetailConstants.Field.Amount.apiName).lableName(RebateIncomeDetailConstants.Field.Amount.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateIncomeDetailConstants.Field.TransactionTime.apiName).lableName(RebateIncomeDetailConstants.Field.TransactionTime.label).renderType(SystemConstants.RenderType.DateTime.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(RebateIncomeDetailConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        components.add(tableComponent);
        return LayoutBuilder.builder().name(RebateIncomeDetailConstants.LIST_LAYOUT_API_NAME).displayName(RebateIncomeDetailConstants.LIST_LAYOUT_DISPLAY_NAME).refObjectApiName(RebateIncomeDetailConstants.API_NAME).isDefault(false).tenantId(tenantId).createBy(fsUserId).isShowFieldName(true).layoutType(SystemConstants.LayoutType.List.layoutType).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    private ILayout generateRebateOutcomeDetailListLayout(String tenantId, String fsUserid) {
        List<IComponent> components = Lists.newArrayList();

        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(RebateOutcomeDetailConstants.Field.Name.apiName).lableName(RebateOutcomeDetailConstants.Field.Name.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateOutcomeDetailConstants.Field.Amount.apiName).lableName(RebateOutcomeDetailConstants.Field.Amount.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateOutcomeDetailConstants.Field.TransactionTime.apiName).lableName(RebateOutcomeDetailConstants.Field.TransactionTime.label).renderType(SystemConstants.RenderType.DateTime.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName).lableName(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.label).renderType(SystemConstants.RenderType.MasterDetail.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateOutcomeDetailConstants.Field.OrderPayment.apiName).lableName(RebateOutcomeDetailConstants.Field.OrderPayment.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        //6.3
        tableColumns.add(TableColumnBuilder.builder().name(RebateOutcomeDetailConstants.Field.RebateUseRule.apiName).lableName(RebateOutcomeDetailConstants.Field.RebateUseRule.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.Owner.apiName).lableName(SystemConstants.Field.Owner.label).renderType(SystemConstants.RenderType.Employee.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.RecordType.apiName).lableName(SystemConstants.Field.RecordType.label).renderType(SystemConstants.RenderType.RecordType.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(RebateOutcomeDetailConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        components.add(tableComponent);
        return LayoutBuilder.builder().name(RebateOutcomeDetailConstants.LIST_LAYOUT_API_NAME).displayName(RebateOutcomeDetailConstants.LIST_LAYOUT_DISPLAY_NAME).refObjectApiName(RebateOutcomeDetailConstants.API_NAME).tenantId(tenantId).createBy(fsUserid).isShowFieldName(true).layoutType(SystemConstants.LayoutType.List.layoutType).agentType(LayoutConstants.AGENT_TYPE).isDefault(false).components(components).build();
    }

    @Override
    public ApprovalInitModel.Result initApproval(String objectApiName, Map<String, String> headers) {
        ApprovalInitModel.Arg approvalInitArg = new ApprovalInitModel.Arg();
        approvalInitArg.setEntityId(objectApiName);
        ApprovalInitModel.Result result = approvalInitProxy.init(approvalInitArg, headers);
        log.debug("Init Default Approval,headers:{},Arg:{},Result:{}", headers, approvalInitArg, result);
        return result;
    }

    private Map<String, String> getApprovalInitHeaders(String tenantId, String fsUserId) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", fsUserId);
        headers.put("x-tenant-id", tenantId);
        return headers;
    }

    private RecordTypeOption getRecordTypeOption(String apiName, String label, boolean isActive) {
        RecordTypeOption recordTypeOption = new RecordTypeOption();
        recordTypeOption.setApiName(apiName);
        recordTypeOption.setIsActive(isActive);
        recordTypeOption.setLabel(label);
        return recordTypeOption;
    }

    private ISelectOption getSelectOption(String label, String value, boolean notUsable) {
        ISelectOption selectOption = new SelectOption();
        selectOption.setLabel(label);
        selectOption.setNotUsable(false);
        selectOption.setValue(value);
        return selectOption;
    }

    @Override
    public DescribeResult initRebateUseRule(User user) {
        IObjectDescribe promotionUseRuleDescribe = generateRebateUseRuleDescribe(user.getTenantId(), user.getUserId());
        ILayout defaultLayout = generateRebateUseRuleDefaultLayout(user.getTenantId(), user.getUserId());
        ILayout listLayout = generateRebateUseRuleListLayout(user.getTenantId(), user.getUserId());
        DescribeResult describeResult = serviceFacade.createDescribe(user, promotionUseRuleDescribe.toJsonString(), defaultLayout.toJsonString(), listLayout.toJsonString(), true, true);
        log.info("user:{},PromotionUseRuleObj describeResult:{}", user, describeResult.toString());
        try {
            initStartAndEndTimeRule(user, RebateUseRuleConstants.API_NAME, RebateUseRuleConstants.START_END_TIME_RULE_API_NAME, RebateUseRuleConstants.START_END_TIME_RULE_DISPLAY_NAME, RebateUseRuleConstants.Field.StartTime.apiName, RebateUseRuleConstants.Field.EndTime.apiName, RebateUseRuleConstants.START_END_TIME_RULE_DESCRIPTION);
        } catch (Exception e) {
            log.warn("init rebateUseRule start ent time rule error", e);
        }
        return describeResult;
    }

    private IObjectDescribe generateRebateUseRuleDescribe(String tenantId, String fsUserId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();
        TextFieldDescribe nameFieldDescribe = TextFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.Name.apiName).label(RebateUseRuleConstants.Field.Name.label).maxLength(1000).build();
        fieldDescribeList.add(nameFieldDescribe);

        DateFieldDescribe startTimeFieldDescribe = DateFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.StartTime.apiName).label(RebateUseRuleConstants.Field.StartTime.label).index(true).required(true).unique(false).build();
        fieldDescribeList.add(startTimeFieldDescribe);

        DateFieldDescribe endTimeFieldDescribe = DateFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.EndTime.apiName).label(RebateUseRuleConstants.Field.EndTime.label).index(true).required(true).unique(false).build();
        fieldDescribeList.add(endTimeFieldDescribe);

        BooleanFieldDescribe statusBooleanFieldDescribe = BooleanFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.Status.apiName).label(RebateUseRuleConstants.Field.Status.label).defaultValue(true).required(true).unique(false).index(true).build();
        fieldDescribeList.add(statusBooleanFieldDescribe);

        CurrencyFieldDescribe minOrderAmountCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.MinOrderAmount.apiName).label(RebateUseRuleConstants.Field.MinOrderAmount.label).currencyUnit("￥").decimalPlaces(2).length(12).maxLength(14).required(false).roundMode(4).build();
        fieldDescribeList.add(minOrderAmountCurrencyFieldDescribe);

        CurrencyFieldDescribe usedMaxAmountCurrencyFieldDescribe = CurrencyFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.UsedMaxAmount.apiName).helpText(RebateUseRuleConstants.usedMaxAmountHelpText).label(RebateUseRuleConstants.Field.UsedMaxAmount.label).currencyUnit("￥").decimalPlaces(2).length(12).maxLength(14).required(false).roundMode(4).build();
        fieldDescribeList.add(usedMaxAmountCurrencyFieldDescribe);

        PercentileFieldDescribe usedMaxPrecentCurrencyFieldDescribe = PercentileFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.UsedMaxPrecent.apiName).helpText(RebateUseRuleConstants.usedMaxPrecentHelpText).label(RebateUseRuleConstants.Field.UsedMaxPrecent.label).required(false).build();
        fieldDescribeList.add(usedMaxPrecentCurrencyFieldDescribe);

        LongTextFieldDescribe longTextFieldDescribe = LongTextFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.Remark.apiName).label(RebateUseRuleConstants.Field.Remark.label).maxLength(1000).pattern(null).build();
        fieldDescribeList.add(longTextFieldDescribe);

        UseScopeFieldDescribe customerRangeUseScopeFieldDescribe = UseScopeFieldDescribeBuilder.builder().apiName(RebateUseRuleConstants.Field.CustomerRange.apiName).label(RebateUseRuleConstants.Field.CustomerRange.label).targetApiName(Utils.ACCOUNT_API_NAME).defaultValue(RebateUseRuleConstants.customerRangeDefaultValue).expressionType(RebateUseRuleConstants.expressionType).build();
        fieldDescribeList.add(customerRangeUseScopeFieldDescribe);

        return ObjectDescribeBuilder.builder().apiName(RebateUseRuleConstants.API_NAME).displayName(RebateUseRuleConstants.DISPLAY_NAME).tenantId(tenantId).createBy(fsUserId).fieldDescribes(fieldDescribeList).storeTableName(RebateUseRuleConstants.STORE_TABLE_NAME).iconIndex(RebateUseRuleConstants.ICON_INDEX).build();
    }

    private ILayout generateRebateUseRuleDefaultLayout(String tenantId, String fsUserId) {
        List<IFieldSection> fieldSections = Lists.newArrayList();
        List<IFormField> formFields = Lists.newArrayList();
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.Name.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Text.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.StartTime.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Date.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.EndTime.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Date.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.Status.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.TrueOrFalse.renderType).required(true).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.MinOrderAmount.apiName).readOnly(false).required(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Currency.renderType).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.UsedMaxAmount.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Currency.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.UsedMaxPrecent.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Percentile.renderType).required(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(true).readOnly(false).build());
        formFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.Remark.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.LongText.renderType).required(false).build());
        FieldSection fieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();

        List<IFormField> customerRangeFormFields = Lists.newArrayList();
        customerRangeFormFields.add(FormFieldBuilder.builder().fieldName(RebateUseRuleConstants.Field.CustomerRange.apiName).readOnly(false).renderType(com.facishare.crm.constants.SystemConstants.RenderType.UseScope.renderType).required(true).build());
        FieldSection customerRangeFieldSection = FieldSectionBuilder.builder().header(RebateUseRuleConstants.Field.CustomerRange.label).name(RebateUseRuleConstants.CUSTOMER_RANGE_SECTION_API_NAME).showHeader(true).fields(customerRangeFormFields).build();
        fieldSections.add(fieldSection);
        fieldSections.add(customerRangeFieldSection);
        fieldSections.add(InitUtil.getSystemFieldSection());

        FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).buttons(null).fieldSections(fieldSections).build();
        List<IComponent> components = Lists.newArrayList(formComponent);

        return LayoutBuilder.builder().createBy(fsUserId).tenantId(tenantId).name(RebateUseRuleConstants.DEFAULT_LAYOUT_API_NAME).displayName(RebateUseRuleConstants.DEFAULT_LAYOUT_DISPLAY_NAME).isDefault(true).refObjectApiName(RebateUseRuleConstants.API_NAME).components(components).layoutType(com.facishare.crm.constants.SystemConstants.LayoutType.Detail.layoutType).build();
    }

    private ILayout generateRebateUseRuleListLayout(String tenantId, String fsUserId) {
        List<ITableColumn> tableColumns = Lists.newArrayList();
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.Name.apiName).lableName(RebateUseRuleConstants.Field.Name.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Text.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.StartTime.apiName).lableName(RebateUseRuleConstants.Field.StartTime.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.EndTime.apiName).lableName(RebateUseRuleConstants.Field.EndTime.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Date.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.Status.apiName).lableName(RebateUseRuleConstants.Field.Status.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.TrueOrFalse.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.MinOrderAmount.apiName).lableName(RebateUseRuleConstants.Field.MinOrderAmount.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.UsedMaxAmount.apiName).lableName(RebateUseRuleConstants.Field.UsedMaxAmount.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Currency.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.UsedMaxPrecent.apiName).lableName(RebateUseRuleConstants.Field.UsedMaxPrecent.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.Percentile.renderType).build());
        tableColumns.add(TableColumnBuilder.builder().name(RebateUseRuleConstants.Field.Remark.apiName).lableName(RebateUseRuleConstants.Field.Remark.label).renderType(com.facishare.crm.constants.SystemConstants.RenderType.LongText.renderType).build());

        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(RebateUseRuleConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        List<IComponent> components = Lists.newArrayList(tableComponent);
        return LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).refObjectApiName(RebateUseRuleConstants.API_NAME).layoutType(com.facishare.crm.constants.SystemConstants.LayoutType.List.layoutType).isDefault(false).name(RebateUseRuleConstants.LIST_LAYOUT_API_NAME).displayName(RebateUseRuleConstants.LIST_LAYOUT_DISPLAY_NAME).isShowFieldName(true).agentType(LayoutConstants.AGENT_TYPE).components(components).build();
    }

    @Data
    private class RecordViewVo {
        private String recordTypeId;
        private String viewId;

        public RecordViewVo(String recordTypeId, String viewId) {
            this.recordTypeId = recordTypeId;
            this.viewId = viewId;
        }
    }
}
