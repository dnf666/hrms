package com.facishare.crm.promotion.predefine.action;

import java.util.List;

import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.util.PromotionValidateUtil;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;

public class PromotionEditAction extends StandardEditAction {
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        String promotionType = objectData.get(PromotionConstants.Field.Type.apiName, String.class);
        String masterRecordType = objectData.getRecordType();
        List<IObjectData> promotionProductDatas = detailObjectData.get(PromotionProductConstants.API_NAME);
        List<IObjectData> promotionRuleDatas = detailObjectData.get(PromotionRuleConstants.API_NAME);
        PromotionValidateUtil.validDuplicatePromotionRuleAndField(masterRecordType, promotionType, promotionRuleDatas);
        PromotionValidateUtil.validatePromotionProduct(actionContext.getTenantId(), masterRecordType, promotionProductDatas, promotionRuleDatas, serviceFacade);
    }
}
