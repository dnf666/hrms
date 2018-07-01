package com.facishare.crm.rest.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Created by xujf on 2017/10/17.
 */
@Data
public class CrmCustomerVo {
    @SerializedName("CustomerID")
    private String customerID;
    @SerializedName("Name")
    private String name;
    @SerializedName("Level")
    private String level;
    @SerializedName("AreaFullName")
    private String areaFullName;
    private String houseNo;
    private String tel;
    private String postCode;
    private String fax;
    @SerializedName("OwnerID")
    private String ownerID;
    @SerializedName("OwnerName")
    private String ownerName;

    private String customerType;
    // 客户状态 1-未报备 2-报备中 3-已报备 99-已作废
    @SerializedName("Status")
    private int status;

    public boolean isInvalid() {
        return status == 99;
    }
}
