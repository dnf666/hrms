package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

public interface LeadsObjTransferModel {
    @Data
    class Arg implements Serializable {
        @JSONField(name = "M1")
        private String salesClueID;
        @JSONField(name = "M2")
        private Map<String, ObjectDataDocument> dataList;
        @JSONField(name = "M3")
        private boolean combineCRMFeed;
        @JSONField(name = "M4")
        private boolean putTeamMembersIntoCustomer;
    }

    @Data
    class Result {
        @JSONField(name = "M1")
        private boolean isCustomerDuplicate;
        @JSONField(name = "M2")
        private boolean isContactDuplicate;
        @JSONField(name = "M3")
        private boolean isOpportunityDuplicate;
        @JSONField(name = "M4")
        private  Map<String, Object> contactData;
    }
}
