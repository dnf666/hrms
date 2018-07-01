package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.dao.StockLogDAO;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 库存加减
 * Created by linchf on 2018/1/13.
 */
@Service
@Slf4j(topic = "stockAccess")
public class StockCalculateManager {

    @Resource
    private StockManager stockManager;

    @Resource
    private StockLogDAO stockLogDAO;

    @Resource
    private SaleOrderManager saleOrderManager;

    private static final String BLOCK_API_NAME = StockConstants.Field.BlockedStock.apiName;

    private static final String REAL_API_NAME = StockConstants.Field.RealStock.apiName;

    private static final String AVAILABLE_API_NAME = StockConstants.Field.AvailableStock.apiName;

    /**
     * 添加冻结库存  添加前需校验
     * @param objectData 库存对象
     * @param value 增加库存数
     */
    public IObjectData addBlocked(User user, IObjectData objectData, BigDecimal value) {
        if (objectData == null) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }


        String realStock = objectData.get(REAL_API_NAME).toString();
        String nowBlockStock = new BigDecimal(objectData.get(BLOCK_API_NAME).toString()).add(value).toString();
        objectData.set(BLOCK_API_NAME, nowBlockStock);
        objectData.set(AVAILABLE_API_NAME, new BigDecimal(realStock).subtract(new BigDecimal(nowBlockStock)).toString());

        return objectData;
    }

    /**
     * 减少冻结库存
     * @param objectData 库存对象
     * @param value 增加库存数
     */
    public IObjectData minusBlocked(User user, IObjectData objectData, BigDecimal value) {
        if (objectData == null) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }
        String realStock = objectData.get(REAL_API_NAME).toString();
        String nowBlockStock = new BigDecimal(objectData.get(BLOCK_API_NAME).toString()).subtract(value).toString();

        objectData.set(BLOCK_API_NAME, nowBlockStock);
        objectData.set(AVAILABLE_API_NAME, new BigDecimal(realStock).subtract(new BigDecimal(nowBlockStock)).toString());

        return objectData;
    }

    /**
     * 增加实际库存
     * @param objectData 库存对象
     * @param value 增加库存数
     */
    public IObjectData addReal(User user, IObjectData objectData, BigDecimal value) {
        if (objectData == null) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }

        String blockStock = objectData.get(BLOCK_API_NAME).toString();
        String nowRealStock = new BigDecimal(objectData.get(REAL_API_NAME).toString()).add(value).toString();

        objectData.set(REAL_API_NAME, nowRealStock);
        objectData.set(AVAILABLE_API_NAME, new BigDecimal(nowRealStock).subtract(new BigDecimal(blockStock)).toString());

        return objectData;
    }

    /**
     * 减少实际库存  减少前需校验
     * @param objectData 库存对象
     * @param value 增加库存数
     */
    public IObjectData minusReal(User user, IObjectData objectData, BigDecimal value) {
        if (objectData == null) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }

        String blockStock = objectData.get(BLOCK_API_NAME).toString();
        String nowRealStock = new BigDecimal(objectData.get(REAL_API_NAME).toString()).subtract(value).toString();

        objectData.set(REAL_API_NAME, nowRealStock);
        objectData.set(AVAILABLE_API_NAME, new BigDecimal(nowRealStock).subtract(new BigDecimal(blockStock)).toString());

        return objectData;
    }

    /**
     * 添加实际库存、添加冻结库存
     * @param objectData 库存对象
     * @param value 增加冻结库存数
     */
    public IObjectData addBlockReal(User user, IObjectData objectData, BigDecimal value) {
        if (objectData == null) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }

        BigDecimal nowRealStock = new BigDecimal(objectData.get(REAL_API_NAME).toString());
        BigDecimal nowBlockStock = new BigDecimal(objectData.get(BLOCK_API_NAME).toString());

        objectData.set(REAL_API_NAME, nowRealStock.add(value).toString());
        objectData.set(BLOCK_API_NAME, nowBlockStock.add(value).toString());
        objectData.set(AVAILABLE_API_NAME, nowRealStock.subtract(nowBlockStock).toString());

        return objectData;
    }

    /**
     * 扣减实际库存、冻结库存  需要校验
     */
    public IObjectData minusBlockReal(User user, IObjectData objectData, BigDecimal value) {
        if (objectData == null) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }


        BigDecimal nowRealStock = new BigDecimal(objectData.get(REAL_API_NAME).toString());
        BigDecimal nowBlockStock = new BigDecimal(objectData.get(BLOCK_API_NAME).toString());

        objectData.set(REAL_API_NAME, nowRealStock.subtract(value).toString());
        objectData.set(BLOCK_API_NAME, nowBlockStock.subtract(value).toString());
        objectData.set(AVAILABLE_API_NAME, nowRealStock.subtract(nowBlockStock).toString());

        return objectData;
    }


    /**
     * 发货单废除操作后增加实际库存和冻结库存
     */
    public void deliveryNoteToInvalid(User user, String warehouseId, String salesId, Map<String, BigDecimal> deliveryProduct, StockOperateInfo info) {
        //1、校验参数
        Preconditions.checkNotNull(info);
        Preconditions.checkNotNull(salesId);
        Preconditions.checkNotNull(warehouseId);
        if (deliveryProduct.isEmpty()) {
            log.info("deliveryProduct is null");
            return;
        }

        //2、根据订单ID查询订货仓库
        String salesOrderWareHouseId = saleOrderManager.getById(user, salesId).getWarehouseId();
        Set<String> productIds = deliveryProduct.keySet();
        List<String> productIdList = Lists.newArrayList(productIds);

        //3、判断订货仓库和发货仓库是否一致
        if (salesOrderWareHouseId.equals(warehouseId)) {
            //3.1、若一致，查询发货仓库产品的库存信息，增加冻结库存和实际库存
            List<IObjectData> stockList = buildStockAfterAddBlockReal(user, warehouseId, deliveryProduct, productIdList);
            if (!CollectionUtils.isEmpty(stockList)) {
                //保存库存操作记录
                List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                    StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                    stockLogDO.setModifiedBlockedStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId())));
                    stockLogDO.setModifiedRealStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId())));
                    return stockLogDO;
                }).collect(Collectors.toList());

                stockManager.batchUpdate(user, stockList, stockLogDOs);
            }
        } else {
            //3.2、若不一致，分别增加订货仓库的冻结库存和发货仓库的实际库存
            addSalesOrderAndDeliveryStock(user, warehouseId, deliveryProduct, salesOrderWareHouseId, productIdList, info);
        }
    }

    private void addSalesOrderAndDeliveryStock(User user, String warehouseId,
                                               Map<String, BigDecimal> deliveryProduct,
                                               String salesOrderWareHouseId,
                                               List<String> productIdList,
                                               StockOperateInfo info) {
        Map<String, IObjectData> productId2ObjectData;
        List<IObjectData> queryDeliveryWarehouse = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIdList);
        if (CollectionUtils.isEmpty(queryDeliveryWarehouse)) {
            log.warn("stockManager.queryByWarehouseIdAndProductIds result is empty! user[{}], warehouseId[{}], productIds",
                    user, warehouseId, productIdList);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR);
        }
        productId2ObjectData = queryDeliveryWarehouse.stream().collect(Collectors.toMap(e -> e.get(StockConstants.Field.Product.apiName, String.class), e -> e));

        List<IObjectData> querySalesWarehouseStock = stockManager.queryByWarehouseIdAndProductIds(user, salesOrderWareHouseId, productIdList);
        if (CollectionUtils.isEmpty(querySalesWarehouseStock)) {
            log.warn("stockManager.queryByWarehouseIdAndProductIds result is empty! user[{}], warehouseId[{}], productIds",
                    user, warehouseId, productIdList);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR);
        }

        Map<String, IObjectData> productId2SalesStockObject = querySalesWarehouseStock.stream().
                collect(Collectors.toMap(e -> e.get(StockConstants.Field.Product.apiName, String.class), e -> e));

        List<IObjectData> stockList = Lists.newArrayList();
        List<IObjectData> salesOfStockList = Lists.newArrayList();
        List<IObjectData> deliveryOfStockList = Lists.newArrayList();

        productIdList.forEach(productId -> {
            //订货仓库增加冻结库存
            salesOfStockList.add(addBlocked(user, productId2SalesStockObject.get(productId), deliveryProduct.get(productId)));
            //发货仓库增加实际库存
            deliveryOfStockList.add(addReal(user, productId2ObjectData.get(productId), deliveryProduct.get(productId)));
        });

        stockList.addAll(salesOfStockList);
        stockList.addAll(deliveryOfStockList);

        if (!CollectionUtils.isEmpty(stockList)) {

            //保存库存操作日志
            List<StockLogDO> saleOfStockLogDOs = salesOfStockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId())));
                return stockLogDO;
            }).collect(Collectors.toList());

            List<StockLogDO> deliveryStockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedRealStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId())));
                return stockLogDO;
            }).collect(Collectors.toList());
            saleOfStockLogDOs.addAll(deliveryStockLogDOs);

            //批量更新库存记录
            stockManager.batchUpdate(user, stockList, saleOfStockLogDOs);
        }
    }

    private List<IObjectData> buildStockAfterAddBlockReal(User user, String warehouseId, Map<String, BigDecimal> deliveryProduct, List<String> productIdList) {
        Map<String, IObjectData> productId2ObjectData;
        List<IObjectData> queryStocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIdList);
        if (CollectionUtils.isEmpty(queryStocks)) {
            log.warn("stockManager.queryByWarehouseIdAndProductIds result is empty! user[{}], warehouseId[{}], productIds",
                    user, warehouseId, productIdList);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR);
        }
        productId2ObjectData = queryStocks.stream().collect(Collectors.toMap(e -> e.get(StockConstants.Field.Product.apiName, String.class), e -> e));

        //增加冻结库存和实际库存
        return productIdList.stream().map(productId -> addBlockReal(user, productId2ObjectData.get(productId), deliveryProduct.get(productId))).collect(Collectors.toList());
    }

    /**
     * 发货单生命状态变为Normal时，扣减库存
     */
    public void deliveryNoteToNormal(User user, String warehouseId, String salesId, Map<String, BigDecimal> deliveryProduct, StockOperateInfo info) {
        //1、校验参数
        Preconditions.checkNotNull(info);
        Preconditions.checkNotNull(salesId);
        Preconditions.checkNotNull(warehouseId);
        if (deliveryProduct.isEmpty()) {
            log.info("deliveryProduct is null");
            return;
        }

        //2、根据订单ID查询订货仓库
        String salesOrderWareHouseId = saleOrderManager.getById(user, salesId).getWarehouseId();
        Set<String> productIds = deliveryProduct.keySet();
        List<String> productIdList = Lists.newArrayList(productIds);


        //3、判断订货仓库和发货仓库是否一致
        if (salesOrderWareHouseId.equals(warehouseId)) {
            //3.1、若一致，查询发货仓库产品的库存信息，扣减冻结库存和实际库存
            List<IObjectData> stockList = buildStockAfterMinusBlockReal(user, warehouseId, deliveryProduct, productIdList);
            if (!CollectionUtils.isEmpty(stockList)) {

                //保存库存操作日志
                List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                    StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                    stockLogDO.setModifiedBlockedStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId()).negate()));
                    stockLogDO.setModifiedRealStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId()).negate()));
                    return stockLogDO;
                }).collect(Collectors.toList());

                stockManager.batchUpdate(user, stockList, stockLogDOs);
            }
        } else {
            //3.2、若不一致，分别扣减订货仓库的冻结库存和发货仓库的实际库存
            minusSalesOrderAndDeliveryStock(user, warehouseId, deliveryProduct, salesOrderWareHouseId, productIdList, info);
        }
    }

    private void minusSalesOrderAndDeliveryStock(User user, String warehouseId,
                                                  Map<String, BigDecimal> deliveryProduct,
                                                  String salesOrderWareHouseId,
                                                  List<String> productIdList,
                                                 StockOperateInfo info) {
        Map<String, IObjectData> productId2ObjectData;
        List<IObjectData> queryDeliveryWarehouse = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIdList);
        if (CollectionUtils.isEmpty(queryDeliveryWarehouse)) {
            log.warn("stockManager.queryByWarehouseIdAndProductIds result is empty! user[{}], warehouseId[{}]", user, warehouseId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR);
        }
        productId2ObjectData = queryDeliveryWarehouse.stream().collect(Collectors.toMap(e -> e.get(StockConstants.Field.Product.apiName, String.class), e -> e));

        List<IObjectData> querySalesWarehouse = stockManager.queryByWarehouseIdAndProductIds(user, salesOrderWareHouseId, productIdList);
        if (CollectionUtils.isEmpty(queryDeliveryWarehouse)) {
            log.warn("stockManager.queryByWarehouseIdAndProductIds result is empty! user[{}], warehouseId[{}]", user, warehouseId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR);
        }

        Map<String, IObjectData> productId2SalesStockObject = querySalesWarehouse.stream().
                collect(Collectors.toMap(e -> e.get(StockConstants.Field.Product.apiName, String.class), e -> e));

        //检验发货仓库实际库存
        checkRealStock(productIdList, productId2ObjectData, deliveryProduct);

        List<IObjectData> stockList = Lists.newArrayList();
        List<IObjectData> salesOfStockList = Lists.newArrayList();
        List<IObjectData> deliveryOfStockList = Lists.newArrayList();
        productIdList.forEach(productId -> {
            //订货仓库扣减冻结库存
            salesOfStockList.add(minusBlocked(user, productId2SalesStockObject.get(productId), deliveryProduct.get(productId)));
            //发货仓库扣减实际库存
            deliveryOfStockList.add(minusReal(user, productId2ObjectData.get(productId), deliveryProduct.get(productId)));
        });

        stockList.addAll(salesOfStockList);
        stockList.addAll(deliveryOfStockList);

        if (!CollectionUtils.isEmpty(stockList)) {

            //保存库存操作日志日志
            List<StockLogDO> saleOfStockLogDOs = salesOfStockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());

            List<StockLogDO> deliveryStockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedRealStockNum(String.valueOf(deliveryProduct.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());
            saleOfStockLogDOs.addAll(deliveryStockLogDOs);

            //批量更新库存记录
            stockManager.batchUpdate(user, stockList, saleOfStockLogDOs);
        }
    }

    private List<IObjectData> buildStockAfterMinusBlockReal(User user, String warehouseId, Map<String, BigDecimal> deliveryProduct, List<String> productIdList) {
        Map<String, IObjectData> productId2ObjectData;
        List<IObjectData> queryStocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIdList);
        if (CollectionUtils.isEmpty(queryStocks)) {
            log.warn("stockManager.queryByWarehouseIdAndProductIds result is empty! user[{}], warehouseId[{}], productIds",
                    user, warehouseId, productIdList);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR);
        }
        productId2ObjectData = queryStocks.stream().collect(Collectors.toMap(e -> e.get(StockConstants.Field.Product.apiName, String.class), e -> e));

        //校验发货仓库实际库存
        checkRealStock(productIdList, productId2ObjectData, deliveryProduct);

        return productIdList.stream().map(productId -> minusBlockReal(user, productId2ObjectData.get(productId), deliveryProduct.get(productId))).collect(Collectors.toList());
    }

    private void checkRealStock(List<String> productIds, Map<String, IObjectData> productId2ObjectData, Map<String, BigDecimal> deliveryProduct) {
        productIds.forEach(productId -> {
            BigDecimal stockAmount = productId2ObjectData.get(productId).get(StockConstants.Field.RealStock.apiName, BigDecimal.class);
            BigDecimal deliveryAmount = deliveryProduct.get(productId);
            if (stockAmount.compareTo(deliveryAmount) < 0) {
                log.warn("checkRealStock failed!，productId[{}], stockObjectData[{}], deliveryAmount[{}]",
                        productId, productId2ObjectData.get(productId), deliveryAmount);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "实际库存不足，扣减库存失败");
            }
        });
    }
}
