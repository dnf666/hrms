package com.facishare.crm.sfa.predefine.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.service.PriceBookStandardService;
import com.github.autoconf.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luxin on 2017/11/17.
 */

@Service
@Slf4j
public class ProductEventConsumer {
    private AutoConfRocketMQProcessor processor;
    @Autowired
    private PriceBookStandardService priceBookStandardService;
    private boolean productEventStart = Boolean.FALSE;
    private boolean grayRelease = Boolean.FALSE;
    private List<String> tenantIds;

    {
        ConfigFactory.getConfig("fs-crm-product-event-mq", iConfig -> {
            productEventStart = iConfig.getBool("productEventStart");
            grayRelease = iConfig.getBool("grayRelease");
            String tenantIdsStr = iConfig.get("grayReleaseTenantIds");
            if (tenantIdsStr != null) {
                tenantIds = Arrays.asList(tenantIdsStr.split(","));
            }

        });
    }

    @PostConstruct
    public void init() {
        if (!productEventStart) {
            return;
        }

        MessageListenerConcurrently listener = (messages, context) -> {
            for (MessageExt messageExt : messages) {
                try {
                    log.info("ProductEventConsumer messageExt -{}", messageExt);
                    if (!processMessage(messageExt)) {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                } catch (Throwable e) {
                    log.error("ProductEventConsumer processMessage error messages: {}", messages, e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
        try {
            processor = new AutoConfRocketMQProcessor("fs-crm-product-event-mq", "NAMESERVER", "GROUP_CONSUMER", "TOPICS", listener);
            processor.init();
        } catch (Exception e) {
            log.error("cannot init ProductEventConsumer", e);
        }
        log.info("ProductEventConsumer init over");
    }


    private boolean processMessage(MessageExt messages) {
        byte[] msgBody = messages.getBody();

        if (msgBody != null) {
            ProductEvent productEvent = JSON.parseObject(new String(msgBody), ProductEvent.class);

            String apiName = productEvent.getApiName();
            if (!Utils.PRODUCT_API_NAME.equals(apiName)) {
                return Boolean.TRUE;
            }

            //如果是灰度,企业在灰度名单里没有改企业,就跳过
            if (grayRelease && productEvent.getTenantId() != null && !tenantIds.contains(productEvent.getTenantId().toString())) {
                return Boolean.TRUE;
            }

            if (isValidProductEvent(productEvent)) {
                log.info("before doUpdateStandardPriceBookProduct. messages{},productEvent{}", messages, JSON.toJSONString(productEvent));

                String actionCode = productEvent.getActionCode();
                if (ProductEvent.ADD_ACTION.equals(actionCode) || ProductEvent.DELETE_ACTION.equals(actionCode)) {
                    @SuppressWarnings("unchecked")
                    List<String> productIds = JSONObject.parseObject(productEvent.getObjectIdsStr(), List.class);
                    for (String productId : productIds) {
                        try {
                            if (!priceBookStandardService.doUpdateStandardPriceBookProduct(productEvent.getTenantId().toString(), productId, actionCode)) {
                                log.warn("doUpdateStandardPriceBookProduct fail. tenantId {},productId {},actionCode {}", productEvent.getTenantId(), productId, actionCode);
                                return Boolean.FALSE;
                            }
                        } catch (Throwable e) {
                            log.error("doUpdateStandardPriceBookProduct throw exception. tenantId {},productId {},actionCode {}", productEvent.getTenantId(), productId, actionCode, e);
                            return Boolean.FALSE;
                        }
                    }
                    log.info("doUpdateStandardPriceBookProduct success. tenantId {},productIds {},actionCode {}", productEvent.getTenantId(), productIds, actionCode);
                    return Boolean.TRUE;
                } else {
                    return Boolean.TRUE;
                }
            } else {
                //如果参数不正确,则不再消费,将这个数据丢掉
                log.warn("param wrong,productEvent is{}", productEvent.toString());
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


    /**
     * 校验参数的有效性
     *
     * @param productEvent
     * @return
     */
    private boolean isValidProductEvent(ProductEvent productEvent) {
        if (productEvent != null &&
                StringUtils.isNotBlank(productEvent.getActionCode()) &&
                productEvent.getTenantId() != null &&
                StringUtils.isNotBlank(productEvent.getTenantId().toString()) &&
                StringUtils.isNotBlank(productEvent.getObjectIdsStr()) &&
                productEvent.getObjectIdsStr().startsWith("[")) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }


    @PreDestroy
    public void shutDown() {
        processor.shutDown();
    }


}
