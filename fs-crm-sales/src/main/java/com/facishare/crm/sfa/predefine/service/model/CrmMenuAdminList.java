package com.facishare.crm.sfa.predefine.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import java.util.List;

import lombok.Builder;
import lombok.Data;

public interface CrmMenuAdminList {
    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        private List<ObjectDataDocument> menuList;
    }
}
