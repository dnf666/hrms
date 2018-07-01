package com.facishare.crm.erpstock.predefine.manager;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.crm.erpstock.enums.YesOrNoEnum;
import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.crm.erpstock.model.ErpCheckOrderModel;
import com.facishare.crm.erpstock.predefine.service.dto.ErpOrderCheckType;
import com.facishare.crm.erpstock.predefine.service.dto.ErpStockType;
import com.facishare.crm.erpstock.predefine.service.model.IsDeliveryNoteEnableModel;
import com.facishare.crm.erpstock.predefine.service.model.IsErpStockEnableModel;
import com.facishare.crm.erpstock.util.ConfigCenter;
import com.facishare.crm.erpstock.util.HttpUtil;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linchf
 * @date 2018/5/8
 */
@Service
@Slf4j(topic = "erpStockAccess")
public class ErpStockManager extends CommonManager {
    @Resource
    private ErpStockConfigManager erpStockConfigManager;

    /**
     * 判断纷享发货单是否开启
     * @param user
     * @return
     */
    public Boolean isDeliveryNoteEnable(User user) {
        String checkDeliveryNoteUrl = ConfigCenter.PAAS_FRAMEWORK_URL + "delivery_note/service/is_delivery_note_enable";
        try {
            Map<String, String> headers = HttpUtil.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
            headers.put("Content-Type", "application/json");
            IsDeliveryNoteEnableModel.ResultVO resultVO = HttpUtil.post(checkDeliveryNoteUrl, headers, null, IsDeliveryNoteEnableModel.ResultVO.class);
            if (resultVO != null && resultVO.getResult().getEnable()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断纷享库存是否开启
     * @param user
     * @return
     */
    public Boolean isStockEnable(User user) {
        String checkStockEnableUrl = ConfigCenter.PAAS_FRAMEWORK_URL + "stock/service/is_stock_enable";
//        String checkStockEnableUrl = "http://localhost:8080/API/v1/inner/object/stock/service/is_stock_enable";
        try {
            Map<String, String> headers = HttpUtil.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
            headers.put("Content-Type", "application/json");
            IsErpStockEnableModel.ResultVO resultVO = HttpUtil.post(checkStockEnableUrl, headers, null, IsErpStockEnableModel.ResultVO.class);
            if (resultVO != null && resultVO.getResult().getIsEnable()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断ERP库存是否开启
     *
     * @param tenantId 企业id
     * @return true:开启 false:未开启
     */
    public Boolean isErpStockEnable(String tenantId) {
        ErpStockType.ErpStockSwitchEnum erpStockSwitchEnum = erpStockConfigManager.getErpStockSwitch(tenantId);
        switch (erpStockSwitchEnum) {
            case ENABLE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    /**
     * ERP 库存是否展示库存为0的数据
     * @param tenantId
     * @return
     */
    public Boolean isNotShowZeroStock(String tenantId) {
        YesOrNoEnum yesOrNoEnum = erpStockConfigManager.getErpIsNotShowZeroStock(tenantId);
        switch (yesOrNoEnum) {
            case YES: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public IObjectData queryByProductIdAndWarehouseId(User user, String productId, String warehouseId, Boolean includeDeleted) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, ErpStockConstants.Field.ErpWarehouse.apiName, warehouseId);
        if (!includeDeleted) {
            SearchUtil.fillFilterEq(filters, SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        }

        SearchUtil.fillFilterEq(filters, ErpStockConstants.Field.Product.apiName, productId);
        QueryResult<IObjectData> queryResult = searchQuery(user, ErpStockConstants.API_NAME, filters, Lists.newArrayList(), 0, 10000, 0);
        List<IObjectData> resultList = queryResult.getData();
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        return resultList.get(0);
    }

    /**
     * 查询指定产品的库存信息
     *
     * @param user 用户
     * @param productIds 产品id列表
     * @return 返回库存信息
     */
    public List<IObjectData> queryStocksByProductIds(User user, List<String> productIds) {

        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        SearchUtil.fillFilterIn(filters, ErpStockConstants.Field.Product.apiName, productIds);
        QueryResult<IObjectData> queryResult = searchQuery(user, ErpStockConstants.API_NAME, filters, Lists.newArrayList(), 0, 10000, 0);
        return queryResult.getData();
    }

    /**
     * 校验可用库存
     * @param user
     * @param stocks
     * @param checkProducts
     */
    public void checkAvailableErpStockWithProducts(User user, List<IObjectData> stocks, List<ErpCheckOrderModel.CheckProduct> checkProducts) {
        Map<String, BigDecimal> stockProductsMap = sumAvailableErpStockNum(stocks);


        ErpOrderCheckType.OrderCheckTypeEnum orderCheckTypeEnum = erpStockConfigManager.getErpOrderCheckType(user);
        Boolean canSubmit = orderCheckTypeEnum.equals(ErpOrderCheckType.OrderCheckTypeEnum.CANSUBMIT);

        checkProducts.forEach(product -> {
            //校验仓库是否包含全部订单产品
            if (!stockProductsMap.containsKey(product.getProductId())) {
                log.warn("stock haven't the product[{}]. user[{}]",
                        product, user);
                throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "仓库没有" + product.getProductName() + "产品");
            }
            //若库存不足不允许提交订单，则校验可用库存是否大于产品数量
            if (!canSubmit) {
                Boolean isSatisfy = product.getProductNum().compareTo(stockProductsMap.get(product.getProductId())) <= 0;
                if (!isSatisfy) {
                    log.warn("the stock of product[{}] not enough. user[{}]",
                            product, user);
                    throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, product.getProductName() + "产品可用库存不足");
                }
            }
        });
    }

    /**
     * 合并产品可用库存数量
     * @param stocks
     * @return
     */
    public Map<String, BigDecimal> sumAvailableErpStockNum(List<IObjectData> stocks) {
        Map<String, BigDecimal> productStockNumMap = new HashMap<>();

        if (CollectionUtils.isEmpty(stocks)) {
            return productStockNumMap;
        }
        stocks.forEach(stock -> {
            String productId = stock.get(ErpStockConstants.Field.Product.apiName, String.class);
            BigDecimal productAvailableStockNum = new BigDecimal(stock.get(ErpStockConstants.Field.AvailableStock.apiName, String.class));

            if (productStockNumMap.get(productId) != null) {
                productStockNumMap.put(productId, productStockNumMap.get(productId).add(productAvailableStockNum));
            } else {
                productStockNumMap.put(productId, productAvailableStockNum);
            }
        });
        return productStockNumMap;
    }

    /**
     * 检查是否开启Erp库存
     * @param tenantId
     */
    public void checkErpStockEnable(String tenantId) {
        if (!isErpStockEnable(tenantId)) {
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "ERP库存已关闭，请联系CRM管理员处理");
        }
    }
}
