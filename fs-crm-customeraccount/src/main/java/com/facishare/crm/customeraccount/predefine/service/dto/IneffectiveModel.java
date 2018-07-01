package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;
import lombok.ToString;

public class IneffectiveModel {

    @Data
    @ToString
    public static class Arg {
        private String id;
    }

    @Data
    public static class Results {

        List<ObjectDataDocument> objectData;
    }
}
