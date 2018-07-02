package com.facishare.crm.stock.predefine.controller;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
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
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author linchf
 * @date 2018/3/28
 */
@Slf4j(topic = "stockAccess")
public class WareHouseFullListController extends WareHouseListController {

    @Override
    protected Result doService(Arg arg) {
        Result r = super.doService(arg);
        if (null != r && CollectionUtils.isNotEmpty(r.getDataList())) {
            r.getDataList().forEach(d ->
                    fillWithDetails(controllerContext.getRequestContext(),
                            controllerContext.getObjectApiName(), d));
        }
        return r;
    }

    @Override
    protected QueryResult<IObjectData> findData(SearchTemplateQuery query) {
        IFilter filter = new Filter();
        filter.setFieldName(IObjectData.IS_DELETED);
        filter.setOperator(Operator.IN);
        filter.setFieldValues(Lists.newArrayList(String.valueOf(DELETE_STATUS.NORMAL.getValue()), String.valueOf(DELETE_STATUS.INVALID.getValue())));
        query.addFilters(Lists.newArrayList(filter));
        return serviceFacade
                .findBySearchQueryWithDeleted(controllerContext.getUser(), objectDescribe, query);
    }

    private ObjectDataDocument fillWithDetails(RequestContext context, String describeApiName,
                                              ObjectDataDocument data) {
        List<IObjectDescribe> detailDescribeList = serviceFacade
                .findDetailDescribes(context.getTenantId(), describeApiName);
        Map<String, List<IObjectData>> details = serviceFacade
                .findDetailObjectDataList(detailDescribeList, data.toObjectData(), context.getUser());
        data.put("details", details);
        return data;
    }
}
