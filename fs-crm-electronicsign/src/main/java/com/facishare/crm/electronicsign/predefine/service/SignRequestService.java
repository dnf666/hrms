package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.SignRequestType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("elec_sign_sign_request")
public interface SignRequestService {
    /**
     * 是否有签署权限
     *
     * 1.平台开关
     * 2.应用级开关
     * 3.这个客户是否有认证账号+启用
     */
    @ServiceMethod("is_has_sign_permission")
    SignRequestType.IsHasSignPermission.Result isHasSignPermission(ServiceContext serviceContext, SignRequestType.IsHasSignPermission.Arg arg);

    /**
     * 获取签章URL
     */
    @ServiceMethod("get_sign_url_or_auto_sign")
    SignRequestType.GetSignUrl.Result getSignUrlOrAutoSign(ServiceContext serviceContext, SignRequestType.GetSignUrl.Arg arg);

    /**
     * 签署完回调
     */
    @ServiceMethod("sign_result_call_back")
    SignRequestType.SignResultCallBack.Result signResultCallBack(ServiceContext serviceContext, SignRequestType.SignResultCallBack.Arg arg);

    /**
     * 查询签署状态
     */
    @ServiceMethod("get_sign_status")
    SignRequestType.GetSignStatus.Result getSignStatus(ServiceContext serviceContext, SignRequestType.GetSignStatus.Arg arg);
}