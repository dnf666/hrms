package com.facishare.crm.deliverynote.predefine.model;

import lombok.*;

import java.util.List;

@Data
@Builder
public class LogisticsVO {
    private String expressOrg;
    private String expressOrgName;
    private String expressOrderId;
    private Long deliveryDate;
    private String reason;
    /**
     * @see StateEnum
     */
    private String state;
    private List<Trace> traces;

    @Data
    public static final class Trace {
        private String acceptTime;
        private String acceptStation;
        private String remark;
    }

    public enum StateEnum {
        UN_DISTINGUISHABLE_EXPRESS_ORDER_ID(-1, "无法识别的物流单号"),
        NO_LOGISTICS(0, "暂无轨迹信息"),
        IN_TRANSIT(2, "在途中"),
        SIGNED(3, "签收"),
        PROBLEM_EXPRESS(4, "问题件"),
        ;

        @Getter
        private int state;
        @Getter
        private String desc;

        StateEnum(int state, String desc) {
            this.desc = desc;
            this.state = state;
        }
    }
}


