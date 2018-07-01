package com.facishare.crm.sfainterceptor.predefine.service.model.common;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by linchf on 2018/1/16.
 */
@Data
@ToString
public class SalesOrderProductVo {
    private String productId;//产品id
    private String productName;//产品名称
    private BigDecimal amount;//产品数量
    private BigDecimal price; //销售单价
    private String recordType;//业务类型

}
