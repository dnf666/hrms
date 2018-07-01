package com.facishare.crm.checkins.controller;

import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.collect.Lists;

public class BaseCheckinsRelatedListController extends StandardRelatedListController {
    @Override
    protected Result after(Arg arg, Result result) {
        log.info("BaseCheckinsRelatedListController -- arg{}",arg);
        result = super.after(arg, result);
        ObjectDescribeDocument objectDescribeDocument = result.getObjectDescribe();
        IObjectDescribe iObjectDescribe = objectDescribeDocument.toObjectDescribe();
        iObjectDescribe.set("actions", Lists.newArrayList());
        result.setObjectDescribe(ObjectDescribeDocument.of(iObjectDescribe));
        return result;
    }
}
