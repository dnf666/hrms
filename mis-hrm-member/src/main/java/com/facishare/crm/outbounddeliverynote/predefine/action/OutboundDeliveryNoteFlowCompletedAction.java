package com.facishare.crm.outbounddeliverynote.predefine.action;

import com.facishare.crm.action.CommonFlowCompletedAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteStockManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/22
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteFlowCompletedAction extends CommonFlowCompletedAction {
    private OutboundDeliveryNoteStockManager outboundDeliveryNoteStockManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteStockManager.class);
    private OutboundDeliveryNoteManager outboundDeliveryNoteManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteManager.class);

    private String newLifeStatus = null;
    private String oldLifeStatus = null;

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
    }


    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        IObjectData note = this.serviceFacade.findObjectDataIncludeDeleted(actionContext.getUser(), arg.getDataId(), OutboundDeliveryNoteConstants.API_NAME);

        newLifeStatus = note.get(SystemConstants.Field.LifeStatus.apiName, String.class);

        List<IObjectData> productList = outboundDeliveryNoteManager.findProductByIds(actionContext.getUser(), Arrays.asList(note.getId()));
        if (Objects.equals(oldLifeStatus, SystemConstants.LifeStatus.UnderReview.value) &&
                Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Ineffective.value)) {
            //under_review -> ineffective 扣减冻结库存
            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(note.getId()).operateType(StockOperateTypeEnum.ADD.value)
                    .beforeLifeStatus(oldLifeStatus)
                    .afterLifeStatus(newLifeStatus)
                    .operateObjectType(StockOperateObjectTypeEnum.OUTBOUND_DELIVERY_NOTE.value)
                    .operateResult(StockOperateResultEnum.REJECT.value)
                    .build();

            outboundDeliveryNoteStockManager.minusBlockedStock(actionContext.getUser(), note, productList, stockOperateInfo);
        }

        if (Objects.equals(oldLifeStatus, SystemConstants.LifeStatus.UnderReview.value) &&
                Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            //under_review -> normal 扣减冻结库存 扣减实际库存
            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(note.getId()).operateType(StockOperateTypeEnum.ADD.value)
                    .beforeLifeStatus(oldLifeStatus)
                    .afterLifeStatus(newLifeStatus)
                    .operateObjectType(StockOperateObjectTypeEnum.OUTBOUND_DELIVERY_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value)
                    .build();

            outboundDeliveryNoteStockManager.minusBlockedAndRealStock(actionContext.getUser(), note, productList, stockOperateInfo);
        }

        if (Objects.equals(oldLifeStatus, SystemConstants.LifeStatus.InChange.value) &&
                Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Invalid.value)) {
            //in_change -> invalid 增加实际库存
            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(note.getId()).operateType(StockOperateTypeEnum.INVALID.value)
                    .beforeLifeStatus(oldLifeStatus)
                    .afterLifeStatus(newLifeStatus)
                    .operateObjectType(StockOperateObjectTypeEnum.OUTBOUND_DELIVERY_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value)
                    .build();
            Map<String, BigDecimal> productAmountMap = productList.stream().collect(Collectors.toMap(product -> product.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class),
                    product -> product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class)));
            outboundDeliveryNoteStockManager.addRealStock(actionContext.getUser(), note, productAmountMap, stockOperateInfo);
        }

        return result;
    }
}
