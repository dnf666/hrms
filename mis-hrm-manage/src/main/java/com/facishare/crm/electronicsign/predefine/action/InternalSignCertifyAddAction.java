package com.facishare.crm.electronicsign.predefine.action;

import com.facishare.crm.action.CommonAddAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class InternalSignCertifyAddAction extends CommonAddAction {
    private InternalSignCertifyObjManager internalSignCertifyObjManager = SpringUtil.getContext().getBean(InternalSignCertifyObjManager.class);

    @Override
    protected void before(Arg arg) {
        User user = this.actionContext.getUser();
        log.info("InternalSignCertifyAddAction, arg[{}]", arg);

        //手机号验证
        internalSignCertifyObjManager.checkMobile(arg);

        //不能重复的检查
        checkHasUse(user, arg);

        //补充数据
        String regMobile = (String) arg.getObjectData().get(InternalSignCertifyObjConstants.Field.RegMobile.apiName);
        arg.getObjectData().put(InternalSignCertifyObjConstants.Field.CertifyStatus.apiName, CertifyStatusEnum.NO_RECORD.getStatus());
        arg.getObjectData().put(InternalSignCertifyObjConstants.Field.BestSignAccount.apiName, internalSignCertifyObjManager.generateBestSignAccount(user, regMobile));
        log.info("InternalSignCertifyAddAction before objectData[{}]", arg.getObjectData());
        super.before(arg);
    }

    /**
     * 不能重复的检查
     */
    private void checkHasUse(User user, Arg arg) {
        String regMobile = (String) arg.getObjectData().get(InternalSignCertifyObjConstants.Field.RegMobile.apiName);
        if (internalSignCertifyObjManager.hasRegMobileUsed(user, regMobile)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册手机号已被使用");
        }

        String enterpriseName = (String) arg.getObjectData().get(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName);
        if (internalSignCertifyObjManager.hasEnterpriseNameUsed(user, enterpriseName)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "企业名称已被使用");
        }
    }
    
    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        User user = this.getActionContext().getUser();

        //没审批流
        String newLifeStatus = (String) result.getObjectData().get(SystemConstants.Field.LifeStatus.apiName);
        if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value )) {
            internalSignCertifyObjManager.reg(user, arg.getObjectData());
        }

        return result;
    }
}