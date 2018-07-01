package com.facishare.crm.erpstock.predefine.service.model;

import com.facishare.crm.erpstock.predefine.service.model.base.BaseModel;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author linchf
 * @date 2018/5/16
 */
public class ErpStockInvalidOrQueryModel extends BaseModel {
    @Data
    public static class Arg {
        @JsonProperty("object_data_id")
        @SerializedName("object_data_id") //兼容FCP的序列化
        private String objectDataId;

        @JsonProperty("erp_warehouse_id")
        @SerializedName("erp_warehouse_id") //兼容FCP的序列化
        private String erpWarehouseId;

        @JsonProperty("product_id")
        @SerializedName("product_id") //兼容FCP的序列化
        private String productId;

    }

}
