package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.FsQuotaType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;

@ServiceModule("elec_sign_fs_quota")
public interface FsQuotaService {


    @ServiceMethod("get_fs_quota")
    FsQuotaType.GetFsQuota.Result getFsQuota();

}
