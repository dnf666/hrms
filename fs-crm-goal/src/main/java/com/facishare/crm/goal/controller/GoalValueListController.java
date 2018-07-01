package com.facishare.crm.goal.controller;

import com.facishare.crm.goal.GoalEnum;
import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.crm.goal.constant.GoalValueConstants;
import com.facishare.paas.appframework.common.service.dto.QueryDeptInfoByDeptIds;
import com.facishare.paas.appframework.common.service.dto.QueryMemberInfosByDeptIds;
import com.facishare.paas.appframework.common.service.dto.QueryUserInfoByIds;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.search.Wheres;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhaopx on 2018/4/4.
 */
public class GoalValueListController extends StandardListController {

    private Set<String> deptIds = new HashSet<>();
    private Set<String> empIds = new HashSet<>();
    private String tenantId;
    private String userId;
    private String checkObjectId;

    @Override
    protected SearchTemplateQuery buildSearchTemplateQuery() {
        SearchTemplateQuery query = super.buildSearchTemplateQuery();
        tenantId = controllerContext.getUser().getTenantId();
        userId = controllerContext.getUser().getUserId();
        List<IFilter> filters = query.getFilters();

        if (CollectionUtils.empty(filters)) {
            return query;
        }

        removeEmptyFilter(filters, GoalRuleObj.GOAL_RULE_DETAIL_ID);
        String goalType = getFieldValue(filters, GoalValueConstants.GOAL_TYPE);
        checkObjectId = getFieldValue(filters, GoalValueConstants.CHECK_OBJECT_ID);
        if (goalType.equals(GoalEnum.GoalTypeValue.CIRCLE.getValue())) {

            //获取直属子部门和直属人员
            deptIds.addAll(serviceFacade.getSubDeptByDeptId(tenantId, userId, checkObjectId, false));
            deptIds.add(checkObjectId);
            empIds.addAll(getMemberByDeptId(checkObjectId));
            List<Wheres> wheres = Lists.newArrayList();
            //拼装适配直属部门的wheres
            wheres.add(buildWheres(GoalEnum.GoalTypeValue.CIRCLE.getValue(), new ArrayList<>(deptIds)));
            if (CollectionUtils.notEmpty(empIds)) {
                //拼装适配直属员工的wheres
                wheres.add(buildWheres(GoalEnum.GoalTypeValue.EMPLOYEE.getValue(), new ArrayList<>(empIds)));
            }
            query.setWheres(wheres);
            //去除wheres外的filter
            removeFilter(filters, GoalValueConstants.GOAL_TYPE);
            removeFilter(filters, GoalValueConstants.CHECK_OBJECT_ID);

            query.getOrders().clear();
            List<OrderBy> orders = Lists.newArrayList();
            orders.add(new OrderBy(GoalValueConstants.GOAL_TYPE, true));
            orders.add(new OrderBy(GoalValueConstants.CHECK_OBJECT_ID, true));
            query.setOrders(orders);
        } else {
            empIds.add(checkObjectId);
        }

        //不走数据权限
        query.setPermissionType(0);

        return query;
    }

    @Override
    protected Result after(Arg arg, Result result) {
        //填充人员或部门名称
        Result rst = super.after(arg, result);
        List<ObjectDataDocument> dataList = rst.getDataList();
        List<QueryDeptInfoByDeptIds.DeptInfo> deptInfoList = serviceFacade.getDeptInfoNameByIds(tenantId, userId,
                new ArrayList<>(deptIds));
        List<QueryUserInfoByIds.UserInfo> edpInfoList = serviceFacade.getUserNameByIds(tenantId, userId,
                new ArrayList<>(empIds));
        for (ObjectDataDocument d : dataList) {
            ObjectData data = new ObjectData(d);
            //排序权重,父部门+1000;子部门+2000;员工+3000
            Integer order;
            String dataId = data.get(GoalValueConstants.CHECK_OBJECT_ID, String.class);
            String dataName;
            if (data.get(GoalValueConstants.GOAL_TYPE, String.class).equals
                    (GoalEnum.GoalTypeValue.CIRCLE.getValue())) {
                Optional<QueryDeptInfoByDeptIds.DeptInfo> dept = deptInfoList.stream().filter(x -> x.getDeptId().equals
                        (dataId)).findFirst();
                dataName = dept.map(QueryDeptInfoByDeptIds.DeptInfo::getDeptName).orElse("");
                order = dept.map(QueryDeptInfoByDeptIds.DeptInfo::getParentId).orElse("").equals(checkObjectId) ?
                        2000 : 1000;

            } else {
                Optional<QueryUserInfoByIds.UserInfo> user = edpInfoList.stream().filter(x -> x.getId().equals(dataId))
                        .findFirst();
                dataName = user.map(QueryUserInfoByIds.UserInfo::getName).orElse("");
                order = 3000;
            }
            data.set("check_object_id__r", dataName);
            data.set("row_num", (Integer) data.get("row_num") + order);

        }
        dataList.sort(GoalValueListController::compareTo);
        return rst;
    }

    private static int compareTo(ObjectDataDocument o1, ObjectDataDocument o2) {
        Integer i1 = Integer.parseInt(o1.get("row_num").toString());
        Integer i2 = Integer.parseInt(o2.get("row_num").toString());
        if (i1 <= i2) {
            if (Objects.equals(i1, i2)) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }

    private List<String> getMemberByDeptId(String deptId) {
        Map<String, List<QueryMemberInfosByDeptIds.Member>> memberMap = serviceFacade.getMemberInfoMapByDeptIds
                (controllerContext.getUser(), Lists.newArrayList
                        (deptId), Boolean.FALSE, null, 1);
        if (CollectionUtils.empty(memberMap)) {
            return Lists.newArrayList();
        }
        List<QueryMemberInfosByDeptIds.Member> memberList = memberMap.get(deptId);
        if (CollectionUtils.empty(memberList)) {
            return Lists.newArrayList();
        }
        return memberList.stream().map(QueryMemberInfosByDeptIds.Member::getId).distinct().collect(Collectors.toList());
    }

    private Wheres buildWheres(String goalType, List<String> ids) {
        Wheres wheres = new Wheres();
        wheres.setConnector(Where.CONN.OR.toString());
        List<IFilter> filters = Lists.newArrayList();
        filters.add(buildFilter(GoalValueConstants.GOAL_TYPE, Operator.EQ, Lists.newArrayList
                (goalType)));
        filters.add(buildFilter(GoalValueConstants.CHECK_OBJECT_ID, Operator.IN, ids));
        wheres.setFilters(filters);
        return wheres;
    }

    private Filter buildFilter(String fieldName, Operator operator, List<String> fieldValues) {
        Filter filter = new Filter();
        filter.setFieldName(fieldName);
        filter.setOperator(operator);
        filter.setFieldValues(fieldValues);
        return filter;
    }

    private void removeFilter(List<IFilter> filters, String fieldName) {
        Iterator<IFilter> iter = filters.iterator();
        while (iter.hasNext()) {
            IFilter item = iter.next();
            if (item.getFieldName().equals(fieldName)) {
                iter.remove();
                break;
            }
        }
    }

    private void removeEmptyFilter(List<IFilter> filters, String fieldName) {
        Iterator<IFilter> iter = filters.iterator();
        while (iter.hasNext()) {
            IFilter item = iter.next();
            if (item.getFieldName().equals(fieldName)) {
                if (item.getFieldValues() == null || item.getFieldValues().isEmpty()) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    private String getFieldValue(List<IFilter> filters, String fieldName) {
        String rst = "";
        List<IFilter> tmpFilter = filters.stream().filter(t ->
                fieldName.equals(t.getFieldName())
        ).collect(Collectors.toList());
        if (CollectionUtils.empty(tmpFilter)) {
            throw new MetaDataBusinessException(String.format("%s 类型不合法", fieldName));
        }
        List<String> fieldValues = tmpFilter.stream().findFirst().map(IFilter::getFieldValues)
                .orElse(Lists.newArrayList());
        if (CollectionUtils.empty(fieldValues)) {
            throw new MetaDataBusinessException(String.format("%s 类型不合法", fieldName));
        }
        for (String fieldValue : fieldValues) {
            if (Strings.isNullOrEmpty(fieldValue)) {
                throw new MetaDataBusinessException(String.format("%s 类型不合法", fieldName));
            }
            rst = fieldValue;
            break;
        }
        return rst;
    }
}
