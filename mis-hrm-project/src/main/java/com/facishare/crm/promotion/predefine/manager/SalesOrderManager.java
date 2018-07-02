package com.facishare.crm.promotion.predefine.manager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.crm.promotion.exception.PromotionBusinessException;
import com.facishare.crm.promotion.exception.PromotionErrorCode;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.ObjectRelatedPromotionModel;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Maps;

@Component
public class SalesOrderManager {
    @Autowired
    private CrmRestApi crmRestApi;

    public List<ObjectRelatedPromotionModel.RelatedPromotionVo> listRelatedPromotionObjectsByPromotionIds(User user, List<String> promotionIds) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-fs-ei", user.getTenantId());
        headers.put("x-fs-userInfo", user.getUserId());
        ObjectRelatedPromotionModel.Result result = crmRestApi.listRelatedObjectsByPromotionIds(promotionIds, headers);
        if (!result.isSuccess()) {
            throw new PromotionBusinessException(PromotionErrorCode.QUERY_ROLE_ERROR, "查询关联促销的订单信息异常");
        }
        return result.getValue();
    }

}
