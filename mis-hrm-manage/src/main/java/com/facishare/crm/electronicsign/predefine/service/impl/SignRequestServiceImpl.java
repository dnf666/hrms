package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.SignRequestManager;
import com.facishare.crm.electronicsign.predefine.service.SignRequestService;
import com.facishare.crm.electronicsign.predefine.service.dto.SignRequestType;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignRequestServiceImpl implements SignRequestService {
    @Autowired
    private SignRequestManager signRequestManager;

    @Override
    public SignRequestType.IsHasSignPermission.Result isHasSignPermission(ServiceContext serviceContext, SignRequestType.IsHasSignPermission.Arg arg) {
        if (arg == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， appType 不能为空");
        }
        if (arg.getAppType() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， appType 不能为空");
        }
        if (!AppTypeEnum.get(arg.getAppType()).isPresent()) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， appType 不合法");
        }
        if (arg.getAccountId() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， accountId 不能为空");
        }

        SignRequestType.IsHasSignPermission.Result result = new SignRequestType.IsHasSignPermission.Result();
        try{
            return signRequestManager.isHasSignPermission(serviceContext.getUser(), arg);
        } catch (ElecSignBusinessException e) {
            result.setStatus(e.getErrorCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            result.setStatus(ElecSignErrorCode.GET_IS_HAS_SIGN_PERMISSION_FAIL.getCode());
            result.setMessage(ElecSignErrorCode.GET_IS_HAS_SIGN_PERMISSION_FAIL.getMessage() + e);
            return result;
        }
    }

    @Override
    public SignRequestType.GetSignUrl.Result getSignUrlOrAutoSign(ServiceContext serviceContext, SignRequestType.GetSignUrl.Arg arg) {
        SignRequestType.GetSignUrl.Result result = new SignRequestType.GetSignUrl.Result();

        //参数检查
        checkArg(arg);

        try{
            return signRequestManager.getSignUrlOrAutoSign(serviceContext.getUser(), arg);
        } catch (ElecSignBusinessException e) {
            result.setStatus(e.getErrorCode());
            result.setMessage(e.getMessage());
            result.setSignType(arg.getSigner().getSignerType());
            return result;
        } catch (Exception e) {
            result.setStatus(ElecSignErrorCode.UPDATE_TEMPLATE_CREATE_CONTRACT_FAIL.getCode());
            result.setMessage(ElecSignErrorCode.UPDATE_TEMPLATE_CREATE_CONTRACT_FAIL.getMessage() + e);
            result.setSignType(arg.getSigner().getSignerType());
            return result;
        }
    }

    /**
     * 参数检查
     */
    private void checkArg(SignRequestType.GetSignUrl.Arg arg) {
        if (arg.getAppType() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， appType 不能为空");
        }
        if (!AppTypeEnum.get(arg.getAppType()).isPresent()) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， appType 不合法");
        }
        if (arg.getObjApiName() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， objApiName 不能为空");
        }
        if (arg.getObjDataId() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， objDataId 不能为空");
        }
        if (arg.getIsReCreateContractIfExpired() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， isReCreateContractIfExpired 不能为空");
        }
        if (arg.getSigner() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， signer 不能为空");
        }

        SignRequestType.GetSignUrl.SignerArg signer = arg.getSigner();

        if (signer.getSignerType() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， signer的signerType 不能为空");
        }
        if (signer.getAccountId() == null && signer.getUpDepartmentId() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， signer的accountId和upDepartmentId 不能都为空");
        }
        if (signer.getOrderNum() == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, ElecSignErrorCode.PARAM_ERROR + "， signer的orderNum 不能为空");
        }
    }

    @Override
    public SignRequestType.SignResultCallBack.Result signResultCallBack(ServiceContext serviceContext, SignRequestType.SignResultCallBack.Arg arg) {
        SignRequestType.SignResultCallBack.Result result = new SignRequestType.SignResultCallBack.Result();
        try {
            signRequestManager.signResultCallBack(serviceContext.getUser(), arg);
            return result;
        } catch (ElecSignBusinessException e) {
            result.setStatus(e.getErrorCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            result.setStatus(ElecSignErrorCode.SIGN_RESULT_CALL_BACK_FAILED.getCode());
            result.setMessage(ElecSignErrorCode.SIGN_RESULT_CALL_BACK_FAILED.getMessage() + e);
            return result;
        }
    }

    @Override
    public SignRequestType.GetSignStatus.Result getSignStatus(ServiceContext serviceContext, SignRequestType.GetSignStatus.Arg arg) {
        SignRequestType.GetSignStatus.Result result = new SignRequestType.GetSignStatus.Result();

        try {
            return signRequestManager.getSignStatus(serviceContext.getUser(), arg);
        } catch (ElecSignBusinessException e) {
            result.setStatus(e.getErrorCode());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            result.setStatus(ElecSignErrorCode.GET_SIGN_STATUS_FAILED.getCode());
            result.setMessage(ElecSignErrorCode.GET_SIGN_STATUS_FAILED.getMessage() + e);
            return result;
        }
    }
}
