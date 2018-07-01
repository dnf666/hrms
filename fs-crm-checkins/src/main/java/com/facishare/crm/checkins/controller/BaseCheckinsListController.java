package com.facishare.crm.checkins.controller;

import com.facishare.crm.checkins.CheckinsUtils;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;

import java.util.List;

public class BaseCheckinsListController extends StandardListController {
    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        ObjectDescribeDocument objectDescribeDocument = result.getObjectDescribe();
        objectDescribeDocument = CheckinsUtils.clearObjectDescribeDocument(objectDescribeDocument);
        if (null != objectDescribeDocument)
            result.setObjectDescribe(objectDescribeDocument);
        //清除外勤图片对象的name
        List<ObjectDataDocument> dataDocumentList = result.getDataList();
        dataDocumentList = CheckinsUtils.formatDataListName(dataDocumentList);
        if (null != dataDocumentList)
            result.setDataList(dataDocumentList);
        return result;
    }
}
