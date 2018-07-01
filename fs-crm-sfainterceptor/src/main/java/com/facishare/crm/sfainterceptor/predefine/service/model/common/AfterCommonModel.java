package com.facishare.crm.sfainterceptor.predefine.service.model.common;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import lombok.Data;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class AfterCommonModel extends CommonModel {
    @Data
    public static class Arg {
         String dataId;
         String beforeLifeStatus;
         String afterLifeStatus;
    }


}
