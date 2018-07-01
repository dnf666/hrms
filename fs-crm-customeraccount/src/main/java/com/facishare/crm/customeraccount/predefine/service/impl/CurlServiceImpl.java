package com.facishare.crm.customeraccount.predefine.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateUseRuleConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.dao.CustomerAccountConfigDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.PrepayOutcomeTypeEnum;
import com.facishare.crm.customeraccount.enums.RebateIncomeTypeEnum;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountConfigManager;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateOutcomeDetailManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.predefine.service.CurlService;
import com.facishare.crm.customeraccount.predefine.service.InitService;
import com.facishare.crm.customeraccount.predefine.service.dto.CurlModel;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.predefine.service.dto.EmptyResult;
import com.facishare.crm.customeraccount.util.ConfigCenter;
import com.facishare.crm.customeraccount.util.HeaderUtil;
import com.facishare.crm.customeraccount.util.InitUtil;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.describebuilder.FieldConfig;
import com.facishare.crm.describebuilder.FieldSectionBuilder;
import com.facishare.crm.describebuilder.FormComponentBuilder;
import com.facishare.crm.describebuilder.FormFieldBuilder;
import com.facishare.crm.describebuilder.ObjectReferenceFieldDescribeBuilder;
import com.facishare.crm.describebuilder.SelectOptionBuilder;
import com.facishare.crm.describebuilder.TableColumnBuilder;
import com.facishare.crm.describebuilder.TableComponentBuilder;
import com.facishare.crm.rest.ApprovalInitProxy;
import com.facishare.crm.rest.FunctionProxy;
import com.facishare.crm.rest.dto.ApprovalInitModel;
import com.facishare.crm.rest.dto.ApprovalInstanceModel;
import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.metadata.FieldLayoutPojo;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.RecordTypeLogicService;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeProxy;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.appframework.privilege.dto.AuthContext;
import com.facishare.paas.appframework.privilege.dto.CreateFunctionPrivilege;
import com.facishare.paas.appframework.privilege.model.FunctionPrivilegeProvider;
import com.facishare.paas.appframework.privilege.model.FunctionPrivilegeProviderManager;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.ILayoutService;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.describe.CurrencyFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectManyFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CurlServiceImpl implements CurlService {
    @Autowired
    private CustomerAccountManager customerAccountManager;
    @Autowired
    private CustomerAccountConfigDao customerAccountConfigDao;
    @Autowired
    private CustomerAccountConfigManager customerAccountConfigManager;
    @Autowired
    private CrmManager crmManager;
    @Autowired
    private InitService initService;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private ILayoutService layoutService;
    @Autowired
    private FunctionProxy functionProxy;
    @Autowired
    private RecordTypeLogicService recordTypeLogicService;
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    private FunctionPrivilegeProxy functionPrivilegeProxy;
    @Autowired
    private FunctionPrivilegeProviderManager providerManager;
    @Autowired
    private RebateOutcomeDetailManager rebateOutcomeDetailManager;
    @Autowired
    private ApprovalInitProxy approvalInitProxy;

    @Override
    public EmptyResult enableCustomerAccountByCurl(CurlModel.TenantIds arg, ServiceContext serviceContext) {
        List<CustomerAccountConfig> openingList;
        List<String> tenantIds = arg.getTenantIds();
        if (CollectionUtils.isNotEmpty(tenantIds)) {
            openingList = customerAccountConfigDao.findEnterpriseWithCustomerAccountOpeningByTenantIds(tenantIds);
        } else {
            openingList = customerAccountConfigDao.findEnterpriseWithCustomerAccountOpening();
        }
        for (CustomerAccountConfig customerAccountConfig : openingList) {
            log.debug("customerAccountConfig:{}", JsonUtil.toJson(customerAccountConfig));
            try {
                boolean success = customerAccountManager.batchInitCustomerAccounts(customerAccountConfig.getTenantId());
                log.info("set customerAccount Enable,for tenantId:{},result={}", customerAccountConfig.getTenantId(), success);
            } catch (Exception e) {
                log.error("error opening customerAccount,for tenantId:{}", customerAccountConfig.getTenantId());
            }
        }
        return new EmptyResult();
    }

    @Override
    public CurlModel.LackCustomerAccountInitResult initLackedCustomerAccountData(CurlModel.TenantIds arg, ServiceContext serviceContext) {
        CurlModel.LackCustomerAccountInitResult result = new CurlModel.LackCustomerAccountInitResult();
        List<String> resultTenantIds = Lists.newArrayList();
        //初始化客户账户
        int offset = 0;
        int limit = ConfigCenter.batchCreateSize;
        int fetchSize = 0;
        Set<String> tenantIds = Sets.newHashSet(arg.getTenantIds());
        tenantIds.add(serviceContext.getTenantId());
        for (String tenantId : tenantIds) {
            User tempUser = new User(tenantId, tenantId.equals(serviceContext.getTenantId()) ? serviceContext.getUser().getUserId() : User.SUPPER_ADMIN_USER_ID);
            do {
                List<IObjectData> customerObjectDatas = crmManager.listPlainCustomersFromPg(tempUser, null, offset, limit);
                Map<String, Integer> customerIdStatusMap = customerObjectDatas.stream().collect(Collectors.toMap(cusObjectData -> ObjectDataUtil.getReferenceId(cusObjectData, CustomerAccountConstants.Field.Customer.apiName), cob -> cob.get("status", Integer.class)));
                Map<String, String> customerIdLifeStatusMap = customerAccountManager.transferCustomerStatusToLifeStatus(tempUser, customerIdStatusMap);
                try {
                    customerAccountManager.batchInitCustomerAccountDatas(tempUser, customerIdLifeStatusMap);
                } catch (Exception e) {
                    log.warn("customerIdStatusMap=" + customerIdStatusMap, e);
                }
                fetchSize = customerIdStatusMap.size();
                offset += limit;
            } while (fetchSize == limit);
            resultTenantIds.add(tenantId);
            log.info("tenantId:{} lackCustomerAccountDatasInit success", tenantId);
        }
        result.setTenantIds(resultTenantIds);
        return result;
    }

    @Override
    public CurlModel.TenantIds listTenantIdsOfLackCustomerAccountDatas(CurlModel.TenantIds arg, ServiceContext serviceContext) {
        CurlModel.TenantIds result = new CurlModel.TenantIds();
        List<String> tenantIds = crmManager.listTenantIdsOfLackCustomerAccountDatas(arg.getTenantIds());
        result.setTenantIds(tenantIds);
        return result;
    }

    @Override
    public CurlModel.QueryCustomerResult queryCustomersFromPg(CurlModel.QueryCustomerArg arg, ServiceContext serviceContext) {
        List<IObjectData> customerObjectDatas = crmManager.listPlainCustomersFromPg(serviceContext.getUser(), arg.getCustomerIds(), arg.getOffset(), arg.getLimit());
        CurlModel.QueryCustomerResult result = new CurlModel.QueryCustomerResult();
        result.setCustomerObjectDatas(ObjectDataDocument.ofList(customerObjectDatas));
        return result;
    }

    @Override
    public CurlModel.CustomerStatusBeforeInvalidResult listCustomerStatusBeforeInvalid(CurlModel.CustomerStatusBeforeInvalidArg arg, ServiceContext serviceContext) {
        Map<String, Integer> oldStatusMap = crmManager.listCustomerStatusBeforeInvalid(serviceContext.getUser(), arg.getCustomerIds());
        CurlModel.CustomerStatusBeforeInvalidResult result = new CurlModel.CustomerStatusBeforeInvalidResult();
        result.setLifeStatusBeforeInvalid(oldStatusMap);
        return result;
    }

    @Override
    public CurlModel.FixCustomerAccountLifeStatusResult fixCustomerAccountLifeStatus(CurlModel.FixCustomerAccountLifeStatusArg arg, ServiceContext serviceContext) {
        CurlModel.FixCustomerAccountLifeStatusResult result = new CurlModel.FixCustomerAccountLifeStatusResult();
        List<ObjectDataDocument> objectDataDocumentList = Lists.newArrayList();
        User user = serviceContext.getUser();
        int offset = 0;
        int limit = ConfigCenter.batchCreateSize;
        int fetchSize;
        do {
            List<IObjectData> recoverObjectDatas = Lists.newArrayList();
            List<IObjectData> customerObjectDatas = crmManager.listPlainCustomersFromPg(user, arg.getCustomerIds(), offset, limit);
            Map<String, Integer> customerIdStatusMap = customerObjectDatas.stream().collect(Collectors.toMap(cusObjectData -> ObjectDataUtil.getReferenceId(cusObjectData, CustomerAccountConstants.Field.Customer.apiName), cob -> cob.get("status", Integer.class)));
            Map<String, String> customerIdLifeStatusMap = customerAccountManager.transferCustomerStatusToLifeStatus(user, customerIdStatusMap);
            List<String> invalidCustomerIds = customerIdLifeStatusMap.entrySet().stream().filter(entry -> SystemConstants.LifeStatus.Invalid.value.equals(entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
            Map<String, String> customerLifeStatusBeforeInvalidMap = customerAccountManager.getCustomerLifeStatusBeforeInvalid(user, invalidCustomerIds);
            List<String> customerIds = Lists.newArrayList(customerIdStatusMap.keySet());
            List<IObjectData> customerAccountObjectDatas = customerAccountManager.listCustomerAccountIncludeInvalidByCustomerIds(user, customerIds);
            for (IObjectData customerAccountObjectData : customerAccountObjectDatas) {
                String customerId = ObjectDataUtil.getReferenceId(customerAccountObjectData, CustomerAccountConstants.Field.Customer.apiName);
                String dbLifeStatus = customerAccountObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                String fixLifeStatus = customerIdLifeStatusMap.get(customerId);
                if (!dbLifeStatus.equals(fixLifeStatus)) {
                    customerAccountObjectData.set(SystemConstants.Field.LifeStatus.apiName, fixLifeStatus);
                    if (SystemConstants.LifeStatus.Invalid.value.equals(fixLifeStatus)) {
                        customerAccountObjectData.set("life_status_before_invalid", customerLifeStatusBeforeInvalidMap.get(customerId));
                    }
                    log.info("customerAccountObjectData:{}", customerAccountObjectData.toJsonString());
                    IObjectData objectData = serviceFacade.updateObjectData(user, customerAccountObjectData, true);
                    if (SystemConstants.LifeStatus.Invalid.value.equals(fixLifeStatus) && !objectData.isDeleted()) {
                        objectData = serviceFacade.invalid(objectData, serviceContext.getUser());
                    }
                    if (customerAccountObjectData.isDeleted() && !fixLifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {
                        recoverObjectDatas.add(objectData);
                    }
                    objectDataDocumentList.add(ObjectDataDocument.of(objectData));
                }
            }
            if (CollectionUtils.isNotEmpty(recoverObjectDatas)) {
                recoverObjectDatas = serviceFacade.bulkRecover(recoverObjectDatas, user);
                log.info("recoverObjectDatas:{}", JsonUtil.toJson(recoverObjectDatas));
            }
            fetchSize = customerObjectDatas.size();
            offset += limit;
        } while (fetchSize == limit);
        result.setCustomerAccountObjectDatas(objectDataDocumentList);
        return result;
    }

    @Override
    public EmptyResult initPrepayLayoutRecordType(ServiceContext serviceContext) {
        initService.initPrepayLayoutRecordType(serviceContext);
        return new EmptyResult();
    }

    @Override
    public EmptyResult initApproval(CurlModel.ObjectApiNameArg arg, ServiceContext serviceContext) {
        ApprovalInitModel.Result result = initService.initApproval(arg.getObjectApiName(), HeaderUtil.getApprovalHeader(serviceContext.getUser()));
        log.info("fix initApproval result:{}", result);
        return new EmptyResult();
    }

    //客户账户结算方式，信用额度；预存款收入支出类型；返利收入的收入类型，刷描述开放指定功能
    @Override
    public EmptyResult updateSelectOneFieldDescribe(CurlModel.TenantIds tenantIdArg, ServiceContext serviceContext) {
        try {
            List<String> tenantIds = tenantIdArg.getTenantIds();
            if (tenantIds == null) {
                tenantIds = Lists.newArrayList();
            }
            if (!tenantIds.contains(serviceContext.getTenantId())) {
                tenantIds.add(serviceContext.getTenantId());
            }
            for (String tenantId : tenantIds) {
                //客户账户
                IObjectDescribe customerAccountDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, CustomerAccountConstants.API_NAME);
                SelectManyFieldDescribe settleTypeSelectManyFieldDescribe = (SelectManyFieldDescribe) customerAccountDescribe.getFieldDescribe(CustomerAccountConstants.Field.SettleType.apiName);
                CurrencyFieldDescribe creditQuotaCurrencyFieldDescribe = (CurrencyFieldDescribe) customerAccountDescribe.getFieldDescribe(CustomerAccountConstants.Field.CreditQuota.apiName);
                Map<String, Object> settleTypeFieldConfig = FieldConfig.builder().attrs(Lists.newArrayList("help_text", "default_value"), 1).build();
                Map<String, Object> creditQuotaFieldConfig = FieldConfig.builder().attrs(Lists.newArrayList("help_text", "default_value", "decimal_places", "max_length"), 1).build();
                settleTypeSelectManyFieldDescribe.setConfig(settleTypeFieldConfig);
                creditQuotaCurrencyFieldDescribe.setConfig(creditQuotaFieldConfig);
                IObjectDescribe updatedCustomerAccountDescribe = objectDescribeService.updateFieldDescribe(customerAccountDescribe, Lists.newArrayList(settleTypeSelectManyFieldDescribe, creditQuotaCurrencyFieldDescribe));
                log.info("tenantId:{},updatedCustomerAccountDescribe:{}", tenantId, updatedCustomerAccountDescribe.toJsonString());
                //预存款
                IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, PrepayDetailConstants.API_NAME);
                SelectOneFieldDescribe prepayIncomeField = (SelectOneFieldDescribe) objectDescribe.getFieldDescribe(PrepayDetailConstants.Field.IncomeType.apiName);
                SelectOneFieldDescribe prepayOutcomeField = (SelectOneFieldDescribe) objectDescribe.getFieldDescribe(PrepayDetailConstants.Field.OutcomeType.apiName);

                List<ISelectOption> prepayIncomSelectOptions = prepayIncomeField.getSelectOptions();
                prepayIncomSelectOptions.forEach(selectOption -> {
                    if (PrepayIncomeTypeEnum.OnlineCharge.getValue().equals(selectOption.getValue())) {

                    } else if (PrepayIncomeTypeEnum.OrderRefund.getValue().equals(selectOption.getValue())) {

                    } else if (PrepayIncomeTypeEnum.contain(selectOption.getValue())) {
                        Map<String, Object> config = getOptionConfig();
                        selectOption.set("config", config);
                    }
                });
                prepayIncomeField.setSelectOptions(prepayIncomSelectOptions);
                prepayIncomeField.setConfig(getSelectOneFieldConfig());

                List<ISelectOption> prepayOutcomeSelectOptions = prepayOutcomeField.getSelectOptions();
                prepayOutcomeSelectOptions.forEach(selectOption -> {
                    if (PrepayOutcomeTypeEnum.OffsetOrder.getValue().equals(selectOption.getValue())) {

                    } else if (PrepayOutcomeTypeEnum.contain(selectOption.getValue())) {
                        Map<String, Object> config = getOptionConfig();
                        selectOption.set("config", config);
                    }
                });
                prepayOutcomeField.setSelectOptions(prepayOutcomeSelectOptions);
                prepayOutcomeField.setConfig(getSelectOneFieldConfig());
                IObjectDescribe objectDescribeResult = objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(prepayIncomeField, prepayOutcomeField));
                log.info("prepayDetailObj update option describe,tennatId:{}, result:{}", tenantId, objectDescribeResult.toJsonString());

                //返利
                IObjectDescribe rebateIncomeDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, RebateIncomeDetailConstants.API_NAME);
                SelectOneFieldDescribe rebateIncomeField = (SelectOneFieldDescribe) rebateIncomeDescribe.getFieldDescribe(RebateIncomeDetailConstants.Field.IncomeType.apiName);

                List<ISelectOption> rebateIncomeSelectOptions = rebateIncomeField.getSelectOptions();
                for (ISelectOption selectOption : rebateIncomeSelectOptions) {
                    if (!Lists.newArrayList(RebateIncomeTypeEnum.OrderRefund.getValue(), RebateIncomeTypeEnum.OrderRebate.getValue()).contains(selectOption.getValue())) {
                        Map<String, Object> config = getOptionConfig();
                        selectOption.set("config", config);
                    }
                }
                rebateIncomeField.setSelectOptions(rebateIncomeSelectOptions);
                rebateIncomeField.setConfig(getSelectOneFieldConfig());
                IObjectDescribe rebateIncomeObjectDescribeResult = objectDescribeService.updateFieldDescribe(rebateIncomeDescribe, Lists.newArrayList(rebateIncomeField));
                log.info("rebateIncomeDetailObj update option describe,tenantId:{}, result:{}", tenantId, rebateIncomeObjectDescribeResult.toJsonString());
            }
        } catch (MetadataServiceException e) {
            log.warn("", e);
        }
        return new EmptyResult();
    }

    private Map<String, Object> getSelectOneFieldConfig() {
        Map<String, Object> config = Maps.newHashMap();
        Map<String, Object> attrMap = Maps.newHashMap();
        attrMap.put("options", 1);
        attrMap.put("default_value", 1);
        attrMap.put("is_required", 0);
        attrMap.put("is_readonly", 0);
        config.put("add", 1);
        config.put("attrs", attrMap);
        //        config.put("edit", 1);
        return config;
    }

    private Map<String, Object> getOptionConfig() {
        Map<String, Object> config = Maps.newHashMap();
        config.put("edit", 1);
        config.put("remove", 1);
        config.put("enable", 1);
        return config;
    }

    @Override
    public CurlModel.FixSelectOneFieldResult fixSelectOneFieldDescribe(CurlModel.FixSelectOneFieldArg arg, ServiceContext serviceContext) {
        CurlModel.FixSelectOneFieldResult result = new CurlModel.FixSelectOneFieldResult();
        if (CollectionUtils.isEmpty(arg.getTenantIds())) {
            arg.setTenantIds(Lists.newArrayList(serviceContext.getTenantId()));
        } else if (!arg.getTenantIds().contains(serviceContext.getTenantId())) {
            arg.getTenantIds().add(serviceContext.getTenantId());
        }
        List<String> fixedTenantIds = Lists.newArrayList();
        for (String tenantId : arg.getTenantIds()) {
            try {
                String fieldApiName = arg.getFieldApiName();
                List<Map<String, Object>> options = getOptions(arg.getObjectApiName(), fieldApiName);
                IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, arg.getObjectApiName());
                SelectOneFieldDescribe selectOneFieldDescribe = (SelectOneFieldDescribe) objectDescribe.getFieldDescribe(arg.getFieldApiName());
                selectOneFieldDescribe.set("options", options);
                objectDescribe = objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(selectOneFieldDescribe));
                fixedTenantIds.add(tenantId);
                log.info("fixPrepayOutcomeType objectDescribe:{}", objectDescribe);
            } catch (MetadataServiceException e) {
                log.warn("fixPrepayOutcomeType,tenantId:{}", tenantId, e);
            }
        }
        result.setTenantIds(fixedTenantIds);
        return result;
    }

    private List<Map<String, Object>> getOptions(String objectApiName, String fieldApiName) {
        List<Map<String, Object>> options = null;
        if (PrepayDetailConstants.API_NAME.equals(objectApiName)) {
            if (PrepayDetailConstants.Field.IncomeType.apiName.equals(fieldApiName)) {
                options = Arrays.stream(PrepayIncomeTypeEnum.values()).map(x -> {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("label", x.getLabel());
                    map.put("value", x.getValue());
                    map.put("not_usable", x.getNotUsable());
                    return map;
                }).collect(Collectors.toList());
            } else if (PrepayDetailConstants.Field.OutcomeType.apiName.equals(fieldApiName)) {
                options = Arrays.stream(PrepayOutcomeTypeEnum.values()).map(x -> {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("label", x.getLabel());
                    map.put("value", x.getValue());
                    map.put("not_usable", x.getNotUsable());
                    return map;
                }).collect(Collectors.toList());
            } else {
                throw new ValidateException(String.format("{%s}不存在", fieldApiName));
            }
        } else if (RebateIncomeDetailConstants.API_NAME.equals(objectApiName)) {
            if (RebateIncomeDetailConstants.Field.IncomeType.apiName.equals(fieldApiName)) {
                options = Arrays.stream(RebateIncomeTypeEnum.values()).map(x -> {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("label", x.getLabel());
                    map.put("value", x.getValue());
                    map.put("not_usable", x.getNotUsable());
                    return map;
                }).collect(Collectors.toList());
            } else {
                throw new ValidateException(String.format("{%s}不存在", fieldApiName));
            }
        }
        return options;
    }

    @Override
    public EmptyResult updateLayout(CurlModel.UpdateLayoutArg arg, ServiceContext serviceContext) {
        try {
            ILayout layout = layoutService.findByName(arg.getLayoutApiName(), serviceContext.getTenantId());
            layout.setIsShowFieldname(true);
            layout.setAgentType("agent_type_mobile");
            layout = layoutService.update(layout);
            log.warn("updateLayout user:{},layout:{}", serviceContext.getUser(), layout);
            return new EmptyResult();
        } catch (MetadataServiceException e) {
            log.warn("", e);
            throw new ValidateException("updatelayout error," + e.getMessage());
        }
    }

    @Override
    public CurlModel.AddOrderPaymentFieldResult addOrderPaymentField(CurlModel.AddOrderPaymentFieldArg arg, ServiceContext serviceContext) {
        CurlModel.AddOrderPaymentFieldResult result = new CurlModel.AddOrderPaymentFieldResult();

        try {

            //预存款增加orderPayment 字段<br>
            IObjectDescribe prepayDetailDescribe = serviceFacade.findObject(arg.getTenantId(), PrepayDetailConstants.API_NAME);
            ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OrderPayment.apiName).label(PrepayDetailConstants.Field.OrderPayment.label).required(false).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(PrepayDetailConstants.Field.OrderPayment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();
            objectDescribeService.addCustomFieldDescribe(prepayDetailDescribe, Lists.newArrayList(customerObjectReferenceFieldDescribe));

            //返利增加orderPayment 字段
            IObjectDescribe rebateOutcomeDetailDescribe = serviceFacade.findObject(arg.getTenantId(), RebateOutcomeDetailConstants.API_NAME);
            ObjectReferenceFieldDescribe orderPaymentReferenceField = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.OrderPayment.apiName).label(RebateOutcomeDetailConstants.Field.OrderPayment.label).required(false).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(RebateOutcomeDetailConstants.Field.OrderPayment.targetRelatedListName).targetRelatedListLabel(RebateOutcomeDetailConstants.Field.OrderPayment.targetRelatedListLabel)
                    .build();
            objectDescribeService.addCustomFieldDescribe(rebateOutcomeDetailDescribe, Lists.newArrayList(orderPaymentReferenceField));

            User user = serviceContext.getUser();
            //            deletePaymentFieldOfPrepayDetail(user);
            //            deletePaymentFieldOfRebateOutcome(user);
            updateOrderPaymentLayout(user);
        } catch (MetadataServiceException e) {
            log.error("addOrderPaymentField->for tenantId:{},exception:{}", e);
        }
        return result;
    }

    @Override
    public CurlModel.DelPaymentFieldResult delPaymentField(ServiceContext serviceContext) {
        deletePaymentFieldOfPrepayDetail(serviceContext.getUser());
        deletePaymentFieldOfRebateOutcome(serviceContext.getUser());
        CurlModel.DelPaymentFieldResult result = new CurlModel.DelPaymentFieldResult();
        result.setSuccess(true);
        return result;
    }

    @Override
    public CurlModel.AddOrderPaymentFieldResult addPaymentField(CurlModel.AddOrderPaymentFieldArg arg, ServiceContext serviceContext) {
        CurlModel.AddOrderPaymentFieldResult result = new CurlModel.AddOrderPaymentFieldResult();

        try {

            //预存款增加orderPayment 字段<br>
            IObjectDescribe prepayDetailDescribe = serviceFacade.findObject(arg.getTenantId(), PrepayDetailConstants.API_NAME);

            ObjectReferenceFieldDescribe paymentFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Payment.apiName).label(PrepayDetailConstants.Field.Payment.label).required(false).targetApiName(SystemConstants.PaymentApiName).targetRelatedListName(PrepayDetailConstants.Field.Payment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.Payment.targetRelatedListLabel).build();
            objectDescribeService.addCustomFieldDescribe(prepayDetailDescribe, Lists.newArrayList(paymentFieldDescribe));

            //返利增加orderPayment 字段
            IObjectDescribe rebateOutcomeDetailDescribe = serviceFacade.findObject(arg.getTenantId(), RebateOutcomeDetailConstants.API_NAME);

            //FIXME 临时 可以删除掉
            ObjectReferenceFieldDescribe rebatePaymentFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.Payment.apiName).label(RebateOutcomeDetailConstants.Field.Payment.label).required(false).targetApiName(SystemConstants.PaymentApiName).targetRelatedListName(RebateOutcomeDetailConstants.Field.Payment.targetRelatedListName).targetRelatedListLabel(RebateOutcomeDetailConstants.Field.Payment.targetRelatedListLabel).build();
            objectDescribeService.addCustomFieldDescribe(rebateOutcomeDetailDescribe, Lists.newArrayList(rebatePaymentFieldDescribe));

        } catch (MetadataServiceException e) {
            log.error("addOrderPaymentField->for tenantId:{},exception:{}", e);
        }
        return result;
    }

    private void updateOrderPaymentLayout(User user) {
        IObjectDescribe rebateOutcomeDescribe = serviceFacade.findObject(user.getTenantId(), RebateOutcomeDetailConstants.API_NAME);

        IObjectDescribe prepayDetailDescribe = serviceFacade.findObject(user.getTenantId(), PrepayDetailConstants.API_NAME);

        ILayout prepayOutcomeLayout = serviceFacade.findLayoutByApiName(user, PrepayDetailConstants.OUTCOME_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
        prepayOutcomeLayout = InitUtil.updatePrepayDetailLayoutForOrderPaymentReplace(user, prepayOutcomeLayout);
        serviceFacade.updateLayout(user, prepayOutcomeLayout);

        ILayout prepayDefaultLayout = serviceFacade.findLayoutByApiName(user, PrepayDetailConstants.DEFAULT_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
        prepayDefaultLayout = InitUtil.updatePrepayDetailLayoutForOrderPaymentReplace(user, prepayDefaultLayout);
        serviceFacade.updateLayout(user, prepayDefaultLayout);

        ILayout rebateOutcomeDefaultLayout = serviceFacade.findLayoutByApiName(user, RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME, rebateOutcomeDescribe.getApiName());
        rebateOutcomeDefaultLayout = InitUtil.updateRebateOutcomeLayoutForOrderPaymentReplace(user, rebateOutcomeDefaultLayout);
        serviceFacade.updateLayout(user, rebateOutcomeDefaultLayout);

    }

    //删除payment字段
    private void deletePaymentFieldOfPrepayDetail(User user) {
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

    //删除payment字段
    private void deletePaymentFieldOfRebateOutcome(User user) {
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

    @Override
    public CurlModel.AddImportPrivilegeResult addImportFunctionPrivilegeToRole(CurlModel.AddImportPrivilegeArg arg1) {
        String[] tenantIdArray = arg1.getTenantIds().split(",");
        for (String tenantId : tenantIdArray) {
            log.debug("begin addImportPrivilege for tenantId:{}", tenantId);
            ServiceContext ctx = generateServiceContext(tenantId);
            //查询是否有导入权限,User user, String objectApiName, String actionCode<br>
            List<String> privileges = functionPrivilegeService.getHavePrivilegeRolesByActionCode(ctx.getUser(), CustomerAccountConstants.API_NAME, "Import");

            if (CollectionUtils.isEmpty(privileges)) {
                log.debug("没有初始化相关权限，现在添加 batchImport权限");
                addImportFunctionPrivilegeByObjctApinName(ctx.getUser(), CustomerAccountConstants.API_NAME);
                addImportFunctionPrivilegeByObjctApinName(ctx.getUser(), PrepayDetailConstants.API_NAME);
                addImportFunctionPrivilegeByObjctApinName(ctx.getUser(), RebateIncomeDetailConstants.API_NAME);
            }
        }
        CurlModel.AddImportPrivilegeResult result = new CurlModel.AddImportPrivilegeResult();
        result.setSuccess(true);
        return result;
    }

    @Override
    public CurlModel.AddImportPrivilegeResult delImportFunctionPrivilegeToRole(CurlModel.AddImportPrivilegeArg arg1) {
        String[] tenantIdArray = arg1.getTenantIds().split(",");
        for (String tenantId : tenantIdArray) {
            log.debug("begin addImportPrivilege for tenantId:{}", tenantId);
            ServiceContext ctx = generateServiceContext(tenantId);
            //查询是否有导入权限,User user, String objectApiName, String actionCode<br>
            List<String> privileges = functionPrivilegeService.getHavePrivilegeRolesByActionCode(ctx.getUser(), CustomerAccountConstants.API_NAME, "Import");

            if (CollectionUtils.isNotEmpty(privileges)) {
                log.debug("有初始化相关权限，现在删除batchImport权限");
                delImportFunctionPrivilegeByObjctApinName(ctx.getUser(), CustomerAccountConstants.API_NAME);
                delImportFunctionPrivilegeByObjctApinName(ctx.getUser(), PrepayDetailConstants.API_NAME);
                delImportFunctionPrivilegeByObjctApinName(ctx.getUser(), RebateIncomeDetailConstants.API_NAME);
            }
        }
        CurlModel.AddImportPrivilegeResult result = new CurlModel.AddImportPrivilegeResult();
        result.setSuccess(true);
        return result;
    }

    /**
     * 纯粹的删除 权限元数据<br>
     * @param arg
     * @return
     */
    @Override
    public CurlModel.AddImportPrivilegeResult delImportFunctionPrivilege(CurlModel.DelImportPrivilegeArg arg) {
        String[] tenantIdArray = arg.getTenantIds().split(",");
        for (String tenantId : tenantIdArray) {
            log.debug("begin addImportPrivilege for tenantId:{}", tenantId);
            ServiceContext ctx = generateServiceContext(tenantId);
            //查询是否有导入权限,User user, String objectApiName, String actionCode<br>
            functionPrivilegeService.deleteUserDefinedActionCode(ctx.getUser(), CustomerAccountConstants.API_NAME, "Import");
            functionPrivilegeService.deleteUserDefinedActionCode(ctx.getUser(), PrepayDetailConstants.API_NAME, "Import");
            functionPrivilegeService.deleteUserDefinedActionCode(ctx.getUser(), RebateIncomeDetailConstants.API_NAME, "Import");
        }

        CurlModel.AddImportPrivilegeResult result = new CurlModel.AddImportPrivilegeResult();
        result.setSuccess(true);
        return result;
    }

    private AuthContext buildAuthContext(User user) {
        return AuthContext.builder().appId("CRM").tenantId(user.getTenantId()).userId(user.getUserId()).build();
    }

    private void addImportFunctionPrivilegeByObjctApinName(User user, String objectApiName) {
        AuthContext authContext = buildAuthContext(user);
        FunctionPrivilegeProvider provider = this.providerManager.getProvider(objectApiName);

        //添加权限
        List<CreateFunctionPrivilege.FunctionPojo> functionPojos = getUserDefinedFunctionPojoList(authContext.getTenantId(), objectApiName);
        //这个是添加纯粹的权限
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(functionPojos)) {
            CreateFunctionPrivilege.Arg arg = CreateFunctionPrivilege.Arg.builder().authContext(authContext).functionPojoList(functionPojos).build();
            this.functionPrivilegeProxy.createFunctionPrivilege(arg);
        }

        //给角色添加权限<br>
        //回款财务
        String paymentFinacailRole = "00000000000000000000000000000002";
        //销售人员
        String salesRole = "00000000000000000000000000000015";
        addRoleFunctionPrivilege(authContext, "00000000000000000000000000000006", objectApiName);
        addRoleFunctionPrivilege(authContext, paymentFinacailRole, objectApiName);
        addRoleFunctionPrivilege(authContext, salesRole, objectApiName);
    }

    private void delImportFunctionPrivilegeByObjctApinName(User user, String objectApiName) {
        AuthContext authContext = buildAuthContext(user);
        FunctionPrivilegeProvider provider = this.providerManager.getProvider(objectApiName);

        //添加权限
        List<CreateFunctionPrivilege.FunctionPojo> functionPojos = getUserDefinedFunctionPojoList(authContext.getTenantId(), objectApiName);
        //这个是添加纯粹的权限
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(functionPojos)) {
            CreateFunctionPrivilege.Arg arg = CreateFunctionPrivilege.Arg.builder().authContext(authContext).functionPojoList(functionPojos).build();
            this.functionPrivilegeProxy.createFunctionPrivilege(arg);
        }

        //给角色添加权限<br>
        //回款财务
        String paymentFinacailRole = "00000000000000000000000000000002";
        //销售人员
        String salesRole = "00000000000000000000000000000015";
        delRoleFunctionPrivilege(authContext, "00000000000000000000000000000006", objectApiName);
        delRoleFunctionPrivilege(authContext, paymentFinacailRole, objectApiName);
        delRoleFunctionPrivilege(authContext, salesRole, objectApiName);
    }

    private void addRoleFunctionPrivilege(AuthContext authContext, String roleCode, String objectApiName) {
        FunctionPrivilegeProvider provider = this.providerManager.getProvider(objectApiName);
        String funcCode = provider.getFunctionCodeFromActionCode(objectApiName, "Import");
        List<String> funcCodeList = new ArrayList<>();
        funcCodeList.add(funcCode);
        this.addRoleFunctionPrivilege(authContext, roleCode, funcCodeList);

    }

    private void delRoleFunctionPrivilege(AuthContext authContext, String roleCode, String objectApiName) {
        FunctionPrivilegeProvider provider = this.providerManager.getProvider(objectApiName);
        String funcCode = provider.getFunctionCodeFromActionCode(objectApiName, "Import");
        List<String> funcCodeList = new ArrayList<>();
        funcCodeList.add(funcCode);
        this.delRoleFunctionPrivilege(authContext, roleCode, funcCodeList);

    }

    private void addRoleFunctionPrivilege(AuthContext authContext, String roleCode, List<String> addFuncCodes) {
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(addFuncCodes)) {
            com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Arg arg = com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Arg.builder().authContext(authContext).roleCode(roleCode).addFuncCode(addFuncCodes).build();
            com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Result result = this.functionPrivilegeProxy.updateRoleModifiedFuncPrivilege(arg);
            if (!result.isSuccess()) {
                log.error("addFunctionPrivilege error,arg:{},result:{}", arg, result);
            }
        }
    }

    private void delRoleFunctionPrivilege(AuthContext authContext, String roleCode, List<String> delFuncCodes) {
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(delFuncCodes)) {
            com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Arg arg = com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Arg.builder().authContext(authContext).roleCode(roleCode).delFuncCode(delFuncCodes).build();
            com.facishare.paas.appframework.privilege.dto.UpdateRoleModifiedFuncPrivilege.Result result = this.functionPrivilegeProxy.updateRoleModifiedFuncPrivilege(arg);
            if (!result.isSuccess()) {
                log.error("addFunctionPrivilege error,arg:{},result:{}", arg, result);
            }

        }
    }

    private List<CreateFunctionPrivilege.FunctionPojo> getUserDefinedFunctionPojoList(String tenantId, String apiName) {
        FunctionPrivilegeProvider provider = this.providerManager.getProvider(apiName);
        ArrayList userDefinedFunctionPojoList = Lists.newArrayList();
        String actionCode = ObjectAction.BATCH_IMPORT.getActionCode();
        CreateFunctionPrivilege.FunctionPojo pojo = new CreateFunctionPrivilege.FunctionPojo();
        pojo.setAppId("CRM");
        pojo.setTenantId(tenantId);
        pojo.setFuncType(Integer.valueOf(1));
        pojo.setParentCode("00000000000000000000000000000000");
        pojo.setFuncName(ObjectAction.of(actionCode).getActionLabel());
        pojo.setFuncCode(provider.getFunctionCodeFromActionCode(apiName, actionCode));
        userDefinedFunctionPojoList.add(pojo);

        return userDefinedFunctionPojoList;
    }

    private ServiceContext generateServiceContext(String tenantId) {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(Optional.of(new User(tenantId, User.SUPPER_ADMIN_USER_ID))).build();
        return new ServiceContext(requestContext, null, null);
    }

    private void delCustomerAccountFunccodeAndInit(ServiceContext serviceContext) {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(serviceContext.getTenantId()).userId(serviceContext.getUser().getUserId()).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return CustomerAccountConstants.API_NAME;
            } else {
                return CustomerAccountConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        log.debug("delFuncModel.Result result:{}", result);
        this.functionPrivilegeService.initFunctionPrivilege(serviceContext.getUser(), CustomerAccountConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(serviceContext.getUser(), CustomerAccountConstants.DETAIL_LAYOUT_API_NAME, serviceContext.getTenantId(), CustomerAccountConstants.API_NAME);
    }

    private void delPrepayDetailFuncCodeAndInit(ServiceContext serviceContext) {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(serviceContext.getTenantId()).userId(serviceContext.getUser().getUserId()).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return PrepayDetailConstants.API_NAME;
            } else {
                return PrepayDetailConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        log.debug("delPrepayDetailFuncCodeAndInit->delFuncModel.Result result:{}", result);
        this.functionPrivilegeService.initFunctionPrivilege(serviceContext.getUser(), PrepayDetailConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(serviceContext.getUser(), PrepayDetailConstants.DEFAULT_LAYOUT_API_NAME, serviceContext.getTenantId(), PrepayDetailConstants.API_NAME);
    }

    private void delRebateIncomeFuncCodeAndInit(ServiceContext serviceContext) {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(serviceContext.getTenantId()).userId(serviceContext.getUser().getUserId()).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return RebateIncomeDetailConstants.API_NAME;
            } else {
                return RebateIncomeDetailConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        log.debug("delRebateIncomeFuncCodeAndInit->delFuncModel.Result result:{}", result);
        this.functionPrivilegeService.initFunctionPrivilege(serviceContext.getUser(), RebateIncomeDetailConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(serviceContext.getUser(), RebateIncomeDetailConstants.DEFAULT_LAYOUT_API_NAME, serviceContext.getTenantId(), RebateIncomeDetailConstants.API_NAME);
    }

    @Override
    public CurlModel.ListLayoutResult fixRebateIncomeListLayout(CurlModel.TenantIds tenantIdArg, ServiceContext serviceContext) {
        List<String> tenantIds = tenantIdArg.getTenantIds();
        CurlModel.ListLayoutResult listLayoutResult = new CurlModel.ListLayoutResult();
        try {
            if (CollectionUtils.isEmpty(tenantIds)) {
                return listLayoutResult;
            }
            for (String tenantId : tenantIds) {
                ILayout layout = layoutService.findByName(RebateIncomeDetailConstants.LIST_LAYOUT_API_NAME, tenantId);
                if (CollectionUtils.isNotEmpty(layout.getComponents())) {
                    continue;
                }
                List<IComponent> components = Lists.newArrayList();
                List<ITableColumn> tableColumns = Lists.newArrayList();
                tableColumns.add(TableColumnBuilder.builder().name(RebateIncomeDetailConstants.Field.Customer.apiName).lableName(RebateIncomeDetailConstants.Field.Customer.label).renderType(SystemConstants.RenderType.ObjectReference.renderType).build());
                tableColumns.add(TableColumnBuilder.builder().name(RebateIncomeDetailConstants.Field.Amount.apiName).lableName(RebateIncomeDetailConstants.Field.Amount.label).renderType(SystemConstants.RenderType.Currency.renderType).build());
                tableColumns.add(TableColumnBuilder.builder().name(SystemConstants.Field.LifeStatus.apiName).lableName(SystemConstants.Field.LifeStatus.label).renderType(SystemConstants.RenderType.SelectOne.renderType).build());
                tableColumns.add(TableColumnBuilder.builder().name(RebateIncomeDetailConstants.Field.TransactionTime.apiName).lableName(RebateIncomeDetailConstants.Field.TransactionTime.label).renderType(SystemConstants.RenderType.DateTime.renderType).build());

                TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(RebateIncomeDetailConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
                components.add(tableComponent);
                layout.setComponents(components);
                layout = layoutService.update(layout);
                listLayoutResult.add(layout);
                log.info("updateLayout user:{},layout:{}", serviceContext.getUser(), layout);
            }
            return listLayoutResult;
        } catch (MetadataServiceException e) {
            log.warn("", e);
            throw new ValidateException("updatelayout error," + e.getMessage());
        }
    }

    @Override
    public EmptyResult fixRebateIncomeStartEndTimeLabelAndTransactionTime(CurlModel.TenantIds tenantIdArg, ServiceContext serviceContext) {
        List<String> tenantIds = tenantIdArg.getTenantIds();
        try {
            for (String tenantId : tenantIds) {
                IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, RebateIncomeDetailConstants.API_NAME);
                List<IFieldDescribe> fieldDescribeListToUpdate = Lists.newArrayList();
                IFieldDescribe startTimeFieldDescribe = objectDescribe.getFieldDescribe(RebateIncomeDetailConstants.Field.StartTime.apiName);
                IFieldDescribe endTimeFieldDescribe = objectDescribe.getFieldDescribe(RebateIncomeDetailConstants.Field.EndTime.apiName);
                if (!RebateIncomeDetailConstants.Field.StartTime.label.equals(startTimeFieldDescribe.getLabel())) {
                    startTimeFieldDescribe.setLabel(RebateIncomeDetailConstants.Field.StartTime.label);
                    fieldDescribeListToUpdate.add(startTimeFieldDescribe);
                }
                if (!RebateIncomeDetailConstants.Field.EndTime.label.equals(endTimeFieldDescribe.getLabel())) {
                    endTimeFieldDescribe.setLabel(RebateIncomeDetailConstants.Field.EndTime.label);
                    fieldDescribeListToUpdate.add(endTimeFieldDescribe);
                }
                if (CollectionUtils.isNotEmpty(fieldDescribeListToUpdate)) {
                    IObjectDescribe updateObjectDescribe = objectDescribeService.updateFieldDescribe(objectDescribe, fieldDescribeListToUpdate);
                    log.info("Updated ObjectDescribe :{}", updateObjectDescribe.toJsonString());
                }

                ILayout layout = layoutService.findByName(RebateIncomeDetailConstants.DEFAULT_LAYOUT_API_NAME, tenantId);
                if (Objects.nonNull(layout)) {
                    List<IComponent> components = Lists.newArrayList();
                    List<IFormField> formFields = Lists.newArrayList();
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Name.apiName).renderType(SystemConstants.RenderType.AutoNumber.renderType).required(true).readOnly(true).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Customer.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.IncomeType.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Amount.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.UsedRebate.apiName).renderType(SystemConstants.RenderType.Currency.renderType).required(false).readOnly(true).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.StartTime.apiName).renderType(SystemConstants.RenderType.Date.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.EndTime.apiName).renderType(SystemConstants.RenderType.Date.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.TransactionTime.apiName).renderType(SystemConstants.RenderType.DateTime.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Refund.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(true).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Remark.apiName).renderType(SystemConstants.RenderType.LongText.renderType).required(false).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(RebateIncomeDetailConstants.Field.Attach.apiName).renderType(SystemConstants.RenderType.FileAttachment.renderType).required(false).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.LifeStatus.apiName).renderType(SystemConstants.RenderType.SelectOne.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.RecordType.apiName).renderType(SystemConstants.RenderType.RecordType.renderType).required(true).readOnly(false).build());
                    formFields.add(FormFieldBuilder.builder().fieldName(SystemConstants.Field.Owner.apiName).renderType(SystemConstants.RenderType.Employee.renderType).required(true).readOnly(false).build());
                    FieldSection baseFieldSection = FieldSectionBuilder.builder().name(LayoutConstants.BASE_FIELD_SECTION_API_NAME).header(LayoutConstants.BASE_FIELD_SECTION_DISPLAY_NAME).showHeader(true).fields(formFields).build();
                    FieldSection systemFieldSection = InitUtil.getSystemFieldSection();
                    List<IFieldSection> fieldSections = Lists.newArrayList();
                    fieldSections.add(baseFieldSection);
                    fieldSections.add(systemFieldSection);

                    FormComponent formComponent = FormComponentBuilder.builder().name(LayoutConstants.FORM_COMPONENT_API_NAME).fieldSections(fieldSections).buttons(null).build();
                    components.add(formComponent);
                    layout.setComponents(components);
                    layout = layoutService.update(layout);
                    log.info("updated layout:{}", layout.toJsonString());
                }
            }
        } catch (MetadataServiceException e) {
            log.warn("", e);
        }
        return new EmptyResult();
    }

    @Override
    public EmptyResult fixCustomerAccountRelateBalance(CurlModel.FixCustomerAccountBalanceArg arg, ServiceContext serviceContext) {
        ObjectDataDocument objectDataDocument = arg.getObjectDataDocument();
        IObjectData objectData = new ObjectData(objectDataDocument);
        String apiName = objectData.getDescribeApiName();
        String id = objectData.getId();
        if (StringUtils.isEmpty(apiName) || StringUtils.isEmpty(id)) {
            return new EmptyResult();
        }
        objectData = serviceFacade.updateObjectData(serviceContext.getUser(), objectData, true);
        log.info("updated objectData:{}", objectData.toJsonString());
        return new EmptyResult();
    }

    @Override
    public CurlModel.FixSelectOneFieldResult findDescribeByApiName(CurlModel.FixSelectOneFieldArg arg, ServiceContext serviceContext) {
        IObjectDescribe objectDescribe = serviceFacade.findObject(serviceContext.getTenantId(), arg.getObjectApiName());
        CurlModel.FixSelectOneFieldResult fixSelectOneFieldResult = new CurlModel.FixSelectOneFieldResult();
        fixSelectOneFieldResult.setObjectDescribe(ObjectDescribeDocument.of(objectDescribe));
        return fixSelectOneFieldResult;
    }

    @Override
    public TenantIdModel.Result initRebateUseRule(TenantIdModel.Arg arg, ServiceContext serviceContext) {
        TenantIdModel.Result result = new TenantIdModel.Result();
        List<String> tenantIdsList = Lists.newArrayList();
        for (String tenantId : arg.getTenantIds()) {
            CustomerAccountType.CustomerAccountEnableSwitchStatus customerAccountEnableSwitchStatus = customerAccountConfigManager.getStatus(tenantId);
            if (customerAccountEnableSwitchStatus != CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE) {
                continue;
            }
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            Set<String> apiNames = Sets.newHashSet(RebateUseRuleConstants.API_NAME);
            Map<String, IObjectDescribe> describeMap = serviceFacade.findObjects(user.getTenantId(), apiNames);
            if (!describeMap.containsKey(RebateUseRuleConstants.API_NAME)) {
                initService.initRebateUseRule(user);
                tenantIdsList.add(tenantId);
            } else {
                log.info("already init tenantId:{}", tenantId);
            }
        }
        result.setTenantIds(tenantIdsList);
        return result;
    }

    @Override
    public TenantIdModel.Result addSelectOptionInRebateIncomeType(TenantIdModel.Arg arg, ServiceContext serviceContext) {
        TenantIdModel.Result result = new TenantIdModel.Result();
        if (CollectionUtils.isEmpty(arg.getTenantIds())) {
            return result;
        }
        List<String> failedTenantIds = Lists.newArrayList();
        for (String tenantId : arg.getTenantIds()) {
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            try {
                addSelectOptionInRebateIncomeType(user);
            } catch (Exception e) {
                log.warn("addSelectOptionInRebateIncomeType", e);
                failedTenantIds.add(tenantId);
            }
        }
        result.setTenantIds(failedTenantIds);
        return result;
    }

    @Override
    public TenantIdModel.Result addRebateUseRuleFieldInRebateOutcome(TenantIdModel.Arg arg, ServiceContext serviceContext) {
        TenantIdModel.Result result = new TenantIdModel.Result();
        if (CollectionUtils.isEmpty(arg.getTenantIds())) {
            return result;
        }
        List<String> failedTenantIds = Lists.newArrayList();
        for (String tenantId : arg.getTenantIds()) {
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            try {
                addRebateUseRuleFieldInRebateOutcome(user);
            } catch (Exception e) {
                log.warn("addSelectOptionInRebateIncomeType", e);
                failedTenantIds.add(tenantId);
            }
        }
        result.setTenantIds(failedTenantIds);
        return result;
    }

    private void addSelectOptionInRebateIncomeType(User user) throws MetadataServiceException {
        IObjectDescribe rebateIncomeObjectDescribe = serviceFacade.findObject(user.getTenantId(), RebateIncomeDetailConstants.API_NAME);
        SelectOneFieldDescribe fieldDescribe = (SelectOneFieldDescribe) rebateIncomeObjectDescribe.getFieldDescribe(RebateIncomeDetailConstants.Field.IncomeType.apiName);
        List<ISelectOption> selectOptions = fieldDescribe.getSelectOptions();
        boolean hasOrderRebate = false;
        for (ISelectOption selectOption : selectOptions) {
            if (!Lists.newArrayList(RebateIncomeTypeEnum.OrderRefund.getValue(), RebateIncomeTypeEnum.OrderRebate.getValue()).contains(selectOption.getValue())) {
                selectOption.set("config", getOptionConfig());
            }
            if (RebateIncomeTypeEnum.OrderRebate.getValue().equals(selectOption.getValue())) {
                hasOrderRebate = true;
            }
        }
        if (!hasOrderRebate) {
            selectOptions.add(SelectOptionBuilder.builder().label(RebateIncomeTypeEnum.OrderRebate.getLabel()).value(RebateIncomeTypeEnum.OrderRebate.getValue()).build());
        }
        fieldDescribe.setConfig(getSelectOneFieldConfig());
        fieldDescribe.setSelectOptions(selectOptions);
        rebateIncomeObjectDescribe = objectDescribeService.updateFieldDescribe(rebateIncomeObjectDescribe, Lists.newArrayList(fieldDescribe));
        log.info("addSelectOptionInRebateIncomeType:{}", rebateIncomeObjectDescribe.toJsonString());
    }

    private void addRebateUseRuleFieldInRebateOutcome(User user) throws MetadataServiceException {
        IObjectDescribe rebateOutcomeObjectDescribe = serviceFacade.findObject(user.getTenantId(), RebateOutcomeDetailConstants.API_NAME);
        IFieldDescribe rebateUseRuleField = rebateOutcomeObjectDescribe.getFieldDescribe(RebateOutcomeDetailConstants.Field.RebateUseRule.apiName);
        if (rebateUseRuleField == null) {
            IFieldDescribe toAddFieldDescribe = getRebateUseField();
            rebateOutcomeObjectDescribe = objectDescribeService.addCustomFieldDescribe(rebateOutcomeObjectDescribe, Lists.newArrayList(toAddFieldDescribe));
            log.info("addRebateUseRuleField:{}", rebateOutcomeObjectDescribe.toJsonString());
            ILayout layout = serviceFacade.findLayoutByApiName(user, RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME, rebateOutcomeObjectDescribe.getApiName());
            LayoutExt layoutExt = LayoutExt.of(layout);
            FieldLayoutPojo fieldLayoutPojo = new FieldLayoutPojo();
            fieldLayoutPojo.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
            fieldLayoutPojo.setRequired(false);
            fieldLayoutPojo.setReadonly(false);
            layoutExt.addField(toAddFieldDescribe, fieldLayoutPojo);
            layout = serviceFacade.updateLayout(user, layout);
            log.info("addRebateUseRuloLayout:{}", layout.toJsonString());
        }
    }

    private IFieldDescribe getRebateUseField() {
        ObjectReferenceFieldDescribe rebateUseRuleObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.RebateUseRule.apiName).label(RebateOutcomeDetailConstants.Field.RebateUseRule.label).required(false).targetApiName(RebateUseRuleConstants.API_NAME).targetRelatedListName(RebateOutcomeDetailConstants.Field.RebateUseRule.targetRelatedListName)
                .targetRelatedListLabel(RebateOutcomeDetailConstants.Field.RebateUseRule.targetRelatedListLabel).build();
        return rebateUseRuleObjectReferenceFieldDescribe;
    }

    //返利收入 新增字段lookup销售订单，detail_layout也增加,list layout不增加
    @Override
    public TenantIdModel.Result addSalesOrderAndRebateUseRuleField(TenantIdModel.Arg arg, ServiceContext serviceContext) {
        TenantIdModel.Result result = new TenantIdModel.Result();
        List<String> tenantIds = arg.getTenantIds();
        List<String> failedTenantIds = Lists.newArrayList();
        for (String tenantId : tenantIds) {
            CustomerAccountType.CustomerAccountEnableSwitchStatus customerAccountEnableSwitchStatus = customerAccountConfigManager.getStatus(tenantId);
            if (customerAccountEnableSwitchStatus != CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE) {
                continue;
            }
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            IObjectDescribe rebateIncomeObjectDescribe = serviceFacade.findObject(tenantId, RebateIncomeDetailConstants.API_NAME);
            try {
                IFieldDescribe fieldDescribe = rebateIncomeObjectDescribe.getFieldDescribe(RebateIncomeDetailConstants.Field.SalesOrder.apiName);
                if (fieldDescribe == null) {
                    IFieldDescribe salesOrderField = getSalesOrderField();
                    rebateIncomeObjectDescribe = objectDescribeService.addCustomFieldDescribe(rebateIncomeObjectDescribe, Lists.newArrayList(salesOrderField));
                    log.info("addSalesOrderField:{}", rebateIncomeObjectDescribe.toJsonString());
                    ILayout layout = serviceFacade.findLayoutByApiName(user, RebateIncomeDetailConstants.DEFAULT_LAYOUT_API_NAME, rebateIncomeObjectDescribe.getApiName());
                    LayoutExt layoutExt = LayoutExt.of(layout);
                    FieldLayoutPojo fieldLayoutPojo = new FieldLayoutPojo();
                    fieldLayoutPojo.setRenderType(SystemConstants.RenderType.ObjectReference.renderType);
                    fieldLayoutPojo.setRequired(false);
                    fieldLayoutPojo.setReadonly(false);
                    layoutExt.addField(salesOrderField, fieldLayoutPojo);
                    layout = serviceFacade.updateLayout(user, layout);
                    log.info("updateLayout:{}", layout.toJsonString());
                    //返利收入类型 增加订单返利选项
                    addSelectOptionInRebateIncomeType(user);
                    //返利支出 新增返利使用规则字段
                    addRebateUseRuleFieldInRebateOutcome(user);
                }
            } catch (MetadataServiceException e) {
                failedTenantIds.add(tenantId);
                log.warn("", e);
            }
        }
        result.setTenantIds(failedTenantIds);
        return result;
    }

    private IFieldDescribe getSalesOrderField() {
        ObjectReferenceFieldDescribe orderObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateIncomeDetailConstants.Field.SalesOrder.apiName).label(RebateIncomeDetailConstants.Field.SalesOrder.label).required(false).targetApiName(SystemConstants.SalesOrderApiName).targetRelatedListName(RebateIncomeDetailConstants.Field.SalesOrder.targetRelatedListName).targetRelatedListLabel(RebateIncomeDetailConstants.Field.SalesOrder.targetRelatedListLabel).build();
        return orderObjectReferenceFieldDescribe;
    }

    @Data
    public static class TenantIdModel {
        private int offset = 0;
        private int limit = 20;

        @Data
        public static class Arg {
            private List<String> tenantIds;
        }

        @Data
        public static class Result {
            private List<String> tenantIds;
        }
    }

    //修复返利收入  收入类型为空bug 和引起的返利收入金额不对的问题
    @Override
    public EmptyResult fixRebateAmountByRebateIncomeTypeNull(CurlModel.RebateIncomeIdArg arg, ServiceContext serviceContext) {
        List<String> rebateIncomeIds = arg.getRebateIncomeIds();
        if (CollectionUtils.isEmpty(rebateIncomeIds)) {
            return new EmptyResult();
        }
        List<IObjectData> rebateIncomeObjectDatas = serviceFacade.findObjectDataByIdsIncludeDeleted(serviceContext.getUser(), rebateIncomeIds, RebateIncomeDetailConstants.API_NAME);
        Map<String, IObjectData> rebateIncomeIdDataMap = rebateIncomeObjectDatas.stream().collect(Collectors.toMap(objectData -> objectData.getId(), x -> x));
        for (String rebateIncomeId : rebateIncomeIds) {
            int limit = 100;
            int offset = 0;
            int size;
            IObjectData rebateIncomeObjectData = rebateIncomeIdDataMap.get(rebateIncomeId);
            log.info("RebateIncomeObjectData:{}", rebateIncomeObjectData.toJsonString());
            String rebateIncomeType = rebateIncomeObjectData.get(RebateIncomeDetailConstants.Field.IncomeType.apiName, String.class);
            if (StringUtils.isNotEmpty(rebateIncomeType)) {
                continue;
            }
            do {
                QueryResult<IObjectData> objectDataQueryResult = rebateOutcomeDetailManager.queryInvalidDataByField(serviceContext.getUser(), RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, Lists.newArrayList(rebateIncomeIds), offset, limit);
                if (CollectionUtils.isEmpty(objectDataQueryResult.getData())) {
                    size = 0;
                    String rebateIncomeLifeStatusBeforeInvalid = rebateIncomeObjectData.get(SystemConstants.Field.LifeStatusBeforeInvalid.apiName, String.class);
                    String rebateIncomeLifeStatus = rebateIncomeObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                    if (SystemConstants.LifeStatus.Invalid.value.equals(rebateIncomeLifeStatus) && SystemConstants.LifeStatus.Ineffective.value.equals(rebateIncomeLifeStatusBeforeInvalid)) {
                        continue;
                    }
                    String customerId = ObjectDataUtil.getReferenceId(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.Customer.apiName);
                    IObjectData customerAccountObjectData = customerAccountManager.getCustomerAccountIncludeInvalidByCustomerId(serviceContext.getUser(), customerId).get();
                    log.info("CustomerAccountObjectData:{}", customerAccountObjectData.toJsonString());
                    BigDecimal customerAccountRebateAmount = ObjectDataUtil.getBigDecimal(customerAccountObjectData, CustomerAccountConstants.Field.RebateBalance.apiName);
                    BigDecimal customerAccountRebateLockAmount = ObjectDataUtil.getBigDecimal(customerAccountObjectData, CustomerAccountConstants.Field.RebateLockedBalance.apiName);

                    BigDecimal availableRebate = ObjectDataUtil.getBigDecimal(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
                    BigDecimal usedRebate = ObjectDataUtil.getBigDecimal(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.UsedRebate.apiName);
                    BigDecimal amount = ObjectDataUtil.getBigDecimal(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.Amount.apiName);
                    List<ApprovalInstanceModel.Instance> instanceList = approvalInstance(serviceContext.getUser(), rebateIncomeId);
                    if (SystemConstants.LifeStatus.Ineffective.value.equals(rebateIncomeLifeStatus)) {
                        //新建有流程，驳回   钱不需要处理
                    } else if (SystemConstants.LifeStatus.UnderReview.value.equals(rebateIncomeLifeStatus)) {
                        //新建有流程     不需要处理
                    } else if (SystemConstants.LifeStatus.Normal.value.equals(rebateIncomeLifeStatus)) {
                        //新建有流程 新建无流程  处理方式一样
                        availableRebate = availableRebate.add(amount);
                        customerAccountRebateAmount = customerAccountRebateAmount.add(amount);
                    } else if (SystemConstants.LifeStatus.InChange.value.equals(rebateIncomeLifeStatus)) {
                        customerAccountRebateAmount = customerAccountRebateAmount.add(amount);
                        customerAccountRebateLockAmount = customerAccountRebateLockAmount.add(amount);
                    } else if (SystemConstants.LifeStatus.Invalid.value.equals(rebateIncomeLifeStatus)) {
                        if (hasInvalidApproval(instanceList)) {
                            customerAccountRebateAmount = customerAccountRebateAmount.add(amount);
                            customerAccountRebateLockAmount = customerAccountRebateLockAmount.add(amount);
                        }
                    }
                    //返利收入类型
                    rebateIncomeObjectData.set(RebateIncomeDetailConstants.Field.IncomeType.apiName, RebateIncomeTypeEnum.Other.getValue());
                    rebateIncomeObjectData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, availableRebate);
                    rebateIncomeObjectData.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, usedRebate);
                    rebateIncomeObjectData = serviceFacade.updateObjectData(serviceContext.getUser(), rebateIncomeObjectData, true);
                    log.info("fix rebateIncomeObjectData:{}", rebateIncomeObjectData.toJsonString());
                    BigDecimal customerAccountAvailableAmount = customerAccountRebateAmount.subtract(customerAccountRebateLockAmount);
                    customerAccountObjectData.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, customerAccountAvailableAmount);
                    customerAccountObjectData.set(CustomerAccountConstants.Field.RebateBalance.apiName, customerAccountRebateAmount);
                    customerAccountObjectData.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, customerAccountRebateLockAmount.add(amount));
                    customerAccountObjectData = serviceFacade.updateObjectData(serviceContext.getUser(), customerAccountObjectData, true);
                    log.info("fix customerAccountObjectData:{}", customerAccountObjectData.toJsonString());
                } else {
                    for (IObjectData objectData : objectDataQueryResult.getData()) {
                        log.info("RebateOutcomeObjectData:{}", objectData.toJsonString());
                        String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                        //String rebateIncomeId = ObjectDataUtil.getReferenceId(objectData, RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName);

                        String customerId = ObjectDataUtil.getReferenceId(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.Customer.apiName);

                        IObjectData customerAccountObjectData = customerAccountManager.getCustomerAccountIncludeInvalidByCustomerId(serviceContext.getUser(), customerId).get();
                        log.info("CustomerAccountObjectData:{}", customerAccountObjectData.toJsonString());
                        BigDecimal amount = ObjectDataUtil.getBigDecimal(objectData, RebateOutcomeDetailConstants.Field.Amount.apiName);
                        String orderPaymentId = objectData.getId();
                        List<ApprovalInstanceModel.Instance> instanceList = getCustomerPaymentApprovalIntancesByOrderPyamentId(serviceContext.getUser(), orderPaymentId);
                        BigDecimal customerAccountRebateAmount = ObjectDataUtil.getBigDecimal(customerAccountObjectData, CustomerAccountConstants.Field.RebateBalance.apiName);
                        BigDecimal customerAccountRebateLockAmount = ObjectDataUtil.getBigDecimal(customerAccountObjectData, CustomerAccountConstants.Field.RebateLockedBalance.apiName);
                        BigDecimal availableRebate = ObjectDataUtil.getBigDecimal(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
                        BigDecimal usedRebate = ObjectDataUtil.getBigDecimal(rebateIncomeObjectData, RebateIncomeDetailConstants.Field.UsedRebate.apiName);
                        if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                            availableRebate = availableRebate.add(amount.negate());
                            usedRebate = usedRebate.add(amount);
                            if (hasCreateApproval(instanceList)) {
                                customerAccountRebateLockAmount = customerAccountRebateLockAmount.add(amount);
                            } else {
                                customerAccountRebateAmount = customerAccountRebateAmount.add(amount.negate());
                            }
                        } else if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
                            availableRebate = availableRebate.add(amount.negate());
                            usedRebate = usedRebate.add(amount);
                            customerAccountRebateLockAmount = customerAccountRebateLockAmount.add(amount);
                        } else if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                            availableRebate = availableRebate.add(amount.negate());
                            usedRebate = usedRebate.add(amount);
                            if (hasCreateApproval(instanceList)) {
                                customerAccountRebateLockAmount = customerAccountRebateLockAmount.add(amount);
                            } else {
                                customerAccountRebateAmount = customerAccountRebateAmount.add(amount.negate());
                            }
                        } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
                            if (hasCreateApproval(instanceList)) {
                                /*if (hasInvalidApproval(instanceList)) {
                                
                                } else {
                                
                                }*/
                                //是否有作废审批流 处理方式相同
                                customerAccountRebateAmount = customerAccountRebateAmount.add(amount);
                                customerAccountRebateLockAmount = customerAccountRebateLockAmount.add(amount);
                            } else {
                                //无需处理
                                /* if (hasInvalidApproval(instanceList)) {
                                
                                } else {
                                
                                }*/
                            }
                        }
                        rebateIncomeObjectData.set(RebateIncomeDetailConstants.Field.IncomeType.apiName, RebateIncomeTypeEnum.Other.getValue());
                        rebateIncomeObjectData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, availableRebate);
                        rebateIncomeObjectData.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, usedRebate);
                        rebateIncomeObjectData = serviceFacade.updateObjectData(serviceContext.getUser(), rebateIncomeObjectData, true);
                        rebateIncomeIdDataMap.put(rebateIncomeId, rebateIncomeObjectData);

                        BigDecimal customerAccountAvailableAmount = customerAccountRebateAmount.subtract(customerAccountRebateLockAmount);
                        customerAccountObjectData.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, customerAccountAvailableAmount);
                        customerAccountObjectData.set(CustomerAccountConstants.Field.RebateBalance.apiName, customerAccountRebateAmount);
                        customerAccountObjectData.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, customerAccountRebateLockAmount.add(amount));
                        serviceFacade.updateObjectData(serviceContext.getUser(), customerAccountObjectData, true);
                    }
                    size = objectDataQueryResult.getData().size();
                }
                offset += limit;
            } while (size == limit);
        }
        return new EmptyResult();
    }

    public List<ApprovalInstanceModel.Instance> approvalInstance(User user, String dataId) {
        ApprovalInstanceModel.Arg arg = new ApprovalInstanceModel.Arg();
        arg.setObjectId(dataId);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", user.getUserId());
        headers.put("x-tenant-id", user.getTenantId());
        ApprovalInstanceModel.Result result = approvalInitProxy.approvalInstance(arg, headers);
        if (!result.success()) {
            throw new ValidateException(result.getMessage());
        }
        List<ApprovalInstanceModel.Instance> list = result.getData().stream().sorted(Comparator.comparing(ApprovalInstanceModel.Instance::getCreateTime).reversed()).collect(Collectors.toList());
        return list;
    }

    public boolean hasCreateApproval(List<ApprovalInstanceModel.Instance> list) {
        return list.stream().anyMatch(instance -> ApprovalFlowTriggerType.CREATE.getId().equals(instance.getTriggerType()));
    }

    public boolean hasInvalidApproval(List<ApprovalInstanceModel.Instance> list) {
        return list.stream().anyMatch(instance -> ApprovalFlowTriggerType.INVALID.getId().equals(instance.getTriggerType()));
    }

    public List<ApprovalInstanceModel.Instance> getCustomerPaymentApprovalIntancesByOrderPyamentId(User user, String orderPaymentId) {
        String paymentId = getCustomerPaymentIdsByOrderPaymentIds(user, Lists.newArrayList(orderPaymentId)).get(orderPaymentId);
        return approvalInstance(user, paymentId);
    }

    public Map<String, String> getCustomerPaymentIdsByOrderPaymentIds(User user, List<String> orderPaymentIds) {
        List<IObjectData> orderPaymentObjectDataList = serviceFacade.findObjectDataByIdsIncludeDeleted(user, orderPaymentIds, SystemConstants.OrderPaymentApiname);
        return orderPaymentObjectDataList.stream().collect(Collectors.toMap(objData -> objData.getId(), data -> data.get("payment_id", String.class)));
    }
}
