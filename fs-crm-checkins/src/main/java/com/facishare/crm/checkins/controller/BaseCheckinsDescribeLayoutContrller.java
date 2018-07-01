package com.facishare.crm.checkins.controller;

import com.facishare.crm.checkins.CheckinsUtils;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangsm on 2018/4/9/0009.
 */
@Slf4j
public class BaseCheckinsDescribeLayoutContrller extends StandardDescribeLayoutController {
    @Override
    protected Result after(Arg arg, Result result) {
        log.info("BaseCheckinsDescribeLayoutContrller -- arg{}", arg);
        LayoutDocument layoutDocument = result.getLayout();
        layoutDocument = CheckinsUtils.clearLayoutDocument(layoutDocument);
        if (null != layoutDocument)
            result.setLayout(layoutDocument);
        ObjectDescribeDocument objectDescribeDocument = result.getObjectDescribe();
        objectDescribeDocument = CheckinsUtils.clearObjectDescribeDocument(objectDescribeDocument);
        if (null != objectDescribeDocument)
            result.setObjectDescribe(objectDescribeDocument);
        return super.after(arg, result);
    }
}
