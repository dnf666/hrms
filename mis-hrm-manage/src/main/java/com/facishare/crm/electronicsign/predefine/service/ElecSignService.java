package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.ElecSignType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("elec_sign")
public interface ElecSignService {
    /**
     * 查询 "初始化状态、电子签章开关、余额警告、业务配额、购买记录"
     */
    @ServiceMethod("get_tenant_elec_sign_info")
    ElecSignType.GetTenantElecSignInfoResult getTenantElecSignInfo(ServiceContext serviceContext);

    /**
     *  修改初始化状态（测试需要）
     */
    @ServiceMethod("change_init_status")
    ElecSignType.InitElecSign.Result changeInitStatusNotOpen(ServiceContext serviceContext);

    /**
     *  修改初始化状态（测试需要）
     */
    @ServiceMethod("change_init_status_opened")
    ElecSignType.InitElecSign.Result changeInitStatusOpened(ServiceContext serviceContext);

    /**
     *  初始化
     */
    @ServiceMethod("init_elec_sign")
    ElecSignType.InitElecSign.Result initElecSign(ServiceContext serviceContext);

    /**
     *  测试
     */
    @ServiceMethod("is_use_custom_account_statement_obj_api_name")
    ElecSignType.InitElecSign.Result isUseCustomAccountStatementObjApiName(ServiceContext serviceContext);

    /**
     *  初始化权限（测试）
     */
    @ServiceMethod("init_elec_sign_privilege")
    ElecSignType.InitElecSign.Result initElecSignPrivilege(ServiceContext serviceContext);

    /**
     * 启用or停用租户级开关（电子签章、余额告警）
     */
    @ServiceMethod("enable_or_disable_tenant_switch")
    ElecSignType.EnableOrDisableTenantSwitch.Result enableOrDisableTenantSwitch(ServiceContext serviceContext, ElecSignType.EnableOrDisableTenantSwitch.Arg arg);
}