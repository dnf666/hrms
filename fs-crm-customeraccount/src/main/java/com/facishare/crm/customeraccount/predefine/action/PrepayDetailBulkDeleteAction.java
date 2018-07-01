package com.facishare.crm.customeraccount.predefine.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.manager.PrepayDetailManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujf on 2017/12/5.
 */
@Slf4j
public class PrepayDetailBulkDeleteAction extends StandardBulkDeleteAction {
    private CrmManager crmManager;
    private PrepayDetailManager prepayDetailManager;

    @Override
    protected void before(Arg arg) {
        log.debug("begin PrepayDetailBulkDeleteAction,for arg:{}", arg);
        super.before(arg);
        prepayDetailManager = SpringUtil.getContext().getBean(PrepayDetailManager.class);
        crmManager = SpringUtil.getContext().getBean(CrmManager.class);
        if (!RequestUtil.isFromInner(actionContext)) {
            String objectApiName = arg.getDescribeApiName();
            List<String> ids = arg.getIdList();
            QueryResult<IObjectData> queryResult = prepayDetailManager.queryInvalidDataByField(actionContext.getUser(), objectApiName, SystemConstants.Field.Id.apiName, ids, 0, ids.size());
            List<IObjectData> prepayDatas = queryResult.getData();
            log.info("begin delete,for prepaDatas:{}", prepayDatas);
            List<String> orderPaymentIds = prepayDatas.stream().filter(objectData -> ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.OrderPayment.apiName) != null).map(objectData -> ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.OrderPayment.apiName)).collect(Collectors.toList());
            List<String> refundIds = prepayDatas.stream().filter(objectData -> ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.Refund.apiName) != null).map(objectData -> ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.Refund.apiName)).collect(Collectors.toList());

            String paymentMessage = "";
            if (CollectionUtils.isNotEmpty(orderPaymentIds)) {
                List<IObjectData> orderPaymentDatas = crmManager.listInvalidOrderPaymentByIds(actionContext.getUser(), orderPaymentIds);
                Map<String, String> paymentIdNameMap = orderPaymentDatas.stream().collect(Collectors.toMap(ob -> ob.getId(), IObjectData::getName));
                Map<String, String> orderPaymentNameMap = Maps.newHashMap();
                prepayDatas.stream().filter(objectData -> ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.OrderPayment.apiName) != null).forEach(prepayData -> {
                    String orderPaymentId = ObjectDataUtil.getReferenceId(prepayData, PrepayDetailConstants.Field.OrderPayment.apiName);
                    orderPaymentNameMap.put(prepayData.getName(), paymentIdNameMap.get(orderPaymentId));
                });
                paymentMessage = String.format("预存款{%s}有关联回款明细，不能直接删除，回款明细编码{%s}", Joiner.on(",").join(orderPaymentNameMap.keySet()), Joiner.on(",").join(orderPaymentNameMap.values()));
            }
            String refundMessage = "";
            if (CollectionUtils.isNotEmpty(refundIds)) {
                List<IObjectData> refundDatas = crmManager.listInvalidRefundByIds(actionContext.getUser(), refundIds);
                Map<String, String> refundIdNameMap = refundDatas.stream().collect(Collectors.toMap(ob -> ob.getId(), IObjectData::getName));
                Map<String, String> nameRefundNameMap = Maps.newHashMap();
                prepayDatas.stream().filter(objectData -> ObjectDataUtil.getReferenceId(objectData, PrepayDetailConstants.Field.Refund.apiName) != null).forEach(prepayData -> {
                    String refundId = ObjectDataUtil.getReferenceId(prepayData, PrepayDetailConstants.Field.Refund.apiName);
                    nameRefundNameMap.put(prepayData.getName(), refundIdNameMap.get(refundId));
                });
                refundMessage = String.format("预存款{%s}有关联退款，不能直接删除，退款编码{%s}", Joiner.on(",").join(nameRefundNameMap.keySet()), Joiner.on(",").join(nameRefundNameMap.values()));
            }
            if (StringUtils.isNotEmpty(paymentMessage) || StringUtils.isNotEmpty(refundMessage)) {
                String errorMessage = "";
                if (StringUtils.isNotEmpty(paymentMessage) && StringUtils.isNotEmpty(refundMessage)) {
                    errorMessage = Joiner.on("；").join(Lists.newArrayList(paymentMessage, refundMessage));
                } else if (StringUtils.isNotEmpty(paymentMessage)) {
                    errorMessage = paymentMessage;
                } else if (StringUtils.isNotEmpty(refundMessage)) {
                    errorMessage = refundMessage;
                }
                throw new ValidateException(errorMessage);
            }
        }
    }
}
