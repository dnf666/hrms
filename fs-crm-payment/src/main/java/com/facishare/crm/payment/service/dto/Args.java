package com.facishare.crm.payment.service.dto;


import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class Args {
  public static class ListArg{
    private String search_template_id;
    private String search_query_info;

    public String getSearch_template_id() {
      return search_template_id;
    }

    public String getSearch_query_info() {
      return search_query_info;
    }
  }
  public static class CalculateOrdersPaymentMoneyArg{
    private List<String> ids;
    private String status;
    private boolean dnr;

    public List< String > getIds() {
      return ids;
    }

    public String getStatus() {
      return status;
    }

    public boolean isDnr() {
      return dnr;
    }
  }


  @Data
  public static class MergeCustomerPayment {
    @JsonProperty("source_customer_id")
    private List< String > sourceCustomerId;
    @JsonProperty("target_customer_id")
    private String targetCustomerId;
  }
}
