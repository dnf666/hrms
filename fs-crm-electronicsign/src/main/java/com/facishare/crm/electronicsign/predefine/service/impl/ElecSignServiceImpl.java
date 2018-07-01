package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.enums.TenantElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.status.ElecSignInitStatusEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.predefine.manager.*;
import com.facishare.crm.electronicsign.predefine.manager.obj.ElecSignInitManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.ElecSignPrivilegeManager;
import com.facishare.crm.electronicsign.predefine.model.vo.TenantQuotaVO;
import com.facishare.crm.electronicsign.predefine.service.ElecSignService;
import com.facishare.crm.electronicsign.predefine.service.dto.ElecSignType;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ElecSignServiceImpl implements ElecSignService {
    @Resource
    private TenantQuotaManager tenantQuotaManager;
    @Resource
    private ElecSignConfigManager elecSignConfigManager;
    @Resource
    private ElecSignManager elecSignManager;
    @Resource
    private ElecSignInitManager elecSignInitManager;
    @Autowired
    private ElecSignPrivilegeManager elecSignPrivilegeManager;
    @Resource
    private GrayReleaseManager grayReleaseManager;
    @Resource
    private BuyRecordManager buyRecordManager;

    @Override
    public ElecSignType.GetTenantElecSignInfoResult getTenantElecSignInfo(ServiceContext serviceContext) {
        String tenantId = serviceContext.getTenantId();
        ElecSignType.GetTenantElecSignInfoResult result = new ElecSignType.GetTenantElecSignInfoResult();

        // 是否初始化灰度
        result.setIsGrayed(grayReleaseManager.isInitSwitchGrayed(tenantId) ? 1 : 2);

        //是否初始化
        ElecSignInitStatusEnum elecSignInitStatusEnum = elecSignConfigManager.getElecSignInitStatus(tenantId);
        result.setInitStatus(elecSignInitStatusEnum.getStatus());
        if (!Objects.equals(elecSignInitStatusEnum, ElecSignInitStatusEnum.OPENED)) {
            return result;
        }

        //电子签章开关
        TenantElecSignSwitchEnum tenantElecSignSwitchEnum = elecSignConfigManager.getTenantElecSignStatus(tenantId);
        result.setTenantElecSignSwitch(tenantElecSignSwitchEnum.getStatus());

        //租户配额
        List<TenantQuotaVO> tenantQuotaVOS = tenantQuotaManager.getByTenantId(tenantId);
        result.setTenantQuotas(tenantQuotaVOS);

        //最新5条购买记录
        result.setBuyRecords(buyRecordManager.getLastBuyRecordVOs(tenantId, 5));

        return result;
    }

    @Override
    public ElecSignType.InitElecSign.Result changeInitStatusNotOpen(ServiceContext serviceContext) {
        elecSignConfigManager.updateElecSignInitStatus(serviceContext.getUser(), ElecSignInitStatusEnum.OPEN_FAIL);
        return new ElecSignType.InitElecSign.Result();
    }

    @Override
    public ElecSignType.InitElecSign.Result changeInitStatusOpened(ServiceContext serviceContext) {
        elecSignConfigManager.updateElecSignInitStatus(serviceContext.getUser(), ElecSignInitStatusEnum.OPENED);
        return new ElecSignType.InitElecSign.Result();
    }

    @Override
    public ElecSignType.InitElecSign.Result initElecSign(ServiceContext serviceContext) {
        try {
            elecSignInitManager.init(serviceContext.getUser());
            return new ElecSignType.InitElecSign.Result();
        } catch (ElecSignBusinessException e) {
            ElecSignType.InitElecSign.Result result = new ElecSignType.InitElecSign.Result();
            result.setInitStatus(ElecSignInitStatusEnum.OPEN_FAIL.getStatus());
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            ElecSignType.InitElecSign.Result result = new ElecSignType.InitElecSign.Result();
            result.setInitStatus(ElecSignInitStatusEnum.OPEN_FAIL.getStatus());
            result.setMessage("电子签章初始化失败，" + e);
            return result;
        }
    }

    @Override
    public ElecSignType.InitElecSign.Result isUseCustomAccountStatementObjApiName(ServiceContext serviceContext) {
        boolean is = ConfigCenter.isUseCustomAccountStatementObjApiName(serviceContext.getTenantId());
        return null;
    }

    @Override
    public ElecSignType.InitElecSign.Result initElecSignPrivilege(ServiceContext serviceContext) {
        elecSignPrivilegeManager.initPrivilege(serviceContext.getUser());
        return new ElecSignType.InitElecSign.Result();
    }

    @Override
    public ElecSignType.EnableOrDisableTenantSwitch.Result enableOrDisableTenantSwitch(ServiceContext serviceContext, ElecSignType.EnableOrDisableTenantSwitch.Arg arg) {
        ElecSignType.EnableOrDisableTenantSwitch.Result result = new ElecSignType.EnableOrDisableTenantSwitch.Result();
        elecSignManager.enableOrDisableSwitch(serviceContext.getUser(), arg);
        return result;
    }
}