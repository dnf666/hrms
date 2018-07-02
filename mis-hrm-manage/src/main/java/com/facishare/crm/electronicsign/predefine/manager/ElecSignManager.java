package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.enums.TenantElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.type.SwitchTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.service.dto.ElecSignType;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 电子签证
 */
@Service
@Slf4j
public class ElecSignManager {
    @Resource
    private ElecSignConfigManager elecSignConfigManager;
    @Resource
    private InternalSignCertifyObjManager internalSignCertifyObjManager;

    /**
     * 开启or关闭
     */
    public void enableOrDisableSwitch(User user, ElecSignType.EnableOrDisableTenantSwitch.Arg arg) {
        //租户'电子签章开关'
        if (Objects.equals(arg.getSwitchType(), SwitchTypeEnum.TENANT_ELECTRONIC_SIGN.getType())) {
            //查老的
            TenantElecSignSwitchEnum tenantElecSignSwitchEnum = elecSignConfigManager.getTenantElecSignStatus(user.getTenantId());
            if (Objects.equals(tenantElecSignSwitchEnum.getStatus(), arg.getStatus())) {
                return;
            }

            //想要开启，必须有'已认证'+'已启用'的'内部签章认证'记录
            if (Objects.equals(arg.getStatus(), TenantElecSignSwitchEnum.ON.getStatus())) {
                if (!internalSignCertifyObjManager.isHasCertifiedAndEnableRecord(user)) {
                    throw new ElecSignBusinessException(ElecSignErrorCode.HAS_NO_CERTIFIED_AND_ENABLE_TENANT_CERTIFY_OBJ_RECORD, "请先为企业申请实名认证，认证通过并启用后，服务即可使用。");
                }
            }

            //更新
            elecSignConfigManager.updateTenantElecSignSwitchStatus(user, TenantElecSignSwitchEnum.get(arg.getStatus()).get());
        }
    }
}