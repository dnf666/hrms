package com.facishare.crm.erpstock.predefine.service.model.base;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * @author linchf
 * @date 2018/5/10
 */
@Data
public class BaseModel implements Serializable {
    @Data
    public static class Arg {
        @JsonProperty("object_data_id")
        @SerializedName("object_data_id") //兼容FCP的序列化
        private String objectDataId;

        @JsonProperty("object_data")
        ObjectDataDocument objectData;
    }

    @Data
    public static class Result {
        private ObjectDataDocument objectData;
    }
}
