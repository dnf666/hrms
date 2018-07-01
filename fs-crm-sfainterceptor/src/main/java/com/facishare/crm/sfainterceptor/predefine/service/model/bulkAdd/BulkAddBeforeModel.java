package com.facishare.crm.sfainterceptor.predefine.service.model.bulkAdd;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class BulkAddBeforeModel {
    @Data
    @ToString
    public static class Arg {
        List<BulkObj> bulkObjs;
    }

    @Data
    @ToString
    public static class BulkObj {
        ObjectDataDocument data;
        String rawId;
    }

    @Data
    public static class Result {
        List<BulkResultObj> successBulkResultObj;
        List<BulkResultObj> failBulkResultObj;

        //无意义，满足.net的需求
        private String info = "info";
    }

    @Data
    @ToString
    public static class BulkResultObj {
        ObjectDataDocument data;
        String rawId;
        String errCode;
        String errMessage;
    }

}
