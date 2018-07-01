package com.facishare.crm.sfa.predefine.service;

import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.service.impl.ObjectDescribeServiceImpl;
import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.crm.sfa.utilities.proxy.ProductProxy;
import com.facishare.crm.sfa.utilities.proxy.model.BatchGetProductInfoModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.metadata.restdriver.ObjectDataConverter;
import com.facishare.paas.appframework.metadata.restdriver.ObjectDataConverterManager;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@ServiceModule("product")
@Service
@Slf4j
public class ProductHttpService {
    @Autowired
    private ProductProxy productProxy;
    @Autowired
    private ObjectDescribeServiceImpl objectDescribeService;
    @Autowired
    private ObjectDataConverterManager objectDataConverterManager;

    @ServiceMethod("spec_list")
    public Result findSpecListByProductID(JSONObject arg, ServiceContext context) throws MetadataServiceException {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("x-fs-ei", context.getTenantId());
        headers.put("x-fs-userInfo", context.getUser().getUserId());
        Map<String, String> pathParams = Maps.newHashMap();
        pathParams.put("id", arg.getString("product_id"));
        BatchGetProductInfoModel.SpecResult specResult = productProxy.listAsGroup(headers, pathParams);

        List originDataList = specResult.getValue().get("Items");
        ObjectDataConverter converter = objectDataConverterManager.getObjectDataConverter("ProductObj");
        IObjectDescribe describe = objectDescribeService.findByTenantIdAndDescribeApiName(context.getTenantId(), "ProductObj");
        List<IObjectData> dataList = converter.toDataObjects(originDataList, describe);
        Result result = Result.builder().dataList(ObjectDataDocument.ofList(dataList)).build();
        return result;
    }

    @Data
    @Builder
    public static class Result {
        @JSONField(name = "M1")
        private List<ObjectDataDocument> dataList;
    }
}
