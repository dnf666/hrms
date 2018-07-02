package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.predefine.dao.InternalSignCertifyUseRangeDAO;
import com.facishare.crm.electronicsign.predefine.model.InternalSignCertifyUseRangeDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InternalSignCertifyUseRangeDAOImpl extends BaseDaoImpl<InternalSignCertifyUseRangeDO> implements InternalSignCertifyUseRangeDAO {
    @Autowired
    public InternalSignCertifyUseRangeDAOImpl(Datastore electronicSignDataStore) {
        super(electronicSignDataStore, InternalSignCertifyUseRangeDO.class);
    }

    @Override
    public UpdateResults updateDepartments(InternalSignCertifyUseRangeDO entity) {
        Query<InternalSignCertifyUseRangeDO> query = this.createQuery();
        query.criteria("tenantId").equal(entity.getTenantId());
        query.criteria("internalSignCertifyId").equal(entity.getInternalSignCertifyId());

        UpdateOperations<InternalSignCertifyUseRangeDO> updateOperations = this.createUpdateOperations();
        updateOperations.set("departmentIds", entity.getDepartmentIds());
        updateOperations.set("updateTime", entity.getUpdateTime());

        updateOperations.setOnInsert("tenantId", entity.getTenantId());
        updateOperations.setOnInsert("internalSignCertifyId", entity.getInternalSignCertifyId());
        updateOperations.setOnInsert("bestSignAccount", entity.getBestSignAccount());
        updateOperations.setOnInsert("certifyStatus", entity.getCertifyStatus());
        updateOperations.setOnInsert("useStatus", entity.getUseStatus());
        updateOperations.setOnInsert("createTime", entity.getCreateTime());

        return this.getDatastore().update(query, updateOperations, true);
    }

    @Override
    public int updateByBestSignAccount(String bestSignAccount, InternalSignCertifyUseRangeDO entity, boolean setDepartmentIdsToEmpty) {
        Query<InternalSignCertifyUseRangeDO> query = createQuery();
        query.field("bestSignAccount").equal(bestSignAccount);

        final UpdateOperations<InternalSignCertifyUseRangeDO> update = createUpdateOperations();
        boolean hasSet = false;

        if (null != entity.getCertifyStatus()) {
            update.set("certifyStatus", entity.getCertifyStatus());
            hasSet = true;
        }

        if (null != entity.getUseStatus()) {
            update.set("useStatus", entity.getUseStatus());
            hasSet = true;
        }

        if (setDepartmentIdsToEmpty) {
            update.set("departmentIds", Lists.newArrayList());
            hasSet = true;
        }

        if (!hasSet) {
            return 0;
        }

        update.set("updateTime", System.currentTimeMillis());

        return getDatastore().update(query, update).getUpdatedCount();
    }

    @Override
    public List<InternalSignCertifyUseRangeDO> queryByTenantIdAndDeptIds(String tenantId, List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return new ArrayList<>(0);
        }
        Query<InternalSignCertifyUseRangeDO> query = this.createQuery();
        query.criteria("tenantId").equal(tenantId);
        query.criteria("departmentIds").hasAnyOf(deptIds);
        return query.asList();
    }

    @Override
    public InternalSignCertifyUseRangeDO queryByInternalSignCertifyId(String internalSignCertifyId) {
        Query<InternalSignCertifyUseRangeDO> query = createQuery();
        query.criteria("internalSignCertifyId").equal(internalSignCertifyId);
        return query.get();
    }

    @Override
    public void deleteByInternalSignCertifyId(String internalSignCertifyId) {
        Query<InternalSignCertifyUseRangeDO> query = this.createQuery();
        query.criteria("internalSignCertifyId").equal(internalSignCertifyId);
        this.getDatastore().delete(query);
    }

}
