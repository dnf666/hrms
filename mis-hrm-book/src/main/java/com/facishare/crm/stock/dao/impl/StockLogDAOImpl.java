package com.facishare.crm.stock.dao.impl;

import com.facishare.crm.stock.dao.StockLogDAO;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockLogDO;
import com.facishare.crm.stock.predefine.service.model.QueryStockLogByTemplateModel;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/7
 */
@Slf4j
@Repository
public class StockLogDAOImpl implements StockLogDAO {
    @Resource
    private Datastore stockDataStore;

    @Override
    public List<String> bulkSave(List<StockLogDO> stockLogDOs) {
        Iterator<Key<StockLogDO>> iterator = stockDataStore.save(stockLogDOs).iterator();
        List<String> stockLogDOList = Lists.newArrayList();
        while (iterator.hasNext()) {
            stockLogDOList.add(iterator.next().getId().toString());
        }

        return stockLogDOList;
    }

    @Override
    public void bulkUpdate(List<StockLogDO> stockLogDOs) {
        stockDataStore.save(stockLogDOs);
    }

    @Override
    public List<StockLogDO> queryByIds(List<String> ids) {
        final Query<StockLogDO> query = stockDataStore.createQuery(StockLogDO.class);
        List<ObjectId> idList = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toList());
        query.field("_id").in(idList);
        return query.asList();
    }

    @Override
    public List<StockLogDO> queryBySalesOrderId(String tenantId, String salesOrderId) {
        Query<StockLogDO> query = stockDataStore.createQuery(StockLogDO.class);
        query.criteria("tenantId").equal(tenantId);
        query.criteria("operateObjectId").equal(salesOrderId);
        query.criteria("operateObjectType").equal(StockOperateObjectTypeEnum.SALES_ORDER.value);
        query.criteria("hasFinished").notEqual(YesOrNoEnum.NO.getStatus());
        return query.asList();
    }

    @Override
    public List<StockLogDO> queryByTemplate(QueryStockLogByTemplateModel.StockLogVO template, int limit, int offset) {
        Query<StockLogDO> query = getTemplateQuery(template);
        if (template.getIsModifyTimeDesc()) {
            query.order("-modifiedTime");
        } else {
            query.order("modifiedTime");
        }
        return query.offset(offset).limit(limit).asList();
    }

    @Override
    public Long countByTemplate(QueryStockLogByTemplateModel.StockLogVO template, int limit, int offset) {
        Query<StockLogDO> query = getTemplateQuery(template);
        return query.countAll();
    }


    private Query<StockLogDO> getTemplateQuery(QueryStockLogByTemplateModel.StockLogVO template) {
        Query<StockLogDO> query = stockDataStore.createQuery(StockLogDO.class);
        if (StringUtils.isBlank(template.getTenantId())) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "企业id不能为空");
        }

        query.criteria("tenantId").equal(template.getTenantId());

        if (!StringUtils.isBlank(template.getStockId())) {
            query.criteria("stockId").equal(template.getStockId());
        }

        if (!StringUtils.isBlank(template.getProductId())) {
            query.criteria("productId").equal(template.getProductId());
        }

        if (!StringUtils.isBlank(template.getOperateObjectId())) {
            query.criteria("operateObjectId").equal(template.getOperateObjectId());
        }

        if (template.getOperateObjectType() != null) {
            query.criteria("operateObjectType").equal(template.getOperateObjectType());
        }


        if (template.getIsModifyRealStock()) {
            query.criteria("modifiedRealStockNum").exists();
        }

        if (template.getIsModifyBlockedStock()) {
            query.criteria("modifiedBlockedStockNum").exists();
        }
        return query;
    }
}
