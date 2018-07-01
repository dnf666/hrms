package com.facishare.crm.sfa.predefine.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.facishare.crm.customeraccount.predefine.action.PrepayDetailFlowCompletedAction;
import com.facishare.crm.customeraccount.predefine.service.dto.CreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by xujf on 2017/9/25.
 */
public class TestJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String args[]) {
        BigDecimal bigDecimal = new BigDecimal("");
    }

    @Test
    public void testGson() {
        Gson gson = new GsonBuilder().create();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("a1", "a1_value");
        map1.put("a2", "a2_value");

        List<Object> list1 = new ArrayList<Object>();
        list1.add("123");
        list1.add("456");
        list1.add(true);
        list1.add(89);

        map1.put("a3", list1);

        System.out.println("gson-map1=" + gson.toJson(map1));
    }

    /**
     * 2017-11-06测试OK<br>
     */
    @Test
    public void testJsonDeserialized() {
        String customerStr = "{\"objectData\" :{\"customer_id\": \"eebe39d4fca743ed80802825279353f8\", \"amount\": 12.0, \"transaction_time\": \"1509587574414\", \"income_type\": \"2\", \"payment_id\": \"7d15e877abcd47b8ac5d715173d60aa0\"}}";

        try {
            CreateModel.Arg arg = objectMapper.readValue(customerStr, CreateModel.Arg.class);
            System.out.println(arg);
        } catch (IOException e) {
            throw new RuntimeException("decode error!", e);
        }

    }

    @Test
    public void testJsonUtil() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"customerId\":\"51324dbe7c464590a10d02dd4b72c156\"}";
        try {
            CustomerAccountType.GetByCustomerIdArg arg = objectMapper.readValue(json, CustomerAccountType.GetByCustomerIdArg.class);
            System.out.println(arg);
        } catch (IOException e) {
            throw new RuntimeException("decode error!", e);
        }

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("a1", "a1_value");
        map1.put("a2", "a2_value");

        List<Object> list1 = new ArrayList<Object>();
        list1.add("123");
        list1.add("456");
        list1.add(true);
        list1.add(89);

        map1.put("a3", list1);

        System.out.println("jsonUtil====" + JsonUtil.toJson(map1));
    }

    @Test
    public void testDeserializedCanInvalidCustomerArg() {
        String json = "{\n" + "\t\"customerId\":[\"eebe39d4fca743ed80802825279353f8\"]\n" + "}";
        try {
            String argJson = "{\"triggerType\":\"Create\"}";
            PrepayDetailFlowCompletedAction.Arg prepayDetailFlowCompletedActionArg = new PrepayDetailFlowCompletedAction.Arg();
            PrepayDetailFlowCompletedAction.Arg o = objectMapper.readValue(argJson, PrepayDetailFlowCompletedAction.Arg.class);
            System.out.println(o);
        } catch (IOException e) {
            throw new RuntimeException("decode error!", e);
        }

    }

    @Test
    public void testIsActive() {
        Date date = new Date();
        Calendar start = Calendar.getInstance();
        start.set(2012, 11, 30);
        Calendar end = Calendar.getInstance();
        end.set(2017, 11, 30);
        Date startTime = start.getTime();
        Date endTime = end.getTime();
        boolean active = ObjectDataUtil.isCurrentTimeActive(startTime, endTime);
        System.out.println(active);
    }

}
