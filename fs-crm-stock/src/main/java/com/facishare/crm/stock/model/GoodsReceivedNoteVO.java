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

public class GoodsReceivedNoteVO implements Serializable {
    private static final long serialVersionUID = -4083307968682943819L;

    private String requisitionId;
    private Long goodsReceivedDate;
    private String warehouseId;
    private String goodsReceivedType;
    private String remark;
}
