package com.facishare.crm.sfa.utilities.proxy.model;

import com.facishare.paas.appframework.metadata.restdriver.dto.FindBySearchQuery;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2017/11/16.
 */
public interface BatchGetProductInfoModel {

  @Data
  @AllArgsConstructor
  class Arg {
    List<String> productIds;
  }

  @Data
  class Result {
    String message;
    Integer errorCode;
    List<Map<String, Object>> value;
    Boolean success;
  }



  @Data
  class SpecResult {
    boolean success;
    String message;
    int errorCode;
    Map<String,List> value;

  }


}
