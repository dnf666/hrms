package com.facishare.crm.stock.predefine.controller;

import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created by linchf on 2018/1/26.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseListController extends StandardListController {
    @Override
    public Result after(Arg arg, Result result) {
        List<LayoutDocument> layoutDocuments =  result.getListLayouts();

        if (CollectionUtils.isNotEmpty(layoutDocuments)) {
            LayoutDocument layoutDocument = layoutDocuments.get(0);
            //终端隐藏按钮
            if (controllerContext.getClientInfo() != null &&
                    (controllerContext.getClientInfo().contains(RequestContext.Android_CLIENT_INFO_PREFIX)
                            || controllerContext.getClientInfo().contains(RequestContext.IOS_CLIENT_INFO_PREFIX))) {
                layoutDocument.put("buttons", Lists.newArrayList());
            }
        }
        return super.after(arg, result);
    }
}
