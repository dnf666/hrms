package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.stock.dao.StockLogDAO;
import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.model.StockVO;
import com.facishare.crm.stock.predefine.service.model.QueryStockLogByTemplateModel;
import com.facishare.crm.util.BeanUtils;
import com.facishare.open.common.storage.mysql.dao.Pager;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/4/23
 */
@Service
public class StockLogManager {
    @Resource
    private StockLogDAO stockLogDAO;

    public List<StockVO> calculateStockVOsBySalesOrderId(String tenantId, String saleOrderId) {
        List<StockLogDO> stockLogDOs = stockLogDAO.queryBySalesOrderId(tenantId, saleOrderId);
        List<StockVO> stockVOs = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(stockLogDOs)) {
            Map<String, List<StockLogDO>> stockDosMap = stockLogDOs.stream().collect(Collectors.groupingBy(StockLogDO::getProductId));
            stockDosMap.keySet().forEach(productId -> {
                List<StockLogDO> productStockLogDOs = stockDosMap.get(productId);
                if (!CollectionUtils.isEmpty(productStockLogDOs)) {
                    Optional<String> totalBlockedStockOpt = productStockLogDOs.stream().map(StockLogDO::getModifiedBlockedStockNum).reduce((x, y) -> String.valueOf(new BigDecimal(x).add(new BigDecimal(y))));
                    if (totalBlockedStockOpt.isPresent()) {
                        StockVO stockVO = new StockVO();
                        stockVO.setBlockedStock(new BigDecimal(totalBlockedStockOpt.get()));
                        stockVO.setStockId(productStockLogDOs.get(0).getStockId());
                        stockVO.setProductId(productId);
                        stockVOs.add(stockVO);
                    }
                }
            });
        }

        return stockVOs;
    }

    public List<String> bulkSave(List<StockLogDO> stockLogDOs) {
        if (!CollectionUtils.isEmpty(stockLogDOs)) {
            stockLogDOs.forEach(stockLogDO -> stockLogDO.setHasFinished(YesOrNoEnum.NO.getStatus()));
            return stockLogDAO.bulkSave(stockLogDOs);
        }
        return Lists.newArrayList();
    }

    public void bulkUpdate(List<StockLogDO> stockLogDOs) {
        if (!CollectionUtils.isEmpty(stockLogDOs)) {
            stockLogDOs.forEach(stockLogDO -> stockLogDO.setHasFinished(YesOrNoEnum.YES.getStatus()));
            stockLogDAO.bulkUpdate(stockLogDOs);
        }
    }

    public Pager<QueryStockLogByTemplateModel.StockLogVO> queryByPage(Pager<QueryStockLogByTemplateModel.StockLogVO> pager, QueryStockLogByTemplateModel.StockLogVO template) {
        List<StockLogDO> stockLogDOs = stockLogDAO.queryByTemplate(template, pager.getPageSize(), pager.offset());
        List<QueryStockLogByTemplateModel.StockLogVO> stockLogVOs = stockLogDOs.stream().map(stockLogDO -> BeanUtils.copyProperties(QueryStockLogByTemplateModel.StockLogVO.class, stockLogDO)).collect(Collectors.toList());
        if (stockLogVOs == null) {
            stockLogVOs = Lists.newArrayList();
        }
        pager.setData(stockLogVOs);
        Long recordSize = stockLogDAO.countByTemplate(template, pager.getPageSize(), pager.offset());
        pager.setRecordSize(recordSize.intValue());
        return pager;
    }

    List<StockLogDO> queryByIds(List<String> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            return stockLogDAO.queryByIds(ids);
        }
        return Lists.newArrayList();
    }
}
