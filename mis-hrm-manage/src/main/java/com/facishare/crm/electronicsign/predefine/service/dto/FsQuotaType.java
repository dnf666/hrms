package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.predefine.model.vo.FsQuotaVO;
import lombok.Data;

import java.io.Serializable;
@Data
public class FsQuotaType implements Serializable {
    @Data
    public static class GetFsQuota {
        @Data
        public static class Result {
       private FsQuotaVO fsQuotaVO;
        }
    }
}
