package com.facishare.crm.promotion.predefine.manager;

import com.facishare.crm.promotion.predefine.service.dto.PromotionType;
import com.facishare.paas.appframework.config.ConfigService;
import com.facishare.paas.appframework.config.ConfigValueType;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TenantConfigManager {
    @Autowired
    private ConfigService configService;

    private String promotionStatusKey = "promotion_status";
    final public static String STRING_VALUE_TYPE = "string";

    public PromotionType.PromotionSwitchEnum getPromotionStatus(String tenantId) {
        String config = configService.findTenantConfig(new User(tenantId, User.SUPPER_ADMIN_USER_ID), promotionStatusKey);
        if (config == null) {
            return PromotionType.PromotionSwitchEnum.NOT_OPEN;
        }
        return PromotionType.PromotionSwitchEnum.get(Integer.valueOf(config)).orElseThrow(() -> new ValidateException("PromotionStatus不合法"));
    }

    public int updatePromotionStatus(User user, PromotionType.PromotionSwitchEnum switchEnum) {
        String configValue = configService.findTenantConfig(user, promotionStatusKey);

        if (configValue == null) {
            configService.createTenantConfig(user, promotionStatusKey, switchEnum.status + "", ConfigValueType.STRING);
        } else {
            configService.updateTenantConfig(user, promotionStatusKey, switchEnum.status + "", ConfigValueType.STRING);
        }
        log.info("update promotion status ,user:{},status{}", user, switchEnum);
        return 0;
    }

}
