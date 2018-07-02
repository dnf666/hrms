package com.facishare.crm.outbounddeliverynote.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author linchf
 * @date 2018/3/15
 */
@Data
@Builder
public class OutboundDeliveryNoteProductVO implements Serializable {

    private static final long serialVersionUID = 5730908375350180105L;

    private String productId;
    private String outboundAmount;
    private String stockId;
    private String remark;
}
