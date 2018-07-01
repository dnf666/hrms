package com.facishare.crm.sfa.utilities.constant;

public interface QuoteConstants {

    enum QuoteField {

        PRICEBOOKID("price_book_id", "价目表"),
        PRICEBOOKNAME("price_book_id__r", "价目表名称"),
        OPPORTUNITYID("opportunity_id", "商机"),
        QUOTEID("quote_id", "报价单"),
        ACCOUNTID("account_id", "客户");

        private String apiName;
        private String label;

        QuoteField(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return this.apiName;
        }
    }

    enum QuoteLinesField{

        PRICEBOOKPRODUCTID("price_book_product_id", "价目表产品"),
        PRODUCTID("product_id","产品ID"),
        PRODUCTNAME("product_name","产品");

        private String apiName;
        private String label;

        QuoteLinesField(String apiName, String label) {
            this.apiName = apiName;
            this.label = label;
        }

        public String getApiName() {
            return this.apiName;
        }
    }
}
