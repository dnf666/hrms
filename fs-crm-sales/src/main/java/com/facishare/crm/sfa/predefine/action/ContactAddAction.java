package com.facishare.crm.sfa.predefine.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.sfa.utilities.common.convert.ConvertorFactory;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.crm.sfa.utilities.util.PhoneUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;

import org.springframework.util.ObjectUtils;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cqx
 * @date 2018/3/19 16:09
 */
@Slf4j
public class ContactAddAction extends SFAAddAction {
    //特殊处理10个电话手机字段
    @Override
    protected void before(Arg arg) {
        super.before(arg);

        ObjectDataDocument objectData = arg.getObjectData();
        PhoneUtil.dealPhone(objectData);
    }

    @Override
    protected Result doAct(Arg arg) {

        String dataJson = ConvertorFactory.convertToOldFieldNamesString(actionContext.getObjectApiName(), JsonUtil.toJsonWithNullValues(arg.getObjectData()));
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
        log.info("ContactAdd>after()>result={}" + JsonUtil.toJsonWithNullValues(result));
        super.after(arg, result);
        Map<String, Object> tmpResult = JSON.parseObject(result.getObjectData().get("CRMResponse").toString());
        IObjectData iObjectData = null;
        if (!ObjectUtils.isEmpty(tmpResult.get("_id"))) {
            iObjectData = serviceFacade.findObjectData(actionContext.getUser(), tmpResult.get("_id").toString(), objectDescribe);
        } else {
            iObjectData = new ObjectData();
        }
        iObjectData.set("IsDuplicate", tmpResult.get("IsDuplicate") == null ? false : tmpResult.get("IsDuplicate"));
        result.setObjectData(ObjectDataDocument.of(ObjectDataExt.of(iObjectData).toMap()));
        return result;
    }

}
