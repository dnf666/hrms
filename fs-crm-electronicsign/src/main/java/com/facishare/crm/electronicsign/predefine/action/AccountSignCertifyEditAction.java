package com.facishare.crm.electronicsign.predefine.action;

import com.facishare.crm.action.CommonEditAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.obj.AccountSignCertifyObjManager;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class AccountSignCertifyEditAction extends CommonEditAction {
    private AccountSignCertifyObjManager accountSignCertifyObjManager = SpringUtil.getContext().getBean(AccountSignCertifyObjManager.class);
    private ServiceFacade serviceFacade = SpringUtil.getContext().getBean(ServiceFacade.class);

    @Override
    protected void before(Arg arg) {
        User user = this.actionContext.getUser();
        log.info("arg[{}]", arg);

        //手机号验证
        accountSignCertifyObjManager.checkMobile(arg);

        super.before(arg);

        checkHasUse(user, arg);

        // 校验认证状态
        String certifyStatus = objectData.get(AccountSignCertifyObjConstants.Field.CertifyStatus.apiName, String.class);
        if (Objects.equals(CertifyStatusEnum.CERTIFYING.getStatus(), certifyStatus)
                || Objects.equals(CertifyStatusEnum.CERTIFYING.getStatus(), certifyStatus)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "认证中或已认证的数据不可以修改");
        }

        String regMobile = (String) arg.getObjectData().get(AccountSignCertifyObjConstants.Field.RegMobile.apiName);
        String recordType = (String) arg.getObjectData().get(SystemConstants.Field.RecordType.apiName);
        arg.getObjectData().put(AccountSignCertifyObjConstants.Field.BestSignAccount.apiName, accountSignCertifyObjManager.generateBestSignAccount(user, regMobile, recordType));
    }

    /**
     * 同租户中不能重复
     */
    private void checkHasUse(User user, Arg arg) {
        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), this.objectData.getId(), this.objectData.getDescribeApiName());

        //'注册手机号'在同个租户中是否已被使用
        String currentRegMobile = (String) objectData.get(AccountSignCertifyObjConstants.Field.RegMobile.apiName);
        String newRegMobile = (String) arg.getObjectData().get(AccountSignCertifyObjConstants.Field.RegMobile.apiName);
        if (!Objects.equals(currentRegMobile, newRegMobile)) {
            if (accountSignCertifyObjManager.hasRegMobileUsed(user, newRegMobile)) {
                throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "注册手机号已被使用");
            }
        }

        //'企业名称'在同个租户中是否已被使用
        String currentEnterpriseName = (String) objectData.get(AccountSignCertifyObjConstants.Field.EnterpriseName.apiName);
        String newEnterpriseName = (String) arg.getObjectData().get(AccountSignCertifyObjConstants.Field.EnterpriseName.apiName);
        if (!Objects.equals(currentEnterpriseName, newEnterpriseName)) {
            if (accountSignCertifyObjManager.hasEnterpriseNameUsed(user, newEnterpriseName)) {
                throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "企业名称已被使用");
            }
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        User user = this.getActionContext().getUser();

        //没审批流
        String newLifeStatus = (String) result.getObjectData().get(SystemConstants.Field.LifeStatus.apiName);
        if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value )) {
            accountSignCertifyObjManager.reg(user, arg.getObjectData());
        }

        return result;
    }
}