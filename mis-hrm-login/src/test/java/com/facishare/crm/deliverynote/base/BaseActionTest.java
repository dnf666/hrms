package com.facishare.crm.deliverynote.base;

import com.facishare.paas.appframework.core.model.*;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class BaseActionTest extends BaseTest {
    @Autowired
    private ActionLocateService actionLocateService;
    @Autowired
    private SerializerManager serializerManager;
    @Autowired
    private IObjectDescribeService objectDescribeService;

    protected String apiName;

    public BaseActionTest(String apiName) {
        this.apiName = apiName;
    }

    public String getObjectDescribeId() {
        try {
            IObjectDescribe desc = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, apiName);
            return desc.getId();
        } catch (MetadataServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public IObjectDescribe getObjectDescribe() {
        try {
            IObjectDescribe desc = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, apiName);
            return desc;
        } catch (MetadataServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Object executeAdd(IObjectData objectData, Map<String, List<ObjectDataDocument>> details) {
        objectData.setDescribeId(getObjectDescribeId());
        objectData.setDescribeApiName(apiName);
        objectData.setTenantId(tenantId);
        BaseObjectSaveAction.Arg arg = new BaseObjectSaveAction.Arg();
        arg.setDetails(details);
        arg.setObjectData(ObjectDataDocument.of(objectData));
        return execute(StandardAction.Add.name(), arg);
    }

    public <T> Object executeEdit(IObjectData objectData) {
        objectData.setDescribeId(getObjectDescribeId());
        objectData.setDescribeApiName(apiName);
        objectData.setTenantId(tenantId);
        BaseObjectSaveAction.Arg arg = new BaseObjectSaveAction.Arg();
        arg.setObjectData(ObjectDataDocument.of(objectData));
        return execute(StandardAction.Edit.name(), arg);
    }

    public <T> Object executeDelete(IObjectData objectData) {
        objectData.setDescribeId(getObjectDescribeId());
        objectData.setDescribeApiName(apiName);
        BaseObjectSaveAction.Arg arg = new BaseObjectSaveAction.Arg();
        arg.setObjectData(ObjectDataDocument.of(objectData));
        return execute(StandardAction.Delete.name(), arg);
    }

    public <T> Object executeInvalid(IObjectData objectData) {
        objectData.setDescribeId(getObjectDescribeId());
        StandardInvalidAction.Arg arg = new StandardInvalidAction.Arg();
        arg.setObjectDescribeId(objectData.getDescribeId());
        arg.setObjectDescribeApiName(objectData.getDescribeApiName());
        arg.setObjectDataId(objectData.getId());
        return execute(StandardAction.Invalid.name(), arg);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> Object execute(String actionCode, T arg) {
        ActionContext actionContext = new ActionContext(requestContext, apiName, actionCode);
        String payload = serializerManager.getSerializer(actionContext.getRequestContext().getContentType()).encode(arg);
        Action action = actionLocateService.locateAction(actionContext, payload);
        return action.act(arg);
    }

}
