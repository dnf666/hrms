package com.facishare.crm.sfa.utilities.proxy;

import com.facishare.crm.sfa.utilities.proxy.model.BatchAddSpecModel;
import com.facishare.crm.sfa.utilities.proxy.model.BatchGetProductInfoModel;
import com.facishare.rest.proxy.annotation.Body;
import com.facishare.rest.proxy.annotation.GET;
import com.facishare.rest.proxy.annotation.HeaderMap;
import com.facishare.rest.proxy.annotation.POST;
import com.facishare.rest.proxy.annotation.PathParams;
import com.facishare.rest.proxy.annotation.RestResource;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2017/11/16.
 */
@RestResource(value = "CRM_SFA", desc = "CRM Rest API Call", contentType = "application/json")
public interface ProductProxy {

    @POST(value = "/crm/product/querylistbyids", desc = "获取产品对象信息")
    BatchGetProductInfoModel.Result batchGetProductInfo(@Body List<String> productIds, @HeaderMap Map<String, String> headers);

    @GET(value = "/crm/product/listAsGroup/{id}", desc = "获取产品的全部规格信息")
    BatchGetProductInfoModel.SpecResult listAsGroup(@HeaderMap Map<String, String> headers, @PathParams Map<String, String> pathParams);

    @POST(value = "/crm/product/addspecproduct", desc = "产品新增规格")
    BatchAddSpecModel.Result addSpec(@Body BatchAddSpecModel.Arg arg, @HeaderMap Map<String, String> headers);
}
