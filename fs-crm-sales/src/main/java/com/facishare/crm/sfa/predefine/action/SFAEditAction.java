package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Maps;

import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.impl.IRule;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by luohuilong on 2017/12/5.
 */
@Slf4j
public class SFAEditAction extends SFAObjectSaveAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        //校验规则校验
        validateValidationRules(
                arg.getObjectData().toObjectData(),
                ObjectDataDocument.ofDataMap(arg.getDetails()),
                IRule.UPDATE);
    }


    @Override
    protected Result after(Arg arg, Result result) {
        log.info("SFAEditAction>after()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        log.info("SFAEditAction>after()>result={}" + JsonUtil.toJsonWithNullValues(result));

        Map<String, Object> tmpResult = Maps.newHashMap();
        tmpResult.put("success", result.getObjectData().get("CRMResponse"));
        tmpResult.put("_id", objectData.getId());
        result.setObjectData(ObjectDataDocument.of(tmpResult));
        return result;
    }}
