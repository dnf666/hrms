package com.facishare.crm.sfa.predefine.service;

import com.facishare.paas.metadata.api.IObjectData;

import java.util.List;
import java.util.Set;

/**
 * Created by luxin on 2018/5/2.
 * 预制的老对象根据对象的名字查询对象id信息
 */
public interface PredefinedObjSearchService {

    String getApiName();

    List<IObjectData> getObjectDataList(String tenantId, String userId, String name, String accountId, boolean isFuzzySearch);

    List<IObjectData> getObjectDataListByObjectNames(String tenantId, Set<String> names);

    List<IObjectData> getObjectDataListByObjectIds(String tenantId, Set<String> objectIds);

    default String getSearchConnectKey(boolean isFuzzySearch) {

        return isFuzzySearch ? " like '%%%s%%' " : " = '%s' ";
    }

    default String getLimitCondition(boolean isFuzzySearch) {

        return isFuzzySearch ? " limit 10" : " limit 1";
    }
}
