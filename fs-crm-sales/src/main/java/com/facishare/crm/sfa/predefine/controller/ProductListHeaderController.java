package com.facishare.crm.sfa.predefine.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.facishare.paas.appframework.common.util.DocumentBaseEntity;
import com.facishare.paas.appframework.common.util.Tuple;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductListHeaderController extends StandardListHeaderController {
    protected Result doService(Arg arg) {
        Result ret = super.doService(arg);
        List<DocumentBaseEntity> fieldLs = Lists.newArrayList();
        fieldLs.add(buildFieldConfig("name"));
        fieldLs.add(buildFieldConfig("product_code"));
        fieldLs.add(buildFieldConfig("product_status"));
        fieldLs.add(buildFieldConfig("category"));
        fieldLs.add(buildFieldConfig("price"));
        fieldLs.add(buildFieldConfig("unit"));
        fieldLs.add(buildFieldConfig("product_line"));
        fieldLs.add(buildFieldConfig("barcode"));
        fieldLs.add(buildFieldConfig("created_by"));
        fieldLs.add(buildFieldConfig("last_modified_time"));
        fieldLs.add(buildFieldConfig("owner"));
        fieldLs.add(buildFieldConfig("lock_status"));
        ret.setFieldList(fieldLs);
        return ret;
    }

    private DocumentBaseEntity buildFieldConfig(String fieldName) {
        Map<String, Object> fieldConfig = Maps.newHashMap();
        fieldConfig.put(fieldName, true);
        return new DocumentBaseEntity(fieldConfig);
    }

}
