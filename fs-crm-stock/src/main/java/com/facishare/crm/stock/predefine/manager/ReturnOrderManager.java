package com.facishare.crm.stock.predefine.manager;

import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.ReturnOrderModel;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by linchf on 2018/1/16.
 */
@Service
@Slf4j(topic = "stockAccess")
public class ReturnOrderManager extends CommonManager {
    @Resource
    private CrmRestApi crmRestApi;

    /**
     * 获取退货单详情
     */
    public ReturnOrderModel.ReturnOrderVo getById(User user, String returnOrderId) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        try {
            ReturnOrderModel.GetByIdResult result = crmRestApi.getReturnOrderById(returnOrderId, headers);
            if (!result.isSuccess()) {
                log.warn("crmRestApi.getReturnOrderById failed. result:{}, returnOrderId:{}, headers:{}", result, returnOrderId, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                return result.getValue();
            }
        } catch (StockBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("crmRestApi.getReturnOrderById fail! headers[{}], returnOrderId[{}]", headers, returnOrderId);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据退货单id, 查询退货单详情异常");
        }
    }

    public List<ReturnOrderModel.ReturnOrderVo> getByIds(User user, List<String> returnOrderIds) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        try {
            ReturnOrderModel.GetByIdsResult result = crmRestApi.getReturnOrderByIds(returnOrderIds.toArray(new String[returnOrderIds.size()]), headers);
            if (!result.isSuccess()) {
                log.warn("crmRestApi.getReturnOrderByIds failed. result:{}, returnOrderIds:{}, headers:{}", result, returnOrderIds, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                return result.getValue();
            }
        } catch (StockBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("crmRestApi.getReturnOrderByIds fail! headers[{}], returnOrderIds[{}]", headers, returnOrderIds);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据退货单Id列表，查询退货单详情异常");
        }
    }

    public List<ReturnOrderModel.ReturnOrderVo> getBySalesOrderId(User user, String salesOrderId) {
        Map<String, String> headers = StockUtils.getHeaders(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        ReturnOrderModel.QueryReturnOrderArg arg = buildQueryReturnOrderBySalesOrderId(salesOrderId);
        try {
            ReturnOrderModel.QueryReturnOrderByConditionResult result = crmRestApi.queryReturnOrderByCondition(arg, headers);
            if (!result.isSuccess()) {
                log.warn("crmRestApi.queryReturnOrderByCondition failed. result:{}, arg:{}, headers:{}", result, arg, headers);
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, result.getMessage());
            } else {
                if (result.getValue() != null && !CollectionUtils.isEmpty(result.getValue().getItems())) {
                    return result.getValue().getItems();
                }
                return Lists.newArrayList();

            }
        } catch (StockBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("crmRestApi.queryReturnOrderByCondition fail! arg:{}, headers:{}", arg, headers);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "根据订单Id，查询退货单详情异常");
        }
    }

    private ReturnOrderModel.QueryReturnOrderArg buildQueryReturnOrderBySalesOrderId(String salesOrderId) {
        ReturnOrderModel.QueryReturnOrderArg arg = new ReturnOrderModel.QueryReturnOrderArg();
        arg.setOffset(0);
        // 需一次把它全部查询出来，所以设置个很大的值
        arg.setLimit(10000);
        ReturnOrderModel.QueryReturnOrderArg.Condition condition = new ReturnOrderModel.QueryReturnOrderArg.Condition();
        condition.setConditionType("0");
        ReturnOrderModel.ReturnOrderVo conditions = new ReturnOrderModel.ReturnOrderVo();
        conditions.setCustomerTradeId(salesOrderId);
        condition.setConditions(conditions);
        arg.setConditions(Lists.newArrayList(condition));
        return arg;
    }
}
