package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.enums.AppElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.TenantElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignTypeEnum;
import com.facishare.crm.electronicsign.predefine.dao.SignSettingDAO;
import com.facishare.crm.electronicsign.predefine.manager.ElecSignConfigManager;
import com.facishare.crm.electronicsign.predefine.manager.SignSettingManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.model.SignSettingDO;
import com.facishare.crm.electronicsign.predefine.model.SignerSettingDO;
import com.facishare.crm.electronicsign.predefine.model.vo.SignSettingVO;
import com.facishare.crm.electronicsign.predefine.service.SignSettingService;
import com.facishare.crm.electronicsign.predefine.service.dto.ElecSignType;
import com.facishare.crm.electronicsign.util.CopyUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class SignSettingServiceImpl implements SignSettingService {
    @Resource
    private ElecSignConfigManager elecSignConfigManager;
    @Resource
    private InternalSignCertifyObjManager internalSignCertifyObjManager;
    @Resource
    private SignSettingManager signSettingManager;
    @Resource
    private SignSettingDAO signSettingDAO;

    @Override
    public ElecSignType.EnableSwitchForApp.Result enableSwitchForApp(ServiceContext serviceContext, ElecSignType.EnableSwitchForApp.Arg arg) {
        Preconditions.checkArgument(Objects.nonNull(arg));
        if (!AppTypeEnum.get(arg.getAppType()).isPresent()) {
            throw new ValidateException("arg.appType 不合法");
        }
        AppTypeEnum argAppTypeEnum = AppTypeEnum.get(arg.getAppType()).get();
        if (!AppElecSignSwitchEnum.get(arg.getStatus()).isPresent()) {
            throw new ValidateException("arg.status 不合法");
        }
        AppElecSignSwitchEnum argStatusEnum = AppElecSignSwitchEnum.get(arg.getStatus()).get();

        // 租户电子签章是否开启
        TenantElecSignSwitchEnum tenantElecSignSwitchEnum = elecSignConfigManager.getTenantElecSignStatus(serviceContext.getTenantId());
        if (Objects.equals(TenantElecSignSwitchEnum.OFF, tenantElecSignSwitchEnum)) {
            return new ElecSignType.EnableSwitchForApp.Result(2, "公司暂未启用电子签章业务，请联系系统管理员！");
        }

        // 开启时校验是否存在已经认证的内部签章认证信息
        if (Objects.equals(AppElecSignSwitchEnum.ON, argStatusEnum)
                && !internalSignCertifyObjManager.hasAnyCertifiedObjectData(serviceContext.getTenantId())) {
            return new ElecSignType.EnableSwitchForApp.Result(2, "请先联系系统管理员，为企业申请实名认证，认证通过后，服务即可使用。");
        }

        // 更新应用签章开关
        elecSignConfigManager.updateAppElecSignStatus(serviceContext.getUser(), argAppTypeEnum, argStatusEnum);

        return new ElecSignType.EnableSwitchForApp.Result();
    }

    @Override
    public ElecSignType.QueryAppSwitchAndSignSetting.Result queryAppSwitchAndSignSetting(ServiceContext serviceContext, ElecSignType.QueryAppSwitchAndSignSetting.Arg arg) {
        Preconditions.checkArgument(Objects.nonNull(arg));

        if (!AppTypeEnum.get(arg.getAppType()).isPresent()) {
            throw new ValidateException("arg.appType 不合法");
        }
        AppTypeEnum argAppType = AppTypeEnum.get(arg.getAppType()).get();

        TenantElecSignSwitchEnum tenantElecSignSwitchEnum = elecSignConfigManager.getTenantElecSignStatus(serviceContext.getTenantId());
        AppElecSignSwitchEnum appElecSignSwitchEnum = elecSignConfigManager.getAppElecSignStatus(serviceContext.getTenantId(), AppTypeEnum.get(arg.getAppType()).get());
        List<SignSettingVO> signSettingVOList = signSettingManager.query(serviceContext.getTenantId(), argAppType);
        ElecSignType.QueryAppSwitchAndSignSetting.Result result = new ElecSignType.QueryAppSwitchAndSignSetting.Result();
        result.setPlatformSwitch(tenantElecSignSwitchEnum.getStatus());
        result.setAppSwitch(appElecSignSwitchEnum.getStatus());
        result.setSignSettings(signSettingVOList);
        return result;
    }

    @Override
    public ElecSignType.SaveOrUpdateSignSetting.Result saveOrUpdateSignSetting(ServiceContext serviceContext, ElecSignType.SaveOrUpdateSignSetting.Arg arg) {
        Preconditions.checkArgument(Objects.nonNull(arg), "arg is null");
        Preconditions.checkArgument(Objects.nonNull(arg.getObjApiName()), "objApiName is null");
        Preconditions.checkArgument(Objects.nonNull(arg.getIsHasOrder()), "isHasOrder is null");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(arg.getSignerSettings()), "signerSettings is null");
        arg.getSignerSettings().forEach(signerSettingVO -> {
            Preconditions.checkArgument(Objects.nonNull(signerSettingVO.getSignType()), "signType is null");
            if (!SignTypeEnum.get(signerSettingVO.getSignType()).isPresent()) {
                throw new ValidateException("signerSettings.signType 不合法");
            }
            Preconditions.checkArgument(Objects.nonNull(signerSettingVO.getOrderNum()), "orderNum is null");
            Preconditions.checkArgument(Objects.nonNull(signerSettingVO.getKeyword()), "keyword is null");
        });

        if (!AppTypeEnum.get(arg.getAppType()).isPresent()) {
            throw new ValidateException("arg.appType 不合法");
        }

        // 租户电子签章是否开启
        TenantElecSignSwitchEnum tenantElecSignSwitchEnum = elecSignConfigManager.getTenantElecSignStatus(serviceContext.getTenantId());
        if (Objects.equals(TenantElecSignSwitchEnum.OFF, tenantElecSignSwitchEnum)) {
            return new ElecSignType.SaveOrUpdateSignSetting.Result(2, "公司暂未启用电子签章业务，请联系系统管理员！");
        }

        SignSettingDO entity = buildEntityForUpdate(serviceContext, arg);
        signSettingDAO.createOrUpdate(entity);

        return new ElecSignType.SaveOrUpdateSignSetting.Result();
    }

    private SignSettingDO buildEntityForUpdate(ServiceContext serviceContext, ElecSignType.SaveOrUpdateSignSetting.Arg arg) {
        SignSettingDO entity = CopyUtil.copyOne(SignSettingDO.class, arg);
        entity.setTenantId(serviceContext.getTenantId());
        if (!CollectionUtils.isEmpty(arg.getSignerSettings())) {
            entity.setSignerSettings(CopyUtil.copyMany(SignerSettingDO.class, arg.getSignerSettings()));
        }
        return entity;
    }

    @Override
    public ElecSignType.DeleteSignSetting.Result deleteSignSetting(ServiceContext serviceContext, ElecSignType.DeleteSignSetting.Arg arg) {
        Preconditions.checkNotNull(arg, "arg is null");
        Preconditions.checkNotNull(arg.getSignSettingId(), "arg.signSettingId is null");
        Preconditions.checkNotNull(arg.getAppType(), "arg.appType is null");
        if (!AppTypeEnum.get(arg.getAppType()).isPresent()) {
            throw new ValidateException("arg.appType 不合法");
        }
        signSettingManager.delete(serviceContext.getTenantId(), arg.getAppType(), arg.getSignSettingId());
        return new ElecSignType.DeleteSignSetting.Result();
    }
}
