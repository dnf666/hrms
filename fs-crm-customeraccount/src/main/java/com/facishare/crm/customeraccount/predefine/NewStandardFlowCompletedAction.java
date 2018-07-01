package com.facishare.crm.customeraccount.predefine;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.facishare.crm.customeraccount.constants.SystemConstants;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.StopWatch;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO 通过判断lifestatus状态临时解决重复提交问题
 *
 * @author zhongxing
 */
@Slf4j
public class NewStandardFlowCompletedAction extends StandardFlowCompletedAction {
    protected IObjectData resultData;

    @Override
    protected Result doAct(Arg arg) {
        /**
         * 1. 取数据信息
         * 2. 根据审批结果更新数据的状态，其实就是通过状态时，就更新为已生效状态 或  作废 状态
         * 3. 如何作废成功 则执行作废
         * 5. 记录日志信息
         * 6. 触发工作流
         */
        log.debug("Entering StandardFlowCompletedAction,arg:{}", arg);
        StopWatch stopWatch = StopWatch.create("FlowComplatedAction" + arg.getDataId());
        String dataId = arg.getDataId();
        String apiName = arg.getDescribeApiName();
        IObjectData data = serviceFacade.findObjectData(arg.getUser(), dataId, apiName);
        ObjectLifeStatus lifeStatus = ObjectDataExt.of(data).getLifeStatus();
        if (!Sets.newHashSet(ObjectLifeStatus.UNDER_REVIEW, ObjectLifeStatus.IN_CHANGE).contains(lifeStatus)) {
            log.info("data={}", data);
            throw new ValidateException("审批已处理");
        }
        stopWatch.lap("find_data_status");
        List<String> fieldsProjection = ObjectDataExt.of(data).modifyObjectDataWhenApprovalFlowFinish(arg.getApprovalFlowTriggerType(), arg.isPass());

        Result result = new Result(true);
        if (fieldsProjection.isEmpty()) {
            return result;
        }
        if (RequestUtil.isFromInner(actionContext)) {
            data.set(SystemConstants.Field.LockStatus.apiName, SystemConstants.LockStatus.UnLock.value);
        }
        resultData = serviceFacade.updateObjectData(arg.getUser(), data);
        stopWatch.lap("update_data_status");
        if (arg.isPass()) {
            List<IObjectData> dataToTriggerWorkFlow = Lists.newArrayList();
            if (arg.getApprovalFlowTriggerType().equals(ApprovalFlowTriggerType.INVALID)) {
                this.detailDataList = this.invalidDataAndDetailData(data);
                dataToTriggerWorkFlow.addAll(this.detailDataList);
                this.detailDataList.forEach(objectData -> {
                    if (objectData.getId().equals(arg.getDataId())) {
                        resultData = objectData;
                    }
                });
            }

            if (arg.getApprovalFlowTriggerType().equals(ApprovalFlowTriggerType.UPDATE)) {
                Object callbackData = arg.getCallbackData();
                Map<String, Object> updatedFieldMap = (Map) callbackData;
                if (Objects.nonNull(updatedFieldMap)) {
                    ObjectDataExt.of(data).toMap().putAll(updatedFieldMap);
                    this.serviceFacade.updateWithMap(arg.getUser(), data, updatedFieldMap);
                }

                this.serviceFacade.log(this.actionContext.getUser(), EventType.MODIFY, ActionType.UPDATE_OBJ, this.objectDescribe, data);
                this.serviceFacade.sendActionMq(this.actionContext.getUser(), Lists.newArrayList(new IObjectData[] { data }), ObjectAction.UPDATE);
            }
            dataToTriggerWorkFlow.add(data);
            if (CollectionUtils.notEmpty(dataToTriggerWorkFlow)) {
                dataToTriggerWorkFlow.forEach(toStartWorkFlowData -> {
                    this.serviceFacade.startWorkFlow(toStartWorkFlowData.getId(), apiName, arg.getApprovalFlowTriggerType().getTriggerTypeCode(), arg.getUser(), (Map) (arg.getApprovalFlowTriggerType().equals(ApprovalFlowTriggerType.UPDATE) ? (Map) arg.getCallbackData() : Maps.newHashMap()));
                });
                stopWatch.lap("trigger_process");
            }
        }
        stopWatch.log();

        return result;
    }
}
