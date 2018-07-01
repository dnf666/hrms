package com.facishare.crm.sfainterceptor.predefine.service.model.bulkInvalid;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author chenzengyong
 * @date on 2018/1/10.
 */
@Data
public class BulkInvalidAfterModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        private List<BulkObj> bulkObjs;
    }


    @Data
    @ToString
    public static class BulkObj {
        private String dataId;
        private String beforeLifeStatus;
        private String afterLifeStatus;
    }

}

