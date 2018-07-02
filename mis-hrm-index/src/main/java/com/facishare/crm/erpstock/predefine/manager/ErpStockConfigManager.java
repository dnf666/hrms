package com.facishare.crm.erpstock.predefine.manager;

import com.facishare.crm.erpstock.enums.YesOrNoEnum;
import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.crm.erpstock.predefine.service.dto.ErpOrderCheckType;
import com.facishare.crm.erpstock.predefine.service.dto.ErpStockType;
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
import java.util.Date;
import java.util.Objects;

/**
 * @author linchf
 * @date 2018/5/8
 */
@Service
@Slf4j(topic = "erpStockAccess")
public class ErpStockConfigManager {
    @Resource
    private BizConfApi bizConfApi;

    private final static String ERP_STOCK_SWITCH_KEY= "erp_stock_switch";

    private final static String ERP_NOT_SHOW_ZERO_STOCK = "erp_not_show_zero_stock";

    private final static String ERP_ORDER_CHECK_KEY = "erp_order_check";

    private final String CONFIG_PKG_NAME = "CRM";

    private final static String STRING_VALUE_TYPE = "string";

    /**
     * 获取Erp开关
     * @param tenantId
     * @return
     */
    public ErpStockType.ErpStockSwitchEnum getErpStockSwitch(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ERP_STOCK_SWITCH_KEY).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return ErpStockType.ErpStockSwitchEnum.UNABLE;
            }
            return ErpStockType.ErpStockSwitchEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getErpStockSwitch error, tenantId[{}]", tenantId, e);
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 更新Erp开关
     * @param user
     * @param erpStockSwitchEnum
     */
    public void insertOrUpdateErpStockSwitch(User user, ErpStockType.ErpStockSwitchEnum erpStockSwitchEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ERP_STOCK_SWITCH_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, ERP_STOCK_SWITCH_KEY, erpStockSwitchEnum.getStringStatus()));
                log.info("insertErpStockSwitch, user[{}], status[{}]", user, erpStockSwitchEnum.getStatus());
            } else {
                if (Objects.equals(erpStockSwitchEnum.getStringStatus(), config)) {
                    return;
                }
                bizConfApi.updateConfig(buildConfigArg(user, ERP_STOCK_SWITCH_KEY, erpStockSwitchEnum.getStringStatus()));
            }
        } catch (Exception e) {
            log.info("insertOrUpdateErpStockSwitch error, user[{}], status[{}]", user, erpStockSwitchEnum.getStatus(), e);
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 获取 是否展示ERP库存为0 配置
     * @param tenantId
     * @return
     */
    public YesOrNoEnum getErpIsNotShowZeroStock(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ERP_NOT_SHOW_ZERO_STOCK).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return YesOrNoEnum.NO;
            }
            return YesOrNoEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getErpIsNotShowZeroStock error,tenantId[{}]", tenantId, e);
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 更新 是否展示ERP库存为0 配置
     * @param user
     * @param yesOrNoEnum
     */
    public void updateErpIsNotShowZeroStock(User user, YesOrNoEnum yesOrNoEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ERP_NOT_SHOW_ZERO_STOCK).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, ERP_NOT_SHOW_ZERO_STOCK, yesOrNoEnum.getStringStatus()));
                log.info("insertErpIsNotShowZeroStock, user[{}], isNotShowZeroStock[{}]", user, yesOrNoEnum.getStringStatus());
            } else {
                bizConfApi.updateConfig(buildConfigArg(user, ERP_NOT_SHOW_ZERO_STOCK, yesOrNoEnum.getStringStatus()));
            }
        } catch (Exception e) {
            log.info("updateErpIsNotShowZeroStock error, user[{}], isNotShowZeroStock[{}]", user, yesOrNoEnum.getStringStatus(), e);
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 查询ERP库存 订单校验规则 配置
     *
     * @param user 身份信息
     * @return ErpOrderCheckType.OrderCheckTypeEnum
     */
    public ErpOrderCheckType.OrderCheckTypeEnum getErpOrderCheckType(User user) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ERP_ORDER_CHECK_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return ErpOrderCheckType.OrderCheckTypeEnum.CANNOTSUBMIT;
            }
            return ErpOrderCheckType.OrderCheckTypeEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getErpOrderCheckType error, user[{}]", user, e);
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, e.getMessage());
        }

    }

    /**
     * 更新ERP库存 订单校验规则 配置
     *
     * @param user 身份信息
     * @param orderCheckTypeEnum ErpOrderCheckType.OrderCheckTypeEnum
     * @return result
     */
    public void updateErpOrderCheckType(User user, ErpOrderCheckType.OrderCheckTypeEnum orderCheckTypeEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ERP_ORDER_CHECK_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());
            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, ERP_ORDER_CHECK_KEY, orderCheckTypeEnum.getStringStatus()));
                log.debug("updateErpOrderCheckType, user[{}], status[{}]", user, orderCheckTypeEnum.getStatus());
            } else {
                if (Objects.equals(orderCheckTypeEnum.getStringStatus(), config)) {
                    return;
                }
                bizConfApi.updateConfig(buildConfigArg(user, ERP_ORDER_CHECK_KEY, orderCheckTypeEnum.getStringStatus()));
            }
        } catch (Exception e) {
            log.warn("updateErpOrderCheckType error, user[{}], status[{}]", user, orderCheckTypeEnum.getStatus(), e);
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }


    private ConfigArg buildConfigArg(User user, String key, String value) {
        return ConfigArg.builder().key(key).operator(user.getUserId()).pkg(CONFIG_PKG_NAME).userId(user.getUserId()).tenantId(user.getTenantId()).valueType(ValueType.STRING).value(value).rank(Rank.TENANT).build();
    }
}
