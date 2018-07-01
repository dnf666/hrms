package com.facishare.crm.sfa.predefine.service;

import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.exception.APPException;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * Created by luxin on 2018/5/2.
 */
public abstract class AbstractPredefinedObjSearchService implements PredefinedObjSearchService {
    @Autowired
    private ObjectDataServiceImpl objectDataService;

    @Override
    public List<IObjectData> getObjectDataList(String tenantId, String userId, String name, String accountId, boolean isFuzzySearch) {
        String sql = getSearchSql(tenantId, name, accountId, isFuzzySearch);

        return getObjectDataListBySql(tenantId, sql);
    }


    @Override
    public List<IObjectData> getObjectDataListByObjectNames(String tenantId, Set<String> names) {
        if (CollectionUtils.notEmpty(names)) {

            // TODO: 2018/5/4 防止sql注入
            String sql = getNamesAccurateSearchSql(tenantId, names);
            return getObjectDataListBySql(tenantId, sql);
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }

    @Override
    public List<IObjectData> getObjectDataListByObjectIds(String tenantId, Set<String> objectIds) {
        if (CollectionUtils.notEmpty(objectIds)) {

            // TODO: 2018/5/4 防止sql注入
            String sql = findByIdsSql(tenantId, objectIds);
            return getObjectDataListBySql(tenantId, sql);
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }

    private List<IObjectData> getObjectDataListBySql(String tenantId, String sql) {
        if (sql != null) {
            QueryResult<IObjectData> queryResult;
            try {
                queryResult = objectDataService.findBySql(sql, tenantId, getApiName());
            } catch (MetadataServiceException e) {
                throw new APPException("元数据异常", e);
            }
            return queryResult.getData();
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }


    protected abstract String getSearchSql(String tenantId, String name, String accountId, boolean isFuzzySearch);

    protected abstract String getNamesAccurateSearchSql(String tenantId, Set<String> names);

    protected abstract String findByIdsSql(String tenantId, Set<String> objectIds);

}
