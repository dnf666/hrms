package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2017/11/13.
 */
public interface BatchGetProductModel {

  @Data
  class Arg {
    @JSONField(name = "M1")
    @NotEmpty(message = "priceBookId is blank")
    private String priceBookId;

    @JSONField(name = "M2")
    @NotEmpty(message = "productIds is empty")
    private List<String> productIds;
  }

  @Data
  @AllArgsConstructor
  class Result {
    @JSONField(name = "M1")
    private List<ObjectDataDocument> dataList;
  }


}
