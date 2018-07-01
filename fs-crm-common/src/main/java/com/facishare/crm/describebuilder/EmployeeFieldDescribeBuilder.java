package com.facishare.crm.describebuilder;

import com.facishare.paas.metadata.impl.describe.EmployeeFieldDescribe;

public class EmployeeFieldDescribeBuilder {
    private EmployeeFieldDescribe employeeFieldDescribe;

    private EmployeeFieldDescribeBuilder() {
        employeeFieldDescribe = new EmployeeFieldDescribe();
        employeeFieldDescribe.setUnique(false);
        employeeFieldDescribe.setStatus("released");
        employeeFieldDescribe.setIndex(true);
        employeeFieldDescribe.setDefineType("package");
        employeeFieldDescribe.setIsSingle(true);
        employeeFieldDescribe.setActive(true);
        employeeFieldDescribe.setIsExtend(false);
        employeeFieldDescribe.setFieldNum(null);
    }

    public static EmployeeFieldDescribeBuilder builder() {
        return new EmployeeFieldDescribeBuilder();
    }

    public EmployeeFieldDescribe build() {
        return employeeFieldDescribe;
    }

    public EmployeeFieldDescribeBuilder apiName(String apiName) {
        employeeFieldDescribe.setApiName(apiName);
        return this;
    }

    public EmployeeFieldDescribeBuilder label(String label) {
        employeeFieldDescribe.setLabel(label);
        return this;
    }

    public EmployeeFieldDescribeBuilder required(boolean required) {
        employeeFieldDescribe.setRequired(required);
        return this;
    }
}
