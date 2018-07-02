package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.predefine.dao.BuyRecordDAO;
import com.facishare.crm.electronicsign.predefine.model.BuyRecordDO;
import com.facishare.crm.electronicsign.predefine.model.vo.BuyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author dailf
 */
@Repository
public class BuyRecordDAOImpl extends BaseDaoImpl<BuyRecordDO> implements BuyRecordDAO {
    @Autowired
    public BuyRecordDAOImpl(Datastore electronicSignDataStore) {
        super(electronicSignDataStore, BuyRecordDO.class);
    }




    @Override
    public List<BuyRecordDO> getTenantBuyQuotaRecordsByConditionAndPage(BuyRecordDO buyRecordDO, Long startTime, Long endTime, Pager<BuyRecordVO> pager) {
        //主要逻辑
        Query<BuyRecordDO> query = createQuery(buyRecordDO);
        if (startTime != null) {
            query.criteria("buyTime").greaterThanOrEq(startTime);
        }
        if (endTime != null){
             query.criteria("buyTime").lessThanOrEq(endTime);
        }
        query.order("-buyTime");
        query = query.offset(pager.offset()).limit(pager.getPageSize());
        return query.asList();
    }

    @Override
    public int getTenantBuyQuotaRecordsByConditionCount(BuyRecordDO buyRecordDO, Long startTime, Long endTime) {
        Query<BuyRecordDO> query = createQuery(buyRecordDO);
        if (startTime != null) {
            query.criteria("buyTime").greaterThanOrEq(startTime);
        }
        if (endTime != null){
            query.criteria("buyTime").lessThanOrEq(endTime);
        }
        return (int)query.countAll();
    }
}
