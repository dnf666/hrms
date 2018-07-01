package com.facishare.crm.sfa.predefine.controller;

import com.google.common.base.Strings;

import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.appframework.metadata.SearchTemplateQueryExt;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

/**
 * @author cqx
 * @date 2018/5/3 20:54
 */
public class OpportunityRelatedListController extends StandardRelatedListController {
    protected void modifyQueryByRefFieldName(SearchTemplateQuery query) {

        if (!Strings.isNullOrEmpty(((StandardRelatedListController.Arg) this.arg).getTargetObjectDataId()) && !Strings.isNullOrEmpty(((StandardRelatedListController.Arg) this.arg).getTargetObjectApiName()) && "ContactObj".equals(((StandardRelatedListController.Arg) this.arg).getTargetObjectApiName())) {
            SearchTemplateQueryExt.of(query).addFilter(Operator.EQ, "ContactID", ((StandardRelatedListController.Arg) this.arg).getTargetObjectDataId());
        } else {
            super.modifyQueryByRefFieldName(query);
        }
    };
}
