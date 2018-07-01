package com.facishare.crm.payment.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

public interface PaymentTransferDispatch {

  @Data
  class Arg {
    private Boolean all = false;
    private Integer offset = 0;
    private Integer limit = 1000;
    private Integer max = Integer.MAX_VALUE;
    private Integer poolSize = 1;
    private String tenantIds; //企业id
    private String host; //地址，如 http://172.17.21.121:8006

    public Arg() {}

    public Arg(String tenantIds, String host) {
      this.tenantIds = tenantIds;
      this.host = host;
    }
  }

  @Data
  class Result {
    private List<String> fails = new ArrayList<>();
  }
}
