package com.facishare.crm.outbounddeliverynote.manager;

import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
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
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    private String tenantId = "55985";
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
    public void delNoteFuncCodeTest() {
        DelFuncModel.Arg arg = new DelFuncModel.Arg();
        arg.setAuthContext(AuthContext.builder().tenantId(tenantId).userId(fsUserId).appId("CRM").build());

        List<String> noteFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return OutboundDeliveryNoteConstants.API_NAME;
            } else {
                return OutboundDeliveryNoteConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(noteFuncset);
        DelFuncModel.Result result = functionProxy.batchDelFunc(arg);

        List<String> productFuncset = Arrays.stream(ObjectAction.values()).map(objectAction -> {
            if (objectAction.getActionCode().equals(ObjectAction.VIEW_LIST.getActionCode())) {
                return OutboundDeliveryNoteProductConstants.API_NAME;
            } else {
                return OutboundDeliveryNoteProductConstants.API_NAME + "||" + objectAction.getActionCode();
            }
        }).collect(Collectors.toList());
        arg.setFuncSet(productFuncset);
        result = functionProxy.batchDelFunc(arg);


        System.out.println(result);
    }
}