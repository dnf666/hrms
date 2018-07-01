package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.InternalSignCertifyType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("elec_sign_internal_sign_certify")
public interface InternalSignCertifyService {
    @ServiceMethod("enable_or_disable")
    InternalSignCertifyType.EnableOrDisable.Result enableOrDisable(ServiceContext serviceContext, InternalSignCertifyType.EnableOrDisable.Arg arg);
}