package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteProductVO;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteVO;
import com.facishare.crm.deliverynote.predefine.util.ObjectDataUtil;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.search.IFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 发货单产品
 * Created by chenzs on 2018/1/23.
 */
@Service
@Slf4j
public class DeliveryNoteProductManager extends CommonManager {
    private static final int MAX_LIMIT_FOR_QUERY_ALL = 1000000;

    @Autowired
    private DeliveryNoteManager deliveryNoteManager;
    @Autowired
    private SalesOrderManager salesOrderManager;

    /**
     * 获取订单的已发货产品数量 <productId, 已发货产品数量>
     *
     * @param user
     * @param salesOrderId
     * @param includeInApprovalStatus 已发货是否包括DeliveryNoteObjStatusEnum.IN_APPROVAL"审核中"这种状态的
     */
    public Map<String, BigDecimal> getProductId2HasDeliveredAmountMap(User user, String salesOrderId, boolean includeInApprovalStatus) {
        List<DeliveryNoteProductVO> deliveryNoteProductList = getHasDeliveredProducts(user, salesOrderId, includeInApprovalStatus);
        return getProductId2DeliveryNum(deliveryNoteProductList);
    }

    /**
     * 获了订单的已发货产品
     */
    public List<DeliveryNoteProductVO> getHasDeliveredProducts(User user, String salesOrderId, boolean includeInApprovalStatus) {
        List<String> deliveredStatus = Lists.newArrayList();
        deliveredStatus.add(DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus());
        deliveredStatus.add(DeliveryNoteObjStatusEnum.RECEIVED.getStatus());
        deliveredStatus.add(DeliveryNoteObjStatusEnum.CHANGING.getStatus());
        if (includeInApprovalStatus) {
            deliveredStatus.add(DeliveryNoteObjStatusEnum.IN_APPROVAL.getStatus());
        }

        List<DeliveryNoteVO> deliveryNoteVOList = deliveryNoteManager.queryDeliveryNoteBySalesOrderId(user, salesOrderId, deliveredStatus);
        List<String> deliveryNoteIds = deliveryNoteVOList.stream().map(DeliveryNoteVO::getId).collect(Collectors.toList());
        List<IObjectData> deliveryNoteProductObjectDataList = this.queryObjectDatas(user, deliveryNoteIds);
        return ObjectDataUtil.parseObjectData(deliveryNoteProductObjectDataList, DeliveryNoteProductVO.class);
    }

    /**
     * 获取产品id对应的本次发货数之和 <产品id，本次发货数之和>
     */
    public Map<String, BigDecimal> getProductId2DeliveryNum(List<DeliveryNoteProductVO> deliveryNoteProductVOList) {
        Map<String, BigDecimal> result = Maps.newHashMap();
        deliveryNoteProductVOList.forEach(vo -> {
            String productId = vo.getProductId();
            BigDecimal deliveryNum = vo.getDeliveryNum();
            if (result.containsKey(productId)) {
                BigDecimal currentDeliveryNum = result.get(productId);
                result.put(productId, currentDeliveryNum.add(deliveryNum));
            } else {
                result.putIfAbsent(productId, deliveryNum);
            }
        });

        return result;
    }

    /**
     * 获取deliveryNoteId的所有发货单产品（可以查'已作废'的发货单的数据）
     */
    public List<DeliveryNoteProductVO> getDeliveryNoteProductVO(User user, String deliveryNoteId) {
        QueryResult<IObjectData> allDeliveryNoteProductResult = queryAllDataByField(user,
                DeliveryNoteProductObjConstants.API_NAME, DeliveryNoteProductObjConstants.Field.DeliveryNoteId.getApiName(),
                Lists.newArrayList(deliveryNoteId), 0, MAX_LIMIT_FOR_QUERY_ALL);

        List<IObjectData> invalidDeliveryNoteProductObjectDatas = allDeliveryNoteProductResult.getData();
        return ObjectDataUtil.parseObjectData(invalidDeliveryNoteProductObjectDatas, DeliveryNoteProductVO.class);
    }

    /**
     * 查询deliveryNoteIds的发货单产品
     */
    public List<DeliveryNoteProductVO> queryDeliveryNoteProductVos(User user, List<String> deliveryNoteIds) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, deliveryNoteIds);
        List<IObjectData> deliveryNotes = searchQuery(user, DeliveryNoteProductObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
        return ObjectDataUtil.parseObjectData(deliveryNotes, DeliveryNoteProductVO.class);
    }

    /**
     * 根据发货单ID查询发货单产品
     */
    public List<IObjectData> queryObjectDatas(User user, List<String> deliveryNoteIds) {
        if (CollectionUtils.isEmpty(deliveryNoteIds)) {
            return new ArrayList<>(0);
        }
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, DeliveryNoteProductObjConstants.Field.DeliveryNoteId.apiName, deliveryNoteIds);
        return searchQuery(user, DeliveryNoteProductObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    /**
     * 查询订单的发货单产品（包括已作废的数据）
     */
    public List<IObjectData> queryAllBySaleOrderId(User user, String saleOrderId) {
        QueryResult<IObjectData> queryResult = queryAllDataByField(user, DeliveryNoteProductObjConstants.API_NAME,
                DeliveryNoteProductObjConstants.Field.SalesOrderId.getApiName(), Lists.newArrayList(saleOrderId), 0, MAX_LIMIT_FOR_QUERY_ALL);
        List<IObjectData> allDeliveryNoteObjectDataList = queryResult.getData();
        if (Objects.isNull(allDeliveryNoteObjectDataList)) {
            return new ArrayList<>(0);
        }
        return allDeliveryNoteObjectDataList;
    }

    /**
     * 计算发货单产品的本次发货金额
     */
    public List<ObjectDataDocument> setDeliveryMoney(User user, String salesOrderId, List<ObjectDataDocument> productObjectDataList) {
        if (CollectionUtils.isEmpty(productObjectDataList)) {
            return productObjectDataList;
        }
        Map<String, SalesOrderManager.OrderProduct> productId2OrderProduct = salesOrderManager.getOrderProduct(user, salesOrderId);
        log.info("productId2OrderProduct[{}]", productId2OrderProduct);

        List<DeliveryNoteProductVO> hasDeliveredProducts = getHasDeliveredProducts(user, salesOrderId, true);
        log.info("hasDeliveredProducts[{}]", hasDeliveredProducts);

        Map<String, BigDecimal> productId2HasDeliveredMoneyMap = productId2HasDeliveredMoney(hasDeliveredProducts);
        log.info("productId2HasDeliveredMoneyMap[{}]", productId2HasDeliveredMoneyMap);

        Map<String, BigDecimal> productId2HasDeliveredAmountMap = this.getProductId2DeliveryNum(hasDeliveredProducts);
        log.info("productId2HasDeliveredAmountMap[{}]", productId2HasDeliveredAmountMap);

        productObjectDataList.forEach(iObjectData -> {
            String productId = iObjectData.toObjectData().get(DeliveryNoteProductObjConstants.Field.ProductId.apiName, String.class);
            BigDecimal deliveryNum = iObjectData.toObjectData().get(DeliveryNoteProductObjConstants.Field.DeliveryNum.apiName, BigDecimal.class);
            BigDecimal orderAmount = productId2OrderProduct.get(productId).getAllAmount();
            BigDecimal hasDeliveredAmount = productId2HasDeliveredAmountMap.getOrDefault(productId, BigDecimal.ZERO);
            boolean isLastDelivery = deliveryNum.add(hasDeliveredAmount).compareTo(orderAmount) >= 0;
            BigDecimal deliveryMoney;
            if (isLastDelivery) {
                BigDecimal hasDeliveredProductMoney = productId2HasDeliveredMoneyMap.getOrDefault(productId, BigDecimal.ZERO);
                deliveryMoney = productId2OrderProduct.get(productId).getAllSubTotal().subtract(hasDeliveredProductMoney);
            } else {
                BigDecimal avgPrice = productId2OrderProduct.get(productId).avgPrice();
                deliveryMoney = deliveryNum.multiply(avgPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            iObjectData.put(DeliveryNoteProductObjConstants.Field.AvgPrice.apiName, productId2OrderProduct.get(productId).avgPrice());
            iObjectData.put(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, deliveryMoney);
        });

        log.info("productObjectDataList[{}]", productObjectDataList);
        return productObjectDataList;
    }

    private Map<String, BigDecimal> productId2HasDeliveredMoney(List<DeliveryNoteProductVO> hasDeliveredProducts) {
        Map<String, BigDecimal> productId2HasDeliveredMoney = Maps.newHashMap();
        hasDeliveredProducts.forEach(productVO -> {
            BigDecimal hasDeliveredMoney = productId2HasDeliveredMoney.getOrDefault(productVO.getProductId(), BigDecimal.ZERO);
            productId2HasDeliveredMoney.put(productVO.getProductId(), hasDeliveredMoney.add(productVO.getDeliveryMoney()));
        });
        return productId2HasDeliveredMoney;
    }

}