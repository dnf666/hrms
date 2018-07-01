package com.facishare.crm.requisitionnote.predefine.manager;

import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.dao.StockLogDAO;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.StockCalculateManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 16/03/2018
 */
@Service
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteCalculateManager extends CommonManager {
    @Resource
    private StockManager stockManager;

    @Resource
    private StockLogDAO stockLogDAO;

    @Resource
    private StockCalculateManager stockCalculateManager;

    public void insertOrUpdateStock(User user,String warehouseId, IObjectData objectData, StockOperateInfo info) {
        //1、<产品Id, 调拨数量>
        Map<String, BigDecimal> productId2Amount = queryRequisitionAmount(user, objectData);
        if (productId2Amount.isEmpty()) {
            return;
        }

        //2、查询调拨产品库存记录
        List<IObjectData> stockList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, new ArrayList<>(productId2Amount.keySet()));

        if (CollectionUtils.isEmpty(stockList)) {
            bulkSaveStock(user, warehouseId, new ArrayList<>(productId2Amount.keySet()), productId2Amount, info);
        } else {
            //3、构建计算库存后新的库存记录
            Map<String, IObjectData> productId2stockMap = stockList.stream().collect(Collectors.toMap(i -> i.get(RequisitionNoteProductConstants.Field.Product.apiName, String.class), Function.identity()));
            List<String> noStockOfProductId = productId2Amount.keySet().stream().filter(productId -> !productId2stockMap.containsKey(productId)).collect(Collectors.toList());

            //新增库存记录
            bulkSaveStock(user, warehouseId, noStockOfProductId, productId2Amount, info);

            //更新库存记录
            List<IObjectData> updateStockList = productId2stockMap.entrySet().stream().map(map -> stockCalculateManager.addReal(user, map.getValue(), productId2Amount.get(map.getKey()))).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(updateStockList)) {

                List<StockLogDO> stockLogDOs = updateStockList.stream().map(newStock -> {
                    StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                    stockLogDO.setModifiedRealStockNum(String.valueOf(productId2Amount.get(stockLogDO.getProductId())));
                    return stockLogDO;
                }).collect(Collectors.toList());

                stockManager.batchUpdate(user, updateStockList, stockLogDOs);
            }
        }
    }

    public void minusBlockedStock(User user,String warehouseId, IObjectData objectData, StockOperateInfo info) {
        //1、<产品Id, 调拨数量>
        Map<String, BigDecimal> productId2Amount = queryRequisitionAmount(user, objectData);
        if (productId2Amount.isEmpty()) {
            return;
        }

        //2、查询调拨产品库存记录
        List<IObjectData> stockList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, new ArrayList<>(productId2Amount.keySet()));
        Map<String, IObjectData> productId2stockMap = stockList.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), Function.identity()));

        //3、构建计算库存后新的库存记录
        List<IObjectData> newStockList = Lists.newArrayList();
        productId2stockMap.entrySet().forEach(entry -> {
            newStockList.add(stockCalculateManager.minusBlocked(user, entry.getValue(), productId2Amount.get(entry.getKey())));
        });

        //4、批量更新/保存操作
        if (!CollectionUtils.isEmpty(stockList)) {

            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(productId2Amount.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());

            //扣减冻结库存
            stockManager.batchUpdate(user, stockList, stockLogDOs);

        }

    }
    public void addBlockedStock(User user,String warehouseId, IObjectData objectData, StockOperateInfo info) {
        //1、<产品Id, 调拨数量>
        Map<String, BigDecimal> productId2Amount = queryRequisitionAmount(user, objectData);
        if (productId2Amount.isEmpty()) {
            return;
        }
        log.info("productId2Amount[{}]", productId2Amount);

        //2、查询调拨产品库存记录
        List<IObjectData> stockList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, new ArrayList<>(productId2Amount.keySet()));
        log.info("stockList[{}]", stockList);

        Map<String, IObjectData> productId2stockMap = stockList.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), Function.identity()));

        //3、构建计算库存后新的库存记录
        List<IObjectData> newStockList = Lists.newArrayList();
        productId2stockMap.entrySet().forEach(entry -> {
            newStockList.add(stockCalculateManager.addBlocked(user, entry.getValue(), productId2Amount.get(entry.getKey())));
        });

        //4、批量更新/保存操作
        if (!CollectionUtils.isEmpty(stockList)) {

            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(productId2Amount.get(stockLogDO.getProductId())));
                return stockLogDO;
            }).collect(Collectors.toList());

            //增加冻结库存
            stockManager.batchUpdate(user, stockList, stockLogDOs);
        }

    }

    public void minusRealStock(User user,String warehouseId, IObjectData objectData, StockOperateInfo info) {
        //1、<产品Id, 调拨数量>
        Map<String, BigDecimal> productId2Amount = queryRequisitionAmount(user, objectData);
        if (productId2Amount.isEmpty()) {
            return;
        }

        //2、查询调拨产品库存记录
        List<IObjectData> stockList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, new ArrayList<>(productId2Amount.keySet()));
        Map<String, IObjectData> productId2stockMap = stockList.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), Function.identity()));

        //3、构建计算库存后新的库存记录
        List<IObjectData> newStockList = Lists.newArrayList();
        productId2stockMap.entrySet().forEach(entry -> {
            newStockList.add(stockCalculateManager.minusReal(user, entry.getValue(), productId2Amount.get(entry.getKey())));
        });

        //4、批量更新/保存操作
        if (!CollectionUtils.isEmpty(stockList)) {

            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedRealStockNum(String.valueOf(productId2Amount.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());

            //扣减实际库存
            stockManager.batchUpdate(user, stockList, stockLogDOs);
        }

    }

    private void bulkSaveStock(User user, String warehouseId, List<String> productIds, Map<String, BigDecimal> productId2NumMap, StockOperateInfo info) {
        List<IObjectData> saveStockList = productIds.stream().map(productId ->
                stockManager.buildStock(user, warehouseId, productId, productId2NumMap.get(productId).toString())).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(saveStockList)) {

            List<StockLogDO> stockLogDOs = saveStockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedRealStockNum(String.valueOf(productId2NumMap.get(stockLogDO.getProductId())));
                return stockLogDO;
            }).collect(Collectors.toList());

            stockManager.bulkSave(user, saveStockList, stockLogDOs);
        }
    }


    public void minusBlockedRealStock(User user,String warehouseId, IObjectData objectData, StockOperateInfo info) {
        //1、<产品Id, 调拨数量>
        Map<String, BigDecimal> productId2Amount = queryRequisitionAmount(user, objectData);
        if (productId2Amount.isEmpty()) {
            return;
        }

        //2、查询调拨产品库存记录
        List<IObjectData> stockList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, new ArrayList<>(productId2Amount.keySet()));
        Map<String, IObjectData> productId2stockMap = stockList.stream().collect(Collectors.toMap(d -> d.get(StockConstants.Field.Product.apiName, String.class), Function.identity()));

        //3、构建计算库存后新的库存记录
        List<IObjectData> newStockList = Lists.newArrayList();
        productId2stockMap.entrySet().forEach(entry -> {
            newStockList.add(stockCalculateManager.minusBlockReal(user, entry.getValue(), productId2Amount.get(entry.getKey())));
        });

        //4、批量更新/保存操作
        if (!CollectionUtils.isEmpty(stockList)) {
            List<StockLogDO> stockLogDOs = stockList.stream().map(newStock -> {
                StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
                stockLogDO.setModifiedBlockedStockNum(String.valueOf(productId2Amount.get(stockLogDO.getProductId()).negate()));
                stockLogDO.setModifiedRealStockNum(String.valueOf(productId2Amount.get(stockLogDO.getProductId()).negate()));
                return stockLogDO;
            }).collect(Collectors.toList());

            //扣减冻结库存和实际库存
            stockManager.batchUpdate(user, stockList, stockLogDOs);
        }
    }

    public void invalidAfter(User user, IObjectData objectData) {
        String warehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);

        //<产品id, 调拨数量>
        Map<String, BigDecimal> productId2AmountMap = queryRequisitionAmount(user, objectData);
        List<String> productIds = new ArrayList<>(productId2AmountMap.keySet());

        //查询产品库存信息
        List<IObjectData> stockObjectDataList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        //<产品id, 实际库存>
        Map<String, BigDecimal> productId2RealStockMap = stockObjectDataList.stream().collect(Collectors.toMap(s -> s.get(StockConstants.Field.Product.apiName, String.class), s -> s.get(StockConstants.Field.RealStock.apiName, BigDecimal.class)));

        productId2AmountMap.entrySet().forEach(entry -> {
            if (productId2RealStockMap.containsKey(entry.getKey())) {
                if (0 > productId2RealStockMap.get(entry.getKey()).compareTo(entry.getValue())) {
                    log.error("RequisitionNote invalid has a error that is real stock less than requisition Amount, user[{}], productId[{}], objectData[{}]",
                            user, entry.getKey(), objectData);
                }
            }
        });

    }

    private Map<String, BigDecimal> queryRequisitionAmount(User user, IObjectData objectData) {
        //1、查询调拨单产品
        List<IObjectData> objectDataList = findDetailObjectDataIncludeInvalid(user, objectData);

        //2、<产品Id, 调拨数量>
        return objectDataList.stream().collect(Collectors.toMap(data -> data.get(RequisitionNoteProductConstants.Field.Product.apiName, String.class),
                data -> (new BigDecimal(data.get(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName).toString()))));
    }
}
