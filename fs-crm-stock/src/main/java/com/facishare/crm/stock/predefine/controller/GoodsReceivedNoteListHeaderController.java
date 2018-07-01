package com.facishare.crm.stock.predefine.controller;

import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by linchf on 2018/2/23.
 */
@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteListHeaderController extends StandardListHeaderController {
    @Override
    protected StandardListHeaderController.Result doService(StandardListHeaderController.Arg arg) {
        StandardListHeaderController.Result ret = super.doService(arg);
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
    }
}
