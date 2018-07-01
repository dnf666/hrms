package com.facishare.crm.customeraccount.predefine.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.enums.RebateActionEnum;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusChangeExecutor;
import com.facishare.crm.customeraccount.predefine.manager.common.LifeStatusChangeExecutor.LifeStatusChange;
import com.facishare.crm.customeraccount.predefine.service.dto.RebateIncomeModle;
import com.facishare.crm.customeraccount.util.CustomerAccountRecordLogger;
import com.facishare.crm.customeraccount.util.DateUtil;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.common.util.BulkOpResult;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.Where;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RebateIncomeDetailManager extends CommonManager {
    @Autowired
    private CustomerAccountManager customerAccountManager;

    @Autowired
    private CustomerAccountBillManager customerAccountBillManager;

    /**
     * 临时因为框架并发问题使用
     *
     * @param user
     * @param objectData
     * @return
     */
    public IObjectData updateTmp(User user, IObjectData objectData, String... updateFields) {
        List<String> updateFieldList = new ArrayList<>();
        for (String value : updateFields) {
            updateFieldList.add(value);
        }
        BulkOpResult result = serviceFacade.parallelBulkUpdateObjectData(user, Lists.newArrayList(objectData), false, updateFieldList);
        log.info("updateTmp result:{}", JsonUtil.toJson(result));
        this.recordLog(user, objectData);
        return result.getSuccessObjectDataList().get(0);
    }

    public IObjectData update(User user, IObjectData objectData) {
        IObjectData result = serviceFacade.updateObjectData(user, objectData, true);
        this.recordLog(user, objectData);
        return result;
    }

    public List<IObjectData> batchUpdate(User user, List<IObjectData> objectDatas) {
        List<IObjectData> resultList = new ArrayList<>(objectDatas.size());
        for (IObjectData objectData : objectDatas) {
            IObjectData result = this.update(user, objectData);
            resultList.add(result);
        }
        return resultList;
    }

    //6.3
    public IObjectData addIncomeBalance(User user, IObjectData inComeData, BigDecimal addAvailableAmount, BigDecimal addUsedAmount) {
        BigDecimal usedAmount = ObjectDataUtil.getBigDecimal(inComeData, RebateIncomeDetailConstants.Field.UsedRebate.apiName);
        BigDecimal availableAmount = ObjectDataUtil.getBigDecimal(inComeData, RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
        inComeData.set(RebateIncomeDetailConstants.Field.UsedRebate.apiName, usedAmount.add(addUsedAmount == null ? BigDecimal.valueOf(0) : addUsedAmount));
        inComeData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, availableAmount.add(addAvailableAmount));

        IObjectData updatedData = this.update(user, inComeData);
        log.info("addIncomeBalance,Pre[usedAmount:{}-availableAmount:{}],New[usedAmount:{}-availableAmount:{}]", usedAmount, availableAmount, inComeData.get(RebateIncomeDetailConstants.Field.UsedRebate.apiName, BigDecimal.class), inComeData.get(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, BigDecimal.class));
        return updatedData;
    }

    /**
     * 更新返利收入余额-状态更新时
     *
     * @param user
     * @param incomeData
     * @param oldLifeStatus
     * @param newLifeStatus
     */

    public void updateBalanceForLifeStatus(User user, IObjectData incomeData, String oldLifeStatus, String newLifeStatus) {
        BigDecimal usedAmount = incomeData.get(RebateIncomeDetailConstants.Field.UsedRebate.apiName, BigDecimal.class);
        if (usedAmount.doubleValue() != 0) {
            throw new ValidateException("已用返利不为零，不能修改状态");
        }
        Date start = incomeData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date end = incomeData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        if (!ObjectDataUtil.isCurrentTimeActive(start, end)) {
            throw new ValidateException("返利未到有效期不需要更新");
        }
        BigDecimal amount = incomeData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        String customerId = incomeData.get(RebateIncomeDetailConstants.Field.Customer.apiName, String.class);
        String customerAccountId = incomeData.get(RebateIncomeDetailConstants.Field.CustomerAccount.apiName, String.class);
        String incomeId = incomeData.getId();
        String info = CustomerAccountRecordLogger.generateRebateInfo(incomeId, oldLifeStatus, newLifeStatus);
        LifeStatusChange lifeStatusChange = new LifeStatusChange() {

            @Override
            public void ineffectiveToUnderReview() {
                //返利收入不变，客户账户不能
            }

            @Override
            public void ineffectiveToNormal() {
                //返利收入可用增加，客户账户锁定减少
                addIncomeBalance(user, incomeData, amount, null);
                customerAccountManager.updateRebateBalance(user, customerId, amount, null, info, incomeData.getId());
            }

            @Override
            public void underReviewToNormal() {
                //返利收入可用增加，客户账户增加
                addIncomeBalance(user, incomeData, amount, null);
                customerAccountManager.updateRebateBalance(user, customerId, amount, null, info, incomeData.getId());
            }

            @Override
            public void underReviewToIneffective() {
                //返利收入不变，客户账户总额不变

            }

            @Override
            public void normalToInChange() {
                //返利收入可用为0，客户账户总额锁定增加
                addIncomeBalance(user, incomeData, amount.negate(), null);
                customerAccountManager.updateRebateBalance(user, customerId, null, amount, info, incomeData.getId());
            }

            @Override
            public void normalToInvalid() {
                //返利收入可用为0，客户账户总额减少，锁定减少
                addIncomeBalance(user, incomeData, amount.negate(), null);
                customerAccountManager.updateRebateBalance(user, customerId, amount.negate(), null, info, incomeData.getId());
            }

            @Override
            public void inChangeToInvalid() {
                //返利收入不变，客户账户总额减少，锁定减少
                customerAccountManager.updateRebateBalance(user, customerId, amount.negate(), amount.negate(), info, incomeData.getId());
            }

            @Override
            public void inChangeToNormal() {
                //返利收入可用增加，客户账户锁定减少
                addIncomeBalance(user, incomeData, amount, null);
                customerAccountManager.updateRebateBalance(user, customerId, null, amount.negate(), info, incomeData.getId());
            }

            @Override
            public void invalidToNormal() {
                //返利收入可用增加，客户账户总额增加
                addIncomeBalance(user, incomeData, amount, null);
                customerAccountManager.updateRebateBalance(user, customerId, amount, null, info, incomeData.getId());
            }

        };
        LifeStatusChangeExecutor executor = new LifeStatusChangeExecutor(lifeStatusChange);
        executor.doChange(oldLifeStatus, newLifeStatus);
    }

    public List<RebateIncomeModle.PayForOutcomeModel> obtainRebateIncomeToPayList(User user, BigDecimal totalAmountToPay, String customerId) {
        //查询AvailableRebate>0
        IFilter customerIdFilter = new Filter();
        customerIdFilter.setOperator(Operator.EQ);
        customerIdFilter.setFieldName(RebateIncomeDetailConstants.Field.Customer.apiName);
        customerIdFilter.setFieldValues(Lists.newArrayList(customerId));
        customerIdFilter.setConnector(Where.CONN.AND.toString());
        IFilter availableRebateFilter = new Filter();
        availableRebateFilter.setOperator(Operator.GT);
        availableRebateFilter.setFieldName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
        availableRebateFilter.setFieldValues(Lists.newArrayList("0"));
        IFilter lifeStatusFilter = new Filter();
        lifeStatusFilter.setOperator(Operator.EQ);
        lifeStatusFilter.setFieldName(SystemConstants.Field.LifeStatus.apiName);
        lifeStatusFilter.setFieldValues(Lists.newArrayList(SystemConstants.LifeStatus.Normal.value));
        List<IFilter> filterList = Lists.newArrayList(customerIdFilter, availableRebateFilter, lifeStatusFilter);
        OrderBy startTimeOrderBy = new OrderBy(RebateIncomeDetailConstants.Field.StartTime.apiName, true);
        OrderBy endTimeOrderBy = new OrderBy(RebateIncomeDetailConstants.Field.EndTime.apiName, true);
        //临时查询100个
        int limit = 100;
        int offset = 0;
        List<IObjectData> incomeObjectDataList = queryByFieldFilterList(user, RebateIncomeDetailConstants.API_NAME, filterList, Lists.newArrayList(startTimeOrderBy, endTimeOrderBy), offset, limit);
        //选取返利收入
        BigDecimal amountLeft = totalAmountToPay;
        List<RebateIncomeModle.PayForOutcomeModel> incomeObjectDataListToPay = new ArrayList<>();
        for (IObjectData tempIncomeObjectData : incomeObjectDataList) {
            BigDecimal availableIncomeRebate = tempIncomeObjectData.get(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, BigDecimal.class);
            if (amountLeft.compareTo(availableIncomeRebate) > 0) {//不够扣
                RebateIncomeModle.PayForOutcomeModel incomeObjToPay = new RebateIncomeModle.PayForOutcomeModel();
                amountLeft = amountLeft.subtract(availableIncomeRebate);
                incomeObjToPay.setPayAmount(availableIncomeRebate);
                incomeObjToPay.setRebateIncomeObj(tempIncomeObjectData);
                incomeObjectDataListToPay.add(incomeObjToPay);
            } else if (amountLeft.compareTo(availableIncomeRebate) == 0) {//刚好可以扣
                RebateIncomeModle.PayForOutcomeModel incomeObjToPay = new RebateIncomeModle.PayForOutcomeModel();
                amountLeft = amountLeft.subtract(availableIncomeRebate);
                incomeObjToPay.setPayAmount(availableIncomeRebate);
                incomeObjToPay.setRebateIncomeObj(tempIncomeObjectData);
                incomeObjectDataListToPay.add(incomeObjToPay);
                break;
            } else {//有多余
                RebateIncomeModle.PayForOutcomeModel incomeObjToPay = new RebateIncomeModle.PayForOutcomeModel();
                incomeObjToPay.setPayAmount(amountLeft);
                incomeObjToPay.setRebateIncomeObj(tempIncomeObjectData);
                incomeObjectDataListToPay.add(incomeObjToPay);
                amountLeft = new BigDecimal(0);
                break;
            }
        }
        if (amountLeft.compareTo(new BigDecimal(0)) != 0) {
            log.info("余额不足，user={},customerId={},totalAmountToPay={}", user, customerId, totalAmountToPay);
            throw new ValidateException("返利余额不足");
        }
        return incomeObjectDataListToPay;
    }

    public void updateRebateIncomeBalanceWhenEdit(User user, IObjectData incomeObjectData, String oldLifeStatus, BigDecimal oldAmount, boolean oldActive) {
        //客户账户变更逻辑
        String customerId = incomeObjectData.get(RebateIncomeDetailConstants.Field.Customer.apiName, String.class);
        //更新后的有效时间是否在有效期内
        BigDecimal rebateIncomeAvailable = incomeObjectData.get(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, BigDecimal.class);
        String id = incomeObjectData.getId();
        boolean updatedIsActive = isActiveTime(incomeObjectData);
        BigDecimal newAmount = incomeObjectData.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
        String newLifeStatus = incomeObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        if (SystemConstants.LifeStatus.Normal.value.equals(oldLifeStatus) && newAmount.compareTo(oldAmount) != 0) {
            //正常状态的返利时不允许编辑，实际情况应该走不到该分支流程。<br> normal  无支出关联改变金额
            BigDecimal tmp = newAmount.subtract(oldAmount);
            if (oldActive && updatedIsActive) {//有效到有效
                BigDecimal zero = new BigDecimal(0);
                String info = CustomerAccountRecordLogger.generateRebateInfo(id, RebateActionEnum.Edit.getValue());
                customerAccountManager.updateRebateBalance(user, customerId, tmp, null, info, id);
            } else if (!oldActive && updatedIsActive) {//无效到有效
                String info = CustomerAccountRecordLogger.generateRebateInfo(id, RebateActionEnum.Edit.getValue());
                customerAccountManager.updateRebateBalance(user, customerId, newAmount, null, info, id);
                rebateIncomeAvailable = newAmount;
            }
            //仅仅更新可用收入
            incomeObjectData.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, rebateIncomeAvailable);
        } else if (SystemConstants.LifeStatus.Ineffective.value.equals(oldLifeStatus) && SystemConstants.LifeStatus.Normal.value.equals(newLifeStatus)) {
            //未生效(Ineffective)编辑，没有流程，直接审批通过
            if (updatedIsActive) {
                //当前时间有效
                String info = CustomerAccountRecordLogger.generateRebateInfo(id, RebateActionEnum.Edit.getValue());
                customerAccountManager.updateRebateBalance(user, customerId, newAmount, null, info, id);
            }
        }
    }

    private boolean isActiveTime(IObjectData incomeObjectData) {
        Date updatedStart = incomeObjectData.get(RebateIncomeDetailConstants.Field.StartTime.apiName, Date.class);
        Date updatedEnd = incomeObjectData.get(RebateIncomeDetailConstants.Field.EndTime.apiName, Date.class);
        boolean active = ObjectDataUtil.isCurrentTimeActive(updatedStart, updatedEnd);
        return active;
    }

    /**
     * 把返利收入设置为过期
     *
     * @param list
     */
    public int batchInvalidRebateIncomeDatails(List<IObjectData> list) {
        int successSize = 0;
        for (IObjectData income : list) {
            String id = income.getId();
            String customerId = income.get(RebateIncomeDetailConstants.Field.Customer.apiName, String.class);
            try {
                User user = RequestUtil.getSysteomUser(income.getTenantId());
                BigDecimal availableRebate = income.get(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, BigDecimal.class);
                income.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, "0");
                this.update(user, income);
                String info = CustomerAccountRecordLogger.generateRebateInfo(id, RebateActionEnum.Disable.getValue());
                customerAccountManager.updateRebateBalance(user, customerId, availableRebate.negate(), null, info, income.getId());
                successSize++;
            } catch (Exception e) {
                log.warn("InvalidRebateIncomeDatail error,id=" + id + ",customerId=" + customerId, e);
            }
        }
        return successSize;
    }

    /**
     * 把返利收入设置为有效
     *
     * @param enabledRebateList
     */
    public int batchEnableRebateIncomeDatails(List<IObjectData> enabledRebateList) {
        int successSize = 0;
        for (IObjectData income : enabledRebateList) {
            String id = income.getId();
            String customerId = income.get(RebateIncomeDetailConstants.Field.Customer.apiName, String.class);

            try {
                User user = RequestUtil.getSysteomUser(income.getTenantId());
                BigDecimal amount = income.get(RebateIncomeDetailConstants.Field.Amount.apiName, BigDecimal.class);
                income.set(RebateIncomeDetailConstants.Field.AvailableRebate.apiName, amount);
                this.update(user, income);
                //更新客户账户
                String info = CustomerAccountRecordLogger.generateRebateInfo(id, RebateActionEnum.Effective.getValue());
                customerAccountManager.updateRebateBalance(user, customerId, amount, null, info, income.getId());
                successSize++;
            } catch (Exception e) {
                log.warn("InvalidRebateIncomeDatail error,id=" + id + ",customerId=" + customerId, e);
            }
        }
        return successSize;
    }

    /**
     * 获取昨天过期的返利收入
     *
     * @param
     * @param
     * @return
     */
    public List<IObjectData> listYestdayInvalidRebateIncomeDetails(String tenantId, int offset, int size) {
        //当天0点>endTime>=前一天0点,可用》0
        IFilter endDateLtFilter = new Filter();
        endDateLtFilter.setOperator(Operator.LT);
        endDateLtFilter.setFieldName(RebateIncomeDetailConstants.Field.EndTime.apiName);
        endDateLtFilter.setFieldValues(Lists.newArrayList(DateUtil.getNowBenginTime() + ""));
        IFilter endDateGteFilter = new Filter();
        endDateGteFilter.setOperator(Operator.GTE);
        endDateGteFilter.setFieldName(RebateIncomeDetailConstants.Field.EndTime.apiName);
        endDateGteFilter.setFieldValues(Lists.newArrayList(DateUtil.getYesterdayBenginTime() + ""));
        IFilter availableRebateFilter = new Filter();
        availableRebateFilter.setOperator(Operator.GT);
        availableRebateFilter.setFieldName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
        availableRebateFilter.setFieldValues(Lists.newArrayList("0"));
        List<IFilter> filterList = Lists.newArrayList(endDateLtFilter, endDateGteFilter, availableRebateFilter);
        User user = RequestUtil.getSysteomUser(tenantId);
        List<IObjectData> incomeObjectDataList = this.queryByFieldFilterList(user, RebateIncomeDetailConstants.API_NAME, filterList, offset, size).getData();
        return incomeObjectDataList;

    }

    /**
     * 获取今天开始的返利收入
     *
     * @param
     * @param
     * @return
     */
    public List<IObjectData> listNowDayEnableRebateIncomeDetails(String tenantId, int offset, int size) {
        //startTime>=当天0点,可用=0
        IFilter startDateGteFilter = new Filter();
        startDateGteFilter.setOperator(Operator.GTE);
        startDateGteFilter.setFieldName(RebateIncomeDetailConstants.Field.StartTime.apiName);
        startDateGteFilter.setFieldValues(Lists.newArrayList(DateUtil.getNowBenginTime() + ""));
        IFilter availableRebateFilter = new Filter();
        availableRebateFilter.setOperator(Operator.EQ);
        availableRebateFilter.setFieldName(RebateIncomeDetailConstants.Field.AvailableRebate.apiName);
        availableRebateFilter.setFieldValues(Lists.newArrayList("0"));
        List<IFilter> filterList = Lists.newArrayList(startDateGteFilter, availableRebateFilter);
        User user = RequestUtil.getSysteomUser(tenantId);
        List<IObjectData> incomeObjectDataList = this.queryByFieldFilterList(user, RebateIncomeDetailConstants.API_NAME, filterList, offset, size).getData();
        return incomeObjectDataList;
    }

    public List<IObjectData> listByRefundIds(User user, List<String> refundIds) {
        QueryResult<IObjectData> queryResult = queryByFieldList(user, RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.Field.Refund.apiName, refundIds, 0, refundIds.size());
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return Lists.newArrayList();
        } else {
            return queryResult.getData();
        }
    }

    /**
     * 根据客户id查询为完成流程的预存款明细<br>
     *
     * @param user
     * @param customerId
     * @return
     */
    public List<IObjectData> listUnfinishedRebateIncomeDetailByCustomerId(User user, String customerId) {
        IFilter customerIdFilter = new Filter();
        customerIdFilter.setOperator(Operator.EQ);
        customerIdFilter.setFieldName(RebateIncomeDetailConstants.Field.Customer.apiName);
        customerIdFilter.setFieldValues(Lists.newArrayList(customerId));

        IFilter unfinishedFlowFilter = new Filter();
        unfinishedFlowFilter.setOperator(Operator.EQ);
        unfinishedFlowFilter.setFieldName(UdobjConstants.LIFE_STATUS_API_NAME);
        unfinishedFlowFilter.setFieldValues(Lists.newArrayList(SystemConstants.LifeStatus.UnderReview.value, SystemConstants.LifeStatus.InChange.value));

        List<IFilter> filterList = Lists.newArrayList(customerIdFilter, unfinishedFlowFilter);
        List<IObjectData> rebateIncomeDetailObjList = this.queryByFieldFilterList(user, RebateIncomeDetailConstants.API_NAME, filterList, 0, 100).getData();

        if (CollectionUtils.isEmpty(rebateIncomeDetailObjList)) {
            return Lists.newArrayList();
        } else {
            return rebateIncomeDetailObjList;
        }
    }

    public IObjectData getByRefundId(User user, String refundId) {
        QueryResult<IObjectData> queryResult = queryByField(user, RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.Field.Refund.apiName, refundId, 0, 1);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return null;
        } else {
            return queryResult.getData().get(0);
        }
    }

    public List<IObjectData> listInvalidDataByRefundIds(User user, List<String> refundIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, RebateIncomeDetailConstants.API_NAME, RebateIncomeDetailConstants.Field.Refund.apiName, refundIds, 0, refundIds.size());
        if (CollectionUtils.isNotEmpty(queryResult.getData())) {
            return queryResult.getData();
        }
        return new ArrayList<>();
    }

    public List<IObjectData> listInvalidDataByIds(User user, List<String> dataIds) {
        QueryResult<IObjectData> queryResult = queryInvalidDataByField(user, RebateIncomeDetailConstants.API_NAME, IObjectData.ID, dataIds, 0, 1);
        if (CollectionUtils.isEmpty(queryResult.getData())) {
            return Lists.newArrayList();
        }
        return queryResult.getData();
    }
}
