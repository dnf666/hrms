package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.predefine.base.BaseServiceTest;
import com.facishare.crm.electronicsign.predefine.service.dto.SignRequestType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by chenzs on 2018/5/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
@Slf4j
public class SignRequestServiceTest extends BaseServiceTest{
    @Resource
    private SignRequestService signRequestService;

    public SignRequestServiceTest() {
        super(InternalSignCertifyObjConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Override
    public void initUser() {
        this.tenantId = "55988";
        this.fsUserId = "1000";
    }

    /**
     * {
     "appType":"2",
     "objApiName":"object_22473__c",
     "objDataId":"5b07b325bab09c434e4be9c4",
     "isReCreateContractIfExpired":true,
     "urlExpireTime":0,
     "signer":{
     "signerType":"2",
     "upSignerId":1001,
     "signType":"2",
     "orderNum":1
     },
     "createContractUpTriggerUserId":1001,
     "allSigndReturnUrl":"",
     "contractFileAttachmentName":"对账单.pdf",
     "title":"对账单",
     "contractExpireTime":1527579148
     }
     */
    @Test
    public void getSignUrlOrAutoSign() {
        SignRequestType.GetSignUrl.SignerArg signer  = new SignRequestType.GetSignUrl.SignerArg();
        signer.setSignerType(2);
        signer.setUpDepartmentId("1000");
        signer.setOrderNum(1);

        SignRequestType.GetSignUrl.Arg arg = new SignRequestType.GetSignUrl.Arg();
        arg.setAppType("2");
        arg.setObjApiName("object_22473__c");
        arg.setObjDataId("5b07b325bab09c434e4be9c4");
        arg.setIsReCreateContractIfExpired(true);
        arg.setUrlExpireTime(null);
        arg.setSigner(signer);
        arg.setUpCreateContractUserId(1001);
        arg.setSignedReturnUrl("http://www.baidu.com");
        arg.setContractFileAttachmentName("对账单.pdf");
        arg.setTitle("对账单");
        arg.setContractExpireTime(1527579148L);

        SignRequestType.GetSignUrl.Result result = signRequestService.getSignUrlOrAutoSign(newServiceContext(), arg);
        System.out.println(result);
    }

    /**
     * {
     "appType":"2",
     "objApiName":"object_22473__c",
     "objDataId":"5b07b325bab09c434e4be9c4",
     "isReCreateContractIfExpired":true,
     "urlExpireTime":0,
     "signer":{
     "signerType":"1",
     "accountId":"fa7b5a8728f14f59aba1f5cc18899b23",
     "signType":"1",
     "orderNum":2
     },
     "createContractUpTriggerUserId":1001,
     "allSigndReturnUrl":"",
     "contractFileAttachmentName":"对账单",
     "title":"对账单",
     "contractExpireTime":1527233548
     }
     */
    @Test
    public void getSignUrlOrAutoSign2() {
        SignRequestType.GetSignUrl.SignerArg signer  = new SignRequestType.GetSignUrl.SignerArg();
        signer.setSignerType(2);
        signer.setAccountId("fa7b5a8728f14f59aba1f5cc18899b23");
        signer.setOrderNum(2);

        SignRequestType.GetSignUrl.Arg arg = new SignRequestType.GetSignUrl.Arg();
        arg.setAppType("2");
        arg.setObjApiName("object_22473__c");
        arg.setObjDataId("5b07b325bab09c434e4be9c4");
        arg.setIsReCreateContractIfExpired(true);
        arg.setSigner(signer);
        arg.setUpCreateContractUserId(1001);
        arg.setContractFileAttachmentName("对账单.pdf");
        arg.setTitle("对账单");
        arg.setContractExpireTime(1527579148L);

        SignRequestType.GetSignUrl.Result result = signRequestService.getSignUrlOrAutoSign(newServiceContext(), arg);
        System.out.println(result);
    }
}
