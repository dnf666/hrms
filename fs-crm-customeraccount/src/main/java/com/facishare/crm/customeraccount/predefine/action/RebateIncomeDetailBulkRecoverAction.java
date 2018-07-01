package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.RebateActionEnum;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.util.CustomerAccountRecordLogger;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailBulkRecoverAction extends StandardBulkRecoverAction {
    private CustomerAccountManager customerAccountManager;
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    private CrmManager crmManager;

    @Override
    public void before(Arg arg) {
        super.before(arg);
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        crmManager = SpringUtil.getContext().getBean(CrmManager.class);
        log.info("RebateIncomeDetailBulkRecoverAction,Arg:{},actionContext", arg, actionContext.getRequestSource());
        if (!RequestUtil.isFromInner(actionContext)) {
            String objectApiName = arg.getObjectDescribeAPIName();
            List<String> ids = arg.getIdList();
            QueryResult<IObjectData> queryResult = rebateIncomeDetailManager.queryInvalidDataByField(actionContext.getUser(), objectApiName, SystemConstants.Field.Id.apiName, ids, 0, ids.size());
            List<IObjectData> rebateIncomeDatas = queryResult.getData();
            List<String> refundIds = rebateIncomeDatas.stream().filter(data -> Objects.nonNull(data.get(RebateIncomeDetailConstants.Field.Refund.apiName))).map(ob -> ObjectDataUtil.getReferenceId(ob, RebateIncomeDetailConstants.Field.Refund.apiName)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(refundIds)) {
                List<String> rebateIncomeNames = rebateIncomeDatas.stream().filter(data -> Objects.nonNull(data.get(RebateIncomeDetailConstants.Field.Refund.apiName))).map(IObjectData::getName).collect(Collectors.toList());
                List<IObjectData> refundDatas = crmManager.listInvalidRefundByIds(actionContext.getUser(), refundIds);
                List<String> refundNames = refundDatas.stream().map(IObjectData::getName).collect(Collectors.toList());
                throw new ValidateException(String.format("返利收入{%s}有关联退款，请恢复关联对象，退款编号:{%s}", Joiner.on(",").join(rebateIncomeNames), Joiner.on(",").join(refundNames)));
            }
        }
    }

    @Override
    public Result after(Arg arg, Result result) {
        super.after(arg, result);
        String objectApiName = arg.getObjectDescribeAPIName();
        List<String> ids = arg.getIdList();
        List<IObjectData> rebateIncomeDatas = serviceFacade.findObjectDataByIds(actionContext.getTenantId(), ids, objectApiName);
        List<IObjectData> rebateIncomeNormalAndActiveDatas = Lists.newArrayList();
        List<IObjectData> rebateIncomeNormalAndNotActiveDatas = Lists.newArrayList();
        rebateIncomeDatas.forEach(objectData -> {
            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            Date start = objectData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
            Date end = objectData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
            if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
                if (ObjectDataUtil.isCurrentTimeActive(start, end)) {
                    rebateIncomeNormalAndActiveDatas.add(objectData);
                } else {
                    rebateIncomeNormalAndNotActiveDatas.add(objectData);
                }
            }
        });
        log.info("rebateIncomeNormalAndActiveDatas:{},rebateIncomeNormalAndNotActiveDatas:{}", JsonUtil.toJson(rebateIncomeNormalAndActiveDatas), JsonUtil.toJson(rebateIncomeNormalAndNotActiveDatas));
        rebateIncomeNormalAndNotActiveDatas.forEach(objectData -> {
            BigDecimal amount = objectData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
            rebateIncomeDetailManager.addIncomeBalance(actionContext.getUser(), objectData, amount, null);
        });
        rebateIncomeNormalAndActiveDatas.forEach(objectData -> {
            BigDecimal amount = objectData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
            rebateIncomeDetailManager.addIncomeBalance(actionContext.getUser(), objectData, amount, null);
            String customerId = ObjectDataUtil.getReferenceId(objectData, RebateIncomeDetailConstants.Field.Customer.apiName);
            String info = CustomerAccountRecordLogger.generateRebateInfo(objectData.getId(), RebateActionEnum.Recover.getValue());
            customerAccountManager.updateRebateBalance(actionContext.getUser(), customerId, amount, BigDecimal.valueOf(0), info, objectData.getId());
        });
        return result;
    }

}
