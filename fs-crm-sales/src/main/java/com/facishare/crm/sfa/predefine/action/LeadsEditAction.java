package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by zhaopx on 2018/3/21.
 */
@Slf4j
public class LeadsEditAction extends SFAEditAction {
    @Override
    protected void before(Arg arg) {
        log.info("LeadsEditAction>before()arg=" + JsonUtil.toJsonWithNullValues(arg));
        super.before(arg);
    }

    @Override
    protected Result doAct(Arg arg) {
        log.info("LeadsEditAction>act()arg=" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);

        IObjectData updated = serviceFacade.updateObjectData(actionContext.getUser(), objectData);
        return StandardEditAction.Result.builder().objectData(ObjectDataDocument.of(updated)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("LeadsAddAction>after()arg=" + JsonUtil.toJsonWithNullValues(arg));
        log.info("LeadsAddAction>after()result=" + JsonUtil.toJsonWithNullValues(result));
        Map<String, Object> rstObjectData = (Map<String, Object>) result.getObjectData().get("CRMResponse");
        rstObjectData.put("_id",objectData.getId());
        result.setObjectData(ObjectDataDocument.of(rstObjectData));
        return result;
    }
}
