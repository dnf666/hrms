package com.facishare.crm.payment.controller;

import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

public class CustomerPaymentApproveListController extends CustomerPaymentListController {

  @Override
  protected SearchTemplateQuery buildSearchTemplateQuery() {
    SearchTemplateQuery query = super.buildSearchTemplateQuery();
    if(CollectionUtils.isNotEmpty(query.getFilters())) {
      Filter filter = new Filter();
      filter.setFieldName("approve_employee_id");
      filter.setConnector("AND");
      filter.setFieldValues(Lists.newArrayList(controllerContext.getUser().getUserId()));
      filter.setOperator(Operator.CONTAINS);
      query.addFilters(Lists.newArrayList(filter));
    }
    return query;
  }
}
