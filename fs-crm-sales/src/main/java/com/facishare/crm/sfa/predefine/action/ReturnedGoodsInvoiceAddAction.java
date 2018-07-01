package com.facishare.crm.sfa.predefine.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.utilities.common.convert.ConvertUtil;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/1/2.
 */
@Slf4j
public class ReturnedGoodsInvoiceAddAction extends SFAObjectSaveAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        ConvertUtil.mergeMDForSalesOrder(arg.getObjectData(), arg.getDetails(), "ReturnedGoodsInvoiceProductObj");
    }

    @Override
    protected Result doAct(Arg arg) {
        super.doAct(arg);

        Object returnProductsTmp = objectData.get("returnproduct");
        List<Map<String, Object>> returnProducts = null;
        if (returnProductsTmp != null && returnProductsTmp instanceof List) {
            returnProducts = (List<Map<String, Object>>) returnProductsTmp;
        } else {
            log.warn("objectData issue. context{}, {}", JSON.toJSONString(actionContext), JSON.toJSONString(objectData));
        }

        if (CollectionUtils.isNotEmpty(returnProducts)) {
            for (Map<String, Object> returnProduct : returnProducts) {
                returnProduct.put("_id", returnProduct.get("TradeProductID"));
            }
        }

        IObjectData result = serviceFacade.saveObjectData(actionContext.getUser(), objectData);
        return Result.builder().objectData(ObjectDataDocument.of(result)).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        Map<String, Object> tmpResult = Maps.newHashMap();

        if (result.getObjectData() != null && result.getObjectData().get("CRMResponse") != null) {
            JSONObject value = (JSONObject) result.getObjectData().get("CRMResponse");
            tmpResult.put("_id", value.get("_id"));
            tmpResult.put("returned_goods_number", value.get("ReturnOrderCode"));
        }
        result.setObjectData(ObjectDataDocument.of(tmpResult));
        return result;
    }
}
