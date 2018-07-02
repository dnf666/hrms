package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.predefine.service.dto.InternalSignCertifyType;
import com.facishare.crm.electronicsign.predefine.manager.InternalSignCertifyUseRangeManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.service.InternalSignCertifyService;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class InternalSignCertifyServiceImpl implements InternalSignCertifyService {
    @Resource
    private InternalSignCertifyObjManager tenantCertifyObjManager;
    @Resource
    private InternalSignCertifyUseRangeManager internalSignCertifyUseRangeManager;

    @Override
    public InternalSignCertifyType.EnableOrDisable.Result enableOrDisable(ServiceContext serviceContext, InternalSignCertifyType.EnableOrDisable.Arg arg) {
        tenantCertifyObjManager.enableOrDisable(serviceContext.getUser(), arg.getInternalSignCertifyId(), arg.getUseStatus());
        internalSignCertifyUseRangeManager.updateUseStatusAndDepartmentIds(arg.getInternalSignCertifyId(), arg.getUseStatus());
        return new InternalSignCertifyType.EnableOrDisable.Result();
    }
}