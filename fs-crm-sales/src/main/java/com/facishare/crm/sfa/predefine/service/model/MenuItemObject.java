package com.facishare.crm.sfa.predefine.service.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MenuItemObject {
    private String api_name;
    private String display_name;
    private Integer icon_index;
    private String icon_path;
    private Boolean is_active;
}
