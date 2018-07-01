package com.facishare.crm.rest;

import com.facishare.crm.rest.dto.RenderPdfModel;
import com.facishare.rest.proxy.annotation.*;

import java.util.Map;

/**
 * Created by chenzs on 2018/2/5.
 */
@RestResource(value = "CRMTemplate", desc = "打印模板", contentType = "application/json")
public interface TemplateApi {
    @GET(value = "/print_template/init/{tenantId}", desc = "初始化")
    Object init(@PathParams Map map, @QueryParamsMap Map initDescribeApiNames, @HeaderMap Map<String, String> headers);

    @POST(value = "/template/renderPdf", desc = "获取打印模板PDF的path")  //tnPath
    RenderPdfModel.Result renderPdf(@Body RenderPdfModel.Arg arg, @HeaderMap Map<String, String> headers);
}