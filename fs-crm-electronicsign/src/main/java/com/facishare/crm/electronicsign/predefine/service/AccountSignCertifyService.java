package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.AccountSignCertifyType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("elec_sign_account_sign_certify")
public interface AccountSignCertifyService {
    @ServiceMethod("enable_or_disable")
    AccountSignCertifyType.EnableOrDisable.Result enableOrDisable(ServiceContext serviceContext, AccountSignCertifyType.EnableOrDisable.Arg arg);
}