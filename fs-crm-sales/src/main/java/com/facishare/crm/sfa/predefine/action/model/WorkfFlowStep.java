package com.facishare.crm.sfa.predefine.action.model;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * Created by lilei on 2017/8/14.
 */
@Data
public class WorkfFlowStep {
    @JSONField(name = "ApproverID")
    private int approverID;
    @JSONField(name = "ApproverType")
    private int aproverType;
    @JSONField(name = "Level")
    private int level;

}
