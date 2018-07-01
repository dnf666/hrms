package com.facishare.crm.rest;

import java.util.Map;

import com.facishare.crm.rest.dto.ApprovalInitModel;
import com.facishare.crm.rest.dto.ApprovalInstanceModel;
import com.facishare.crm.rest.dto.GetCurInstanceStateModel;
import com.facishare.rest.proxy.annotation.Body;
import com.facishare.rest.proxy.annotation.HeaderMap;
import com.facishare.rest.proxy.annotation.POST;
import com.facishare.rest.proxy.annotation.RestResource;

@RestResource(value = "PAAS-FLOW", desc = "审批流服务", contentType = "application/json")
public interface ApprovalInitProxy {
    @POST(value = "fs-crm-workflow/approval/definition/init", desc = "初始化流程")
    ApprovalInitModel.Result init(@Body ApprovalInitModel.Arg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "fs-crm-workflow/approval/approvalInstance", desc = "查询审批流")
    ApprovalInstanceModel.Result approvalInstance(@Body ApprovalInstanceModel.Arg arg, @HeaderMap Map<String, String> headers);

    @POST(value = "fs-crm-workflow/approval/instance/getCurInstanceStateByObjectIds", desc = "查询当前审批流")
    GetCurInstanceStateModel.Result getCurInstanceStateByObjectIds(@Body GetCurInstanceStateModel.Arg arg, @HeaderMap Map<String, String> headers);
}