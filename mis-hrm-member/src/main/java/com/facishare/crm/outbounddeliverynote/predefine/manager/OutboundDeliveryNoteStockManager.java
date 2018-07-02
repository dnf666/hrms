package com.facishare.crm.outbounddeliverynote.predefine.manager;

import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteErrorCode;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteException;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.StockCalculateManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/20
 */
@Service
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteStockManager {
    @Resource
    private StockManager stockManager;

    @Resource
    private StockCalculateManager stockCalculateManager;

    public void checkAvailableStock(User user, IObjectData note, final List<IObjectData> productList) {
        List<String> productIds = productList.stream().map(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class)).collect(Collectors.toList());
        String warehouseId = note.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class);

        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, BigDecimal> productAmountMap = productList.stream()
                .collect(Collectors.toMap(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class),
                        product -> product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class)));

        stockManager.checkAvailableStock(user, stocks, productAmountMap, warehouseId);
    }

    public void batchCheckAvailableStock(User user, Map<String, Map<String, BigDecimal>> warehouseIdProductAmountMap) {
        List<String> productIds = warehouseIdProductAmountMap.values().stream().flatMap(productMap -> productMap.keySet().stream()).distinct().collect(Collectors.toList());
        List<String> warehouseIds = warehouseIdProductAmountMap.keySet().stream().collect(Collectors.toList());

        List<IObjectData> stocks = stockManager.queryStocksByWarehouseIdsAndProductIds(user, productIds, warehouseIds);
        //校验可用库存
        stockManager.batchCheckAvailableStock(user, stocks, warehouseIdProductAmountMap, productIds, false);
    }

    public void minusRealStock(User user, IObjectData note, List<IObjectData> productList, StockOperateInfo stockOperateInfo) {
        List<String> productIds = productList.stream().map(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class)).distinct().collect(Collectors.toList());
        String warehouseId = note.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class);

        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, BigDecimal> productAmountMap = productList.stream()
                .collect(Collectors.toMap(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class),
                        product -> product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class)));

        Map<String, IObjectData> stockProductMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName, String.class), stock -> stock));


        if (!stockManager.checkRealStock(user, stocks, productAmountMap)) {
            log.warn("outboundDeliveryNote minusRealStock. realStock is not enough.");
        }

        List<IObjectData> newStocks = productAmountMap.keySet().stream().map(productId ->
            stockCalculateManager.minusReal(user, stockProductMap.get(productId), productAmountMap.get(productId))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, stockOperateInfo, newStocks, productAmountMap, true, "-", false, "");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }
    }

    public void addBlockedStock(User user, IObjectData note, List<IObjectData> productList, boolean checkAvailableStock, StockOperateInfo stockOperateInfo) {
        if (checkAvailableStock) {
            checkAvailableStock(user, note, productList);
        }

        List<String> productIds = productList.stream().map(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class)).distinct().collect(Collectors.toList());
        String warehouseId = note.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class);

        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, BigDecimal> productAmountMap = productList.stream()
                .collect(Collectors.toMap(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class),
                        product -> product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class)));

        Map<String, IObjectData> stockProductMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName, String.class), stock -> stock));


        List<IObjectData> newStocks = productAmountMap.keySet().stream().map(productId ->
                stockCalculateManager.addBlocked(user, stockProductMap.get(productId), productAmountMap.get(productId))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, stockOperateInfo, newStocks, productAmountMap, false, "", true, "");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }
    }

    public void minusBlockedStock(User user, IObjectData note, List<IObjectData> productList, StockOperateInfo stockOperateInfo) {
        List<String> productIds = productList.stream().map(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class)).collect(Collectors.toList());
        String warehouseId = note.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class);

        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, BigDecimal> productAmountMap = productList.stream()
                .collect(Collectors.toMap(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class),
                        product -> product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class)));

        Map<String, IObjectData> stockProductMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName, String.class), stock -> stock));
        List<IObjectData> newStocks = productAmountMap.keySet().stream().map(productId ->
                stockCalculateManager.minusBlocked(user, stockProductMap.get(productId), productAmountMap.get(productId))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, stockOperateInfo, newStocks, productAmountMap, false, "", true, "-");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }
    }

    public void minusBlockedAndRealStock(User user, IObjectData note, List<IObjectData> productList, StockOperateInfo stockOperateInfo) {
        List<String> productIds = productList.stream().map(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class)).collect(Collectors.toList());
        String warehouseId = note.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class);

        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, BigDecimal> productAmountMap = productList.stream()
                .collect(Collectors.toMap(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class),
                        product -> product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class)));

        Map<String, IObjectData> stockProductMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName, String.class), stock -> stock));

        if (!stockManager.checkRealStock(user, stocks, productAmountMap)) {
            log.warn("outboundDeliveryNote minusBlockedAndRealStock. realStock is not enough.");
        }

        List<IObjectData> newStocks = productAmountMap.keySet().stream().map(productId ->
                stockCalculateManager.minusBlockReal(user, stockProductMap.get(productId), productAmountMap.get(productId))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, stockOperateInfo, newStocks, productAmountMap, true, "-", true, "-");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }

    }

    public void addRealStock(User user, IObjectData note, Map<String, BigDecimal> productAmountMap, StockOperateInfo stockOperateInfo) {
        List<String> productIds = productAmountMap.keySet().stream().collect(Collectors.toList());
        String warehouseId = note.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class);

        List<IObjectData> stocks = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        Map<String, IObjectData> stockProductMap = stocks.stream().collect(Collectors.toMap(stock -> stock.get(StockConstants.Field.Product.apiName, String.class), stock -> stock));

        List<IObjectData> newStocks = productAmountMap.keySet().stream().map(productId ->
                stockCalculateManager.addReal(user, stockProductMap.get(productId), productAmountMap.get(productId))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newStocks)) {
            List<StockLogDO> stockLogDOs = buildStockLog(user, stockOperateInfo, newStocks, productAmountMap, true, "", false, "");
            stockManager.batchUpdate(user, newStocks, stockLogDOs);
        }
    }

    public void checkProducts(final List<IObjectData> productList) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(productList)) {
            log.warn("checkProducts failed. productList[{}]", productList);
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "请选择出库单产品");
        }

        if (productList.stream().anyMatch(product ->
                BigDecimal.ZERO.compareTo(product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class)) >= 0)) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "出库数量必须大于0");
        }
    }

    private List<StockLogDO> buildStockLog(User user, StockOperateInfo info, List<IObjectData> newStocks, Map<String, BigDecimal> productAmountMap,
                                           boolean isModifiedReal, String modifiedRealType, boolean isModifiedBlocked, String modifiedBlockedType) {
        List<StockLogDO> stockLogDOs = newStocks.stream().map(newStock -> {
            StockLogDO stockLogDO = StockLogDO.buildLog(user, newStock, info);
            if (isModifiedReal) {
                stockLogDO.setModifiedRealStockNum(modifiedRealType + productAmountMap.get(stockLogDO.getProductId()));
            }
            if (isModifiedBlocked) {
                stockLogDO.setModifiedBlockedStockNum(modifiedBlockedType + productAmountMap.get(stockLogDO.getProductId()));
            }
            return stockLogDO;
        }).collect(Collectors.toList());
        return stockLogDOs;
    }
}
