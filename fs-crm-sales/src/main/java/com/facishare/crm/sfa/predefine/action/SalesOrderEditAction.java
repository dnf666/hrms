package com.facishare.crm.sfa.predefine.action;

import java.util.Map;

import com.facishare.crm.sfa.utilities.common.convert.ConvertUtil;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户编辑操作
 * <p>
 * Created by liyiguang on 2017/7/13.
 */
@Slf4j
public class SalesOrderEditAction extends SFAEditAction {

    @Override
    public void before(Arg arg) {
        log.info("SalesOrderEditAction>before()>arg={}" + JsonUtil.toJsonWithNullValues(arg));

        super.before(arg);

        ConvertUtil.mergeMDForSalesOrder(arg.getObjectData(), arg.getDetails(), "SalesOrderProductObj");
        //arg.setObjectData(objectDataArg);
    }

    @Override
    public Result doAct(Arg arg) {
        log.info("SalesOrderEditAction>act()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);
        IObjectData updated = serviceFacade.updateObjectData(actionContext.getUser(), objectData);
        return StandardEditAction.Result.builder().objectData(ObjectDataDocument.of(updated)).build();
    }

    @Override
    public Result after(Arg arg, Result result) {
        log.info("SalesOrderEditAction>after()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        log.info("SalesOrderEditAction>after()>result={}" + JsonUtil.toJsonWithNullValues(result));
        result.setObjectData(ObjectDataDocument.of((Map<String, Object>) result.getObjectData().get("CRMResponse")));
        return result;
    }
}
