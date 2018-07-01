package com.facishare.crm.payment.service.dto;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.math.BigDecimal;

public interface PaymentPlanResult {
  @Data
  class Arg {
    @JsonProperty("order_id")
    private String orderId;
  }


  @Data
  @Builder
  class Result {
    @JsonProperty("plan_payment_amount")
    private BigDecimal planPaymentAmount;
  }
}
