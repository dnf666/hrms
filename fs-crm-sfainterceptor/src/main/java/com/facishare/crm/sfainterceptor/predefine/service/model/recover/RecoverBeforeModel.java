package com.facishare.crm.sfainterceptor.predefine.service.model.recover;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import lombok.Data;
import lombok.ToString;

/**
 * @author chenzengyong
 * @date on 2018/1/10.
 */
@Data
public class RecoverBeforeModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        String dataId;
        String recoverToStatus;//恢复到什么状态
    }

}
