package com.facishare.crm.customeraccount.predefine.action;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.remote.CrmManager;
import com.facishare.crm.customeraccount.util.ObjectDataUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RebateIncomeDetailBulkDeleteAction extends StandardBulkDeleteAction {
    private RebateIncomeDetailManager rebateIncomeDetailManager;
    private CrmManager crmManager;

    @Override
    protected void before(Arg arg) {
        rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        crmManager = SpringUtil.getContext().getBean(CrmManager.class);
        super.before(arg);
        List<IObjectData> rebateIncomeDetailDatas = rebateIncomeDetailManager.listInvalidDataByIds(actionContext.getUser(), arg.getIdList());

        List<String> refundIds = Lists.newArrayList();
        List<String> rebateIncomdeDetailNamesRelatedWithRefund = rebateIncomeDetailDatas.stream().filter(iObjectData -> {
            String refundId = ObjectDataUtil.getReferenceId(iObjectData, RebateIncomeDetailConstants.Field.Refund.apiName);
            if (StringUtils.isNotEmpty(refundId)) {
                refundIds.add(refundId);
                return true;
            } else {
                return false;
            }
        }).map(IObjectData::getName).collect(Collectors.toList());
        log.debug("RebateIncomeDetailBulkDeleteAction->rebateIncomdeDetailNamesRelatedWithRefund:{}", rebateIncomdeDetailNamesRelatedWithRefund);
        List<IObjectData> refundDatas = crmManager.listInvalidRefundByIds(actionContext.getUser(), refundIds);
        List<String> refundNames = refundDatas.stream().map(IObjectData::getName).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(rebateIncomdeDetailNamesRelatedWithRefund)) {
            throw new ValidateException(String.format("返利{%s}有关联退款,不能直接删除,退款Id{%s}", JsonUtil.toJson(rebateIncomdeDetailNamesRelatedWithRefund), JsonUtil.toJson(refundNames)));
        }
    }
}
