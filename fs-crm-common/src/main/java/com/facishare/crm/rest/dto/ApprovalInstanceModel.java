package com.facishare.crm.rest.dto;

import java.util.List;

import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;

import lombok.Data;

public class ApprovalInstanceModel {
    @Data
    public static class Arg {
        String objectId;
    }

    @Data
    public static class Result {
        private int code;
        private String message;
        private List<Instance> data;

        public boolean success() {
            return code == 0;
        }
    }

    @Data
    public static class Instance {
        private String instanceId;
        private String instanceName;
        private String objectId;
        private String triggerType;
        private String state;
        private long createTime;
        private long lastModifyTime;
        private long endTime;
        private String apiName;
        private String applicantId;
        private long cancelTime;
    }

    public enum ApprovalFlowState {
        IN_PROGRESS("in_progress"),

        PASS("pass"),

        ERROR("error"),

        CANCEL("cancel"),

        REJECT("reject");

        private String value;

        ApprovalFlowState(String value) {
            this.value = value;
        }

        public static ApprovalFlowState of(String value) {
            for (ApprovalFlowState approvalFlowState : values()) {
                if (value.equals(approvalFlowState.value)) {
                    return approvalFlowState;
                }
            }
            return null;
        }
    }
}
