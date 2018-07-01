package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.constants.SystemConstants;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteProductManager;
import com.facishare.crm.deliverynote.predefine.manager.SalesOrderManager;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class DeliveryNoteProductRelatedListController extends StandardRelatedListController {
    private DeliveryNoteProductManager deliveryNoteProductManager;
    private SalesOrderManager salesOrderManager;

    @Autowired
    protected ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) SpringUtil.getContext().getBean("taskExecutor");

    @Override
    protected void before(Arg arg) {
        super.before(arg);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        super.after(arg, result);
        deliveryNoteProductManager = SpringUtil.getContext().getBean(DeliveryNoteProductManager.class);
        salesOrderManager = SpringUtil.getContext().getBean(SalesOrderManager.class);

        // 去掉新建按钮
        if (DeliveryNoteProductObjConstants.API_NAME.equals(arg.getObjectApiName())) {
            result.getListLayouts().forEach(layoutDocument -> {
                List<IButton> buttons = layoutDocument.toLayout().getButtons();
                buttons = buttons.stream()
                        .filter(button -> !SystemConstants.ActionCode.Add.getActionCode().equals(button.getAction()))
                        .collect(Collectors.toList());
                layoutDocument.toLayout().setButtons(buttons);
            });
        }


        // 填充订单产品数及已发货数
        if (DeliveryNoteProductObjConstants.API_NAME.equals(arg.getObjectApiName())) {
            User user = this.getControllerContext().getUser();

            List<String> salesOrderIds = result.getDataList().stream()
                    .map(objectDataDocument -> (String) objectDataDocument.get(DeliveryNoteProductObjConstants.Field.SalesOrderId.apiName))
                    .distinct().collect(Collectors.toList());
            Map<String, Map<String, BigDecimal>> salesOrderId2ProductAmountMap = getSalesOrderId2ProductAmountMap(user, salesOrderIds);
            log.debug("salesOrderId2ProductAmountMap [{}]", salesOrderId2ProductAmountMap);

            Map<String, Map<String, BigDecimal>> salesOrderId2ProductHasDeliveredAmountMap = getSalesOrderId2ProductHasDeliveredAmountMap(user, salesOrderIds);
            log.debug("salesOrderId2ProductHasDeliveredAmountMap [{}]", salesOrderId2ProductHasDeliveredAmountMap);

            List<ObjectDataDocument> newDataList = new ArrayList<>();
            result.getDataList().forEach(objectDataDocument -> {
                IObjectData objectData = objectDataDocument.toObjectData();
                String productId = objectData.get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class);

                // 订单产品数量
                String salesOrderId = objectData.get(DeliveryNoteProductObjConstants.Field.SalesOrderId.apiName, String.class);
                BigDecimal orderProductAmount = null;
                if (salesOrderId2ProductAmountMap.containsKey(salesOrderId)) {
                    orderProductAmount = salesOrderId2ProductAmountMap.get(salesOrderId).get(productId);
                }
                objectData.set(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName, orderProductAmount);
                // 订单产品已发货数
                BigDecimal hasDeliveredNum = BigDecimal.ZERO;
                if (salesOrderId2ProductHasDeliveredAmountMap.containsKey(salesOrderId)) {
                    hasDeliveredNum = salesOrderId2ProductHasDeliveredAmountMap.get(salesOrderId).get(productId);
                    if (Objects.isNull(hasDeliveredNum)) {
                        hasDeliveredNum = BigDecimal.ZERO;
                    }
                }
                objectData.set(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName, hasDeliveredNum);
                log.debug("salesOrderId[{}] productId[{}] orderProductAmount[{}] hasDeliveredNum[{}]", salesOrderId, productId,  orderProductAmount, hasDeliveredNum);

                newDataList.add(ObjectDataDocument.of(objectData));
            });

            result.setDataList(newDataList);
        }
        return result;
    }

    private Map<String, Map<String, BigDecimal>> getSalesOrderId2ProductAmountMap(User user, List<String> salesOrderIds) {
        if (CollectionUtils.isEmpty(salesOrderIds)) {
            return new HashMap<>(0);
        }
        Map<String, Map<String, BigDecimal>> result = Maps.newHashMap();
        CompletionService<GetOrderProductAmountTask.TaskResult> completionService = new ExecutorCompletionService<>(executor);
        salesOrderIds.forEach(salesOrderId -> completionService.submit(new GetOrderProductAmountTask(user, salesOrderId)));
        salesOrderIds.forEach(salesOrderId -> {
            try {

                Future<GetOrderProductAmountTask.TaskResult> future = completionService.take();
                try {
                    GetOrderProductAmountTask.TaskResult taskResult = future.get();
                    result.put(taskResult.getSalesOrderId(), taskResult.getProductId2AmountMap());
                } catch (ExecutionException e) {
                    throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SYSTEM_ERROR);
                }
            } catch (InterruptedException ignored) {}
        });
        return result;
    }

    @AllArgsConstructor
    @Getter
    public class GetOrderProductAmountTask implements Callable<GetOrderProductAmountTask.TaskResult> {
        private User user;
        private String salesOrderId;

        @Override
        public TaskResult call() throws Exception {
            TaskResult result = new TaskResult();
            result.setSalesOrderId(salesOrderId);
            result.setProductId2AmountMap(salesOrderManager.getOrderProductAmountMap(user, salesOrderId));
            return result;
        }

        @Data
        class TaskResult {
            private String salesOrderId;
            private Map<String, BigDecimal> productId2AmountMap;
        }
    }

    private Map<String, Map<String, BigDecimal>> getSalesOrderId2ProductHasDeliveredAmountMap(User user, List<String> salesOrderIds) {
        if (CollectionUtils.isEmpty(salesOrderIds)) {
            return new HashMap<>(0);
        }

        // todo 这里后续可考虑不用并发查询逻辑，直接一次将所有订单的发货单产品查询出来再分组计算就可以了 added by liqiulin
        Map<String, Map<String, BigDecimal>> result = Maps.newHashMap();
        CompletionService<GetDeliveredProductAmountTask.TaskResult> completionService = new ExecutorCompletionService<>(executor);
        salesOrderIds.forEach(salesOrderId -> completionService.submit(new GetDeliveredProductAmountTask(user, salesOrderId)));
        salesOrderIds.forEach(salesOrderId -> {
            try {
                Future<GetDeliveredProductAmountTask.TaskResult> future = completionService.take();
                try {
                    GetDeliveredProductAmountTask.TaskResult taskResult = future.get();
                    result.put(taskResult.getSalesOrderId(), taskResult.getProductId2AmountMap());
                } catch (ExecutionException e) {
                    log.error("GetDeliveredProductAmountTask execute error. salesOrderId[{}]", salesOrderId, e);
                    throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SYSTEM_ERROR);
                }
            } catch (InterruptedException ignored) {}
        });
        return result;
    }

    @AllArgsConstructor
    @Getter
    public class GetDeliveredProductAmountTask implements Callable<GetDeliveredProductAmountTask.TaskResult> {
        private User user;
        private String salesOrderId;

        @Override
        public TaskResult call() throws Exception {
            TaskResult result = new TaskResult();
            result.setSalesOrderId(salesOrderId);
            result.setProductId2AmountMap(deliveryNoteProductManager.getProductId2HasDeliveredAmountMap(user, salesOrderId, true));
            return result;
        }

        @Data
        class TaskResult {
            private String salesOrderId;
            private Map<String, BigDecimal> productId2AmountMap;
        }
    }




}
