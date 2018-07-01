package com.facishare.crm.customeraccount.predefine.service.dto;

import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;

import lombok.Data;

public class UpdateRebateStatusModel {
    @Data
    public static class Arg {
        private ObjectData objectData;
    }

    @Data
    public static class Result {
        private IObjectData objectData;
    }

}
