package com.facishare.crm.sfa.predefine.action;

import java.util.List;
import java.util.Set;

import com.facishare.crm.sfa.utilities.common.convert.ConvertorFactory;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.metadata.api.DBRecord;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by luohuilong on 2017/12/5.
 */
@Slf4j
public class SFAObjectSaveAction extends BaseObjectSaveAction {

    @Override
    protected void init() {
        Set<String> apiNames = Sets.newHashSet(arg.getDetails().keySet());
        apiNames.add(actionContext.getObjectApiName());
        objectDescribes = serviceFacade.findObjects(actionContext.getTenantId(), apiNames);
        if (this.objectDescribe == null) {
            objectDescribe = objectDescribes.get(actionContext.getObjectApiName());
        }
        this.objectData = arg.getObjectData().toObjectData();
        this.detailObjectData = ObjectDataDocument.ofDataMap(arg.getDetails());
        stopWatch.lap("init");
    }

    @Override
    protected String getButtonApiName() {
        return super.getButtonApiName();
    }

    @Override
    protected String getIRule() {
        return null;
    }

    @Override
    protected ObjectAction getObjectAction() {
        return null;
    }

    @Override
    protected List<String> getFuncPrivilegeCodes() {
        return null;
    }

    @Override
    protected List<String> getDataPrivilegeIds(Arg arg) {
        return null;
    }

    @Override
    protected void before(Arg arg) {
        init();
        callValidationFunction();
        log.info("callValidationFunction");
    }

    @Override
    protected Result doAct(Arg arg) {
        Object outOwner = arg.getObjectData().get(DBRecord.OUT_OWNER);
        if (outOwner != null && outOwner instanceof List) {
            if (((List) outOwner).size() > 0) {
                arg.getObjectData().put(DBRecord.OUT_OWNER, ((List) outOwner).get(0));
            } else {
                arg.getObjectData().remove(DBRecord.OUT_OWNER);
            }
        }

        String dataJson = ConvertorFactory.convertToOldFieldNamesString(actionContext.getObjectApiName(), JsonUtil.toJsonWithNullValues(arg.getObjectData()));
        dataJson = ConvertorFactory.specialFieldConvert(actionContext.getObjectApiName(), dataJson);
        Gson gson = new GsonBuilder().create();
        arg.setObjectData(gson.fromJson(dataJson, ObjectDataDocument.class));

        this.objectData = arg.getObjectData().toObjectData();
        this.detailObjectData = ObjectDataDocument.ofDataMap(arg.getDetails());

        //设置默认业务类型
        setDefaultRecordType(objectData, objectDescribe);

        return null;
    }
}
