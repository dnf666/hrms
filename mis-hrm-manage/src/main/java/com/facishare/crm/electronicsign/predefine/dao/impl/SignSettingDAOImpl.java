package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.predefine.dao.SignSettingDAO;
import com.facishare.crm.electronicsign.predefine.model.SignSettingDO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class SignSettingDAOImpl extends BaseDaoImpl<SignSettingDO> implements SignSettingDAO {
    @Autowired
    public SignSettingDAOImpl(Datastore electronicSignDataStore) {
        super(electronicSignDataStore, SignSettingDO.class);
    }

    @Override
    public void delete(SignSettingDO condition) {
        this.getDatastore().delete(createQuery(condition));
    }

    @Override
    public void createOrUpdate(SignSettingDO entity) {
        Preconditions.checkNotNull(entity.getAppType());
        Preconditions.checkNotNull(entity.getTenantId());
        Preconditions.checkNotNull(entity.getObjApiName());
        Preconditions.checkNotNull(entity.getIsHasOrder());

        SignSettingDO queryCondition = new SignSettingDO();
        queryCondition.setObjApiName(entity.getObjApiName());
        queryCondition.setAppType(entity.getAppType());
        queryCondition.setTenantId(entity.getTenantId());
        Query<SignSettingDO> query = createQuery(queryCondition);

        UpdateOperations<SignSettingDO> updateOperations = createUpdateOperations();
        updateOperations.set("isHasOrder", entity.getIsHasOrder());
        updateOperations.set("signerSettings", Objects.nonNull(entity.getSignerSettings()) ? entity.getSignerSettings() : Lists.newArrayList());
        long currentTimeMillis = System.currentTimeMillis();
        updateOperations.set("updateTime", currentTimeMillis);
        updateOperations.setOnInsert("createTime", currentTimeMillis);

        this.getDatastore().update(query, updateOperations, true);
    }

}