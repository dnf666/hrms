package com.facishare.crm.describebuilder;

import java.util.List;
import java.util.Map;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.describe.EmployeeFieldDescribe;
import com.facishare.paas.metadata.impl.describe.ObjectDescribe;
import com.facishare.paas.metadata.impl.describe.TextFieldDescribe;

public class ObjectDescribeBuilder {
    private IObjectDescribe objectDescribe;

    private ObjectDescribeBuilder() {
        objectDescribe = new ObjectDescribe();
        objectDescribe.setPackage("CRM");
        objectDescribe.setDefineType("package");
        objectDescribe.setDeleted(false);
        objectDescribe.setIsActive(true);
        objectDescribe.setCreateTime(System.currentTimeMillis());
        objectDescribe.setIsUdef(true);
    }

    public static ObjectDescribeBuilder builder() {
        return new ObjectDescribeBuilder();
    }

    public IObjectDescribe build() {
        return objectDescribe;
    }

    public ObjectDescribeBuilder apiName(String apiName) {
        objectDescribe.setApiName(apiName);
        return this;
    }

    public ObjectDescribeBuilder displayName(String displayName) {
        objectDescribe.setDisplayName(displayName);
        return this;
    }

    public ObjectDescribeBuilder tenantId(String tenantId) {
        objectDescribe.setTenantId(tenantId);
        return this;
    }

    public ObjectDescribeBuilder isUdef(Boolean isUdef) {
        objectDescribe.setIsUdef(isUdef);
        return this;
    }

    public ObjectDescribeBuilder createBy(String fsUserId) {
        objectDescribe.setCreatedBy(fsUserId);
        return this;
    }

    public ObjectDescribeBuilder fieldDescribes(List<IFieldDescribe> fieldDescribes) {
        objectDescribe.setFieldDescribes(fieldDescribes);
        ObjectDescribeExt objectDescribeExt = ObjectDescribeExt.of(objectDescribe);
        TextFieldDescribe extendObjDataIdTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(CommonConstants.EXTEND_OBJ_DATA_ID).label(CommonConstants.EXTEND_OBJ_DATA_ID).maxLength(256).build();
        TextFieldDescribe ownerDepartmentTextFieldDescribe = TextFieldDescribeBuilder.builder().apiName(SystemConstants.Field.OwnerDepartment.apiName).label(SystemConstants.Field.OwnerDepartment.label).maxLength(256).build();
        EmployeeFieldDescribe ownerEmployeeFieldDescribe = EmployeeFieldDescribeBuilder.builder().required(true).apiName(SystemConstants.Field.Owner.apiName).label(SystemConstants.Field.Owner.label).build();
        objectDescribeExt.addFieldIfAbsent(extendObjDataIdTextFieldDescribe);
        objectDescribeExt.addFieldIfAbsent(ownerDepartmentTextFieldDescribe);
        objectDescribeExt.addFieldIfAbsent(ownerEmployeeFieldDescribe);
        return this;
    }

    public ObjectDescribeBuilder iconIndex(Integer iconIndex) {
        objectDescribe.setIconIndex(iconIndex);
        return this;
    }

    public ObjectDescribeBuilder storeTableName(String storeTableName) {
        objectDescribe.setStoreTableName(storeTableName);
        return this;
    }

    public ObjectDescribeBuilder config(Map<String, Object> configMap) {
        objectDescribe.setConfig(configMap);
        return this;
    }

}
