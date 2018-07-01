package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by liux on 2017/7/24.
 */
@Slf4j
public class OpportunityAddAction extends SFAAddAction {

    @Override
    protected void before(Arg arg) {
        log.info("OpportunityAddAction>before()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        super.before(arg);
    }

    @Override
    protected Result doAct(Arg arg) {
        log.info("OpportunityAddAction>act()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);
        IObjectData result = serviceFacade.saveObjectData(actionContext.getUser(), objectData);
        return Result.builder().objectData(ObjectDataDocument.of(result)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("OpportunityAddAction>after()>result={}" + JsonUtil.toJsonWithNullValues(result));
        super.after(arg, result);
        result.setObjectData(ObjectDataDocument.of((Map<String, Object>) result.getObjectData().get("CRMResponse")));
        return result;
    }
}
