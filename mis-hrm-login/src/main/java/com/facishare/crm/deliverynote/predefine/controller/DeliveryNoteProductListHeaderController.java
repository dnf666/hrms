package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.crm.deliverynote.constants.SystemConstants;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.ILayout;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class DeliveryNoteProductListHeaderController extends StandardListHeaderController {

    @Override
    public Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        // 过滤掉新建按键
        ILayout layout = result.getLayout().toLayout();
        List<IButton> buttonList = layout.getButtons();
        buttonList = buttonList.stream()
                .filter(button -> !Objects.equals(SystemConstants.ActionCode.Add.getActionCode(), button.getAction()))
                .collect(Collectors.toList());
        layout.setButtons(buttonList);
        result.setLayout(LayoutDocument.of(layout));

        return result;
    }

}
