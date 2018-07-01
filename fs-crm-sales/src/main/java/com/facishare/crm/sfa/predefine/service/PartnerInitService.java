package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.rest.ApprovalInitProxy;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.ApprovalInitModel;
import com.facishare.crm.rest.dto.SyncTenantSwitchModel;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.config.ConfigService;
import com.facishare.paas.appframework.config.ConfigValueType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.RecordTypeLogicServiceImpl;
import com.facishare.paas.appframework.metadata.dto.RecordTypeResult;
import com.facishare.paas.appframework.metadata.dto.auth.RecordTypeRoleViewPojo;
import com.facishare.paas.appframework.metadata.menu.MenuCommonService;
import com.facishare.paas.appframework.privilege.RoleService;
import com.facishare.paas.appframework.privilege.model.role.ChannelManagerRoleProvider;
import com.facishare.paas.appframework.privilege.model.role.Role;
import com.facishare.paas.appframework.privilege.util.PrivilegeConstants;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.ILayout;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@ServiceModule("partner_init")
@Service
@Slf4j
public class PartnerInitService {
    @Autowired
    ServiceFacade serviceFacade;
    @Autowired
    RecordTypeLogicServiceImpl recordTypeLogicService;
    @Autowired
    MenuCommonService menuCommonService;
    @Autowired
    EnterpriseInitService enterpriseInitService;
    @Autowired
    ConfigService configService;
    @Autowired
    CrmRestApi crmRestApi;
    @Autowired
    ApprovalInitProxy approvalInitProxy;
    @Autowired
    RoleService roleService;
    @Autowired
    ChannelManagerRoleProvider channelManagerRoleProvider;

    private String configPartnerIsOpenKey = "config_partner_open";
    private String statusOpen = "open";
    private String statusClose = "close";

    /**
     * 合作伙伴开启
     */
    @ServiceMethod("status_open")
    public Map openStatus(ServiceContext context) {
        String configPartnerIsOpen = configService.findTenantConfig(context.getUser(), configPartnerIsOpenKey);
        Map map = Maps.newHashMap();
        map.put("result", StringUtils.isEmpty(configPartnerIsOpen) ? statusClose : configPartnerIsOpen);
        return map;
    }

    /**
     * 合作伙伴开启
     */
    @ServiceMethod("open")
    public Map openService(ServiceContext context) {
        String msg = this.open(context.getUser());
        Map map = Maps.newHashMap();
        map.put("message", "开启成功");
        map.put("code", 0);
        return map;
    }

    public String open(User user) {
        String tenantId = user.getTenantId();
        String partnerApiName = Utils.PARTNER_API_NAME;
        log.warn("partner_init open all start,tenantId {}", tenantId);
        StringBuilder sb = new StringBuilder();
        //刷describe
        try {
            enterpriseInitService.initDescribeForTenant(tenantId, partnerApiName);
            sb.append(" \n initDescribeForTenant success");
        } catch (MetadataServiceException e) {
            sb.append(" \n initDescribeForTenant error:" + e.getMessage());
            log.error("partner_init open initDescribeForTenant error,tenantId {}", tenantId, e);
        }
        //先刷合作伙伴的功能权限
        String initPrivilegeMsg = enterpriseInitService.initPrivilegeRelate(Lists.newArrayList(partnerApiName), user);
        sb.append(" \n initPrivilegeRelate result:" + initPrivilegeMsg);
        //刷布局
        String initLayoutMsg = enterpriseInitService.initMultiLayoutForOneTenant(Lists.newArrayList(partnerApiName), user.getTenantId());
        sb.append(" \n initMultiLayoutForOneTenant result:" + initLayoutMsg);
        //工商查询映射规则
        try {
            enterpriseInitService.initObjectMappingRule(user, "rule_bizqueryobj2partnerobj__c");
            sb.append(" \n initObjectMappingRule success");
        } catch (Exception e) {
            sb.append(" \n initObjectMappingRule error:" + e.getMessage());
            log.error("partner_init open initObjectMappingRule error,tenantId {}", tenantId, e);
        }
        //4个预制对象新增合作伙伴、外部来源字段
        SyncTenantSwitchModel.Arg openArg = new SyncTenantSwitchModel.Arg();
        openArg.setKey("37");
        openArg.setValue("1");
        SyncTenantSwitchModel.Result openResult = crmRestApi.syncTenantSwitch(openArg, getHeaders(tenantId, user.getUserId()));
        if (openResult.getSuccess()) {
            sb.append(" \n sfaObjectOpen partner success,value:" + openResult.getValue());
        } else {
            sb.append(String.format(" \n sfaObjectOpen partner error,value %s,errorCode %s,errorMsg %s", openResult.getValue(), openResult.getErrorCode(), openResult.getMessage()));
            log.error("partner_init open sfaObjectOpen partner error,tenantId {},value {},errorCode {},errorMsg {}",
                    tenantId, openResult.getValue(), openResult.getErrorCode(), openResult.getMessage());
        }
        //4个老对象刷功能权限，更换合作伙伴、移除合作伙伴、更换外部负责人
        String batchInitPartnerRelateMsg = batchInitPartnerRelateAction(user);
        if (StringUtils.isNotEmpty(batchInitPartnerRelateMsg)) {
            sb.append(" \n batchInitPartnerRelateAction error:" + batchInitPartnerRelateMsg);
            log.error("partner_init open batchInitPartnerRelateAction error,tenantId {},msg {}", tenantId, batchInitPartnerRelateMsg);
        } else {
            sb.append(" \n batchInitPartnerRelateAction success");
        }
        //新增渠道经理角色
        try {
            roleService.addPredefinedRole(tenantId, Role.CHANNEL_MANAGER);
            sb.append(" \n channelManagerRole add role success");
        } catch (Exception e) {
            sb.append(" \n channelManagerRole add role error:" + e.getMessage());
            log.error("partner_init open channelManagerRole add role error,tenantId {},msg {}", tenantId, e.getMessage());
        }
        try {
            serviceFacade.updatePreDefinedFuncAccess(user, Role.CHANNEL_MANAGER.getRoleCode(),
                    Lists.newArrayList(channelManagerRoleProvider.getHavePermissFuncCodes()), null);
            sb.append(" \n channelManagerRole roleFuncAccess success");
        } catch (Exception e) {
            sb.append(" \n channelManagerRole roleFuncAccess error:" + e.getMessage());
            log.error("partner_init open channelManagerRole roleFuncAccess error,tenantId {},msg {}", tenantId, e.getMessage());
        }
        //初始化合作伙伴员工布局
        try {
            String layoutJson = enterpriseInitService.getLayoutJsonFromResourceByApiName(Utils.CONTACT_API_NAME, "partnerdetail");
            ILayout layout = new Layout(Document.parse(layoutJson));
            serviceFacade.createLayout(user, layout);
            sb.append(" \n createContactPartnerLayout success");
        } catch (Exception e) {
            sb.append(" \n createContactPartnerLayout error:" + e.getMessage());
            log.error("partner_init open createContactPartnerLayout error,tenantId {}", tenantId, e);
        }
        //初始化合作伙伴联系人业务类型
        try {
            RecordTypeResult createRecordTypeResult = createContactPartnerRecordType(user);
            if (createRecordTypeResult.isSuccess()) {
                sb.append(" \n createContactPartnerRecordType success");
            } else {
                sb.append(" \n createContactPartnerRecordType error:" + createRecordTypeResult.getFailMessage());
            }
        } catch (Exception e) {
            sb.append(" \n createContactPartnerRecordType error:" + e.getMessage());
            log.error("partner_init open createContactPartnerRecordType error,tenantId {}", tenantId, e);
        }
        //预设审批,代理通二期上线
//        StringBuilder initApprovalResult = new StringBuilder();
//        initApprovalResult.append(" initApproval accountObj:" + initApproval(user, Utils.ACCOUNT_API_NAME));
//        initApprovalResult.append(" initApproval opportunityobj:" + initApproval(user, Utils.OPPORTUNITY_API_NAME));
//        initApprovalResult.append(" initApproval salesOrderObj:" + initApproval(user, Utils.SALES_ORDER_API_NAME));
//        if (StringUtils.isEmpty(initApprovalResult)) {
//            sb.append(" \n initApproval all  success");
//        } else {
//            sb.append(" \n initApproval all  error:" + initApprovalResult.toString());
//            log.error("partner_init open createContactPartnerRecordType error,tenantId {},message {}", tenantId, initApprovalResult.toString());
//        }
        //默认CRM菜单中新增合作伙伴菜单
        menuCommonService.createMenuItem(user, partnerApiName);
        sb.append(" \n createMenuItem success");

        //更改合作伙伴的启用状态标识
        String configPartnerIsOpen = configService.findTenantConfig(user, configPartnerIsOpenKey);
        if (StringUtils.isEmpty(configPartnerIsOpen)) {
            configService.createTenantConfig(user, configPartnerIsOpenKey, statusOpen, ConfigValueType.STRING);
        } else {
            configService.updateTenantConfig(user, configPartnerIsOpenKey, statusOpen, ConfigValueType.STRING);
        }
        sb.append(" \n createOpenConfig success");

        log.warn("partner_init open all success,tenantId {}，msg {}", tenantId, sb.toString());
        return sb.toString();
    }

    /**
     * 初始化更换合作伙伴三个操作
     */
    public String batchInitPartnerRelateAction(User user) {
        Set<String> needInitApiNames = Sets.newHashSet(Utils.ACCOUNT_API_NAME, Utils.LEADS_API_NAME, Utils.OPPORTUNITY_API_NAME, Utils.SALES_ORDER_API_NAME);
        List<String> actionCodeList = Lists.newArrayList(ObjectAction.CHANGE_PARTNER.getActionCode(), ObjectAction.DELETE_PARTNER.getActionCode()
                , ObjectAction.CHANGE_PARTNER_OWNER.getActionCode());
        StringBuilder stringBuilder = new StringBuilder();
        for (String needInitApiName : needInitApiNames) {
            try {
                serviceFacade.batchCreateFunc(user, needInitApiName, actionCodeList);
            } catch (Exception e) {
                stringBuilder.append(String.format("batchInitPartnerRelateAction batchCreateFunc error,apiName %s,failMsg %s", needInitApiName, e.getMessage()));
                log.error("batchInitPartnerRelateAction batchCreateFunc error,apiName {}", needInitApiName, e);
            }
            try {
                serviceFacade.updateUserDefinedFuncAccess(user, PrivilegeConstants.ADMIN_ROLE_CODE, needInitApiName, actionCodeList, Lists.newArrayList());
            } catch (Exception e) {
                stringBuilder.append(String.format("batchInitPartnerRelateAction addRoleFunc error,apiName %s,failMsg %s", needInitApiName, e.getMessage()));
                log.error("batchInitPartnerRelateAction addRoleFunc error,apiName {}", needInitApiName, e);
            }
        }
        return stringBuilder.toString();
    }

    public String initApproval(User user, String apiName) {
        Map<String, String> headerMap = getApprovalInitHeaders(user.getTenantId(), user.getUserId());
        ApprovalInitModel.Arg arg = new ApprovalInitModel.Arg();
        arg.setEntityId(apiName);
        ApprovalInitModel.Result result = approvalInitProxy.init(arg, headerMap);
        if (result.isData()) {
            return null;
        } else {
            return result.getMessage();
        }
    }

    /**
     * 创建合作伙伴联系人业务类型
     */
    public RecordTypeResult createContactPartnerRecordType(User user) {
        String json = "{\"label\":\"合作伙伴员工\",\"api_name\":\"default_contact_partner__c\",\"description\":\"合作伙伴员工\",\"config\":{\"remove\":0},\"is_active\":true,\"roles\":[{\"role_code\":\"00000000000000000000000000000025\",\"is_default\":true,\"is_used\":true,\"layout_api_name\":\"default_crm_ContactObj_partnerdetail_layout_by_UDObjectServer__c\"},{\"role_code\":\"00000000000000000000000000000006\",\"is_default\":false,\"is_used\":true,\"layout_api_name\":\"default_crm_ContactObj_partnerdetail_layout_by_UDObjectServer__c\"}]}";
        RecordTypeRoleViewPojo pojo = JSON.parseObject(json, RecordTypeRoleViewPojo.class);
        RecordTypeResult result = recordTypeLogicService.createRecordType(user.getTenantId(), Utils.CONTACT_API_NAME, pojo, user);
        return result;
    }

    public Map<String, String> getHeaders(String tenantId, String userId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        headers.put("Expect", "100-continue");
        return headers;
    }

    private Map<String, String> getApprovalInitHeaders(String tenantId, String fsUserId) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", fsUserId);
        headers.put("x-tenant-id", tenantId);
        return headers;
    }
}
