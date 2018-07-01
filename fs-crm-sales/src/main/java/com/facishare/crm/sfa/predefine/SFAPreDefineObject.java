package com.facishare.crm.sfa.predefine;

import com.google.common.collect.Lists;

import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ActionClassInfo;
import com.facishare.paas.appframework.core.model.ControllerClassInfo;
import com.facishare.paas.appframework.core.model.PreDefineObject;
import com.facishare.paas.appframework.core.model.PreDefineObjectRegistry;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * SFA 预定义对象定义并注册
 * <p>
 * Created by liyiguang on 2017/7/9.
 */

@Slf4j
public enum SFAPreDefineObject implements PreDefineObject {

    Account("AccountObj"), Contact("ContactObj"), SalesOrder("SalesOrderObj"), PriceBook("PriceBookObj"), PriceBookProduct("PriceBookProductObj"), Product("ProductObj"),
    Payment("PaymentObj"), Refund("RefundObj"), Opportunity("OpportunityObj"), Leads("LeadsObj"), ReturnedGoodsInvoice("ReturnedGoodsInvoiceObj"),
    Quote("QuoteObj"), QuoteLines("QuoteLinesObj"), ReturnedGoodsInvoiceProduct("ReturnedGoodsInvoiceProductObj"),
    SalesOrderProduct("SalesOrderProductObj"), Cases("CasesObj"), Partner("PartnerObj"), GoalValue("GoalValueObj"), Visiting("VisitingObj");

    SFAPreDefineObject(String apiName) {
        this.apiName = apiName;
    }

    public static SFAPreDefineObject getEnum(String apiName) {
        List<SFAPreDefineObject> list = Arrays.asList(SFAPreDefineObject.values());
        return list.stream().filter(m -> m.getApiName().equalsIgnoreCase(apiName)).findAny().orElse(null);
    }

    @Override
    public String getApiName() {
        return apiName;
    }

    @Override
    public ActionClassInfo getDefaultActionClassInfo(String actionCode) {
        String className = PACKAGE_NAME + ".action." + this + actionCode + "Action";
        ActionClassInfo actionClassInfo = new ActionClassInfo(className);
        if (isPartnerAction(actionCode)) {
            //合作伙伴操作，暂时没有移动到passframework中，自定义对象层也不支持动态配置动态业务action路由，所以此处特殊处理
            if (!check(actionClassInfo)) {
                return new ActionClassInfo(PACKAGE_NAME + ".action.Standard" + actionCode + "Action");
            }
        }
        return actionClassInfo;
    }

    private Boolean isPartnerAction(String actionCode) {
        List<String> partnerActionCodes = Lists.newArrayList(ObjectAction.CHANGE_PARTNER.getActionCode(),
                ObjectAction.CHANGE_PARTNER_OWNER.getActionCode(), ObjectAction.DELETE_PARTNER.getActionCode());
        return partnerActionCodes.contains(actionCode);
    }

    public ControllerClassInfo getControllerClassInfo(String methodName) {
        String className = PACKAGE_NAME + ".controller." + this + methodName + "Controller";
        return new ControllerClassInfo(className);
    }

    @Override
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    private String apiName;
    private static String PACKAGE_NAME = SFAPreDefineObject.class.getPackage().getName();

    private boolean check(ActionClassInfo classInfo) {
        try {
            Class.forName(classInfo.getClassName());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void init() {
        for (SFAPreDefineObject object : SFAPreDefineObject.values()) {
            PreDefineObjectRegistry.register(object);
        }
    }

}
