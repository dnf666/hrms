package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.model.vo.TenantQuotaVO;
import lombok.Data;
import lombok.NonNull;

/**
 * created by dailf on 2018/4/25
 *
 * @author dailf
 */
@Data
public class BuyQuotaType {
    @Data
    public static class AddBuyQuota {
        @Data
        public static class Result {
            private int errCode;
            private String errMessage;
        }

        @Data
        public static class Arg {
            private String tenantId;
            private int buyQuota;
            private String quotaType;
            private String payType;
        }
    }
}
