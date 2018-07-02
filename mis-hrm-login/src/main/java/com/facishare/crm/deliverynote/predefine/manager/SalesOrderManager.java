package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.enums.SalesOrderLogisticsStatusEnum;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.deliverynote.predefine.util.DeliveryNoteUtil;
import com.facishare.crm.describebuilder.FormFieldBuilder;
import com.facishare.crm.manager.DeliveryNoteLayoutManager;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.rest.dto.UpdateCustomerOrderForDeliveryNoteModel;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component("SalesOrderManagerForDelivery")
public class SalesOrderManager extends CommonManager {
    @Resource
    private CrmRestApi crmRestApi;
    @Autowired
    private DeliveryNoteManager deliveryNoteManager;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private DeliveryNoteLayoutManager deliveryNoteLayoutManager;

    private static final String SALES_ORDER_DELIVERED_AMOUNT_SUM_FIELD_NAME = "delivered_amount_sum";
    private static final String SALES_ORDER_PRODUCT_DELIVERED_COUNT_FIELD_NAME = "delivered_count";
    private static final String SALES_ORDER_PRODUCT_DELIVERY_AMOUNT_FIELD_NAME = "delivery_amount";
    private static final String API_NAME_SUFFIX = "_generate_by_UDObjectServer__c";

    /**
     * 获取订单中订单产品中每个产品的数量
     */
    public Map<String, BigDecimal> getOrderProductAmountMap(User user, String salesOrderId) {
        List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts = getSalesOrderProducts(user, salesOrderId);
        return getOrderProductAmountMap(salesOrderProducts);
    }

    public Map<String, BigDecimal> getOrderProductAmountMap(List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts) {
        if (CollectionUtils.empty(salesOrderProducts)) {
            return Maps.newHashMap();
        }
        Map<String, BigDecimal> productId2OrderAmount = Maps.newHashMap();
        salesOrderProducts.forEach(vo -> {
            String productId = vo.getProductId();
            if (Objects.isNull(productId2OrderAmount.get(productId))) {
                productId2OrderAmount.put(productId, vo.getAmount());
            } else {
                BigDecimal amount =  productId2OrderAmount.get(productId).add(vo.getAmount());
                productId2OrderAmount.put(productId, amount);
            }
        });
        return productId2OrderAmount;
    }

    /**
     * 获取订单产品
     */
    public Map<String, OrderProduct> getOrderProduct(User user, String salesOrderId) {
        List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts = getSalesOrderProducts(user, salesOrderId);
        return getOrderProduct(salesOrderProducts);
    }

    public Map<String, OrderProduct> getOrderProduct(List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts) {
        Map<String, OrderProduct> productId2OrderProduct = Maps.newHashMap();
        salesOrderProducts.forEach(vo -> {
            String productId = vo.getProductId();
            BigDecimal price = vo.getPrice();
            BigDecimal amount = vo.getAmount();
            BigDecimal subTotal = amount.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (Objects.isNull(productId2OrderProduct.get(productId))) {
                productId2OrderProduct.put(productId, new OrderProduct(productId, amount, subTotal));
            } else {
                OrderProduct productPrice = productId2OrderProduct.get(productId);
                productPrice.addAmount(amount);
                productPrice.addSubTotal(subTotal);
            }
        });
        return productId2OrderProduct;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class OrderProduct {
        private String productId;
        private BigDecimal allAmount;
        private BigDecimal allSubTotal;

        public void addAmount(BigDecimal amount) {
            this.allAmount = this.allAmount.add(amount);
        }

        public void addSubTotal(BigDecimal subTotal) {
            this.allSubTotal = this.allSubTotal.add(subTotal);
        }

        public BigDecimal avgPrice() {
            return allSubTotal.divide(allAmount, 2, BigDecimal.ROUND_HALF_UP);
        }

        public static void main(String[] args) {
            OrderProduct p = new OrderProduct("1", new BigDecimal(3), new BigDecimal(10));
            System.out.println(p.avgPrice());
        }
    }

    /**
     * 获取订单可发货的产品数量（剔除订单产品数小于等于0的产品）
     */
    public Map<String, BigDecimal> getCanDeliverProductAmountMap(User user, String salesOrderId) {
        Map<String, BigDecimal> productId2OrderAmount = getOrderProductAmountMap(user, salesOrderId);
        List<String> notNeedDeliverProductIds = Lists.newArrayList();
        productId2OrderAmount.forEach((key, value) -> {
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                notNeedDeliverProductIds.add(key);
            }
        });
        notNeedDeliverProductIds.forEach(productId2OrderAmount::remove);
        return productId2OrderAmount;
    }

    /**
     * 获取订单详情
     */
    public SalesOrderModel.SalesOrderVo getById(User user, String salesOrderId) {
        Map<String, String> headers = getHeaders(user.getTenantId(), user.getUserId());
        SalesOrderModel.GetByIdResult result = crmRestApi.getCustomerOrderById(salesOrderId, headers);
        if (!result.isSuccess()) {
            log.warn("crmRestApi.getCustomerOrderById failed. result:{}, salesOrderId:{}, headers:{}", salesOrderId, headers);
            throw new DeliveryNoteBusinessException(result::getErrorCode, result.getMessage());
        } else {
            return result.getValue();
        }
    }

    /**
     * 设置订单物流状态
     */
    @Deprecated
    public void setLogisticsStatus(User user, String salesOrderId, SalesOrderLogisticsStatusEnum logisticStatus) {
        Map<String, String> headers = getHeaders(user.getTenantId(), user.getUserId());
        SalesOrderModel.SetLogisticsStatusArg arg = new SalesOrderModel.SetLogisticsStatusArg();
        arg.setObjectId(salesOrderId);
        arg.setLogisticsStatus(logisticStatus.getStatus());
        SalesOrderModel.SetLogisticsStatusResult result = crmRestApi.setLogisticsStatus(arg, headers);
        log.debug("crmRestApi.setLogisticsStatus arg[{}], headers[{}], result[{}]", arg, headers, result);
        if (!result.isSuccess()) {
            log.warn("crmRestApi.setLogisticsStatus failed. result:{}, arg:{}, headers:{}", arg, headers);
            throw new DeliveryNoteBusinessException(result::getErrorCode, result.getMessage());
        }
    }

    /**
     * 根据订单id，获取订单产品列表
     */
    public List<SalesOrderModel.SalesOrderProductVO> getSalesOrderProducts(User user, String salesOrderId) {
        Map<String, String> headers = getHeaders(user.getTenantId(), user.getUserId());
        SalesOrderModel.QueryOrderProductArg arg = buildGetProductsByIdArg(salesOrderId);
        SalesOrderModel.QueryOrderProductResult result = crmRestApi.queryOrderProduct(arg, headers);
        if (!result.isSuccess()) {
            log.warn("crmRestApi.queryOrderProduct failed. result:{}, arg:{}, headers:{}", result, arg, headers);
            throw new DeliveryNoteBusinessException(result::getErrorCode, result.getMessage());
        } else {
            return result.getValue();
        }
    }

    private SalesOrderModel.QueryOrderProductArg buildGetProductsByIdArg(String salesOrderId) {
        SalesOrderModel.QueryOrderProductArg arg = new SalesOrderModel.QueryOrderProductArg();
        arg.setOffset(0);
        // 需一次把它全部查询出来，所以设置个很大的值
        arg.setLimit(10000);
        SalesOrderModel.QueryOrderProductArg.Condition condition = new SalesOrderModel.QueryOrderProductArg.Condition();
        condition.setConditionType("0");
        SalesOrderModel.SalesOrderVo conditions = new SalesOrderModel.SalesOrderVo();
        conditions.setCustomerTradeId(salesOrderId);
        condition.setConditions(conditions);
        arg.setConditions(Lists.newArrayList(condition));
        return arg;
    }

    /**
     * 所有需要发货的数量
     */
    public BigDecimal getAllNeedDeliveryNum(User user, String salesOrderId) {
        List<SalesOrderModel.SalesOrderProductVO> salesOrderProducts = getSalesOrderProducts(user, salesOrderId);

        BigDecimal allNeedDeliveryNum = null;
        for (SalesOrderModel.SalesOrderProductVO s : salesOrderProducts) {
            if (allNeedDeliveryNum == null) {
                allNeedDeliveryNum = s.getAmount();
            } else {
                allNeedDeliveryNum.add(s.getAmount());
            }
        }
        return allNeedDeliveryNum;
    }

    /**
     * 获取某个企业创建过发货单的所有订单id（包括已作废的发货单）
     */
    public List<String> getAllHasDeliveryNoteSaleOrderIds(User user) {
        Set<String> allHasDeliveryNoteSaleOrderIds = new HashSet<>();

        int offset = 0;
        int limit = 10;   // TODO: 2018/3/20 chenzs 测试后改大一点
        int fetchSize = 0;

        List<OrderBy> orderBys = Lists.newArrayList();
        SearchUtil.fillOrderBy(orderBys, SystemConstants.Field.Id.apiName, true);
        do {
            //获取一页
            List<IObjectData> onePageObjectDatas = deliveryNoteManager.getObjectDatasByTenantId(user, user.getTenantId(), offset, limit, orderBys);
            if (CollectionUtils.empty(onePageObjectDatas)) {
                break;
            }
            fetchSize = onePageObjectDatas.size();
            offset += limit;

            //提取saleOrderId
            onePageObjectDatas.forEach(objectData -> {
                allHasDeliveryNoteSaleOrderIds.add((String)objectData.get(DeliveryNoteObjConstants.Field.SalesOrderId.apiName));
            });
        } while (fetchSize == limit);

        return Lists.newArrayList(allHasDeliveryNoteSaleOrderIds);
    }

    /**
     * 保存订单修改日志
     */
    public void saveModifyLog(User user, String salesOrderId, String logText) {
        IObjectDescribe salesOrderObjectDescribe = serviceFacade.findObject(user.getTenantId(), "SalesOrderObj");
        IObjectData salesOrderObjectData = serviceFacade.findObjectData(user, salesOrderId, salesOrderObjectDescribe);
        serviceFacade.logWithCustomMessage(user, EventType.MODIFY, ActionType.None, salesOrderObjectDescribe, salesOrderObjectData, logText);
    }

    public boolean updateCustomerOrderForDeliveryNote(User user, UpdateCustomerOrderForDeliveryNoteModel.Arg arg) {
        Map<String, String> headers = getHeaders(user.getTenantId(), user.getUserId());
        UpdateCustomerOrderForDeliveryNoteModel.Result result = crmRestApi.updateCustomerOrderForDeliveryNote(arg, headers);
        return result.isSuccess();
    }


    /**
     * 查询是否存在已发货状态的订单
     */
    public boolean existsDeliveredOrders(String tenantId, String userId) {
        Map<String, String> headers = DeliveryNoteUtil.getHeadersWithLength(tenantId, userId);

        SalesOrderModel.ExistsDeliveredOrders existsDeliveredOrdersResult;
        try {
            existsDeliveredOrdersResult = crmRestApi.existsDeliveredOrders(headers);
            log.info("crmRestApi.existsDeliveredOrders. headers:{}, result:{}", headers, existsDeliveredOrdersResult);
        } catch (Exception e) {
            log.error("crmRestApi.existsDeliveredOrders failed ", e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.QUERY_EXIST_DELIVERED_ORDER_FAILED, DeliveryNoteErrorCode.QUERY_EXIST_DELIVERED_ORDER_FAILED.getMessage() + e.getMessage());
        }
        if (!existsDeliveredOrdersResult.isSuccess()) {
            log.info("crmRestApi.existsDeliveredOrders failed. headers:{}, result:{}", headers, existsDeliveredOrdersResult);
            throw new DeliveryNoteBusinessException(() -> DeliveryNoteErrorCode.QUERY_EXIST_DELIVERED_ORDER_FAILED.getCode(), DeliveryNoteErrorCode.QUERY_EXIST_DELIVERED_ORDER_FAILED.getMessage() + existsDeliveredOrdersResult.getMessage());
        }
        return  existsDeliveredOrdersResult.isValue();
    }

    /**
     * 6.3 加的字段
     * 这个逻辑写在销售订单那边比较好，但是找了袁建龙不理会，（销售订单那边只加了describe，不加layout），所以就加到了我们这边
     *
     * 如果销售订单的defaultLayout没有'已发货金额'（delivered_amount_sum），则加上
     */
    public void salesOrderAddDeliveredAmountSumField(User user) {
        log.info("salesOrderAddDeliveredAmountSumField, user[{}]", user);
        List<ILayout> layouts = deliveryNoteLayoutManager.findByObjectDescribeApiNameAndTenantId(SystemConstants.SalesOrderApiName, user.getTenantId());
        if (!org.springframework.util.CollectionUtils.isEmpty(layouts)) {
            Optional<ILayout> defaultLayoutOpt = layouts.stream().filter(layout -> Objects.equals(layout.getName(), SystemConstants.SalesOrderApiName + "_layout_generate_by_UDObjectServer__c")).findFirst();
            if (defaultLayoutOpt.isPresent()) {
                ILayout defaultLayout = defaultLayoutOpt.get();

                IFieldSection baseFieldSection = getSaleOrderBaseFieldSection(defaultLayout);

                if (baseFieldSection != null) {
                    List<IFormField> oldFormFields = baseFieldSection.getFields();
                    Optional<IFormField> deliveredAmountSumOpt = oldFormFields.stream().filter(oldFormField -> Objects.equals(oldFormField.getFieldName(), SALES_ORDER_DELIVERED_AMOUNT_SUM_FIELD_NAME)).findFirst();
                    if (!deliveredAmountSumOpt.isPresent()) {
                        IFormField deliveredAmountSumFormField = FormFieldBuilder.builder().fieldName(SALES_ORDER_DELIVERED_AMOUNT_SUM_FIELD_NAME).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build();
                        log.info("salesOrderAddDeliveredAmountSumField addField, user[{}], deliveredAmountSumFormField[{}]", user, deliveredAmountSumFormField);
                        oldFormFields.add(deliveredAmountSumFormField);
                        baseFieldSection.setFields(oldFormFields);
                        deliveryNoteLayoutManager.replace(defaultLayout);
                    }
                }
            }
        }
    }

    private IFieldSection getSaleOrderBaseFieldSection(ILayout defaultLayout) {
        List<IComponent> components = null;
        FormComponent formComponent = null;
        try {
            components = defaultLayout.getComponents();
        } catch (MetadataServiceException e) {
            log.warn("layout.getComponents failed, layout:{}", defaultLayout);
            throw new DeliveryNoteBusinessException((DeliveryNoteErrorCode.BUSINESS_ERROR), "layout获取Component信息错误," + e.getMessage());
        }
        for (IComponent iComponent : components) {
            if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)
                    || Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME + API_NAME_SUFFIX)) {
                formComponent = (FormComponent) iComponent;
                break;
            }
        }

        //2、获取formFields
        IFieldSection baseFieldSection = null;
        if (formComponent != null) {
            List<IFieldSection> fieldSections = formComponent.getFieldSections();
            for (IFieldSection fieldSection : fieldSections) {
                if (Objects.equals(fieldSection.getName(), LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                    baseFieldSection = fieldSection;
                    break;
                }
            }
        }

        return baseFieldSection;
    }

    /**
     * 6.3 加的字段
     * 这个逻辑写在销售订单那边比较好，但是找了袁建龙不理会，（销售订单那边只加了describe，不加layout），所以就加到了我们这边
     *
     * 如果销售订单产品的defaultLayout没有'已发货数'、'发货金额小计'（delivered_count,delivery_amount），则加上
     */
    public void salesOrderProductAddDeliveredCountAndDeliveryAmountField(User user) {
        log.info("salesOrderProductAddDeliveredCountAndDeliveryAmountField, user[{}]", user);
        boolean isNeedReplace = false;
        List<ILayout> layouts = deliveryNoteLayoutManager.findByObjectDescribeApiNameAndTenantId(SystemConstants.SalesOrderProductApiName, user.getTenantId());
        if (!org.springframework.util.CollectionUtils.isEmpty(layouts)) {
            Optional<ILayout> defaultLayoutOpt = layouts.stream().filter(layout -> Objects.equals(layout.getName(), SystemConstants.SalesOrderProductApiName + "_layout_generate_by_UDObjectServer__c")).findFirst();
            if (defaultLayoutOpt.isPresent()) {
                ILayout defaultLayout = defaultLayoutOpt.get();
                IFieldSection baseFieldSection = getSaleOrderBaseFieldSection(defaultLayout);

                if (baseFieldSection != null) {
                    List<IFormField> oldFormFields = baseFieldSection.getFields();

                    //delivered_count
                    Optional<IFormField> deliveredAmountSumOpt = oldFormFields.stream().filter(oldFormField -> Objects.equals(oldFormField.getFieldName(), SALES_ORDER_PRODUCT_DELIVERED_COUNT_FIELD_NAME)).findFirst();
                    if (!deliveredAmountSumOpt.isPresent()) {
                        IFormField deliveredCountFormField = FormFieldBuilder.builder().fieldName(SALES_ORDER_PRODUCT_DELIVERED_COUNT_FIELD_NAME).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build();
                        log.info("salesOrderProductAddDeliveredCountAndDeliveryAmountField addField, user[{}], deliveredCountFormField[{}]", user, deliveredCountFormField);
                        oldFormFields.add(deliveredCountFormField);
                        isNeedReplace = true;
                    }

                    //delivery_amount
                    Optional<IFormField> deliveryAmountOpt = oldFormFields.stream().filter(oldFormField -> Objects.equals(oldFormField.getFieldName(), SALES_ORDER_PRODUCT_DELIVERY_AMOUNT_FIELD_NAME)).findFirst();
                    if (!deliveryAmountOpt.isPresent()) {
                        IFormField deliveryAmountFormField = FormFieldBuilder.builder().fieldName(SALES_ORDER_PRODUCT_DELIVERY_AMOUNT_FIELD_NAME).readOnly(true).renderType(SystemConstants.RenderType.Number.renderType).required(false).build();
                        log.info("salesOrderProductAddDeliveredCountAndDeliveryAmountField addField, user[{}], deliveryAmountFormField[{}]", user, deliveryAmountFormField);
                        oldFormFields.add(deliveryAmountFormField);
                        isNeedReplace = true;
                    }

                    if (isNeedReplace) {
                        baseFieldSection.setFields(oldFormFields);
                        deliveryNoteLayoutManager.replace(defaultLayout);
                    }
                }
            }
        }
    }
}
