package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.TenantQuotaManager;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.model.vo.TenantQuotaVO;
import com.facishare.crm.electronicsign.predefine.service.TenantQuotaService;
import com.facishare.crm.electronicsign.predefine.service.dto.BuyQuotaType;
import com.facishare.crm.electronicsign.predefine.service.dto.TenantQuotaType;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.paas.appframework.core.exception.PermissionError;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * created by dailf on 2018/4/25
 *
 * @author dailf
 */
@Slf4j
@Component
public class TenantQuotaServiceImpl implements TenantQuotaService {
    @Resource
    private TenantQuotaManager tenantQuotaManager;

    @Override
    public TenantQuotaType.GetTenantQuotaByPage.Result getTenantQuotaByPage(TenantQuotaType.GetTenantQuotaByPage.Arg arg) {
        TenantQuotaType.GetTenantQuotaByPage.Result result = new TenantQuotaType.GetTenantQuotaByPage.Result();
        Pager<TenantQuotaVO> data = tenantQuotaManager.getTenantQuotaByPage(arg);
        result.setPager(data);
        return result;

    }

    @Override
    public BuyQuotaType.AddBuyQuota.Result buyQuotaByFs(ServiceContext serviceContext, BuyQuotaType.AddBuyQuota.Arg arg) {
        if (!Objects.equals(serviceContext.getTenantId(), ConfigCenter.X_FS_EI_FOR_ELECTRON_SIGN)
                || !Objects.equals(serviceContext.getUser().getUserId(), User.SUPPER_ADMIN_USER_ID)) {
            throw new PermissionError("无操作权限");
        }
        if (Objects.isNull(arg)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.ARG_NULL_ERROR);
        }
        BuyQuotaType.AddBuyQuota.Result result = new BuyQuotaType.AddBuyQuota.Result();
        int updateResult = tenantQuotaManager.buyQuotaByFs(arg);
        result.setErrCode(updateResult);
        result.setErrMessage("更新成功");
        return result;

    }
}
