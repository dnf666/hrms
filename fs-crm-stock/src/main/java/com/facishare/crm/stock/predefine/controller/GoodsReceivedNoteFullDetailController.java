package com.facishare.crm.stock.predefine.controller;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteFullDetailController extends GoodsReceivedNoteDetailController {
    @Override
    protected void before(Arg arg) {
        arg.setFromRecycleBin(true);
        super.before(arg);
    }

    @Override
    protected Result doService(Arg arg) {
        Result r = super.doService(arg);
        if (null != r && null != r.getData()) {
            r.setData(fillWithDetails(controllerContext.getRequestContext(),
                    controllerContext.getObjectApiName(), r.getData()));
        }
        return r;
    }

    private ObjectDataDocument fillWithDetails(RequestContext context, String describeApiName,
                                               ObjectDataDocument data) {
        List<IObjectDescribe> detailDescribeList = serviceFacade
                .findDetailDescribes(context.getTenantId(), describeApiName);
        Map<String, List<IObjectData>> details = serviceFacade
                .findDetailObjectDataList(detailDescribeList, data.toObjectData(), context.getUser());
        data.put("details", details);
        return data;
    }
}
