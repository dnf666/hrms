package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.describebuilder.FieldSectionBuilder;
import com.facishare.crm.electronicsign.predefine.dao.FsQuotaDAO;
import com.facishare.crm.electronicsign.predefine.model.FsQuotaDO;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author dailf
 */
@Slf4j
@Repository
public class FsQuotaDAOImpl extends BaseDaoImpl<FsQuotaDO> implements FsQuotaDAO {
    @Autowired
    public FsQuotaDAOImpl(Datastore electronicSignDataStore) {
        super(electronicSignDataStore, FsQuotaDO.class);
    }

    @Override
    public FsQuotaDO getFsQuota() {
        Query<FsQuotaDO> fsQuotaDOQuery = createQuery();
        if (fsQuotaDOQuery.countAll() == 0L) {
            FsQuotaDO fsQuotaDO = new FsQuotaDO();
            fsQuotaDO.setSaleEnterpriseQuota(0L);
            fsQuotaDO.setSaleIndividualQuota(0L);
            fsQuotaDO.setSaleMoney(0L);
            fsQuotaDO.setCreateTime(System.currentTimeMillis());
            fsQuotaDO.setUpdateTime(System.currentTimeMillis());
            save(fsQuotaDO);
            getFsQuota();
        }
        return fsQuotaDOQuery.get();

    }

    @Override
    public int updateSaleEnterpriseQuota(int buyQuota, Long payMoney) {
        Query<FsQuotaDO> query = createQuery();
        UpdateOperations<FsQuotaDO> updateOperations = createUpdateOperations().inc("saleEnterpriseQuota", buyQuota).set("updateTime", System.currentTimeMillis()).inc("saleMoney", payMoney);
        return getDatastore().update(query, updateOperations).getUpdatedCount();
    }

    @Override
    public int updateSaleIndividualQuota(int buyQuota, Long payMoney) {
        //不用先查询，记录肯定存在
        Query<FsQuotaDO> query = createQuery();
        UpdateOperations<FsQuotaDO> updateOperations = createUpdateOperations().inc("saleIndividualQuota", buyQuota).set("updateTime", System.currentTimeMillis()).inc("saleMoney", payMoney);
        return getDatastore().update(query, updateOperations).getUpdatedCount();
    }

}