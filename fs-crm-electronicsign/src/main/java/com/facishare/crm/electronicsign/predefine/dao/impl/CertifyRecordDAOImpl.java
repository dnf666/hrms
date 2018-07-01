package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.predefine.dao.CertifyRecordDAO;
import com.facishare.crm.electronicsign.predefine.model.CertifyRecordDO;
import org.bson.types.ObjectId;
import com.facishare.crm.electronicsign.predefine.model.vo.CertifyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CertifyRecordDAOImpl extends BaseDaoImpl<CertifyRecordDO> implements CertifyRecordDAO {

    @Autowired
    public CertifyRecordDAOImpl(Datastore electronicSignDataStore) {
        super(electronicSignDataStore, CertifyRecordDO.class);
    }

    @Override
    public List<CertifyRecordDO> getCertifyRecordByConditionAndPage(CertifyRecordDO certifyRecordDO, Long startTime, Long endTime, Pager<CertifyRecordVO> pager) {
        Query<CertifyRecordDO> query = createQuery(certifyRecordDO);

        if (startTime != null) {
            query.criteria("createTime").greaterThanOrEq(startTime);
        }
        if (endTime != null) {
            query.criteria("createTime").lessThanOrEq(endTime);
        }
        query.order("-createTime");
        query = query.offset(pager.offset()).limit(pager.getPageSize());
        return query.asList();
    }

    @Override
    public int getCertifyRecordByConditionCount(CertifyRecordDO certifyRecordDO, Long startTime, Long endTime) {
        Query<CertifyRecordDO> query = createQuery(certifyRecordDO);

        if (startTime != null) {
            query.criteria("createTime").greaterThanOrEq(startTime);
        }
        if (endTime != null) {
            query.criteria("createTime").lessThanOrEq(endTime);
        }
        return (int) query.countAll();
    }

    @Override
    public CertifyRecordDO queryByTaskId(String taskId) {
        Query<CertifyRecordDO> query = createQuery();
        query.criteria("taskId").equal(taskId);
        return query.get();
    }

    @Override
    public int updateById(String id, CertifyRecordDO entity) {
        Query<CertifyRecordDO> query = createQuery();
        query.field("_id").equal(new ObjectId(id));

        final UpdateOperations<CertifyRecordDO> update = createUpdateOperations();
        boolean hasSet = false;

        if (null != entity.getCertifyStatus()) {
            update.set("certifyStatus", entity.getCertifyStatus());
            hasSet = true;
        }
        if (null != entity.getCertifyErrMsg()) {
            update.set("certifyErrMsg", entity.getCertifyErrMsg());
            hasSet = true;
        }

        if (!hasSet) {
            return 0;
        }

        update.set("updateTime", System.currentTimeMillis());

        return getDatastore().update(query, update).getUpdatedCount();
    }
}
