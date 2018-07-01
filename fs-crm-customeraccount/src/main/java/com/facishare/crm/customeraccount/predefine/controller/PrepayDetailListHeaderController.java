package com.facishare.crm.customeraccount.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;

public class PrepayDetailListHeaderController extends StandardListHeaderController {
    @Override
    protected StandardListHeaderController.Result after(StandardListHeaderController.Arg arg, StandardListHeaderController.Result result) {
        StandardListHeaderController.Result result1 = super.after(arg, result);
        return result1;
    }

}
