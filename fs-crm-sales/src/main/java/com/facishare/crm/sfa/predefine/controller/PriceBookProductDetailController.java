package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.predefine.service.PriceBookService;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.facishare.crm.sfa.utilities.constant.PriceBookConstants.ProductField.PRODUCTID;
import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.CLIENT_INFO;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

@Slf4j
public class PriceBookProductDetailController extends StandardDetailController {
    private PriceBookService priceBookService = (PriceBookService) SpringUtil.getContext().getBean("priceBookService");

    @Override
    protected ILayout getLayout() {
        ILayout layout = super.getLayout();
        specialLogicForLayout(layout);
        return layout;
    }

    private void specialLogicForLayout(ILayout layout) {
        List<IButton> buttons = layout.getButtons();
        //标准价目表产品，不下发删除按钮
        if (!priceBookService.isStandardProduct(this.getControllerContext().getTenantId(),
                this.data.get(PriceBookConstants.ProductField.PRICEBOOKID.getApiName()).toString())) {
            buttons.forEach(button -> {
                //作废的按钮改为删除
                if (button.getAction().equals(ObjectAction.INVALID.getActionCode())) {
                    Map<String, Boolean> map = serviceFacade.funPrivilegeCheck(this.getControllerContext().getUser(), PriceBookConstants.API_NAME, StandardAction.BulkDelete.getFunPrivilegeCodes());
                    Boolean deleteResult = map.get(StandardAction.BulkDelete.getFunPrivilegeCodes().get(0));
                    // TODO: 2017/11/23 标准价目表下不能有删除按鈕
                    if (deleteResult != null && deleteResult) {
                        button.setAction(ObjectAction.DELETE.getActionCode());
                        button.setLabel("删除");
                        button.setName(ObjectAction.DELETE.getActionCode() + "_button_default");
                    }
                }
            });
        } else {
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.INVALID.getActionCode()));
        }
        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.DELETE.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.PRINT.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.START_BPM.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.CREATE.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.UPDATE.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.INTELLIGENTFORM.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.CLONE.getActionCode()));
        }
        layout.setButtons(buttons);
        try {
            List<IComponent> groupComponentList = layout.getComponents();
            for (IComponent component : groupComponentList) {
                if (component.getName().equals("detailInfo")) {
                    GroupComponent groupComponent = (GroupComponent) component;
                    FormComponent formComponent = (FormComponent) groupComponent.getChildComponents().get(0);
                    List<IFieldSection> fieldSections = formComponent.getFieldSections();
                    for (IFieldSection fieldSection : fieldSections) {
                        List<IFormField> fields = fieldSection.getFields();
                        for (IFormField field : fields) {
                            if (PRODUCTID.getApiName().equals(field.getFieldName())) {
                                field.setReadOnly(true);
                            }
                        }
                    }
                }
                if (component.getName().equals("relatedObject")) {
                    //价目表产品下要把关联的销售订单产品页签去掉。
                    ArrayList<Map> childComponents = (ArrayList) ((GroupComponent) component).getContainerDocument().get("child_components");
                    childComponents.removeIf((Map entry) -> "SalesOrderProductObj".equals(entry.get("ref_object_api_name")));
                }
            }
        } catch (MetadataServiceException e) {
            log.error("component exception:", e);
        }

        //根据配置隐藏关联对象列表的新建按钮
        PreDefLayoutUtil.invisibleRefObjectListAddButton(arg.getObjectDescribeApiName(),layout);
    }
}
