package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.BuyQuotaType;
import com.facishare.crm.electronicsign.predefine.service.dto.TenantQuotaType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * created by dailf on 2018/4/25
 *
 * @author dailf
 */
@ServiceModule("elec_sign_tenant_quota")
public interface TenantQuotaService {
    /**
     * 分页返回租户配额
     *
     * @param arg 条件
     * @return 分页的租户配额
     */
    @ServiceMethod("get_tenant_quota_by_page")
    TenantQuotaType.GetTenantQuotaByPage.Result getTenantQuotaByPage(TenantQuotaType.GetTenantQuotaByPage.Arg arg);

    /**
     * 购买配额
     *
     * @param serviceContext 参数
     * @param arg
     * @return 购买结果
     */
    @ServiceMethod("buy_quota_by_fs")
    BuyQuotaType.AddBuyQuota.Result buyQuotaByFs(ServiceContext serviceContext, BuyQuotaType.AddBuyQuota.Arg arg);

}
