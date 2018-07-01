package com.facishare.crm.outbounddeliverynote.predefine.service.impl;

import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteInitManager;
import com.facishare.crm.outbounddeliverynote.predefine.service.OutboundDeliveryNoteService;
import com.facishare.crm.outbounddeliverynote.predefine.service.dto.OutboundDeliveryNoteType;
import com.facishare.crm.outbounddeliverynote.predefine.service.model.CommonModel;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author linchf
 * @date 2018/3/20
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
@Component
public class OutboundDeliveryNoteServiceImpl implements OutboundDeliveryNoteService {

    @Resource
    private OutboundDeliveryNoteInitManager outboundDeliveryNoteInitManager;

    @Resource
    private StockManager stockManager;

    @Override
    public OutboundDeliveryNoteType.EnableOutboundDeliveryNoteResult enableOutboundDeliveryNote(ServiceContext serviceContext) {
        OutboundDeliveryNoteType.EnableOutboundDeliveryNoteResult result = new OutboundDeliveryNoteType.EnableOutboundDeliveryNoteResult();

        Boolean isSuccess = false;

        try {
            isSuccess = outboundDeliveryNoteInitManager.init(serviceContext.getUser());
        } catch (Exception e) {
            log.warn("outboundDeliveryNote init failed! user[{}]", serviceContext.getUser());
        }

        if (isSuccess) {
            result.setEnableStatus(OutboundDeliveryNoteType.OutboundDeliveryNoteSwitchEnum.ENABLE.getStatus());
            result.setMessage(OutboundDeliveryNoteType.OutboundDeliveryNoteSwitchEnum.ENABLE.getLabel());
        } else {
            result.setEnableStatus(OutboundDeliveryNoteType.OutboundDeliveryNoteSwitchEnum.FAILED.getStatus());
            result.setMessage(OutboundDeliveryNoteType.OutboundDeliveryNoteSwitchEnum.FAILED.getLabel());
        }
        return result;
    }

    @Override
    public CommonModel.Result addFuncAccess(ServiceContext serviceContext) {
        CommonModel.Result result = new CommonModel.Result();
        String tenantIds = ConfigCenter.ENABLE_STOCK_TENANT_IDS;
        if (Objects.equals(ConfigCenter.SUPPER_ADMIN_ID, serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            if (!StringUtils.isBlank(tenantIds)) {
                List<String> tenantIdList = Arrays.asList(tenantIds.split(";"));

                tenantIdList.forEach(tenantId -> {
                    if (stockManager.isStockEnable(tenantId)) {
                        log.info("addFuncAccess start. tenantId[{}]", tenantId);
                        outboundDeliveryNoteInitManager.addFuncAccess(tenantId);
                        log.info("addFuncAccess end. tenantId[{}]", tenantId);
                    }
                });
            }
        } else {
            log.warn("addFuncAccess failed. authorized failed. user[{}], tenantIds[{}]", serviceContext.getUser(), tenantIds);
            result.setResult("addFuncAccess failed. authorized failed.");
        }

        return result;
    }
}
