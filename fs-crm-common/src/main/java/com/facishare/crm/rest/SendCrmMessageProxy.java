package com.facishare.crm.rest;

import com.facishare.crm.rest.dto.SendCrmMessageModel;
import com.facishare.rest.proxy.annotation.*;

import java.util.Map;

/**
 * Created by linqy on 2018/01/24
 */
@RestResource(
        value = "SendCrmMessage",
        desc = "发送CRM消息",
        contentType = "application/json"
)
public interface SendCrmMessageProxy {

    @POST(value = "/AddRemindRecord", desc = "发送CRM消息")
//    SendCrmMessageModel.Result sendCrmMessages(@HeaderParam("x-fs-ei") String tenantId, @Body SendCrmMessageModel.Arg arg);
    SendCrmMessageModel.Result sendCrmMessages(@HeaderMap Map<String, String> headers, @Body SendCrmMessageModel.Arg arg);
}