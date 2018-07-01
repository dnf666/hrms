package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.constant.CrmPackageObjectConstants;
import com.facishare.crm.payment.utils.FieldUtils;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class CustomerPaymentRemindListController extends CustomerPaymentListController {

  @Override
  protected void before(StandardListController.Arg arg) {
    super.before(arg);
    SearchTemplateQuery query = this.serviceFacade
        .getSearchTemplateQuery(controllerContext.getUser(), objectDescribe,
            arg.getSearchTemplateId(), arg.getSearchQueryInfo());
    List< IFilter > filters = Lists.newArrayList();
    filters.addAll(query.getFilters());
    filters.add(FieldUtils.buildFilter(CrmPackageObjectConstants.FIELD_SYS_APPROVE_EMPLOYEE_ID,
        Arrays.asList(controllerContext.getUser().getUserId()), Operator.EQ, 0));
    query.resetFilters(filters);
    arg.setSearchQueryInfo(JsonUtil.toJson(query));
  }

  @Override
  protected Result doService(Arg arg) {
    return super.doService(arg);
  }
}
