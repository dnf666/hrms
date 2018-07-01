package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.manager.SfaCustomerAccountManager;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户对象创建操作
 * <p>
 * Created by liyiguang on 2017/7/12.
 */
@Slf4j
public class AccountAddAction extends SFAAddAction {
    private SfaCustomerAccountManager sfaCustomerAccountManager;

    @Override
    protected void before(Arg arg) {
        log.info("AccountAddAction>before()arg=" + JsonUtil.toJsonWithNullValues(arg));
        super.before(arg);
        this.sfaCustomerAccountManager = SpringUtil.getContext().getBean(SfaCustomerAccountManager.class);

    }

    @Override
    protected Result doAct(Arg arg) {
        log.info("AccountAddAction>act()arg=" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);
        IObjectData result = serviceFacade.saveObjectData(actionContext.getUser(), objectData);
        return Result.builder().objectData(ObjectDataDocument.of(result)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("AccountAddAction>after()arg=" + JsonUtil.toJsonWithNullValues(arg));
        log.info("AccountAddAction>after()result=" + JsonUtil.toJsonWithNullValues(result));
        super.after(arg, result);
        result.setObjectData(ObjectDataDocument.of((Map<String, Object>) result.getObjectData().get("CRMResponse")));
        return result;
    }

}
