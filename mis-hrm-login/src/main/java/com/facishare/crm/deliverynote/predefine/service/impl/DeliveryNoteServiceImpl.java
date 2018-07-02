package com.facishare.crm.deliverynote.predefine.service.impl;

import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.enums.DeliveryNoteSwitchEnum;
import com.facishare.crm.deliverynote.enums.ExpressOrgEnum;
import com.facishare.crm.deliverynote.enums.SalesOrderLogisticsStatusEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.deliverynote.predefine.manager.*;
import com.facishare.crm.deliverynote.predefine.manager.kdapi.KdniaoAPI;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteProductVO;
import com.facishare.crm.deliverynote.predefine.model.LogisticsVO;
import com.facishare.crm.deliverynote.predefine.service.DeliveryNoteService;
import com.facishare.crm.deliverynote.predefine.service.dto.DeliveryNoteType;
import com.facishare.crm.deliverynote.predefine.service.dto.DeliveryNoteType.*;
import com.facishare.crm.deliverynote.predefine.service.dto.EmptyResult;
import com.facishare.crm.deliverynote.predefine.util.DeliveryNoteUtil;
import com.facishare.crm.deliverynote.util.ConfigCenter;
import com.facishare.crm.deliverynote.util.DateUtil;
import com.facishare.crm.manager.DeliveryNoteObjManager;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.rest.dto.SyncTenantSwitchModel;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.predefine.manager.ProductManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * impl
 * Created by chenzs on 2018/1/9.
 */
@Slf4j
@Component
public class DeliveryNoteServiceImpl implements DeliveryNoteService {
    @Resource
    private ObjectDescribeManager objectDescribeManager;
    @Resource
    private InitObjManager initObjManager;
    @Resource
    private ConfigManager configManager;
    @Autowired
    private SalesOrderManager salesOrderManager;
    @Resource
    private StockManager stockManager;
    @Resource
    private DeliveryNoteManager deliveryNoteManager;
    @Autowired
    private DeliveryNoteProductManager deliveryNoteProductManager;
    @Resource
    private ProductManager productManager;
    @Autowired
    private ServiceFacade serviceFacade;
    @Resource
    private DeliveryNoteObjManager deliveryNoteObjManager;
    @Resource
    private KdniaoAPI kdniaoAPI;
    @Resource
    private LayoutManager layoutManager;
    @Resource
    private DeliveryNoteTransferService deliveryNoteTransferService;
    @Resource
    private ErpStockSwitchManager erpStockSwitchManager;

    @Override
    public IsDeliveryNoteEnableResult isDeliveryNoteEnable(ServiceContext serviceContext) {
        DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(serviceContext.getTenantId());

        IsDeliveryNoteEnableResult result = new IsDeliveryNoteEnableResult();
        result.setEnable(deliveryNoteSwitchStatus.getStatus() == DeliveryNoteSwitchEnum.OPENED.getStatus());
        result.setHasSalesOrderNeedUpdate(salesOrderManager.existsDeliveredOrders(serviceContext.getTenantId(), serviceContext.getUser().getUserId()));
        result.setSwitchStatus(deliveryNoteSwitchStatus.getStatus());
        return result;
    }

    @Override
    public EnableDeliveryNoteResult initPrivilege(ServiceContext serviceContext) {
        EnableDeliveryNoteResult result = new EnableDeliveryNoteResult();
        try {
            initObjManager.initPrivilege(serviceContext.getUser());
        } catch (Exception e) {
            log.warn("initObjManager.initPrivilege ", e);
            result.setEnableStatus(DeliveryNoteSwitchEnum.OPEN_FAIL.getStatus());
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public EnableDeliveryNoteResult initPrintTemplate(ServiceContext serviceContext) {
        EnableDeliveryNoteResult result = new EnableDeliveryNoteResult();
        try {
            initObjManager.initPrintTemplate(serviceContext.getUser());
        } catch (Exception e) {
            log.warn("initObjManager.initPrintTemplate ", e);
            result.setEnableStatus(DeliveryNoteSwitchEnum.OPEN_FAIL.getStatus());
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public EnableDeliveryNoteResult addFuncAccessForRole(ServiceContext serviceContext) {
        EnableDeliveryNoteResult result = new EnableDeliveryNoteResult();
        initObjManager.addFuncAccessForRole(serviceContext.getUser());
        return result;
    }

    @Override
    public EnableDeliveryNoteResult testUpdateSalesOrderStatus(ServiceContext serviceContext) {
        return initObjManager.testUpdateSalesOrderStatus(serviceContext.getTenantId(), serviceContext.getUser());
    }

    @Override
    public EnableDeliveryNoteResult enableDeliveryNote(ServiceContext serviceContext) {
        return initObjManager.enableDeliveryNote(serviceContext.getTenantId(), serviceContext.getUser());
    }

    @Override
    public AddFieldResult addField(ServiceContext serviceContext) {
        AddFieldResult result = new AddFieldResult();
        try {
            deliveryNoteObjManager.addFieldForOpenStock(serviceContext.getUser());
//          objectDescribeManager.addFieldForOpenStock(serviceContext.getUser());
            result.setErrorCode(2);
        } catch (DeliveryNoteBusinessException e) {
            log.warn("objectDescribeManager.addField, serviceContext:{}", serviceContext, e);
            result.setErrorCode(1);
            result.setMessage("添加字段失败" + e.getMessage());
        } catch (Exception e) {
            log.warn("objectDescribeManager.addField, serviceContext:{}", serviceContext, e);
            result.setErrorCode(1);
            result.setMessage("添加字段失败");
        }
        return result;
    }

    @Override
    public DeliveryNoteType.EmptyResult changeFieldRequire(ServiceContext serviceContext) {
        String changeFieldRequireTenantIds = ConfigCenter.changeFieldRequire_TenantIds;
        if (StringUtils.isEmpty(changeFieldRequireTenantIds)) {
            return new DeliveryNoteType.EmptyResult();
        }
        List<String> tenantIds = Lists.newArrayList(changeFieldRequireTenantIds.split(";"));
        log.info("changeFieldRequire tenantIds:{}", tenantIds);

        if (CollectionUtils.isEmpty(tenantIds)) {
            return new DeliveryNoteType.EmptyResult();
        }

        tenantIds.forEach(tenantId -> {
            log.info("-------------------changeFieldRequire tenantId:{}", tenantId);
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);

            //开启了发货单+库存的
            DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(tenantId);
            if (Objects.equals(deliveryNoteSwitchStatus.getStatus(), DeliveryNoteSwitchEnum.OPENED.getStatus())) {
                boolean isStockEnable = stockManager.isStockEnable(user.getTenantId());
                if (isStockEnable) {
                    objectDescribeManager.changeFieldRequireToFalse(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName);
                    objectDescribeManager.changeFieldRequireToFalse(user, DeliveryNoteProductObjConstants.API_NAME, DeliveryNoteProductObjConstants.Field.StockId.apiName);
                    layoutManager.changeFieldRequireToFalse(user, DeliveryNoteObjConstants.API_NAME, DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName);                }
            }
        });

        return new DeliveryNoteType.EmptyResult();
    }

    @Override
    public AddFieldResult addFieldDescribeAndData(ServiceContext serviceContext) {
        String transferTenantIds = ConfigCenter.Transfer_TenantIds;
        if (Strings.isEmpty(transferTenantIds)) {
            return new AddFieldResult();
        }
        List<String> tenantIds = Lists.newArrayList(transferTenantIds.split(";"));
        log.info("addFieldDescribeAndData tenantIds:{}", tenantIds);

        if (CollectionUtils.isEmpty(tenantIds)) {
            return new AddFieldResult();
        }
        for (String tenantId : tenantIds) {
            log.info("---------------------");
            log.info("--------------------- addFieldDescribeAndData begin tenantId:{}", tenantId);
            deliveryNoteTransferService.addFieldDescribeAndData(serviceContext, tenantId);
            log.info("--------------------- addFieldDescribeAndData end tenantId:{}", tenantId);
        }
        return new AddFieldResult();
    }

    @Override
    public DeliveryNoteType.EmptyResult updateDeliveryNoteProductDescribeConfig(ServiceContext serviceContext) {
        String transferTenantIds = ConfigCenter.Transfer_TenantIds;
        if (Strings.isEmpty(transferTenantIds)) {
            return new DeliveryNoteType.EmptyResult();
        }
        List<String> tenantIds = Lists.newArrayList(transferTenantIds.split(";"));
        log.info("updateDeliveryNoteProductDescribeConfig tenantIds:{}", tenantIds);

        if (CollectionUtils.isEmpty(tenantIds)) {
            return new DeliveryNoteType.EmptyResult();
        }
        for (String tenantId : tenantIds) {
            log.info("--------------------- updateDeliveryNoteProductDescribeConfig tenantId:{}", tenantId);
            objectDescribeManager.updateDescribeConfig(tenantId, DeliveryNoteProductObjConstants.API_NAME);
        }
        return new DeliveryNoteType.EmptyResult();
    }

    @Override
    public DeliveryNoteType.EmptyResult salesOrderDefaultLayoutAddFields(ServiceContext serviceContext) {
        String transferTenantIds = ConfigCenter.Transfer_TenantIds_SalesOrder_Add_Fields;
        if (Strings.isEmpty(transferTenantIds)) {
            return new DeliveryNoteType.EmptyResult();
        }
        List<String> tenantIds = Lists.newArrayList(transferTenantIds.split(";"));
        log.info("salesOrderDefaultLayoutAddFields tenantIds:{}", tenantIds);

        if (CollectionUtils.isEmpty(tenantIds)) {
            return new DeliveryNoteType.EmptyResult();
        }
        for (String tenantId : tenantIds) {
            log.info("--------------------- salesOrderDefaultLayoutAddFields tenantId:{}", tenantId);
            //开启了发货单才需要处理
            DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(tenantId);
            if (!Objects.equals(deliveryNoteSwitchStatus.getStatus(), DeliveryNoteSwitchEnum.OPENED.getStatus())) {
                log.info("--------------------- salesOrderDefaultLayoutAddFields tenantId:{} deliveryNote switch is not 'OPENED'", tenantId);
                break;
            }
            User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);
            salesOrderManager.salesOrderAddDeliveredAmountSumField(user);
            salesOrderManager.salesOrderProductAddDeliveredCountAndDeliveryAmountField(user);
        }
        return new DeliveryNoteType.EmptyResult();
    }

    @Override
    public GetWarehouseBySalesOrderIdModel.Result getWarehouseBySalesOrderId(ServiceContext serviceContext, GetWarehouseBySalesOrderIdModel.Arg arg) {
        Preconditions.checkNotNull(arg, "arg is null");
        Preconditions.checkNotNull(arg.getSalesOrderId(), "订单ID为空");

        // 查询订单并返回
        SalesOrderModel.SalesOrderVo salesOrderVo = salesOrderManager.getById(serviceContext.getUser(), arg.getSalesOrderId());
        String deliveryWarehouseId = salesOrderVo.getWarehouseId();
        GetWarehouseBySalesOrderIdModel.Result result = new GetWarehouseBySalesOrderIdModel.Result();
        result.setId(deliveryWarehouseId);
        if (StringUtils.isNotBlank(deliveryWarehouseId)) {
            IObjectData warehouseObjectData = serviceFacade.findObjectData(serviceContext.getUser(), deliveryWarehouseId, WarehouseConstants.API_NAME);
            result.setName(warehouseObjectData.get(WarehouseConstants.Field.Name.apiName, String.class));
        }
        return result;
    }

    @Override
    public GetCanDeliverProductsModel.Result getCanDeliverProducts(ServiceContext serviceContext, GetCanDeliverProductsModel.Arg arg) {
        Preconditions.checkNotNull(arg, "arg is null");
        Preconditions.checkNotNull(arg.getSalesOrderId(), "订单ID为空");

        User user = serviceContext.getUser();
        String salesOrderId = arg.getSalesOrderId();

        // 校验订单状态
        SalesOrderModel.SalesOrderVo salesOrderVo = salesOrderManager.getById(user, salesOrderId);
        deliveryNoteManager.checkSalesOrderStatus(user, salesOrderVo);

        // 订单产品数量
        Map<String, BigDecimal> canDeliverProductId2OrderAmount = salesOrderManager.getCanDeliverProductAmountMap(user, salesOrderId);
        if (canDeliverProductId2OrderAmount.isEmpty()) {
            GetCanDeliverProductsModel.Result result = new GetCanDeliverProductsModel.Result();
            result.setList(Lists.newArrayList());
            result.setEmptyReason(GetCanDeliverProductsModel.EmptyReason.getOrderNoCanDeliverProductReason());
            return result;
        }

        // 订单已经发货产品数量
        Map<String, BigDecimal> productId2HasDeliveredAmount = deliveryNoteProductManager.getProductId2HasDeliveredAmountMap(serviceContext.getUser(), arg.getSalesOrderId(), true);

        // 可发货产品ID列表
        List<String> canDeliveryProductIds = canDeliverProductId2OrderAmount.entrySet().stream().filter(entry -> {
            String productId = entry.getKey();
            BigDecimal orderAmount = entry.getValue();
            return !productId2HasDeliveredAmount.containsKey(productId) || productId2HasDeliveredAmount.get(productId).compareTo(orderAmount) < 0;
        }).map(Map.Entry::getKey).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(canDeliveryProductIds)) {
            GetCanDeliverProductsModel.Result result = new GetCanDeliverProductsModel.Result();
            result.setList(Lists.newArrayList());
            result.setEmptyReason(GetCanDeliverProductsModel.EmptyReason.getAllOrderProductHasDeliveredReason());
            return result;
        }

        // 可发货发货单产品
        List<QueryProductByIds.ProductVO> crmProductVOList = productManager.queryProductByIds(serviceContext.getUser(), canDeliveryProductIds);
        List<DeliveryNoteProductVO> canDeliveredProducts = crmProductVOList.stream()
                .map(crmProductVO -> {
                    String productId = crmProductVO.getId();
                    BigDecimal hasDeliveredNum = productId2HasDeliveredAmount.getOrDefault(productId, BigDecimal.ZERO);
                    return DeliveryNoteProductVO.builder()
                            .salesOrderId(salesOrderId)
                            .salesOrderName(salesOrderVo.getTradeCode())
                            .productId(crmProductVO.getId())
                            .productName(crmProductVO.getProductName())
                            .specs(crmProductVO.getSpecs())
                            .unit(crmProductVO.getUnit())
                            .hasDeliveredNum(hasDeliveredNum)
                            .orderProductAmount(canDeliverProductId2OrderAmount.get(productId))
                            .salesOrderId(arg.getSalesOrderId())
                            .build();
                }).collect(Collectors.toList());

        // 开启了库存，过滤掉实际库存为0的产品
        boolean isStockEnable = stockManager.isStockEnable(serviceContext.getTenantId());
        if (isStockEnable && StringUtils.isNotEmpty(arg.getWarehouseId())) {
            Map<String, StockType.StockVO> productId2StockMap = deliveryNoteManager.getProductStock(user, arg.getWarehouseId(), canDeliveryProductIds);

            canDeliveredProducts = canDeliveredProducts.stream().filter(product -> {
                StockType.StockVO stockVO = productId2StockMap.get(product.getProductId());
                return Objects.nonNull(stockVO) && stockVO.getRealStock().compareTo(BigDecimal.ZERO) > 0;
            }).collect(Collectors.toList());

            canDeliveredProducts.forEach(canDeliveredProduct -> {
                StockType.StockVO stockVO = productId2StockMap.get(canDeliveredProduct.getProductId());
                canDeliveredProduct.setStockId(stockVO.getId());
                canDeliveredProduct.setStockName(stockVO.getName());
                canDeliveredProduct.setRealStock(stockVO.getRealStock());
            });

            if (CollectionUtils.isEmpty(canDeliveredProducts)) {
                GetCanDeliverProductsModel.Result result = new GetCanDeliverProductsModel.Result();
                result.setList(canDeliveredProducts);
                result.setEmptyReason(GetCanDeliverProductsModel.EmptyReason.getStockNoCanDeliverProductReason());
                return result;
            }
        }

        // 设置平均发货数
        Map<String, SalesOrderManager.OrderProduct> productId2OrderProduct = salesOrderManager.getOrderProduct(user, salesOrderId);
        canDeliveredProducts.forEach(deliveryNoteProduct ->
                deliveryNoteProduct.setAvgPrice(productId2OrderProduct.get(deliveryNoteProduct.getProductId()).avgPrice()));

        GetCanDeliverProductsModel.Result result = new GetCanDeliverProductsModel.Result();
        result.setList(canDeliveredProducts);
        return result;
    }

    @Override
    public EmptyResult confirmReceive(ServiceContext serviceContext, DeliveryNoteType.ConfirmReceiveArg arg) {
        Preconditions.checkArgument(Objects.nonNull(arg));
        Preconditions.checkArgument(Objects.nonNull(arg.getDeliveryNoteId()));
        if (!CollectionUtils.isEmpty(arg.getDeliveryNoteProducts())) {
            arg.getDeliveryNoteProducts().forEach(deliveryNoteProduct -> Preconditions.checkArgument(Objects.nonNull(deliveryNoteProduct.getProductId())));
        }
        Preconditions.checkArgument(Objects.nonNull(arg.getDeliveryNoteId()));

        // 操作权限
        List<String> actionCodes = Lists.newArrayList(DeliveryNoteObjConstants.Button.ConfirmReceipt.apiName);
        serviceFacade.doFunPrivilegeCheck(serviceContext.getUser(), DeliveryNoteObjConstants.API_NAME, actionCodes);

        User user = serviceContext.getUser();
        String deliveryNoteId = arg.getDeliveryNoteId();
        IObjectData deliveryNoteObjectData = this.serviceFacade.findObjectData(user, deliveryNoteId, DeliveryNoteObjConstants.API_NAME);
        if (Objects.isNull(deliveryNoteObjectData)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_NOT_EXISTS);
        }

        String status = deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.Status.apiName, String.class);
        if (!DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus().equals(status)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SALES_ORDER_STATUS_NOT_HAS_DELIVERED);
        }

        // 校验本次收货数不能大于产品发货数
        List<IObjectData> productObjectDataList = deliveryNoteProductManager.queryObjectDatas(user, Lists.newArrayList(arg.getDeliveryNoteId()));
        Map<String, BigDecimal> productId2DeliveryNumMap = productObjectDataList.stream()
                .collect(Collectors.toMap(x -> x.get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class),
                        x -> x.get(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, BigDecimal.class)));
        Map<String, BigDecimal> productId2RealReceiveNumMap = arg.getDeliveryNoteProducts().stream()
                .filter(deliveryNoteProduct -> Objects.nonNull(deliveryNoteProduct.getRealReceiveNum()) || Objects.nonNull(deliveryNoteProduct.getReceiveRemark()))
                .collect(Collectors.toMap(DeliveryNoteProduct::getProductId, DeliveryNoteProduct::getRealReceiveNum));
        productId2RealReceiveNumMap.forEach((key, realReceiveNum) -> {
            if (!productId2DeliveryNumMap.containsKey(key)) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.SYSTEM_ERROR, "产品ID["+key+"]不属于当前发货单");
            }
            BigDecimal deliveryNum = productId2DeliveryNumMap.get(key);
            if (Objects.nonNull(realReceiveNum) && realReceiveNum.compareTo(deliveryNum) > 0) {
                throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.REAL_RECEIVE_NUM_GREAT_TO_DELIVERY_NUM);
            }
        });

        // 更新发货单产品的本次收货数及收货备注
        List<IObjectData> needUpdateProductObjectDataList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(arg.getDeliveryNoteProducts())) {
            productObjectDataList.forEach(productObjectData -> {
                String productId = productObjectData.get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class);
                Optional<DeliveryNoteProduct> noteProductOptional = arg.getDeliveryNoteProducts().stream().filter(deliveryNoteProduct -> deliveryNoteProduct.getProductId().equals(productId)).findFirst();
                noteProductOptional.ifPresent(deliveryNoteProduct -> {
                    productObjectData.set(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName, deliveryNoteProduct.getRealReceiveNum());
                    productObjectData.set(DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName, deliveryNoteProduct.getReceiveRemark());
                    needUpdateProductObjectDataList.add(productObjectData);
                });
            });
            serviceFacade.batchUpdateByFields(user, needUpdateProductObjectDataList, Lists.newArrayList(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName, DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName));
        }

        // 更新发货单状态、收货日志及收货备注
        deliveryNoteManager.updateStatus(user, deliveryNoteObjectData, DeliveryNoteObjStatusEnum.RECEIVED);
        Map<String, Object> updateFiledMap = Maps.newHashMap();
        updateFiledMap.put(DeliveryNoteObjConstants.Field.Status.apiName, DeliveryNoteObjStatusEnum.RECEIVED.getStatus());
        updateFiledMap.put(DeliveryNoteObjConstants.Field.ReceiveRemark.apiName, arg.getReceiveRemark());
        updateFiledMap.put(DeliveryNoteObjConstants.Field.ReceiveDate.apiName, DateUtil.getTimesTodayStartTime().getTime());
        deliveryNoteObjectData = this.serviceFacade.updateWithMap(user, deliveryNoteObjectData, updateFiledMap);


        String salesOrderId = ObjectDataExt.of(deliveryNoteObjectData).get(DeliveryNoteObjConstants.Field.SalesOrderId.apiName, String.class);
        boolean isReceivedAll = deliveryNoteManager.isAllReceived(user, salesOrderId);
        SalesOrderLogisticsStatusEnum newLogisticsStatus = isReceivedAll ? SalesOrderLogisticsStatusEnum.Received : SalesOrderLogisticsStatusEnum.PartialReceipt;

        // 记录订单修改日志
        String deliveryNoteName = deliveryNoteObjectData.getName();
        String logText = String.format("关联的发货单%s已确认收货", deliveryNoteName);
        SalesOrderModel.SalesOrderVo currentSalesOrder = salesOrderManager.getById(user, salesOrderId);
        Integer oldLogisticStatus = currentSalesOrder.getLogisticsStatus();
        if (!Objects.equals(oldLogisticStatus, newLogisticsStatus.getStatus())) {
            logText = logText + String.format("，发货状态变更为%s", newLogisticsStatus.getMessage());
        }
        salesOrderManager.saveModifyLog(user, salesOrderId, logText);

        // 更新订单信息
        deliveryNoteManager.updateSalesOrderForStatusChange(user, currentSalesOrder, newLogisticsStatus);
        return new EmptyResult();
    }

    @Override
    public GetByDeliveryNoteIdResult getByDeliveryNoteId(ServiceContext serviceContext, GetByDeliveryNoteIdArg arg) {
        Preconditions.checkArgument(Objects.nonNull(arg));
        Preconditions.checkArgument(Objects.nonNull(arg.getDeliveryNoteId()));

        User user = serviceContext.getUser();
        String deliveryNoteId = arg.getDeliveryNoteId();

        IObjectData masterObjectData = deliveryNoteManager.getObjectDataById(user, deliveryNoteId);
        if (Objects.isNull(masterObjectData)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_NOT_EXISTS);
        }

        IObjectDescribe productDetailDescribe = serviceFacade.findObject(user.getTenantId(), DeliveryNoteProductObjConstants.API_NAME);
        List<IObjectData> deliveryProductObjectDataList = this.serviceFacade.findDetailObjectDataList(productDetailDescribe, masterObjectData, user);
        GetByDeliveryNoteIdResult result = new GetByDeliveryNoteIdResult();
        result.setDeliveryNoteObjData(ObjectDataDocument.of(masterObjectData));
        result.setDeliveryNoteProductObjDataList(deliveryProductObjectDataList.stream().map(ObjectDataDocument::of).collect(Collectors.toList()));
        return result;
    }

    @Override
    public GetBySalesOrderIdResult getBySalesOrderId(ServiceContext serviceContext, GetBySalesOrderIdArg arg) {
        Preconditions.checkArgument(Objects.nonNull(arg));
        Preconditions.checkArgument(Objects.nonNull(arg.getSalesOrderId()));

        User user = serviceContext.getUser();
        String salesOrderId = arg.getSalesOrderId();

        List<String> status = Lists.newArrayList(DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus(), DeliveryNoteObjStatusEnum.RECEIVED.getStatus());
        List<IObjectData> deliveryNoteList = deliveryNoteManager.queryDeliveryNoteObjectDataBySalesOrderId(user, salesOrderId, status);

        // 补充物流公司名称
        deliveryNoteList.forEach(objectData -> {
            String expressOrg = objectData.get(DeliveryNoteObjConstants.Field.ExpressOrg.apiName, String.class);
            if (StringUtils.isNotEmpty(expressOrg)) {
                String expressOrgName = getExpressOrgName(expressOrg);
                objectData.set("express_org_name", expressOrgName);
            }
        });

        List<String> deliveryNoteIds = deliveryNoteList.stream().map(objectData -> objectData.get(DeliveryNoteObjConstants.Field.Id.apiName, String.class)).collect(Collectors.toList());
        Map<String, List<ObjectDataDocument>> deliveryNoteId2ProductListMap = Maps.newHashMap();
        List<IObjectData> productList = deliveryNoteProductManager.queryObjectDatas(user, deliveryNoteIds);
        productList.forEach(product -> {
            String deliveryNoteId = product.get(DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, String.class);
            if (!deliveryNoteId2ProductListMap.containsKey(deliveryNoteId)) {
                deliveryNoteId2ProductListMap.put(deliveryNoteId, Lists.newArrayList(ObjectDataDocument.of(product)));
            } else {
                deliveryNoteId2ProductListMap.get(deliveryNoteId).add(ObjectDataDocument.of(product));
            }
        });

        GetBySalesOrderIdResult result = new GetBySalesOrderIdResult();
        result.setDeliveryNoteList(deliveryNoteList.stream().map(ObjectDataDocument::of).collect(Collectors.toList()));
        result.setDeliveryNoteId2ProductsMap(deliveryNoteId2ProductListMap);
        return result;
    }

    @Override
    public DeliveryNoteType.GetLogisticsResult getLogistics(ServiceContext serviceContext, DeliveryNoteType.GetLogisticsArg arg) {
        Preconditions.checkArgument(Objects.nonNull(arg));
        Preconditions.checkArgument(Objects.nonNull(arg.getDeliveryNoteId()));

        // 校验权限
        List<String> actionCodes = Lists.newArrayList(DeliveryNoteObjConstants.Button.ViewLogistics.apiName);
        serviceFacade.doFunPrivilegeCheck(serviceContext.getUser(), DeliveryNoteObjConstants.API_NAME, actionCodes);

        IObjectData deliveryNoteObjectData = deliveryNoteManager.getDeliveryNoteObjectData(serviceContext.getUser(), arg.getDeliveryNoteId());
        if (Objects.isNull(deliveryNoteObjectData)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.DELIVERY_NOTE_NOT_EXISTS);
        }

        String expressOrg = deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.ExpressOrg.apiName, String.class);
        String expressOrderId = deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.ExpressOrderId.apiName, String.class);
        if (StringUtils.isBlank(expressOrderId)) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.EXPRESS_ORDER_ID_IS_BLANK);
        }

        Long deliveryDate = deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.DeliveryDate.apiName, Long.class);

        LogisticsVO.LogisticsVOBuilder logisticsVOBuilder = LogisticsVO.builder();
        logisticsVOBuilder.expressOrderId(expressOrderId);
        logisticsVOBuilder.deliveryDate(deliveryDate);
        logisticsVOBuilder.expressOrg(expressOrg);

        if (StringUtils.isBlank(expressOrg)) {
            expressOrg = distinguishExpressOrg(expressOrderId).orElse(null);
            if (Objects.isNull(expressOrg)) {
                logisticsVOBuilder.state(Integer.toString(LogisticsVO.StateEnum.UN_DISTINGUISHABLE_EXPRESS_ORDER_ID.getState()));
                logisticsVOBuilder.reason(LogisticsVO.StateEnum.UN_DISTINGUISHABLE_EXPRESS_ORDER_ID.getDesc());
                return GetLogisticsResult.builder().logisticsVO(logisticsVOBuilder.build()).build();
            }
        }

        KdniaoAPI.GetOrderTracesResult getOrderTracesResultResult = kdniaoAPI.getOrderTraces(expressOrg, expressOrderId);
        logisticsVOBuilder.reason(getOrderTracesResultResult.getReason());
        logisticsVOBuilder.expressOrg(getOrderTracesResultResult.getShipperCode());
        logisticsVOBuilder.expressOrgName(getExpressOrgName(getOrderTracesResultResult.getShipperCode()));
        logisticsVOBuilder.state(getOrderTracesResultResult.getState());
        List<LogisticsVO.Trace> traces;
        if (CollectionUtils.isNotEmpty(getOrderTracesResultResult.getTraces())) {
            traces = getOrderTracesResultResult.getTraces().stream().map(trace -> {
                LogisticsVO.Trace traceVO = new LogisticsVO.Trace();
                traceVO.setAcceptStation(trace.getAcceptStation());
                traceVO.setAcceptTime(trace.getAcceptTime());
                traceVO.setRemark(trace.getRemark());
                return traceVO;
            }).collect(Collectors.toList());
        } else {
            traces = Lists.newArrayList();
        }
        logisticsVOBuilder.traces(traces);
        return GetLogisticsResult.builder().logisticsVO(logisticsVOBuilder.build()).build();
    }

    private Optional<String> distinguishExpressOrg(String expressOrderId) {
        KdniaoAPI.OrderDistinguishResult distinguishResult = kdniaoAPI.orderDistinguish(expressOrderId);
        if (CollectionUtils.isEmpty(distinguishResult.getShippers())) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(distinguishResult.getShippers().get(0).getCode());
        }

    }

    private String getExpressOrgName(String expressOrg) {
        String expressName = "未知";
        if (StringUtils.isBlank(expressOrg)) {
            return expressName;
        }
        try {
            expressName = ExpressOrgEnum.getByCode(expressOrg).getLabel();
        } catch (IllegalArgumentException e) {}
        return  expressName;
    }
}