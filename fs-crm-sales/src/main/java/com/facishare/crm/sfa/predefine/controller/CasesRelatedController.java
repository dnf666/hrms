package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import java.util.Map;

/**
 * Created by luxin on 2018/4/16.
 */
public class CasesRelatedController extends StandardRelatedController {
    @Override
    protected void fillRecordHeader(Map<String, Object> recordInfo, IObjectDescribe describe) {
        recordInfo.put("api_name", "service_log");
        describe.setDisplayName("服务记录");
    }
}
