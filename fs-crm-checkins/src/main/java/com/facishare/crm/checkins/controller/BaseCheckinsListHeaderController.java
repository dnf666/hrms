package com.facishare.crm.checkins.controller;

import com.facishare.crm.checkins.CheckinsUtils;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.DocumentBaseEntity;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by zhangsm on 2018/4/9/0009.
 */
@Slf4j
public class BaseCheckinsListHeaderController extends StandardListHeaderController {
    @Override
    protected Result after(Arg arg, Result result) {
        log.info("BaseCheckinsListHeaderController -- arg{}", arg);
        //layout去除按钮
        LayoutDocument layoutDocument = result.getLayout();
        layoutDocument = CheckinsUtils.clearLayoutDocument(layoutDocument);
        if (null != layoutDocument)
            result.setLayout(layoutDocument);

        ObjectDescribeDocument objectDescribeDocument = result.getObjectDescribe();
        objectDescribeDocument = CheckinsUtils.clearObjectDescribeDocument(objectDescribeDocument);
        if (null != objectDescribeDocument)
            result.setObjectDescribe(objectDescribeDocument);
        //fieldList
        List<DocumentBaseEntity> fieldList = result.getFieldList();
        fieldList = CheckinsUtils.hideField4Checkins(fieldList);
        if (CollectionUtils.notEmpty(fieldList))
            result.setFieldList(fieldList);
        super.after(arg, result);
        return result;
    }
}
