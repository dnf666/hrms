package com.facishare.crm.payment.mq;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.payment.PaymentObject;
import com.facishare.crm.payment.mq.dto.ApprovalTaskChangeEvent;
import com.facishare.crm.payment.mq.dto.ApprovalTaskChangeEvent.EventData;
import com.facishare.paas.appframework.common.mq.RocketMQMessageListener;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentApprovalEventListener implements RocketMQMessageListener {
    @Autowired
    private ServiceFacade serviceFacade;

    @Override
    public void consumeMessage(List<MessageExt> messages) {
        for (MessageExt message : messages) {
            log.debug("Approval task message consume: {}", message);
            try {
                ApprovalTaskChangeEvent event = JSON
                        .parseObject(message.getBody(), ApprovalTaskChangeEvent.class);
                if (Utils.CUSTOMER_PAYMENT_API_NAME.equals(event.getTag()) && "task_change".equals(event.getEventType())) {
                    log.debug("PaymentApprovalEventListener consumeMessage event {}", JSON.toJSONString(event));
                    EventData data = event.getEventData();
                    String dataId = event.getEventData().getDataId();
                    String describeApiName = PaymentObject.CUSTOMER_PAYMENT.getApiName();
                    User admin = new User(data.getTenantId(), User.SUPPER_ADMIN_USER_ID);
                    IObjectData objectData = serviceFacade.findObjectData(admin, dataId, describeApiName);
                    if (objectData == null) {
                        log.debug("PaymentApprovalEventListener consumeMessage objectData not exist,describeApiName {},dataId {}", describeApiName, dataId);
                        continue;
                    }
                    objectData.set("approve_employee_id", data.getCandidateIds());
                    serviceFacade.batchUpdateWithMap(admin, Lists.newArrayList(objectData), new HashMap<>(ImmutableMap.of("approve_employee_id", data.getCandidateIds())));
                    log.debug("PaymentApprovalEventListener consumeMessage objectData: {}", objectData);
                }
            } catch (Exception e) {
                log.error("PaymentApprovalEventListener consumeMessage error,messageExt {}", message, e);
            }
        }
    }
}
