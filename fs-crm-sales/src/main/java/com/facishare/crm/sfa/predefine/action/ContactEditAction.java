package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.crm.sfa.utilities.util.PhoneUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cqx
 * @date 2018/3/19 16:10
 */
@Slf4j
public class ContactEditAction extends SFAEditAction {


    @Override
    protected void before(Arg arg) {
        super.before(arg);

        ObjectDataDocument objectData = arg.getObjectData();
        PhoneUtil.dealPhone(objectData);
    }

    @Override
    public Result doAct(Arg arg) {
        log.info("ContactEditAction>act()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);
        IObjectData updated = serviceFacade.updateObjectData(actionContext.getUser(), objectData);
        return StandardEditAction.Result.builder().objectData(ObjectDataDocument.of(updated)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("ContactEditAction>after()arg=" + JsonUtil.toJsonWithNullValues(arg));
        log.info("ContactEditAction>after()result=" + JsonUtil.toJsonWithNullValues(result));
        Map<String, Object> rstObjectData = (Map<String, Object>) result.getObjectData().get("CRMResponse");
        rstObjectData.put("_id",objectData.getId());
        result.setObjectData(ObjectDataDocument.of(rstObjectData));
        return result;
    }

}
