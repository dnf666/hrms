package com.facishare.crm.promotion.predefine.action;

import java.util.List;

import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.crm.promotion.util.PromotionValidateUtil;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.facishare.paas.metadata.api.IObjectData;

public class PromotionAddAction extends StandardAddAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        checkPromotionRule();
    }

    private void checkPromotionRule() {
        String promotionType = objectData.get(PromotionConstants.Field.Type.apiName, String.class);
        String masterRecordType = objectData.getRecordType();
        List<IObjectData> promotionProductDatas = detailObjectData.get(PromotionProductConstants.API_NAME);
        List<IObjectData> promotionRuleDatas = detailObjectData.get(PromotionRuleConstants.API_NAME);
        PromotionValidateUtil.validDuplicatePromotionRuleAndField(masterRecordType, promotionType, promotionRuleDatas);
        PromotionValidateUtil.validatePromotionProduct(actionContext.getTenantId(), masterRecordType, promotionProductDatas, promotionRuleDatas, serviceFacade);
        if (PromotionRecordTypeEnum.OrderPromotion.apiName.equals(masterRecordType) && detailObjectData.containsKey(PromotionProductConstants.API_NAME)) {
            //订单促销没有促销产品
            detailObjectData.remove(PromotionProductConstants.API_NAME);
        }
    }

}
