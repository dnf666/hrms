package com.facishare.crm.customeraccount.predefine.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.facishare.paas.appframework.core.model.ActionContext;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContext.RequestSource;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

@Component
public class CommonService {
    @Autowired
    protected ServiceFacade serviceFacade;

    public static ApprovalFlowTriggerType getApprovalFlowTriggerTypeInstance(String value) {
        for (ApprovalFlowTriggerType type : ApprovalFlowTriggerType.values()) {
            if (type.getId().equals(value)) {
                return type;
            }
        }
        return null;
    }

    protected ObjectDataDocument triggerAddAction(ServiceContext serviceContext, String apiName, ObjectDataDocument objectData) {
        objectData.put(IObjectData.DESCRIBE_API_NAME, apiName);
        if (!objectData.containsKey(IObjectData.DESCRIBE_ID)) {
            IObjectDescribe desc = getObjectDescribe(serviceContext.getTenantId(), apiName);
            objectData.put(IObjectData.DESCRIBE_ID, desc.getId());
        }
        BaseObjectSaveAction.Arg arg = new BaseObjectSaveAction.Arg();
        arg.setObjectData(objectData);
        BaseObjectSaveAction.Result result = this.triggerAction(serviceContext, apiName, StandardAction.Add.name(), arg);
        return result.getObjectData();
    }

    protected ObjectDataDocument triggerInvalidAction(ServiceContext serviceContext, String apiName, String objectDataId, Map<String, Object> params) {
        StandardInvalidAction.Arg arg = new StandardInvalidAction.Arg();
        IObjectDescribe desc = getObjectDescribe(serviceContext.getTenantId(), apiName);
        arg.setObjectDescribeId(desc.getId());
        arg.setObjectDescribeApiName(apiName);
        arg.setObjectDataId(objectDataId);
        StandardInvalidAction.Result result = this.triggerAction(serviceContext, apiName, StandardAction.Invalid.name(), arg, params);
        return result.getObjectData();
    }

    protected ObjectDataDocument triggerEditAction(ServiceContext serviceContext, String apiName, ObjectDataDocument objectData) {
        objectData.put(IObjectData.DESCRIBE_API_NAME, apiName);
        if (!objectData.containsKey(IObjectData.DESCRIBE_ID)) {
            IObjectDescribe desc = getObjectDescribe(serviceContext.getTenantId(), apiName);
            objectData.put(IObjectData.DESCRIBE_ID, desc.getId());
        }
        BaseObjectSaveAction.Arg arg = new BaseObjectSaveAction.Arg();
        arg.setObjectData(objectData);
        BaseObjectSaveAction.Result result = this.triggerAction(serviceContext, apiName, StandardAction.Edit.name(), arg);
        return result.getObjectData();
    }

    protected <A, T> T triggerAction(ServiceContext serviceContext, String apiName, String actionCode, A arg) {
        return this.triggerAction(serviceContext, apiName, actionCode, arg, null);
    }

    protected <A, T> T triggerAction(ServiceContext serviceContext, String apiName, String actionCode, A arg, Map<String, Object> values) {
        RequestContext oldRequestContext = serviceContext.getRequestContext();
        User user = serviceContext.getUser();
        RequestContext newRequestContext = RequestContext.builder().requestSource(RequestSource.INNER).postId(oldRequestContext.getPostId()).tenantId(user.getTenantId()).user(Optional.of(user)).build();
        if (values != null) {
            for (Entry<String, Object> entry : values.entrySet()) {
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
