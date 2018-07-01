package com.facishare.crm.checkins.controller;

import com.facishare.crm.checkins.CheckinsUtils;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangsm on 2018/4/9/0009.
 */
@Slf4j
public class BaseCheckinsDetailController extends StandardDetailController {

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("BaseCheckinsDetailController -- arg{}", arg);
        LayoutDocument layoutDocument = result.getLayout();
        layoutDocument = CheckinsUtils.clearLayoutDocument(layoutDocument);
        if (null != layoutDocument)
            result.setLayout(layoutDocument);
        ObjectDescribeDocument objectDescribeDocument = result.getDescribe();
        objectDescribeDocument = CheckinsUtils.clearObjectDescribeDocument(objectDescribeDocument);
        if (null != objectDescribeDocument)
            result.setDescribe(objectDescribeDocument);
        ObjectDataDocument dataDocument =result.getData();
        dataDocument = CheckinsUtils.formatDetailName(dataDocument);
        if (null != dataDocument)
            result.setData(dataDocument);
        return super.after(arg, result);
    }
}
