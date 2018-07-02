package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.SignRecordType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("elec_sign_sign_record")
public interface SignRecordService {
    /**
     * 获取合同附件
     */
    @ServiceMethod("get_contract_file_attachment")
    SignRecordType.GetContractFileAttachment.Result getContractFileAttachment(ServiceContext serviceContext, SignRecordType.GetContractFileAttachment.Arg arg);
}