package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Sets;

import com.facishare.crm.sfa.predefine.service.PartnerService;
import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.paas.appframework.common.util.ParallelUtils;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.IRule;
import com.facishare.paas.metadata.util.SpringUtil;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by luohuilong on 2017/12/5.
 */
@Slf4j
public class SFAAddAction extends SFAObjectSaveAction {
    protected PartnerService partnerService = SpringUtil.getContext().getBean(PartnerService.class);
    protected IObjectData argObjectData;

    @Override
    protected void before(Arg arg) {
        this.argObjectData = arg.getObjectData().toObjectData();
        super.before(arg);
        //校验规则校验
        validateValidationRules(
                arg.getObjectData().toObjectData(),
                ObjectDataDocument.ofDataMap(arg.getDetails()),
                IRule.CREATE);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        doChangePartnerAndOwner(arg);
        return result;
    }

    protected void doChangePartnerAndOwner(Arg arg) {
        //更换合作伙伴，获取外部企业和外部负责人填充
        try {
            ParallelUtils.ParallelTask parallelTask = ParallelUtils.createParallelTask();
            parallelTask.submit(() -> {
                String partnerId = this.argObjectData.get(PartnerConstants.FIELD_PARTNER_ID, String.class);
                if (Objects.nonNull(partnerId)) {
                    partnerService.changePartnerAndOwner(this.actionContext.getUser(), this.objectDescribe.getApiName(), Sets.newHashSet(this.objectData.getId())
                            , partnerId);
                }
            });
            parallelTask.run();
        } catch (Exception ex) {
            log.error("SFAAddAction execute changePartnerAndOwner error,arg {}", arg, ex);
        }
    }

}
