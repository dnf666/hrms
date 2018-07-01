package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.model.vo.TenantQuotaVO;
import lombok.Data;

/**
 * created by dailf on 2018/4/25
 *
 * @author dailf
 */
@Data
public class TenantQuotaType {
    @Data
    public static class GetTenantQuotaByPage {
        @Data
        public static class Result {
            private Pager<TenantQuotaVO> pager;
        }

        @Data
        public static class Arg {
            private Integer pageSize;
            private Integer currentPage;
            private String quotaType;
        }
    }
}
