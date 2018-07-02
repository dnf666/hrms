package com.facishare.crm.electronicsign.predefine.model.vo;

import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class InternalSignCertifyUseRangeVO {
    private String id;
    private String tenantId;
    private String internalSignCertifyId;
    /**
     * 租户上上签账号
     */
    private String bestSignAccount;
    /**
     * @see com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum
     */
    private String certifyStatus;
    /**
     * @see UseStatusEnum
     */
    private String useStatus;
    private List<String> departmentIds;
    private Long createTime;
    private Long updateTime;
}
