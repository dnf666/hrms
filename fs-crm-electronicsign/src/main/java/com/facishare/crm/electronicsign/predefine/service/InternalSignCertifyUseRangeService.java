package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.InternalSignCertifyUseRangeType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("elec_sign_internal_sign_certify_use_range")
public interface InternalSignCertifyUseRangeService {

    /**
     * 获取内部认证对象
     */
    @ServiceMethod("get_internal_sign_certify_use_range_setting_list")
    InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.Result getInternalSignCertifyUseRangeSettingList(ServiceContext serviceContext);

    @ServiceMethod("set_use_range")
    InternalSignCertifyUseRangeType.SetUseRange.Result setUseRange(ServiceContext serviceContext, InternalSignCertifyUseRangeType.SetUseRange.Arg arg);

}
