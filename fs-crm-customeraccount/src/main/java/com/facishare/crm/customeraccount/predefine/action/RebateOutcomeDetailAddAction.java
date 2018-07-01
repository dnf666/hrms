package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.predefine.FlowStandardAddAction;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.flow.ApprovalFlowStartResult;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;

public class RebateOutcomeDetailAddAction extends FlowStandardAddAction {

    @Override
    public Map<String, ApprovalFlowStartResult> startApprovalFlow(List<IObjectData> objectDataList,
                                                                  ApprovalFlowTriggerType approvalFlowTriggerType,
                                                                  Map<String,Map<String,Object>> updatedFieldMap) {
        Map<String, ApprovalFlowStartResult> result = Maps.newHashMap();
        if (!RequestUtil.isFromInner(actionContext)) {
            result = super.startApprovalFlow(objectDataList,approvalFlowTriggerType,updatedFieldMap);
        }
        return result;
    }

    @Override
    protected void modifyObjectDataBeforeCreate(IObjectData objectData, IObjectDescribe describe) {
        String oldLifeStatus = (String) this.arg.getObjectData().get(SystemConstants.Field.LifeStatus.apiName);
        super.modifyObjectDataBeforeCreate(objectData, describe);
        if (StringUtil.isNullOrEmpty(oldLifeStatus)) {
            this.objectData.set(SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Ineffective.value);
        } else {
            this.objectData.set(SystemConstants.Field.LifeStatus.apiName, oldLifeStatus);
        }
    }

    @Override
    protected void before(BaseObjectSaveAction.Arg arg) {
        super.before(arg);
        String fsUserId = actionContext.getUser().getUserId();
        objectData.set(SystemConstants.Field.LockUser.apiName, Lists.newArrayList(fsUserId));
        objectData.set(SystemConstants.Field.Owner.apiName, Lists.newArrayList(fsUserId));
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        return result;
    }

    @Override
    protected void doFunPrivilegeCheck() {

    }
}
