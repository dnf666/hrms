package com.facishare.crm.outbounddeliverynote.model;

import com.facishare.paas.appframework.core.model.User;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author linchf
 * @date 2018/3/15
 */
@Data
@Builder
public class OutboundDeliveryNoteVO implements Serializable {

    private static final long serialVersionUID = -5774887330549002366L;

    private Long outboundDate;
    private String warehouseId;
    /**
     * @see com.facishare.crm.outbounddeliverynote.enums.OutboundTypeEnum   5销售出库（发货单）  6 调拨出库（调拨单）
     */
    private String outboundType;
    private String deliveryNoteId;
    private String requisitionNoteId;
    private String remark;
}

