package com.facishare.crm.goal.service.dto;

import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.ObjectAPINameMapping;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.Order;
import com.facishare.paas.metadata.api.condition.IConditions;
import com.facishare.paas.metadata.api.condition.TermConditions;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.*;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public interface FindRuleList {
    @Data
    class Arg {
        String name;
        Integer pageSize = 20;
        Integer pageNumber = 1;
        String goalRuleId = "";
        String goalRuleDetailId = "";

        public SearchTemplateQuery buildSearchQuery() {
            SearchTemplateQuery searchQuery = new SearchTemplateQuery();
            Integer pageNumber = this.getPageNumber();
            Integer pageSize = this.getPageSize();
            Integer offset = (pageNumber - 1) * pageSize;
            Integer limit = pageSize;
            searchQuery.setOffset(offset);
            searchQuery.setLimit(limit);

            List filters = Lists.newLinkedList();
            if (StringUtils.isNotBlank(name)) {
                SearchUtil.fillFilterLike(filters, GoalRuleObj.NAME, name);
            }
            searchQuery.setFilters(filters);

            List<OrderBy> orders =  new ArrayList<>();
            orders.add(new OrderBy(GoalRuleObj.LAST_MODIFIED_TIME,Boolean.FALSE));
            searchQuery.setOrders(orders);

            return searchQuery;
        }
    }

    @Data
    @Builder
    class Result {
        List<ObjectDataDocument> dataList;
        Integer pageCount;
        Integer totalCount;
    }

}
