package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.predefine.manager.obj.AccountSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.service.AccountSignCertifyService;
import com.facishare.crm.electronicsign.predefine.service.dto.AccountSignCertifyType;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class AccountSignCertifyServiceImpl implements AccountSignCertifyService {
    @Resource
    private AccountSignCertifyObjManager accountSignCertifyObjManager;

    @Override
    public AccountSignCertifyType.EnableOrDisable.Result enableOrDisable(ServiceContext serviceContext, AccountSignCertifyType.EnableOrDisable.Arg arg) {
        accountSignCertifyObjManager.enableOrDisable(serviceContext.getUser(), arg.getAccountSignCertifyId(), arg.getUseStatus());
        return new AccountSignCertifyType.EnableOrDisable.Result();
    }
}