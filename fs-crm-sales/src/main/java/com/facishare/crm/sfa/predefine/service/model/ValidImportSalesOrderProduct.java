package com.facishare.crm.sfa.predefine.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by luxin on 2017/11/24.
 */
public interface ValidImportSalesOrderProduct {

    String ROW_NO_KEY = "RowNo";
    String PRICE_BOOK_ID_KEY = "PriceBookID";
    String PRODUCT_ID_KEY = "ProductID";
    String PRICE_BOOK_PRODUCT_ID_KEY = "PriceBookProductID";
    String PRICE_BOOK_PRODUCT_IMPORT_INFO_LIST_KEY = "PriceBookProductImportInfoList";


    @Data
    class Arg {
        @NotEmpty(message = "import_info is blank")
        @JsonProperty(value = PRICE_BOOK_PRODUCT_IMPORT_INFO_LIST_KEY)
        private List<PriceBookImportInfo> priceBookProductImportInfoList;
    }


    @Data
    class PriceBookImportInfo {
        @JsonProperty(value = ROW_NO_KEY)
        String rowNo;

        @NotEmpty
        @JsonProperty(value = PRICE_BOOK_ID_KEY)
        String priceBookId;

        @NotEmpty
        @JsonProperty(value = PRODUCT_ID_KEY)
        String productId;

        @JsonProperty(value = PRICE_BOOK_PRODUCT_ID_KEY)
        String priceBookProductId;
    }


    @Data
    @AllArgsConstructor
    class Result {
        @JsonProperty(value = PRICE_BOOK_PRODUCT_IMPORT_INFO_LIST_KEY)
        private List<PriceBookImportInfo> priceBookProductImportInfoList;
    }
}
