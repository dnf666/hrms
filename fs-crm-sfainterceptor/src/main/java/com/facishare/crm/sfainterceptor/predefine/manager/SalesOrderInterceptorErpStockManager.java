package com.facishare.crm.sfainterceptor.predefine.manager;


import com.facishare.crm.erpstock.model.ErpCheckOrderModel;
import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.common.SalesOrderProductVo;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.predefine.manager.ProductManager;
import com.facishare.crm.stock.predefine.manager.SaleOrderManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/5/10
 */
@Service
@Slf4j(topic = "sfainterceptorAccess")
public class SalesOrderInterceptorErpStockManager {

    @Resource
    private ProductManager productManager;

    @Resource
    private ErpStockManager erpStockManager;

    @Resource
    private SaleOrderManager saleOrderManager;

    /**
     * 校验订单和可用库存
     *
     * @param user                 操作者
     * @param salesOrderProductVos 订单产品列表
     */
    public void checkOrderAndAvailableStock(User user, List<SalesOrderProductVo> salesOrderProductVos) {
        //过滤重复订单产品 累加数量
        salesOrderProductVos = sumProductAmount(salesOrderProductVos);
        if (!CollectionUtils.isEmpty(salesOrderProductVos)) {
            List<String> productIds = salesOrderProductVos.stream().map(SalesOrderProductVo::getProductId).distinct().collect(Collectors.toList());
            List<QueryProductByIds.ProductVO> productVOs = productManager.queryProductByIds(user, productIds);
            Map<String, String> productNameMap = productVOs.stream().collect(Collectors.toMap(QueryProductByIds.ProductVO::getId, QueryProductByIds.ProductVO::getProductName));

            List<ErpCheckOrderModel.CheckProduct> products =
                    salesOrderProductVos.stream().map(product -> new ErpCheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), productNameMap.get(product.getProductId())))
                            .collect(Collectors.toList());
            //校验可用库存
            checkAvailableStock(user, products);
        }
    }

    /**
     * 根据订单id 校验订单和可用库存
     * @param user 操作者
     * @param dataId 订单id
     */
    public void checkOrderAndAvailableStockByDataId(User user, String dataId) {
        List<SalesOrderModel.SalesOrderProductVO> productVOs = productManager.getProductsByOrderId(user, dataId, true);
        if (CollectionUtils.isEmpty(productVOs)) {
            log.info("checkOrderByDataId quit. products is empty.");
            return;
        }

        List<ErpCheckOrderModel.CheckProduct> products =
                productVOs.stream().map(product -> new ErpCheckOrderModel.CheckProduct(product.getProductId(), product.getAmount(), product.getProductName()))
                        .collect(Collectors.toList());


        if (!CollectionUtils.isEmpty(products)) {
            //校验可用库存
            checkAvailableStock(user, products);
        }
    }

    public void checkOrderAvailableStockByDataIds(User user, List<String> dataIds) {
        List<SalesOrderModel.SalesOrderVo> salesOrderVos = saleOrderManager.getByIds(user, dataIds);
        if(CollectionUtils.isEmpty(salesOrderVos)) {
            return;
        }

        dataIds = salesOrderVos.stream().map(SalesOrderModel.SalesOrderVo::getCustomerTradeId).collect(Collectors.toList());

        Map<String, List<SalesOrderModel.SalesOrderProductVO>> productVOsMap = productManager.queryProductsByOrderIds(user, dataIds);
        List<SalesOrderModel.SalesOrderProductVO> salesOrderProductVOs = productVOsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(salesOrderProductVOs)) {
            return;
        }
        salesOrderProductVOs = productManager.sumSalesOrderProductAmount(salesOrderProductVOs);

        List<ErpCheckOrderModel.CheckProduct> checkProducts = salesOrderProductVOs.stream().map(salesOrderProductVO ->
                new ErpCheckOrderModel.CheckProduct(salesOrderProductVO.getProductId(), salesOrderProductVO.getAmount(), salesOrderProductVO.getProductName())).collect(Collectors.toList());
        checkAvailableStock(user, checkProducts);
    }


    //校验可用库存
    private ErpCheckOrderModel.CheckProductStock checkAvailableStock(User user, List<ErpCheckOrderModel.CheckProduct> checkProducts) {
        ErpCheckOrderModel.CheckProductStock result = new ErpCheckOrderModel.CheckProductStock();
        Map<String, BigDecimal> saleProductsMap = new HashMap<>();
        List<IObjectData> queryStockResult = Lists.newArrayList();
        //校验订货仓库可用库存是否满足
        List<String> productList = checkProducts.stream().map(ErpCheckOrderModel.CheckProduct::getProductId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(productList)) {
            saleProductsMap = checkProducts.stream().collect(Collectors.toMap(ErpCheckOrderModel.CheckProduct::getProductId, ErpCheckOrderModel.CheckProduct::getProductNum));
            queryStockResult = erpStockManager.queryStocksByProductIds(user, productList);
            erpStockManager.checkAvailableErpStockWithProducts(user, queryStockResult, checkProducts);
        }
        result.setCheckProductStock(queryStockResult);
        result.setCheckProductNum(saleProductsMap);
        result.setProductIds(productList);
        return result;
    }

    //合并产品数量
    private List<SalesOrderProductVo> sumProductAmount(List<SalesOrderProductVo> salesOrderProductVos) {
        Map<String, BigDecimal> productMap = new HashMap<>();
        salesOrderProductVos.stream().forEach(product -> {
            if (productMap.get(product.getProductId()) != null) {
                productMap.put(product.getProductId(), product.getAmount().add(productMap.get(product.getProductId())));
            } else {
                productMap.put(product.getProductId(), product.getAmount());
            }
        });

        salesOrderProductVos.stream().forEach(product -> {
            product.setAmount(productMap.get(product.getProductId()));
        });

        Map<String, SalesOrderProductVo> salesOrderProductVoMap = new HashMap<>();
        salesOrderProductVos.stream().forEach(product -> {
            if (salesOrderProductVoMap.get(product.getProductId()) == null) {
                salesOrderProductVoMap.put(product.getProductId(), product);
            }
        });
        return new ArrayList<>(salesOrderProductVoMap.values());
    }
}
