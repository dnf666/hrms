package com.facishare.crm.requisitionnote.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by linchf on 2018/2/23.
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteListHeaderController extends StandardListHeaderController {
/*    @Override
    protected Result doService(Arg arg) {
        Result ret = super.doService(arg);
        LayoutDocument layout = ret.getLayout();

        //隐藏导入按钮
        List<Map<String, String>> buttons = (List<Map<String,String>>) layout.get("buttons");
        if (CollectionUtils.isEmpty(buttons)) {
            return ret;
        }

        buttons.removeIf(button -> {
            Object action = button.get("action");
            return null != action && Objects.equals(action.toString(), ObjectAction.BATCH_IMPORT.getActionCode());
        });
        layout.put("buttons", buttons);

        ret.setLayout(layout);
        return ret;
    }*/
}
