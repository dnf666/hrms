package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.predefine.service.dto.*;
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


@Service
@Slf4j(topic = "stockAccess")
public class StockConfigManager {
    @Resource
    private BizConfApi bizConfApi;

    @Resource
    private InitManager initManager;

    @Resource
    private StockWarningJobManager stockWarningJobManager;

    @Resource
    private SaleOrderManager saleOrderManager;

    private final static String ORDER_CHECK_KEY = "dht_order_check";

    private final static String STOCK_VIEW_KEY = "dht_stock_view";

    private final static String STOCK_SWITCH_KEY= "dht_stock_switch";

    private final static String STOCK_ORDER_WAREHOUSE_TYPE_KEY ="dht_stock_order_type";

    private final static String STOCK_WARNING_TYPE_KEY ="dht_stock_warning_type";

    private final static String STOCK_WARNING_JOB_ID_KEY ="dht_stock_warning_job_id";

    private final static String NOT_SHOW_ZERO_STOCK = "dht_not_show_zero_stock";

    private final String CONFIG_PKG_NAME = "CRM";

    private final static String STRING_VALUE_TYPE = "string";


    /**
     * 查询订单校验规则
     *
     * @param user 身份信息
     * @return OrderCheckType.OrderCheckTypeEnum
     */
    public OrderCheckType.OrderCheckTypeEnum getOrderCheckType(User user) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ORDER_CHECK_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return OrderCheckType.OrderCheckTypeEnum.CANNOTSUBMIT;
            }
            return OrderCheckType.OrderCheckTypeEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getOrderCheckTypeConfig error, user[{}]", user, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    /**
     * 更新订单校验规则
     *
     * @param user 身份信息
     * @param orderCheckTypeEnum OrderCheckType.OrderCheckTypeEnum
     * @return result
     */
    public void updateOrderCheckType(User user, OrderCheckType.OrderCheckTypeEnum orderCheckTypeEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(ORDER_CHECK_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());
            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, ORDER_CHECK_KEY, orderCheckTypeEnum.getStringStatus()));
                log.debug("updateOrderCheckType, user[{}], status[{}]", user, orderCheckTypeEnum.getStatus());
            } else {
                if (Objects.equals(orderCheckTypeEnum.getStringStatus(), config)) {
                    return;
                }
                bizConfApi.updateConfig(buildConfigArg(user, ORDER_CHECK_KEY, orderCheckTypeEnum.getStringStatus()));
            }
        } catch (Exception e) {
            log.warn("updateOrderCheckType error, user[{}], status[{}]", user, orderCheckTypeEnum.getStatus(), e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public void updateStockViewType(User user, StockViewType.StockViewTypeEnum stockViewTypeEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_VIEW_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, STOCK_VIEW_KEY, stockViewTypeEnum.getStringStatus()));
            } else {
                if (Objects.equals(stockViewTypeEnum.getStringStatus(), config)) {
                    return;
                }
                bizConfApi.updateConfig(buildConfigArg(user, STOCK_VIEW_KEY, stockViewTypeEnum.getStringStatus()));
            }
        } catch (Exception e) {
            log.warn("updateOrderCheckType error, user[{}], status[{}]", user, stockViewTypeEnum.getStatus(), e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public void updateOrderWarehouseType(User user, OrderWarehouseType.OrderWarehouseTypeEnum orderWarehouseTypeEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_ORDER_WAREHOUSE_TYPE_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());
            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, STOCK_ORDER_WAREHOUSE_TYPE_KEY, orderWarehouseTypeEnum.getStringStatus()));
            } else {
                if (Objects.equals(orderWarehouseTypeEnum.getStringStatus(), config)) {
                    return;
                }
                bizConfApi.updateConfig(buildConfigArg(user, STOCK_ORDER_WAREHOUSE_TYPE_KEY, orderWarehouseTypeEnum.getStringStatus()));
            }

            if ((config == null || Objects.equals(config, OrderWarehouseType.OrderWarehouseTypeEnum.SINGLE_WAREHOUSE.getStringStatus()))
                    && Objects.equals(orderWarehouseTypeEnum, OrderWarehouseType.OrderWarehouseTypeEnum.ALL_WAREHOUSE)) {
                saleOrderManager.hideSalesOrderWarehouse(user);
            }

            if (Objects.equals(config, OrderWarehouseType.OrderWarehouseTypeEnum.ALL_WAREHOUSE.getStringStatus()) && Objects.equals(orderWarehouseTypeEnum, OrderWarehouseType.OrderWarehouseTypeEnum.SINGLE_WAREHOUSE)) {
                saleOrderManager.showSalesOrderWarehouse(user);
            }
        } catch (Exception e) {
            log.warn("updateOrderWarehouseType error, user[{}], status[{}]", user, orderWarehouseTypeEnum.getStringStatus(), e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public void updateStockWarningType(User user, StockWarningType.StockWarningTypeEnum stockWarningTypeEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_WARNING_TYPE_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            String oldConfigType = StockWarningType.StockWarningTypeEnum.UNABLE.getStringStatus();
            if (StringUtils.isBlank(config)) {
                 bizConfApi.createConfig(buildConfigArg(user, STOCK_WARNING_TYPE_KEY, stockWarningTypeEnum.getStringStatus()));
            } else {
                oldConfigType = config;
                if (!Objects.equals(stockWarningTypeEnum.getStringStatus(), config)) {
                    bizConfApi.updateConfig(buildConfigArg(user, STOCK_WARNING_TYPE_KEY, stockWarningTypeEnum.getStringStatus()));
                }
            }
            if ((StringUtils.isBlank(config) || Objects.equals(oldConfigType, StockWarningType.StockWarningTypeEnum.UNABLE.getStringStatus()))
                    && Objects.equals(stockWarningTypeEnum, StockWarningType.StockWarningTypeEnum.ENABLE)) {
                int jobId = stockWarningJobManager.addJob(user.getTenantId());
                if (jobId != -1) {
                    updateStockWarningJobId(user, String.valueOf(jobId));
                }

            } else if (Objects.equals(config, StockWarningType.StockWarningTypeEnum.ENABLE.getStringStatus())
                    && Objects.equals(stockWarningTypeEnum, StockWarningType.StockWarningTypeEnum.UNABLE)) {
                int jobId = getStockWarningJobId(user.getTenantId());
                if (jobId != -1) {
                    stockWarningJobManager.deleteJob(jobId);
                }
            }
        } catch (Exception e) {
            log.warn("updateStockWarningType error, user[{}], status[{}]", user, stockWarningTypeEnum.getStringStatus(), e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public void updateStockWarningJobId(User user, String id) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_WARNING_JOB_ID_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, STOCK_WARNING_JOB_ID_KEY, id));
                log.info("insertStockWarningJobId, user[{}], id[{}]", user, id);
            } else {
                bizConfApi.updateConfig(buildConfigArg(user, STOCK_WARNING_JOB_ID_KEY, id));
            }
        } catch (Exception e) {
            log.info("updateStockWarningJobId error, user[{}], id[{}]", user, id, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }



    public void insertOrUpdateStockSwitch(User user, StockType.StockSwitchEnum stockSwitchEnum, boolean isCheck) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_SWITCH_KEY).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, STOCK_SWITCH_KEY, stockSwitchEnum.getStringStatus()));
                log.info("insertStockSwitch, user[{}], status[{}]", user, stockSwitchEnum.getStatus());
            } else {
                if (isCheck) {
                    if (stockSwitchEnum.getStatus() == StockType.StockSwitchEnum.UNABLE.getStatus()) {
                        throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存开启后不能关闭");
                    }
                }
                if (Objects.equals(stockSwitchEnum.getStringStatus(), config)) {
                    return;
                }
                bizConfApi.updateConfig(buildConfigArg(user, STOCK_SWITCH_KEY, stockSwitchEnum.getStringStatus()));
            }
        } catch (Exception e) {
            log.info("insertOrUpdateStockSwitch error, user[{}], status[{}]", user, stockSwitchEnum.getStatus(), e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    private ConfigArg buildConfigArg(User user, String key, String value) {
        return ConfigArg.builder().key(key).operator(user.getUserId()).pkg(CONFIG_PKG_NAME).userId(user.getUserId()).tenantId(user.getTenantId()).valueType(ValueType.STRING).value(value).rank(Rank.TENANT).build();
    }

    /**
     * 查询库存显示规则
     *
     * @param tenantId 身份信息
     * @return StockViewType.StockViewTypeEnum
     */
    public StockViewType.StockViewTypeEnum getStockViewType(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_VIEW_KEY).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return StockViewType.StockViewTypeEnum.NO;
            }
            return StockViewType.StockViewTypeEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getStockViewTypeConfig error, tenantId[{}]", tenantId, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }


    public StockType.StockSwitchEnum getStockSwitch(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_SWITCH_KEY).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return StockType.StockSwitchEnum.UNABLE;
            }
            return StockType.StockSwitchEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getStockSwitchConfig error, tenantId[{}]", tenantId, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public OrderWarehouseType.OrderWarehouseTypeEnum getOrderWarehouseType(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_ORDER_WAREHOUSE_TYPE_KEY).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return OrderWarehouseType.OrderWarehouseTypeEnum.SINGLE_WAREHOUSE;
            }
            return OrderWarehouseType.OrderWarehouseTypeEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getOrderTypeConfig error,tenantId[{}]", tenantId, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public StockWarningType.StockWarningTypeEnum getStockWarningType(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_WARNING_TYPE_KEY).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return StockWarningType.StockWarningTypeEnum.UNABLE;
            }
            return StockWarningType.StockWarningTypeEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getStockWarningType error,tenantId[{}]", tenantId, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public int getStockWarningJobId(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(STOCK_WARNING_JOB_ID_KEY).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return -1;
            }
            return Integer.valueOf(config);
        } catch (Exception e) {
            log.warn("getStockWarningJobId error,tenantId[{}]", tenantId, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public YesOrNoEnum getIsNotShowZeroStock(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(NOT_SHOW_ZERO_STOCK).tenantId(tenantId).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                return YesOrNoEnum.NO;
            }
            return YesOrNoEnum.valueOf(Integer.valueOf(config));
        } catch (Exception e) {
            log.warn("getIsNotShowZeroStock error,tenantId[{}]", tenantId, e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }

    public void updateIsNotShowZeroStock(User user, YesOrNoEnum yesOrNoEnum) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key(NOT_SHOW_ZERO_STOCK).tenantId(user.getTenantId()).pkg(CONFIG_PKG_NAME).build());

            if (StringUtils.isBlank(config)) {
                bizConfApi.createConfig(buildConfigArg(user, NOT_SHOW_ZERO_STOCK, yesOrNoEnum.getStringStatus()));
                log.info("insertIsNotShowZeroStock, user[{}], isNotShowZeroStock[{}]", user, yesOrNoEnum.getStringStatus());
            } else {
                bizConfApi.updateConfig(buildConfigArg(user, NOT_SHOW_ZERO_STOCK, yesOrNoEnum.getStringStatus()));
            }
        } catch (Exception e) {
            log.info("updateIsNotShowZeroStock error, user[{}], isNotShowZeroStock[{}]", user, yesOrNoEnum.getStringStatus(), e);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, e.getMessage());
        }
    }



}
