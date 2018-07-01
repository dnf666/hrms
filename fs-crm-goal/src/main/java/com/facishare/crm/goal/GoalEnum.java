package com.facishare.crm.goal;
/**
 * Created by zhaopx on 2018/4/25.
 */
public interface GoalEnum {
    enum GoalTypeValue {

        CIRCLE("1", "部门"), EMPLOYEE("2", "人员");

        private String value;
        private String label;

        GoalTypeValue(String value, String label) {
            this.value = value;
            this.label = label;
        }
//todo test
        public String getValue() {
            return this.value;
        }

        public String getLabel() {
            return this.label;
        }
    }
}
