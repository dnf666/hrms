package com.facishare.crm.erpstock.manager;

import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.crm.erpstock.constants.ErpWarehouseConstants;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class PrivilegeManagerTest {
    @Autowired
    private FunctionProxy functionProxy;

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    private String tenantId = "69664";
    private String fsUserId = "1000";

    @Before
    public void initUser() {
        String invirentment = System.getProperty("spring.profiles.active");
        if (invirentment.equals("ceshi113")) {
            tenantId = "55424";
        } else if (invirentment.equals("fstest")) {
            tenantId = "69664";
        }
    }

    /**
     * 功能删除
     */
    @Test
    public void delCustomerAccountFunccodeTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());

        List<String> wFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return ErpWarehouseConstants.API_NAME;
            } else {
                return ErpWarehouseConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(wFuncset);
        DelFuncModel.Result result = new DelFuncModel.Result();

        List<String> sFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return ErpStockConstants.API_NAME;
            } else {
                return ErpStockConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(sFuncset);
        result = functionProxy.batchDelFunc(arg);

        System.out.println(result);
    }
}