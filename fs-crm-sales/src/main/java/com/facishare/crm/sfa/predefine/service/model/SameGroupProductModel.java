package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by luxin on 2017/11/15.
 */
public interface SameGroupProductModel {

  @Data
  class Arg {
    @JSONField(name = "M1")
    @NotEmpty(message = "priceBookId is blank")
    private String priceBookId;

    @JSONField(name = "M2")
    @NotEmpty(message = "productId is blank")
    private String productId;

    @JSONField(name = "M3")
    private Integer offset = 0; //默认为0

    @JSONField(name = "M4")
    private Integer limit = 20; //默认分页大小是20
  }

  @Data
  @AllArgsConstructor
  class Result {
    @JSONField(name = "M1")
    private List<ObjectDataDocument> dataList;
  }


}
