package com.facishare.crm.sfa.utilities.proxy;

import com.facishare.crm.sfa.utilities.proxy.model.GetHomePermissionsModel;
import com.facishare.rest.proxy.annotation.GET;
import com.facishare.rest.proxy.annotation.HeaderParam;
import com.facishare.rest.proxy.annotation.RestResource;


/**
 * @author cqx
 * @date 2018/1/20 14:39
 */
@RestResource(value = "HomePage", desc = "获取首页权限&列表", contentType = "application/json")
public interface GetHomePermissionsProxy {

    @GET(value = "/homepage/gethomepermissions", desc = "获取首页权限")
    GetHomePermissionsModel.Result getHomePermissionsByTenantId(@HeaderParam("x-fs-ei") String tenantId,@HeaderParam("x-fs-userInfo") String userId);


    @GET(value = "/homepage/getmenu", desc = "获取列表")
    GetHomePermissionsModel.Result getMenuByTenantId(@HeaderParam("x-fs-ei") String tenantId,@HeaderParam("x-fs-userInfo") String userId);
}
