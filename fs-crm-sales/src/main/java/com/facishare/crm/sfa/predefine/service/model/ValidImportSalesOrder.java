package com.facishare.crm.sfa.predefine.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by luxin on 2017/11/24.
 */
public interface ValidImportSalesOrder {

    String ROW_NO_KEY = "RowNo";
    String CUSTOMER_ID_KEY = "CustomerId";
    String PRICE_BOOK_NAME = "PriceBookName";
    String PRICE_BOOK_ID_KEY = "PriceBookID";
    String EMPLOYEE_ID_KEY = "EmployeeId";
    String PRICE_BOOK_IMPORT_INFO_LIST_KEY = "PriceBookImportInfoList";


    @Data
    class ValidPriceBookImportInfoKey {
        String customerId;
        String priceBookId;

        public ValidPriceBookImportInfoKey(String customerId,String priceBookId){
            this.customerId=customerId;
            this.priceBookId=priceBookId;
        }

    }


    @Data
    class Arg {
        @NotEmpty(message = "import_info is blank")
        @JsonProperty(value = PRICE_BOOK_IMPORT_INFO_LIST_KEY)
        private List<PriceBookImportInfo> priceBookImportInfoList;
    }


    @Data
    class PriceBookImportInfo {
        @JsonProperty(value = ROW_NO_KEY)
        String rowNo;

        @NotEmpty
        @JsonProperty(value = EMPLOYEE_ID_KEY)
        Integer userId;

        @JsonProperty(value = CUSTOMER_ID_KEY)
        String customerId;

        @NotEmpty
        @JsonProperty(value = PRICE_BOOK_NAME)
        String priceBookName;

        @JsonProperty(value = PRICE_BOOK_ID_KEY)
        String priceBookId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ValidPriceBookImportInfo {
        @JsonProperty(value = ROW_NO_KEY)
        int rowNo;

        @NotEmpty
        @JsonProperty(value = CUSTOMER_ID_KEY)
        String customerId;

        @NotEmpty
        @JsonProperty(value = PRICE_BOOK_ID_KEY)
        String priceBookId;

        Boolean isApply=false;
    }

    @Data
    @AllArgsConstructor
    class Result {
        @JsonProperty(value = PRICE_BOOK_IMPORT_INFO_LIST_KEY)
        private List<PriceBookImportInfo> priceBookImportInfoList;
    }
}