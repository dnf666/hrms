package com.facishare.crm.customeraccount.predefine.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusChangeExecutor;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusChangeExecutor.LifeStatusChange;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusDeleteExecutor;
import com.facishare.crm.customeraccount.util.CustomerAccountRecordLogger;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.common.util.BulkOpResult;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.google.common.collect.Lists;

import io.netty.util.internal.StringUtil;

@Component
public class PrepayDetailManager extends CommonManager {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private CustomerAccountManager customerAccountManager;

    @Autowired
    private CustomerAccountBillManager customerAccountBillManager;

    public IObjectData update(User user, IObjectData objectData) {
        IObjectData result = serviceFacade.updateObjectData(user, objectData);
        this.recordLog(user, objectData);
        return result;
    }

    public IObjectData updateTmp(User user, IObjectData objectData, String... fields) {
        List<String> updateFieldList = new ArrayList<>();
        for (String value : fields) {
            updateFieldList.add(value);
        }
        BulkOpResult result = serviceFacade.parallelBulkUpdateObjectData(user, Lists.newArrayList(objectData), false, updateFieldList);
        this.recordLog(user, objectData);
        return result.getSuccessObjectDataList().get(0);
    }

    public void updatePrepayBalanceWhenEdit(User user, String customerId, String incomeType, String outcomeType, String oldLifeStatus, String newLifeStatus, BigDecimal prepayOldAmount, BigDecimal prepayNewAmount, String prepayId, String customerAccountId) {
        BigDecimal minus = prepayNewAmount.subtract(prepayOldAmount);
        String info = CustomerAccountRecordLogger.generatePrepayInfo(prepayId, oldLifeStatus, newLifeStatus);
        // 相当于生成了一笔minus的预存款<br>
        if (SystemConstants.LifeStatus.Ineffective.value.equals(oldLifeStatus)) {
            if (SystemConstants.LifeStatus.UnderReview.value.equals(newLifeStatus)) {
                // 这里不是这么的判断的。
                if (StringUtils.isNotEmpty(incomeType)) {
                    // 不变
                    // customerAccountManager.handlePrepayBalanceChange(user, customerId, prepayNewAmount, prepayNewAmount, info);
                } else if (StringUtils.isNotEmpty(outcomeType)) {
                    customerAccountManager.updatePrepayBalance(user, customerId, null, prepayNewAmount, info, prepayId);
                }
            } else if (SystemConstants.LifeStatus.Normal.value.equals(newLifeStatus)) {
                if (StringUtils.isNotEmpty(incomeType)) {
                    customerAccountManager.updatePrepayBalance(user, customerId, prepayNewAmount, null, info, prepayId);
                } else if (StringUtils.isNotEmpty(outcomeType)) {
                    customerAccountManager.updatePrepayBalance(user, customerId, prepayNewAmount.negate(), null, info, prepayId);
                }
            }
        } else if (SystemConstants.LifeStatus.Normal.value.equals(oldLifeStatus)) {
            if (StringUtils.isNotEmpty(incomeType)) {
                customerAccountManager.updatePrepayBalance(user, customerId, minus, null, info, prepayId);
            } else if (StringUtils.isNotEmpty(outcomeType)) {
                customerAccountManager.updatePrepayBalance(user, customerId, minus.negate(), null, info, prepayId);
            }
        }
    }

    public void updateBalance(User user, IObjectData prepayData, String oldLifeStatus) {
        String newLifeStatus = prepayData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        String incomeType = prepayData.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
        if (StringUtil.isNullOrEmpty(incomeType)) {
            this.updateBalanceForOutcome(user, prepayData, oldLifeStatus, newLifeStatus);
        } else {
            this.updateBalanceForIncome(user, prepayData, oldLifeStatus, newLifeStatus);
        }
    }

    public void updateBalanceWhenDeleteByOrderPayment(User user, IObjectData prepayData, String oldLifeStatus) {
        String incomeType = prepayData.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
        if (StringUtil.isNullOrEmpty(incomeType)) {
            this.updateBalanceForOutcomeWhenDelete(user, prepayData, oldLifeStatus);
        }
    }

    /**
     * 预存款收入更新
     */
    private void updateBalanceForIncome(User user, IObjectData prepayData, String oldLifeStatus, String newLifeStatus) {
        String incomeType = prepayData.get(PrepayDetailConstants.Field.IncomeType.apiName, String.class);
        if (StringUtil.isNullOrEmpty(incomeType)) {
            throw new ValidateException("非预存款收入操作");
        }
        String prepayId = prepayData.getId();
        String customerId = prepayData.get(PrepayDetailConstants.Field.Customer.apiName, String.class);
        String customerAccountId = prepayData.get(PrepayDetailConstants.Field.CustomerAccount.apiName, String.class);
        String info = CustomerAccountRecordLogger.generatePrepayInfo(prepayId, oldLifeStatus, newLifeStatus);
        BigDecimal amount = prepayData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);

        LifeStatusChange lifeStatusChange = new LifeStatusChange() {
            @Override
            public void ineffectiveToUnderReview() {
                //客户账户总额不变
            }

            @Override
            public void ineffectiveToNormal() {
                //客户账户总额增加
                customerAccountManager.updatePrepayBalance(user, customerId, amount, null, info, prepayId);
            }

            @Override
            public void underReviewToNormal() {
                //客户账户总额增加
                customerAccountManager.updatePrepayBalance(user, customerId, amount, null, info, prepayId);
            }

            @Override
            public void underReviewToIneffective() {
                //客户账户不变
            }

            @Override
            public void normalToInChange() {
                //客户账户锁定增加
                customerAccountManager.updatePrepayBalance(user, customerId, null, amount, info, prepayId);
            }

            @Override
            public void normalToInvalid() {
                //客户账户总额减少，锁定减少
                customerAccountManager.updatePrepayBalance(user, customerId, amount.negate(), null, info, prepayId);
            }

            @Override
            public void inChangeToInvalid() {
                //客户账户总额減少，锁定減少
                customerAccountManager.updatePrepayBalance(user, customerId, amount.negate(), amount.negate(), info, prepayId);
            }

            @Override
            public void inChangeToNormal() {
                //客户账户锁定减少
                customerAccountManager.updatePrepayBalance(user, customerId, null, amount.negate(), info, prepayId);
            }

            @Override
            public void invalidToNormal() {
                //客户账户总额增加
                customerAccountManager.updatePrepayBalance(user, customerId, amount, null, info, prepayId);
            }
        };
        LifeStatusChangeExecutor executor = new LifeStatusChangeExecutor(lifeStatusChange);
        executor.doChange(oldLifeStatus, newLifeStatus);
    }

    private void updateBalanceForOutcome(User user, IObjectData prepayData, String oldLifeStatus, String newLifeStatus) {
        String outcomeType = prepayData.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
        if (StringUtil.isNullOrEmpty(outcomeType)) {
            throw new ValidateException("非预存款支出操作");
        }
        String prepayId = prepayData.getId();
        String customerId = prepayData.get(PrepayDetailConstants.Field.Customer.apiName, String.class);
        String customerAccountId = prepayData.get(PrepayDetailConstants.Field.CustomerAccount.apiName, String.class);
        String info = CustomerAccountRecordLogger.generatePrepayInfo(prepayId, oldLifeStatus, newLifeStatus);
        BigDecimal amount = prepayData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);

        LifeStatusChange lifeStatusChange = new LifeStatusChange() {
            @Override
            public void ineffectiveToUnderReview() {
                //客户账户总额锁定增加
                customerAccountManager.updatePrepayBalance(user, customerId, null, amount, info, prepayId);
            }

            @Override
            public void ineffectiveToNormal() {
                //客户账户总额減少
                customerAccountManager.updatePrepayBalance(user, customerId, amount.negate(), null, info, prepayId);
            }

            @Override
            public void underReviewToNormal() {
                //客户账户总额减少，锁定减少
                customerAccountManager.updatePrepayBalance(user, customerId, amount.negate(), amount.negate(), info, prepayId);
            }

            @Override
            public void underReviewToIneffective() {
                //客户账户锁定减少
                customerAccountManager.updatePrepayBalance(user, customerId, null, amount.negate(), info, prepayId);
            }

            @Override
            public void normalToInChange() {
                //客户账户总额增加，锁定增加
                //2017-11-30 确定，审批确认后才加钱<br>
                //customerAccountManager.handlePrepayBalanceChange(user, customerId, amount, amount, info);
            }

            @Override
            public void normalToInvalid() {
                //客户账户总额增加
                customerAccountManager.updatePrepayBalance(user, customerId, amount, null, info, prepayId);
            }

            @Override
            public void inChangeToInvalid() {
                //客户账户锁定減少
                //customerAccountManager.handlePrepayBalanceChange(user, customerId, null, amount.negate(), info);
                customerAccountManager.updatePrepayBalance(user, customerId, amount, null, info, prepayId);
            }

            /**
             * normal -> inchange 不变，inchange -> norml也不变。
             */
            @Override
            public void inChangeToNormal() {
                //客户账户总额减少，锁定减少
                //customerAccountManager.handlePrepayBalanceChange(user, customerId, null, null, info);

            }

            @Override
            public void invalidToNormal() {
                //客户账户总额减少
                customerAccountManager.updatePrepayBalance(user, customerId, amount.negate(), null, info, prepayId);
            }
        };
        LifeStatusChangeExecutor executor = new LifeStatusChangeExecutor(lifeStatusChange);
        executor.doChange(oldLifeStatus, newLifeStatus);
    }

    private void updateBalanceForOutcomeWhenDelete(User user, IObjectData prepayData, String oldLifeStatus) {
        String outcomeType = prepayData.get(PrepayDetailConstants.Field.OutcomeType.apiName, String.class);
        if (StringUtil.isNullOrEmpty(outcomeType)) {
            throw new ValidateException("非预存款支出操作");
        }
        String prepayId = prepayData.getId();
        String customerId = prepayData.get(PrepayDetailConstants.Field.Customer.apiName, String.class);
        String newLifeStatus = "deleted";
        String info = CustomerAccountRecordLogger.generatePrepayInfo(prepayId, oldLifeStatus, newLifeStatus);
        BigDecimal amount = prepayData.get(PrepayDetailConstants.Field.Amount.apiName, BigDecimal.class);

        LifeStatusDeleteExecutor.LifeStatusToDelete lifeStatusToDelete = new LifeStatusDeleteExecutor.LifeStatusToDelete() {
            @Override
            public void ineffectiveToDeleted() {
                //ineffective 相当于无，直接删除即可。
            }

            @Override
            public void underReviewToDeleted() {
                //无-> underReview ：准备减 还没减总
                //underReview -> deleted 不减了
                customerAccountManager.updatePrepayBalance(user, customerId, null, amount.negate(), info, prepayId);
            }

            @Override
            public void normalToDeleted() {
                //无 -> noraml 快速减
                //noraml -> deleted 快速加
                customerAccountManager.updatePrepayBalance(user, customerId, amount, null, info, prepayId);
            }

            @Override
            public void inchangeToDeleted() {
                //normal -> inchange :准备加,可用没有加
                //inchange-> delete: 不加了
                customerAccountManager.updatePrepayBalance(user, customerId, amount.negate(), amount.negate(), info, prepayId);

            }

            @Override
            public void invalidToDeleted() {
                //invalid ->删除没有变化
            }
        };
        LifeStatusDeleteExecutor executor = new LifeStatusDeleteExecutor(lifeStatusToDelete);
        executor.doChange(oldLifeStatus);
    }

    public List<IObjectData> batchUpdate(User user, List<IObjectData> objectDatas) {
        List<IObjectData> result = new ArrayList<>(objectDatas.size());
        for (IObjectData objectData : objectDatas) {
            IObjectData resultData = this.update(user, objectData);
            result.add(resultData);
        }
        return result;
    }

    public List<IObjectData> listByPaymentIds(User user, List<String> paymentIds) {
        QueryResult<IObjectData> prepayResult = queryByFieldList(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.Payment.apiName, paymentIds, 0, paymentIds.size());
        if (CollectionUtils.isEmpty(prepayResult.getData())) {
            return Lists.newArrayList();
        } else {
            return prepayResult.getData();
        }
    }

    public List<IObjectData> listByOrderPaymentIds(User user, List<String> orderPaymentIds) {
        QueryResult<IObjectData> prepayResult = queryByFieldList(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.OrderPayment.apiName, orderPaymentIds, 0, orderPaymentIds.size());
        if (CollectionUtils.isEmpty(prepayResult.getData())) {
            return Lists.newArrayList();
        } else {
            return prepayResult.getData();
        }
    }

    public List<IObjectData> listInvalidDataByPaymentIds(User user, List<String> paymentIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.Payment.apiName, paymentIds, 0, paymentIds.size());
        if (CollectionUtils.isNotEmpty(queryResult.getData())) {
            return queryResult.getData();
        }
        return new ArrayList<>();
    }

    public List<IObjectData> listInvalidDataByOrderPaymentIds(User user, List<String> orderPaymentIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.OrderPayment.apiName, orderPaymentIds, 0, orderPaymentIds.size());
        if (CollectionUtils.isNotEmpty(queryResult.getData())) {
            return queryResult.getData();
        }
        return new ArrayList<>();
    }

    public IObjectData getByPaymentId(User user, String paymentId) {
        QueryResult<IObjectData> prepayResult = queryByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.Payment.apiName, paymentId, 0, 1);
        if (CollectionUtils.isEmpty(prepayResult.getData())) {
            return null;
        }
        return prepayResult.getData().get(0);
    }

    public IObjectData getByOrderPaymentId(User user, String orderPaymentId) {
        QueryResult<IObjectData> prepayResult = queryByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.OrderPayment.apiName, orderPaymentId, 0, 1);
        if (CollectionUtils.isEmpty(prepayResult.getData())) {
            return null;
        }
        return prepayResult.getData().get(0);
    }

    public IObjectData getByRefundId(User user, String refundId) {
        QueryResult<IObjectData> prepayResult = queryByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.Refund.apiName, refundId, 0, 1);
        if (CollectionUtils.isEmpty(prepayResult.getData())) {
            return null;
        }
        return prepayResult.getData().get(0);
    }

    public List<IObjectData> listByRefundIds(User user, List<String> refundIds) {
        QueryResult<IObjectData> prepayResult = queryByFieldList(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.Refund.apiName, refundIds, 0, refundIds.size());
        if (CollectionUtils.isEmpty(prepayResult.getData())) {
            return Lists.newArrayList();
        } else {
            return prepayResult.getData();
        }
    }

    /**
     * 根据客户id查询为完成流程的预存款明细<br>
     * @param user
     * @param customerId
     * @return
     */
    public List<IObjectData> listUnfinishedPrepayDetailByCustomerId(User user, String customerId) {
        IFilter customerIdFilter = new Filter();
        customerIdFilter.setOperator(Operator.EQ);
        customerIdFilter.setFieldName(PrepayDetailConstants.Field.Customer.apiName);
        customerIdFilter.setFieldValues(Lists.newArrayList(customerId));

        IFilter unfinishedFlowFilter = new Filter();
        unfinishedFlowFilter.setOperator(Operator.EQ);
        unfinishedFlowFilter.setFieldName(UdobjConstants.LIFE_STATUS_API_NAME);
        unfinishedFlowFilter.setFieldValues(Lists.newArrayList(SystemConstants.LifeStatus.UnderReview.value, SystemConstants.LifeStatus.InChange.value));

        List<IFilter> filterList = Lists.newArrayList(customerIdFilter, unfinishedFlowFilter);
        List<IObjectData> prepayDetailObjList = this.queryByFieldFilterList(user, PrepayDetailConstants.API_NAME, filterList, 0, 100).getData();

        if (CollectionUtils.isEmpty(prepayDetailObjList)) {
            return Lists.newArrayList();
        } else {
            return prepayDetailObjList;
        }
    }

    public List<IObjectData> listInvalidDataByRefundIds(User user, List<String> refundIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, PrepayDetailConstants.API_NAME, PrepayDetailConstants.Field.Refund.apiName, refundIds, 0, refundIds.size());
        if (CollectionUtils.isNotEmpty(queryResult.getData())) {
            return queryResult.getData();
        }
        return new ArrayList<>();
    }

}
