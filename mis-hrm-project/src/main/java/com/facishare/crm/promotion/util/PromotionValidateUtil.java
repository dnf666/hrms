package com.facishare.crm.promotion.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.GiftTypeEnum;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.crm.promotion.enums.PromotionRuleRecordTypeEnum;
import com.facishare.crm.promotion.enums.PromotionTypeEnum;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PromotionValidateUtil {

    public static void validatePromotionProduct(String tenantId, String promotionRecordType, List<IObjectData> promotionProductDatas, List<IObjectData> promotionRuleDatas, ServiceFacade serviceFacade) {
        List<String> productIds = Lists.newArrayList();
        if (PromotionRecordTypeEnum.ProductPromotion.apiName.equals(promotionRecordType)) {
            if (CollectionUtils.isEmpty(promotionProductDatas)) {
                throw new ValidateException("促销产品数据不能为空");
            }
            promotionProductDatas.forEach(promotionProductData -> {
                promotionProductData.setRecordType("default__c");
                String productId = promotionProductData.get(PromotionProductConstants.Field.Product.apiName, String.class);
                if (Objects.isNull(productId)) {
                    throw new ValidateException("订单产品不能为空");
                }
                productIds.add(productId);
            });
        }
        List<String> giftProductIds = promotionRuleDatas.stream().filter(promotionRuleData -> {
            String product = promotionRuleData.get(PromotionRuleConstants.Field.GiftProduct.apiName, String.class);
            return StringUtils.isNotEmpty(product);
        }).map(ob -> ob.get(PromotionRuleConstants.Field.GiftProduct.apiName, String.class)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(giftProductIds)) {
            productIds.addAll(giftProductIds);
        }
        if (CollectionUtils.isNotEmpty(productIds)) {
            List<IObjectData> productObjectDatas = serviceFacade.findObjectDataByIds(tenantId, productIds, Utils.PRODUCT_API_NAME);
            Map<String, String> idNameMap = productObjectDatas.stream().collect(Collectors.toMap(ob -> ob.getId(), IObjectData::getName));
            productIds.forEach(productId -> {
                if (!idNameMap.containsKey(productId)) {
                    throw new ValidateException(String.format("产品{%s}不存在", productId));
                }
            });
        }
    }

    public static void validDuplicatePromotionRuleAndField(String masterRecordType, String promotionType, List<IObjectData> promotionRuleDatas) {
        if (CollectionUtils.isEmpty(promotionRuleDatas)) {
            throw new ValidateException("促销规则数据不能为空");
        }
        Set<Object> keys = Sets.newHashSet();
        promotionRuleDatas.forEach(promotionRuleData -> {
            Object key = validateDetailField(PromotionTypeEnum.get(promotionType), promotionRuleData);
            if (PromotionRecordTypeEnum.OrderPromotion.apiName.equals(masterRecordType)) {
                promotionRuleData.setRecordType(PromotionRuleRecordTypeEnum.OrderPromotion.apiName);
                if (keys.contains(key)) {
                    throw new ValidateException(String.format("促销规则{%s}不可重复", PromotionRuleConstants.Field.OrderMoney.label));
                }
            } else if (PromotionRecordTypeEnum.ProductPromotion.apiName.equals(masterRecordType)) {
                promotionRuleData.setRecordType(PromotionRuleRecordTypeEnum.ProductPromotion.apiName);
                if (keys.contains(key)) {
                    throw new ValidateException(String.format("促销规则{%s}不可重复", PromotionRuleConstants.Field.PurchaseNum.label));
                }
            }
            keys.add(key);
        });
    }

    public static Object validateDetailField(PromotionTypeEnum promotionType, IObjectData promotionRuleData) {
        Set<PromotionTypeEnum> productPromotionEnumSet = Sets.newHashSet(PromotionTypeEnum.PriceDiscount, PromotionTypeEnum.DerateMoney, PromotionTypeEnum.FixedPrice, PromotionTypeEnum.NumberReachedGift);
        Set<PromotionTypeEnum> orderPromotionEnumSet = Sets.newHashSet(PromotionTypeEnum.OrderDiscount, PromotionTypeEnum.OrderDerateMoney, PromotionTypeEnum.OrderMoneyReachedGift);
        String errorMessageFormat = "促销规则为{%s}，从对象促销规则{%s}字段不能为空！";
        if (productPromotionEnumSet.contains(promotionType)) {
            Integer purchaseNum = promotionRuleData.get(PromotionRuleConstants.Field.PurchaseNum.apiName, Integer.class);
            if (Objects.isNull(purchaseNum)) {
                throw new ValidateException(String.format(errorMessageFormat, promotionType.label, PromotionRuleConstants.Field.PurchaseNum.label));
            }
            promotionRuleData.set(PromotionRuleConstants.Field.OrderDerateMoney.apiName, null);
            promotionRuleData.set(PromotionRuleConstants.Field.OrderMoney.apiName, null);
            promotionRuleData.set(PromotionRuleConstants.Field.OrderDiscount.apiName, null);
            if (PromotionTypeEnum.PriceDiscount.equals(promotionType)) {
                BigDecimal priceDiscount = promotionRuleData.get(PromotionRuleConstants.Field.PriceDiscount.apiName, BigDecimal.class);
                if (Objects.isNull(priceDiscount)) {
                    throw new ValidateException(String.format(errorMessageFormat, promotionType.label, PromotionRuleConstants.Field.PriceDiscount.label));
                }
                promotionRuleData.set(PromotionRuleConstants.Field.GiftType.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProduct.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProductNum.apiName, null);
                //            promotionRuleData.set(PromotionRuleConstants.Field.PriceDiscount.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.DerateMoney.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.FixedPrice.apiName, null);
            } else if (PromotionTypeEnum.DerateMoney.equals(promotionType)) {
                BigDecimal derateMoney = promotionRuleData.get(PromotionRuleConstants.Field.DerateMoney.apiName, BigDecimal.class);
                if (Objects.isNull(derateMoney)) {
                    throw new ValidateException(String.format(errorMessageFormat, promotionType.label, PromotionRuleConstants.Field.DerateMoney.label));
                }
                promotionRuleData.set(PromotionRuleConstants.Field.GiftType.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProduct.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProductNum.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.PriceDiscount.apiName, null);
                //                promotionRuleData.set(PromotionRuleConstants.Field.DerateMoney.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.FixedPrice.apiName, null);
            } else if (PromotionTypeEnum.FixedPrice.equals(promotionType)) {
                BigDecimal fixedPrice = promotionRuleData.get(PromotionRuleConstants.Field.FixedPrice.apiName, BigDecimal.class);
                if (Objects.isNull(fixedPrice)) {
                    throw new ValidateException(String.format(errorMessageFormat, promotionType.label, PromotionRuleConstants.Field.FixedPrice.label));
                }
                promotionRuleData.set(PromotionRuleConstants.Field.GiftType.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProduct.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProductNum.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.PriceDiscount.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.DerateMoney.apiName, null);
                //                promotionRuleData.set(PromotionRuleConstants.Field.FixedPrice.apiName, null);
            } else if (PromotionTypeEnum.NumberReachedGift.equals(promotionType)) {
                String giftType = promotionRuleData.get(PromotionRuleConstants.Field.GiftType.apiName, String.class);
                if (GiftTypeEnum.NormalProduct.value.equals(giftType)) {
                    String giftProduct = promotionRuleData.get(PromotionRuleConstants.Field.GiftProduct.apiName, String.class);
                    Integer giftProductNum = promotionRuleData.get(PromotionRuleConstants.Field.GiftProductNum.apiName, Integer.class);
                    if (Objects.isNull(giftProduct) || Objects.isNull(giftProductNum)) {
                        throw new ValidateException(String.format(errorMessageFormat, promotionType.label, "赠品信息相关"));
                    }
                } else if (GiftTypeEnum.Self.value.equals(giftType)) {
                    Integer giftProductNum = promotionRuleData.get(PromotionRuleConstants.Field.GiftProductNum.apiName, Integer.class);
                    if (Objects.isNull(giftProductNum)) {
                        throw new ValidateException(String.format(errorMessageFormat, promotionType.label, "赠送本品数量"));
                    }
                    promotionRuleData.set(PromotionRuleConstants.Field.GiftProduct.apiName, null);
                } else {
                    throw new ValidateException(String.format(errorMessageFormat, promotionType.label, "赠品类型"));
                }
                promotionRuleData.set(PromotionRuleConstants.Field.PriceDiscount.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.DerateMoney.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.FixedPrice.apiName, null);
            }
            return promotionRuleData.get(PromotionRuleConstants.Field.PurchaseNum.apiName);
        } else if (orderPromotionEnumSet.contains(promotionType)) {
            BigDecimal orderMoney = promotionRuleData.get(PromotionRuleConstants.Field.OrderMoney.apiName, BigDecimal.class);
            if (Objects.isNull(orderMoney)) {
                throw new ValidateException(String.format(errorMessageFormat, promotionType.label, PromotionRuleConstants.Field.OrderMoney.label));
            }
            promotionRuleData.set(PromotionRuleConstants.Field.GiftType.apiName, null);
            promotionRuleData.set(PromotionRuleConstants.Field.PurchaseNum.apiName, null);
            promotionRuleData.set(PromotionRuleConstants.Field.DerateMoney.apiName, null);
            promotionRuleData.set(PromotionRuleConstants.Field.PriceDiscount.apiName, null);
            if (PromotionTypeEnum.OrderDiscount.equals(promotionType)) {
                BigDecimal orderDiscount = promotionRuleData.get(PromotionRuleConstants.Field.OrderDiscount.apiName, BigDecimal.class);
                if (Objects.isNull(orderDiscount)) {
                    throw new ValidateException(String.format(errorMessageFormat, promotionType.label, PromotionRuleConstants.Field.OrderDiscount.label));
                }
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProduct.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProductNum.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.OrderDerateMoney.apiName, null);
            } else if (PromotionTypeEnum.OrderDerateMoney.equals(promotionType)) {
                BigDecimal orderDiscount = promotionRuleData.get(PromotionRuleConstants.Field.OrderDerateMoney.apiName, BigDecimal.class);
                if (Objects.isNull(orderDiscount)) {
                    throw new ValidateException(String.format(errorMessageFormat, promotionType.label, PromotionRuleConstants.Field.OrderDerateMoney.label));
                }
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProduct.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.GiftProductNum.apiName, null);
                //                promotionRuleData.set(PromotionRuleConstants.Field.OrderDerateMoney.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.OrderDiscount.apiName, null);
            } else if (PromotionTypeEnum.OrderMoneyReachedGift.equals(promotionType)) {
                String giftProduct = promotionRuleData.get(PromotionRuleConstants.Field.GiftProduct.apiName, String.class);
                Integer giftProductNum = promotionRuleData.get(PromotionRuleConstants.Field.GiftProductNum.apiName, Integer.class);
                if (Objects.isNull(giftProduct) || Objects.isNull(giftProductNum)) {
                    throw new ValidateException(String.format(errorMessageFormat, promotionType.label, "赠品信息相关"));
                }
                //                promotionRuleData.set(PromotionRuleConstants.Field.GiftProduct.apiName, null);
                //                promotionRuleData.set(PromotionRuleConstants.Field.GiftProductNum.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.OrderDerateMoney.apiName, null);
                promotionRuleData.set(PromotionRuleConstants.Field.OrderDiscount.apiName, null);
            }
            return promotionRuleData.get(PromotionRuleConstants.Field.OrderMoney.apiName);
        }
        throw new ValidateException(String.format("促销规则{%s}不存在", promotionType.label));
    }
}
