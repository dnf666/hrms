package com.facishare.crm.sfa.utilities.constant;

public interface ProductConstants {
    enum Field {
        STATUS("product_status", "状态"),
        PRICE("price", "标准价格");

        private String apiName;
        private String label;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return this.apiName;
        }
    }

    enum Status {
        ON("1", "已上架"), OFF("2", "已下架"), HIDE("99", "已作废");

        private String status;
        private String name;

        Status(String status, String name) {
            this.status = status;
            this.name = name;
        }

        public String getStatus() {
            return this.status;
        }
    }
}
