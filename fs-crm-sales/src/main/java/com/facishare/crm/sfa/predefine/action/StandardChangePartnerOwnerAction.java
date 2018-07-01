package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Lists;

import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.paas.appframework.common.service.dto.QueryUserInfoByIds;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.appframework.log.dto.LogInfo;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardChangePartnerOwnerAction extends BaseUpdatePartnerAction<StandardChangePartnerOwnerAction.Arg, BaseUpdatePartnerAction.Result> {
    protected StandardChangePartnerOwnerAction.Result result = StandardChangePartnerOwnerAction.Result.builder().errorCode("0").message("更换外部负责人").build();

    @Override
    protected List<String> getFuncPrivilegeCodes() {
        return Lists.newArrayList(ObjectAction.CHANGE_PARTNER_OWNER.getActionCode());
    }

    @Override
    protected List<String> getDataPrivilegeIds(Arg arg) {
        return arg.getDataIds();
    }

    @Override
    protected void before(Arg arg) {
        log.info("StandardChangePartnerOwnerAction before arg {}", arg);
        super.before(arg);
        if (CollectionUtils.isEmpty(arg.getDataIds())) {
            throw new ValidateException("数据id不能为空");
        }
        if (StringUtils.isBlank(arg.getOwnerId())) {
            throw new ValidateException("外部负责人不能为空");
        }
        objectDataList = this.findByIdsIncludeLookUpName(this.actionContext.getUser(), this.objectDescribe.getApiName(), arg.getDataIds());
        if (CollectionUtils.isEmpty(objectDataList)) {
            throw new ValidateException("数据不存在");
        }
        objectDataList.forEach(k -> {
            if (StringUtils.isBlank(k.get(PartnerConstants.FIELD_PARTNER_ID, String.class))) {
                throw new ValidateException("数据不存在合伙伙伴，不能添加外部负责人");
            }
        });
    }

    @Override
    protected Result doAct(Arg arg) {
        if (!result.isSuccess()) {
            return result;
        }
        //更换外部负责人id
        stopWatch.lap("StandardChangePartnerOwnerAction bulkChangePartner update partner start ");
        IObjectData objectData = objectDataList.get(0);
        partnerService.changePartnerAndOwner(this.actionContext.getUser(),
                this.objectDescribe.getApiName(), objectDataList.stream().map(k -> k.getId()).collect(Collectors.toSet()),
                objectData.get(PartnerConstants.FIELD_PARTNER_ID, String.class), Long.valueOf(arg.getOwnerId()));
        stopWatch.lap("StandardChangePartnerOwnerAction bulkChangePartner update partner finished ");
        return result;
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        // TODO: 2018/4/10 待确认，sfa对象不需要在此处记录吧？记录操作日志，新增修改合作伙伴操作类型，新增或更改
        try {
            ParallelUtils.ParallelTask parallelTask = ParallelUtils.createParallelTask();
            parallelTask.submit(() -> {
                this.logChangePartnerOwner(this.actionContext.getUser(), EventType.MODIFY, ActionType.CHANGE_PARTNER_OWNER, objectDataList, arg.getOwnerId(), objectDescribe);
            });
            parallelTask.run();
        } catch (Exception ex) {
            log.error("StandardChangePartnerOwnerAction execute logChangePartnerOwner error,arg {}", arg, ex);
        }
        // TODO: 2018/4/10 给下游发送消息通知
        // TODO: 2018/4/11 如果数据已有合作伙伴，可能需要feed通知旧的合作伙伴


        return result;
    }

    protected void logChangePartnerOwner(User user, EventType eventType, ActionType actionType,
                                         List<IObjectData> oldDataList, String newOwnerId, IObjectDescribe objectDescribe) {

        List<String> outOwnerIdList = (List<String>) oldDataList.stream().flatMap(k -> k.getOutOwner().stream()).collect(Collectors.toList());
        outOwnerIdList.add(newOwnerId);
        List<QueryUserInfoByIds.UserInfo> outOwnerUserInfoList = serviceFacade.getUserNameByIds(user.getTenantId(), user.getUserId(), outOwnerIdList);
        Map<String, String> outOwnerMap = outOwnerUserInfoList.stream().collect(Collectors.toMap(k -> k.getId(), k -> k.getName(), (k, v) -> v));
        for (IObjectData data : oldDataList) {
            String oldOwnerId = data.getOutOwner() != null && CollectionUtils.isNotEmpty(data.getOutOwner()) ? String.valueOf(data.getOutOwner().get(0)) : null;
            this.logChangePartnerOwner(user, eventType, actionType, data, oldOwnerId != null ? outOwnerMap.getOrDefault(oldOwnerId, "外部负责人") : null
                    , outOwnerMap.getOrDefault(newOwnerId, "外部负责人"), objectDescribe);
        }
    }

    protected void logChangePartnerOwner(User user, EventType eventType, ActionType actionType, IObjectData objectData,
                                         String oldOwnerName, String newOwnerName, IObjectDescribe objectDescribe) {

        List<LogInfo.LintMessage> textMsg = new ArrayList<>();
        StringBuilder sb = new StringBuilder(" , ");
        if (StringUtils.isNotEmpty(oldOwnerName)) {
            sb.append(String.format("原外部负责人 %s , %s;",
                    oldOwnerName, "移除外部负责人"));
        }
        sb.append(String.format("新外部负责人: %s", newOwnerName));
        textMsg.add(new LogInfo.LintMessage(sb.toString(), objectData.getId(), objectDescribe.getApiName()));

        LogInfo.ObjectSnapshot snapshot = LogInfo.ObjectSnapshot.builder().textMsg(textMsg).build();
        serviceFacade.logWithCustomMessage(user, eventType, actionType, objectDescribe, objectData, snapshot.getMessage());
    }

    @Data
    @NoArgsConstructor
    static class Arg {
        //数据ID列表
        private List<String> dataIds;

        //外部负责人数据id
        private String ownerId;
    }

}
