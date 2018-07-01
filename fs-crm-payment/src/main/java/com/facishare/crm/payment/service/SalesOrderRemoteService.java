package com.facishare.crm.payment.service;


import com.facishare.paas.appframework.common.util.ObjectAPINameMapping;
import com.facishare.paas.appframework.core.model.ControllerContext;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.appframework.metadata.restdriver.CRMRemoteServiceProxy;
import com.facishare.paas.appframework.metadata.restdriver.dto.FindObjectByIds;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SalesOrderRemoteService {
    private CRMRemoteServiceProxy crmRemoteServiceProxy =
            SpringUtil.getContext().getBean(CRMRemoteServiceProxy.class);

    public List<Map<String, Object>> findObjectDataByIds(ControllerContext context, String apiName, List<String> ids) {
        IActionContext actionContext = ActionContextExt.of(context.getUser()).getContext();
        Map<String, String> headers = getHeaders(context.getUser().getTenantId(), context.getUser().getUserId());
        FindObjectByIds.Arg arg = FindObjectByIds.Arg.builder().ids(ids)
                .objectType(ObjectAPINameMapping.toIntObjectType(apiName)).includeUserDefinedFields(true)
                .includeCalculationFields(actionContext.doCalculate()).build();

        FindObjectByIds.Result result = crmRemoteServiceProxy.findObjectDataByIds(arg, headers);
        if (!result.isSuccess()) {
            log.error("findObjectDataByIds error,headers:{},arg:{},result:{}", headers, arg, result);
            throw new MetaDataBusinessException(result.getMessage());
        }
        return result.getValue();
    }

    private Map<String, String> getHeaders(String tenantId, String userId) {
        Map<String, String> headers = Maps.newLinkedHashMap();
        headers.put("x-fs-ei", tenantId);
        headers.put("x-fs-userInfo", userId);
        if (RequestContextManager.getContext() != null
                && RequestContextManager.getContext().getPostId() != null) {
            headers.put("x-fs-postId", RequestContextManager.getContext().getPostId());
        }
        return headers;
    }

}
