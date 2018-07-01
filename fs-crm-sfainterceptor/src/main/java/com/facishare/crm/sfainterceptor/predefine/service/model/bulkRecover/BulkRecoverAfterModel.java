package com.facishare.crm.sfainterceptor.predefine.service.model.bulkRecover;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class BulkRecoverAfterModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        List<BulkObj> bulkObjs;
    }


    @Data
    @ToString
    public static class BulkObj {
        String dataId;
        String beforeLifeStatus;
        String afterLifeStatus;
    }

}
