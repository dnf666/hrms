package com.facishare.crm.outbounddeliverynote.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteErrorCode;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteException;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteStockManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/4/10
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteInvalidAction extends StandardInvalidAction {

    private OutboundDeliveryNoteStockManager outboundDeliveryNoteStockManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteStockManager.class);;
    private OutboundDeliveryNoteManager outboundDeliveryManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteManager.class);;
    private Map<String, Map<String, BigDecimal>> noteIdProductAmountMap = new HashMap<>();
    private List<IObjectData> productList = Lists.newArrayList();

    private List<IObjectData> iObjectDataList = Lists.newArrayList();

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        boolean existInValid = objectDataList.stream().anyMatch(objectData -> !Objects.equals(objectData.getRecordType(), OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName));
        if (existInValid) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "作废失败，只能作废预设业务类型数据");
        }

        iObjectDataList = objectDataList.stream().filter(iObjectData -> Objects.equals(iObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class), SystemConstants.LifeStatus.Normal.value)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(iObjectDataList)) {
            List<String> ids = iObjectDataList.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            productList = outboundDeliveryManager.findProductByIds(actionContext.getUser(), ids);

            noteIdProductAmountMap = outboundDeliveryManager.findProductMapByIds(actionContext.getUser(), ids, productList);
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        log.debug("OutboundDeliveryNoteBulkInvalidAction.after, arg[{}], actionContext[{}], result[{}]", arg, actionContext, result);

        if (CollectionUtils.isNotEmpty(iObjectDataList)) {
            List<String> outboundDeliveryNoteIds = iObjectDataList.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());

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
        return result;
    }
}
