package com.facishare.crm.customeraccount.predefine.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.service.CommonService;
import com.facishare.crm.customeraccount.predefine.service.PrepayDetailService;
import com.facishare.crm.customeraccount.predefine.service.dto.CreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.EditModel;
import com.facishare.crm.customeraccount.predefine.service.dto.GetByPaymentIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.GetByRefundIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/9/26.
 */
@Slf4j
@Component
public class PrepayDetailServiceImpl extends CommonService implements PrepayDetailService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CustomerAccountManager customerAccountManager;

    /**
     *创建回款/退款同时创建交易明细;
     *若回款/退款有流程，交易明细lifeStatus为underReview;否则，交易明细lifeStatus为normal
     */
    @Override
    public CreateModel.Result create(CreateModel.Arg arg, ServiceContext serviceContext) {
        CreateModel.Result result = new CreateModel.Result();
        IObjectDescribe describe = serviceFacade.findObject(serviceContext.getTenantId(), PrepayDetailConstants.API_NAME);
        IObjectData objectData = arg.getObjectData().toObjectData();
        objectData.setDescribeId(describe.getId());
        arg.setObjectData(ObjectDataDocument.of(objectData));
        ObjectDataDocument doc = this.triggerAddAction(serviceContext, PrepayDetailConstants.API_NAME, arg.getObjectData());
        result.setObjectData(doc);
        return result;
    }

    @Override
    public GetByPaymentIdModel.Result getByPaymentId(GetByPaymentIdModel.Arg arg, ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        GetByPaymentIdModel.Result result = new GetByPaymentIdModel.Result();
        //以前预付款是关联回款，订货通6.2改造后，预付款关联回款明细。但是PrepayDetailService.getBypaymentId为的对外接口，接口协议不方便动，所以直接吧内部改成引用OrderPayment
        QueryResult<IObjectData> queryResult = customerAccountManager.queryByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.OrderPayment.apiName, arg.getPaymentId(), 0, 10);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return result;
        }
        result.setObjectData(ObjectDataDocument.of(queryResult.getData().get(0)));
        return result;
    }

    @Override
    public GetByRefundIdModel.Result getByRefundId(GetByRefundIdModel.Arg arg, ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        GetByRefundIdModel.Result result = new GetByRefundIdModel.Result();
        QueryResult<IObjectData> queryResult = customerAccountManager.queryByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.Refund.apiName, arg.getRefundId(), 0, 10);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return result;
        }
        result.setObjectData(ObjectDataDocument.of(queryResult.getData().get(0)));
        return result;
    }

    @Override
    public ListByIdModel.Result listByCustomerId(ListByIdModel.Arg arg, ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        ListByIdModel.Result result = new ListByIdModel.Result();
        List<IFilter> filterList = new ArrayList();
        if (StringUtils.isNotBlank(arg.getId())) {
            SearchUtil.fillFilterEq(filterList, PrepayDetailConstants.Field.Customer.apiName, arg.getId());
        }
        if (StringUtils.isNotBlank(arg.getLifeStatus())) {
            //如果传under_review状态，需要把待确认的数据都返回（under_review, in_change）
            if (SystemConstants.LifeStatus.UnderReview.value.equals(arg.getLifeStatus())) {
                SearchUtil.fillFilterIn(filterList, SystemConstants.Field.LifeStatus.apiName, Lists.newArrayList(SystemConstants.LifeStatus.UnderReview.value, SystemConstants.LifeStatus.InChange.value));
            } else {
                SearchUtil.fillFilterEq(filterList, SystemConstants.Field.LifeStatus.apiName, arg.getLifeStatus());
            }
        }
        if (StringUtils.isNotBlank(arg.getRecordType())) {
            SearchUtil.fillFilterEq(filterList, SystemConstants.Field.RecordType.apiName, arg.getRecordType());
        }
        if (StringUtils.isNotBlank(arg.getIncomeType())) {
            if ("other".equals(arg.getIncomeType())) {
                SearchUtil.fillFilterStartWith(filterList, PrepayDetailConstants.Field.IncomeType.apiName, arg.getIncomeType());
            } else {
                SearchUtil.fillFilterEq(filterList, PrepayDetailConstants.Field.IncomeType.apiName, arg.getIncomeType());
            }
            //SearchUtil.fillFilterEq(filterList, PrepayDetailConstants.Field.IncomeType.apiName, arg.getIncomeType());
        }
        if (StringUtils.isNotBlank(arg.getOutcomeType())) {
            if ("other".equals(arg.getOutcomeType())) {
                SearchUtil.fillFilterStartWith(filterList, PrepayDetailConstants.Field.OutcomeType.apiName, arg.getOutcomeType());
            } else {
                SearchUtil.fillFilterEq(filterList, PrepayDetailConstants.Field.OutcomeType.apiName, arg.getOutcomeType());
            }
            //SearchUtil.fillFilterEq(filterList, PrepayDetailConstants.Field.OutcomeType.apiName, arg.getOutcomeType());
        }
        if (StringUtils.isNotBlank(arg.getName())) {
            SearchUtil.fillFilterLike(filterList, PrepayDetailConstants.Field.Name.apiName, arg.getName());
        }

        List<OrderBy> orderByList = new ArrayList<>();
        if (arg.getCreateTime() != null) {
            SearchUtil.fillFilterGTE(filterList, SystemConstants.Field.CreateTime.apiName, arg.getCreateTime());
            SearchUtil.fillFilterLT(filterList, SystemConstants.Field.CreateTime.apiName, arg.getCreateTimeEnd());
        }
        SearchUtil.fillOrderBy(orderByList, SystemConstants.Field.CreateTime.apiName, false);
        QueryResult<IObjectData> queryResult = customerAccountManager.searchQuery(user, PrepayDetailConstants.API_NAME, filterList, orderByList, arg.getOffset(), arg.getLimit());

        result.setObjectDatas(ObjectDataDocument.ofList(queryResult.getData()));
        result.setTotalNumber(queryResult.getTotalNumber());
        result.setPageSize(arg.getPageSize());
        result.setPageNumber(arg.getPageNumber());
        Integer totalPage = queryResult.getTotalNumber() % arg.getPageSize() == 0 ? queryResult.getTotalNumber() / arg.getPageSize() : queryResult.getTotalNumber() / arg.getPageSize() + 1;
        result.setTotalPage(totalPage);
        return result;
    }

    @Override
    public ListByIdModel.Result listByCustomerIdCompatible(ListByIdModel.Arg arg, ServiceContext serviceContext) {
        return listByCustomerId(arg, serviceContext);
    }

    public EditModel.Result edit(EditModel.Arg arg, ServiceContext serviceContext) {
        String lifeStatus = (String) arg.getObjectData().get(SystemConstants.Field.LifeStatus.apiName);
        if (StringUtils.isEmpty(lifeStatus)) {
            throw new ValidateException("lifeStatus 不能为空");
        }
        EditModel.Result result = new EditModel.Result();
        ObjectDataDocument data = this.triggerEditAction(serviceContext, PrepayDetailConstants.API_NAME, ObjectDataDocument.of(arg.getObjectData()));
        result.setObjectData(data);
        return result;
    }
}
