package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.predefine.service.PartnerService;
import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.paas.appframework.core.model.PreDefineAction;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.DBRecord;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseUpdatePartnerAction<A, Result> extends PreDefineAction<A, Result> {
    protected List<IObjectData> objectDataList = null;
    protected PartnerService partnerService = SpringUtil.getContext().getBean(PartnerService.class);

    protected String getPartnerName(IObjectData objectData) {
        return Optional.ofNullable(objectData.get(PartnerConstants.FIELD_PARTNER_ID + "__r", String.class)).orElse("");
    }

    protected String getPartnerOwnerName(IObjectData objectData) {
        Map outOwner = objectData.get(DBRecord.OUT_OWNER + "__r", Map.class);
        // TODO: 2018/4/13 需要获取外部负责人的名称
        return outOwner != null && outOwner.get("name") != null ? outOwner.get("name").toString() : "外部负责人";
    }

    protected List<IObjectData> findByIdsIncludeLookUpName(User user, String apiName, List<String> ids) {
        return ids.stream().map(id -> serviceFacade.findObjectData(user, id, apiName)).collect(Collectors.toList());
    }

    @Data
    @Builder
    public static class Result {
        private String errorDetail;
        private String errorCode;
        private String message;

        public boolean isSuccess() {
            if ("0".equals(errorCode)) {
                return true;
            }
            return false;
        }
    }
}
