package com.facishare.crm.promotion.predefine.service.dto;

import java.util.List;

import lombok.Data;

public class AddEditActionModel {

    @Data
    public static class Arg {
        private List<String> tenantIds;
        private List<String> objectApiNames;
    }
}
