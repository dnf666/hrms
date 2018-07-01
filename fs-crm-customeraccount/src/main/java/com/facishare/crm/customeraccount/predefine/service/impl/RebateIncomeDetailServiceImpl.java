package com.facishare.crm.customeraccount.predefine.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.service.CommonService;
import com.facishare.crm.customeraccount.predefine.service.RebateIncomeDetailService;
import com.facishare.crm.customeraccount.predefine.service.dto.CreateModel;
import com.facishare.crm.customeraccount.predefine.service.dto.EditModel;
import com.facishare.crm.customeraccount.predefine.service.dto.GetByRefundIdModel;
import com.facishare.crm.customeraccount.predefine.service.dto.ListByIdModel;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
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
public class RebateIncomeDetailServiceImpl extends CommonService implements RebateIncomeDetailService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CustomerAccountManager customerAccountManager;

    /**
     *  收入（退款（带生命周期状态），手动创建（无生命周期状态））
     *  若退款有流程，交易明细lifeStatus为underReview;否则，交易明细lifeStatus为normal
     */
    @Override
    public CreateModel.Result create(CreateModel.Arg arg, ServiceContext serviceContext) {
        arg.getObjectData().put(SystemConstants.ObjectDescribeApiName, RebateIncomeDetailConstants.API_NAME);
        IObjectDescribe describe = serviceFacade.findObject(serviceContext.getTenantId(), RebateIncomeDetailConstants.API_NAME);
        arg.getObjectData().put(SystemConstants.ObjectDescribeId, describe.getId());
        ObjectDataDocument newObjectData = this.triggerAddAction(serviceContext, RebateIncomeDetailConstants.API_NAME, arg.getObjectData());
        CreateModel.Result result = new CreateModel.Result();
        result.setObjectData(newObjectData);
        return result;
    }

    @Override
    public GetByRefundIdModel.Result getByRefundId(GetByRefundIdModel.Arg arg, ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        GetByRefundIdModel.Result result = new GetByRefundIdModel.Result();
        QueryResult<IObjectData> queryResult = customerAccountManager.queryByField(user, RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.Field.Refund.apiName, arg.getRefundId(), 0, 10);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return result;
        }
        result.setObjectData(ObjectDataDocument.of(queryResult.getData().get(0)));
        return result;
    }

    @Override
    public ListByIdModel.Result listByCustomerId(ListByIdModel.RebateArg arg, ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        ListByIdModel.Result result = new ListByIdModel.Result();
        List<IFilter> filterList = new ArrayList();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(arg.getId())) {
            SearchUtil.fillFilterEq(filterList, RebateIncomeDetailConstants.Field.Customer.apiName, arg.getId());
        }
        if (StringUtils.isNotBlank(arg.getLifeStatus())) {
            if (SystemConstants.LifeStatus.UnderReview.value.equals(arg.getLifeStatus())) {
                SearchUtil.fillFilterIn(filterList, SystemConstants.Field.LifeStatus.apiName, Lists.newArrayList(SystemConstants.LifeStatus.UnderReview.value, SystemConstants.LifeStatus.InChange.value));
            } else {
                SearchUtil.fillFilterEq(filterList, SystemConstants.Field.LifeStatus.apiName, arg.getLifeStatus());
            }
        }
        if (StringUtils.isNotEmpty(arg.getIncomeType())) {
            if ("other".equals(arg.getIncomeType())) {
                SearchUtil.fillFilterStartWith(filterList, RebateIncomeDetailConstants.Field.IncomeType.apiName, arg.getIncomeType());
            } else {
                SearchUtil.fillFilterEq(filterList, RebateIncomeDetailConstants.Field.IncomeType.apiName, arg.getIncomeType());
            }
        }

        if (StringUtils.isNotBlank(arg.getName())) {
            SearchUtil.fillFilterLike(filterList, RebateIncomeDetailConstants.Field.Name.apiName, arg.getName());
        }
        List<OrderBy> orderByList = new ArrayList<>();
        SearchUtil.fillOrderBy(orderByList, SystemConstants.Field.CreateTime.apiName, false);
        if (arg.getCreateTime() != null) {
            SearchUtil.fillFilterGTE(filterList, SystemConstants.Field.CreateTime.apiName, arg.getCreateTime());
            SearchUtil.fillFilterLT(filterList, SystemConstants.Field.CreateTime.apiName, arg.getCreateTimeEnd());
        }
        QueryResult<IObjectData> queryResult = customerAccountManager.searchQuery(user, RebateIncomeDetailConstants.API_NAME, filterList, orderByList, arg.getOffset(), arg.getLimit());
        result.setObjectDatas(ObjectDataDocument.ofList(queryResult.getData()));
        result.setTotalNumber(queryResult.getTotalNumber());
        result.setPageNumber(arg.getPageNumber());
        result.setPageSize(arg.getPageSize());
        Integer totalPage = queryResult.getTotalNumber() % arg.getPageSize() == 0 ? queryResult.getTotalNumber() / arg.getPageSize() : queryResult.getTotalNumber() / arg.getPageSize() + 1;
        result.setTotalPage(totalPage);
        return result;
    }

    @Override
    public ListByIdModel.Result listByCustomerIdCompatible(ListByIdModel.RebateArg arg, ServiceContext serviceContext) {
        return listByCustomerId(arg, serviceContext);
    }

    @Override
    public EditModel.Result update(EditModel.Arg arg, ServiceContext serviceContext) {
        //退款方调用编辑,可以修改时间（起止时间，交易时间），金额（未绑定支出前提）
        IObjectData objectData = ObjectDataExt.of(arg.getObjectData()).getObjectData();
        String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        String refundId = ObjectDataUtil.getReferenceId(objectData, RebateIncomeDetailConstants.Field.Refund.apiName);//objectData.get(RebateIncomeDetailConstants.Field.Refund.apiName, String.class);
        Double use = objectData.get(RebateIncomeDetailConstants.Field.UsedRebate.apiName, Double.class);
        Double available = objectData.get(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, Double.class);
        if (use != null || available != null) {
            throw new ValidateException("不能修改可用金额或者已用金额");
        }
        if (StringUtils.isEmpty(lifeStatus) || StringUtils.isEmpty(refundId)) {
            throw new ValidateException("生命周期状态和退款id不能为空");
        }
        GetByRefundIdModel.Arg arg1 = new GetByRefundIdModel.Arg();
        arg1.setRefundId(refundId);
        IObjectData db = getByRefundId(arg1, serviceContext).getObjectData().toObjectData();
        objectData.setId(db.getId());
        objectData.setDescribeApiName(db.getDescribeApiName());
        objectData.setDescribeId(db.getDescribeId());
        ObjectDataDocument newObjectData = this.triggerEditAction(serviceContext, RebateIncomeDetailConstants.API_NAME, arg.getObjectData());
        EditModel.Result result1 = new EditModel.Result();
        result1.setObjectData(newObjectData);
        return result1;

    }
}
