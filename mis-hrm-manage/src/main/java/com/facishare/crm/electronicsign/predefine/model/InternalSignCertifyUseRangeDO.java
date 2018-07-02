package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

@Entity(value = "internalSignCertifyUseRange", noClassnameStored = true)
@Data
public class InternalSignCertifyUseRangeDO {
    @Id
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
