package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;

import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.ui.layout.ILayout;

import java.util.List;

@SuppressWarnings("Duplicates")
public class PriceBookListController extends StandardListController {
    @Override
    protected Result doService(Arg arg) {
        List<ILayout> layouts = findMobileLayouts();
        for (ILayout layout : layouts) {
            layout.set("buttons", Lists.newArrayList());
        }
        SearchTemplateQuery query = buildSearchTemplateQuery();
        QueryResult<IObjectData> queryResult = getQueryResult(query);
        return buildResult(layouts, query, queryResult);
    }
}
