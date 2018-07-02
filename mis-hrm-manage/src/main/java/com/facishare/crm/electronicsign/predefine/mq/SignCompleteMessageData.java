package com.facishare.crm.electronicsign.predefine.mq;

import com.facishare.crm.electronicsign.predefine.model.vo.SimpleSignerVO;
import lombok.Data;

@Data
public class SignCompleteMessageData extends AbstractMessageData {
    private String tenantId;
    private String appType;
    private String objApiName;
    private String objDataId;
    private String totalSignStatus;
    private String signStatus;
    private SimpleSignerVO signer;
}
