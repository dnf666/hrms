package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.common.convert.ConvertUtil;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by lilei on 2017/7/24.
 */
@Slf4j
public class SalesOrderAddAction extends SFAAddAction {

    @Override
    protected void before(Arg arg) {
        log.info("SalesOrderAddAction>before()>arg={}" + JsonUtil.toJsonWithNullValues(arg));

        super.before(arg);
        ConvertUtil.mergeMDForSalesOrder(arg.getObjectData(), arg.getDetails(), "SalesOrderProductObj");
        //arg.setObjectData(objectDataArg);
    }

    @Override
    protected Result doAct(Arg arg) {
        log.info("SalesOrderAddAction>act()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);
        IObjectData result = serviceFacade.saveObjectData(actionContext.getUser(), objectData);
        return Result.builder().objectData(ObjectDataDocument.of(result)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("SalesOrderAddAction>after()>result={}" + JsonUtil.toJsonWithNullValues(result));
        super.after(arg, result);
        result.setObjectData(ObjectDataDocument.of((Map<String, Object>) result.getObjectData().get("CRMResponse")));
        return result;
    }
}
