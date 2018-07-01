package com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author chenzengyong
 * @date on 2018/1/10.
 */
@Data
public class BulkRecoverBeforeModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        List<BulkObj> bulkObjs;
    }


    @Data
    @ToString
    public static class BulkObj {
        String dataId;
        String recoverToStatus;
    }

}
