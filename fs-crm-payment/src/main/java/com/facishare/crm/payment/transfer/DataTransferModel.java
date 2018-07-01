package com.facishare.crm.payment.transfer;

import lombok.Data;

public class DataTransferModel {

  @Data
  public static class Arg {
    private String biz;//环境
    private String eidsAll;//企业id
    private String hook;//回调地址
    private String sql;
  }

}