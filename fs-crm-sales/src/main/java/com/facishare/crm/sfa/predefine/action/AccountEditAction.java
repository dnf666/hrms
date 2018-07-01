package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 客户编辑操作
 * <p>
 * Created by liyiguang on 2017/7/13.
 */
@Slf4j
public class AccountEditAction extends SFAEditAction {

    @Override
    protected void before(Arg arg) {
        log.info("AccountEditAction>before()arg=" + JsonUtil.toJsonWithNullValues(arg));
        super.before(arg);
    }

    @Override
    protected Result doAct(Arg arg) {
        log.info("AccountEditAction>act()arg=" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);

        IObjectData updated = serviceFacade.updateObjectData(actionContext.getUser(), objectData);
        return StandardEditAction.Result.builder().objectData(ObjectDataDocument.of(updated)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("AccountAddAction>after()arg=" + JsonUtil.toJsonWithNullValues(arg));
        log.info("AccountAddAction>after()result=" + JsonUtil.toJsonWithNullValues(result));
        Map<String, Object> rstObjectData = (Map<String, Object>) result.getObjectData().get("CRMResponse");
        rstObjectData.put("_id",objectData.getId());
        result.setObjectData(ObjectDataDocument.of(rstObjectData));
        return result;
    }
}
