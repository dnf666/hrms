package com.facishare.crm.payment.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

public interface PaymentInitialize {

  enum InitializeMode {
    ALL,
    NEW,
    CUSTOMER_PAYMENT,
    ORDER_PAYMENT,
    PAYMENT_PLAN
  }

  @Data
  class Arg {

    private String tenantIds;
    private InitializeMode mode;
  }

  @Builder
  @Data
  class Result {
    private List<String> fails;
  }
}
