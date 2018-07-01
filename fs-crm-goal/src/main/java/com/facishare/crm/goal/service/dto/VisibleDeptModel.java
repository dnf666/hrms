package com.facishare.crm.goal.service.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by renlb on 2018/5/5.
 */
@Data
public class VisibleDeptModel {
    List<String> responsibleDeptIds;
    List<String> belong2DeptIds;
    List<String> directSubordinateIds;
    List<String> subordinateBelong2DeptIds;
    List<String> visibleDeptIds;
    List<String> allSubordinateIds;
}
