package com.facishare.crm.promotion.predefine.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.GiftTypeEnum;
import com.facishare.crm.promotion.enums.PromotionRuleRecordTypeEnum;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.describe.ObjectDescribe;
import com.facishare.paas.metadata.impl.describe.RecordTypeFieldDescribe;

public class PromotionRuleRelatedListController extends StandardRelatedListController {

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        List<ObjectDataDocument> objectDataList = result.getDataList();
        List<String> recordTypes = objectDataList.stream().map(objectDataDocument -> {
            ObjectData objectData = new ObjectData(objectDataDocument);
            String recordType = objectData.getRecordType();
            String leftDescriptionFormat = "%s%s%s";
            String rightDescriptionFormat = "%s%s%s";
            String left = "";
            String right = "";
            String giftProductName = objectData.get(PromotionRuleConstants.Field.GiftProduct.apiName.concat("__r"), String.class);
            String giftProductId = objectData.get(PromotionRuleConstants.Field.GiftProduct.apiName, String.class);
            Integer giftProductNum = objectData.get(PromotionRuleConstants.Field.GiftProductNum.apiName, Integer.class);
            if (PromotionRuleRecordTypeEnum.ProductPromotion.apiName.equals(recordType)) {
                Integer purchaseNum = objectData.get(PromotionRuleConstants.Field.PurchaseNum.apiName, Integer.class);
                BigDecimal derateMoney = objectData.get(PromotionRuleConstants.Field.DerateMoney.apiName, BigDecimal.class);
                BigDecimal priceDiscount = objectData.get(PromotionRuleConstants.Field.PriceDiscount.apiName, BigDecimal.class);
                BigDecimal fixedPrice = objectData.get(PromotionRuleConstants.Field.FixedPrice.apiName, BigDecimal.class);
                String giftType = objectData.get(PromotionRuleConstants.Field.GiftType.apiName, String.class);
                left = String.format(leftDescriptionFormat, "购买满", purchaseNum, "件");
                if (Objects.nonNull(derateMoney)) {
                    right = String.format(rightDescriptionFormat, "销售单价减", derateMoney, "元");
                } else if (Objects.nonNull(priceDiscount)) {
                    right = String.format(rightDescriptionFormat, "销售单价折扣", priceDiscount, "%");
                } else if (Objects.nonNull(fixedPrice)) {
                    right = String.format(rightDescriptionFormat, "销售单价降为", fixedPrice, "元");
                } else if (Objects.nonNull(giftProductId)) {//6.2开通促销的企业，giftType为空,giftProductId 不为空
                    right = String.format(rightDescriptionFormat, "赠送", giftProductName + " " + giftProductNum, "件");
                } else if (Objects.nonNull(giftType)) {
                    if (GiftTypeEnum.Self.value.equals(giftType)) {
                        right = String.format(rightDescriptionFormat, "赠送本品 ", giftProductNum, "件");
                    } else {
                        right = String.format(rightDescriptionFormat, "赠送", giftProductName + " " + giftProductNum, "件");
                    }
                }
            } else if (PromotionRuleRecordTypeEnum.OrderPromotion.apiName.equals(recordType)) {
                BigDecimal orderMoney = objectData.get(PromotionRuleConstants.Field.OrderMoney.apiName, BigDecimal.class);
                left = String.format(leftDescriptionFormat, "订单金额满", orderMoney, "元");
                BigDecimal orderDiscount = objectData.get(PromotionRuleConstants.Field.OrderDiscount.apiName, BigDecimal.class);
                BigDecimal orderDerateMoney = objectData.get(PromotionRuleConstants.Field.OrderDerateMoney.apiName, BigDecimal.class);
                if (Objects.nonNull(orderDiscount)) {
                    right = String.format(rightDescriptionFormat, "金额折扣", orderDiscount, "%");
                } else if (Objects.nonNull(orderDerateMoney)) {
                    right = String.format(rightDescriptionFormat, "立减", orderDerateMoney, "元");
                } else if (Objects.nonNull(giftProductId)) {
                    right = String.format(rightDescriptionFormat, "赠送", giftProductName + " " + giftProductNum, "件");
                }
            }
            objectData.set("promotion_rule_description", left + "，" + right);
            return recordType;
        }).collect(Collectors.toList());
        ObjectDescribeDocument objectDescribe = result.getObjectDescribe();
        ObjectDescribeExt objectDescribeExt = ObjectDescribeExt.of(new ObjectDescribe(objectDescribe));
        RecordTypeFieldDescribe recordTypeFieldDescribe = (RecordTypeFieldDescribe) objectDescribeExt.getFieldDescribe(SystemConstants.Field.RecordType.apiName);
        List<IRecordTypeOption> recordTypeOptions = recordTypeFieldDescribe.getRecordTypeOptions();
        recordTypeOptions.removeIf(iSelectOption -> !recordTypes.contains(iSelectOption.getApiName()));
        recordTypeFieldDescribe.setRecordTypeOptions(recordTypeOptions);
        return result;
    }
}
