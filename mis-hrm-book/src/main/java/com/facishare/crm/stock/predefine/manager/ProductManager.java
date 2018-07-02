package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.QueryProductByIds;
import com.facishare.crm.rest.dto.ReturnOrderModel;
import com.facishare.crm.rest.dto.SalesOrderModel;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 16/01/2018
 */

@Slf4j(topic = "stockAccess")
@Service
public class ProductManager extends CommonManager {

    @Resource
    private CrmRestApi crmRestApi;

    /**
     * 根据产品id 查找产品详情
     * @param user 用户
     * @param productIds 产品Id
     * @return 包含安全库存的产品详情列表
     */
    public List<QueryProductByIds.ProductVO> queryProductByIds(User user, List<String> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            log.warn("crmRestApi.queryListByProductIds fail! user[{}], productIds[{}]", user, productIds);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "产品id列表参数不能为空");
        }

        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        try {
            QueryProductByIds.Result result = crmRestApi.queryListByProductIds(productIds.toArray(new String[productIds.size()]), headers);

            if (!result.isSuccess()) {
                log.warn("crm.RestApi.queryProductByIds failed. result{}, user{}, headers{}", result, user, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                return result.getValue();
            }
        } catch (StockBusinessException se) {
            //业务错误，直接抛出
            throw se;
        } catch (Exception e) {
            log.warn("crmRestApi.queryListByProductIds fail! headers[{}], productIds[{}]", headers, productIds);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "查询产品列表详细信息异常");
        }
    }

    /**
     * 根据订单id 查询产品
     * @param user 用户
     * @param salesOrderId 销售订单Id
     * @param isSumProductAmount 是否合并不同业务类型 同种产品数量
     * @return 订单详情列表
     */
    public List<SalesOrderModel.SalesOrderProductVO> getProductsByOrderId(User user, String salesOrderId, boolean isSumProductAmount) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        SalesOrderModel.QueryOrderProductArg arg = buildGetProductsByIdArg(salesOrderId);
        try {
            SalesOrderModel.QueryOrderProductResult result = crmRestApi.queryOrderProduct(arg, headers);
            if (!result.isSuccess()) {
                log.warn("crmRestApi.queryOrderProduct failed. result:{}, arg:{}, headers:{}", result, arg, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                if (isSumProductAmount) {
                    return sumSalesOrderProductAmount(result.getValue());
                }
                return result.getValue();
            }
        } catch (StockBusinessException se) {
            throw se;
        } catch (Exception e) {
            log.warn("crmRestApi.queryOrderProduct fail! headers[{}], arg[{}]", headers, arg);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据订单id, 查询产品信息异常");
        }
    }

    /**
     * 根据退货单id 查询产品
     * @param user 用户
     * @param returnOrderId 退货单id
     * @return 退货单详情列表
     */
    public List<ReturnOrderModel.ReturnOrderProductVO> getReturnProductsByOrderId(User user, String returnOrderId) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        ReturnOrderModel.QueryReturnOrderProductArg arg = buildGetReturnProductsByIdArg(returnOrderId);
        try {
            ReturnOrderModel.QueryReturnOrderProductResult result = crmRestApi.queryReturnOrderProduct(arg, headers);
            if (!result.isSuccess()) {
                log.warn("crmRestApi.queryReturnOrderProduct failed. result:{}, arg:{}, headers:{}", result, arg, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                return sumReturnOrderProductAmount(result.getValue());
            }
        } catch (StockBusinessException se) {
            throw se;
        } catch (Exception e) {
            log.warn("crmRestApi.queryReturnOrderProduct fail! headers[{}], arg[{}]", headers, arg);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据退货单id, 查询产品信息异常");
        }

    }

    /**
     * 根据订单ids批量查询订单产品
     * @param user 用户
     * @param ids 销售订单id列表
     * @return
     */
    public Map<String, List<SalesOrderModel.SalesOrderProductVO>> queryProductsByOrderIds(User user, List<String> ids) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        Map<String, List<SalesOrderModel.SalesOrderProductVO>> salesOrderMap;
        try {
            SalesOrderModel.QueryOrderProductResult result = crmRestApi.queryOrderProductByOrderIds(ids.toArray(new String[ids.size()]), headers);

            if (!result.isSuccess()) {
                log.warn("crmRestApi.queryOrderProductByOrderIds failed. result:{}, arg:{}, headers:{}", result, ids.toArray(new String[ids.size()]), headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                salesOrderMap = result.getValue().stream().collect(Collectors.groupingBy(SalesOrderModel.SalesOrderProductVO::getCustomerTradeId));
                salesOrderMap.keySet().forEach(id -> {
                    List<SalesOrderModel.SalesOrderProductVO> salesOrderProductVOs = sumSalesOrderProductAmount(salesOrderMap.get(id));
                    salesOrderMap.put(id, salesOrderProductVOs);
                });
            }
        } catch (StockBusinessException se) {
            throw se;
        } catch (Exception e) {
            log.warn("crmRestApi.queryOrderProductByOrderIds fail! headers[{}], ids[{}]", headers, ids);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据订单ids, 查询产品信息异常");
        }
        return salesOrderMap;
    }

    /**
     * 根据退货单ids批量查询产品
     * @param user 用户
     * @param ids 退货单Id列表
     * @return
     */
    public Map<String, List<ReturnOrderModel.ReturnOrderProductVO>> queryReturnOrderProductsByReturnOrderIds(User user, List<String> ids) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        Map<String, List<ReturnOrderModel.ReturnOrderProductVO>> returnOrderMap;

        try {
            ReturnOrderModel.QueryReturnOrderProductResult result = crmRestApi.queryReturnOrderProductByReturnOrderIds(ids.toArray(new String[ids.size()]), headers);

            if (!result.isSuccess()) {
                log.warn("crmRestApi.queryReturnOrderProductByReturnOrderIds failed. result:{}, arg:{}, headers:{}", result, ids.toArray(new String[ids.size()]), headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                returnOrderMap = result.getValue().stream().collect(Collectors.groupingBy(ReturnOrderModel.ReturnOrderProductVO::getReturnOrderID));
                returnOrderMap.keySet().forEach(id -> {
                    List<ReturnOrderModel.ReturnOrderProductVO> returnOrderProductVOs = sumReturnOrderProductAmount(returnOrderMap.get(id));
                    returnOrderMap.put(id, returnOrderProductVOs);
                });
            }
        } catch (StockBusinessException se) {
            throw se;
        } catch (Exception e) {
            log.warn("crmRestApi.queryReturnOrderProductByReturnOrderIds fail! headers[{}], ids[{}]", headers, ids);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据退货单ids, 查询产品信息异常");
        }
        return returnOrderMap;
    }



    public List<SalesOrderModel.SalesOrderProductVO> sumSalesOrderProductAmount(List<SalesOrderModel.SalesOrderProductVO> productVOs) {
        Map<String, BigDecimal> salesOrderProductMap = new HashMap<>();
        //由于不同场景，订单产品列表可能有多个相同产品
        productVOs.forEach(product -> {
            BigDecimal amount = salesOrderProductMap.get(product.getProductId());
            if (amount != null) {
                salesOrderProductMap.put(product.getProductId(), amount.add(product.getAmount()));
            } else {
                salesOrderProductMap.put(product.getProductId(), product.getAmount());
            }
        });

        List<SalesOrderModel.SalesOrderProductVO> sumProductList = Lists.newArrayList();
        List<String> existProductIds = Lists.newArrayList();
        productVOs.forEach(product -> {
            if (!existProductIds.contains(product.getProductId())) {
                SalesOrderModel.SalesOrderProductVO salesOrderProductVo = new SalesOrderModel.SalesOrderProductVO();
                salesOrderProductVo.setPrice(product.getPrice());
                salesOrderProductVo.setProductId(product.getProductId());
                salesOrderProductVo.setAmount(salesOrderProductMap.get(product.getProductId()));
                salesOrderProductVo.setProductName(product.getProductName());
                salesOrderProductVo.setTradeProductId(product.getTradeProductId());
                salesOrderProductVo.setCustomerTradeId(product.getCustomerTradeId());
                sumProductList.add(salesOrderProductVo);
                existProductIds.add(product.getProductId());
            }
        });
        return sumProductList;
    }

    public List<ReturnOrderModel.ReturnOrderProductVO> sumReturnOrderProductAmount(List<ReturnOrderModel.ReturnOrderProductVO> productVOs) {
        Map<String, BigDecimal> returnProductMap = new HashMap<>();
        //由于不同场景，退货单产品列表可能有多个相同产品
        productVOs.forEach(product -> {
            BigDecimal amount = returnProductMap.get(product.getProductId());
            if (amount != null) {
                returnProductMap.put(product.getProductId(), amount.add(product.getAmount()));
            } else {
                returnProductMap.put(product.getProductId(), product.getAmount());
            }
        });

        List<ReturnOrderModel.ReturnOrderProductVO> sumProductList = Lists.newArrayList();
        List<String> existProductIds = Lists.newArrayList();
        productVOs.forEach(product -> {
            if (!existProductIds.contains(product.getProductId())) {
                ReturnOrderModel.ReturnOrderProductVO returnOrderProductVo = new ReturnOrderModel.ReturnOrderProductVO();

                returnOrderProductVo.setProductId(product.getProductId());
                returnOrderProductVo.setAmount(returnProductMap.get(product.getProductId()));
                returnOrderProductVo.setReturnOrderID(product.getReturnOrderID());
                sumProductList.add(returnOrderProductVo);
                existProductIds.add(product.getProductId());
            }
        });
        return sumProductList;
    }
}
