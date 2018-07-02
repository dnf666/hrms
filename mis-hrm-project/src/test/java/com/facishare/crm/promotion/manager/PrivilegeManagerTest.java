package com.facishare.crm.promotion.manager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.rest.FunctionProxy;
import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.RecordTypeLogicService;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.appframework.privilege.dto.AuthContext;

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

    private String tenantId = "2";
    private String fsUserId = "1000";

    @Before
    public void initUser() {
        String invirentment = System.getProperty("spring.profiles.active");
        if (invirentment.equals("ceshi113")) {
            tenantId = "2";
        } else if (invirentment.equals("fstest")) {
            tenantId = "7";
        }
    }

    @Test
    public void delPromotionRuleFunccodeAndInitTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return PromotionRuleConstants.API_NAME;
            } else {
                return PromotionRuleConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println(result);
        User user = new User(tenantId, fsUserId);
        this.functionPrivilegeService.initFunctionPrivilege(user, PromotionRuleConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(user, PromotionRuleConstants.DEFAULT_LAYOUT_API_NAME, user.getTenantId(), PromotionRuleConstants.API_NAME);
        System.out.println();
    }

    @Test
    public void delPromotionProductFunccodeAndInitTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return PromotionProductConstants.API_NAME;
            } else {
                return PromotionProductConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println(result);
        User user = new User(tenantId, fsUserId);
        this.functionPrivilegeService.initFunctionPrivilege(user, PromotionProductConstants.API_NAME);
        this.recordTypeLogicService.recordTypeInit(user, PromotionProductConstants.DEFAULT_LAYOUT_API_NAME, user.getTenantId(), PromotionProductConstants.API_NAME);
        System.out.println();
    }

}
