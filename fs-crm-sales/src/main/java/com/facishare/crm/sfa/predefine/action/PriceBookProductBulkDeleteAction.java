package com.facishare.crm.sfa.predefine.action;

import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.predefine.mq.ProductEvent;
import com.facishare.crm.sfa.predefine.service.PriceBookService;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.common.util.BulkOpResult;
import com.facishare.paas.metadata.api.IObjectData;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceBookProductBulkDeleteAction extends StandardBulkInvalidAction {
    private PriceBookService priceBookService = (PriceBookService) SpringUtil.getContext().getBean("priceBookService");

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        // TODO: 2017/11/23 选择删除部分价目表产品
        IObjectData standardPrice = priceBookService.getStandardPriceBook(new User(this.actionContext.getTenantId(), ProductEvent.SUPER_ADMIN_USER_ID));
        this.objectDataList.forEach(objectData -> {
            if (standardPrice.getId().equals(objectData.get(PriceBookConstants.ProductField.PRICEBOOKID.getApiName()).toString())) {
                throw new ValidateException("标准价目表下产品不能删除！");
            }
        });
    }

    @Override
    public Result doAct(Arg arg) {
        String result = serviceFacade.bulkInvalidAndDeleteWithSuperPrivilege(objectDataList, this.getActionContext().getUser());
        if (StringUtils.isNotBlank(result)) {
            log.error("删除价目表产品失败！,tenantId {},arg {},failReason {}", this.getActionContext().getTenantId(), arg, result);
            throw new ValidateException("删除失败！");
        }
        this.bulkOpResult = BulkOpResult.builder().successObjectDataList(objectDataList).failObjectDataList(Lists.newLinkedList()).build();
        this.serviceFacade.log(this.actionContext.getUser(), EventType.MODIFY, ActionType.Delete, this.objectDescribeMap, objectDataList);
        return this.generateResult();
    }

    @Override
    protected void validateObjectStatus() {
        try {
            CountDownLatch latch = new CountDownLatch(objectDataList.size());
            this.objectDataList.forEach((o) -> {
                this.serviceFacade.checkActionByLockStatusAndLifeStatus(o, ObjectAction.INVALID, this.actionContext.getUser(), o.getDescribeApiName(), false);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                log.error("validateObjectStatus list,CountDownLatch error.", e);
            }
        } catch (ValidateException e) {
            //作废的提示名称更换为删除
            throw new ValidateException(e.getMessage().replace(ObjectAction.INVALID.getActionLabel(), ObjectAction.DELETE.getActionLabel()));
        }
    }

    protected void initObjectDataList() {
        if (StringUtils.isEmpty(((StandardBulkInvalidAction.Arg) this.arg).getJson())) {
            throw new ValidateException("作废数据不能为空");
        } else {
            JSONObject jsonObject;
            try {
                jsonObject = JSON.parseObject(((StandardBulkInvalidAction.Arg) this.arg).getJson());
            } catch (JSONException var4) {
                log.error(var4.getMessage(), var4);
                throw new ValidateException("作废数据解析失败");
            }

            JSONArray jsonArray = jsonObject.getJSONArray("dataList");
            if (null == jsonArray) {
                throw new ValidateException("没有选择需要作废的数据");
            } else {
                this.processJsonObject(jsonArray);
            }
        }
    }

    private void processJsonObject(JSONArray jsonArray) {
        this.objectDescribeMap.put(objectDescribe.getApiName(), this.objectDescribe);
        List<String> ids = Lists.newLinkedList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ids.add(jsonObject.getString("_id"));
        }
        if (CollectionUtils.isEmpty(ids)) {
            throw new ValidateException("没有找到数据 id 或对象描述名称");
        }
        this.objectDataList = this.serviceFacade.findObjectDataByIds(this.getActionContext().getTenantId(), ids, objectDescribe.getApiName());
        if (this.objectDataList.size() != jsonArray.size()) {
            throw new ValidateException("没有找到数据");
        }
    }
}
