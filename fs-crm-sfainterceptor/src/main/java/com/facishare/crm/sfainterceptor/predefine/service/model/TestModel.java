package com.facishare.crm.sfainterceptor.predefine.service.model;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import lombok.ToString;

/**
 * @author chenzengyong
 * @date on 2018/1/15.
 */
@Data
public class TestModel {

    @Data
    @ToString
    public static class Arg {
        String a;
    }

}
