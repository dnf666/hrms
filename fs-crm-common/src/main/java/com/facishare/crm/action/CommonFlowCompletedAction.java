package com.facishare.crm.action;

import com.facishare.crm.exception.CommonBusinessException;
import com.facishare.crm.exception.CommonErrorCode;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class CommonFlowCompletedAction extends StandardFlowCompletedAction {
    /**
     * 是否重复调用
     */
    protected boolean isRepeatedInvoke = false;
    /**
     * 回调处理之前的对象数据
     */
    protected IObjectData currentObjectData;

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        currentObjectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());

        // 重复调用
        isRepeatedInvoke = isRepeatedInvoke(arg);
        log.debug("isRepeatedInvoke[{}]", isRepeatedInvoke);
        if (isRepeatedInvoke) {
            handleRepeatedlyInvoke(arg);
        }
    }

    @Override
    protected Result doAct(Arg arg) {
        if (isRepeatedInvoke) {
            return new StandardFlowCompletedAction.Result(true);
        } else {
            return super.doAct(arg);
        }
    }

    /**
     * 判断是否重复请求(审批流回调时有超时重试机制)
     */
    protected boolean isRepeatedInvoke(Arg arg) {
        return judgeRepeatedInvokeByObjectLifeStatus(arg, ObjectDataExt.of(currentObjectData).getLifeStatus());
    }

    private boolean judgeRepeatedInvokeByObjectLifeStatus(Arg arg, ObjectLifeStatus currentObjectLifeStatus) {
        ObjectLifeStatus completeObjectLifeStatus = null;
        if (Objects.equals(arg.getTriggerType(), ApprovalFlowTriggerType.CREATE.getTriggerTypeCode())) {
            completeObjectLifeStatus = getCreateApprovalCompleteObjectLifeStatus(arg.isPass());
        } else if (Objects.equals(arg.getTriggerType(), ApprovalFlowTriggerType.UPDATE.getTriggerTypeCode())) {
            completeObjectLifeStatus = getUpdateApprovalCompleteObjectLifeStatus(arg.isPass());
        } else if (Objects.equals(arg.getTriggerType(), ApprovalFlowTriggerType.INVALID.getTriggerTypeCode())) {
            completeObjectLifeStatus = getInvalidApprovalCompleteObjectLifeStatus(arg.isPass());
        }
        return Objects.equals(currentObjectLifeStatus, completeObjectLifeStatus);
    }


    private ObjectLifeStatus getInvalidApprovalCompleteObjectLifeStatus(boolean isPass) {
        if (isPass) {
            return ObjectLifeStatus.INVALID;
        } else {
            return ObjectLifeStatus.NORMAL;
        }
    }

    private ObjectLifeStatus getUpdateApprovalCompleteObjectLifeStatus(boolean isPass) {
        if (isPass) {
            return ObjectLifeStatus.NORMAL;
        } else {
            return ObjectLifeStatus.NORMAL;
        }
    }

    private ObjectLifeStatus getCreateApprovalCompleteObjectLifeStatus(boolean isPass) {
        if (isPass) {
            return ObjectLifeStatus.NORMAL;
        } else {
            return ObjectLifeStatus.INEFFECTIVE;
        }
    }

    /**
     * 重复请求处理
     */
    protected void handleRepeatedlyInvoke(Arg arg) {
    }
}
