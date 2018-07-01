package com.facishare.crm.deliverynote.predefine.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 批量作废
 * Created by chenzs on 2018/1/19.
 */
@Slf4j
public class DeliveryNoteBulkInvalidAction extends StandardBulkInvalidAction {
    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    private Map<String, String> deliveryNoteId2oldStatusMap = new HashMap<>();

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        List<IObjectData> deliveryNoteObjectDatas = objectDataList.stream().filter(o -> objectDescribe.getApiName().equals(o.getDescribeApiName())).collect(Collectors.toList());
        for (IObjectData data : deliveryNoteObjectDatas) {
            deliveryNoteManager.checkForInvalid(this.actionContext.getUser(), data);
            String oldStatus = data.get(DeliveryNoteObjConstants.Field.Status.apiName, String.class);
            deliveryNoteId2oldStatusMap.put(data.getId(), oldStatus);
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("DeliveryNoteBulkInvalidAction after. arg:{}, result:{}", arg, result);
        result = super.after(arg, result);

        log.info("startApprovalFlowAsynchronous[{}]", startApprovalFlowAsynchronous);
        if (startApprovalFlowAsynchronous) {
            return result;
        }

        JSONObject argJsonObject = JSON.parseObject(arg.getJson());
        JSONArray array = JSONArray.parseArray(argJsonObject.get("dataList").toString());
        List<String> deliveryNoteIds = Lists.newArrayList();
        for (Object a : array) {
            Document doc = Document.parse(a.toString());
            deliveryNoteIds.add((String)doc.get("_id"));
        }
        log.info("DeliveryNoteBulkInvalidAction after. deliveryNoteIds:{}", deliveryNoteIds);

        List<IObjectData> deliveryNoteObjectDatas = deliveryNoteManager.getDeliveryNotes(actionContext.getUser(), deliveryNoteIds);
        log.info("DeliveryNoteBulkInvalidAction after deliveryNoteObjectDatas:{}", deliveryNoteObjectDatas);

        deliveryNoteObjectDatas.forEach(d -> {
            String deliveryNoteId = d.getId();
            String oldStatus = deliveryNoteId2oldStatusMap.get(deliveryNoteId);
            deliveryNoteManager.doAfterInvalidAction(actionContext.getUser(), d, oldStatus);
        });

        return result;
    }

}