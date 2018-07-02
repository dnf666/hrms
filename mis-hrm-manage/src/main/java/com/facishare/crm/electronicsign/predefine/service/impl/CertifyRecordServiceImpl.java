package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.CertifyRecordManager;
import com.facishare.crm.electronicsign.predefine.model.vo.CertifyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.service.CertifyRecordService;
import com.facishare.crm.electronicsign.predefine.service.dto.CertifyRecordType;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/4/26
 *
 * @author dailf
 */
@Slf4j
@Component
public class CertifyRecordServiceImpl implements CertifyRecordService {
    @Resource
    private CertifyRecordManager certifyRecordManager;

    @Override
    public CertifyRecordType.GetCertifyRecordByPage.Result getCertifyRecordByPage(CertifyRecordType.GetCertifyRecordByPage.Arg arg) {
        CertifyRecordType.GetCertifyRecordByPage.Result result = new CertifyRecordType.GetCertifyRecordByPage.Result();
            Pager<CertifyRecordVO> pager = certifyRecordManager.getCertifyRecordByPage(arg);
            result.setPager(pager);
        return result;

    }

    @Override
    public CertifyRecordType.CertCallBack.Result certCallBack(ServiceContext serviceContext, CertifyRecordType.CertCallBack.Arg arg) {
        CertifyRecordType.CertCallBack.Result result = new CertifyRecordType.CertCallBack.Result();
        try {
            certifyRecordManager.certCallBack(arg);
            return result;
        } catch (ElecSignBusinessException e) {
            result.setStatus(e.getErrorCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            result.setStatus(ElecSignErrorCode.CERTIFY_CALLBACK_FAILED.getCode());
            result.setMessage(ElecSignErrorCode.CERTIFY_CALLBACK_FAILED.getMessage() + e);
            return result;
        }
    }
}
