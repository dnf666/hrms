package com.facishare.crm.electronicsign.predefine.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class SignPositionDO implements Serializable {
    private String x1;
    private String y1;
    private String x2;
    private String y2;
    private Integer pageNum;
}