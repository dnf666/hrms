package com.facishare.crm.sfainterceptor.predefine.service.model.add;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class AddBeforeModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        private ObjectDataDocument data;
    }

}
