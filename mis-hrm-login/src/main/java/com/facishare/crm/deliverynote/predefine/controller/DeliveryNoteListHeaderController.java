package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chenzs on 2018/2/23.
 */
public class DeliveryNoteListHeaderController extends StandardListHeaderController {
    @Override
    protected StandardListHeaderController.Result doService(StandardListHeaderController.Arg arg) {
        StandardListHeaderController.Result ret = super.doService(arg);
        LayoutDocument layout = ret.getLayout();

        //隐藏发货单列表页的导入按钮
        List<Map<String, String>> buttons = (List<Map<String,String>>) layout.get("buttons");
        buttons.removeIf(button -> Objects.equals(button.get("action"), ObjectAction.BATCH_IMPORT.getActionCode()));
        layout.put("buttons", buttons);

        ret.setLayout(layout);
        return ret;
    }
}