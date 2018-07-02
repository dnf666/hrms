package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.predefine.dao.SignRequestDAO;
import com.facishare.crm.electronicsign.predefine.model.SignRequestDO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class SignRequestDAOImpl extends BaseDaoImpl<SignRequestDO> implements SignRequestDAO {

    @Autowired
    public SignRequestDAOImpl(Datastore electronicSignDataStore) {
        super(electronicSignDataStore, SignRequestDO.class);
    }

    @Override
    public SignRequestDO queryByContractId(String contractId) {
        Query<SignRequestDO> query = createQuery();
        query.criteria("contractId").equal(contractId);
        return query.get();
    }

    @Override
    public int updateById(String id, SignRequestDO entity) {
        Query<SignRequestDO> query = createQuery();
        query.field("_id").equal(new ObjectId(id));

        final UpdateOperations<SignRequestDO> update = createUpdateOperations();
        boolean hasSet = false;

        if (null != entity.getSigners()) {
            update.set("signers", entity.getSigners());
            hasSet = true;
        }

        if (null != entity.getContractId()) {
            update.set("contractId", entity.getContractId());
            hasSet = true;
        }

        if (null != entity.getContactExpireTime()) {
            update.set("contactExpireTime", entity.getContactExpireTime());
            hasSet = true;
        }

        if (!hasSet) {
            return 0;
        }

        update.set("updateTime", System.currentTimeMillis());

        return getDatastore().update(query, update).getUpdatedCount();
    }
}
