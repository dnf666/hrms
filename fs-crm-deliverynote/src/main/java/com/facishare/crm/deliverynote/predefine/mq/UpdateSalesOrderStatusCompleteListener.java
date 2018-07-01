package com.facishare.crm.deliverynote.predefine.mq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.crm.deliverynote.predefine.manager.InitObjManager;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

/**
 * 如果有已发货状态的订单，就调接口POST /customerorder/createupdatecustomerorderdeliverytoreceivedtask，把所有已发货状态的订单改成已收货状态
 * 然后这些接口执行完就发MQ，我们监听MQ消息，就触发开启发货单
 *
 * CRM Action推送MQ方案
 * http://wiki.firstshare.cn/pages/viewpage.action?pageId=37088911
 *
 * ActionCode：UpdateCustomerOrderDeliveryToReceivedTaskFinished
 *
 * Created by chenzs on 2018/4/10.
 */
@Slf4j
@Service(value="updateSalesOrderStatusCompleteListener")
public class UpdateSalesOrderStatusCompleteListener implements MessageListenerConcurrently {
    private static final String CHANGE_STATUS_COMPLETE_TOPIC = "TOPIC_CRM_OPENAPI";
    private static final String ACTION_CODE = "UpdateCustomerOrderDeliveryToReceivedTaskFinished";

    @Autowired
    private InitObjManager initObjManager;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if (msgs.size() > 1) {
            log.error("There are more than one message in the RocketMQ, size[{}]", msgs.size());
        }

        try {
            processMessage(msgs.get(0));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception ex) {
            log.error("UpdateSalesOrderStatusCompleteListener Process Message Error, message={}", msgs.get(0), ex);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    /**
     * msg.getBody()的一个例子
     * {
         "TenantID":55984,
         "TenantAccount":"55984",
         "AppID":"CRM",
         "Package":"CRM",
         "ObjectApiName":"SalesOrderObj",
         "ObjectID":"",
         "ActionCode":"UpdateCustomerOrderDeliveryToReceivedTaskFinished",
         "ActionContent":{
             "EmployeeID":1000,
             "Success":true
         },
         "OperatorID":0,
         "ActionTime":1524138107965,
         "Source":"CRM"
     }
     */
    private void processMessage(MessageExt msg) {
        log.info("UpdateSalesOrderStatusCompleteListener receive message={}", msg);

        String topic = msg.getTopic();
        switch (topic) {
            case CHANGE_STATUS_COMPLETE_TOPIC:
                final String jsonBody = new String(msg.getBody(), Charset.forName("utf-8"));
                Document doc = Document.parse(jsonBody);
                log.info("UpdateSalesOrderStatusCompleteListener msg body={}", doc);
                String actionCode = (String)doc.get("ActionCode");

                if (Objects.equals(actionCode, ACTION_CODE)) {
                    Integer tenantId = (Integer) doc.get("TenantID");
                    Document actionContentDoc = (Document)doc.get("ActionContent");
                    Integer employeeID = (Integer) actionContentDoc.get("EmployeeID");
                    Boolean success = (Boolean) actionContentDoc.get("Success");
                    User user = new User(String.valueOf(tenantId), String.valueOf(employeeID));
                    log.info("UpdateSalesOrderStatusCompleteListener enableDeliveryNote, tenantId={}, user={}", tenantId, user);
                    if (success) {
                        initObjManager.enableDeliveryNoteNotReturnIfOpening(String.valueOf(tenantId), user, true, user.getUserId());
                    } else {
                        initObjManager.updateSalesOrderStatusFail(user);
                    }
                }
            default:
                break;
        }
        log.info("UpdateSalesOrderStatusCompleteListener consumed message success message={}", msg);
    }
}