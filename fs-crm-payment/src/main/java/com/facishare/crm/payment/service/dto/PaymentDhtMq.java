package com.facishare.crm.payment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDhtMq {
  private String status; //reject pass no_flow
  private List<String> paymentOrderIds;
}
