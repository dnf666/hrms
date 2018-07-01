package com.facishare.crm.sfa.predefine.action;

import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;

import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceBookProductEditAction extends StandardEditAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        //验证是否价目表产品是否重复
        validateRepeat();
    }

    private void validateRepeat() {

        Object productId = objectData.get(PriceBookConstants.ProductField.PRODUCTID.getApiName());
        if (productId == null || StringUtils.isBlank(productId.toString())) {
            throw new ValidateException("产品不能为空");
        }
        Object priceBookId = objectData.get(PriceBookConstants.ProductField.PRICEBOOKID.getApiName());
        if (priceBookId == null || StringUtils.isBlank(priceBookId.toString())) {
            IObjectData oldObjectData = this.serviceFacade.findObjectData(this.getActionContext().getUser(), objectData.getId(), PriceBookConstants.API_NAME_PRODUCT);
            priceBookId = oldObjectData.get(PriceBookConstants.ProductField.PRICEBOOKID.getApiName());
        }
        // 判断参数列表中，和数据库中产品做比对，如果有，则必须是更新且更新的主键id必须相同
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(10);
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFiltersWithUser(actionContext.getUser(), filters);
        SearchUtil.fillFilterEq(filters, PriceBookConstants.ProductField.PRICEBOOKID.getApiName(), priceBookId);
        SearchUtil.fillFilterEq(filters, PriceBookConstants.ProductField.PRODUCTID.getApiName(), Arrays.asList(productId));
        searchQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = this.serviceFacade.findBySearchQuery(this.getActionContext().getUser(), PriceBookConstants.API_NAME_PRODUCT, searchQuery);
        IObjectData priceBookProduct = queryResult.getData().get(0);
        if (!priceBookProduct.getId().equals(objectData.getId())) {
            throw new ValidateException("当前价目表下产品已存在");
        }
    }
}
