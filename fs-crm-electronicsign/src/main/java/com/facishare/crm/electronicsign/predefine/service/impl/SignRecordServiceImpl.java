package com.facishare.crm.electronicsign.predefine.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.obj.SignRecordObjManager;
import com.facishare.crm.electronicsign.predefine.service.SignRecordService;
import com.facishare.crm.electronicsign.predefine.service.dto.SignRecordType;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class SignRecordServiceImpl implements SignRecordService {
    @Autowired
    private SignRecordObjManager signRecordObjManager;

    @Override
    public SignRecordType.GetContractFileAttachment.Result getContractFileAttachment(ServiceContext serviceContext, SignRecordType.GetContractFileAttachment.Arg arg) {
        User user = serviceContext.getUser();

        SignRecordType.GetContractFileAttachment.Result result = new SignRecordType.GetContractFileAttachment.Result();
        try {
            ArrayList contractFileAttachment = signRecordObjManager.getContractFileAttachment(user, arg);
            result.setContractFileAttachment(contractFileAttachment);
            return result;
        } catch (ElecSignBusinessException e) {
            result.setStatus(e.getErrorCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            result.setStatus(ElecSignErrorCode.GET_CONTRACT_FILE_ATTACHMENT_FAILED.getCode());
            result.setMessage(ElecSignErrorCode.GET_CONTRACT_FILE_ATTACHMENT_FAILED.getMessage() + e);
            return result;
        }
    }
}
