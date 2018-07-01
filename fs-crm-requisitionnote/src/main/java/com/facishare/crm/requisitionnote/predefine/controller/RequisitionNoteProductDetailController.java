package com.facishare.crm.requisitionnote.predefine.controller;

import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author liangk
 * @date 05/02/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteProductDetailController extends StandardDetailController {
    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        //隐藏锁定\编辑\作废按钮
        delRequisitionNoteProductButtons(result);
        return result;
    }

    private void delRequisitionNoteProductButtons(Result result) {

        ILayout layout = result.getLayout().toLayout();
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
