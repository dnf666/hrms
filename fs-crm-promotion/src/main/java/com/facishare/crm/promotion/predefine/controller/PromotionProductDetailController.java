package com.facishare.crm.promotion.predefine.controller;

import java.util.List;

import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IButton;

public class PromotionProductDetailController extends StandardDetailController {

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        LayoutDocument layoutDocument = result.getLayout();
        List<IButton> buttonList = LayoutExt.of(new Layout(layoutDocument)).getButtons();
        buttonList.removeIf(button -> button.getAction().equals(ObjectAction.UPDATE.getActionCode()) || button.getAction().equals(ObjectAction.CLONE.getActionCode()));
        LayoutExt.of(new Layout(layoutDocument)).setButtons(buttonList);
        return result;
    }
}
