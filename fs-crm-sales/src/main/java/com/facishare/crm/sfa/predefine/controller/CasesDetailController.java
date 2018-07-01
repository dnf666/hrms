package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by luxin on 2018/4/28.
 */
@Slf4j
public class CasesDetailController extends StandardDetailController {
    @Override
    protected ILayout getLayout() {
        ILayout layout = super.getLayout();

        List<IComponent> components = Lists.newArrayList();
        try {
            for (IComponent component : layout.getComponents()) {
                if ("relatedObject".equals(component.getName())) {
                    components.add(0, component);
                } else {
                    components.add(component);
                }
            }
            layout.setComponents(components);
        } catch (MetadataServiceException e) {
            log.error("getComponents error.", e);
        }
        return layout;
    }

}
