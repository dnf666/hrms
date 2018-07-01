package com.facishare.crm.promotion.predefine.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facishare.paas.metadata.util.SpringUtil;
import org.apache.commons.collections.CollectionUtils;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.predefine.manager.SalesOrderManager;
import com.facishare.crm.rest.dto.ObjectRelatedPromotionModel;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PromotionBulkInvalidAction extends StandardBulkInvalidAction {
    private SalesOrderManager salesOrderManager;

    @Override
    protected void before(Arg arg) {
        salesOrderManager = SpringUtil.getContext().getBean(SalesOrderManager.class);
        super.before(arg);
        Map<String, String> idNameMap = objectDataList.stream().filter(objectData -> objectData.getDescribeApiName().equals(PromotionConstants.API_NAME)).collect(Collectors.toMap(objectData -> objectData.getId(), IObjectData::getName));
        //TODO
        List<ObjectRelatedPromotionModel.RelatedPromotionVo> relatedPromotionVos = salesOrderManager.listRelatedPromotionObjectsByPromotionIds(actionContext.getUser(), Lists.newArrayList(idNameMap.keySet()));
        if (CollectionUtils.isNotEmpty(relatedPromotionVos)) {
            Map<Integer, List<ObjectRelatedPromotionModel.RelatedPromotionVo>> objectTypeVoMap = relatedPromotionVos.stream().collect(Collectors.groupingBy(x -> x.getObjectType()));
            StringBuilder errorMessage = new StringBuilder();
            for (Map.Entry<Integer, List<ObjectRelatedPromotionModel.RelatedPromotionVo>> entry : objectTypeVoMap.entrySet()) {
                int obType = entry.getKey();
                List<ObjectRelatedPromotionModel.RelatedPromotionVo> relatedVos = entry.getValue();
                if (obType == CommonConstants.SALES_ORDER_OBJECT_TYPE) {//销售订单
                    Map<String, List<String>> promotionVoNamesMap = Maps.newHashMap();
                    for (ObjectRelatedPromotionModel.RelatedPromotionVo relatedPromotionVo : relatedVos) {
                        String promotionName = idNameMap.get(relatedPromotionVo.getPromotionId());
                        if (!promotionVoNamesMap.containsKey(promotionName)) {
                            promotionVoNamesMap.put(promotionName, Lists.newArrayList());
                        }
                        promotionVoNamesMap.get(promotionName).add(relatedPromotionVo.getTradeCode());
                    }
                    String promotionName = Lists.newArrayList(promotionVoNamesMap.keySet()).get(0);
                    errorMessage.append(String.format("促销{%s}被销售订单{%s}使用,不可作废！", promotionName, Joiner.on(",").join(promotionVoNamesMap.get(promotionName))));
                    break;
                } else if (obType == CommonConstants.SALES_ORDER_PRODUCT_OBJECT_TYPE) {//订单产品
                    Map<String, List<String>> promotionVoNamesMap = Maps.newHashMap();
                    for (ObjectRelatedPromotionModel.RelatedPromotionVo relatedPromotionVo : relatedVos) {
                        String promotionName = idNameMap.get(relatedPromotionVo.getPromotionId());
                        if (!promotionVoNamesMap.containsKey(promotionName)) {
                            promotionVoNamesMap.put(promotionName, Lists.newArrayList());
                        }
                        promotionVoNamesMap.get(promotionName).add(relatedPromotionVo.getTradeCode());
                    }
                    String name = Lists.newArrayList(promotionVoNamesMap.keySet()).get(0);
                    String tradeCode = promotionVoNamesMap.get(name).get(0);
                    List<String> productNameList = relatedVos.stream().filter(vo -> vo.getTradeCode().equals(tradeCode)).map(ObjectRelatedPromotionModel.RelatedPromotionVo::getProductName).collect(Collectors.toList());
                    errorMessage.append(String.format("促销{%s}被销售订单{%s}下的订单产品{%s}使用,不可作废！", name, tradeCode, Joiner.on(",").join(productNameList)));
                    break;
                }
            }

            if (errorMessage.length() > 0) {
                throw new ValidateException(errorMessage.toString());
            }
        }
    }
}
