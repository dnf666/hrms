package com.facishare.crm.sfainterceptor.predefine.service.model.edit;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class EditBeforeModel extends CommonModel {
    @Data
    @ToString
    public static class Arg {
        ObjectDataDocument data;
        String nowLifeStatus;
    }
}
