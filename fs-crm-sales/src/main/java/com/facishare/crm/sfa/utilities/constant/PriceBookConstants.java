package com.facishare.crm.sfa.utilities.constant;

public interface PriceBookConstants {
    String API_NAME = "PriceBookObj";
    String API_NAME_PRODUCT = "PriceBookProductObj";

    enum Field {
        ACTIVESTATUS("active_status", "启用状态"),
        STARTDATE("start_date", "有效开始时间"),
        ENDDATE("end_date", "有效开始时间"),
        ISSTANDARD("is_standard", "是否默认");

        private String apiName;
        private String label;

        Field(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return this.apiName;
        }

        public String getLabel() {
            return this.label;
        }
    }

    enum ProductField {
        PRICEBOOKID("pricebook_id", "价目表主键"), PRICEBOOKPRICE("pricebook_price", "价目表价格"),
        PRODUCTID("product_id", "产品主键"), PRODUCTNAME("product_name", "产品名称");
        private String apiName;
        private String label;

        ProductField(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return this.apiName;
        }
    }

    enum ActiveStatus {
        ON("1", "启用"), OFF("0", "禁用");
        private String status;
        private String name;

        ActiveStatus(String status, String name) {
            this.status = status;
            this.name = name;
        }

        public String getStatus() {
            return status;
        }
    }
}
