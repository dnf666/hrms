package com.facishare.crm.customeraccount.predefine.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/10/16.
 * 客户账户指允许跟新结算方式和信用额度，在代码层面就是前端传什么值就更新什么值<br>
 */
@Slf4j
@Component
public class CustomerAccountEditAction extends StandardEditAction {
    @SuppressWarnings("rawtypes")
    @Override
    protected void before(Arg arg) {
        super.before(arg);

        IObjectData newObjectData = arg.getObjectData().toObjectData();
        BigDecimal creditQuota = ObjectDataUtil.getBigDecimal(newObjectData, CustomerAccountConstants.Field.CreditQuota.apiName);//newObjectData.get(CustomerAccountConstants.Field.CreditQuota.apiName, BigDecimal.class);
        if (creditQuota.compareTo(BigDecimal.ZERO) == -1) {
            log.warn("信用额度不能为负数，for customerAccountId:{}", newObjectData.getId());
            throw new ValidateException("信用额度不能为负数");
        }
        checkReadOnlyField(objectData, newObjectData);
        List settleTypes = (List) objectData.get(CustomerAccountConstants.Field.SettleType.apiName);
        log.debug("settleTypes=" + settleTypes);
        List<String> newSettleTypes = new ArrayList<>(settleTypes.size());
        for (Object settleType : settleTypes) {
            newSettleTypes.add(String.valueOf(settleType));
        }
        objectData.set(CustomerAccountConstants.Field.SettleType.apiName, newSettleTypes);
        IObjectData oldObjectData = refindObjectData();
        BigDecimal oldPrepayBalance = oldObjectData.get(CustomerAccountConstants.Field.PrepayBalance.apiName, BigDecimal.class);
        BigDecimal oldPrepayAvailableBalance = oldObjectData.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class);
        BigDecimal oldPrepayLockedBalance = oldObjectData.get(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, BigDecimal.class);
        BigDecimal oldRebateBalance = oldObjectData.get(CustomerAccountConstants.Field.RebateBalance.apiName, BigDecimal.class);
        BigDecimal oldRebateAvailableBalance = oldObjectData.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class);
        BigDecimal oldRebateLockedBalance = oldObjectData.get(CustomerAccountConstants.Field.RebateLockedBalance.apiName, BigDecimal.class);
        objectData.set(CustomerAccountConstants.Field.PrepayBalance.apiName, oldPrepayBalance);
        objectData.set(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, oldPrepayAvailableBalance);
        objectData.set(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, oldPrepayLockedBalance);
        objectData.set(CustomerAccountConstants.Field.RebateBalance.apiName, oldRebateBalance);
        objectData.set(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, oldRebateAvailableBalance);
        objectData.set(CustomerAccountConstants.Field.RebateLockedBalance.apiName, oldRebateLockedBalance);
        log.debug("Before CustomerAccount Edit,oldObjectData:{},objectData:{}", oldObjectData.toJsonString(), objectData.toJsonString());
    }

    private void checkReadOnlyField(IObjectData oldObjectData, IObjectData newObjectData) {
        BigDecimal oldPrepayBalance = oldObjectData.get(CustomerAccountConstants.Field.PrepayBalance.apiName, BigDecimal.class);
        BigDecimal oldPrepayAvailableBalance = oldObjectData.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class);
        BigDecimal oldPrepayLockedBalance = oldObjectData.get(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, BigDecimal.class);
        BigDecimal oldRebateBalance = oldObjectData.get(CustomerAccountConstants.Field.RebateBalance.apiName, BigDecimal.class);
        BigDecimal oldRebateAvailableBalance = oldObjectData.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class);
        BigDecimal oldRebateLockedBalance = oldObjectData.get(CustomerAccountConstants.Field.RebateLockedBalance.apiName, BigDecimal.class);

        BigDecimal newPrepayBalance = newObjectData.get(CustomerAccountConstants.Field.PrepayBalance.apiName, BigDecimal.class);
        BigDecimal newPrepayAvailableBalance = newObjectData.get(CustomerAccountConstants.Field.PrepayAvailableBalance.apiName, BigDecimal.class);
        BigDecimal newPrepayLockedBalance = newObjectData.get(CustomerAccountConstants.Field.PrepayLockedBalance.apiName, BigDecimal.class);
        BigDecimal newRebateBalance = newObjectData.get(CustomerAccountConstants.Field.RebateBalance.apiName, BigDecimal.class);
        BigDecimal newRebateAvailableBalance = newObjectData.get(CustomerAccountConstants.Field.RebateAvailableBalance.apiName, BigDecimal.class);
        BigDecimal newRebateLockedBalance = newObjectData.get(CustomerAccountConstants.Field.RebateLockedBalance.apiName, BigDecimal.class);
        List<String> cannotEditFields = Lists.newArrayList();
        if (oldPrepayBalance.compareTo(newPrepayBalance) != 0) {
            cannotEditFields.add(CustomerAccountConstants.Field.PrepayBalance.label);
        }
        if (oldPrepayAvailableBalance.compareTo(newPrepayAvailableBalance) != 0) {
            cannotEditFields.add(CustomerAccountConstants.Field.PrepayAvailableBalance.label);
        }
        if (oldPrepayLockedBalance.compareTo(newPrepayLockedBalance) != 0) {
            cannotEditFields.add(CustomerAccountConstants.Field.PrepayLockedBalance.label);
        }
        if (oldRebateBalance.compareTo(newRebateBalance) != 0) {
            cannotEditFields.add(CustomerAccountConstants.Field.RebateBalance.label);
        }
        if (oldRebateAvailableBalance.compareTo(newRebateAvailableBalance) != 0) {
            cannotEditFields.add(CustomerAccountConstants.Field.RebateAvailableBalance.label);
        }
        if (oldRebateLockedBalance.compareTo(newRebateLockedBalance) != 0) {
            cannotEditFields.add(CustomerAccountConstants.Field.RebateLockedBalance.label);
        }
        if (CollectionUtils.isNotEmpty(cannotEditFields)) {
            throw new ValidateException(Joiner.on(",").join(cannotEditFields).concat("不可编辑").replaceAll("\\(元\\)", ""));
        }
    }

    private IObjectData refindObjectData() {
        String id = objectData.getId();
        IObjectData objectData = serviceFacade.findObjectData(actionContext.getTenantId(), id, objectDescribe);
        return objectData;
    }

    @Override
    protected void doDataPrivilegeCheck(Arg arg) {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.doDataPrivilegeCheck(arg);
        }
    }

    @Override
    protected void doFunPrivilegeCheck() {

    }
}
