package com.facishare.crm.rest.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author linchf
 * @date 2018/4/20
 */
public class SetRemindRecordModel {
    @Data
    public static class Item {
        @SerializedName("EmployeeID")
        private Integer employeeId;

        @SerializedName("RemindCount")
        private Integer remindCount;

        @SerializedName("NotReadCount")
        private Integer notReadCount;

        @SerializedName("LastSummary")
        private String lastSummary;

        @SerializedName("LastTime")
        private Integer lastTime;

        @SerializedName("ShouldSendPush")
        private Boolean shouldSendPush;
    }

    @Data
    public static class Result {
        private boolean value;
        private boolean success;
        private String message;
        private int errorCode;
    }
}
