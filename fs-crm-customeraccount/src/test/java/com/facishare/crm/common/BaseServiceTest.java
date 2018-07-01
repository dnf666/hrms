package com.facishare.crm.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.facishare.paas.appframework.core.model.ActionContext;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;

public class BaseServiceTest extends BaseTest {
    protected String apiName;
    @Autowired
    ServiceFacade serviceFacade;
    @Autowired
    private IObjectDescribeService objectDescribeService;

    public BaseServiceTest(String apiName) {
        this.apiName = apiName;
    }

    public ServiceContext newServiceContext() {
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        return serviceContext;
    }

    protected <A, T> T triggerAction(ServiceContext serviceContext, String apiName, String actionCode, A arg) {
        return this.triggerAction(serviceContext, apiName, actionCode, arg, null);
    }

    protected <A, T> T triggerAction(ServiceContext serviceContext, String apiName, String actionCode, A arg, Map<String, Object> values) {
        RequestContext oldRequestContext = serviceContext.getRequestContext();
        RequestContext newRequestContext = RequestContext.builder().requestSource(RequestContext.RequestSource.INNER).postId(oldRequestContext.getPostId()).tenantId(oldRequestContext.getTenantId()).user(Optional.of(oldRequestContext.getUser())).build();
        if (values != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                newRequestContext.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        ActionContext actionContext = new ActionContext(newRequestContext, apiName, actionCode);
        T result = serviceFacade.triggerAction(actionContext, arg);
        return result;
    }

    protected IObjectDescribe getObjectDescribe(String tenantId, String apiName) {
        IObjectDescribe describe = serviceFacade.findObject(tenantId, apiName);
        return describe;
    }

}
