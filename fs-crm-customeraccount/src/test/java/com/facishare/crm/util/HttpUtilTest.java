package com.facishare.crm.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.predefine.manager.result.FuncResult;
import com.facishare.crm.customeraccount.predefine.service.dto.SfaCreateModel;
import com.facishare.crm.customeraccount.util.HttpUtil;
import com.facishare.crm.userdefobj.CrmActionEnum;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.Tuple;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class HttpUtilTest {

    private static String crmManagerRole = "00000000000000000000000000000006";

    static {
        System.setProperty("spring.profiles.active", "fstest");
    }

    @Test
    public void jsonTest() {
        class Test {
            String key1;
            String key2;
        }
        String json = "{\"code\":\"0\",\"msg\":\"ok\",\"result\":{\"key1\":\"11\",\"key2\":\"22\"}}";
        FuncResult<Test> result = JsonUtil.fromJson(json, FuncResult.class);
        System.out.println(result);
    }

    @Test
    public void testFormat() {
        String errorMsgformat = "customerAccount id:%s name:%s cannot invalid,because %s";
        String result = String.format(errorMsgformat, 123, "abc", "不想invalid");
        System.out.println("result===" + result);
    }

    @Test
    public void getFunctionPrivilegeTest() throws IOException {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-fs-ei", "55732");
        headers.put("x-fs-userId", "1000");
        List<String> actionCodes = Arrays.stream(ObjectAction.values()).map(ObjectAction::getActionCode).collect(Collectors.toList());
        String url = "http://10.113.32.46:8003/metadata/crmrest/objectPrivilege/getObjectsFunctionPrivilege";//10.112.32.68:8004  10.113.32.46:8003
        Map<String, Object> body = Maps.newHashMap();
        body.put(RebateOutcomeDetailConstants.API_NAME, actionCodes);
        Map<String, Object> resultMap = HttpUtil.post(url, headers, body, Map.class);
        System.out.println(resultMap);
    }

    @Test
    public void addFunByFuncInfo() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        String url = "http://10.113.32.46:8003/metadata/crmrest/updatefuncrest/addfuncbyfuncinfo";
        Map<String, Object> body = Maps.newHashMap();
        body.put("tenantIds", Lists.newArrayList("55732"));
        Map<String, Object> funcCode2DescMap = Maps.newHashMap();
        /*for (CrmActionEnum functionEnum : CrmActionEnum.values()) {
        	if (!functionEnum.getActionCode().equals("List")) {
        		funcCode2DescMap.put(PrepayDetailConstants.API_NAME + "||" + functionEnum.getActionCode(), functionEnum.getActionLabel());
        	} else {
        		funcCode2DescMap.put(PrepayDetailConstants.API_NAME, functionEnum.getActionLabel());
        	}
        }*/
        funcCode2DescMap.put(RebateIncomeDetailConstants.API_NAME + "||" + "Unlock", "解锁");
        funcCode2DescMap.put(RebateIncomeDetailConstants.API_NAME + "||" + "Lock", "锁定");
        body.put("funcCode2DescMap", funcCode2DescMap);
        try {
            Boolean result = HttpUtil.post(url, headers, body, Boolean.class);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initCrmMangerAllFunc() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        String initurl = "http://10.113.32.46:8003/metadata/crmrest/v1/userDefinedObjectInit";
        Map<String, Object> body1 = Maps.newHashMap();
        body1.put("tenantId", "55732");
        body1.put("userId", 1000);
        body1.put("apiName", RebateIncomeDetailConstants.API_NAME);
        try {
            FuncResult<Boolean> result = HttpUtil.post(initurl, headers, body1, FuncResult.class);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addFunc2Role() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        String toRoleUlr = "http://10.113.32.46:8003/metadata/crmrest/commonprivilege/addfunc2role";
        Map<String, Object> body2 = Maps.newHashMap();
        body2.put("tenantId", "55732");
        body2.put("roleCode", crmManagerRole);
        List<String> funcCodes = Lists.newArrayList();
        for (CrmActionEnum functionEnum : CrmActionEnum.values()) {
        }
        funcCodes.add(RebateIncomeDetailConstants.API_NAME + "||" + "Abolish");
        funcCodes.add(RebateIncomeDetailConstants.API_NAME + "||" + "Unlock");
        funcCodes.add(RebateIncomeDetailConstants.API_NAME + "||" + "Lock");
        funcCodes.add(RebateIncomeDetailConstants.API_NAME + "||" + "ChangeOwner");
        body2.put("funcCodes", funcCodes);
        try {
            FuncResult<String> roleResult = HttpUtil.post(toRoleUlr, headers, body2, FuncResult.class);
            System.out.println(roleResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void flapMapTest() {
        List<Tuple<String, List<SfaCreateModel.Arg>>> relatedObjectList = Lists.newArrayList(Tuple.of("test", null));
        List<IObjectData> iObjectDataList = (List) relatedObjectList.stream().map((x) -> {
            return (List) x.getValue();
        }).flatMap((y) -> {
            return y.stream();
        }).collect(Collectors.toList());
        System.out.println(relatedObjectList);
    }
}
