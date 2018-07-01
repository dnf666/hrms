package com.facishare.crm.sfainterceptor.predefine.manager;

import com.facishare.crm.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.manager.DeliveryNoteObjManager;
import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import com.facishare.crm.sfainterceptor.predefine.service.model.salesOrder.edit.SalesOrderEditBeforeModel;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.model.StockVO;
import com.facishare.crm.stock.predefine.manager.*;
import com.facishare.crm.stock.predefine.service.model.CheckOrderModel;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/22
 */
@Service
@Slf4j(topic = "sfainterceptorAccess")
public class SalesOrderInterceptorStockManager extends CommonStockManager {

    @Resource
    private StockManager stockManager;

    @Resource
    private SaleOrderManager saleOrderManager;

    @Resource
    private ProductManager productManager;

    @Resource
    private WareHouseManager wareHouseManager;

    @Resource
    private StockCalculateManager stockCalculateManager;

    @Resource
    private StockLogManager stockLogManager;

    @Resource
    private DeliveryNoteObjManager deliveryNoteObjManager;

    /**
     * 所有订单关联的发货单是否为未生效或已作废
     * @param user
     * @param salesOrderId
     * @return
     */
    public Boolean isAllDeliveryNoteInvalid(User user, String salesOrderId) {
        List<String> invalidStatus = Arrays.asList(DeliveryNoteObjStatusEnum.INVALID.getStatus(), DeliveryNoteObjStatusEnum.UN_DELIVERY.getStatus());
        return deliveryNoteObjManager.isAllDeliveryNoteUnderStatus(user, salesOrderId, invalidStatus);
    }

    /**
     * 订单产品数量、种类、销售单价是否修改
     * @param newProductVOs 新产品列表
     * @param oldProductVOs 老产品列表
     * @return
     */
    public boolean isProductsModified(List<SalesOrderProductVo> newProductVOs, List<SalesOrderModel.SalesOrderProductVO> oldProductVOs) {
        if (!(CollectionUtils.isEmpty(newProductVOs) == CollectionUtils.isEmpty(oldProductVOs))) {
            return true;
        }

        if (!CollectionUtils.isEmpty(newProductVOs) && !CollectionUtils.isEmpty(oldProductVOs)) {
            //合并且校验数量
            List<SalesOrderProductVo> sumNewProductVOs = sumProductAmount(newProductVOs);
            List<SalesOrderModel.SalesOrderProductVO> sumOldProductVOs = sumSalesOrderProductAmount(oldProductVOs);
            if (sumNewProductVOs.size() != sumOldProductVOs.size()) {
                return true;
            }

            Map<String, BigDecimal> productAmountMap = sumOldProductVOs.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderProductVO::getProductId, SalesOrderModel.SalesOrderProductVO::getAmount));

            boolean checkProductResult = sumNewProductVOs.stream().allMatch(sumNewProductVO ->
                    productAmountMap.get(sumNewProductVO.getProductId()) != null && sumNewProductVO.getAmount().compareTo(productAmountMap.get(sumNewProductVO.getProductId())) == 0);
            if (!checkProductResult) {
                return true;
            }

            //校验销售单价与平均销售单价  销售单价由 产品id与业务类型确定唯一值  平均销售单价由销售单价和产品数量确定
            //Map<产品id, 产品列表>
            Map<String, List<SalesOrderProductVo>> newProductMap = newProductVOs.stream().collect(Collectors.groupingBy(SalesOrderProductVo::getProductId));
            Map<String, List<SalesOrderModel.SalesOrderProductVO>> oldProductMap = oldProductVOs.stream().collect(Collectors.groupingBy(SalesOrderModel.SalesOrderProductVO::getProductId));

            //Map<产品id, Map<业务类型, 销售单价>>
            Map<String, Map<String, BigDecimal>> newProductPriceMap = Maps.newHashMap();
            Map<String, Map<String, BigDecimal>> oldProductPriceMap = Maps.newHashMap();

            //Map<产品id, Map<业务类型, 销售单价>>
            Map<String, Map<String, BigDecimal>> newProductAmountMap = Maps.newHashMap();
            Map<String, Map<String, BigDecimal>> oldProductAmountMap = Maps.newHashMap();


            newProductMap.keySet().forEach(productId -> {
                //销售单价
                Map<String, BigDecimal> recordTypePriceMap = Maps.newHashMap();
                newProductMap.get(productId).forEach(productVO ->
                    recordTypePriceMap.put(productVO.getRecordType(), productVO.getPrice()));
                newProductPriceMap.put(productId, recordTypePriceMap);

                //数量
                Map<String, BigDecimal> recordTypeAmountMap = Maps.newHashMap();
                //Map<业务类型, 产品列表>
                Map<String, List<SalesOrderProductVo>> recordTypeProductMap = newProductMap.get(productId).stream().collect(Collectors.groupingBy(SalesOrderProductVo::getRecordType));
                //同种业务类型的产品数量求和
                recordTypeProductMap.keySet().forEach(recordType ->
                    recordTypeAmountMap.put(recordType, recordTypeProductMap.get(recordType).stream().map(SalesOrderProductVo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)));
                newProductAmountMap.put(productId, recordTypeAmountMap);
            });

            oldProductMap.keySet().forEach(productId -> {
                //销售单价
                Map<String, BigDecimal> recordTypePriceMap = Maps.newHashMap();
                oldProductMap.get(productId).forEach(productVO ->
                        recordTypePriceMap.put(productVO.getRecordType(), productVO.getPrice()));
                oldProductPriceMap.put(productId, recordTypePriceMap);

                //数量
                Map<String, BigDecimal> recordTypeAmountMap = Maps.newHashMap();
                //Map<业务类型, 产品列表>
                Map<String, List<SalesOrderModel.SalesOrderProductVO>> recordTypeProductMap = oldProductMap.get(productId).stream().collect(Collectors.groupingBy(SalesOrderModel.SalesOrderProductVO::getRecordType));
                //同种业务类型的产品数量求和
                recordTypeProductMap.keySet().forEach(recordType ->
                        recordTypeAmountMap.put(recordType, recordTypeProductMap.get(recordType).stream().map(SalesOrderModel.SalesOrderProductVO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)));
                oldProductAmountMap.put(productId, recordTypeAmountMap);
            });

            try {
                oldProductPriceMap.keySet().forEach(productId -> {
                    Map<String, BigDecimal> newProductRecordTypePriceMap = newProductPriceMap.get(productId);
                    Map<String, BigDecimal> oldProductRecordTypePriceMap = oldProductPriceMap.get(productId);
                    //产品+业务类型 对应的销售单价不能修改
                    if (oldProductRecordTypePriceMap.keySet().stream().anyMatch(recordType ->
                            oldProductRecordTypePriceMap.get(recordType) == null || newProductRecordTypePriceMap.get(recordType) == null || oldProductRecordTypePriceMap.get(recordType).compareTo(newProductRecordTypePriceMap.get(recordType)) != 0)) {
                        throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "销售单价不能修改");
                    }

                    //产品+业务类型 对应的数量不能修改 （修改会导致平均销售单价计算问题）
                    Map<String, BigDecimal> newProductRecordTypeAmountMap = newProductAmountMap.get(productId);
                    Map<String, BigDecimal> oldProductRecordTypeAmountMap = oldProductAmountMap.get(productId);

                    if (oldProductRecordTypeAmountMap.keySet().stream().anyMatch(recordType ->
                            oldProductRecordTypeAmountMap.get(recordType) == null || newProductRecordTypeAmountMap.get(recordType) == null || oldProductRecordTypeAmountMap.get(recordType).compareTo(newProductRecordTypeAmountMap.get(recordType)) != 0)) {
                        throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "订单产品数量不能修改");
                    }

                });
            } catch (StockBusinessException e) {
                return true;
            } catch (Exception ex) {
                throw ex;
            }

        }
        return false;
    }

    /**
     * 比对冻结库存和订单产品 筛选变化的产品数量
     * @param user
     * @param salesOrderId
     * @return
     */
    public Map<String, List<SalesOrderProductVo>> getModifiedProductsVosByBlockedStock(User user, String salesOrderId) {
        Map<String, List<SalesOrderProductVo>> result = new HashMap<>();

        List<SalesOrderProductVo> addProducts = Lists.newArrayList();
        List<SalesOrderProductVo> minusProducts = Lists.newArrayList();
        //已冻结的库存
        List<StockVO> blockedStocks = stockLogManager.calculateStockVOsBySalesOrderId(user.getTenantId(), salesOrderId);
        Map<String, BigDecimal> blockedStockAmountMap = blockedStocks.stream().collect(Collectors.toMap(StockVO::getProductId, StockVO::getBlockedStock));

        //订单现有数据
        List<SalesOrderModel.SalesOrderProductVO> salesOrderProductVOs = productManager.getProductsByOrderId(user, salesOrderId, true);
        Map<String, BigDecimal> productAmountMap = salesOrderProductVOs.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderProductVO::getProductId, SalesOrderModel.SalesOrderProductVO::getAmount));
        Map<String, String> productNameMap = salesOrderProductVOs.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderProductVO::getProductId, SalesOrderModel.SalesOrderProductVO::getProductName));

        //获取需要增加冻结库存的产品列表
        productAmountMap.keySet().forEach(productId -> {
            SalesOrderProductVo productVo = null;
            if (blockedStockAmountMap.get(productId) == null && productAmountMap.get(productId).compareTo(BigDecimal.ZERO) > 0) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(productAmountMap.get(productId));
                productVo.setProductId(productId);
                productVo.setProductName(productNameMap.get(productId));
            } else if (productAmountMap.get(productId).compareTo(blockedStockAmountMap.get(productId)) > 0) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(productAmountMap.get(productId).subtract(blockedStockAmountMap.get(productId)));
                productVo.setProductId(productId);
                productVo.setProductName(productNameMap.get(productId));
            }
            if (productVo != null) {
                addProducts.add(productVo);
            }
        });

        //获取需要扣减冻结库存的产品列表
        blockedStocks.forEach(blockedStock -> {
            SalesOrderProductVo productVo = null;
            if (productAmountMap.get(blockedStock.getProductId()) == null) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(blockedStock.getBlockedStock());
                productVo.setProductId(blockedStock.getProductId());
            } else if (blockedStock.getBlockedStock().compareTo(productAmountMap.get(blockedStock.getProductId())) > 0) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(blockedStock.getBlockedStock().subtract(productAmountMap.get(blockedStock.getProductId())));
                productVo.setProductId(blockedStock.getProductId());
            }
            if (productVo != null) {
                minusProducts.add(productVo);
            }
        });
        result.put("addProducts", addProducts);
        result.put("minusProducts", minusProducts);
        return result;
    }

    /**
     * 比对新旧产品列表 筛选变化的产品数量
     * @param user
     * @param newSalesOrderProductVos
     * @param oldSalesOrderProductVOs
     * @return
     */
    public Map<String, List<SalesOrderProductVo>> getModifiedProductsVos(User user, List<SalesOrderProductVo> newSalesOrderProductVos, List<SalesOrderModel.SalesOrderProductVO> oldSalesOrderProductVOs, Boolean isNeedSumOldProducts) {
        Map<String, List<SalesOrderProductVo>> result = new HashMap<>();

        List<SalesOrderProductVo> addProducts = Lists.newArrayList();
        List<SalesOrderProductVo> minusProducts = Lists.newArrayList();

        if (CollectionUtils.isEmpty(newSalesOrderProductVos)) {
            return result;
        }

        //累加数量
        List<SalesOrderProductVo> newSumProductVos = sumProductAmount(newSalesOrderProductVos);
        Map<String, BigDecimal> newProductAmountMap = newSumProductVos.stream().collect(Collectors.toMap(SalesOrderProductVo::getProductId, SalesOrderProductVo::getAmount));
        Map<String, String> newProductNameMap = newSumProductVos.stream().collect(Collectors.toMap(SalesOrderProductVo::getProductId, SalesOrderProductVo::getProductName));

        List<SalesOrderModel.SalesOrderProductVO> oldSumProductVos = oldSalesOrderProductVOs;
        if (isNeedSumOldProducts) {
            oldSumProductVos = sumSalesOrderProductAmount(oldSalesOrderProductVOs);
        }
        Map<String, BigDecimal> oldProductAmountMap = oldSumProductVos.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderProductVO::getProductId, SalesOrderModel.SalesOrderProductVO::getAmount));
        Map<String, String> oldProductNameMap = oldSumProductVos.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderProductVO::getProductId, SalesOrderModel.SalesOrderProductVO::getProductName));

        //获取增加冻结库存的产品
        newProductAmountMap.keySet().forEach(productId -> {
            SalesOrderProductVo productVo = null;
            if (oldProductAmountMap.get(productId) == null && newProductAmountMap.get(productId).compareTo(BigDecimal.ZERO) > 0) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(newProductAmountMap.get(productId));
                productVo.setProductId(productId);
                productVo.setProductName(newProductNameMap.get(productId));
            } else if (newProductAmountMap.get(productId).compareTo(oldProductAmountMap.get(productId)) > 0) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(newProductAmountMap.get(productId).subtract(oldProductAmountMap.get(productId)));
                productVo.setProductId(productId);
                productVo.setProductName(newProductNameMap.get(productId));
            }
            if (productVo != null) {
                addProducts.add(productVo);
            }
        });

        //获取减少冻结库存的产品
        oldProductAmountMap.keySet().forEach(productId -> {
            SalesOrderProductVo productVo = null;
            if (newProductAmountMap.get(productId) == null && oldProductAmountMap.get(productId).compareTo(BigDecimal.ZERO) > 0) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(oldProductAmountMap.get(productId));
                productVo.setProductId(productId);
                productVo.setProductName(oldProductNameMap.get(productId));
            } else if (oldProductAmountMap.get(productId).compareTo(newProductAmountMap.get(productId)) > 0) {
                productVo = new SalesOrderProductVo();
                productVo.setAmount(oldProductAmountMap.get(productId).subtract(newProductAmountMap.get(productId)));
                productVo.setProductId(productId);
                productVo.setProductName(oldProductNameMap.get(productId));
            }
            if (productVo != null) {
                minusProducts.add(productVo);
            }
        });

        result.put("addProducts", addProducts);
        result.put("minusProducts", minusProducts);
        return result;
    }


    /**
     * 校验订单和可用库存
     *
     * @param user                 操作者
     * @param salesOrderProductVos 订单产品列表
     * @param warehouseId          订货仓库id
     * @param customerId           客户id
     * @param isNeedCheckSatisfied 是否需要校验仓库适用  未生效需要校验
     */
    public String checkOrderAndAvailableStock(User user, List<SalesOrderProductVo> salesOrderProductVos, String warehouseId, String customerId, boolean isNeedCheckSatisfied) {
        boolean isAllWarehouseOrder = stockManager.isAllWarehouseOrder(user.getTenantId());
        List<String> validWarehouseIds;
        String defaultWarehouseId = warehouseId;

        if (StringUtils.isBlank(customerId)) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "客户不能为空");
        }

        Boolean isCustomerSatisfied = true;
        //校验仓库适用
        if (isNeedCheckSatisfied) {
            if (StringUtils.isNotBlank(warehouseId)) {
                //校验仓库和客户是否适用
                try {
                    wareHouseManager.checkCustomerSatisfied(user, customerId, warehouseId);
                } catch (StockBusinessException e) {
                    if (isAllWarehouseOrder && Objects.equals(e.getErrorCode(), StockErrorCode.WAREHOUSE_UN_SATISFIED.getCode())) {
                        isCustomerSatisfied = false;
                    } else {
                        throw e;
                    }
                }
            }
        }

        //合并仓库订货仓库选择
        if (isAllWarehouseOrder) {
            List<IObjectData> warehouses = wareHouseManager.queryValidByAccountId(user, customerId, null);
            validWarehouseIds = warehouses.stream().map(warehouse -> warehouse.getId()).collect(Collectors.toList());

            if (StringUtils.isNotBlank(defaultWarehouseId) && !validWarehouseIds.contains(defaultWarehouseId)) {
                validWarehouseIds.add(defaultWarehouseId);
            }

            if (CollectionUtils.isEmpty(validWarehouseIds)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "客户没有适用的仓库");
            }

            if (StringUtils.isBlank(defaultWarehouseId) || !isCustomerSatisfied) {
                Optional<IObjectData> defaultWarehouse = warehouses.stream().filter(warehouse -> Objects.equals(warehouse.get(WarehouseConstants.Field.Is_Default.apiName).toString(), String.valueOf(true))).findFirst();
                if (defaultWarehouse.isPresent()) {
                    defaultWarehouseId = defaultWarehouse.get().getId();
                } else {
                    defaultWarehouseId = warehouses.get(0).getId();
                }
            }
        } else {
            validWarehouseIds = Arrays.asList(warehouseId);
        }

        if (!CollectionUtils.isEmpty(salesOrderProductVos)) {
            //过滤重复订单产品 累加数量
            salesOrderProductVos = sumProductAmount(salesOrderProductVos);

            List<String> productIds = salesOrderProductVos.stream().map(SalesOrderProductVo::getProductId).distinct().collect(Collectors.toList());
            List<QueryProductByIds.ProductVO> productVOs = productManager.queryProductByIds(user, productIds);
            Map<String, String> productNameMap = productVOs.stream().collect(Collectors.toMap(QueryProductByIds.ProductVO::getId, QueryProductByIds.ProductVO::getProductName));

            List<CheckOrderModel.CheckProduct> products =
                    salesOrderProductVos.stream().map(product -> new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), productNameMap.get(product.getProductId())))
                            .collect(Collectors.toList());
            //校验可用库存
            checkAvailableStock(user, validWarehouseIds, products, isAllWarehouseOrder);
        }

        return defaultWarehouseId;
    }

    /**
     * 校验订单并增加冻结库存
     *
     * @param user   操作者
     * @param dataId 订单id
     * @param info 库存操作信息
     * @return
     */
    public void checkOrderAndAddBlockedStock(User user, String dataId, StockOperateInfo info) {
        //校验订单增加冻结库存
        SalesOrderModel.SalesOrderVo salesOrderVo = saleOrderManager.getById(user, dataId);
        String lockWarehouseId = salesOrderVo.getWarehouseId();

        if (StringUtils.isBlank(lockWarehouseId)) {
            log.info("checkOrderLockStock quit. warehouseId is null.");
            return;
        }

        List<SalesOrderModel.SalesOrderProductVO> productVOs = productManager.getProductsByOrderId(user, dataId, true);
        if (CollectionUtils.isEmpty(productVOs)) {
            log.info("checkOrderLockStock quit. products is empty.");
            return;
        }
        //合并仓库订货选择增加冻结库存的仓库
        boolean isAllWarehouseOrder = stockManager.isAllWarehouseOrder(user.getTenantId());
        List<String> validWarehouseIds = Arrays.asList(salesOrderVo.getWarehouseId());
        if (isAllWarehouseOrder) {
            List<IObjectData> warehouses = wareHouseManager.queryValidByAccountId(user, salesOrderVo.getCustomerId(), null);
            validWarehouseIds = warehouses.stream().map(warehouse -> warehouse.getId()).collect(Collectors.toList());
            if (StringUtils.isNotBlank(lockWarehouseId) && !validWarehouseIds.contains(lockWarehouseId)) {
                validWarehouseIds.add(lockWarehouseId);
            }
            if (CollectionUtils.isEmpty(validWarehouseIds)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "客户没有适用的仓库");
            }
        }

        List<CheckOrderModel.CheckProduct> products = productVOs.stream().map(product -> new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), product.getProductName()))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(products)) {
            //校验可用库存
            CheckOrderModel.CheckProductStock queryStock = checkAvailableStock(user, validWarehouseIds, products, isAllWarehouseOrder);
            //增加冻结库存
            addBlockedStock(user, queryStock, isAllWarehouseOrder, lockWarehouseId, info);
        }
    }

    /**
     * 校验订单并增加冻结库存
     *
     * @param user   操作者
     * @param salesOrderVo 订单
     * @param addProductVos 增加的产品
     * @param info 库存操作信息
     * @return
     */
    public void checkOrderAndAddBlockedStockByAddProducts(User user, SalesOrderModel.SalesOrderVo salesOrderVo, List<SalesOrderProductVo> addProductVos, StockOperateInfo info) {
        String lockWarehouseId = salesOrderVo.getWarehouseId();

        if (StringUtils.isBlank(lockWarehouseId)) {
            log.info("checkOrderAndAddBlockedStockByAddProducts quit. warehouseId is null.");
            return;
        }

        //合并仓库订货选择增加冻结库存的仓库
        boolean isAllWarehouseOrder = stockManager.isAllWarehouseOrder(user.getTenantId());
        List<String> validWarehouseIds = Arrays.asList(salesOrderVo.getWarehouseId());
        if (isAllWarehouseOrder) {
            List<IObjectData> warehouses = wareHouseManager.queryValidByAccountId(user, salesOrderVo.getCustomerId(), null);
            validWarehouseIds = warehouses.stream().map(warehouse -> warehouse.getId()).collect(Collectors.toList());
            if (StringUtils.isNotBlank(lockWarehouseId) && !validWarehouseIds.contains(lockWarehouseId)) {
                validWarehouseIds.add(lockWarehouseId);
            }

            if (CollectionUtils.isEmpty(validWarehouseIds)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "客户没有适用的仓库");
            }
        }

        List<CheckOrderModel.CheckProduct> products = addProductVos.stream().map(product -> new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), product.getProductName()))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(products)) {
            //校验可用库存
            CheckOrderModel.CheckProductStock queryStock = checkAvailableStock(user, validWarehouseIds, products, isAllWarehouseOrder);
            //增加冻结库存
            addBlockedStock(user, queryStock, isAllWarehouseOrder, lockWarehouseId, info);
        }
    }

    /**
     * 根据订单id 校验订单和可用库存
     * @param user 操作者
     * @param dataId 订单id
     */
    public void checkOrderAndAvailableStockByDataId(User user, String dataId) {
        SalesOrderModel.SalesOrderVo salesOrderVo = saleOrderManager.getById(user, dataId);
        if (StringUtils.isBlank(salesOrderVo.getWarehouseId())) {
            log.info("checkOrderByDataId quit. warehouseId is null.");
            return;
        }

        List<SalesOrderModel.SalesOrderProductVO> productVOs = productManager.getProductsByOrderId(user, dataId, true);
        if (CollectionUtils.isEmpty(productVOs)) {
            log.info("checkOrderByDataId quit. products is empty.");
            return;
        }

        List<String> validWarehouseIds = Arrays.asList(salesOrderVo.getWarehouseId());
        List<CheckOrderModel.CheckProduct> products =
                productVOs.stream().map(product -> new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), product.getProductName()))
                        .collect(Collectors.toList());
        boolean isAllWarehouseOrder = stockManager.isAllWarehouseOrder(user.getTenantId());
        if (isAllWarehouseOrder) {
            validWarehouseIds = wareHouseManager.queryValidByAccountId(user, salesOrderVo.getCustomerId(), null).stream().map(warehouse -> warehouse.getId()).collect(Collectors.toList());
        }

        if (!CollectionUtils.isEmpty(products)) {
            //校验可用库存
            checkAvailableStock(user, validWarehouseIds, products, isAllWarehouseOrder);
        }
    }

    /**
     * 批量校验订单和可用库存
     * @param user 操作者
     * @param dataIds 订单ids
     */
    public void checkOrderAvailableStockByDataIds(User user, List<String> dataIds) {
        List<SalesOrderModel.SalesOrderVo> salesOrderVos = saleOrderManager.getByIds(user, dataIds);
        //过滤仓库为空的数据
        salesOrderVos = salesOrderVos.stream().filter(salesOrderVo -> StringUtils.isNotBlank(salesOrderVo.getWarehouseId())).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(salesOrderVos)) {
            return;
        }
        boolean isAllWarehouseOrder = stockManager.isAllWarehouseOrder(user.getTenantId());
        //合并仓库订货下，只能单一订单恢复
        if (isAllWarehouseOrder && salesOrderVos.size() > 1) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "合并仓库订货条件下，只能单条订单恢复，不支持批量恢复");
        }

        dataIds = salesOrderVos.stream().map(SalesOrderModel.SalesOrderVo::getCustomerTradeId).collect(Collectors.toList());

        Map<String, List<SalesOrderModel.SalesOrderProductVO>> productVOsMap = productManager.queryProductsByOrderIds(user, dataIds);
        //合并仓库订货下，只有一条数据恢复
        if (isAllWarehouseOrder) {
            List<SalesOrderModel.SalesOrderProductVO> salesOrderProductVOs = productVOsMap.get(salesOrderVos.get(0).getCustomerTradeId());
            if (CollectionUtils.isEmpty(salesOrderProductVOs)) {
                return;
            }
            List<String> validWarehouseIds = wareHouseManager.queryValidByAccountId(user, salesOrderVos.get(0).getCustomerId(), null).stream().map(warehouse -> warehouse.getId()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(validWarehouseIds)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "客户没有适用的仓库");
            }

            List<CheckOrderModel.CheckProduct> checkProducts = salesOrderProductVOs.stream().map(salesOrderProductVO ->
                    new CheckOrderModel.CheckProduct(salesOrderProductVO.getProductId(), salesOrderProductVO.getAmount(), salesOrderProductVO.getProductName())).collect(Collectors.toList());
            checkAvailableStock(user, validWarehouseIds, checkProducts, isAllWarehouseOrder);
        } else {
            //单一仓库订货  合并产品数量 批量校验可用库存
            batchCheckAvailableStock(user, salesOrderVos, productVOsMap);
        }

    }


    /**
     * 批量校验订单和可用库存
     * @param user 操作者
     * @param dataIds 订单ids
     * @param info 库存操作信息
     */
    public void batchCheckOrderAndAddBlockedStock(User user, List<String> dataIds, StockOperateInfo info) {
        //批量查询订单 并过滤没有仓库的订单
        List<SalesOrderModel.SalesOrderVo> salesOrderVos = saleOrderManager.getByIds(user, dataIds);
        salesOrderVos = salesOrderVos.stream().filter(salesOrderVo -> !StringUtils.isBlank(salesOrderVo.getWarehouseId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(salesOrderVos)) {
            dataIds = salesOrderVos.stream().map(SalesOrderModel.SalesOrderVo::getCustomerTradeId).collect(Collectors.toList());

            //Map<订单id, 仓库id>
            Map<String, String> salesOrderIdWarehouseIdMap = salesOrderVos.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderVo::getCustomerTradeId, SalesOrderModel.SalesOrderVo::getWarehouseId));

            //Map<订单id, 产品列表>
            Map<String, List<SalesOrderModel.SalesOrderProductVO>> salesOrderProductMap = productManager.queryProductsByOrderIds(user, dataIds);


            boolean isAllWarehouseOrder = stockManager.isAllWarehouseOrder(user.getTenantId());
            //库存不能批量查询，所以这里还是采用循环
            dataIds.forEach(dataId -> {
                List<SalesOrderModel.SalesOrderProductVO> productVOs = salesOrderProductMap.get(dataId);

                List<CheckOrderModel.CheckProduct> products = productVOs.stream().map(product -> new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), product.getProductName()))
                        .collect(Collectors.toList());
                info.setOperateObjectId(dataId);

                List<String> validWarehouseIds = Arrays.asList(salesOrderIdWarehouseIdMap.get(dataId));

                CheckOrderModel.CheckProductStock queryStock = new CheckOrderModel.CheckProductStock();

                Map<String, BigDecimal> productNumMap = productVOs.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderProductVO::getProductId, SalesOrderModel.SalesOrderProductVO::getAmount));
                List<String> productIds = productNumMap.keySet().stream().collect(Collectors.toList());
                List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, validWarehouseIds.get(0), productIds);

                queryStock.setCheckProductNum(productNumMap);
                queryStock.setProductIds(productIds);
                queryStock.setCheckProductStock(stocks);

                //增加冻结库存
                addBlockedStock(user, queryStock, isAllWarehouseOrder, validWarehouseIds.get(0), info);
            });
        }
    }

    /**
     * 扣减冻结库存
     * @param user 操作者
     * @param dataId 订单id
     * @param info 库存操作信息
     */
    public void minusBlockedStock(User user, String dataId, StockOperateInfo info) {
        SalesOrderModel.SalesOrderVo salesOrderVo = saleOrderManager.getById(user, dataId);
        if (StringUtils.isBlank(salesOrderVo.getWarehouseId())) {
            log.info("minusBlockedStock quit. warehouseId is null.");
            return;
        }
        List<SalesOrderModel.SalesOrderProductVO> productVOs = productManager.getProductsByOrderId(user, dataId, true);
        if (!CollectionUtils.isEmpty(productVOs)) {
            minusBlockedStockByProducts(user, productVOs, salesOrderVo.getWarehouseId(), info);
        }
    }

    /**
     * 扣减冻结库存
     * @param user 操作者
     * @param salesOrderVo 订单
     * @param info 库存操作信息
     */
    public void minusBlockedStockByMinusProducts(User user, SalesOrderModel.SalesOrderVo salesOrderVo, List<SalesOrderProductVo> minusProductVos, StockOperateInfo info) {
        if (StringUtils.isBlank(salesOrderVo.getWarehouseId())) {
            log.info("minusBlockedStock quit. warehouseId is null.");
            return;
        }
        List<SalesOrderModel.SalesOrderProductVO> productVOs = minusProductVos.stream().map(productVo -> {
            SalesOrderModel.SalesOrderProductVO salesOrderProductVO = new SalesOrderModel.SalesOrderProductVO();
            salesOrderProductVO.setAmount(productVo.getAmount());
            salesOrderProductVO.setProductId(productVo.getProductId());
            salesOrderProductVO.setProductName(productVo.getProductName());
            return salesOrderProductVO;
        }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(productVOs)) {
            minusBlockedStockByProducts(user, productVOs, salesOrderVo.getWarehouseId(), info);
        }
    }



    /**
     * 批量扣减冻结库存
     * @param user 操作者
     * @param dataIds 订单id
     * @param info 库存操作信息
     */
    public void batchMinusBlockedStock(User user, List<String> dataIds, StockOperateInfo info) {
        if (CollectionUtils.isEmpty(dataIds)) {
            return;
        }

        List<SalesOrderModel.SalesOrderVo> salesOrderVos = saleOrderManager.getByIds(user, dataIds);
        if (CollectionUtils.isEmpty(salesOrderVos)) {
            return;
        }

        //过滤仓库为空的数据
        salesOrderVos = salesOrderVos.stream().filter(salesOrderVo -> !StringUtils.isBlank(salesOrderVo.getWarehouseId())).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(salesOrderVos)) {
            //Map<订单id, 仓库id>
            Map<String, String> orderWarehouseMap = salesOrderVos.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderVo::getCustomerTradeId, SalesOrderModel.SalesOrderVo::getWarehouseId));

            dataIds = salesOrderVos.stream().map(SalesOrderModel.SalesOrderVo::getCustomerTradeId).collect(Collectors.toList());
            //Map<订单id, 产品列表>
            Map<String, List<SalesOrderModel.SalesOrderProductVO>> salesOrderProductMap = productManager.queryProductsByOrderIds(user, dataIds);

            salesOrderProductMap.keySet().forEach(orderId -> {
                info.setOperateObjectId(orderId);
                minusBlockedStockByProducts(user, salesOrderProductMap.get(orderId), orderWarehouseMap.get(orderId), info);
            });
        }
    }


    /**
     * 校验仓库是否与之前一致  合并仓库订货下 特殊逻辑
     * @param user 操作者
     * @param arg 校验参数
     */

    public String checkBeforeEdit(User user, SalesOrderEditBeforeModel.Arg arg, SalesOrderModel.SalesOrderVo salesOrderVo) {
        if (arg.getSalesOrderVo() != null) {
            if (salesOrderVo == null) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "获取订单信息失败");
            }

            String oldWarehouseId = salesOrderVo.getWarehouseId();
            String newWarehouseId = arg.getSalesOrderVo().getWarehouseId();

            if (StringUtils.isBlank(oldWarehouseId) && StringUtils.isBlank(newWarehouseId)) {
                return null;
            }

            if (StringUtils.isBlank(newWarehouseId)) {
                //合并仓库下布局隐藏订货仓库（用户也可能手动设置显示） newWarehouseId为空（实际没修改） 读取订单数据上的仓库
                if (stockManager.isAllWarehouseOrder(user.getTenantId()) && !saleOrderManager.isSalesOrderWarehouseLayoutExisted(user)) {
                    newWarehouseId = oldWarehouseId;
                }
            }

            //仓库不允许修改
            if (!Objects.equals(oldWarehouseId, newWarehouseId)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "仓库不能修改");
            }

            return newWarehouseId;
        }
        return null;
    }

    //根据产品列表扣减冻结库存
    private void minusBlockedStockByProducts(User user, List<SalesOrderModel.SalesOrderProductVO> productVOs, String warehouseId, StockOperateInfo info) {
        if (CollectionUtils.isEmpty(productVOs) || StringUtils.isBlank(warehouseId)) {
            return;
        }

        List<String> productIds = productVOs.stream().map(SalesOrderModel.SalesOrderProductVO::getProductId).collect(Collectors.toList());
        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, IObjectData> stockMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName).toString(), stock -> stock));

        List<IObjectData> newStocks = productVOs.stream()
                .filter(productVO -> stockMap.get(productVO.getProductId()) != null)
                .map(productVO -> stockCalculateManager.minusBlocked(user, stockMap.get(productVO.getProductId()), productVO.getAmount()))
                .collect(Collectors.toList());

        Map<String, BigDecimal> productAmountMap = productVOs.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderProductVO::getProductId, SalesOrderModel.SalesOrderProductVO::getAmount));

        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, info, newStocks, productAmountMap, false, "", true, "-");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }
    }


    //增加冻结库存
    private void addBlockedStock(User user, CheckOrderModel.CheckProductStock queryStock, boolean isAllWarehouseOrder, String lockWarehouseId, StockOperateInfo info) {
        List<IObjectData> stocks = queryStock.getCheckProductStock();

        //合并仓库订货 增加冻结库存记录 要过滤锁库仓库
        if (isAllWarehouseOrder) {
            stocks = stocks.stream().filter(stock -> Objects.equals(stock.get(StockConstants.Field.Warehouse.apiName, String.class), lockWarehouseId)).collect(Collectors.toList());
        }
        Map<String, BigDecimal> productAmountMap = queryStock.getCheckProductNum();

        User admin = new User(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        //锁库的仓库 不存在的产品列表
        List<String> unAvailableProductIds = queryStock.getProductIds();
        //库存记录不为空 （可能全部产品库存都存在或存在部分）
        if (!CollectionUtils.isEmpty(stocks)) {
            Map<String, IObjectData> productStockMap = stocks.stream().collect(Collectors.toMap(iObjectData ->
                    iObjectData.get(StockConstants.Field.Product.apiName, String.class), stock -> stock));

            unAvailableProductIds.removeIf(productId -> productStockMap.keySet().contains(productId));
            //增加冻结库存
            List<IObjectData> newStocks = productStockMap.keySet().stream().map(productId -> stockCalculateManager.addBlocked(user, productStockMap.get(productId), productAmountMap.get(productId))).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(newStocks)) {
                List<StockLogDO> stockLogDOs = buildStockLog(user, info, newStocks, productAmountMap, false, "", true, "");
                stockManager.batchUpdate(admin, newStocks, stockLogDOs);

            }
            if (!CollectionUtils.isEmpty(unAvailableProductIds)) {
                List<IObjectData> saveStocks = unAvailableProductIds.stream().map(productId ->
                        stockManager.buildStockByStockAmount(user, lockWarehouseId, productId, "0", String.valueOf(productAmountMap.get(productId)), String.valueOf(BigDecimal.ZERO.subtract(productAmountMap.get(productId))))
                ).collect(Collectors.toList());

                List<StockLogDO> stockLogDOs = buildStockLog(user, info, saveStocks, productAmountMap, false, "", true, "");
                stockManager.bulkSave(admin, saveStocks, stockLogDOs);
            }
        } else {
            List<IObjectData> saveStocks = queryStock.getProductIds().stream().map(productId ->
                    stockManager.buildStockByStockAmount(user, lockWarehouseId, productId, "0", String.valueOf(productAmountMap.get(productId)), String.valueOf(BigDecimal.ZERO.subtract(productAmountMap.get(productId))))
            ).collect(Collectors.toList());

            List<StockLogDO> stockLogDOs = buildStockLog(user, info, saveStocks, productAmountMap, false, "", true, "");
            stockManager.bulkSave(admin, saveStocks, stockLogDOs);
        }
    }


    //校验可用库存
    private CheckOrderModel.CheckProductStock checkAvailableStock(User user, List<String> wareHouseIds, List<CheckOrderModel.CheckProduct> checkProducts, boolean isAllWarehouseOrder) {
        CheckOrderModel.CheckProductStock result = new CheckOrderModel.CheckProductStock();
        //校验订货仓库可用库存是否满足
        List<String> productList = checkProducts.stream().map(CheckOrderModel.CheckProduct::getProductId).collect(Collectors.toList());
        Map<String, BigDecimal> saleProductsMap = checkProducts.stream().collect(Collectors.toMap(CheckOrderModel.CheckProduct::getProductId, CheckOrderModel.CheckProduct::getProductNum));
        List<IObjectData> queryStockResult;

        if (isAllWarehouseOrder) {
            queryStockResult = stockManager.queryStocksByWarehouseIdsAndProductIds(user, productList, wareHouseIds);
        } else {
            queryStockResult = stockManager.queryByWarehouseIdAndProductIds(user, wareHouseIds.get(0), productList);
        }
        stockManager.checkAvailableStockWithProducts(user, queryStockResult, checkProducts, wareHouseIds.get(0), true, isAllWarehouseOrder);

        log.debug("querySalesOrderProductStock success!. user[{}], wareHouseId[{}], Products[{}]", user,
                wareHouseIds.get(0), checkProducts);
        result.setCheckProductStock(queryStockResult);
        result.setCheckProductNum(saleProductsMap);
        result.setProductIds(productList);
        return result;
    }

    //校验可用库存
    private void batchCheckAvailableStock(User user, List<SalesOrderModel.SalesOrderVo> salesOrderVos, Map<String, List<SalesOrderModel.SalesOrderProductVO>> productVOsMap) {
        //合并所有订单产品
        Map<String, String> salesOrderWarehouseMap = salesOrderVos.stream().collect(Collectors.toMap(SalesOrderModel.SalesOrderVo::getCustomerTradeId, SalesOrderModel.SalesOrderVo::getWarehouseId));
        //Map<仓库id, Map<产品id, 产品数量>
        Map<String, Map<String, BigDecimal>> warehouseProductAmountMap = new HashMap<>();

        productVOsMap.keySet().forEach(salesOrderId -> {
            String warehouseId = salesOrderWarehouseMap.get(salesOrderId);

            Map<String, BigDecimal> productAmountMap = warehouseProductAmountMap.get(warehouseId) != null ? warehouseProductAmountMap.get(warehouseId) : new HashMap<>();

            productVOsMap.get(salesOrderId).forEach(product -> {
                if (productAmountMap.get(product.getProductId()) == null) {
                    productAmountMap.put(product.getProductId(), product.getAmount());
                } else {
                    productAmountMap.put(product.getProductId(), productAmountMap.get(product.getProductId()).add(product.getAmount()));
                }
            });
            warehouseProductAmountMap.put(warehouseId, productAmountMap);
        });

        List<String> warehouseIds = warehouseProductAmountMap.keySet().stream().collect(Collectors.toList());

        List<String> productIds = warehouseProductAmountMap.values().stream().flatMap(productAmountMap -> productAmountMap.keySet().stream()).distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(productIds)) {
            List<IObjectData> stocks = stockManager.queryStocksByWarehouseIdsAndProductIds(user, productIds, warehouseIds);

            stockManager.batchCheckAvailableStock(user, stocks, warehouseProductAmountMap, productIds, true);
        }
    }

    //合并产品数量
    private List<SalesOrderProductVo> sumProductAmount(List<SalesOrderProductVo> salesOrderProductVos) {
        Map<String, BigDecimal> productMap = new HashMap<>();
        salesOrderProductVos.stream().forEach(product -> {
            if (productMap.get(product.getProductId()) != null) {
                productMap.put(product.getProductId(), product.getAmount().add(productMap.get(product.getProductId())));
            } else {
                productMap.put(product.getProductId(), product.getAmount());
            }
        });


        List<SalesOrderProductVo> sumProductList = Lists.newArrayList();
        List<String> existProductIds = Lists.newArrayList();
        salesOrderProductVos.forEach(product -> {
            if (!existProductIds.contains(product.getProductId())) {
                SalesOrderProductVo salesOrderProductVo = new SalesOrderProductVo();
                salesOrderProductVo.setPrice(product.getPrice());
                salesOrderProductVo.setProductId(product.getProductId());
                salesOrderProductVo.setAmount(productMap.get(product.getProductId()));
                salesOrderProductVo.setProductName(product.getProductName());
                sumProductList.add(salesOrderProductVo);
                existProductIds.add(product.getProductId());
            }
        });

        return sumProductList;
    }

    //合并产品数量
    private List<SalesOrderModel.SalesOrderProductVO> sumSalesOrderProductAmount(List<SalesOrderModel.SalesOrderProductVO> productVOs) {
        return productManager.sumSalesOrderProductAmount(productVOs);
    }
}
