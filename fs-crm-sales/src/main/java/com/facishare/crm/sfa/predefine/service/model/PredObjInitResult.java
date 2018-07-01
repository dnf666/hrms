package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

public interface PredObjInitResult {
  @Data
    class Arg implements Serializable {
    @JSONField(name = "M1")
    private String tenantId;

    @JSONField(name = "M2")
    private List<String> describeApiNames;

    @JSONField(name = "M3")
    private List<String> operations;


  }

  @Data
  @Builder
  class Result {
    @JSONField(name = "M1")
    String rtnMsg;
  }
}
