package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PartnerDetailController extends StandardDetailController {

    @Override
    protected ILayout getLayout() {
        ILayout layout = super.getLayout();
        removePartnerRelatedBtn(layout);
        return layout;
    }

    /**
     * 移除合作伙伴下的所有关联对象的关联、取消关联
     */
    private void removePartnerRelatedBtn(ILayout layout) {
        LayoutExt layoutExt = LayoutExt.of(layout);
        layoutExt.getRelatedComponent().ifPresent(relatedComponent -> {
            try {
                List<IComponent> childComponents = relatedComponent.getChildComponents();
                childComponents.stream().forEach(childComponent -> {
                    List<IButton> btnList = childComponent.getButtons();
                    if (CollectionUtils.isNotEmpty(btnList)) {
                        btnList.removeIf(btn -> "BulkRelate".equals(btn.getAction()) || "BulkDisRelate".equals(btn.getAction()));
                    }
                    //如果是预制联系人页签，则移除客户名称字段
                    if ("partner_contact_list".equals(childComponent.get("related_list_name", String.class))) {
                        List<Map> includeFields = childComponent.get("include_fields", List.class);
                        includeFields.removeIf(k -> "account_id".equals(k.get("field_name")));
                        childComponent.set("include_fields", includeFields);
                    }
                    childComponent.setButtons(btnList);
                });
                relatedComponent.setChildComponents(childComponents);
            } catch (MetadataServiceException e) {
                log.warn("PartnerDetailController getChildComponents error", e);
            }
        });
    }
}
