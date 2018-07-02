package com.facishare.crm.outbounddeliverynote.predefine.action;

import com.facishare.crm.action.CommonFlowStartCallbackAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteStockManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author linchf
 * @date 2018/4/12
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteFlowStartCallbackAction extends CommonFlowStartCallbackAction {

    private String oldLifeStatus;
    private OutboundDeliveryNoteStockManager outboundDeliveryNoteStockManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteStockManager.class);;
    private OutboundDeliveryNoteManager outboundDeliveryManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteManager.class);;
    private List<IObjectData> productList = Lists.newArrayList();
    private Map<String, Map<String, BigDecimal>> noteIdProductAmountMap = new HashMap<>();
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        log.info("OutboundDeliveryNoteFlowStartCallbackAction before. arg[{}}", arg);

        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        List<String> ids = Arrays.asList(objectData.getId());
        productList = outboundDeliveryManager.findProductByIds(actionContext.getUser(), ids);
        noteIdProductAmountMap = outboundDeliveryManager.findProductMapByIds(actionContext.getUser(), ids, productList);


    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.info("OutboundDeliveryNoteFlowStartCallbackAction after. objectData[{}}, oldLifeStatus[{}]", objectData, oldLifeStatus);
        result = super.after(arg, result);
        if (!arg.isTriggerSynchronous()) {
            if (Objects.equals(oldLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
                List<String> outboundDeliveryNoteIds = Arrays.asList(objectData.getId());

                List<IObjectData> newObjectDataList = outboundDeliveryManager.findByIds(actionContext.getUser(), outboundDeliveryNoteIds);
                if (CollectionUtils.isNotEmpty(newObjectDataList)) {
                    newObjectDataList.forEach(newObjectData -> {
                        String id = newObjectData.getId();
                        String lifeStatus = newObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                        if (noteIdProductAmountMap.containsKey(id)) {
                            if (Objects.equals(lifeStatus, SystemConstants.LifeStatus.Invalid.value)) {
                                //normal -> invalid 增加实际库存
                                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(id).operateType(StockOperateTypeEnum.INVALID.value)
                                        .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                                        .afterLifeStatus(lifeStatus)
                                        .operateObjectType(StockOperateObjectTypeEnum.OUTBOUND_DELIVERY_NOTE.value)
                                        .operateResult(StockOperateResultEnum.PASS.value)
                                        .build();

                                outboundDeliveryNoteStockManager.addRealStock(actionContext.getUser(), newObjectData, noteIdProductAmountMap.get(id), stockOperateInfo);
                            }
                        }
                    });
                }
            }
        }
        return result;
    }
}
