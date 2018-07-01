package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.facishare.crm.sfa.predefine.service.PriceBookService;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.ui.layout.ILayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PriceBookRelatedListController extends StandardRelatedListController {
    private PriceBookService priceBookService = (PriceBookService) SpringUtil.getContext().getBean("priceBookService");
    private String accountId = null;
    private Boolean isAccountSelect = false;

    protected void doFunPrivilegeCheck() {
        initAccountId();
        //有客户id时，不需要校验功能权限
        if (StringUtils.isBlank(accountId)) {
            super.doFunPrivilegeCheck();
        }
    }

    protected Result doService(StandardRelatedListController.Arg arg) {
        List<ILayout> layouts = this.findMobileLayouts();
        SearchTemplateQuery query = this.buildSearchTemplateQuery();
        QueryResult<IObjectData> queryResult;
        if (StringUtils.isNotBlank(accountId)) {
            queryResult = priceBookService.findPriceBookByAccountId(getControllerContext().getUser(), query, accountId);
        } else {
            query.setLimit(2000);
            queryResult = serviceFacade.findBySearchQuery(getControllerContext().getUser(), PriceBookConstants.API_NAME, query);
        }
        return this.buildResult(layouts, query, queryResult);
    }

    private void initAccountId() {
        if (arg.getObjectData() != null && arg.getObjectData().containsKey("account_id")) {
            accountId = Objects.nonNull(arg.getObjectData().get("account_id")) ? arg.getObjectData().get("account_id").toString() : null;
            isAccountSelect = true;
            // TODO: 2018/3/5 确认是否要验证下面的是否需要
        }/* else if (Utils.ACCOUNT_API_NAME.equals(arg.getTargetObjectApiName())) {
            if (StringUtils.isBlank(arg.getTargetObjectDataId())) {
                throw new ValidateException("参数错误");
            }
            accountId = arg.getTargetObjectDataId();
        }*/
    }

    @Override
    protected void modifyQueryByRefFieldName(SearchTemplateQuery query) {
        if (isAccountSelect) {
            //如果是从客户选择查看
            return;
        }
        super.modifyQueryByRefFieldName(query);
    }

    protected List<ILayout> findMobileLayouts() {
        List<ILayout> mobileLayouts = super.findMobileLayouts();
        for (ILayout layout : mobileLayouts) {
            layout.set("buttons", Lists.newArrayList());
        }
        return mobileLayouts;
    }

}
