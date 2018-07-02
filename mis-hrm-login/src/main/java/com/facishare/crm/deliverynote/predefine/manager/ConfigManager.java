package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.deliverynote.constants.DeliveryNoteConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteSwitchEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
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
import java.util.Objects;

/**
 * "发货单"开关
 * Created by chenzs on 2018/1/10.
 */
@Slf4j
@Service
public class ConfigManager {
    @Resource
    private BizConfApi bizConfApi;

    /**
     * 查看开关状态
     */
    public DeliveryNoteSwitchEnum getDeliveryNoteStatus(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(DeliveryNoteConstants.SWITCH_KEY).tenantId(tenantId).pkg(DeliveryNoteConstants.SWITCH_PKG).build());
            if (StringUtils.isBlank(config)) {
                return DeliveryNoteSwitchEnum.NOT_OPEN;
            }
            return DeliveryNoteSwitchEnum.get(Integer.valueOf(config)).orElseThrow(() -> new ValidateException("DeliveryNoteStatus不合法"));
        } catch (Exception e) {
            log.warn("getConfig error,tenantId:{}", tenantId, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 更新开关状态，没有就新增一条记录
     */
    public int updateDeliveryNoteStatus(User user, DeliveryNoteSwitchEnum switchEnum) {
        try {
            int result = -1;
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(DeliveryNoteConstants.SWITCH_KEY).tenantId(user.getTenantId()).pkg(DeliveryNoteConstants.SWITCH_PKG).build());
            if (StringUtils.isBlank(config)) {
                ConfigArg configArg = buildConfigArg(user, DeliveryNoteConstants.SWITCH_KEY, String.valueOf(switchEnum.getStatus()));
                result = bizConfApi.createConfig(configArg);
                log.info("bizConfApi.createConfig ,configArg:{}", configArg);
            } else {
                if (switchEnum.getStatus() == DeliveryNoteSwitchEnum.NOT_OPEN.getStatus()) {
                    throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.BUSINESS_ERROR, "发货单开启后不能关闭");
                }
                if (Objects.equals(switchEnum.getStatus(), config)) {
                    return 1;
                }
                result = bizConfApi.updateConfig(buildConfigArg(user, DeliveryNoteConstants.SWITCH_KEY, String.valueOf(switchEnum.getStatus())));
            }
            return result;
        } catch (Exception e) {
            log.warn("updateDeliveryNoteStatus error,user:{}, switchEnum:{}", user, switchEnum, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    private ConfigArg buildConfigArg(User user, String key, String value) {
        return ConfigArg.builder().key(key).operator(user.getUserId()).pkg(DeliveryNoteConstants.SWITCH_PKG).userId(user.getUserId()).tenantId(user.getTenantId()).valueType(ValueType.STRING).value(value).rank(Rank.TENANT).build();
    }
}