package com.facishare.crm.electronicsign.predefine.mq;

import com.facishare.paas.appframework.common.mq.RocketMQMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ElecSignMQSender {
    @Autowired
    private RocketMQMessageSender electronicSignMqMessageSender;

    public void sendSignCompleteMsg(SignCompleteMessageData signCompleteMessageData) {
        electronicSignMqMessageSender.sendMessage(signCompleteMessageData.toMessageData());
        log.info("electronicSignMqMessageSender.sendMessage, signCompleteMessageData[{}], messageData[{}]", signCompleteMessageData, signCompleteMessageData.toMessageData());
    }
}
