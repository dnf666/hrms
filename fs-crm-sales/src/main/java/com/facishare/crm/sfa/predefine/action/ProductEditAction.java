package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.common.convert.ConvertUtil;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by luxin on 2018/1/16.
 */
@Slf4j
public class ProductEditAction extends SFAEditAction {

    @Override
    public void before(Arg arg) {
        log.info("ProductEditAction>before()>arg={}" + JsonUtil.toJsonWithNullValues(arg));

        super.before(arg);
    }

    @Override
    public Result doAct(Arg arg) {
        log.info("ProductEditAction>act()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);
        IObjectData updated = serviceFacade.updateObjectData(actionContext.getUser(), objectData);
        return StandardEditAction.Result.builder().objectData(ObjectDataDocument.of(updated)).build();
    }
}
