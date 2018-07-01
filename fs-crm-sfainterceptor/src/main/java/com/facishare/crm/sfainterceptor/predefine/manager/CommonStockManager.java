package com.facishare.crm.sfainterceptor.predefine.manager;

import com.facishare.crm.stock.dao.StockLogDAO;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/23
 */
public class CommonStockManager {

    //保存库存修改记录
    protected List<StockLogDO> buildStockLog(User user, StockOperateInfo info, List<IObjectData> newStocks, Map<String, BigDecimal> productAmountMap,
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
