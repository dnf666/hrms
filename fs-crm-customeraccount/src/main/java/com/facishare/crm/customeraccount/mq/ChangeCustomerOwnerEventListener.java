package com.facishare.crm.customeraccount.mq;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.mq.event.ChangeCustomerOwnerEvent;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.util.SearchUtil;
import com.facishare.enterprise.common.util.JsonUtil;
import com.facishare.paas.appframework.common.mq.RocketMQMessageListener;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangeCustomerOwnerEventListener implements RocketMQMessageListener {
    @Autowired
    private ServiceFacade serviceFacade;

    @Override
    public void consumeMessage(List<MessageExt> messages) {
        for (MessageExt message : messages) {
            try {
                String json = new String(message.getBody(), Charset.forName("UTF-8"));
                log.debug("Consume topic{TOPIC_CRM_OPENAPI} message body:{},message:{}", json, message);
                JsonElement jsonElement = JsonUtil.parseToJsonObject(json);
                JsonElement actionCodeJsonElement = jsonElement.getAsJsonObject().get("ActionCode");
                JsonElement objectApiNameJsonElement = jsonElement.getAsJsonObject().get("ObjectApiName");
                if (Objects.isNull(actionCodeJsonElement) || Objects.isNull(objectApiNameJsonElement) || !actionCodeJsonElement.isJsonPrimitive() || !objectApiNameJsonElement.isJsonPrimitive()) {
                    continue;
                }
                String actionCode = actionCodeJsonElement.getAsString();
                String objectApiName = objectApiNameJsonElement.getAsString();
                if ("ChangeOwner".equals(actionCode) && Utils.ACCOUNT_API_NAME.equals(objectApiName)) {
                    ChangeCustomerOwnerEvent event = JsonUtil.fromJson(json, ChangeCustomerOwnerEvent.class);
                    String tenantId = event.getTennatId();
                    ChangeCustomerOwnerEvent.ActionContent actionContent = event.getActionContent();
                    String customerId = actionContent.getCustomerId();
                    Integer newCustomerOwnerId = actionContent.getOwnerId();
                    User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
                    Map<String, IObjectDescribe> objectDescribeMap = serviceFacade.findObjects(tenantId, Lists.newArrayList(CustomerAccountConstants.API_NAME));
                    if (!objectDescribeMap.containsKey(CustomerAccountConstants.API_NAME)) {
                        continue;
                    }
                    IObjectDescribe objectDescribe = objectDescribeMap.get(CustomerAccountConstants.API_NAME);
                    SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
                    searchTemplateQuery.setOffset(0);
                    searchTemplateQuery.setLimit(10);
                    List<IFilter> filters = Lists.newArrayList();
                    SearchUtil.fillFilterEq(filters, CustomerAccountConstants.Field.Customer.apiName, customerId);
                    searchTemplateQuery.setFilters(filters);
                    QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQueryWithDeleted(user, objectDescribe, searchTemplateQuery);
                    if (CollectionUtils.isEmpty(queryResult.getData())) {
                        log.warn("queryCustomerAccountResult:{}", queryResult);
                    } else {
                        IObjectData customerAccountObjectData = queryResult.getData().get(0);
                        customerAccountObjectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(String.valueOf(newCustomerOwnerId)));
                        serviceFacade.updateObjectData(user, customerAccountObjectData, true);
                    }
                }
            } catch (Exception ex) {
                log.warn("cusume change customer owner event error", ex);
            }
        }
    }
}
