package com.facishare.crm.promotion.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.promotion.base.BaseActionTest;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class PromotionActionTest extends BaseActionTest {
    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    public PromotionActionTest() {
        super(PromotionConstants.API_NAME);
    }

    @Test
    public void addPromotionTest() {
        //        String customerId = "15b5734b742244f3a55e0f3245105fde";
        IObjectData objectData = new ObjectData();
        objectData.setRecordType(PromotionRecordTypeEnum.OrderPromotion.apiName);
        objectData.set(PromotionConstants.Field.Name.apiName, "大甩卖");
        objectData.set(PromotionConstants.Field.StartTime.apiName, new Date());
        objectData.set(PromotionConstants.Field.EndTime.apiName, new Date());
        objectData.set(PromotionConstants.Field.Status.apiName, Boolean.TRUE);
        objectData.set(PromotionConstants.Field.Type.apiName, 11);
        objectData.set(PromotionConstants.Field.CustomerRange.apiName, PromotionConstants.customerRangeDefaultValue);

        objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(fsUserId));
        List<IObjectData> details = Lists.newArrayList();
        IObjectData detailObjectData = new ObjectData();
        detailObjectData.set(PromotionRuleConstants.Field.OrderMoney.apiName, BigDecimal.valueOf(100));
        detailObjectData.set(PromotionRuleConstants.Field.OrderDiscount.apiName, 80);
        details.add(detailObjectData);
        Map<String, List<ObjectDataDocument>> detailsMap = Maps.newHashMap();
        detailsMap.put(PromotionRuleConstants.API_NAME, ObjectDataDocument.ofList(details));
        Object result = executeAdd(objectData, detailsMap);
        System.out.println("addCustomerAccount->result=" + result);
    }

    @Test
    public void editTest() {
        String masterData = "{\"record_type\":\"default__c\",\"object_describe_api_name\":\"PromotionObj\",\"object_describe_id\":\"5a54741e8fbadd1948efb407\",\"name\":\"wp\",\"start_time\":1516118400000,\"end_time\":1517328000000,\"status\":false,\"images\":[],\"type\":\"11\",\"customer_range\":\"{\\\"type\\\":\\\"hasCondition\\\",\\\"value\\\":{\\\"conditions\\\":[{\\\"type\\\":\\\"and\\\",\\\"conditions\\\":[{\\\"left\\\":{\\\"expression\\\":\\\"account_type\\\"},\\\"right\\\":{\\\"type\\\":{\\\"name\\\":\\\"select_one\\\"},\\\"value\\\":\\\"2\\\"},\\\"type\\\":\\\"2\\\"}]}],\\\"type\\\":\\\"or\\\"}}\",\"version\":\"\",\"_id\":\"5a5f4636830bdbeb62800ac1\"}";
        String promotionRuleData = "{\"order_money\":\"1.00\",\"order_discount\":\"10\",\"object_describe_api_name\":\"PromotionRuleObj\",\"object_describe_id\":\"5a54750a8fbadd1144181349\",\"record_type\":\"default__c\",\"version\":\"2\",\"_id\":\"5a5f4636830bdbeb62800ac4\"}";

        IObjectData masterObjectData = new ObjectData(Document.parse(masterData));
        IObjectData detailObjectData = new ObjectData(Document.parse(promotionRuleData));

        Map<String, List<ObjectDataDocument>> details = Maps.newHashMap();
        details.put(PromotionRuleConstants.API_NAME, Lists.newArrayList(ObjectDataDocument.of(detailObjectData)));
        Object result = excuteEdit(masterObjectData, details);
        System.out.println(result);

    }

    @Test
    public void deleteTest() {
        StandardBulkDeleteAction.Arg arg = new StandardBulkDeleteAction.Arg();
        arg.setDescribeApiName(PromotionConstants.API_NAME);
        arg.setIdList(Lists.newArrayList("5a66a7a9a5083d1f41720582"));
        Object result = execute("BulkDelete", arg);
    }
}
