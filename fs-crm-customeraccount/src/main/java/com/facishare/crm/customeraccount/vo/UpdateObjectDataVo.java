package com.facishare.crm.customeraccount.vo;

import com.facishare.paas.metadata.api.IObjectData;

import lombok.Data;

@Data
public class UpdateObjectDataVo {
    private IObjectData objectData;
    private String oldLifeStatus;

    public UpdateObjectDataVo(IObjectData objectData, String oldLifeStatus) {
        this.objectData = objectData;
        this.oldLifeStatus = oldLifeStatus;
    }
}
