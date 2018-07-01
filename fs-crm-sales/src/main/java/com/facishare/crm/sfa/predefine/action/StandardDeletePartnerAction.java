package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Lists;

import com.facishare.crm.sfa.predefine.service.PartnerService;
import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.appframework.log.dto.LogInfo;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardDeletePartnerAction extends BaseUpdatePartnerAction<StandardDeletePartnerAction.Arg, BaseUpdatePartnerAction.Result> {
    protected StandardDeletePartnerAction.Result result = StandardDeletePartnerAction.Result.builder().errorCode("0").message("移除合作伙伴成功").build();
    protected PartnerService partnerService = SpringUtil.getContext().getBean(PartnerService.class);

    @Override
    protected List<String> getFuncPrivilegeCodes() {
        return Lists.newArrayList(ObjectAction.DELETE_PARTNER.getActionCode());
    }

    @Override
    protected List<String> getDataPrivilegeIds(Arg arg) {
        return arg.getDataIds();
    }

    @Override
    protected void before(Arg arg) {
        log.info("StandardDeletePartnerAction before arg {}", arg);
        super.before(arg);
        if (CollectionUtils.isEmpty(arg.getDataIds())) {
            throw new ValidateException("数据id不能为空");
        }
        objectDataList = this.findByIdsIncludeLookUpName(this.actionContext.getUser(), this.objectDescribe.getApiName(), arg.getDataIds());
        if (CollectionUtils.isEmpty(objectDataList)) {
            throw new ValidateException("数据不存在");
        }
        objectDataList.forEach(k -> {
            if (StringUtils.isBlank(k.get(PartnerConstants.FIELD_PARTNER_ID, String.class))) {
                throw new ValidateException("数据不存在合伙伙伴，无需移除");
            }
        });
    }

    @Override
    protected Result doAct(Arg arg) {
        if (!result.isSuccess()) {
            return result;
        }
        //移除合作伙伴id
        stopWatch.lap("StandardDeletePartnerAction bulkChangePartner update partner start ");
        partnerService.removePartner(this.actionContext.getUser(), this.objectDescribe.getApiName(),
                objectDataList.stream().map(k -> k.getId()).collect(Collectors.toSet()));
        stopWatch.lap("StandardDeletePartnerAction bulkChangePartner update partner finished ");
        return result;
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        this.logDeletePartner(this.actionContext.getUser(), EventType.DELETE, ActionType.DELETE_PARTNER, objectDataList, objectDescribe);
        // TODO: 2018/4/10 给下游发送消息通知
        // TODO: 2018/4/11 如果数据已有合作伙伴，可能需要feed通知旧的合作伙伴

        return result;
    }

    protected void logDeletePartner(User user, EventType eventType, ActionType actionType,
                                    List<IObjectData> oldDatas, IObjectDescribe objectDescribe) {
        for (IObjectData data : oldDatas) {
            this.logDeletePartner(user, eventType, actionType, data, objectDescribe);
        }
    }

    protected void logDeletePartner(User user, EventType eventType, ActionType actionType,
                                    IObjectData data, IObjectDescribe objectDescribe) {
        String partnerName = getPartnerName(data);

        List<LogInfo.LintMessage> textMsg = new ArrayList<>();
        textMsg.add(new LogInfo.LintMessage("，在" + objectDescribe.getDisplayName(), "", ""));
        textMsg.add(new LogInfo.LintMessage(data.getName(), data.getId(), objectDescribe.getApiName()));
        textMsg.add(new LogInfo.LintMessage("中移除" + partnerName, "", ""));
        LogInfo.ObjectSnapshot snapshot = LogInfo.ObjectSnapshot.builder().textMsg(textMsg).build();
        serviceFacade.logWithCustomMessage(user, eventType, actionType, objectDescribe, data, snapshot.getMessage());
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
