package com.facishare.crm.stock.manager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.rest.FunctionProxy;
import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.privilege.dto.AuthContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
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
    public void delCustomerAccountFunccodeTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());

        List<String> wFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return WarehouseConstants.API_NAME;
            } else {
                return WarehouseConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(wFuncset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);

        List<String> gFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return GoodsReceivedNoteConstants.API_NAME;
            } else {
                return GoodsReceivedNoteConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(gFuncset);
        result = functionProxy.batchDelFunc(arg);

        List<String> gpFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return GoodsReceivedNoteProductConstants.API_NAME;
            } else {
                return GoodsReceivedNoteProductConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(gpFuncset);
        result = functionProxy.batchDelFunc(arg);

        List<String> sFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return StockConstants.API_NAME;
            } else {
                return StockConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(sFuncset);
        result = functionProxy.batchDelFunc(arg);

        System.out.println(result);
    }
}