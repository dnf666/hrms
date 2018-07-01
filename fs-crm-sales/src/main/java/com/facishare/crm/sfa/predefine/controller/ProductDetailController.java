package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.utilities.constant.ProductConstants;
import com.facishare.paas.appframework.core.model.AbstractController;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import com.facishare.paas.appframework.common.util.ObjectAction;
import lombok.extern.slf4j.Slf4j;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.CLIENT_INFO;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

/**
 * Created by luxin on 2018/1/15.
 */
@Slf4j
public class ProductDetailController extends SFADetailController {

    @Override
    protected Result after(Arg arg, Result result) {
        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            return super.after(arg, result);
        }
        Result newResult = super.after(arg, result);
        ILayout layout = new Layout(newResult.getLayout());
        handleButtons(result, layout);
        newResult.setLayout(LayoutDocument.of(layout));
        return newResult;
    }

    private void handleButtons(Result result, ILayout layout) {
        List<IButton> buttons = Lists.newArrayList();
        //根据客户端的类型,处理buttons
        handleProductStatus(result, buttons);
        List<IButton> oldButtons = layout.getButtons();
        oldButtons.addAll(buttons);
        layout.setButtons(oldButtons);
    }

    /**
     * 处理产品的状态,如果产品状态为上架,增加下架 button,如果为下架,增加上架 button
     *
     * @param result
     * @param buttons
     */
    private void handleProductStatus(Result result, List<IButton> buttons) {
        Object productStatus = result.getData().get("product_status");
        Map<Object, Object> map = Maps.newHashMap();

        map.put("action_type", "customer");
        map.put("action", "ProductStatus");
        if (productStatus != null) {
            if (productStatus.toString().equals(ProductConstants.Status.ON.getStatus())) {
                map.put("api_name", "ProductObject_Status_Off_button_custom__c");
                map.put("label", "下架");
                buttons.add(new Button(map));
            } else if (productStatus.toString().equals(ProductConstants.Status.OFF.getStatus())) {
                map.put("api_name", "ProductObject_Status_On_button_custom__c");
                map.put("label", "上架");
                buttons.add(new Button(map));
            }
        }
    }

    @Override
    protected ILayout getLayout() {
        ILayout layout = super.getLayout();

        try {
            specialLogicForLayout(layout);
        } catch (MetadataServiceException e) {
            log.error("getChildComponents error.", e);
        }
        removeRelatedObjBtns(layout);
        return layout;
    }

    private void removeRelatedObjBtns(ILayout layout) {
        final List<String> removeActionsRelatedObjs = Lists.newArrayList("PriceBookProductObj", "GoodsReceivedNoteProductObj", "DeliveryNoteProductObj");
        LayoutExt.of(layout).getRelatedComponent().ifPresent(x -> {
            try {
                x.getChildComponents().forEach(childComponent -> {
                    if (removeActionsRelatedObjs.contains(childComponent.get("ref_object_api_name", String.class))) {
                        List<IButton> buttons = Lists.newArrayList();
                        childComponent.setButtons(buttons);
                    }
                });
            } catch (MetadataServiceException ignored) {
            }
        });

    }

    private void specialLogicForLayout(ILayout layout) throws MetadataServiceException {
        List<IButton> buttons = layout.getButtons();
        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            //终端不下发销售记录，相关团队，附件 卡片
            LayoutExt.of(layout).getRelatedComponent().ifPresent(x -> {
                try {
                    List<IComponent> components = x.getChildComponents();
                    components.removeIf(component -> "sale_log".equals(component.getName())
                            || "relevant_team_component".equals(component.getName())
                            || "ProductAttObj_related_list".equals(component.getName()));

                    x.setChildComponents(components);
                } catch (MetadataServiceException ignored) {
                }
            });

            List<IComponent> groupComponentList = layout.getComponents();
            List<IComponent> removeComponentList = Lists.newArrayList();
            for (IComponent component : groupComponentList) {
                if (component.getName().equals("otherInfo")) {
                    removeComponentList.add(component);
                }
            }
            groupComponentList.removeAll(removeComponentList);
            layout.setComponents(groupComponentList);
            buttons.removeIf(k -> {
                String action = k.getAction();

                return action.equals("UpdateStatus")
                        || action.equals("AddSpec") ||
                        action.equals("ViewAttach") ||
                        action.equals("ProductStatus") ||
                        action.equals("EditTeamMember") ||
                        action.equals("UploadDeleteAttach") ||
                        action.equals(ObjectAction.DELETE.getActionCode()) ||
                        action.equals(ObjectAction.PRINT.getActionCode()) ||
                        action.equals(ObjectAction.START_BPM.getActionCode()) ||
                        action.equals(ObjectAction.CREATE.getActionCode()) ||
                        action.equals(ObjectAction.UPDATE.getActionCode()) ||
                        action.equals(ObjectAction.INTELLIGENTFORM.getActionCode()) ||
                        action.equals(ObjectAction.CLONE.getActionCode()) ||
                        action.equals(ObjectAction.EDIT_TEAM_MEMBER.getActionCode()) ||
                        action.equals(ObjectAction.INVALID.getActionCode()) ||
                        action.equals(ObjectAction.VIEW_DETAIL.getActionCode());
            });

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
}
