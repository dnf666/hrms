package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.common.convert.ConvertUtil;
import com.facishare.crm.sfa.utilities.common.convert.ConvertorFactory;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by luxin on 2018/1/16.
 */
@Slf4j
public class ProductAddAction extends SFAObjectSaveAction {

    @Override
    protected void before(Arg arg) {
        log.info("ProductAddAction>before()>arg={}" + JsonUtil.toJsonWithNullValues(arg));

        super.before(arg);
    }

    @Override
    protected Result doAct(Arg arg) {

        String dataJson = ConvertorFactory.convertToOldFieldNamesStringForAddSpec(actionContext.getObjectApiName(), JsonUtil.toJsonWithNullValues(arg.getObjectData()));
        dataJson = ConvertorFactory.specialFieldConvert(actionContext.getObjectApiName(), dataJson);
        Gson gson = new GsonBuilder().create();
        arg.setObjectData(gson.fromJson(dataJson, ObjectDataDocument.class));

        this.objectData = arg.getObjectData().toObjectData();
        this.detailObjectData = ObjectDataDocument.ofDataMap(arg.getDetails());

        //设置默认业务类型
        setDefaultRecordType(objectData, objectDescribe);
        IObjectData result = serviceFacade.saveObjectData(actionContext.getUser(), objectData);
        return Result.builder().objectData(ObjectDataDocument.of(result)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("ProductAddAction>after()>result={}" + JsonUtil.toJsonWithNullValues(result));

        Map<String, Object> tmpResult = Maps.newHashMap();
        tmpResult.put("_id", result.getObjectData().get("CRMResponse"));
        result.setObjectData(ObjectDataDocument.of(tmpResult));
        return result;
    }


}
