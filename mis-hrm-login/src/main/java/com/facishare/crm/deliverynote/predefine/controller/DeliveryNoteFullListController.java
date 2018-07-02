package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.paas.metadata.api.DELETE_STATUS;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

public class DeliveryNoteFullListController extends DeliveryNoteListController {

    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext()
            .getBean(DeliveryNoteManager.class);

    @Override
    protected Result doService(Arg arg) {
        Result r = super.doService(arg);
        if (null != r && CollectionUtils.isNotEmpty(r.getDataList())) {
            r.getDataList().forEach(d -> deliveryNoteManager
                    .fillWithDetails(controllerContext.getRequestContext(),
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
}
