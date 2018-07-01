package com.facishare.crm.customeraccount.predefine.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.RebateOutcomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusChangeExecutor;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusChangeExecutor.LifeStatusChange;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusDeleteExecutor;
import com.facishare.crm.customeraccount.util.ConfigCenter;
import com.facishare.crm.customeraccount.util.CustomerAccountRecordLogger;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/11/8.
 */
@Slf4j
@Component
public class RebateOutcomeDetailManager extends CommonManager {
    @Autowired
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    @Autowired
    private CustomerAccountManager customerAccountManager;

    public IObjectData createRebateOutcomeAndUpdateBalance(User user, IObjectData outcomeData) {
        IObjectDescribe desc = serviceFacade.findObject(user.getTenantId(), outcomeData.getDescribeApiName());
        outcomeData = fillDefaultObject(user, desc, outcomeData);
        IObjectData resultOutcome = this.serviceFacade.saveObjectData(user, outcomeData);
        BigDecimal amount = resultOutcome.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        String lifeStatus = resultOutcome.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        String incomeId = ObjectDataUtil.getReferenceId(outcomeData, RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName);
        String outcomeId = resultOutcome.getId();
        this.updateBalanceForOutcome(user, incomeId, outcomeId, amount, SystemConstants.LifeStatus.Ineffective.value, lifeStatus);
        return resultOutcome;
    }

    public IObjectData editRebateOutcomeAndUpdateBalanceFromSfa(User user, IObjectData rebateObj, String oldLifeStatus) {
        IObjectData resultOutcome = serviceFacade.updateObjectData(user, rebateObj);
        BigDecimal amount = resultOutcome.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        String lifeStatus = resultOutcome.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        String incomeId = ObjectDataUtil.getReferenceId(resultOutcome, RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName);
        String outcomeId = resultOutcome.getId();
        this.updateBalanceForOutcome(user, incomeId, outcomeId, amount, oldLifeStatus, lifeStatus);
        return resultOutcome;
    }

    /**
     * 更新返利收入-返利支出时
     * @param user
     * @param outcomeId
     * @param outcomeAmount
     * @param oldOutcomeLifeStatus
     * @param newOutcomeLifeStatus
     */
    public void updateBalanceForOutcome(User user, String incomeId, String outcomeId, BigDecimal outcomeAmount, String oldOutcomeLifeStatus, String newOutcomeLifeStatus) {
        IObjectData incomeData = this.serviceFacade.findObjectData(user, incomeId, RebateIncomeDetailConstants.API_NAME);
        String customerId = incomeData.get(RebateIncomeDetailConstants.Field.Customer.apiName, String.class);
        Date start = incomeData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date end = incomeData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        String incomeLifeStatus = incomeData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (!ObjectDataUtil.isCurrentTimeActive(start, end)) {
            throw new ValidateException("返利收入未生效，不能使用");
        }
        if (!SystemConstants.LifeStatus.Normal.value.equals(incomeLifeStatus)) {
            throw new ValidateException("返利收入状态非正常，不能使用");
        }
        String info = CustomerAccountRecordLogger.generateRebateInfo("O-" + outcomeId, oldOutcomeLifeStatus, newOutcomeLifeStatus);
        LifeStatusChange lifeStatusChange = new LifeStatusChange() {
            @Override
            public void underReviewToNormal() {
                //返利收入不变；客户账户锁定减少
                customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount.negate(), outcomeAmount.negate(), info, outcomeId);
            }

            @Override
            public void underReviewToIneffective() {
                //返利收入可用增加、已用減少；客户账户锁定減少
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount, outcomeAmount.negate());
                customerAccountManager.updateRebateBalance(user, customerId, null, outcomeAmount.negate(), info, outcomeId);
            }

            @Override
            public void normalToInvalid() {
                //返利收入可用增加、已用減少；客户账户总额減少
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount, outcomeAmount.negate());
                //12-22
                customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount, null, info, outcomeId);
                //customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount.negate(), null, info, outcomeId);
            }

            @Override
            public void normalToInChange() {
                //返利收入可用增加、已用減少；客户账户总额增加、锁定增加
                //2017-11-30确认，加钱有审批流的情况下最后加钱<br>
                //customerAccountManager.handleRebateBalanceChange(user, customerId, outcomeAmount, outcomeAmount, info);
            }

            @Override
            public void invalidToNormal() {
                //返利收入可用減少、已用增加；客户账户总额減少
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount.negate(), outcomeAmount);
                customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount.negate(), null, info, outcomeId);
            }

            @Override
            public void ineffectiveToUnderReview() {
                //返利收入可用減少、已用增加；客户账户锁定增加
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount.negate(), outcomeAmount);
                customerAccountManager.updateRebateBalance(user, customerId, null, outcomeAmount, info, outcomeId);
            }

            @Override
            public void ineffectiveToNormal() {
                //返利收入可用減少、已用增加；客户账户总额減少
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount.negate(), outcomeAmount);
                customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount.negate(), null, info, outcomeId);
            }

            @Override
            public void inChangeToNormal() {
                //返利收入不变；客户账户总额减少，锁定减少
                //customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount.negate(), outcomeAmount, info, outcomeId);
            }

            @Override
            public void inChangeToInvalid() {
                //返利收入可用增加，已用減少；客户账户锁定减少
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount, outcomeAmount.negate());
                customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount, null, info, outcomeId);
            }
        };
        LifeStatusChangeExecutor executor = new LifeStatusChangeExecutor(lifeStatusChange);
        executor.doChange(oldOutcomeLifeStatus, newOutcomeLifeStatus);
    }

    /**
     * 更新返利收入-返利支出时
     * @param user
     * @param outcomeId
     * @param outcomeAmount
     * @param oldOutcomeLifeStatus
     */
    public void updateBalanceForOutcomeWhenOrderPaymentDelete(User user, String incomeId, String outcomeId, BigDecimal outcomeAmount, String oldOutcomeLifeStatus) {
        IObjectData incomeData = this.serviceFacade.findObjectData(user, incomeId, RebateIncomeDetailConstants.API_NAME);
        String customerId = incomeData.get(RebateIncomeDetailConstants.Field.Customer.apiName, String.class);
        Date start = incomeData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date end = incomeData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        String incomeLifeStatus = incomeData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (!ObjectDataUtil.isCurrentTimeActive(start, end)) {
            throw new ValidateException("返利收入未生效，不能使用");
        }
        if (!SystemConstants.LifeStatus.Normal.value.equals(incomeLifeStatus)) {
            throw new ValidateException("返利收入状态非正常，不能使用");
        }
        String info = CustomerAccountRecordLogger.generateRebateInfo("O-" + outcomeId, oldOutcomeLifeStatus, "deleted");
        LifeStatusDeleteExecutor.LifeStatusToDelete lifeStatustoDeleted = new LifeStatusDeleteExecutor.LifeStatusToDelete() {
            @Override
            public void ineffectiveToDeleted() {
                //没有变化<br>
            }

            @Override
            public void underReviewToDeleted() {
                //无 -> underReview  准备减总
                //underReview ->  deleted  不扣了
                customerAccountManager.updateRebateBalance(user, customerId, null, outcomeAmount.negate(), info, outcomeId);
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount, outcomeAmount.negate());
            }

            @Override
            public void normalToDeleted() {
                customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount, null, info, outcomeId);

                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount, outcomeAmount.negate());
            }

            @Override
            public void inchangeToDeleted() {
                rebateIncomeDetailManager.addIncomeBalance(user, incomeData, outcomeAmount, outcomeAmount.negate());
                customerAccountManager.updateRebateBalance(user, customerId, outcomeAmount, null, info, outcomeId);
            }

            @Override
            public void invalidToDeleted() {
                //不做变化
            }
        };
        LifeStatusDeleteExecutor executor = new LifeStatusDeleteExecutor(lifeStatustoDeleted);
        executor.doChange(oldOutcomeLifeStatus);
    }

    public boolean hasRebateOutcomeDetails(User user, String rebateIncomeDetailId) {
        List<IObjectData> rebateOutcomeObjectDatas = queryByField(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, rebateIncomeDetailId, 0, 1).getData();
        return !rebateOutcomeObjectDatas.isEmpty();
    }

    public String getDescribeId(String tenantId) {
        return this.getDescribeId(tenantId, RebateOutcomeDetailConstants.API_NAME);
    }

    public IObjectData update(User user, IObjectData objectData) {
        IObjectData resultData = serviceFacade.updateObjectData(user, objectData);
        this.recordLog(user, objectData);
        return resultData;
    }

    public List<IObjectData> batchUpdate(User user, List<IObjectData> objectDatas) {
        List<IObjectData> result = new ArrayList<>(objectDatas.size());
        for (IObjectData objectData : objectDatas) {
            IObjectData resultData = this.update(user, objectData);
            result.add(resultData);
        }
        return result;
    }

    public List<IObjectData> bulkInvalid(User user, List<IObjectData> rebateOutcomeDetailList, String lifeStatus) {
        if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
            log.info("ToBulkInvalid rebateOutcomeDetailList:{}", JsonUtil.toJson(rebateOutcomeDetailList));
            Map<String, String> idOldLifeStatusMap = rebateOutcomeDetailList.stream().collect(Collectors.toMap(objData -> objData.getId(), data -> data.get(SystemConstants.Field.LifeStatus.apiName, String.class)));
            List<IObjectData> resultList = this.serviceFacade.bulkInvalid(rebateOutcomeDetailList, user);
            log.info("afterInvalid, arg rebateOutcomeDetailList:{}", JsonUtil.toJson(rebateOutcomeDetailList));
            for (IObjectData data : rebateOutcomeDetailList) {
                String outcomeId = data.getId();
                String oldLifeStatus = idOldLifeStatusMap.get(outcomeId);
                String incomeId = data.get(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, String.class);
                BigDecimal outcomeAmount = data.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
                this.recordLog(user, data);
                updateBalanceForOutcome(user, incomeId, outcomeId, outcomeAmount, oldLifeStatus, lifeStatus);
            }
            return resultList;
        } else if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
            List<IObjectData> result = new ArrayList<>(rebateOutcomeDetailList.size());
            for (IObjectData data : rebateOutcomeDetailList) {
                String outcomeId = data.getId();
                String oldLifeStatus = data.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                String incomeId = data.get(RebateOutcomeDetailConstants.Field.RebateIncomeDetail.apiName, String.class);
                BigDecimal outcomeAmount = data.get(RebateOutcomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
                data.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                IObjectData updated = this.update(user, data);
                result.add(updated);
                this.recordLog(user, data);
                updateBalanceForOutcome(user, incomeId, outcomeId, outcomeAmount, oldLifeStatus, lifeStatus);
            }
            return result;
        }
        throw new ValidateException("Lifestatus must InChange or Invalid");
    }

    public List<IObjectData> listByPaymentIds(User user, List<String> paymentIds) {
        QueryResult<IObjectData> queryResult = queryByFieldList(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.Payment.apiName, paymentIds, 0, paymentIds.size() * ConfigCenter.queryCount);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return Lists.newArrayList();
        } else {
            return queryResult.getData();
        }
    }

    public List<IObjectData> listByOrderPaymentIds(User user, List<String> orderPaymentIds) {
        QueryResult<IObjectData> queryResult = queryByFieldList(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.OrderPayment.apiName, orderPaymentIds, 0, orderPaymentIds.size() * ConfigCenter.queryCount);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return Lists.newArrayList();
        } else {
            return queryResult.getData();
        }
    }

    public List<IObjectData> listInvalidDataByPaymentIds(User user, List<String> paymentIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.Payment.apiName, paymentIds, 0, paymentIds.size() * ConfigCenter.queryCount);
        if (CollectionUtils.isNotEmpty(queryResult.getData())) {
            return queryResult.getData();
        }
        return new ArrayList<>();
    }

    public List<IObjectData> listInvalidDataByOrderPaymentIds(User user, List<String> orderPaymentIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.OrderPayment.apiName, orderPaymentIds, 0, orderPaymentIds.size() * ConfigCenter.queryCount);
        if (CollectionUtils.isNotEmpty(queryResult.getData())) {
            return queryResult.getData();
        }
        return new ArrayList<>();
    }

    public List<IObjectData> listInvalidDataByIds(User user, List<String> rebateOutcomeIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, RebateOutcomeDetailConstants.API_NAME, ObjectData.ID, rebateOutcomeIds, 0, rebateOutcomeIds.size());
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return new ArrayList<>();
        }
        return queryResult.getData();
    }

    public List<IObjectData> getByPaymentId(User user, String paymentId) {
        List<IObjectData> resultList = Lists.newArrayList();
        QueryResult<IObjectData> queryResult = queryByField(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.Payment.apiName, paymentId, 0, ConfigCenter.queryCount);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return resultList;
        } else {
            resultList.addAll(queryResult.getData());
        }
        if (queryResult.getTotalNumber() > ConfigCenter.queryCount) {
            queryResult = queryByField(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.Payment.apiName, paymentId, ConfigCenter.queryCount, queryResult.getTotalNumber() - ConfigCenter.queryCount);
            if (CollectionUtils.isNotEmpty(queryResult.getData())) {
                resultList.addAll(queryResult.getData());
            }
        }
        return resultList;
    }

    public List<IObjectData> getByOrderPaymentId(User user, String orderPaymentId) {
        List<IObjectData> resultList = Lists.newArrayList();
        QueryResult<IObjectData> queryResult = queryByField(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.OrderPayment.apiName, orderPaymentId, 0, ConfigCenter.queryCount);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return resultList;
        } else {
            resultList.addAll(queryResult.getData());
        }
        if (queryResult.getTotalNumber() > ConfigCenter.queryCount) {
            queryResult = queryByField(user, RebateOutcomeDetailConstants.API_NAME, RebateOutcomeDetailConstants.Field.OrderPayment.apiName, orderPaymentId, ConfigCenter.queryCount, queryResult.getTotalNumber() - ConfigCenter.queryCount);
            if (CollectionUtils.isNotEmpty(queryResult.getData())) {
                resultList.addAll(queryResult.getData());
            }
        }
        return resultList;
    }

}
