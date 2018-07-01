package com.facishare.crm.customeraccount.predefine.service.dto;

/**
 * Created by xujf on 2017/10/21.
 */
public class EnableCustomerAccountTaskModel {
    /**
     * 客户id<br>
     */
    String customerId;
    //0:未开启  1:开启中  2:开启成功   3:开启失败
    int status;

    /**
     * 客户名称<br>
     */
    String name;

    public EnableCustomerAccountTaskModel(String customerId, int status, String name) {
        this.customerId = customerId;
        this.status = status;
        this.name = name;
    }

    public static class CustomerAccountTaskBuider {

        String customerId;
        //0:未开启  1:开启中  2:开启成功   3:开启失败
        int status;

        /**
         * 客户名称<br>
         */
        String name;

        EnableCustomerAccountTaskModel customerAccountTaskModel;

        public CustomerAccountTaskBuider withCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public CustomerAccountTaskBuider withName(String name) {
            this.name = name;
            return this;
        }

        public CustomerAccountTaskBuider withStatus(int status) {
            this.status = status;
            return this;
        }

        public EnableCustomerAccountTaskModel builder() {
            return new EnableCustomerAccountTaskModel(customerId, status, name);
        }

    }

}
