package com.facishare.crm.stock.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author linchf
 * @date 2018/4/23
 */
@Data
@NoArgsConstructor
public class StockVO implements Serializable {

    private static final long serialVersionUID = 7273995608726581327L;

    private String stockId;
    private String productId;
    private BigDecimal blockedStock;
}
