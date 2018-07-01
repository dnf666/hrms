package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by luxin on 2018/4/3.
 */
public interface HistoryCasesModel {
    @Data
    class Arg {
        private Integer offset;
        private Integer limit;

        @JsonProperty(value = "api_name")
        @JSONField(name = "api_name")
        private String apiName;

        @JsonProperty(value = "object_id")
        @JSONField(name = "object_id")
        //查询某个对象下的历史工单,objectId为对象的id
        private String objectId;

    }


    @Data
    @AllArgsConstructor
    class Result {
        private List<ObjectDataDocument> dataList;
        private Integer total;
    }
}
