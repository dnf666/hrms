package com.facishare.crm.sfa.utilities.proxy;

import java.util.Map;

import com.facishare.paas.appframework.metadata.restdriver.dto.CreateObjectData;
import com.facishare.rest.proxy.annotation.Body;
import com.facishare.rest.proxy.annotation.HeaderMap;
import com.facishare.rest.proxy.annotation.POST;
import com.facishare.rest.proxy.annotation.RestResource;

/**
 * Created by lilei on 2017/7/31.
 */
@RestResource(value = "CRM_SFA", desc = "CRM Rest API Call", contentType = "application/json")
public interface TestProxy {

    @POST(value = "/crm/test", desc = "创建预置对象记录")
    CreateObjectData.Result test(@Body Map<String, Object> objectData, @HeaderMap Map<String, String> headers);

}
