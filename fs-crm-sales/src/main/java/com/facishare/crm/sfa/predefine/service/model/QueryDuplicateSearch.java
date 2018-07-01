package com.facishare.crm.sfa.predefine.service.model;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * Created by rensx on 2017/7/31.
 */
public class QueryDuplicateSearch {
    @Data
    public class Arg {
        @JSONField(name = "M1")
        private String masterApiName;

        @JSONField(name = "M2")
        private List<String> slaveApiNames = new LinkedList<>();

        @JSONField(name = "M3")
        private int dsType;

        @JSONField(name = "M4")
        private JSONObject data;

        @JSONField(name = "M5")
        private int pageSize;

        @JSONField(name = "M6")
        private int pageNumber;

        @JSONField(name = "M7")
        private boolean isNeedDS;
    }

    @Data
    public class Result {

    }
}
