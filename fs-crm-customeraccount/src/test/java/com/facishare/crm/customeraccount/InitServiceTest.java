package com.facishare.crm.customeraccount;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.common.exception.CrmCheckedException;
import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.customeraccount.constants.*;
import com.facishare.crm.customeraccount.enums.PrepayIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.RebateIncomeTypeEnum;
import com.facishare.crm.customeraccount.enums.SettleTypeEnum;
import com.facishare.crm.customeraccount.predefine.CustomerAccountPredefineObject;
import com.facishare.crm.customeraccount.predefine.action.CustomerAccountAddAction;
import com.facishare.crm.customeraccount.predefine.manager.PrivilegeManager;
import com.facishare.crm.customeraccount.predefine.privilege.CustomerAccountFunctionPrivilegeProvider;
import com.facishare.crm.customeraccount.predefine.privilege.PrepayDetailFunctionPrivilegeProvider;
import com.facishare.crm.customeraccount.predefine.privilege.RebateIncomeDetailFunctionPrivilegeProvider;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.predefine.service.CustomerAccountService;
import com.facishare.crm.customeraccount.predefine.service.InitService;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.predefine.service.dto.GetByRefundIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.PrepayTransactionType;
import com.facishare.crm.customeraccount.util.HttpUtil;
import com.facishare.crm.customeraccount.util.InitUtil;
import com.facishare.crm.describebuilder.LayoutBuilder;
import com.facishare.crm.describebuilder.ObjectReferenceFieldDescribeBuilder;
import com.facishare.crm.describebuilder.TableColumnBuilder;
import com.facishare.crm.describebuilder.TableComponentBuilder;
import com.facishare.crm.rest.dto.ApprovalInitModel;
import com.facishare.crm.valueobject.SessionContext;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.*;
import com.facishare.paas.appframework.core.predef.service.ObjectDesignerService;
import com.facishare.paas.appframework.core.predef.service.dto.objectDescribe.AddDescribeCustomField;
import com.facishare.paas.appframework.jaxrs.model.InnerAPIResult;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.LayoutLogicService;
import com.facishare.paas.appframework.metadata.RecordTypeLogicService;
import com.facishare.paas.appframework.metadata.dto.auth.*;
import com.facishare.paas.appframework.privilege.DataPrivilegeService;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeProxy;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.appframework.privilege.dto.AuthContext;
import com.facishare.paas.appframework.privilege.dto.CheckFunctionPrivilege;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.checker.CheckerResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.ILayoutService;
import com.facishare.paas.metadata.api.service.IObjectDataService;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.dispatcher.ObjectDataProxy;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.describe.AutoNumberFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.describe.QuoteFieldDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.Where;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.facishare.rest.proxy.util.JsonUtil;
import com.facishare.restful.client.exception.FRestClientException;
import com.fxiaoke.metadata.option.api.OptionService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class InitServiceTest {
    @Autowired
    private RecordTypeLogicService recordTypeLogicService;
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;
    @Autowired
    private DataPrivilegeService dataPrivilegeService;
    @Autowired
    private InitService initService;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private IObjectDataService objectDataService;
    @Autowired
    private ILayoutService layoutService;
    @Autowired
    private CustomerAccountService customerAccountService;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CustomerAccountAddAction customerAccountAddAction;
    @Autowired
    private PrivilegeManager privilegeManager;
    @Autowired
    private FunctionPrivilegeProxy functionPrivilegeProxy;
    @Autowired
    private ObjectDataProxy objectDataProxy;
    @Autowired
    private CrmManager crmManager;
    @Autowired
    private OptionService optionService;
    @Autowired
    private DescribeLogicService describeLogicService;

    @Autowired
    private ObjectDesignerService objectDesignerService;

    @Autowired
    private LayoutLogicService layoutLogicService;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    String tenantId = "55910";
    String fsUserId = "1000";
    String customerId = "eebe39d4fca743ed80802825279353f8";

    @Before
    public void initUser() {
        String invirentment = System.getProperty("spring.profiles.active");
        if (invirentment.equals("ceshi113")) {
            tenantId = "55910";
        } else if (invirentment.equals("fstest")) {
            tenantId = "7";
        }
    }

    @Test
    public void initRuleTest() throws CrmCheckedException, MetadataServiceException {
        boolean flag = initService.initStartAndEndTimeRule(new User(tenantId, fsUserId), RebateUseRuleConstants.API_NAME, RebateUseRuleConstants.START_END_TIME_RULE_API_NAME, RebateUseRuleConstants.START_END_TIME_RULE_DISPLAY_NAME, RebateUseRuleConstants.Field.StartTime.apiName, RebateUseRuleConstants.Field.EndTime.apiName, RebateUseRuleConstants.START_END_TIME_RULE_DESCRIPTION);
        System.out.print(flag);
    }

    @Test
    public void queryDescribeTest() {
        Set<String> apiNames = Sets.newHashSet("test__c", CustomerAccountConstants.API_NAME, PrepayDetailConstants.API_NAME, RebateIncomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.API_NAME);
        Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);
        System.out.print(describeMap);
    }

    @Test
    public void optionTest() throws FRestClientException {
        String md5 = "0c5677729ed1e4b4982d81d909717a41";
        String optionValue = optionService.findByMd5(md5);
        System.out.println(optionValue);
    }

    @Test
    public void updatePrepayIncomeTypeOption() throws FRestClientException {
        String md5 = "034315c3f916da0055ab6557fe8ebf94";
        String value = "";
        String name = "收入类型";
        List<Map<String, Object>> options = Arrays.stream(PrepayIncomeTypeEnum.values()).map(x -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("label", x.getLabel());
            map.put("value", x.getValue());
            map.put("not_usable", x.getNotUsable());
            return map;
        }).collect(Collectors.toList());
        value = JsonUtil.toJson(options);
        String oldValue = optionService.findByMd5(md5);
        String resultMd5 = optionService.updateOption(tenantId, md5, value, name);
        System.out.println(resultMd5);
    }

    @Test
    public void updateRebateIncomeTypeOption() throws FRestClientException {
        String md5 = "06b84401ecbd76b9c340e31e5e1ef9bb";
        String value = "";
        String name = "收入类型";
        List<Map<String, Object>> options = Arrays.stream(RebateIncomeTypeEnum.values()).map(x -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("label", x.getLabel());
            map.put("value", x.getValue());
            map.put("not_usable", x.getNotUsable());
            return map;
        }).collect(Collectors.toList());
        value = JsonUtil.toJson(options);
        String oldValue = optionService.findByMd5(md5);
        String resultMd5 = optionService.updateOption(tenantId, md5, value, name);
        System.out.println(resultMd5);
    }

    @Test
    public void addPrepayDetailOrderPaymentIdFieldDescribeTest() throws MetadataServiceException {
        IObjectDescribe prepayDetailDescribe = objectDescribeService.findByTenantIdAndDescribeApiName("55910", PrepayDetailConstants.API_NAME);
        //        IFieldDescribe customerFieldDescribe = getQouteFieldDescribe("", "", "quote_customer_id_name__c", false, true);
        //        IFieldDescribe customerAccountFieldDescribe = getQouteFieldDescribe("", "", "quote_customer_account_id_name__c", true, true);
        //        IFieldDescribe paymentFieldDescribe = getQouteFieldDescribe("", "", "quote_payment_id_name__c", true, false);
        //        IFieldDescribe refundFieldDescribe = getQouteFieldDescribe("", "", "quote_refund_id_name__c", true, false);
        IFieldDescribe orderPaymentFieldDescribe = getQouteFieldDescribe("", "", "quote_order_payment_id_name__c", true, false);
        List<IFieldDescribe> addFeildDescribe = Lists.newArrayList(orderPaymentFieldDescribe);
        // List<IFieldDescribe> addFeildDescribe = Lists.newArrayList(customerFieldDescribe, customerAccountFieldDescribe, paymentFieldDescribe, refundFieldDescribe);
        prepayDetailDescribe.addFieldDescribeList(addFeildDescribe);
        prepayDetailDescribe = objectDescribeService.update(prepayDetailDescribe);
        System.out.println(prepayDetailDescribe);
    }

    @Test
    public void findByTenantId() throws FRestClientException {
        List<String> md5s = optionService.findByTenantId(tenantId);
        List<String> values = optionService.findByMd5s(md5s);
        Map<String, String> md5Map = Maps.newHashMap();
        for (int i = 0; i < md5s.size(); i++) {
            String md5Value = values.get(i);
            if (md5Value.contains("收入") && md5Value.contains("支出")) {
                md5Map.put(md5s.get(i), md5Value);
            }
        }
        System.out.println(md5Map);
        System.out.println(values);
    }

    @Test
    public void chectTest() {
        AuthContext authContext = AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build();
        List<String> functionCodes = Lists.newArrayList(CustomerAccountConstants.API_NAME, PrepayDetailConstants.API_NAME, RebateIncomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.API_NAME);
        CheckFunctionPrivilege.Arg arg = CheckFunctionPrivilege.Arg.builder().authContext(authContext).funcCodeLists(functionCodes).build();
        CheckFunctionPrivilege.Result checkResult = this.functionPrivilegeProxy.checkFunctionPrivilege(arg);
        System.out.println(checkResult);
    }

    @Test
    public void addCustomerAccountQuoteFieldDescrbeiTest() throws MetadataServiceException {
        IObjectDescribe customerAccountDescribe = objectDescribeService.findByTenantIdAndDescribeApiName("55910", CustomerAccountConstants.API_NAME);
        customerAccountDescribe.addFieldDescribe(getQouteFieldDescribe("", "", "quote_customer_id_name__c", true, true));
        customerAccountDescribe = objectDescribeService.update(customerAccountDescribe);
        System.out.println(customerAccountDescribe);
    }

    public void addPrepayDetailQuoteFieldDescribeTest() throws MetadataServiceException {
        IObjectDescribe prepayDetailDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, PrepayDetailConstants.API_NAME);
        IFieldDescribe customerFieldDescribe = getQouteFieldDescribe("", "", "quote_customer_id_name__c", false, true);
        IFieldDescribe customerAccountFieldDescribe = getQouteFieldDescribe("", "", "quote_customer_account_id_name__c", true, true);
        IFieldDescribe paymentFieldDescribe = getQouteFieldDescribe("", "", "quote_payment_id_name__c", true, false);
        IFieldDescribe refundFieldDescribe = getQouteFieldDescribe("", "", "quote_refund_id_name__c", true, false);
        List<IFieldDescribe> addFeildDescribe = Lists.newArrayList(customerFieldDescribe, customerAccountFieldDescribe, paymentFieldDescribe, refundFieldDescribe);
        prepayDetailDescribe.addFieldDescribeList(addFeildDescribe);
        prepayDetailDescribe = objectDescribeService.update(prepayDetailDescribe);
        System.out.println(prepayDetailDescribe);
    }

    private RequestContext getRequestContext() {
        //        String tenantId = "55910";
        //        String fsUserId = "1000";
        CustomerAccountPredefineObject.init();
        Optional<User> user = Optional.of(new User(tenantId, fsUserId));
        String postId = System.currentTimeMillis() + "";
        Map<Object, Object> map = new HashMap<>();
        RequestContext.RequestContextBuilder requestContextBuilder = RequestContext.builder();
        requestContextBuilder.tenantId(tenantId);
        requestContextBuilder.user(user);
        requestContextBuilder.contentType(RequestContext.ContentType.FULL_JSON);
        requestContextBuilder.postId(postId);
        requestContextBuilder.requestSource(RequestContext.RequestSource.CEP);
        RequestContext requestContext = requestContextBuilder.build();
        return requestContext;
    }

    @Test
    public void testAddCustomerField() {

        //ServiceContext context = ContextManager.buildServiceContext("describe", "addDescribeCustomField");
        String serviceName = "describe";
        String serviceMethod = "addDescribeCustomField";
        RequestContext requestContext = getRequestContext();
        ServiceContext serviceContext = new ServiceContext(requestContext, serviceName, serviceMethod);

        AddDescribeCustomField.Arg arg = new AddDescribeCustomField.Arg();
        arg.setDescribeAPIName(PrepayDetailConstants.API_NAME);

        ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OrderPayment.apiName).label(PrepayDetailConstants.Field.OrderPayment.label).required(true).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(PrepayDetailConstants.Field.OrderPayment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();
        arg.setField_describe(customerObjectReferenceFieldDescribe.toJsonString());

        List<IComponent> components = Lists.newArrayList();
        List<ITableColumn> tableColumns = Lists.newArrayList();
        //formFields.add(FormFieldBuilder.builder().fieldName(PrepayDetailConstants.Field.OrderPayment.apiName).renderType(SystemConstants.RenderType.ObjectReference.renderType).required(false).readOnly(false).build());
        tableColumns.add(TableColumnBuilder.builder().name(PrepayDetailConstants.Field.OrderPayment.apiName).lableName(PrepayDetailConstants.Field.OrderPayment.label).renderType(SystemConstants.RenderType.AutoNumber.renderType).build());
        TableComponent tableComponent = TableComponentBuilder.builder().refObjectApiName(PrepayDetailConstants.API_NAME).includeFields(tableColumns).buttons(null).build();
        components.add(tableComponent);
        ILayout layout = LayoutBuilder.builder().tenantId(tenantId).createBy(fsUserId).components(components).agentType(LayoutConstants.AGENT_TYPE).isDefault(false).layoutType(SystemConstants.LayoutType.List.layoutType).name(PrepayDetailConstants.LIST_LAYOUT_API_NAME).displayName(PrepayDetailConstants.LIST_LAYOUT_DISPLAY_NAME).refObjectApiName(PrepayDetailConstants.API_NAME).isShowFieldName(true).build();
        List<ILayout> layoutList = new ArrayList<>();
        layoutList.add(layout);
        String jsonListString = JSON.toJSONString(layoutList);
        arg.setLayout_list(jsonListString);

        AddDescribeCustomField.Result rs = objectDesignerService.addDescribeCustomField(arg, serviceContext);
        log.debug("rs=====>{}", rs);
    }

    /**
     * 2018-01-25测试OK<br>
     */
    @Test
    public void addOrderPaymentFieldForPrepayDetail() {
        IObjectDescribe describe = describeLogicService.findObject("55910", PrepayDetailConstants.API_NAME);
        ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.OrderPayment.apiName).label(PrepayDetailConstants.Field.OrderPayment.label).required(false).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(PrepayDetailConstants.Field.OrderPayment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();

        try {
            //调用元数据底层的方法<br>
            objectDescribeService.addCustomFieldDescribe(describe, Lists.newArrayList(customerObjectReferenceFieldDescribe));
        } catch (MetadataServiceException e) {
            e.printStackTrace();
            log.error("addOrderPaymentField->error:{}", e);
        }
    }

    /**
     * 2018-02-05测试OK<br>
     */
    @Test
    public void delOrderPaymentFieldForPrepayDetail() {
        IObjectDescribe describe = describeLogicService.findObject("55910", PrepayDetailConstants.API_NAME);
        ObjectReferenceFieldDescribe paymentFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(PrepayDetailConstants.Field.Payment.apiName).label(PrepayDetailConstants.Field.Payment.label).required(false).targetApiName(SystemConstants.PaymentApiName).targetRelatedListName(PrepayDetailConstants.Field.Payment.targetRelatedListName).targetRelatedListLabel(PrepayDetailConstants.Field.Payment.targetRelatedListLabel).build();

        List<IFieldDescribe> describeList = new ArrayList<>();
        describeList.add(paymentFieldDescribe);

        try {
            //调用元数据底层的方法<br>
            objectDescribeService.deleteCustomFieldDescribe(describe, describeList);
        } catch (MetadataServiceException e) {
            e.printStackTrace();
            log.error("addOrderPaymentField->error:{}", e);
        }
    }

    /**
     * 2018-01-25测试OK<br>
     */
    @Test
    public void addOrderPaymentFieldForRebateOutcomeDetail() {
        IObjectDescribe describe = describeLogicService.findObject("55910", RebateOutcomeDetailConstants.API_NAME);
        ObjectReferenceFieldDescribe customerObjectReferenceFieldDescribe = ObjectReferenceFieldDescribeBuilder.builder().apiName(RebateOutcomeDetailConstants.Field.OrderPayment.apiName).label(RebateOutcomeDetailConstants.Field.OrderPayment.label).required(true).targetApiName(SystemConstants.OrderPaymentApiname).targetRelatedListName(RebateOutcomeDetailConstants.Field.OrderPayment.targetRelatedListName)
                .targetRelatedListLabel(RebateOutcomeDetailConstants.Field.OrderPayment.targetRelatedListLabel).build();

        try {
            //调用元数据底层的方法<br>
            objectDescribeService.addCustomFieldDescribe(describe, Lists.newArrayList(customerObjectReferenceFieldDescribe));
        } catch (MetadataServiceException e) {
            e.printStackTrace();
            log.error("addOrderPaymentField->error:{}", e);
        }
    }

    @Test
    public void updateOrderPaymentLayout() {
        User user = new User("55910", fsUserId);

        IObjectDescribe rebateOutcomeDescribe = describeLogicService.findObject("55910", RebateOutcomeDetailConstants.API_NAME);

        IObjectDescribe prepayDetailDescribe = describeLogicService.findObject("55910", PrepayDetailConstants.API_NAME);

        ILayout prepayOutcomeLayout = layoutLogicService.findLayoutByApiName(user, PrepayDetailConstants.OUTCOME_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
        prepayOutcomeLayout = InitUtil.updatePrepayDetailLayoutForOrderPaymentReplace(user, prepayOutcomeLayout);
        layoutLogicService.updateLayout(user, prepayOutcomeLayout);

        ILayout prepayDefaultLayout = layoutLogicService.findLayoutByApiName(user, PrepayDetailConstants.DEFAULT_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
        prepayDefaultLayout = InitUtil.updatePrepayDetailLayoutForOrderPaymentReplace(user, prepayDefaultLayout);
        layoutLogicService.updateLayout(user, prepayDefaultLayout);

        ILayout rebateOutcomeDefaultLayout = layoutLogicService.findLayoutByApiName(user, RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME, rebateOutcomeDescribe.getApiName());
        rebateOutcomeDefaultLayout = InitUtil.updateRebateOutcomeLayoutForOrderPaymentReplace(user, rebateOutcomeDefaultLayout);
        layoutLogicService.updateLayout(user, rebateOutcomeDefaultLayout);

    }

    @Test
    public void updateOrderPaymentLayoutByGenerate() {
        User user = new User("55910", fsUserId);

        IObjectDescribe rebateOutcomeDescribe = describeLogicService.findObject("55910", RebateOutcomeDetailConstants.API_NAME);

        IObjectDescribe prepayDetailDescribe = describeLogicService.findObject("55910", PrepayDetailConstants.API_NAME);

        ILayout prepayOutcomeLayout = layoutLogicService.findLayoutByApiName(user, PrepayDetailConstants.OUTCOME_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
        prepayOutcomeLayout = InitUtil.generatePrepayOutcomeLayout(user.getTenantId(), user.getUserId());
        layoutLogicService.updateLayout(user, prepayOutcomeLayout);

        ILayout prepayDefaultLayout = layoutLogicService.findLayoutByApiName(user, PrepayDetailConstants.DEFAULT_LAYOUT_API_NAME, prepayDetailDescribe.getApiName());
        prepayDefaultLayout = InitUtil.updatePrepayDetailLayoutForOrderPaymentReplace(user, prepayDefaultLayout);
        layoutLogicService.updateLayout(user, prepayDefaultLayout);

        ILayout rebateOutcomeDefaultLayout = layoutLogicService.findLayoutByApiName(user, RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME, rebateOutcomeDescribe.getApiName());
        rebateOutcomeDefaultLayout = InitUtil.updateRebateOutcomeLayoutForOrderPaymentReplace(user, rebateOutcomeDefaultLayout);
        layoutLogicService.updateLayout(user, rebateOutcomeDefaultLayout);

    }

    @Test
    public void getDescribeRebateIncome() {
        User user = new User("69660", "1000");
        IObjectDescribe rebateIntcomeDescribe = describeLogicService.findObject("69660", RebateIncomeDetailConstants.API_NAME);
        RecordTypeFieldDescribe recoreTypeDescribe = (RecordTypeFieldDescribe) rebateIntcomeDescribe.getFieldDescribe(SystemConstants.Field.RecordType.apiName);
        List<IRecordTypeOption> recordTypeOption = recoreTypeDescribe.getRecordTypeOptions();
        log.debug("recordTypeOption:{}", recordTypeOption);
        log.debug("rebateIncomeDescribe:{}", rebateIntcomeDescribe);
    }

    public void addRebateIncomeQuoteFieldDescribeTest() throws MetadataServiceException {
        IObjectDescribe rebateIncomeDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, RebateIncomeDetailConstants.API_NAME);
        IFieldDescribe customerFieldDescribe = getQouteFieldDescribe("", "", "quote_customer_id_name__c", false, true);
        IFieldDescribe customerAccountFieldDescribe = getQouteFieldDescribe("", "", "quote_customer_account_id_name__c", true, true);
        IFieldDescribe refundFieldDescribe = getQouteFieldDescribe("", "", "quote_refund_id_name__c", true, false);
        List<IFieldDescribe> addFieldDescribe = Lists.newArrayList(customerFieldDescribe, customerAccountFieldDescribe, refundFieldDescribe);
        rebateIncomeDescribe.addFieldDescribeList(addFieldDescribe);
        rebateIncomeDescribe = objectDescribeService.update(rebateIncomeDescribe);
        System.out.println(rebateIncomeDescribe);
    }

    public void addRebateOutcomeQuoteFieldDescribeTest() throws MetadataServiceException {
        IObjectDescribe rebateDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, RebateOutcomeDetailConstants.API_NAME);
        IFieldDescribe rebateIncomeFieldDescribe = getQouteFieldDescribe("", "", "quote_rebate_income_id_name__c", true, false);
        IFieldDescribe paymentFieldDescribe = getQouteFieldDescribe("", "", "quote_payment_id_name__c", true, false);
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList(rebateIncomeFieldDescribe, paymentFieldDescribe);

        IObjectDescribe rebateOutcomeDetailDescribe = objectDescribeService.update(rebateDescribe);
        System.out.println(rebateOutcomeDetailDescribe);
    }

    private QuoteFieldDescribe getQouteFieldDescribe(String quoteField, String quoteFieldType, String apiName, boolean unique, boolean required) {
        QuoteFieldDescribe quoteFieldDescribe = new QuoteFieldDescribe();
        quoteFieldDescribe.setQuoteField(quoteField);
        quoteFieldDescribe.setQuoteFieldType(quoteFieldType);
        quoteFieldDescribe.setActive(true);
        quoteFieldDescribe.setApiName(apiName);
        quoteFieldDescribe.setDefineType("package");
        quoteFieldDescribe.setFieldNum(null);
        quoteFieldDescribe.setIsExtend(false);
        quoteFieldDescribe.setUnique(unique);
        quoteFieldDescribe.setCreateTime(new Date().getTime());
        quoteFieldDescribe.setIndex(true);
        quoteFieldDescribe.setRequired(required);
        quoteFieldDescribe.setStatus("released");
        return quoteFieldDescribe;
    }

    @Test
    public void privilegeTest() {

        AuthContext context = AuthContext.builder().appId("CRM").tenantId(tenantId).userId(fsUserId).build();
        List<String> functionCodes = Lists.newArrayList("27001", CustomerAccountConstants.API_NAME);
        CheckFunctionPrivilege.Arg arg = CheckFunctionPrivilege.Arg.builder().authContext(context).funcCodeLists(functionCodes).build();
        CheckFunctionPrivilege.Result result = functionPrivilegeProxy.checkFunctionPrivilege(arg);
        System.out.println(result);
    }

    @Test
    public void fieldPrivilegetTest() {
        AuthContext authContext = AuthContext.builder().appId("CRM").tenantId(tenantId).userId(fsUserId).build();
        com.facishare.paas.appframework.privilege.dto.GetFieldsPermission.Arg arg = com.facishare.paas.appframework.privilege.dto.GetFieldsPermission.Arg.builder().entityId(CustomerAccountConstants.API_NAME).authContext(authContext).build();

        com.facishare.paas.appframework.privilege.dto.GetFieldsPermission.Result result = functionPrivilegeProxy.getFieldsPermission(arg);
        System.out.println(result);
    }

    @Test
    public void queryTest() {
        //		012017-10-11
        RequestContext requestContext = RequestContext.builder().tenantId("2").user(java.util.Optional.of(new User("2", "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        User user = serviceContext.getUser();
        GetByRefundIdModel.Result result = new GetByRefundIdModel.Result();
        PrepayTransactionType.GetByPaymentIdArg testArg = new PrepayTransactionType.GetByPaymentIdArg();

        IFilter filter = new Filter();
        filter.setOperator(Operator.EQ);
        filter.setFieldName(CustomerAccountConstants.Field.Name.apiName);
        filter.setFieldValues(Lists.newArrayList("022017-10-13"));
        filter.setConnector(Where.CONN.OR.toString());
        List<IObjectData> objectDataList = serviceFacade.findDataWithWhere(user, CustomerAccountConstants.API_NAME, Lists.newArrayList(filter), Lists.newArrayList(), 0, 10);
        System.out.println(objectDataList);
    }

    @Test
    public void init() {
        String tenantId = "69636";
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        boolean flag = initService.init(serviceContext);
        System.out.println(flag);
    }

    @Test
    public void crmSwitchTest() {
        User user = new User("59769", "1000");
        boolean flag = crmManager.syncCustomerAccountSwitchToCrm(user);
        System.out.println(flag);
    }

    @Test
    public void initLeft() {
        User user = new User("55732", "1000");
        this.functionPrivilegeService.initFunctionPrivilege(user, CustomerAccountConstants.API_NAME);
        //        this.dataPrivilegeService.addCommonPrivilegeListResult(user, Lists.newArrayList(new ObjectDataPermissionInfo[] { new ObjectDataPermissionInfo(RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.DISPLAY_NAME, DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PRIVATE.getValue()) }));
        this.recordTypeLogicService.recordTypeInit(user, CustomerAccountConstants.DETAIL_LAYOUT_API_NAME, user.getTenantId(), CustomerAccountConstants.API_NAME);
        System.out.println();
    }

    @Test
    public void initApprovaRebateIncomelTest() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", "-10000");
        headers.put("x-tenant-id", tenantId);
        ApprovalInitModel.Result result = initService.initApproval(RebateIncomeDetailConstants.API_NAME, headers);
        System.out.println(result);
    }

    @Test
    public void initApprovaPrepayDetaillTest() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", "-10000");
        headers.put("x-tenant-id", "59769");
        ApprovalInitModel.Result result = initService.initApproval(RebateIncomeDetailConstants.API_NAME, headers);
        ApprovalInitModel.Result result2 = initService.initApproval(PrepayDetailConstants.API_NAME, headers);
        System.out.println(result);
    }

    @Test
    public void deleteCusomterAccountFuncTest() throws IOException {
        String url = "http://10.112.32.47:8006/fs-paas-auth/batchDelFunc";
        String apiName = CustomerAccountConstants.API_NAME;
        Map<String, Object> bodyMap = Maps.newHashMap();
        Map<String, Object> contextMap = Maps.newHashMap();
        contextMap.put("tenantId", "7");
        contextMap.put("appId", "CRM");
        contextMap.put("userId", "1000");
        bodyMap.put("authContext", contextMap);
        List<String> codes = new CustomerAccountFunctionPrivilegeProvider().getSupportedActionCodes();
        List<String> funcSet = codes.stream().map(actionCode -> {
            if (actionCode.equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return apiName;
            } else {
                return apiName + "||" + actionCode;
            }
        }).collect(Collectors.toList());
        bodyMap.put("funcSet", funcSet);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        InnerAPIResult result = HttpUtil.post(url, headers, bodyMap, InnerAPIResult.class);
        System.out.println(result);
    }

    @Test
    public void deletePrepayFuncTest() throws IOException {
        String url = "http://10.112.32.47:8006/fs-paas-auth/batchDelFunc";
        String apiName = PrepayDetailConstants.API_NAME;
        Map<String, Object> bodyMap = Maps.newHashMap();
        Map<String, Object> contextMap = Maps.newHashMap();
        contextMap.put("tenantId", "7");
        contextMap.put("appId", "CRM");
        contextMap.put("userId", "1000");
        bodyMap.put("authContext", contextMap);
        List<String> codes = new PrepayDetailFunctionPrivilegeProvider().getSupportedActionCodes();
        List<String> funcSet = codes.stream().map(actionCode -> {
            if (actionCode.equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return apiName;
            } else {
                return apiName + "||" + actionCode;
            }
        }).collect(Collectors.toList());
        bodyMap.put("funcSet", funcSet);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        InnerAPIResult result = HttpUtil.post(url, headers, bodyMap, InnerAPIResult.class);
        System.out.println(result);
    }

    @Test
    public void deleteRebateIncomeFuncTest() throws IOException {
        String url = "http://10.112.32.47:8006/fs-paas-auth/batchDelFunc";
        String apiName = RebateIncomeDetailConstants.API_NAME;
        Map<String, Object> bodyMap = Maps.newHashMap();
        Map<String, Object> contextMap = Maps.newHashMap();
        contextMap.put("tenantId", "7");
        contextMap.put("appId", "CRM");
        contextMap.put("userId", "1000");
        bodyMap.put("authContext", contextMap);
        List<String> codes = new RebateIncomeDetailFunctionPrivilegeProvider().getSupportedActionCodes();
        List<String> funcSet = codes.stream().map(actionCode -> {
            if (actionCode.equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return apiName;
            } else {
                return apiName + "||" + actionCode;
            }
        }).collect(Collectors.toList());
        bodyMap.put("funcSet", funcSet);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        InnerAPIResult result = HttpUtil.post(url, headers, bodyMap, InnerAPIResult.class);
        System.out.println(result);
    }

    @Test
    public void assignLayout() {

        AddRoleViewModel.Arg arg = new AddRoleViewModel.Arg();
        List<RoleViewPojo> roleViewPojos = Lists.newArrayList();

        SessionContext sessionContext = new SessionContext();
        sessionContext.setEId(55732L);
        sessionContext.setEa(tenantId);
        sessionContext.setUserId(1000);
        String entityId = RebateOutcomeDetailConstants.API_NAME;

        RoleInfoModel.Arg roleInfoArg = new RoleInfoModel.Arg();
        roleInfoArg.setAuthContext(new User(tenantId, fsUserId));
        RoleInfoModel.Result roleInfoResult = initService.roleInfo(roleInfoArg);

        String recordTypeId = "default__c";
        String viewId = RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME;
        List<RoleInfoPojo> roles = roleInfoResult.getResult().getRoles();
        for (RoleInfoPojo roleInfoPojo : roles) {
            RoleViewPojo roleViewPojo = new RoleViewPojo();
            roleViewPojo.setViewId(viewId);
            roleViewPojo.setRoleCode(roleInfoPojo.getRoleCode());
            roleViewPojo.setRecordTypeId(recordTypeId);
            roleViewPojo.setTenantId(tenantId);
            roleViewPojo.setEntityId(entityId);
            roleViewPojo.setAppId("CRM");
            roleViewPojos.add(roleViewPojo);
        }
        arg.setRoleViewPojos(roleViewPojos);

        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        arg.setAuthContext(serviceContext.getUser());
        AddRoleViewModel.Result result = initService.saveLayoutAssign(arg);
        System.out.println(result);
    }

    @Test
    public void assignRecord() {
        SessionContext sessionContext = new SessionContext();
        sessionContext.setEId(55732L);
        sessionContext.setEa("55732");
        sessionContext.setUserId(1000);
        String entityId = RebateOutcomeDetailConstants.API_NAME;

        List<RecordTypePojo> recordTypePojos = Lists.newArrayList();

        RoleInfoModel.Arg roleInfoArg = new RoleInfoModel.Arg();
        roleInfoArg.setAuthContext(new User(tenantId, fsUserId));
        RoleInfoModel.Result roleInfoResult = initService.roleInfo(roleInfoArg);

        String recordTypeId = "default__c";
        List<RoleInfoPojo> roles = roleInfoResult.getResult().getRoles();
        for (RoleInfoPojo roleInfoPojo : roles) {
            RecordTypePojo recordTypePojo = new RecordTypePojo();
            recordTypePojo.setRoleCode(roleInfoPojo.getRoleCode());
            recordTypePojo.setRecordTypeId(recordTypeId);
            recordTypePojo.setTenantId(tenantId);
            recordTypePojo.setAppId("CRM");
            recordTypePojos.add(recordTypePojo);
        }

        AddRoleRecordTypeModel.Arg arg = new AddRoleRecordTypeModel.Arg();
        arg.setEntityId(entityId);
        arg.setRecordTypeId(recordTypeId);
        arg.setRecordTypePojos(recordTypePojos);
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        arg.setAuthContext(serviceContext.getUser());
        AddRoleRecordTypeModel.Result addResult = initService.addRoleRecordType(arg);
        System.out.println(addResult);
    }

    @Test
    public void assignIncomeLayout() {

        AddRoleViewModel.Arg arg = new AddRoleViewModel.Arg();
        List<RoleViewPojo> roleViewPojos = Lists.newArrayList();

        SessionContext sessionContext = new SessionContext();
        sessionContext.setEId(55732L);
        sessionContext.setEa(tenantId);
        sessionContext.setUserId(1000);
        String entityId = RebateIncomeDetailConstants.API_NAME;

        RoleInfoModel.Arg roleInfoArg = new RoleInfoModel.Arg();
        roleInfoArg.setAuthContext(new User(tenantId, fsUserId));
        RoleInfoModel.Result roleInfoResult = initService.roleInfo(roleInfoArg);

        String recordTypeId = "default__c";
        String viewId = RebateIncomeDetailConstants.DEFAULT_LAYOUT_API_NAME;
        List<RoleInfoPojo> roles = roleInfoResult.getResult().getRoles();
        for (RoleInfoPojo roleInfoPojo : roles) {
            RoleViewPojo roleViewPojo = new RoleViewPojo();
            roleViewPojo.setViewId(viewId);
            roleViewPojo.setRoleCode(roleInfoPojo.getRoleCode());
            roleViewPojo.setRecordTypeId(recordTypeId);
            roleViewPojo.setTenantId(tenantId);
            roleViewPojo.setEntityId(entityId);
            roleViewPojo.setAppId("CRM");
            roleViewPojos.add(roleViewPojo);
        }
        arg.setRoleViewPojos(roleViewPojos);

        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        arg.setAuthContext(serviceContext.getUser());
        AddRoleViewModel.Result result = initService.saveLayoutAssign(arg);
        System.out.println(result);
    }

    @Test
    public void initPrepayLayoutRecordTypeTest() {
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        initService.initPrepayLayoutRecordType(serviceContext);
        System.out.println("");
    }

    @Test
    public void assignIncomeRecord() {
        SessionContext sessionContext = new SessionContext();
        sessionContext.setEId(55732L);
        sessionContext.setEa("55732");
        sessionContext.setUserId(1000);
        String entityId = RebateIncomeDetailConstants.API_NAME;

        List<RecordTypePojo> recordTypePojos = Lists.newArrayList();

        RoleInfoModel.Arg roleInfoArg = new RoleInfoModel.Arg();
        roleInfoArg.setAuthContext(new User(tenantId, fsUserId));
        RoleInfoModel.Result roleInfoResult = initService.roleInfo(roleInfoArg);

        String recordTypeId = "default__c";
        List<RoleInfoPojo> roles = roleInfoResult.getResult().getRoles();
        for (RoleInfoPojo roleInfoPojo : roles) {
            RecordTypePojo recordTypePojo = new RecordTypePojo();
            recordTypePojo.setRoleCode(roleInfoPojo.getRoleCode());
            recordTypePojo.setRecordTypeId(recordTypeId);
            recordTypePojo.setTenantId(tenantId);
            recordTypePojo.setAppId("CRM");
            recordTypePojos.add(recordTypePojo);
        }

        AddRoleRecordTypeModel.Arg arg = new AddRoleRecordTypeModel.Arg();
        arg.setEntityId(entityId);
        arg.setRecordTypeId(recordTypeId);
        arg.setRecordTypePojos(recordTypePojos);
        RequestContext requestContext = RequestContext.builder().tenantId(tenantId).user(java.util.Optional.of(new User(tenantId, "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        arg.setAuthContext(serviceContext.getUser());
        AddRoleRecordTypeModel.Result addResult = initService.addRoleRecordType(arg);
        System.out.println(addResult);
    }

    @Test
    public void roleInfo() {
        RoleInfoModel.Arg arg = new RoleInfoModel.Arg();
        SessionContext sessionContext = new SessionContext();
        sessionContext.setEId(2L);
        sessionContext.setEa("2");
        sessionContext.setUserId(1000);
        arg.setAuthContext(new User(tenantId, fsUserId));
        RoleInfoModel.Result result = initService.roleInfo(arg);
        System.out.println(result);
    }

    @Test
    public void getCustomerAccountDescribeTest() throws MetadataServiceException {
        IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, CustomerAccountConstants.API_NAME);
        String describeJson = objectDescribe.toJsonString();
        System.out.println("describeJson=" + describeJson);
    }

    @Test
    public void updateDescribe() throws MetadataServiceException {
        IObjectDescribe objectDescribeDraft = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, "CustomerAccountNewTest1");
        objectDescribeDraft.getFieldDescribes().stream().filter(fieldDescribe -> fieldDescribe.getApiName().equals("name")).map(fieldDescribe -> {
            fieldDescribe.setDefineType("system");
            return fieldDescribe;
        }).collect(Collectors.toSet());
        System.out.println(objectDescribeDraft);
        IObjectDescribe objectDescribeDraft1 = objectDescribeService.update(objectDescribeDraft);
    }

    @Test
    public void updateDescribeAll() throws MetadataServiceException {
        IObjectDescribe prepayDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, PrepayDetailConstants.API_NAME);
        AutoNumberFieldDescribe pt = (AutoNumberFieldDescribe) prepayDescribe.getFieldDescribe("name");
        pt.setPrefix("PT{yyyy}-{mm}-{dd}_");
        pt.setPostfix("");
        pt.setSerialNumber(4);
        pt.setDefaultValue("0001");
        IObjectDescribe prepayDescribe1 = objectDescribeService.update(prepayDescribe);

        IObjectDescribe rebateDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, RebateIncomeDetailConstants.API_NAME);
        AutoNumberFieldDescribe rt = (AutoNumberFieldDescribe) rebateDescribe.getFieldDescribe("name");
        rt.setPrefix("RT{yyyy}-{mm}-{dd}_");
        rt.setPostfix("");
        rt.setSerialNumber(4);
        rt.setDefaultValue("0001");
        IObjectDescribe rebateDescribe1 = objectDescribeService.update(rebateDescribe);
    }

    @Test
    public void updateLayout() throws MetadataServiceException {
        List<ILayout> layouts = layoutService.findByObjectDescribeApiNameAndTenantId(RebateIncomeDetailConstants.API_NAME, tenantId);
        for (ILayout layout : layouts) {
            for (IComponent component : layout.getComponents()) {
                FormComponent formComponent = (FormComponent) component;
                for (IFieldSection fieldSection : formComponent.getFieldSections()) {
                    fieldSection.getFields().forEach(section -> {
                        if (section.getFieldName().equals(RebateIncomeDetailConstants.Field.CustomerAccount.apiName)) {
                            section.setRenderType("object_reference");
                        }
                    });
                }
            }
        }
    }

    @Test
    public void deleteDescribeTest() throws MetadataServiceException {
        IObjectDescribe ob = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, CustomerAccountConstants.API_NAME);
        CheckerResult result = objectDescribeService.delete(ob);
        System.out.println(result);
    }

    @Test
    public void createCustomerAccountDataTest() throws MetadataServiceException {
        RequestContextManager.setContext(RequestContext.builder().postId("1000").build());
        IObjectData objectData = new ObjectData();
        objectData.setDescribeApiName(CustomerAccountConstants.API_NAME); //59c9b628422c901504ee4ecc
        //String id = "59e6f988422c901d1826185f"; //2017-10-18 19:49:10 查看的id
        String describeId = "59f2b4cc422c90113422b858"; // 2017年10月30日 添加查看<br>
        objectData.setDescribeId(describeId);//59c9b628422c901504ee4ecc

        objectData.setCreatedBy(fsUserId);
        objectData.setDeleted(false);
        objectData.setTenantId(tenantId);
        objectData.setRecordType("default__c");
        objectData.set(CustomerAccountConstants.Field.Customer.apiName, customerId);
        objectData.set(CustomerAccountConstants.Field.PrepayBalance.apiName, 200.0);
        objectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, 50.0);
        objectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, 150.0);
        objectData.set(CustomerAccountConstants.Field.RebateBalance.apiName, 100.0);
        objectData.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, "60.0");
        objectData.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, 40.0);
        objectData.set(CustomerAccountConstants.Field.SettleType.apiName, Lists.newArrayList(SettleTypeEnum.Cash.getValue()));
        objectData.set(CustomerAccountConstants.Field.CreditQuota.apiName, 100);
        objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(fsUserId));
        objectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(fsUserId));
        objectData.set(SystemConstants.Field.LockStatus.apiName, "0");
        objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        //		objectData = objectDataService.create(objectData);
        objectData = serviceFacade.saveObjectData(new User(tenantId, fsUserId), objectData);
        System.out.println(objectData);
    }

    @Test
    public void getCustomerAccountByCustomerId() {
        RequestContext requestContext = RequestContext.builder().tenantId("2").user(java.util.Optional.of(new User("2", "1000"))).build();
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);

        CustomerAccountType.GetByCustomerIdArg arg = new CustomerAccountType.GetByCustomerIdArg();
        arg.setCustomerId(customerId);
        Map<String, Object> objectData = customerAccountService.getByCustomerId(serviceContext, arg).getObjectData();
        System.out.println("objectData===" + objectData);
    }

    @Test
    public void queryCustomerAccountTest() throws MetadataServiceException {
        List<IObjectData> objectDataList = objectDataService.findByTenantId(tenantId, CustomerAccountConstants.API_NAME);
        //		IObjectData objectData = objectDataService.invalid(objectDataList.get(0));
        String json = JsonUtil.toJson(objectDataList.get(0));
        System.out.println(objectDataList);
    }

    @Test
    public void getPrepayTransactionDetailDescribeTest() throws MetadataServiceException {
        //		IObjectDescribe objectDescribe = objectDescribeService.findById(tenantId, describeId);
        IObjectDescribe objectDescribe1 = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, PrepayDetailConstants.API_NAME);
        System.out.println(objectDescribe1);
    }

    @Test
    public void queryPrepayTransactionDetailDataTest() throws MetadataServiceException {
        List<IObjectData> objectDatas = objectDataService.findByTenantId(tenantId, PrepayDetailConstants.API_NAME);
        System.out.println(objectDatas);
    }

    @Test
    public void getRebateTransactionDetailDescribeTest() throws MetadataServiceException {
        String describeId = "59ca1e3e422c90256871b79f";
        IObjectDescribe objectDescribex = objectDescribeService.findById(tenantId, describeId);
        IObjectDescribe objectDescribeDraftx = objectDescribeService.findById(tenantId, describeId);

        String desId = "59ca1e46422c90256871b7a0";
        IObjectDescribe objectDescribez = objectDescribeService.findById(tenantId, desId);
        IObjectDescribe objectDescribeDraftz = objectDescribeService.findById(tenantId, desId);

        //59ca1e3e422c90256871b79f
        IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, RebateIncomeDetailConstants.API_NAME);
        //59ca1e46422c90256871b7a0
        IObjectDescribe objectDescribe1 = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, RebateIncomeDetailConstants.API_NAME);
        String describeJson = objectDescribex.toJsonString();
        System.out.println(describeJson);
    }

    @Test
    public void queryCustomerAccountLayouTest() throws MetadataServiceException {
        List<ILayout> layouts = layoutService.findByObjectDescribeApiNameAndTenantId(CustomerAccountConstants.API_NAME, tenantId);
        System.out.println(layouts);
    }

    @Test
    public void queryPrepayTransactionDetailLayoutTest() throws MetadataServiceException {
        List<ILayout> layouts = layoutService.findByObjectDescribeApiNameAndTenantId(PrepayDetailConstants.API_NAME, tenantId);
        System.out.println(layouts);
    }

    @Test
    public void queryRebateTransactionDetailLayoutTest() throws MetadataServiceException {
        List<ILayout> layouts = layoutService.findByObjectDescribeApiNameAndTenantId(CustomerAccountConstants.API_NAME, tenantId);
        List<ILayout> prepayLayouts = layoutService.findByObjectDescribeApiNameAndTenantId(PrepayDetailConstants.API_NAME, tenantId);
        System.out.println(layouts);
    }

    @Test
    public void predefineObjDescribeTest() throws MetadataServiceException {
        IObjectDescribe describe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, SystemConstants.AccountApiName);
        System.out.println(describe);
    }

    //回款财务
    private String paymentFinacailRole = "00000000000000000000000000000002";
    //销售人员
    private String salesRole = "00000000000000000000000000000015";

}
