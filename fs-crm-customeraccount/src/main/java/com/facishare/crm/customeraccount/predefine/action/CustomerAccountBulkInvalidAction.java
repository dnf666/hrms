package com.facishare.crm.customeraccount.predefine.action;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.facishare.crm.customeraccount.constants.CustomerAccountConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.exception.CustomerAccountBusinessException;
import com.facishare.crm.customeraccount.exception.CustomerAccountErrorCode;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 批量走的是 CUstomerAccountInvalidAction一个一个调<br>
 */
@Slf4j
public class CustomerAccountBulkInvalidAction extends StandardBulkInvalidAction {
    @Autowired
    private CustomerAccountManager customerAccountManager;
    private String lifeStatus;

    @Override
    protected void validateObjectStatus() {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.validateObjectStatus();
        }
    }

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        if (actionContext.getAttributes() != null) {
            lifeStatus = actionContext.getAttribute("lifeStatus");
        }
        List<IObjectData> masterObjectDatas = objectDataList.stream().filter(objectData -> objectDescribe.getApiName().equals(objectData.getDescribeApiName())).collect(Collectors.toList());
        for (IObjectData customerAccountObj : masterObjectDatas) {
            String errorReason = customerAccountManager.canInvalidCustomerAccount(actionContext.getUser(), customerAccountObj);
            if (null != errorReason) {
                String errorMsgformat = "customerAccount cannot invalid,because %s,for id:%s name:%s,customerId:%s";
                String errorMsg = String.format(errorMsgformat, errorReason, customerAccountObj.getId(), customerAccountObj.getName(), customerAccountObj.get(CustomerAccountConstants.Field.Customer.apiName));
                log.info(errorMsg);
                throw new CustomerAccountBusinessException(CustomerAccountErrorCode.CAN_NOT_INVALID_CUSTOMER_ACCOUNT, errorMsg);
            }
        }
    }

    @Override
    public Result doAct(Arg arg) {
        log.debug("CustomerAccount BuklInvalid,objectDataList:{},lifeStatus:{}", JsonUtil.toJson(objectDataList), lifeStatus);
        if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
            objectDataList.stream().filter(objectData -> CustomerAccountConstants.API_NAME.equals(objectData.getDescribeApiName())).forEach(objectData -> {
                objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Invalid.value);
                customerAccountManager.updateLifeStatus(actionContext.getUser(), objectData);
            });
            invalidObjects(objectDataList);
            return Result.builder().objectDataList(ObjectDataDocument.ofList(objectDataList)).build();
        } else if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
            for (IObjectData data : objectDataList) {
                data.set(SystemConstants.Field.LifeStatus.apiName, lifeStatus);
                customerAccountManager.updateLifeStatus(actionContext.getUser(), data);
            }
            return Result.builder().objectDataList(ObjectDataDocument.ofList(objectDataList)).build();
        }
        return super.doAct(arg);
    }

    @Override
    protected void doFunPrivilegeCheck() {

    }

    @Override
    protected void doDataPrivilegeCheck(Arg arg) {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.doDataPrivilegeCheck(arg);
        }
    }
}
