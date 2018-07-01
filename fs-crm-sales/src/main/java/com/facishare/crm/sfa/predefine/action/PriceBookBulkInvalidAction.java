package com.facishare.crm.sfa.predefine.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.predefine.service.PriceBookService;
import com.facishare.paas.appframework.core.exception.APPException;
import com.facishare.paas.appframework.core.exception.ObjectDefNotFoundError;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by luxin on 2017/11/22.
 */
@Slf4j
public class PriceBookBulkInvalidAction extends StandardBulkInvalidAction {
    private final PriceBookService priceBookService = (PriceBookService) SpringUtil.getContext().getBean("priceBookService");


    @Override
    protected void initObjectDataList() {
        if (StringUtils.isEmpty(this.arg.getJson())) {
            throw new ValidateException("作废数据不能为空");
        } else {
            JSONObject jsonObject;
            try {
                jsonObject = JSON.parseObject(this.arg.getJson());
            } catch (JSONException var4) {
                log.error(var4.getMessage(), var4);
                throw new ValidateException("作废数据解析失败");
            }
            JSONArray jsonArray = jsonObject.getJSONArray("dataList");
            if (null == jsonArray) {
                throw new ValidateException("没有选择需要作废的数据");
            } else {
                IObjectData standardPriceBook = priceBookService.getStandardPriceBook(actionContext.getUser());
                for (int i = 0; i < jsonArray.size(); ++i) {
                    processJsonObject(jsonArray.getJSONObject(i), this.actionContext.getUser(), standardPriceBook, jsonArray.size());
                }
            }
        }
    }


    private void processJsonObject(JSONObject jsonObject, User user, IObjectData standardPriceBook, int invalidObjectCount) {
        String apiName = jsonObject.getString("object_describe_api_name");
        String dataId = jsonObject.getString("_id");
        if (isContainStandardPriceBook(standardPriceBook, dataId)) {
            handleInvalidObjectListContainStandardPriceBook(standardPriceBook, invalidObjectCount);
            return;
        }

        if (!StringUtils.isEmpty(dataId) && !StringUtils.isEmpty(apiName)) {
            if (null == this.objectDescribe) {
                throw new ObjectDefNotFoundError("未找到名称为 " + apiName + " 的描述数据");
            } else {
                this.objectDescribeMap.put(apiName, this.objectDescribe);
                IObjectData argObjectData = this.getObjectData(dataId, this.objectDescribe);
                if (null == argObjectData) {
                    throw new ObjectDefNotFoundError("未找到 id 为 " + dataId + " 的对象");
                } else {

                    List<IObjectData> masterAndDetailList = this.getMasterAndDetailObjects(argObjectData, this.objectDescribe, user);
                    if (masterAndDetailList.size() >= 201) {
                        throw new APPException(" 该价目表的产品超过200条，请删除价目表产品再作废该价目表");
                    }
                    this.objectDataList.addAll(masterAndDetailList);
                }
            }
        } else {
            throw new ValidateException("没有找到数据 id 或对象描述名称");
        }
    }

    /**
     * 处理作废的列表中有标准价目表的情况
     *
     * @param standardPriceBook
     * @param invalidObjectCount
     */
    private void handleInvalidObjectListContainStandardPriceBook(IObjectData standardPriceBook, int invalidObjectCount) {
        if (invalidObjectCount == 1) {
            throw new ValidateException("标准价目表不允许作废.");
        } else {
            if (bulkOpResult.getFailObjectDataList() == null) {
                bulkOpResult.setFailObjectDataList(Lists.newArrayList(standardPriceBook));
            } else {
                bulkOpResult.getFailObjectDataList().add(standardPriceBook);
            }
            if (bulkOpResult.getFailReason() == null) {
                bulkOpResult.setFailReason("标准价目表不允许作废");
            } else {
                bulkOpResult.setFailReason(bulkOpResult.getFailReason() + "\n标准价目表不允许作废");
            }
        }
    }

    private boolean isContainStandardPriceBook(IObjectData standardPriceBook, String dataId) {
        if (standardPriceBook == null) log.error("standardPriceBook not exist. tenantId {}", actionContext.getUser());
        if (standardPriceBook != null && dataId != null && dataId.equals(standardPriceBook.getId())) {
            return Boolean.TRUE;
        } else return Boolean.FALSE;
    }


}
