package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.constants.ElecSignConstants;
import com.facishare.crm.electronicsign.enums.AppElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.TenantElecSignSwitchEnum;
import com.facishare.crm.electronicsign.enums.status.ElecSignInitStatusEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.fxiaoke.bizconf.api.BizConfApi;
import com.fxiaoke.bizconf.arg.ConfigArg;
import com.fxiaoke.bizconf.arg.QueryConfigArg;
import com.fxiaoke.bizconf.bean.Rank;
import com.fxiaoke.bizconf.bean.ValueType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class ElecSignConfigManager {
    @Resource
    private BizConfApi bizConfApi;

    private ConfigArg buildConfigArg(User user, String key, String value) {
        return ConfigArg.builder().key(key).operator(user.getUserId()).pkg(ElecSignConstants.PKG).userId(user.getUserId()).tenantId(user.getTenantId()).valueType(ValueType.STRING).value(value).rank(Rank.TENANT).build();
    }

    /**
     * 查看'租户电子签章开关'状态
     */
    public TenantElecSignSwitchEnum getTenantElecSignStatus(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ElecSignConstants.TENANT_ELEC_SIGN_SWITCH_KEY).tenantId(tenantId).pkg(ElecSignConstants.PKG).build());
            if (StringUtils.isBlank(config)) {
                return TenantElecSignSwitchEnum.OFF;
            }
            return TenantElecSignSwitchEnum.get(Integer.valueOf(config)).orElseThrow(() -> new ValidateException("tenantElecSignStatus不合法"));
        } catch (Exception e) {
            log.warn("getTenantElecSignStatus error,tenantId:{}", tenantId, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 更新'租户电子签章开关'状态，没有就新增一条记录
     */
    public int updateTenantElecSignSwitchStatus(User user, TenantElecSignSwitchEnum switchEnum) {
        try {
            int result = -1;
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ElecSignConstants.TENANT_ELEC_SIGN_SWITCH_KEY).tenantId(user.getTenantId()).pkg(ElecSignConstants.PKG).build());
            if (StringUtils.isBlank(config)) {
                ConfigArg configArg = buildConfigArg(user, ElecSignConstants.TENANT_ELEC_SIGN_SWITCH_KEY, String.valueOf(switchEnum.getStatus()));
                result = bizConfApi.createConfig(configArg);
                log.info("bizConfApi.createConfig ,configArg:{}", configArg);
            } else {
                if (Objects.equals(switchEnum.getStatus(), config)) {
                    return 1;
                }
                result = bizConfApi.updateConfig(buildConfigArg(user, ElecSignConstants.TENANT_ELEC_SIGN_SWITCH_KEY, String.valueOf(switchEnum.getStatus())));
            }
            return result;
        } catch (Exception e) {
            log.warn("updateTenantElecSignSwitchStatus error, user:{}, switchEnum:{}", user, switchEnum, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 查看'电子签章初始化开关'状态
     */
    public ElecSignInitStatusEnum getElecSignInitStatus(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ElecSignConstants.ELEC_SIGN_INIT_STATUS_KEY).tenantId(tenantId).pkg(ElecSignConstants.PKG).build());
            if (StringUtils.isBlank(config)) {
                return ElecSignInitStatusEnum.NOT_OPEN;
            }
            return ElecSignInitStatusEnum.get(Integer.valueOf(config)).orElseThrow(() -> new ValidateException("elecSignInitStatus不合法"));
        } catch (Exception e) {
            log.warn("getElecSignInitStatus error,tenantId:{}", tenantId, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 更新'电子签章初始化开关'状态，没有就新增一条记录
     */
    public int updateElecSignInitStatus(User user, ElecSignInitStatusEnum statusEnum) {
        try {
            int result = -1;
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ElecSignConstants.ELEC_SIGN_INIT_STATUS_KEY).tenantId(user.getTenantId()).pkg(ElecSignConstants.PKG).build());
            if (StringUtils.isBlank(config)) {
                ConfigArg configArg = buildConfigArg(user, ElecSignConstants.ELEC_SIGN_INIT_STATUS_KEY, String.valueOf(statusEnum.getStatus()));
                result = bizConfApi.createConfig(configArg);
                log.info("bizConfApi.createConfig ,configArg:{}", configArg);
            } else {
                if (Objects.equals(statusEnum.getStatus(), config)) {
                    return 1;
                }
                result = bizConfApi.updateConfig(buildConfigArg(user, ElecSignConstants.ELEC_SIGN_INIT_STATUS_KEY, String.valueOf(statusEnum.getStatus())));
            }
            return result;
        } catch (Exception e) {
            log.warn("updateTenantElecSignSwitchStatus error, user:{}, statusEnum:{}", user, statusEnum, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 查看'应用电子签章开关'状态
     */
    public AppElecSignSwitchEnum getAppElecSignStatus(String tenantId, AppTypeEnum appTypeEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(getAppElecSignConfigKey(appTypeEnum)).tenantId(tenantId).pkg(ElecSignConstants.PKG).build());
            if (StringUtils.isBlank(config)) {
                return AppElecSignSwitchEnum.OFF;
            }
            return AppElecSignSwitchEnum.get(Integer.valueOf(config)).orElseThrow(() -> new ValidateException("appElecSignStatus不合法"));
        } catch (Exception e) {
            log.warn("getAppElecSignStatus error,tenantId:{}", tenantId, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public int updateAppElecSignStatus(User user, AppTypeEnum appTypeEnum, AppElecSignSwitchEnum appElecSignSwitchEnum) {
        try {
            int result;
            String confKey = getAppElecSignConfigKey(appTypeEnum);
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(confKey).tenantId(user.getTenantId()).pkg(ElecSignConstants.PKG).build());
            ConfigArg configArg = buildConfigArg(user, confKey, String.valueOf(appElecSignSwitchEnum.getStatus()));
            if (StringUtils.isBlank(config)) {
                result = bizConfApi.createConfig(configArg);
                log.info("bizConfApi.createConfig ,configArg:{}, result{}", configArg, result);
            } else {
                if (Objects.equals(String.valueOf(appElecSignSwitchEnum.getStatus()), config)) {
                    return 1;
                }
                result = bizConfApi.updateConfig(configArg);
                log.info("bizConfApi.updateConfig ,configArg:{}, result{}", configArg, result);
            }
            return result;
        } catch (Exception e) {
            log.warn("updateTenantElecSignSwitchStatus error, user:{}, appElecSignSwitchEnum:{}", user, appElecSignSwitchEnum, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    private String getAppElecSignConfigKey(AppTypeEnum appTypeEnum) {
        if (Objects.equals(appTypeEnum.getType(), AppTypeEnum.DING_HUO_TONG.getType())) {
            return ElecSignConstants.DING_HUO_TONG_ELEC_SIGN_SWITCH_KEY;
        }
        if (Objects.equals(appTypeEnum.getType(), AppTypeEnum.ACCOUNT_STATEMENT.getType())) {
            return ElecSignConstants.ACCOUNT_STATEMENT_ELEC_SIGN_SWITCH_KEY;
        }
        throw new ValidateException("appType不合法");
    }
}

