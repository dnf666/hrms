package com.facishare.crm.util;

import java.util.Arrays;
import java.util.List;

import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.OrderBy;

public class SearchUtil {

    public static void fillFiltersWithUser(User user, List filters) {
        fillFilterEq(filters, IObjectDescribe.TENANT_ID, user.getTenantId());
        fillFilterEq(filters, IObjectDescribe.PACKAGE, "CRM");
    }

    public static void fillFilterEq(List filters, String name, Object value) {
        filters.add(filter(name, Operator.EQ, value));
    }

    public static void fillFilterStartWith(List filters, String name, Object value) {
        filters.add(filter(name, Operator.STARTWITH, value));
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

    public static void fillFilterNotIn(List filters, String name, Object value) {
        filters.add(filter(name, Operator.NIN, value));
    }

    public static void fillFilterNotIn(List filters, String name, String fieldValueType, Object value) {
        Filter filter = filter(name, Operator.NIN, value);
        filter.setFieldValueType(fieldValueType);
        filters.add(filter);
    }

    public static void fillFilterGTE(List filters, String name, Object value) {
        filters.add(filter(name, Operator.GTE, value));
    }

    public static void fillFilterGT(List filters, String name, Object value) {
        filters.add(filter(name, Operator.GT, value));
    }

    public static void fillFilterLT(List filters, String name, Object value) {
        filters.add(filter(name, Operator.LT, value));
    }

    public static void fillFilterLTE(List filters, String name, Object value) {
        filters.add(filter(name, Operator.LTE, value));
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

    public static void fillOrderBy(List orders, String name, Boolean isAsc) {
        orders.add(order(name, isAsc));
    }

    public static OrderBy order(String name, Boolean isAsc) {
        OrderBy orderBy = new OrderBy();
        orderBy.setFieldName(name);
        orderBy.setIsAsc(isAsc);
        return orderBy;
    }
}
