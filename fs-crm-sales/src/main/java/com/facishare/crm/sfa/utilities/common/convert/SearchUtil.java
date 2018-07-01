package com.facishare.crm.sfa.utilities.common.convert;

import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.OrderBy;

import java.util.Arrays;
import java.util.List;

public class SearchUtil {
    public static final String FIELD_VALUE_TYPE_SQL = "sql";

    public static void fillFiltersWithUser(User user, List filters) {
        fillFilterEq(filters, IObjectDescribe.TENANT_ID, user.getTenantId());
        fillFilterEq(filters, IObjectDescribe.PACKAGE, "CRM");
    }

    public static void fillFilterEq(List filters, String name, Object value) {
        filters.add(filter(name, Operator.EQ, value));
    }

    public static void fillFilterNotEq(List filters, String name, Object value) {
        filters.add(filter(name, Operator.N, value));
    }

    public static void fillFilterLike(List filters, String name, Object value) {
        filters.add(filter(name, Operator.LIKE, value));
    }

    public static void fillFilterIn(List filters, String name, Object value) {
        filters.add(filter(name, Operator.IN, value));
    }

    public static OrderBy orderByLastModifiedTime() {
        return new OrderBy("last_modified_time", false);
    }

    public static void fillFilterInBySql(List filters, String name, String fieldValueType, Object value) {
        Filter filter = filter(name, Operator.IN, value);
        filter.setFieldValueType(fieldValueType);
        filters.add(filter);
    }
    public static void fillFilterBySql(List filters, String name, Operator operator, Object value) {
        Filter filter = filter(name, operator, value);
        filter.setFieldValueType(SearchUtil.FIELD_VALUE_TYPE_SQL);
        filters.add(filter);
    }

    public static Filter filter(String name, Operator operator, Object value) {
        Filter filter = new Filter();
        filter.setFieldName(name);
        if (value instanceof List) {
            filter.setFieldValues((List<String>) value);
        } else {
            filter.setFieldValues(Arrays.asList(value.toString()));
        }
        filter.setOperator(operator);
        return filter;
    }
}
