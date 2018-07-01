package com.facishare.crm.sfa.predefine.controller;

import com.google.common.collect.Lists;

import com.facishare.paas.appframework.common.util.DocumentBaseEntity;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.metadata.ui.layout.IButton;

import java.util.List;

public class PriceBookListHeaderController extends StandardListHeaderController {
    protected StandardListHeaderController.Result doService(StandardListHeaderController.Arg arg) {
        StandardListHeaderController.Result ret = super.doService(arg);
        List<DocumentBaseEntity> fieldLs = ret.getFieldList();
        List<DocumentBaseEntity>  removeList = Lists.newArrayList();
        if(fieldLs==null)
            return ret;
        for(DocumentBaseEntity entity:fieldLs){
            if(entity.keySet().contains("account_range")||entity.keySet().contains("extend_obj_data_id")){
                removeList.add(entity);
            }
        }
        fieldLs.removeAll(removeList);
        ret.setFieldList(fieldLs);
        List<IButton> btnList = ret.getLayout().toLayout().getButtons();
        btnList.removeIf((IButton btn)->"IntelligentForm_button_default".equals(btn.getName()));
        ret.getLayout().toLayout().setButtons(btnList);
        return ret;
    }

}
