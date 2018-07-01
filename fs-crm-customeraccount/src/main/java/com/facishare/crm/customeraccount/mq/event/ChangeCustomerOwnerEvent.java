package com.facishare.crm.customeraccount.mq.event;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ChangeCustomerOwnerEvent {
    @SerializedName("TenantID")
    private String tennatId;
    @SerializedName("TennatAccount")
    private String tennatAccount;
    @SerializedName("AppID")
    private String appID;
    @SerializedName("Package")
    private String pkg;
    @SerializedName("ObjectApiName")
    private String objectApiName;
    @SerializedName("ObjectID")
    private String objectID;
    @SerializedName("ActionCode")
    private String actionCode;
    @SerializedName("ActionContent")
    private ActionContent actionContent;

    @Data
    public static class ActionContent {
        @SerializedName("OwnerID")
        private Integer ownerId;
        @SerializedName("Title")
        private String title;
        @SerializedName("CustomerName")
        private String customerName;
        @SerializedName("CustomerID")
        private String customerId;
        @SerializedName("OldOwnerID")
        private Integer oldOwnerId;
        @SerializedName("Key")
        private String key;
    }
}
