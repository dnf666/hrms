package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.predefine.model.vo.BuyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.rest.dto.QueryCustomersByPage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * created by dailf on 2018/4/24
 *
 * @author dailf
 */
@Data
public class BuyRecordType {
    @Data
    public static class QueryByPage {
        @Data
        public static class Arg {
            private Integer pageSize;
            private Integer currentPage;
            private String quotaType;
            private String payType;
            private String tenantId;
            private Long startTime;
            private Long endTime;
        }

        @Data
        public static class Result {
            private Pager<BuyRecordVO> pager;
        }
    }
}
