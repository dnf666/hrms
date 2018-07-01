package com.facishare.crm.customeraccount.predefine.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.ActionContext;
import com.facishare.paas.metadata.api.condition.TermConditions;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.dispatcher.ObjectDataProxy;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchQuery;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.impl.search.Where;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonManager {
    @Autowired
    protected ServiceFacade serviceFacade;
    @Autowired
    protected CrmRestApi crmRequestApi;
    @Autowired
    private ObjectDataProxy objectDataProxy;

    protected IObjectData fillDefaultObject(User user, IObjectDescribe objectDescribe, IObjectData objectData) {
        //默认信息
        objectData.setTenantId(user.getTenantId());
        objectData.setCreatedBy(user.getUserId());
        objectData.setLastModifiedBy(user.getUserId());
        objectData.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
        //业务类型
        objectData.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        objectData.set(IObjectData.DESCRIBE_ID, objectDescribe.getId());
        objectData.set(IObjectData.DESCRIBE_API_NAME, objectDescribe.getApiName());
        objectData.set(IObjectData.PACKAGE, "CRM");
        objectData.set(IObjectData.VERSION, objectDescribe.getVersion());
        //相关团队
        ObjectDataExt objectDataExt = ObjectDataExt.of(objectData);
        TeamMember teamMember = new TeamMember(user.getUserId(), TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);
        objectDataExt.setTeamMembers(Lists.newArrayList(teamMember));
        return objectDataExt.getObjectData();
    }

    protected void recordLog(User user, IObjectData objectData) {
        try {
            IObjectDescribe desc = serviceFacade.findObject(user.getTenantId(), objectData.getDescribeApiName());
            serviceFacade.log(user, EventType.MODIFY, ActionType.Modify, desc, objectData);
        } catch (Exception e) {
            log.info("objectData=" + objectData, e);
        }
    }

    protected String getDescribeId(String tenantId, String apiName) {
        IObjectDescribe desc = serviceFacade.findObject(tenantId, apiName);
        if (desc == null) {
            return null;
        }
        return desc.getId();
    }

    //    public QueryCustomersByPage.Result queryCustomers(User user, int offset, int limit) {
    //        QueryCustomersByPage.Arg arg = new QueryCustomersByPage.Arg();
    //        arg.setOffset(offset);
    //        arg.setLimit(limit);
    //        Map<String, String> headers = getHeaders(user.getTenantId(), user.getUserId());
    //        QueryCustomersByPage.Result queryCustomersByPageResult = crmRequestApi.queryCustomersByPage(arg, headers);
    //        return queryCustomersByPageResult;
    //    }

    public Map<String, String> getHeaders(String tenantId, String userId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        headers.put("Expect", "100-continue");
        return headers;
    }

    public QueryResult<IObjectData> searchQuery(User user, String objectApiName, List<IFilter> filters, List<OrderBy> orders, int offset, int limit) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setOrders(orders);
        searchTemplateQuery.setWheres(Lists.newArrayList());
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, objectApiName, searchTemplateQuery);
        return queryResult;
    }

    public QueryResult<IObjectData> queryByField(User user, String objectApiName, String fieldApiName, String fieldValue, int offset, int limit) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        IFilter filter = new Filter();
        filter.setOperator(Operator.EQ);
        filter.setFieldName(fieldApiName);
        filter.setFieldValues(Lists.newArrayList(fieldValue));
        filter.setConnector(Where.CONN.OR.toString());
        searchTemplateQuery.setFilters(Lists.newArrayList(filter));

        searchTemplateQuery.setWheres(new ArrayList<>());
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, objectApiName, searchTemplateQuery);
        return queryResult;
    }

    /**
     * 插叙作废的数据(作废的状态为 is_deleted=0)
     * @param user
     * @param objectApiName
     * @param fieldApiName
     * @param fieldValue
     * @return
     */
    public IObjectData getDeletedObjByField(User user, String objectApiName, String fieldApiName, String fieldValue) {
        TermConditions condition = new TermConditions();
        condition.addCondition(fieldApiName, fieldValue);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addCondition(condition);
        QueryResult<IObjectData> result = serviceFacade.findBySearchQuery(user, objectApiName, searchQuery);
        if (result.getData() != null && !result.getData().isEmpty()) {
            if (result.getData().size() == 1) {
                return result.getData().get(0);
            } else if (result.getData().size() > 1) {
                log.warn("find more  object for ,objectApiName={},fieldApiName={},fieldValue={},result={}", objectApiName, fieldApiName, fieldValue, result);
                return result.getData().get(0);
            }
        }
        return null;
    }

    public QueryResult<IObjectData> queryByFieldList(User user, String objectApiName, String fieldApiName, List<String> fieldValues, int offset, int limit) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        IFilter filter = new Filter();
        filter.setOperator(Operator.IN);
        filter.setFieldName(fieldApiName);
        filter.setFieldValues(fieldValues);
        filter.setConnector(Where.CONN.OR.toString());
        searchTemplateQuery.setFilters(Lists.newArrayList(filter));

        searchTemplateQuery.setWheres(new ArrayList<>());
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, objectApiName, searchTemplateQuery);
        return queryResult;
    }

    public QueryResult<IObjectData> queryByFieldFilterList(User user, String objectApiName, List<IFilter> filterList, int offset, int limit) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        searchTemplateQuery.setFilters(filterList);
        searchTemplateQuery.setWheres(new ArrayList<>());
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, objectApiName, searchTemplateQuery);
        return queryResult;
    }

    protected List<IObjectData> queryByFieldFilterList(User user, String objectApiName, List<IFilter> filterList, List<OrderBy> orders, int offset, int limit) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        searchTemplateQuery.setFilters(filterList);
        searchTemplateQuery.setWheres(new ArrayList<>());
        searchTemplateQuery.setOrders(orders);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, objectApiName, searchTemplateQuery);
        return queryResult.getData();
    }

    public QueryResult<IObjectData> queryInvalidDataByField(User user, String objectApiName, String fieldApiName, List<String> fieldValues, int offset, int limit) {
        ActionContext actionContext = new ActionContext();
        actionContext.setEnterpriseId(user.getTenantId());
        actionContext.setUserId(user.getUserId());
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        List<IFilter> filters = Lists.newArrayList();
        IFilter filter = new Filter();
        filter.setOperator(Operator.IN);
        filter.setFieldName(fieldApiName);
        filter.setFieldValues(fieldValues);
        filters.add(filter);
        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        try {
            QueryResult<IObjectData> result = objectDataProxy.findBySearchQuery(user.getTenantId(), objectApiName, searchTemplateQuery, actionContext);
            return result;
        } catch (MetadataServiceException e) {
            log.warn("queryInvalidDataByField user:{},objectApiName:{},fieldApiName:{},fieldValues:{},offset:{},limit:{}", user, objectApiName, fieldApiName, fieldValues, offset, limit, e);
            throw new CustomerAccountBusinessException(CustomerAccountErrorCode.METADATA_QUERY_ERROR, e.getMessage());
        }
    }
}
