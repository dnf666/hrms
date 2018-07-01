package com.facishare.crm.stock.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liangk
 * @date 21/03/2018
 */
@Data
@Builder
public class GoodsReceivedNoteProductVO implements Serializable {
    private static final long serialVersionUID = 105444875593130222L;

    private String productId;
    private String goodsReceivedAmount;
    private String remark;
}
