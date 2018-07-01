package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.ILayout;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单详情页
 * Created by zhaopx on 2017/11/14.
 */
@Slf4j
public class SalesOrderDetailController extends SFADetailController {

    @Override
    public Result doService(Arg arg) {
        Result rst = super.doService(arg);

        ILayout layout = new Layout(rst.getLayout());
        handleLayoutComponents(layout);

        return rst;
    }


    protected void handleLayoutComponents(ILayout layout) {
        try {
            layout.getComponents().forEach(component -> {
                if (component.getName().equals("relatedObject")) {
                    GroupComponent groupComponent = (GroupComponent) component;
                    try {
                        //退货单产品新建按钮不可见
                        groupComponent.getChildComponents().stream()
                                .filter(childComponent -> childComponent.get("api_name", String.class).equals("DeliveryNoteProductObj_sales_order_id_related_list"))
                                .forEach(childComponent -> {
                                    List<IButton> resultButtons = childComponent.getButtons().stream().filter(b -> !b.getAction().equals("Add")).collect(Collectors.toList());
                                    childComponent.setButtons(resultButtons);
                                });
                    } catch (MetadataServiceException e) {
                        log.error("getChildComponents error.", e);
                    }
                }
            });
        } catch (MetadataServiceException e) {
            log.error("SalesOrderDetailController>handleLayoutComponents>getComponents error", e);
        }
    }
}
