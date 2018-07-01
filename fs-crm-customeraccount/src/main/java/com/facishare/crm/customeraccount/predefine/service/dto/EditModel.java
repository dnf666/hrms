package com.facishare.crm.customeraccount.predefine.service.dto;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import lombok.Data;

public class EditModel {

    @Data
    public static class Arg {
        ObjectDataDocument objectData;
    }

    @Data
    public static class Result {
        ObjectDataDocument objectData;
    }

    @Data
    public static class ResultList {
        List<ObjectDataDocument> objectData;
    }
}
