package com.facishare.crm.sfa.utilities.proxy.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

public interface LeadsObjTransferResult {
    @Data
    class Arg implements Serializable {
        private String salesClueID;
        private Map<String, ObjectDataDocument> dataList;
        private boolean combineCRMFeed;
        private boolean putTeamMembersIntoCustomer;
    }
@Data
    class SpecResult{
        @JSONField(name = "M1")
        private boolean isCustomerDuplicate;
        @JSONField(name = "M2")
        private boolean isContactDuplicate;
        @JSONField(name = "M3")
        private boolean isOpportunityDuplicate;
        @JSONField(name = "M4")
        private String contactID;
    }
    @Data
    class Result {
        boolean success;
        String message;
        int errorCode;
        private SpecResult value;
    }
}
