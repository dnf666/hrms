package com.facishare.crm.requisitionnote.manager;


import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.rest.FunctionProxy;
import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.dto.AuthContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 21/03/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class PrivilegeManagerTest {

    @Autowired
    private FunctionProxy functionProxy;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    private String tenantId = "69664";
    private String fsUserId = "1000";

    @Before
    public void initUser() {
        String invirentment = System.getProperty("spring.profiles.active");
        if (invirentment.equals("ceshi113")) {
            tenantId = "55985";
        } else if (invirentment.equals("fstest")) {
            tenantId = "69664";
        }
    }

    /**
     * 功能删除
     */
    @Test
    public void delRequisitionNoteFunccodeTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return RequisitionNoteConstants.API_NAME;
            } else {
                return RequisitionNoteConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println("--------------" + result);
    }

    /**
     * 功能删除
     */
    @Test
    public void delRequisitionNoteProductFunccodeTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());
        List<String> funcset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return RequisitionNoteProductConstants.API_NAME;
            } else {
                return RequisitionNoteProductConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(funcset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);
        System.out.println("--------------" + result);
    }
}
