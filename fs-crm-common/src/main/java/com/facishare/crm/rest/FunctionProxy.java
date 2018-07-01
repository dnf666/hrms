package com.facishare.crm.rest;

import com.facishare.crm.rest.dto.DelFuncModel;
import com.facishare.crm.rest.dto.FuncCodePermissModel;
import com.facishare.crm.rest.dto.FuncPermissionCheckModel;
import com.facishare.rest.proxy.annotation.Body;
import com.facishare.rest.proxy.annotation.POST;
import com.facishare.rest.proxy.annotation.RestResource;

@RestResource(value = "PAAS-PRIVILEGE", desc = "功能权限服务", contentType = "application/json")
public interface FunctionProxy {
    @POST(value = "/funcCodePermiss", desc = "查询某个功能的权限设置")
    FuncCodePermissModel.Result funcCodePermiss(@Body FuncCodePermissModel.Arg arg);

    @POST(value = "/batchDelFunc", desc = "功能删除")
    DelFuncModel.Result batchDelFunc(@Body DelFuncModel.Arg arg);

    @POST(value = "/funcPermissionCheck", desc = "")
    FuncPermissionCheckModel.Result funcPermissionCheck(@Body FuncPermissionCheckModel.Arg arg);
}
