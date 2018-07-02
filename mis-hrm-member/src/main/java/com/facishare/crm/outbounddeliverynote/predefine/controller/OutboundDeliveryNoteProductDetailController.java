package com.facishare.crm.outbounddeliverynote.predefine.controller;

import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author linchf
 * @date 2018/3/22
 */
@Slf4j
public class OutboundDeliveryNoteProductDetailController extends StandardDetailController {
    @Override
    protected StandardDetailController.Result after(StandardDetailController.Arg arg, StandardDetailController.Result result) {
        result = super.after(arg, result);
        //隐藏锁定\编辑\作废按钮
        delOutboundDeliveryNoteProductButtons(result);
        return result;
    }

    private void delOutboundDeliveryNoteProductButtons(StandardDetailController.Result result) {
        Layout layout = new Layout(result.getLayout());
        List<IButton> buttons = layout.getButtons();
        if (CollectionUtils.isEmpty(buttons)) {
            return;
        }

        List<IButton> newButtons = Lists.newArrayList();
        for (IButton button : buttons) {
            if (!button.getAction().equals(ObjectAction.INVALID.getActionCode()) &&
                    !button.getAction().equals(ObjectAction.LOCK.getActionCode()) &&
                    !button.getAction().equals(ObjectAction.UPDATE.getActionCode())) {
                newButtons.add(button);
            }
        }
        layout.setButtons(newButtons);
        result.setLayout(LayoutDocument.of(layout));
    }
}
