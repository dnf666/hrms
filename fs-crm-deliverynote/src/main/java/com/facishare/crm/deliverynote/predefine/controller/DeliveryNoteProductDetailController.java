package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.constants.SystemConstants;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteProductManager;
import com.facishare.crm.deliverynote.predefine.manager.SalesOrderManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发货单详情页
 * Created by chenzs on 2018/1/8.
 */
@Slf4j
public class DeliveryNoteProductDetailController extends StandardDetailController {
    private SalesOrderManager salesOrderManager;
    private DeliveryNoteProductManager deliveryNoteProductManager;

    @Override
    public Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        this.salesOrderManager = SpringUtil.getContext().getBean(SalesOrderManager.class);
        this.deliveryNoteProductManager = SpringUtil.getContext().getBean(DeliveryNoteProductManager.class);

        // 填充订单产品数及已发货数
        fillOrderAmountAndDeliveredAmount(result);
        // 过滤编辑按钮
        filterEditButton(result);
        return result;
    }

    private void fillOrderAmountAndDeliveredAmount(Result result) {
        String salesOrderId = result.getData().toObjectData().get(DeliveryNoteProductObjConstants.Field.SalesOrderId.apiName, String.class);
        String productId = result.getData().toObjectData().get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class);
        User user = this.getControllerContext().getUser();
        //订单产品数量
        Map<String,BigDecimal> productId2OrderAmount = salesOrderManager.getOrderProductAmountMap(user, salesOrderId);
        BigDecimal salesOrderAmount = productId2OrderAmount.get(productId);
        result.getData().put(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName, salesOrderAmount);

        //已发货数
        Map<String,BigDecimal> productId2HasDeliveredAmount = deliveryNoteProductManager.getProductId2HasDeliveredAmountMap(user, salesOrderId, true);
        BigDecimal hasDeliveredAmount = productId2HasDeliveredAmount.get(productId);
        result.getData().put(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName, hasDeliveredAmount);

        log.debug("productId[{}] salesOrderAmount[{}] hasDeliveredAmount[{}]", productId, salesOrderAmount, hasDeliveredAmount);
    }

    private void filterEditButton(Result result) {
        ILayout layout = new Layout(result.getLayout());
        List<IButton> buttons = LayoutExt.of(layout).getButtons()
                .stream()
                .filter(iButton -> !Objects.equals(SystemConstants.ActionCode.Edit.getActionCode(), iButton.getAction()))
                .collect(Collectors.toList());
        LayoutExt.of(layout).setButtons(buttons);
    }
}