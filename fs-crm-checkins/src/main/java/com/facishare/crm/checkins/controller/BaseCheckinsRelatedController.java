package com.facishare.crm.checkins.controller;

import com.facishare.crm.checkins.CheckinsUtils;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangsm on 2018/4/9/0009.
 */
@Slf4j
public class BaseCheckinsRelatedController extends StandardRelatedController {
    @Override
    protected Result after(Arg arg, Result result) {
        log.info("BaseCheckinsRelatedController -- arg{}", arg);
        LayoutDocument layoutDocument = result.getLayout();
        layoutDocument = CheckinsUtils.clearLayoutDocument(layoutDocument);
        if (null != layoutDocument)
            result.setLayout(layoutDocument);

        ObjectDescribeDocument objectDescribeDocument = result.getDescribe();
        objectDescribeDocument = CheckinsUtils.clearObjectDescribeDocument(objectDescribeDocument);
        if (null != objectDescribeDocument)
            result.setDescribe(objectDescribeDocument);
        result.setRefObjects(CheckinsUtils.clearRefObjects(result.getRefObjects()));
        return super.after(arg, result);
    }
}
