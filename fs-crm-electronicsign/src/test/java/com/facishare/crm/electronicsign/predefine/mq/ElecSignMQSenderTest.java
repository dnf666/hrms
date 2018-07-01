package com.facishare.crm.electronicsign.predefine.mq;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.electronicsign.predefine.model.vo.SimpleSignerVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-test/applicationContext.xml"})
public class ElecSignMQSenderTest {
    @Resource
    private ElecSignMQSender elecSignMQSender;

    static {
        System.setProperty("process.profile", "ceshi113");
    }

    @Test
    public void sendSignCompleteMsg_Success() {
        SignCompleteMessageData messageData = new SignCompleteMessageData();
        messageData.setObjApiName(DeliveryNoteObjConstants.API_NAME);
        messageData.setObjDataId("asdf1234");
        SimpleSignerVO signerVO = new SimpleSignerVO();
        signerVO.setAccountId("accountAAA");
        messageData.setSigner(signerVO);
        elecSignMQSender.sendSignCompleteMsg(messageData);
    }
}
