package com.facishare.crm.payment.controller;

import com.facishare.crm.payment.constant.PaymentPlanObj;
import com.facishare.crm.payment.utils.FieldUtils;
import com.facishare.crm.payment.utils.JsonObjectUtils;
import com.facishare.crm.payment.utils.JsonPaths;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.search.Wheres;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.impl.search.Where;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.facishare.crm.payment.utils.PaymentPlanUtils.getPaymentPlanStatus;


public class PaymentPlanListController extends StandardListController {

    @Override
    protected void before(StandardListController.Arg arg) {
        super.before(arg);
    }

    @Override
    protected SearchTemplateQuery buildSearchTemplateQuery() {
        SearchTemplateQuery query = super.buildSearchTemplateQuery();
        List<IFilter> filters = Lists.newArrayList();
        List<Wheres> wheres = Lists.newArrayList();
        String now = String.valueOf(System.currentTimeMillis());
        query.getFilters().forEach(f -> {
            String fieldName = f.getFieldName();
            String operator = f.getOperator().name();
            String value = f.getFieldValues().get(0);
            if (StringUtils.isNotBlank(fieldName) && fieldName
                    .equals(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS)) {
                if (operator.equals(Operator.EQ.name())) {
                    if (value.equals(PaymentPlanObj.PlanPaymentStatus.COMPLETED.getName())) { //已完成
                        filters.add(FieldUtils.buildFilter(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT,
                                Arrays.asList("$" + PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT + "$"), Operator.GTE,
                                1));
                    } else if (value.equals(PaymentPlanObj.PlanPaymentStatus.INCOMPLETE.getName())) { // 未完成
                        filters.add(FieldUtils.buildFilter(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT,
                                Arrays.asList("$" + PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT + "$"), Operator.LT,
                                1));
                        filters.add(FieldUtils
                                .buildFilter(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME, Arrays.asList(now),
                                        Operator.GT, 0));
                    } else if (value.equals(PaymentPlanObj.PlanPaymentStatus.OVERDUE.getName())) { // 已逾期
                        filters.add(FieldUtils.buildFilter(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT,
                                Arrays.asList("$" + PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT + "$"), Operator.LT,
                                1));
                        filters.add(FieldUtils
                                .buildFilter(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME, Arrays.asList(now),
                                        Operator.LT, 0));
                    }
                } else if ((operator.equals(Operator.N.name()))) {
                    if (value.equals(PaymentPlanObj.PlanPaymentStatus.COMPLETED.getName())) { // 不等于 已完成
                        filters.add(FieldUtils.buildFilter(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT,
                                Arrays.asList("$" + PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT + "$"), Operator.LT,
                                1));
                    } else if (value
                            .equals(PaymentPlanObj.PlanPaymentStatus.INCOMPLETE.getName())) { // 不等于 未完成
                        Filter actualFilter = FieldUtils.buildFilter(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT,
                                Arrays.asList("$" + PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT + "$"), Operator.GTE,
                                1);
                        Filter planFilter = FieldUtils
                                .buildFilter(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME, Arrays.asList(now),
                                        Operator.LT, 0);
                        wheres
                                .add(FieldUtils.buildWheres(Where.CONN.OR.toString(), Arrays.asList(actualFilter)));
                        wheres.add(FieldUtils.buildWheres(Where.CONN.OR.toString(), Arrays.asList(planFilter)));
                    } else if (value.equals(PaymentPlanObj.PlanPaymentStatus.OVERDUE.getName())) { // 不等于 已逾期
                        Filter actualFilter = FieldUtils.buildFilter(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT,
                                Arrays.asList("$" + PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT + "$"), Operator.GTE,
                                1);
                        Filter planFilter = FieldUtils
                                .buildFilter(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME, Arrays.asList(now),
                                        Operator.GT, 0);
                        wheres
                                .add(FieldUtils.buildWheres(Where.CONN.OR.toString(), Arrays.asList(actualFilter)));
                        wheres.add(FieldUtils.buildWheres(Where.CONN.OR.toString(), Arrays.asList(planFilter)));
                    }
                } else if ((operator.equals(Operator.IS.name()))) {
                    filters.add(FieldUtils
                            .buildFilter(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, Arrays.asList(""),
                                    Operator.EQ, 0));
                }
            } else {
                filters.add(f);
            }
        });
        query.setWheres(wheres);
        query.resetFilters(filters);
        return query;
    }

    @Override
    public Result doService(Arg arg) {
        Result result = super.doService(arg);
        List<ObjectDataDocument> dataList = result.getDataList();
        for (ObjectDataDocument dataDocument : dataList) {
            BigDecimal actualPaymentAmount =
                    new BigDecimal(dataDocument.get(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT).toString());
            BigDecimal planPaymentAmount =
                    new BigDecimal(dataDocument.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_AMOUNT).toString());
            Long planPaymentTime =
                    Long.valueOf(dataDocument.get(PaymentPlanObj.FIELD_PLAN_PAYMENT_TIME).toString());
            String paymentPlanStatus =
                    getPaymentPlanStatus(planPaymentAmount, actualPaymentAmount, planPaymentTime);
            dataDocument.put(PaymentPlanObj.FIELD_PLAN_PAYMENT_STATUS, paymentPlanStatus);
            dataDocument.put(PaymentPlanObj.FIELD_ACTUAL_PAYMENT_AMOUNT, actualPaymentAmount);
        }
        return JsonObjectUtils.remove(result, PaymentPlanListController.Result.class,
                JsonPaths.DESCRIBE_LAYOUT_LIST_FIELDS + "." + PaymentPlanObj.EXTEND_OBJ_DATA_ID);
    }
}
