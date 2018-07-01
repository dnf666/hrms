package com.facishare.crm.manager;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.util.HttpUtil;
import com.facishare.crm.rest.FunctionProxy;
import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.crm.rest.dto.FuncCodePermissModel;
import com.facishare.crm.rest.dto.FuncPermissionCheckModel;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.RecordTypeLogicService;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.appframework.privilege.dto.AuthContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class PrivilegeManagerTest {
    @Autowired
    private FunctionProxy functionProxy;
    @Autowired
    private RecordTypeLogicService recordTypeLogicService;
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    private String tenantId = "55910";
    private String fsUserId = "1000";

    //    @Before
    //    public void initUser() {
    //        String invirentment = System.getProperty("spring.profiles.active");
    //        if (invirentment.equals("ceshi113")) {
    //            tenantId = "55732";
    //        } else if (invirentment.equals("fstest")) {
    //            tenantId = "7";
    //        }
    //    }

    @Test
    public void getRoleFunctionCodeTest() throws IOException {
        String url = "http://10.113.32.47:8003/fs-paas-auth/roleFuncPermiss";//http://10.112.32.47:8006/
        Map<String, Object> bodyMap = Maps.newHashMap();
        Map<String, Object> contextMap = Maps.newHashMap();
        contextMap.put("tenantId", tenantId);
        contextMap.put("appId", "CRM");
        contextMap.put("userId", fsUserId);
        bodyMap.put("authContext", contextMap);
        bodyMap.put("roleCode", "00000000000000000000000000000015");
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        Map<String, Object> result = HttpUtil.post(url, headers, bodyMap, Map.class);
        System.out.println(result);
    }

    @Test
    public void funcPermissionCheckTest() {
        FuncPermissionCheckModel.Arg arg = new FuncPermissionCheckModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> codes = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return RebateOutcomeDetailConstants.API_NAME;
            } else {
                return RebateOutcomeDetailConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncCodeList(codes);
        FuncPermissionCheckModel.Result result = functionProxy.funcPermissionCheck(arg);
        System.out.println(result);
    }

    @Test
    public void customerAccountFuncCodePermissionTest() {
        FuncCodePermissModel.Arg arg = new FuncCodePermissModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        arg.setFuncCodes(Lists.newArrayList(CustomerAccountConstants.API_NAME, PrepayDetailConstants.API_NAME, RebateIncomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.API_NAME));
        FuncCodePermissModel.Result result = functionProxy.funcCodePermiss(arg);
        System.out.println(result);
    }

    @Test
    public void delCustomerAccountFunccodeAndInitTest() {
        this.tenantId = "55910";
        this.fsUserId = "1000";
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return CustomerAccountConstants.API_NAME;
            } else {
                return CustomerAccountConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println(result);
        User user = new User(tenantId, fsUserId);
        this.functionPrivilegeService.initFunctionPrivilege(user, CustomerAccountConstants.API_NAME);
        //        this.dataPrivilegeService.addCommonPrivilegeListResult(user, Lists.newArrayList(new ObjectDataPermissionInfo[] { new ObjectDataPermissionInfo(RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.DISPLAY_NAME, DefObjConstants.DATA_PRIVILEGE_OBJECTDATA_PERMISSION.PRIVATE.getValue()) }));
        this.recordTypeLogicService.recordTypeInit(user, CustomerAccountConstants.DETAIL_LAYOUT_API_NAME, user.getTenantId(), CustomerAccountConstants.API_NAME);
        System.out.println("success......");
    }

    @Test
    public void delPrepayDetailFuncCodeAndInitTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return PrepayDetailConstants.API_NAME;
            } else {
                return PrepayDetailConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println(result);
        User user = new User(tenantId, fsUserId);
        this.functionPrivilegeService.initFunctionPrivilege(user, PrepayDetailConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(user, PrepayDetailConstants.DEFAULT_LAYOUT_API_NAME, user.getTenantId(), PrepayDetailConstants.API_NAME);
        System.out.println();
    }

    @Test
    public void delRebateIncomeFuncCodeAndInitTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return RebateIncomeDetailConstants.API_NAME;
            } else {
                return RebateIncomeDetailConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println(result);
        User user = new User(tenantId, fsUserId);
        this.functionPrivilegeService.initFunctionPrivilege(user, RebateIncomeDetailConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(user, RebateIncomeDetailConstants.DEFAULT_LAYOUT_API_NAME, user.getTenantId(), RebateIncomeDetailConstants.API_NAME);
        System.out.println();
    }

    @Test
    public void delRebateOutcomeFuncCodeAndInitTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return RebateOutcomeDetailConstants.API_NAME;
            } else {
                return RebateOutcomeDetailConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println(result);
        User user = new User(tenantId, fsUserId);
        this.functionPrivilegeService.initFunctionPrivilege(user, RebateOutcomeDetailConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(user, RebateOutcomeDetailConstants.DEFAULT_LAYOUT_API_NAME, user.getTenantId(), RebateOutcomeDetailConstants.API_NAME);
        System.out.println();
    }

}
