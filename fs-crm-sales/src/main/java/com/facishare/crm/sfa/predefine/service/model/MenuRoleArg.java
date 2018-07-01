package com.facishare.crm.sfa.predefine.service.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MenuRoleArg {
    private String role_id;
    private String display_name;
}
