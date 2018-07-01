package com.facishare.crm.customeraccount.predefine.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.service.CommonService;
import com.facishare.crm.customeraccount.predefine.service.RebateOutcomeDetailService;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.OrderBy;

/**
 *
 */
@Component
public class RebateOutcomeDetailServiceImpl extends CommonService implements RebateOutcomeDetailService {
    @Autowired
    private CustomerAccountManager customerAccountManager;

    @Override
    public ListByIdModel.Result listByRebateIncomeId(ListByIdModel.RebateOutcomeArg arg, ServiceContext serviceContext) {

        List<IFilter> list = new ArrayList<>();
        List<OrderBy> orderByList = new ArrayList<>();
        SearchUtil.fillFilterEq(list, RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, arg.getId());
        SearchUtil.fillOrderBy(orderByList, SystemConstants.Field.CreateTime.apiName, false);
        QueryResult<IObjectData> queryResult = customerAccountManager.searchQuery(serviceContext.getUser(), RebateOutcomeDetailConstants.API_NAME, list, orderByList, arg.getOffset(), arg.getLimit());
        List<IObjectData> objectDataList = queryResult.getData();
        List<IObjectData> orderPaymentList = null;
        if (CollectionUtils.isNotEmpty(objectDataList)) {
            if (objectDataList.get(0).get(RebateOutcomeDetailConstants.Field.OrderPayment.apiName, String.class) != null) {
                List<String> orderPaymentIds = objectDataList.stream().map(o -> ObjectDataUtil.getReferenceId(o, RebateOutcomeDetailConstants.Field.OrderPayment.apiName)).collect(Collectors.toList());
                list.clear();
                SearchUtil.fillFilterIn(list, "_id", orderPaymentIds);
                orderPaymentList = customerAccountManager.searchQuery(serviceContext.getUser(), "OrderPaymentObj", list, orderByList, 0, 100).getData();
            }
        }
        if (orderPaymentList != null) {
            for (IObjectData objectData : objectDataList) {
                String orderPaymentId = ObjectDataUtil.getReferenceId(objectData, RebateOutcomeDetailConstants.Field.OrderPayment.apiName);
                for (IObjectData orderPaymentData : orderPaymentList) {
                    String id = orderPaymentData.getId();
                    if (id.equals(orderPaymentId)) {
                        String name = orderPaymentData.getName();
                        objectData.set("payment_code", name);
                        continue;
                    }
                }
            }
        }
        ListByIdModel.Result result = new ListByIdModel.Result();
        result.setObjectDatas(ObjectDataDocument.ofList(queryResult.getData()));
        result.setTotalNumber(queryResult.getTotalNumber());
        result.setPageNumber(arg.getPageNumber());
        result.setPageSize(arg.getPageSize());
        return result;
    }

}
