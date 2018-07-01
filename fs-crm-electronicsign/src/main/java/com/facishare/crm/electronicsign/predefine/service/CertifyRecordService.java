package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.CertifyRecordType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * created by dailf on 2018/4/26
 *
 * @author dailf
 */
@ServiceModule("elec_sign_certify_record")
public interface CertifyRecordService {
    /**
     * 分页获取认证记录
     */
    @ServiceMethod("get_certify_record_by_page")
    CertifyRecordType.GetCertifyRecordByPage.Result getCertifyRecordByPage(CertifyRecordType.GetCertifyRecordByPage.Arg arg);

    /**
     * 认证完成回调
     */
    @ServiceMethod("cert_call_back")
    CertifyRecordType.CertCallBack.Result certCallBack(ServiceContext serviceContext, CertifyRecordType.CertCallBack.Arg arg);
}
