package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.enums.DeliveryNoteSwitchEnum;
import com.facishare.crm.deliverynote.enums.SalesOrderLogisticsStatusEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.deliverynote.predefine.manager.order.OrderManager;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteProductVO;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteVO;
import com.facishare.crm.deliverynote.predefine.util.DeliveryNoteStatusUtil;
import com.facishare.crm.deliverynote.predefine.util.ObjectDataUtil;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundTypeEnum;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteProductVO;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteVO;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.crm.rest.ApprovalInitProxy;
import com.facishare.crm.rest.dto.ApprovalInstanceModel;
import com.facishare.crm.rest.dto.ApprovalStatusEnum;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.rest.dto.UpdateCustomerOrderForDeliveryNoteModel;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.StockCalculateManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.crm.util.ObjectFieldConstantsUtil;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.exception.PermissionError;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.metadata.MetaDataActionService;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.facishare.paas.metadata.api.MultiRecordType.RECORD_TYPE_DEFAULT;

@Service
@Slf4j
public class DeliveryNoteManager extends CommonManager {
    /**
     * todo 查询的数据有可能超出 added by liqiulin
     */
    private static final int MAX_LIMIT_FOR_QUERY_ALL = 1000000;

    @Autowired
    protected ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) SpringUtil.getContext().getBean("taskExecutor");

    @Autowired
    private ConfigManager configManager;
    @Autowired
    private StockManager stockManager;
    @Autowired
    private StockCalculateManager stockCalculateManager;
    @Autowired
    private SalesOrderManager salesOrderManager;
    @Autowired
    private DeliveryNoteProductManager deliveryNoteProductManager;
    @Autowired
    private OrderManager orderManager;
    @Autowired
    private MetaDataActionService metaDataActionService;
    @Autowired
    private ApprovalInitProxy approvalInitProxy;
    @Resource
    private OutboundDeliveryNoteManager outboundDeliveryNoteManager;

    public DeliveryNoteVO getById(User user, String id) {
        return ObjectDataUtil.parseObjectData(getObjectDataById(user, id), DeliveryNoteVO.class);
    }

    public IObjectData getObjectDataById(User user, String id) {
        IObjectData iObjectData = this.serviceFacade.findObjectDataIncludeDeleted(user, id, DeliveryNoteObjConstants.API_NAME);
        if (Objects.isNull(iObjectData)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_NOT_EXISTS);
        }
        return iObjectData;
    }

    public void modifyArg(String tenantId, BaseObjectSaveAction.Arg arg) {
        ObjectDataDocument objectData = arg.getObjectData();
        if (objectData == null) {
            throw new ValidateException("对象不能为空");
        }

        // 业务类型默认设置为预设业务类型，因为OpenAPI调用时RecordType会传空
        if (StringUtils.isEmpty(arg.getObjectData().toObjectData().getRecordType())) {
            arg.getObjectData().put(MultiRecordType.RECORD_TYPE, MultiRecordType.RECORD_TYPE_DEFAULT);
        }

        // OpenAPI接口调用时describeID为空，需要补充此字段
        String objectDescribeId = (String) arg.getObjectData().get(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID);
        if (StringUtils.isEmpty(objectDescribeId)) {
            IObjectDescribe describe = findDescribe(tenantId, DeliveryNoteObjConstants.API_NAME);
            setDescribeId(arg.getObjectData(), describe);

            Map<String, List<ObjectDataDocument>> details = arg.getDetails();
            if (MapUtils.isNotEmpty(details)) {
                details.forEach((describeApiName, value) -> {
                    IObjectDescribe detailDescribe = findDescribe(tenantId, describeApiName);
                    value.forEach(objectDataDocument -> setDescribeId(objectDataDocument, detailDescribe));
                });
            }
        }

        List<ObjectDataDocument> productObjectDocList = arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME);
        if (CollectionUtils.isEmpty(productObjectDocList)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_NO_PRODUCT);
        }
        productObjectDocList.forEach(product -> {
            // 不保存发货单产品'已发货数'和'订单产品数量'
            product.put(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName, null);
            product.put(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName, null);

            // 业务类型默认设置为预设业务类型，因为OpenAPI调用时RecordType会传空
            if (StringUtils.isEmpty(product.toObjectData().getRecordType())) {
                product.put(MultiRecordType.RECORD_TYPE, MultiRecordType.RECORD_TYPE_DEFAULT);
            }
        });

        arg.setObjectData(objectData);
    }

    private void setDescribeId(ObjectDataDocument objectDataDocument, IObjectDescribe objectDescribe) {
        objectDataDocument.put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID, objectDescribe.getId());
        objectDataDocument.put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_API_NAME, objectDescribe.getApiName());
    }

    private IObjectDescribe findDescribe(String tenantId, String describeApiName) {
        IObjectDescribe describe = serviceFacade.findObject(tenantId, describeApiName);
        if (describe == null) {
            throw new ValidateException("查询不到对象[" + describeApiName + "]");
        }
        return describe;
    }

    public void checkForAdd(User user, IObjectData mainObjectData, List<IObjectData> detailProductObjectDataList) {
        this.checkIsDeliveryEnabled(user.getTenantId());

        String salesOrderId = (String) mainObjectData.get(DeliveryNoteObjConstants.Field.SalesOrderId.getApiName());
        if (Objects.isNull(salesOrderId)) {
            throw new ValidateException("必填字段[销售订单编号]，[销售订单编号]未填写，不可进行当前操作");
        }
        SalesOrderModel.SalesOrderVo salesOrderVo = salesOrderManager.getById(user, salesOrderId);

        // 发货仓库检验
        String orderWareHouseId = salesOrderVo.getWarehouseId();
        String deliveryWarehouseId = (String) mainObjectData.get(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName);
        if (StringUtils.isEmpty(orderWareHouseId) && StringUtils.isNotEmpty(deliveryWarehouseId)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.WHEN_ORDER_WAREHOUSE_EMPTY_THEN_DELIVERY_WAREHOUSE_CAN_NOT_HAVE_VALUE);
        }
        if (StringUtils.isNotEmpty(orderWareHouseId) && StringUtils.isEmpty(deliveryWarehouseId)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.WHEN_ORDER_WAREHOUSE_NOT_EMPTY_THEN_DELIVERY_WAREHOUSE_CAN_NOT_BE_EMPTY);
        }

        this.checkSalesOrderStatus(user, salesOrderVo);
        this.checkDeliveryProduct(user, salesOrderId, detailProductObjectDataList, StringUtils.isNotEmpty(deliveryWarehouseId));

        boolean isStockEnable = stockManager.isStockEnable(user.getTenantId());
        if (isStockEnable && StringUtils.isNotEmpty(deliveryWarehouseId)) {
            this.checkWarehouseRealStock(user, deliveryWarehouseId, detailProductObjectDataList);
            checkOutboundDeliveryNoteCreateRight(user);
        }
    }


    /**
     *  校验是否有出库单创建权限（因为创建发货单时会同步创建出库单）
     */
    public void checkOutboundDeliveryNoteCreateRight(User user) {
        try {
            this.serviceFacade.doFunPrivilegeCheck(user, OutboundDeliveryNoteConstants.API_NAME, Lists.newArrayList(ObjectAction.CREATE.getActionCode()));
        } catch (PermissionError error) {
            throw new PermissionError("新建发货单需要同时拥有出库单的新建权限，请联系CRM管理员添加。");
        }
    }

    /**
     * 执行创建发货单状态为已发货时的逻辑
     */
    public void doCreateDeliveryNoteBecomeHasDelivered(User user, IObjectData deliveryNoteObjectData, List<IObjectData> productObjectDataList, StockOperateInfo info) {
        String salesOrderId = deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.SalesOrderId.apiName, String.class);

        boolean isStockEnable = stockManager.isStockEnable(user.getTenantId());
        String deliveryWarehouseId = deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName, String.class);
        if (isStockEnable && StringUtils.isNotEmpty(deliveryWarehouseId)) {
            // 创建出库单
            createOutboundDeliveryNote(deliveryNoteObjectData, productObjectDataList, deliveryWarehouseId);

            // 扣库存
            List<DeliveryNoteProductVO> deliveryNoteProductVOList = ObjectDataUtil.parseObjectData(productObjectDataList, DeliveryNoteProductVO.class);
            Map<String, BigDecimal> productId2DeliverAmountMap = deliveryNoteProductManager.getProductId2DeliveryNum(deliveryNoteProductVOList);
            stockCalculateManager.deliveryNoteToNormal(user, deliveryWarehouseId, salesOrderId, productId2DeliverAmountMap, info);
        }

        // 记录订单修改日志
        String deliveryNoteName = deliveryNoteObjectData.getName();
        String logText = String.format("创建了发货单，编号：%s", deliveryNoteName);
        SalesOrderModel.SalesOrderVo currentSalesOrder = salesOrderManager.getById(user, salesOrderId);
        Integer oldLogisticStatus = currentSalesOrder.getLogisticsStatus();
        boolean isAllDelivered = isAllDelivered(user, salesOrderId, false);
        SalesOrderLogisticsStatusEnum newLogisticsStatus = isAllDelivered ? SalesOrderLogisticsStatusEnum.Consigned : SalesOrderLogisticsStatusEnum.PartialDelivery;
        if (!Objects.equals(oldLogisticStatus, newLogisticsStatus.getStatus())) {
            logText = logText + String.format("，发货状态变更为%s", newLogisticsStatus.getMessage());
        }
        salesOrderManager.saveModifyLog(user, salesOrderId, logText);

        // 更新订单信息
        updateSalesOrderForStatusChange(user, currentSalesOrder, newLogisticsStatus);



        // 给下游发通知
        executor.execute(() -> {
            String tenantId = user.getTenantId();
            try {
                deliveredNotify(tenantId, deliveryNoteName, salesOrderId);
            } catch (Exception e) {
                log.error("deliveredNotify error.tenantId[{}], deliveryNoteName[{}], salesOrderId[{}]", tenantId, deliveryNoteName, salesOrderId, e);
            }
        });
    }

    /**
     * 发货单状态变更时更新订单
     */
    public void updateSalesOrderForStatusChange(User user, SalesOrderModel.SalesOrderVo currentSalesOrder, SalesOrderLogisticsStatusEnum newLogisticsStatus) {
        String salesOrderId = currentSalesOrder.getCustomerTradeId();
        UpdateCustomerOrderForDeliveryNoteModel.Arg arg = new UpdateCustomerOrderForDeliveryNoteModel.Arg();
        arg.setCustomerTradeId(salesOrderId);
        arg.setLogisticsStatus(newLogisticsStatus.getStatus());
        arg.setConfirmDeliveryTime(currentSalesOrder.getConfirmDeliveryTime());
        arg.setConfirmReceiveTime(currentSalesOrder.getConfirmReceiveTime());
        if (Objects.equals(SalesOrderLogisticsStatusEnum.ToBeShipped, newLogisticsStatus)) {
            // 默认发货时间为2000-01-01 00:00:00.000
            arg.setConfirmDeliveryTime(946656000000L);
        } else if (Objects.equals(SalesOrderLogisticsStatusEnum.PartialDelivery, newLogisticsStatus)
                || Objects.equals(SalesOrderLogisticsStatusEnum.Consigned, newLogisticsStatus)) {
            arg.setConfirmDeliveryTime(System.currentTimeMillis());
        } else if (Objects.equals(SalesOrderLogisticsStatusEnum.PartialReceipt, newLogisticsStatus)
                || Objects.equals(SalesOrderLogisticsStatusEnum.Received, newLogisticsStatus)) {
            arg.setConfirmReceiveTime(System.currentTimeMillis());
        }

        BigDecimal deliveredAmountSum = BigDecimal.ZERO;
        List<DeliveryNoteProductVO> hasDeliveredDeliveryNoteProductList = deliveryNoteProductManager.getHasDeliveredProducts(user, salesOrderId, false);
        for (DeliveryNoteProductVO hasDeliveredDeliveryNoteProduct : hasDeliveredDeliveryNoteProductList) {
            deliveredAmountSum = deliveredAmountSum.add(hasDeliveredDeliveryNoteProduct.getDeliveryMoney());
        }
        arg.setDeliveredAmountSum(deliveredAmountSum);

        List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts = salesOrderManager.getSalesOrderProducts(user, salesOrderId);
        Map<String, BigDecimal> productId2HasDeliveredAmount = deliveryNoteProductManager.getProductId2DeliveryNum(hasDeliveredDeliveryNoteProductList);
        Map<String, SalesOrderManager.OrderProduct> orderProductMap = salesOrderManager.getOrderProduct(user, salesOrderId);
        List<UpdateCustomerOrderForDeliveryNoteModel.Arg.Product> argProducts = this.getUpdateOrderArgProduct(salesOrderProducts, productId2HasDeliveredAmount, orderProductMap);
        arg.setUpdateDetailList(argProducts);
        boolean updateResult = salesOrderManager.updateCustomerOrderForDeliveryNote(user, arg);
        if (!updateResult) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.UPDATE_CUSTOMER_ORDER_FOR_DELIVERY_NOTE_ERROR);
        }
    }

    private List<UpdateCustomerOrderForDeliveryNoteModel.Arg.Product> getUpdateOrderArgProduct(List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts,
                                                                                               Map<String, BigDecimal> productId2HasDeliveredAmount,
                                                                                               Map<String, SalesOrderManager.OrderProduct> orderProductMap) {
        List<UpdateCustomerOrderForDeliveryNoteModel.Arg.Product> argProducts = Lists.newArrayList();
        Map<String, BigDecimal> tempProductId2HasCalculateAmount = Maps.newHashMap();
        salesOrderProducts.forEach(salesOrderProductVO -> {
            String tradeProductId = salesOrderProductVO.getTradeProductId();
            String productId = salesOrderProductVO.getProductId();
            BigDecimal tradeProductAmount = salesOrderProductVO.getAmount();
            BigDecimal hasCalculateAmount = tempProductId2HasCalculateAmount.getOrDefault(productId, BigDecimal.ZERO);
            BigDecimal hasDeliveredAmount = productId2HasDeliveredAmount.getOrDefault(productId, BigDecimal.ZERO);
            BigDecimal avgPrice = orderProductMap.get(productId).avgPrice();
            BigDecimal residueDeliveryAmount = hasDeliveredAmount.subtract(hasCalculateAmount);
            BigDecimal orderAllAmount = orderProductMap.get(productId).getAllAmount();
            BigDecimal orderAllSubtotal = orderProductMap.get(productId).getAllSubTotal();

            if (residueDeliveryAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal deliveryCount;
                BigDecimal deliveryAmount;
                if (residueDeliveryAmount.compareTo(tradeProductAmount) >= 0) {
                    deliveryCount = tradeProductAmount;
                } else {
                    deliveryCount = residueDeliveryAmount;
                }

                // 是否全部发货的最后批次产品，则发货金额为产品总发货金额减去已发货金额
                boolean isLastDeliveryTimes = hasCalculateAmount.add(deliveryCount).compareTo(orderAllAmount) >= 0;
                if (isLastDeliveryTimes) {
                    deliveryAmount = orderAllSubtotal.subtract(hasCalculateAmount.multiply(avgPrice).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    deliveryAmount = deliveryCount.multiply(avgPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                }

                UpdateCustomerOrderForDeliveryNoteModel.Arg.Product argProduct = new UpdateCustomerOrderForDeliveryNoteModel.Arg.Product();
                argProduct.setTradeProductID(tradeProductId);
                argProduct.setDeliveryAmount(deliveryAmount);
                argProduct.setDeliveredCount(deliveryCount);
                argProducts.add(argProduct);

                hasCalculateAmount = hasCalculateAmount.add(deliveryCount);
                tempProductId2HasCalculateAmount.put(productId, hasCalculateAmount);
            } else {
                UpdateCustomerOrderForDeliveryNoteModel.Arg.Product argProduct = new UpdateCustomerOrderForDeliveryNoteModel.Arg.Product();
                argProduct.setTradeProductID(tradeProductId);
                argProduct.setDeliveryAmount(BigDecimal.ZERO);
                argProduct.setDeliveredCount((BigDecimal.ZERO));
                argProducts.add(argProduct);
            }
        });

        return argProducts;
    }

    private User getCreator(IObjectData objectData) {
        return new User(objectData.getTenantId(), objectData.getCreatedBy());
    }

    private void createOutboundDeliveryNote(IObjectData deliveryNoteObjectData, List<IObjectData> productObjectDataList, String deliveryWarehouseId) {
        OutboundDeliveryNoteVO outboundDeliveryNoteVO = OutboundDeliveryNoteVO.builder()
                .warehouseId(deliveryWarehouseId)
                .outboundDate(System.currentTimeMillis())
                .outboundType(OutboundTypeEnum.SALES_OUTBOUND.value)
                .deliveryNoteId(deliveryNoteObjectData.getId())
                .build();

        List<OutboundDeliveryNoteProductVO> outboundDeliveryNoteProductVOs = Lists.newArrayList();
        productObjectDataList.forEach(deliveryProduct -> {
            OutboundDeliveryNoteProductVO outboundDeliveryNoteProductVO = OutboundDeliveryNoteProductVO.builder()
                    .productId(deliveryProduct.get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class))
                    .stockId(deliveryProduct.get(DeliveryNoteProductObjConstants.Field.StockId.apiName, String.class))
                    .outboundAmount(deliveryProduct.get(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, BigDecimal.class).toString())
                    .build();
            outboundDeliveryNoteProductVOs.add(outboundDeliveryNoteProductVO);
        });

        User creator = getCreator(deliveryNoteObjectData);
        outboundDeliveryNoteManager.create(creator, outboundDeliveryNoteVO, outboundDeliveryNoteProductVOs, OutboundTypeEnum.SALES_OUTBOUND.value);
    }

    private void deliveredNotify(String tenantId, String deliveryNoteName, String salesOrderId) {
        OrderManager.OrderNotifyArg arg = OrderManager.OrderNotifyArg.builder()
                .orderId(salesOrderId)
                .tenantId(tenantId)
                .type("3")
                .typeobjectid(deliveryNoteName).build();
        OrderManager.OrderNotifyResult result = orderManager.orderNotify(arg);
        log.debug("orderManager.orderNotify arg[{}], result[{}]", arg, result);
        if (!result.isValue()) {
            log.error("orderManager.orderNotify failed. arg[{}], result[{}]", arg, result);
        }
    }

    /**
     * 判断订单是否全部收货
     */
    public boolean isAllReceived(User user, String salesOrderId) {
        boolean isAllDelivered = isAllDelivered(user, salesOrderId, true);
        if (!isAllDelivered) {
            return false;
        }

        List<String> deliveredStatus = Lists.newArrayList();
        deliveredStatus.add(DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus());
        deliveredStatus.add(DeliveryNoteObjStatusEnum.RECEIVED.getStatus());
        deliveredStatus.add(DeliveryNoteObjStatusEnum.CHANGING.getStatus());
        deliveredStatus.add(DeliveryNoteObjStatusEnum.IN_APPROVAL.getStatus());
        List<DeliveryNoteVO> deliveryNoteVOList = this.queryDeliveryNoteBySalesOrderId(user, salesOrderId, deliveredStatus);
        return deliveryNoteVOList.stream()
                .allMatch(this::isReceived);
    }

    /**
     * 判断发货单是否处理已收货
     */
    private boolean isReceived(DeliveryNoteVO deliveryNoteVO) {
        boolean statusIsReceived = DeliveryNoteObjStatusEnum.RECEIVED.getStatus().equals(deliveryNoteVO.getStatus());

        // 这里判断收货日期是因为已收货的发货单可编辑，如果触发编辑审批，则状态改为了变更中，这时无法通过状态来判断是否已收货，
        // 因为收货操作时必填收货日期，所以判断是否有收货日期来判断是否收货
        boolean hasReceiveDate = Objects.nonNull(deliveryNoteVO.getReceiveDate());

        if (statusIsReceived || hasReceiveDate) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断订单是否全部发货
     */
    public boolean isAllDelivered(User user, String salesOrderId, boolean includeInApprovalStatus) {
        Map<String, BigDecimal> hasDeliveredProductAmountMap = deliveryNoteProductManager.getProductId2HasDeliveredAmountMap(user, salesOrderId, includeInApprovalStatus);
        Map<String, BigDecimal> productId2OrderAmountMap = salesOrderManager.getOrderProductAmountMap(user, salesOrderId);
        for (Map.Entry<String, BigDecimal> productId2OrderAmount : productId2OrderAmountMap.entrySet()) {
            String orderProductId = productId2OrderAmount.getKey();
            BigDecimal orderAmount = productId2OrderAmount.getValue();
            if (orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            if (!hasDeliveredProductAmountMap.containsKey(orderProductId)) {
                return false;
            } else {
                if (orderAmount.compareTo(hasDeliveredProductAmountMap.get(orderProductId)) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 校验发货单产品
     */
    public void checkDeliveryProduct(User user, String salesOrderId, List<IObjectData> detailProductObjectDataList, boolean hasDeliveryWarehouseId) {
        if (org.springframework.util.CollectionUtils.isEmpty(detailProductObjectDataList)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_NO_PRODUCT);
        }

        Set<String> productIds = Sets.newHashSet();
        detailProductObjectDataList.forEach(productObjectData -> {
            // 产品ID不能为空
            String productId = productObjectData.get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class);
            if (StringUtils.isBlank(productId)) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_FIELD_PRODUCT_ID_CAN_NOT_BE_BLANK);
            }

            // 重复校验
            if (productIds.contains(productId)) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_REPEATED);
            } else {
                productIds.add(productId);
            }

            // 库存ID不能为空
            if (hasDeliveryWarehouseId) {
                String stockId = productObjectData.get(DeliveryNoteProductObjConstants.Field.StockId.apiName, String.class);
                if (StringUtils.isBlank(stockId)) {
                    throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_FIELD_STOCK_ID_CAN_NOT_BE_BLANK);
                }
            }

            // 发货单产品必然为预设业务类型
            if (!Objects.equals(productObjectData.getRecordType(), RECORD_TYPE_DEFAULT)) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_RECORD_TYPE_NOT_DEFAULT);
            }
        });

        List<DeliveryNoteProductVO> deliveryNoteProductVOList = ObjectDataUtil.parseObjectData(detailProductObjectDataList, DeliveryNoteProductVO.class);

        // 校验填写的发货数是否大于0
        deliveryNoteProductVOList.forEach(deliveryNoteProduct -> {
            BigDecimal deliveryNum = deliveryNoteProduct.getDeliveryNum();
            if (Objects.isNull(deliveryNum) || deliveryNum.compareTo(BigDecimal.ZERO) <= 0) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_NEED_GT_ZERO);
            }
        });

        Map<String, BigDecimal> productId2HasDeliveredAmount = deliveryNoteProductManager.getProductId2HasDeliveredAmountMap(user, salesOrderId, true);

        // 校验发货产品是否在订单范围、 校验产品发货数是否大于订单数
        Map<String, BigDecimal> productId2OrderAmount = salesOrderManager.getCanDeliverProductAmountMap(user, salesOrderId);
        Map<String, BigDecimal> productId2DeliverAmountMap = deliveryNoteProductManager.getProductId2DeliveryNum(deliveryNoteProductVOList);
        for (Map.Entry<String, BigDecimal> entry : productId2DeliverAmountMap.entrySet()) {
            String detailProductId = entry.getKey();
            BigDecimal detailProductAmount = entry.getValue();
            BigDecimal orderAmount = productId2OrderAmount.get(detailProductId);
            if (Objects.isNull(orderAmount)) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_NOT_IN_SALES_ORDER);
            }

            BigDecimal hasDeliveredAmount = productId2HasDeliveredAmount.getOrDefault(detailProductId, BigDecimal.ZERO);
            if (hasDeliveredAmount.compareTo(orderAmount) == 0) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_HAS_ALL_DELIVERED, "订单已发货，请查看发货记录！");
            }
            if (hasDeliveredAmount.add(detailProductAmount).compareTo(orderAmount) > 0) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_GT_ORDER_AMOUNT);
            }
        }
    }

    /**
     * 校验产品实际库存
     */
    public void checkWarehouseRealStock(User user, String warehouseId, List<IObjectData> detailProductObjectDataList) {
        List<DeliveryNoteProductVO> deliveryNoteProductVOList = ObjectDataUtil.parseObjectData(detailProductObjectDataList, DeliveryNoteProductVO.class);
        Map<String, DeliveryNoteProductVO> productId2ProductVOMap = deliveryNoteProductVOList.stream().collect(Collectors.toMap(DeliveryNoteProductVO::getProductId, (p) -> p ));
        Map<String, BigDecimal> productId2DeliverAmountMap = deliveryNoteProductManager.getProductId2DeliveryNum(deliveryNoteProductVOList);

        List<String> productIds = new ArrayList<>(productId2DeliverAmountMap.keySet());
        Map<String, StockType.StockVO> productId2StockMap = getProductStock(user, warehouseId, productIds);
        for (Map.Entry<String, BigDecimal> entry : productId2DeliverAmountMap.entrySet()) {
            String detailProductId = entry.getKey();
            BigDecimal detailProductAmount = entry.getValue();
            StockType.StockVO stockVO = productId2StockMap.get(detailProductId);
            if (Objects.isNull(stockVO)) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_NOT_IN_STOCK);
            }
            // 验证提交的库存ID与产品实际库存ID是一致的
            String productDetailStockId = productId2ProductVOMap.get(detailProductId).getStockId();
            if (!Objects.equals(productDetailStockId, stockVO.getId())) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_STOCK_ID_INVALID);
            }
            // 验证实际库存
            BigDecimal readStock = stockVO.getRealStock();
            if (detailProductAmount.compareTo(readStock) > 0) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_PRODUCT_GT_REAL_STOCK);
            }
        }
    }

    /**
     * 获取产品库存
     */
    public Map<String, StockType.StockVO> getProductStock(User user, String warehouseId, List<String> productIds) {
        List<IObjectData> stockObjectDataList = stockManager.queryByWarehouseIdAndProductIds(user, warehouseId, productIds);
        List<StockType.StockVO> stockVOList = ObjectDataUtil.parseObjectData(stockObjectDataList, StockType.StockVO.class);
        return stockVOList.stream().collect(Collectors.toMap(StockType.StockVO::getProductId, Function.identity()));
    }

    /**
     * 作废发货单
     *
     * "已作废"的数据，会修改status为"已作废"
     */
    public void invalidDeliveryNote(User user, IObjectData invalidDeliveryNoteObjData, String oldStatus, StockOperateInfo info) {
        log.info("invalidDeliveryNote, user:{}, invalidDeliveryNoteObjData:{}, oldStatus:{}", user, invalidDeliveryNoteObjData, oldStatus);
        DeliveryNoteVO invalidDeliveryNoteVO = ObjectDataUtil.parseObjectData(invalidDeliveryNoteObjData, DeliveryNoteVO.class);
        String salesOrderId = invalidDeliveryNoteVO.getSalesOrderId();

        //1、修改状态为"已作废"
        updateStatus(user,invalidDeliveryNoteObjData, DeliveryNoteObjStatusEnum.INVALID, true);

        //2、只能作废"未发货"和"已确认"的，作废"未发货"不需要改库存和订单的信息
        if (!Objects.equals(oldStatus, DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus()) && !Objects.equals(oldStatus, DeliveryNoteObjStatusEnum.CHANGING.getStatus())) {
            return;
        }

        //3、获取本次做作废的"发货单产品"数据
//      IObjectDescribe deliveryNoteProductDescribe = serviceFacade.findObject(user.getTenantId(), DeliveryNoteProductObjConstants.API_NAME);
//      List<IObjectData> deliveryNoteProductObjDatas = this.serviceFacade.findDetailObjectDataList(deliveryNoteProductDescribe, deliveryNoteObjData, user);  //作废了的数据，不能通过这个接口获取
        List<DeliveryNoteProductVO> invalidDeliveryNoteProductVOList = deliveryNoteProductManager.getDeliveryNoteProductVO(user, invalidDeliveryNoteVO.getId());

        //4、如果开启了库存，修改库存信息
        boolean isStockEnable = stockManager.isStockEnable(user.getTenantId());
        log.info("invalidDeliveryNote, isStockEnable:{}", isStockEnable);
        String warehouseId = invalidDeliveryNoteVO.getDeliveryWarehouseId();
        if (isStockEnable && StringUtils.isNotEmpty(warehouseId)) {
            //作废对应出库单
            outboundDeliveryNoteManager.invalid(user, invalidDeliveryNoteVO.getId(), OutboundTypeEnum.SALES_OUTBOUND.value);

            //调整库存
            Map<String, BigDecimal> productId2DeliveryNum = deliveryNoteProductManager.getProductId2DeliveryNum(invalidDeliveryNoteProductVOList);
            stockCalculateManager.deliveryNoteToInvalid(user, warehouseId, salesOrderId, productId2DeliveryNum, info);
            log.info("stockCalculateManager.deliveryNoteToInvalid user:{}, warehouseId:{}, salesOrderId:{}, productId2DeliveryNum:{}", user, warehouseId, salesOrderId, productId2DeliveryNum);
        }

        //5、根据订单的所有发货单的状态，修改订单的发货状态
        //查询订单的所有发货单（这种方式"已作废"的没有查回来）
        List<IObjectData> deliveryNoteObjectDatas = queryDeliveryNoteObjectDataBySalesOrderId(user, salesOrderId, null);
        SalesOrderLogisticsStatusEnum newLogisticsStatus = SalesOrderLogisticsStatusEnum.ToBeShipped;
        if (!org.springframework.util.CollectionUtils.isEmpty(deliveryNoteObjectDatas)) {
            List<String> deliveryNoteIds = deliveryNoteObjectDatas.stream().map(d -> d.get(DeliveryNoteObjConstants.Field.Id.apiName, String.class)).collect(Collectors.toList());
            //查询订单的所有发货单产品
            List<DeliveryNoteProductVO> deliveryNoteProductVOs = deliveryNoteProductManager.queryDeliveryNoteProductVos(user, deliveryNoteIds);
            BigDecimal allNeedDeliveryNum = salesOrderManager.getAllNeedDeliveryNum(user, salesOrderId);

            DeliveryNoteStatusUtil util = new DeliveryNoteStatusUtil();
            newLogisticsStatus = util.getDeliveryStatus(deliveryNoteObjectDatas, deliveryNoteProductVOs, allNeedDeliveryNum);
        }
        log.info("invalidDeliveryNote, setLogisticsStatus salesOrderId:{}, newLogisticsStatus:{}", salesOrderId, newLogisticsStatus);

        // 记录订单修改日志
        String deliveryNoteName = invalidDeliveryNoteObjData.getName();
        String logText = String.format("关联的发货单%s已作废", deliveryNoteName);
        SalesOrderModel.SalesOrderVo currentSalesOrder = salesOrderManager.getById(user, salesOrderId);
        Integer oldLogisticStatus = currentSalesOrder.getLogisticsStatus();
        if (!Objects.equals(oldLogisticStatus, newLogisticsStatus.getStatus())) {
            logText = logText + String.format("，发货状态变更为%s", newLogisticsStatus.getMessage());
        }
        salesOrderManager.saveModifyLog(user, salesOrderId, logText);

        // 更新订单信息
        updateSalesOrderForStatusChange(user, currentSalesOrder, newLogisticsStatus);
    }

    /**
     * 获取deliveryNoteIds对应的发货单（可以查'已作废'的发货单的数据）
     */
    public List<IObjectData> getDeliveryNotes(User user, List<String> deliveryNoteIds) {
        QueryResult<IObjectData> allDeliveryNoteResult = queryAllDataByField(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Field.Id.getApiName(),
                deliveryNoteIds, 0, MAX_LIMIT_FOR_QUERY_ALL);
        return allDeliveryNoteResult.getData();
    }

    /**
     * 根据tenantId获取发货单（包括'已作废'的数据）
     */
    public List<IObjectData> getObjectDatasByTenantId(User user, String tenantId, int offset, int limit, List<OrderBy> orderBys) {
        QueryResult<IObjectData>  queryResult = queryAllDataByField(user, DeliveryNoteObjConstants.API_NAME, SystemConstants.Field.TennantID.apiName,
                Lists.newArrayList(tenantId), offset, limit, orderBys);
        return queryResult.getData();
    }

    /**
     * 获取某个订单id的所有发货单（包括'已作废'的数据）
     */
    public List<IObjectData> getAllObjectDatasBySaleOrderId(User user, String saleOrderId) {
        QueryResult<IObjectData>  queryResult = queryAllDataByField(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Field.SalesOrderId.apiName,
                Lists.newArrayList(saleOrderId), 0, MAX_LIMIT_FOR_QUERY_ALL);
        return queryResult.getData();
    }

    /**
     * "发货单"开关是否开启
     */
    public boolean isDeliveryNoteEnable(String tenantId) {
        DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(tenantId);
        return deliveryNoteSwitchStatus.getStatus() == DeliveryNoteSwitchEnum.OPENED.getStatus();
    }

    /**
     * "发货单"开关是否开启
     */
    public void checkIsDeliveryEnabled(String tenantId) {
        if (!isDeliveryNoteEnable(tenantId)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_NOT_ENABLE);
        }
    }

    /**
     * 获取deliveryNoteId的发货单（可以查询已作废的发货单）
     */
    public IObjectData getDeliveryNoteObjectData(User user, String deliveryNoteId) {
        QueryResult<IObjectData> queryAllDataByFieldResult = queryAllDataByField(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Field.Id.getApiName(), Lists.newArrayList(deliveryNoteId), 0, 1);

        List<IObjectData> invalidDeliveryNoteObjectDatas = queryAllDataByFieldResult.getData();
        if (org.springframework.util.CollectionUtils.isEmpty(invalidDeliveryNoteObjectDatas)) {
            return null;
        }
        return invalidDeliveryNoteObjectDatas.get(0);
    }

    /**
     * 作废订单，需要判断所有的发货单是否'已作废'
     */
    public void checkAllDeliveryNoteIsInvalid(User user, List<String> salesOrderIds) {
        List<DeliveryNoteVO> deliveryNoteVOs = queryDeliveryNoteBySalesOrderIds(user, salesOrderIds, null);

        //排除掉'已作废'状态的发货单
        List<DeliveryNoteVO> notInvalidDeliveryNoteVOs = deliveryNoteVOs.stream().filter(d -> !Objects.equals(d.getStatus(), DeliveryNoteObjStatusEnum.INVALID.getStatus())).collect(Collectors.toList());

        //根据salesOrderId分组
        Map<String, List<DeliveryNoteVO>> saleOrderId2DeliveryNoteVOs = notInvalidDeliveryNoteVOs.stream().collect(Collectors.groupingBy(DeliveryNoteVO::getSalesOrderId));

        //有多少订单有非'已作废'状态的发货单
        if (saleOrderId2DeliveryNoteVOs.size() > 0) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.NOT_ALL_DELIVERY_NOT_IS_INVALID, "销售订单作废失败，原因：有" + saleOrderId2DeliveryNoteVOs.size() + "个订单下存在未作废的发货单，请将对应的发货单全部作废后再次尝试。");
        }
    }

    /**
     * 查询订单所有状态在statusList里面的发货单(无法查到已作废的）
     */
    public List<DeliveryNoteVO> queryDeliveryNoteBySalesOrderId(User user, String salesOrderId, List<String> statusList) {
        List<IObjectData> deliveryNotes = queryDeliveryNoteObjectDataBySalesOrderId(user, salesOrderId, statusList);
        return ObjectDataUtil.parseObjectData(deliveryNotes, DeliveryNoteVO.class);
    }

    /**
     * 查询订单所有状态在statusList里面的发货单
     */
    public List<DeliveryNoteVO> queryDeliveryNoteBySalesOrderIds(User user, List<String> salesOrderIds, List<String> statusList) {
        List<IObjectData> deliveryNotes = queryDeliveryNoteObjectDataBySalesOrderIds(user, salesOrderIds, statusList);
        return ObjectDataUtil.parseObjectData(deliveryNotes, DeliveryNoteVO.class);
    }

    /**
     * 查询订单所有状态在statusList里面的发货单【lifeStatus=invalid的查不到】
     *
     * @param user
     * @param salesOrderId
     * @param statusList   null或空则不起作用
     * @return
     */
    public List<IObjectData> queryDeliveryNoteObjectDataBySalesOrderId(User user, String salesOrderId, List<String> statusList) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salesOrderId);
        if (CollectionUtils.isNotEmpty(statusList)) {
            SearchUtil.fillFilterIn(filters, DeliveryNoteObjConstants.Field.Status.apiName, statusList);
        }
        return searchQuery(user, DeliveryNoteObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    /**
     * 查询订单所有状态在statusList里面的发货单【lifeStatus=invalid的查不到】
     *
     * @param user
     * @param salesOrderIds 必填
     * @param statusList    null或空则不起作用
     * @return
     */
    public List<IObjectData> queryDeliveryNoteObjectDataBySalesOrderIds(User user, List<String> salesOrderIds, List<String> statusList) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salesOrderIds);
        if (CollectionUtils.isNotEmpty(statusList)) {
            SearchUtil.fillFilterIn(filters, DeliveryNoteObjConstants.Field.Status.apiName, statusList);
        }
        return searchQuery(user, DeliveryNoteObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    /**
     * 修改发货单状态
     *
     * "已作废"的数据来调这个接口，报错：com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException: 数据已作废或已删除
     * "life_status":"ineffective":可以调用
     */
    public IObjectData updateStatus(User user, IObjectData deliveryNoteObjectData, DeliveryNoteObjStatusEnum newStatus) {
        deliveryNoteObjectData.set(DeliveryNoteObjConstants.Field.Status.apiName, newStatus.getStatus());
        Map<String, Object> updateFiledMap = Maps.newHashMap();
        updateFiledMap.put(DeliveryNoteObjConstants.Field.Status.apiName, newStatus.getStatus());
        return this.serviceFacade.updateWithMap(user, deliveryNoteObjectData, updateFiledMap);
    }

    /**
     * 修改发货单状态
     *
     * @param user
     * @param deliveryNoteObjectData
     * @param newStatus
     * @param allowUpdateInvalid 是否可以更新作废数据
     */
    public IObjectData updateStatus(User user, IObjectData deliveryNoteObjectData, DeliveryNoteObjStatusEnum newStatus, boolean allowUpdateInvalid) {
        deliveryNoteObjectData.set(DeliveryNoteObjConstants.Field.Status.apiName, newStatus.getStatus());
        IObjectData newObjectData = metaDataActionService.updateObjectData(user, deliveryNoteObjectData, allowUpdateInvalid);
        log.debug("metaDataActionService.updateObjectData, user:{}, data:{}, allowUpdateInvalid:{}, result:{}", user, deliveryNoteObjectData, allowUpdateInvalid, newObjectData);
        return newObjectData;
    }

    public void checkSalesOrderStatus(User user, SalesOrderModel.SalesOrderVo salesOrderVo) {
        Integer salesOrderStatus = salesOrderVo.getStatus();
        if (Objects.equals(SalesOrderModel.SalesOrderStatusEnum.INVALID.getCode(), salesOrderStatus)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SALES_ORDER_HAS_INVALID);
        }

        if (!Objects.equals(SalesOrderModel.SalesOrderStatusEnum.HAS_CONFIRMED.getCode(), salesOrderStatus)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SALES_ORDER_STATUS_NOT_NORMAL);
        }

        //校验订单是否处理变更审批中
        checkIsSalesOrderInProgress(user.getTenantId(), salesOrderVo.getCustomerTradeId());
    }

    private void checkIsSalesOrderInProgress(String tenantId, String customerTradeId) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("x-user-id", User.SUPPER_ADMIN_USER_ID);
        headers.put("x-tenant-id", tenantId);
        ApprovalInstanceModel.Arg instanceArg = new ApprovalInstanceModel.Arg();
        instanceArg.setObjectId(customerTradeId);
        ApprovalInstanceModel.Result approvalInstanceResult = approvalInitProxy.approvalInstance(instanceArg, headers);
        log.debug("approvalInitProxy.approvalInstance headers:{},arg:{}, result:{}", headers, instanceArg, approvalInstanceResult);
        if (!approvalInstanceResult.success()) {
            log.error("approvalInitProxy.approvalInstance error. headers:{},arg:{},result:{}", headers, instanceArg, approvalInstanceResult);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SYSTEM_ERROR, "获取订单审批实例异常");
        }
        List<ApprovalInstanceModel.Instance> instanceList = approvalInstanceResult.getData();
        if (!org.springframework.util.CollectionUtils.isEmpty(instanceList)) {
            instanceList = instanceList.stream().sorted(Comparator.comparingLong(ApprovalInstanceModel.Instance::getCreateTime).reversed()).collect(Collectors.toList());
            ApprovalInstanceModel.Instance lastInstance = instanceList.get(0);
            if (ApprovalStatusEnum.IN_PROGRESS.getValue().equals(lastInstance.getState())) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SALES_ORDER_INVALID_WORKFLOW_IN_PROGRESS, "该销售订单目前正在变更中，不可创建发货单。");
            }
        }
    }

    public ObjectDataDocument fillWithDetails(RequestContext context, String describeApiName,
                                              ObjectDataDocument data) {
        List<IObjectDescribe> detailDescribeList = serviceFacade
                .findDetailDescribes(context.getTenantId(), describeApiName);
        Map<String, List<IObjectData>> details = serviceFacade
                .findDetailObjectDataList(detailDescribeList, data.toObjectData(), context.getUser());
        data.put("details", details);
        return data;
    }

    public void checkForInvalid(User user, IObjectData objectData) {
        checkStatusForInvalid(objectData);
        checkOutboundInvalidRight(user);
    }

    private void    checkOutboundInvalidRight(User user) {
        boolean isStockEnable = stockManager.isStockEnable(user.getTenantId());
        if (isStockEnable) {
            try {
                this.serviceFacade.doFunPrivilegeCheck(user, OutboundDeliveryNoteConstants.API_NAME, Lists.newArrayList(ObjectAction.INVALID.getActionCode()));
            } catch (PermissionError error) {
                throw new PermissionError("无["+OutboundDeliveryNoteConstants.DISPLAY_NAME+"]的["+ObjectAction.INVALID.getActionLabel()+"]功能权限");
            }
        }
    }

    private void checkStatusForInvalid(IObjectData objectData) {
        List<String> canInvalidStatus = Lists.newArrayList(DeliveryNoteObjStatusEnum.UN_DELIVERY.getStatus(), DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus());
        String status = objectData.get(DeliveryNoteObjConstants.Field.Status.apiName, String.class);
        if (!canInvalidStatus.contains(status)) {
            throw new ValidateException("'未发货、已发货'状态的发货单才能作废");
        }
    }

    public void doAfterInvalidAction(User user, IObjectData objectData, String oldStatus) {
        String newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName).toString();

        //无审批流
        if (newLifeStatus.equals(ObjectLifeStatus.INVALID.getCode())) {
            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.INVALID.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                    .operateObjectType(StockOperateObjectTypeEnum.DELIVERY_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value).build();

            invalidDeliveryNote(user, objectData, oldStatus, stockOperateInfo);
        } else {  //有审批流
            // 只有作废"已发货"的才有审批流，（作废'未发货'没有审批流）修改status为'变更中'
            IObjectData newInvalidDeliveryNoteObjData = updateStatus(user, objectData, DeliveryNoteObjStatusEnum.CHANGING);
            log.info("after update status: objectData[{}], newInvalidDeliveryNoteObjData[{}]", objectData, newInvalidDeliveryNoteObjData);
        }
    }
}
