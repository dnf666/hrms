package com.facishare.crm.sfa.predefine.action.model;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * Created by lilei on 2017/8/14.
 */
@Data
public class WorkFlowInfo {
    @JSONField(name = "Type")
    private int type;

    @JSONField(name = "WorkFlowSteps")
    private List<WorkfFlowStep> workFlowSteps;
}
