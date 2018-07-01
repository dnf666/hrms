package com.facishare.crm.sfa.predefine.action;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.crm.sfa.predefine.exception.SFABusinessException;
import com.facishare.crm.sfa.utilities.common.convert.ConvertorFactory;
import com.facishare.crm.sfa.utilities.proxy.ProductProxy;
import com.facishare.crm.sfa.utilities.proxy.model.BatchAddSpecModel;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.PreDefineAction;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/1/17.
 */
public class ProductAddSpecAction extends PreDefineAction<ProductAddSpecAction.Arg, ProductAddSpecAction.Result> {
    private ProductProxy productProxy = (ProductProxy) SpringUtil.getContext().getBean("productProxy");

    @Override
    protected List<String> getFuncPrivilegeCodes() {
        return null;
    }

    @Override
    protected List<String> getDataPrivilegeIds(Arg arg) {
        return null;
    }

    @Override
    protected Result doAct(Arg arg) {
        BatchAddSpecModel.Arg batchAddSpecArg;
        batchAddSpecArg = getBatchAddSpecArg(arg);

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("x-fs-ei", actionContext.getTenantId());
        headers.put("x-fs-userInfo", actionContext.getUser().getUserId());

        BatchAddSpecModel.Result addSpecResult = productProxy.addSpec(batchAddSpecArg, headers);

        ProductAddSpecAction.Result result = new ProductAddSpecAction.Result();
        if (addSpecResult.getSuccess()) {
            result.setSuccess(addSpecResult.getSuccess());
        } else {
            throw new SFABusinessException(addSpecResult.getMessage(), addSpecResult::getErrorCode);
        }
        return result;
    }


    private BatchAddSpecModel.Arg getBatchAddSpecArg(Arg arg) {
        BatchAddSpecModel.Arg batchAddSpecArg;
        String dataJson = ConvertorFactory.convertToOldFieldNamesStringForAddSpec(actionContext.getObjectApiName(), JsonUtil.toJsonWithNullValues(arg.getObjectData()));
        dataJson = ConvertorFactory.specialFieldConvert(actionContext.getObjectApiName(), dataJson);
        Gson gson = new GsonBuilder().create();
        batchAddSpecArg = gson.fromJson(dataJson, BatchAddSpecModel.Arg.class);
        return batchAddSpecArg;
    }

    @Data
    public static class Arg {
        @JSONField(name = "M1")
        //兼容接口数据规格
        @JsonProperty("object_data")
        ObjectDataDocument objectData;
    }

    @Data
    public static class Result {
        @JSONField(name = "M1")
        Boolean success;
    }
}
