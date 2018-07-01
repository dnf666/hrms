package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardSimpleDetailController;
import com.facishare.paas.metadata.api.IObjectData;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by luxin on 2018/5/22.
 */
public class ContactSimpleDetailController extends StandardSimpleDetailController {

    @Override
    protected IObjectData findObjectData(StandardSimpleDetailController.Arg arg) {
        IObjectData data = super.findObjectData(arg);
        String tels = data.get("mobile", String.class);
        if (StringUtils.isNotBlank(tels)) {
            String[] telArray = tels.split(";:");
            Arrays.stream(telArray).filter(x -> StringUtils.isNotBlank(x)).findFirst().ifPresent(x -> data.set("mobile", x));
        }
        return data;
    }
}
