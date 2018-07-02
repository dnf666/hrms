package com.facishare.crm.requisitionnote.predefine.manager;

import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.DELETE_STATUS;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author liangk
 * @date 13/03/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class CommonManager {
    @Resource
    private ServiceFacade serviceFacade;

    public List<IObjectData> findByIds(User user, List<String> objectIds, String objectApiName) {
        List<IObjectData> objectDataList = serviceFacade.findObjectDataByIdsIncludeDeleted(user, objectIds, objectApiName);
        if (objectDataList == null) {
            return Lists.newArrayList();
        }
        return objectDataList;
    }

    public IObjectData findById(User user, String id, String objectApiName) {
        IObjectData objectData = serviceFacade.findObjectDataIncludeDeleted(user, id, objectApiName);
        if (objectData == null) {
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "查询自定义对象数据失败");
        }
        return objectData;
    }

    public List<IObjectData> findDetailObjectDataList(User user, String apiName, IObjectData objectData) {
        IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), apiName);
        List<IObjectData> objectDataList = serviceFacade.findDetailObjectDataList(describe, objectData, user);
        if (Objects.isNull(objectDataList)) {
            return Lists.newArrayList();
        }
        return objectDataList;
    }

    public List<IObjectData> findDetailObjectDataIncludeInvalid(User user, IObjectData objectData) {
        IObjectDescribe objectDescribe = serviceFacade.findObject(user.getTenantId(), RequisitionNoteProductConstants.API_NAME);

        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, RequisitionNoteProductConstants.Field.Requisition.apiName, objectData.getId());

        IFilter filter = new Filter();
        filter.setFieldName(IObjectData.IS_DELETED);
        filter.setOperator(Operator.IN);
        filter.setFieldValues(Lists.newArrayList(String.valueOf(DELETE_STATUS.NORMAL.getValue()), String.valueOf(DELETE_STATUS.INVALID.getValue())));
        filters.add(filter);

        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setPermissionType(0); //0不走权限  1走权限
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQueryWithDeleted(user, objectDescribe, searchTemplateQuery);
        if (queryResult.getData() == null) {
            return Lists.newArrayList();
        }
        return queryResult.getData();
    }
}
