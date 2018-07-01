package com.facishare.crm.sfainterceptor.predefine.manager;


import com.facishare.crm.rest.dto.ReturnOrderModel;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.*;
import com.facishare.crm.stock.predefine.service.model.CheckOrderModel;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/23
 */
@Service
@Slf4j(topic = "sfainterceptorAccess")
public class ReturnedGoodsInvoiceInterceptorStockManager extends CommonStockManager {
    @Resource
    private StockManager stockManager;

    @Resource
    private ReturnOrderManager returnOrderManager;

    @Resource
    private ProductManager productManager;

    @Resource
    private StockCalculateManager stockCalculateManager;

    /**
     * 增加实际库存
     * @param user 操作者
     * @param dataId 退货单id
     * @param info 操作库存信息
     */
    public void addRealStock(User user, String dataId, StockOperateInfo info) {
        ReturnOrderModel.ReturnOrderVo returnOrderVo = returnOrderManager.getById(user, dataId);
        if (StringUtils.isBlank(returnOrderVo.getWarehouseId())) {
            log.info("addRealStock quit. warehouseId is null");
            return;
        }
        List<ReturnOrderModel.ReturnOrderProductVO> productVOs = productManager.getReturnProductsByOrderId(user, dataId);
        if (!CollectionUtils.isEmpty(productVOs)) {
            List<String> productIds = productVOs.stream().map(ReturnOrderModel.ReturnOrderProductVO::getProductId).collect(Collectors.toList());
            List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, returnOrderVo.getWarehouseId(), productIds);
            Map<String, IObjectData> stockMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName).toString(), stock -> stock));
            List<ReturnOrderModel.ReturnOrderProductVO> unAvailableProducts = productVOs.stream().filter(productVO -> stockMap.get(productVO.getProductId()) == null).collect(Collectors.toList());
            List<ReturnOrderModel.ReturnOrderProductVO> availableProducts = productVOs.stream().filter(productVO -> stockMap.get(productVO.getProductId()) != null).collect(Collectors.toList());

            //无库存记录  插入库存记录
            List<IObjectData> newStocks = unAvailableProducts.stream().map(productVO -> stockManager.buildStock(user, returnOrderVo.getWarehouseId(), productVO.getProductId(), productVO.getAmount().toString())).collect(Collectors.toList());
            Map<String, BigDecimal> unAvailableProductAmountMap = unAvailableProducts.stream().collect(Collectors.toMap(ReturnOrderModel.ReturnOrderProductVO::getProductId, ReturnOrderModel.ReturnOrderProductVO::getAmount));
            if (!CollectionUtils.isEmpty(newStocks)) {
                List<StockLogDO> stockLogDOs = buildStockLog(user, info, newStocks, unAvailableProductAmountMap, true, "", false, "");
                stockManager.bulkSave(user, newStocks, stockLogDOs);
            }

            Map<String, BigDecimal> availableProductAmountMap = availableProducts.stream().collect(Collectors.toMap(ReturnOrderModel.ReturnOrderProductVO::getProductId, ReturnOrderModel.ReturnOrderProductVO::getAmount));
            //有库存记录 更新库存
            List<IObjectData> updateStocks = availableProducts.stream().map(productVO -> stockCalculateManager.addReal(user, stockMap.get(productVO.getProductId()), productVO.getAmount())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(updateStocks)) {
                List<StockLogDO> stockLogDOs = buildStockLog(user, info, updateStocks, availableProductAmountMap, true, "", false, "");
                stockManager.batchUpdate(user, updateStocks, stockLogDOs);
            }
        }

    }

    /**
     * 增加冻结库存
     * @param user 操作者
     * @param dataId 退货单id
     * @param info 库存操作信息
     */
    public void addBlockedStock(User user, String dataId, StockOperateInfo info) {
        ReturnOrderModel.ReturnOrderVo returnOrderVo = returnOrderManager.getById(user, dataId);
        if (StringUtils.isBlank(returnOrderVo.getWarehouseId())) {
            log.info("addBlockedStock quit. warehouseId is null");
            return;
        }
        List<ReturnOrderModel.ReturnOrderProductVO> returnOrderProductVOs = productManager.getReturnProductsByOrderId(user, dataId);
        if (!CollectionUtils.isEmpty(returnOrderProductVOs)) {
            addBlockedStockWithProducts(user, returnOrderProductVOs, returnOrderVo.getWarehouseId(), info);
        }
    }

    /**
     * 批量增加冻结库存
     * @param user 操作者
     * @param returnOrderVos 退货单列表
     * @param info 库存操作信息
     */
    public void batchAddBlockedStock(User user, List<ReturnOrderModel.ReturnOrderVo> returnOrderVos, StockOperateInfo info) {
        List<String> dataIds = returnOrderVos.stream().map(ReturnOrderModel.ReturnOrderVo::getReturnOrderId).collect(Collectors.toList());

        //Map<退货单id, 仓库id>
        Map<String, String> returnOrderIdWarehouseIdMap = returnOrderVos.stream().collect(Collectors.toMap(ReturnOrderModel.ReturnOrderVo::getReturnOrderId, ReturnOrderModel.ReturnOrderVo::getWarehouseId));

        //Map<退货单id, 退货产品列表>
        Map<String, List<ReturnOrderModel.ReturnOrderProductVO>> returnOrderProductMap = productManager.queryReturnOrderProductsByReturnOrderIds(user, dataIds);

        returnOrderProductMap.keySet().forEach(returnOrderId -> {
            info.setOperateObjectId(returnOrderId);
            addBlockedStockWithProducts(user, returnOrderProductMap.get(returnOrderId), returnOrderIdWarehouseIdMap.get(returnOrderId), info);
        });
    }


    /**
     * 扣减库存
     * @param user 操作者
     * @param dataId 退货单id
     * @param minusBlocked 是否扣减冻结库存
     * @param minusReal 是否扣减实际库存
     * @param info 操作库存信息
     */
    public void minusStock(User user, String dataId, boolean minusBlocked, boolean minusReal, StockOperateInfo info) {
        ReturnOrderModel.ReturnOrderVo returnOrderVo = returnOrderManager.getById(user, dataId);
        if (StringUtils.isBlank(returnOrderVo.getWarehouseId())) {
            log.info("minusStock quit. warehouseId is null");
            return;
        }
        List<ReturnOrderModel.ReturnOrderProductVO> returnOrderProductVOs =  productManager.getReturnProductsByOrderId(user, dataId);
        if (!CollectionUtils.isEmpty(returnOrderProductVOs)) {
            minusStockWithProducts(user, returnOrderProductVOs, returnOrderVo.getWarehouseId(), minusBlocked, minusReal, info);
        }
    }

    /**
     * 批量扣减实际库存
     * @param user 操作者
     * @param returnOrderVos 退货单列表
     * @param info 库存操作信息
     */
    public void batchMinusRealStock(User user, List<ReturnOrderModel.ReturnOrderVo> returnOrderVos, StockOperateInfo info) {
        List<String> dataIds = returnOrderVos.stream().map(ReturnOrderModel.ReturnOrderVo::getReturnOrderId).collect(Collectors.toList());

        //Map<退货单id, 仓库id>
        Map<String, String> returnOrderIdWarehouseIdMap = returnOrderVos.stream().collect(Collectors.toMap(ReturnOrderModel.ReturnOrderVo::getReturnOrderId, ReturnOrderModel.ReturnOrderVo::getWarehouseId));

        //Map<退货单id, 退货产品列表>
        Map<String, List<ReturnOrderModel.ReturnOrderProductVO>> returnOrderProductMap = productManager.queryReturnOrderProductsByReturnOrderIds(user, dataIds);

        returnOrderProductMap.keySet().forEach(returnOrderId -> {
            info.setOperateObjectId(returnOrderId);
            minusStockWithProducts(user, returnOrderProductMap.get(returnOrderId), returnOrderIdWarehouseIdMap.get(returnOrderId), false, true, info);
        });
    }

    /**
     * 校验可用库存
     * @param user 操作者
     * @param dataId 退货单id
     */
    public void checkAvailableStock(User user, String dataId) {
        ReturnOrderModel.ReturnOrderVo returnOrderVo = returnOrderManager.getById(user, dataId);
        if (StringUtils.isBlank(returnOrderVo.getWarehouseId())) {
            log.info("checkAvailableStock quit. warehouseId is null");
            return;
        }
        List<ReturnOrderModel.ReturnOrderProductVO> returnOrderProductVOs = productManager.getReturnProductsByOrderId(user, dataId);
        if (!CollectionUtils.isEmpty(returnOrderProductVOs)) {
            List<String> productIds = returnOrderProductVOs.stream().map(ReturnOrderModel.ReturnOrderProductVO::getProductId).collect(Collectors.toList());
            List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, returnOrderVo.getWarehouseId(), productIds);

            List<CheckOrderModel.CheckProduct> checkProducts = returnOrderProductVOs.stream().map(product -> {
                String productName = product.getProductDetail() != null ? product.getProductDetail().getName() : "";
                return new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), productName);
            }).collect(Collectors.toList());
            stockManager.checkAvailableStockWithProducts(user, stocks, checkProducts, returnOrderVo.getWarehouseId(), false, false);

        }
    }

    /**
     * 批量校验可用库存
     * @param user 操作者
     * @param dataIds 退货单ids
     */
    public void batchCheckAvailableStock(User user, List<String> dataIds) {
        List<ReturnOrderModel.ReturnOrderVo> returnOrderVos = returnOrderManager.getByIds(user, dataIds);
        returnOrderVos = returnOrderVos.stream().filter(returnOrderVo -> StringUtils.isNotBlank(returnOrderVo.getWarehouseId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(returnOrderVos)) {
            dataIds = returnOrderVos.stream().map(ReturnOrderModel.ReturnOrderVo::getReturnOrderId).collect(Collectors.toList());

            //Map<退货单id, 仓库id>
            Map<String, String> returnOrderIdWarehouseIdMap = returnOrderVos.stream().collect(Collectors.toMap(ReturnOrderModel.ReturnOrderVo::getReturnOrderId, ReturnOrderModel.ReturnOrderVo::getWarehouseId));

            //Map<退货单id, 退货产品列表>
            Map<String, List<ReturnOrderModel.ReturnOrderProductVO>> returnOrderProductMap = productManager.queryReturnOrderProductsByReturnOrderIds(user, dataIds);

            //合并退货单所有产品数量
            Map<String, Map<String, BigDecimal>> warehouseProductAmountMap = new HashMap<>();

            returnOrderProductMap.keySet().forEach(returnOrderId -> {
                String warehouseId = returnOrderIdWarehouseIdMap.get(returnOrderId);
                Map<String, BigDecimal> productAmountMap = warehouseProductAmountMap.get(warehouseId) != null ? warehouseProductAmountMap.get(warehouseId) : new HashMap<>();

                returnOrderProductMap.get(returnOrderId).forEach(product -> {
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

                stockManager.batchCheckAvailableStock(user, stocks, warehouseProductAmountMap, productIds, false);
            }
        }
    }

//    /**
//     * 校验退货数量是否超过订单数量
//     * @param user
//     * @param salesOrderId
//     */
//    public void checkReturnedProductsOutNumber(User user, String salesOrderId) {
//        List<ReturnOrderModel.ReturnOrderVo> returnOrderVos = returnOrderManager.getBySalesOrderId(user, salesOrderId);
//        if (CollectionUtils.isEmpty(returnOrderVos)) {
//            return;
//        }
//        List<String> validReturnOrderIds = returnOrderVos.stream().filter(returnOrderVo -> Objects.equals("6", returnOrderVo.getStatus()) || Objects.equals("7", returnOrderVo.getStatus()))
//                .map(ReturnOrderModel.ReturnOrderVo::getReturnOrderId).collect(Collectors.toList());
//        Map<String, List<ReturnOrderModel.ReturnOrderProductVO>> productMap = productManager.queryReturnOrderProductsByReturnOrderIds(user, validReturnOrderIds);
//        Map<String, BigDecimal> productAmountMap = new HashMap<>();
//        productMap.values().forEach(returnOrderProductVOList -> {
//            returnOrderProductVOList.forEach(returnOrderProductVO -> {
//                if (productAmountMap.get(returnOrderProductVO.getProductId()) == null) {
//                    productAmountMap.put(returnOrderProductVO.getProductId(), returnOrderProductVO.getAmount());
//                } else {
//                    productAmountMap.put(returnOrderProductVO.getProductId(), productAmountMap.get(returnOrderProductVO.getProductId()).add(returnOrderProductVO.getAmount()));
//                }
//            });
//        });
//
//        //比较退货产品数量
//    }

    //根据退货产品 扣减库存
    private void minusStockWithProducts(User user, List<ReturnOrderModel.ReturnOrderProductVO> returnOrderProductVOs, String warehouseId, boolean minusBlock, boolean minusReal, StockOperateInfo info) {
        if (CollectionUtils.isEmpty(returnOrderProductVOs) || StringUtils.isBlank(warehouseId)) {
            return;
        }

        List<String> productIds = returnOrderProductVOs.stream().map(ReturnOrderModel.ReturnOrderProductVO::getProductId).collect(Collectors.toList());
        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);

        Map<String, IObjectData> stockMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName).toString(), stock -> stock));

        List<CheckOrderModel.CheckProduct> checkProducts = returnOrderProductVOs.stream().map(product -> {
            String productName = product.getProductDetail() != null ? product.getProductDetail().getName() : "";
            return new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), productName);
        }).collect(Collectors.toList());


        Map<String, BigDecimal> productAmountMap = checkProducts.stream().collect(Collectors.toMap(CheckOrderModel.CheckProduct::getProductId, CheckOrderModel.CheckProduct::getProductNum));
        List<IObjectData> newStocks = null;
        if (minusBlock && minusReal) {
            //TODO 由于退货单对象，这里先允许实际库存为负
            if (!stockManager.checkRealStockWithProducts(user, stocks, checkProducts)) {
                log.warn("returnGoodsOrder minusBlock and minusReal. real stock is not enough.");
            }
            newStocks = checkProducts.stream().map(product -> stockCalculateManager.minusBlockReal(user, stockMap.get(product.getProductId()), product.getProductNum())).collect(Collectors.toList());
        } else if (minusBlock) {
            newStocks = checkProducts.stream().map(product ->
                    stockCalculateManager.minusBlocked(user, stockMap.get(product.getProductId()), product.getProductNum())).collect(Collectors.toList());
        } else if (minusReal) {
            //TODO 由于退货单对象，这里先允许实际库存为负
            if (!stockManager.checkRealStockWithProducts(user, stocks, checkProducts)) {
                log.warn("returnGoodsOrder minusReal. real stock is not enough.");
            }
            newStocks = checkProducts.stream().map(product ->
                    stockCalculateManager.minusReal(user, stockMap.get(product.getProductId()), product.getProductNum())).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, info, newStocks, productAmountMap, minusReal, "-", minusBlock, "-");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }
    }


    //根据退货产品 增加冻结库存
    private void addBlockedStockWithProducts(User user, List<ReturnOrderModel.ReturnOrderProductVO> returnOrderProductVOs, String warehouseId, StockOperateInfo info) {
        if (CollectionUtils.isEmpty(returnOrderProductVOs) || StringUtils.isBlank(warehouseId)) {
            return;
        }

        List<String> productIds = returnOrderProductVOs.stream().map(ReturnOrderModel.ReturnOrderProductVO::getProductId).collect(Collectors.toList());
        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, IObjectData> stockMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName).toString(), stock -> stock));

        List<CheckOrderModel.CheckProduct> checkProducts = returnOrderProductVOs.stream().map(product -> {
            String productName = product.getProductDetail() != null ? product.getProductDetail().getName() : "";
            return new CheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), productName);
        }).collect(Collectors.toList());

        stockManager.checkAvailableStockWithProducts(user, stocks, checkProducts, warehouseId, false, false);

        Map<String, BigDecimal> productAmountMap = checkProducts.stream().collect(Collectors.toMap(CheckOrderModel.CheckProduct::getProductId, CheckOrderModel.CheckProduct::getProductNum));
        List<IObjectData> newStocks = checkProducts.stream().map(product ->
                stockCalculateManager.addBlocked(user, stockMap.get(product.getProductId()), product.getProductNum())).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, info, newStocks, productAmountMap, false, "", true, "");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }
    }
}
