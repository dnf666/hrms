package com.facishare.crm.outbounddeliverynote.predefine.action;

import com.facishare.crm.action.CommonAddAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteErrorCode;
import com.facishare.crm.outbounddeliverynote.exception.OutboundDeliveryNoteException;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteStockManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;

import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/20
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteAddAction extends CommonAddAction {

    private OutboundDeliveryNoteStockManager outboundDeliveryNoteStockManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteStockManager.class);
    private OutboundDeliveryNoteManager outboundDeliveryNoteManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteManager.class);


    @Override
    protected void before(Arg arg) {
        log.info("OutboundDeliveryNoteAddAction.Arg[{}]", arg);
        outboundDeliveryNoteManager.modifyArg(actionContext.getTenantId(), arg);
        super.before(arg);
        String recordType = arg.getObjectData().toObjectData().getRecordType();
        if (!Objects.equals(recordType, OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName)) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "非预设业务类型的出库单不能新建");
        }

        List<IObjectData> productList = arg.getDetails().get(OutboundDeliveryNoteProductConstants.API_NAME).stream().map(ObjectDataDocument::toObjectData).collect(Collectors.toList());

        //校验出库单数量
        outboundDeliveryNoteStockManager.checkProducts(productList);

        //校验可用库存
        outboundDeliveryNoteStockManager.checkAvailableStock(actionContext.getUser(), arg.getObjectData().toObjectData(), productList);
    }


    @Override
    protected Result after(Arg arg, Result result) {
        log.debug("OutboundDeliveryNoteAddActionAfter. result[{}]", result);
        result = super.after(arg, result);
        log.debug("OutboundDeliveryNoteAddAction.after! arg[{}], result[{}]", arg, result);

        IObjectData note = result.getObjectData().toObjectData();
        List<IObjectData> productList = arg.getDetails().get(OutboundDeliveryNoteProductConstants.API_NAME).stream().map(ObjectDataDocument::toObjectData).collect(Collectors.toList());


        String lifeStatus = note.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("OutboundDeliveryNoteAddAction.after! LifeStatus[{}], objectData[{}], result[{}]", lifeStatus, objectData, result);

        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(note.getId()).operateType(StockOperateTypeEnum.ADD.value)
                .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                .afterLifeStatus(lifeStatus)
                .operateObjectType(StockOperateObjectTypeEnum.OUTBOUND_DELIVERY_NOTE.value)
                .build();

        if (Objects.equals(lifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            //ineffective -> normal 扣减实际库存
            stockOperateInfo.setOperateResult(StockOperateResultEnum.PASS.value);
            outboundDeliveryNoteStockManager.minusRealStock(actionContext.getUser(), note, productList, stockOperateInfo);

         } else if (Objects.equals(lifeStatus, SystemConstants.LifeStatus.UnderReview.value)) {
            //ineffective -> underReview  增加冻结库存
            stockOperateInfo.setOperateResult(StockOperateResultEnum.IN_APPROVAL.value);
            outboundDeliveryNoteStockManager.addBlockedStock(actionContext.getUser(), note, productList, false, stockOperateInfo);
        }
        return result;
    }
}
