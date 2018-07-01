package com.facishare.crm.sfa.predefine.controller;


import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProductRelatedController extends SFARelatedController {
    protected StandardRelatedController.Result doService(StandardRelatedController.Arg arg) {
        arg.setIsIncludeLayout(true);
        Result result = super.doService(arg);
        try {
            specialLogicForLayout(result.getLayout());
        } catch (MetadataServiceException e) {
            log.error("specialLogicForLayout error.", e);
        }
        return result;
    }

    private void specialLogicForLayout(LayoutDocument layout) throws MetadataServiceException {
        ILayout layout1 = layout.toLayout();
        List<IComponent> groupComponentList = layout1.getComponents();
        for (IComponent component : groupComponentList) {
            if (component.getName().equals("relatedObject")) {
                //价目表产品页签的所有button都去掉。
                ArrayList<Map> childComponents = (ArrayList) ((GroupComponent) component).getContainerDocument().get("child_components");
                for (Map entry : childComponents) {
                    if ("PriceBookProductObj".equals(entry.get("ref_object_api_name"))) {
                        ArrayList btnList = (ArrayList) entry.get("buttons");
                        btnList.clear();
                    }
                }

            }
        }
        return;
    }
}
