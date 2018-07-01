package com.facishare.crm.electronicsign.predefine.service.dto;

import com.facishare.crm.electronicsign.predefine.model.vo.InternalSignCertifyUseRangeVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

public class InternalSignCertifyUseRangeType {

    public static class GetInternalSignCertifyUseRangeSettingList {

        @Data
        public static class Result {
            private List<SignAccountData> signAccountDataList;

        }

        @Data
        public static class SignAccountData {
            private String internalSignCertifyId;
            private String enterpriseName;
            private InternalSignCertifyUseRangeVO internalSignCertifyUseRange;
        }
    }

    public static class SetUseRange {

        @Data
        public static class Result {
            /**
             * 1 成功  2 失败
             */
            private Integer status = 1;
            private String message = "OK";
        }

        @Data
        public static class Arg {
            Map<String, List<String>> settingMap;

        }
    }



}
