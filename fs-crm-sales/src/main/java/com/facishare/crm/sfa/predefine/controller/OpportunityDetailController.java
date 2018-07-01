package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.RelatedObjectList;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.CLIENT_INFO;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

public class OpportunityDetailController extends SFADetailController {
    @Override
    public Result doService(Arg arg) {
        Result rst = super.doService(arg);

        return rst;
    }
    @Override
    protected Result after(Arg arg, Result result) {
        Result newResult = super.after(arg, result);
        ILayout layout = new Layout(newResult.getLayout());
        ObjectDataDocument objectData = result.getData();
        //web处理是否决策人这个字段类型时，无法采用字符串方式
        if (!ObjectDataExt.of(objectData).isInvalid())
        {
            specialLogicForLayout(layout);
        }

        return result;
    }

    private void specialLogicForLayout(ILayout layout) {

        List<IButton> buttons = layout.getButtons();

        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            List<IButton> remainButtons = Lists.newCopyOnWriteArrayList(buttons);
            buttons.clear();
            buttons.add(createButton(ObjectAction.SALE_RECORD));
            buttons.add(createButton(ObjectAction.DIAL));
            buttons.add(createButton(ObjectAction.SEND_MAIL));
            buttons.add(createButton(ObjectAction.DISCUSS));
            buttons.add(createButton(ObjectAction.SCHEDULE));
            buttons.add(createButton(ObjectAction.REMIND));
            buttons.addAll(remainButtons);
        }
        layout.setButtons(buttons);
    }
    private IButton createButton(ObjectAction action) {
        IButton button = new Button();
        button.setName(action.getActionCode() + "_button_" + IButton.ACTION_TYPE_DEFAULT);
        button.setAction(action.getActionCode());
        button.setActionType(IButton.ACTION_TYPE_DEFAULT);
        button.setLabel(action.getActionLabel());
        return button;
    }
    private void removePhoneButtons(List<IButton> buttons) {
        buttons.removeIf(k -> k.getAction().equals(ObjectAction.SCHEDULE.getActionCode()) ||
                k.getAction().equals(ObjectAction.REMIND.getActionCode()));
    }
}
