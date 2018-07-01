package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

/**
 * Created by luxin on 2018/3/12.
 */
public class ProductRelatedListController extends StandardRelatedListController {
    @Override
    protected QueryResult<IObjectData> findData(SearchTemplateQuery query) {
        return this.serviceFacade.findBySearchQuery(getActionContext(), this.objectDescribe.getApiName(), query);
    }

    private IActionContext getActionContext() {
        IActionContext context;
        // 当目标对象是 商机时候做特殊处理,走rest
        if(Utils.OPPORTUNITY_API_NAME.equals(arg.getTargetObjectApiName())){
            context= ActionContextExt.of(this.controllerContext.getUser()).dbType(IActionContext.DBTYPE_REST).getContext();
        }else{
             context = ActionContextExt.of(this.controllerContext.getUser()).pgDbType().getContext();
        }
        return context;
    }

}
