package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Lists;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.common.util.ObjectAction;
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
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardChangePartnerAction extends BaseUpdatePartnerAction<StandardChangePartnerAction.Arg, BaseUpdatePartnerAction.Result> {
    protected StandardChangePartnerAction.Result result = StandardChangePartnerAction.Result.builder().errorCode("0").message("更换合作伙伴成功").build();
    protected IObjectData partnerObjData = null;

    @Override
    protected List<String> getFuncPrivilegeCodes() {
        return Lists.newArrayList(ObjectAction.CHANGE_PARTNER.getActionCode());
    }

    @Override
    protected List<String> getDataPrivilegeIds(Arg arg) {
        return arg.getDataIds();
    }

    @Override
    protected void before(Arg arg) {
        log.info("StandardChangePartnerAction before arg {}", arg);
        super.before(arg);
        if (CollectionUtils.isEmpty(arg.getDataIds())) {
            throw new ValidateException("数据id不能为空");
        }
        if (StringUtils.isBlank(arg.getPartnerId())) {
            throw new ValidateException("合作伙伴id不能为空");
        }
        objectDataList = this.findByIdsIncludeLookUpName(this.actionContext.getUser(), this.objectDescribe.getApiName(), arg.getDataIds());
        if (CollectionUtils.isEmpty(objectDataList)) {
            throw new ValidateException("数据不存在");
        }
        partnerObjData = serviceFacade.findObjectData(this.actionContext.getUser(), arg.getPartnerId(), Utils.PARTNER_API_NAME);
    }

    @Override
    protected Result doAct(Arg arg) {
        if (!result.isSuccess()) {
            return result;
        }
        //更改数据的合作伙伴id
        stopWatch.lap("StandardChangePartnerAction BulkChangePartner update partner start ");
        partnerService.changePartnerAndOwner(this.actionContext.getUser(),
                this.objectDescribe.getApiName(), objectDataList.stream().map(k -> k.getId()).collect(Collectors.toSet()),
                arg.getPartnerId());
        stopWatch.lap("StandardChangePartnerAction BulkChangePartner update partner finished ");
        return result;
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        this.logByActionType(this.actionContext.getUser(), EventType.MODIFY, ActionType.CHANGE_PARTNER, objectDataList, partnerObjData, objectDescribe);
        // TODO: 2018/4/10 给下游发送消息通知
        // TODO: 2018/4/11 如果数据已有合作伙伴，可能需要feed通知旧的合作伙伴
        return result;
    }

    protected void logByActionType(User user, EventType eventType, ActionType actionType,
                                   List<IObjectData> dataList, IObjectData partnerObjData, IObjectDescribe objectDescribe) {
        for (IObjectData data : dataList) {
            this.logChangePartner(user, eventType, actionType, data, partnerObjData, objectDescribe);
        }
    }

    protected void logChangePartner(User user, EventType eventType, ActionType actionType,
                                    IObjectData oldData, IObjectData partnerObjData, IObjectDescribe objectDescribe) {
        String partnerName = partnerObjData.getName();
        String oldPartnerName = getPartnerName(oldData);

        List<LogInfo.LintMessage> textMsg = new ArrayList<>();
        StringBuilder sb = new StringBuilder(" , ");
        if (StringUtils.isNotEmpty(oldPartnerName)) {
            sb.append(String.format("原合作伙伴 %s , %s;",
                    oldPartnerName, "移除合作伙伴"));
        }
        sb.append(String.format("新合作伙伴: %s", partnerName));
        textMsg.add(new LogInfo.LintMessage(sb.toString(), oldData.getId(), objectDescribe.getApiName()));
        LogInfo.ObjectSnapshot snapshot = LogInfo.ObjectSnapshot.builder().textMsg(textMsg).build();
        serviceFacade.logWithCustomMessage(user, eventType, actionType, objectDescribe, oldData, snapshot.getMessage());
    }

    @Data
    @NoArgsConstructor
    static class Arg {
        //数据ID列表
        private List<String> dataIds;

        //合作伙伴数据id
        private String partnerId;
    }
}
