package com.facishare.crm.sfa.predefine.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.facishare.crm.sfa.utilities.common.convert.ConvertorFactory;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.PreDefineAction;
import com.facishare.paas.metadata.api.IObjectData;

public abstract class BaseSaveSFAAction<A, R> extends PreDefineAction<A, R> {
    private IObjectData toSFAObjectData(IObjectData objectData) {
        String dataJson = ConvertorFactory.convertToOldFieldNamesString(actionContext.getObjectApiName(), JsonUtil.toJsonWithNullValues(ObjectDataDocument.of(objectData)));
        dataJson = ConvertorFactory.specialFieldConvert(actionContext.getObjectApiName(), dataJson);
        Gson gson = new GsonBuilder().create();
        objectData = gson.fromJson(dataJson, ObjectDataDocument.class).toObjectData();
        return objectData;
    }
}
