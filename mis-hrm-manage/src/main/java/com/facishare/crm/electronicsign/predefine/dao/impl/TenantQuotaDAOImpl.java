package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.dao.TenantQuotaDAO;
import com.facishare.crm.electronicsign.predefine.model.TenantQuotaDO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.model.vo.TenantQuotaVO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dailf
 */
@Repository
public class TenantQuotaDAOImpl extends BaseDaoImpl<TenantQuotaDO> implements TenantQuotaDAO {
    @Autowired
    public TenantQuotaDAOImpl(Datastore electronicSignDataStore) {
        super(electronicSignDataStore, TenantQuotaDO.class);
    }

    @Override
    public List<TenantQuotaDO> getByTenantId(String tenantId) {
        Query<TenantQuotaDO> query = createQuery();
        query.criteria("tenantId").equal(tenantId);
        return query.asList();
    }

    @Override
    public int updateUsedQuota(String id, Integer usedQuota, Long lastUpdateTime) {
        if (usedQuota == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR, "usedQuota cannot be null");
        }
        Query<TenantQuotaDO> query = createQuery();
        query.field("_id").equal(new ObjectId(id));
        query.field("updateTime").equal(lastUpdateTime);

        final UpdateOperations<TenantQuotaDO> update = createUpdateOperations();
        update.set("usedQuota", usedQuota);
        update.set("updateTime", System.currentTimeMillis());

        return getDatastore().update(query, update).getUpdatedCount();
    }

    @Override
    public List<TenantQuotaDO> getTenantQuotaByQuotaTypeAndPage(TenantQuotaDO tenantQuotaDO, Pager<TenantQuotaVO> pager) {
        Query<TenantQuotaDO> quotaDOS = createQuery(tenantQuotaDO, pager.offset(), pager.getPageSize());
        return quotaDOS.asList();
    }

    @Override
    public int getTenantQuotaByQuotaTypeCounts(TenantQuotaDO tenantQuotaDO) {
        return (int) queryCount(tenantQuotaDO);
    }

    @Override
    public int updateBuyQuotaByTenantIdAndQuotaType(TenantQuotaDO tenantQuotaDO) {
        Integer buyQuota = tenantQuotaDO.getBuyQuota();
        Long payMoney = tenantQuotaDO.getPayMoney();
        tenantQuotaDO.setPayMoney(null);
        tenantQuotaDO.setBuyQuota(null);
        Query<TenantQuotaDO> query = createQuery(tenantQuotaDO);
        UpdateOperations<TenantQuotaDO> updateOperation = createUpdateOperations().set("updateTime", System.currentTimeMillis()).inc("buyQuota", buyQuota).inc("payMoney", payMoney);
        return getDatastore().update(query, updateOperation).getUpdatedCount();

    }

}