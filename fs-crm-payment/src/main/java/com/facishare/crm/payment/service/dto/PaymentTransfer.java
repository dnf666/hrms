package com.facishare.crm.payment.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

public interface PaymentTransfer {

  @Data
  class Arg {
    private String tenantIds;
    private String host;
  }

  @Data
  class Result {
    private List<String> fails = new ArrayList<>();
  }
}
