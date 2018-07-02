package com.facishare.crm.stock.predefine.action;


import com.facishare.crm.action.CommonFlowCompletedAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.appframework.flow.ApprovalFlowTriggerType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteFlowCompletedAction extends CommonFlowCompletedAction {

    private String newLifeStatus = null;
    private String oldLifeStatus = null;

    private GoodsReceivedNoteProductModel.BuildProductResult productVo;

    private GoodsReceivedNoteManager goodsReceivedNoteManager;

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        this.goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);

        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        productVo = goodsReceivedNoteManager.buildGoodsReceivedNoteProduct(actionContext.getUser(), objectData);
        log.debug("GoodsReceivedNoteFlowCompletedAction.before, arg[{}], oldLifeStatus[{}], productVo[{}], objectData[{}]",
                arg, oldLifeStatus, productVo, objectData);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        //查询数据，获取新生命状态
        User user = actionContext.getUser();
        IObjectData objectData = this.serviceFacade.findObjectDataIncludeDeleted(user, arg.getDataId(), GoodsReceivedNoteConstants.API_NAME);

        log.debug("GoodsReceivedNoteFlowCompletedAction.after, arg[{}], result[{}], IObjectData[{}]", arg, result, objectData);

        newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        //状态流转：underReview -> normal
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.UnderReview.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Normal.value)) {
            log.info("GoodsReceivedNoteFlowCompletedAction.after addRealStock, oldLifeStatus[{}], newLifeStatus[{}], productVo[{}]",
                    oldLifeStatus, newLifeStatus, productVo);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.get("_id").toString()).operateType(StockOperateTypeEnum.ADD_FLOW_COMPLETE.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.UnderReview.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value).build();

            //增加实际库存
            goodsReceivedNoteManager.insertOrUpdateStock(user, objectData, productVo, stockOperateInfo);
        }

        //状态流转：inChange -> invalid
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.InChange.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {
            log.info("GoodsReceivedNoteFlowCompletedAction.after minusBlockedRealStock, oldLifeStatus[{}], newLifeStatus[{}], productVo[{}]",
                    oldLifeStatus, newLifeStatus, productVo);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.get("_id").toString()).operateType(StockOperateTypeEnum.INVALID_FLOW_COMPLETE.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.InChange.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                    .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value).build();

            //校验实际库存是否大于入库数量，如果不大于就记录日志
            goodsReceivedNoteManager.invalidAfter(user, objectData, productVo);
            //扣减实际库存、扣减冻结库存
            goodsReceivedNoteManager.minusBlockedRealStock(user, objectData, productVo, stockOperateInfo);
        }


        //状态流转：inChange -> normal
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.InChange.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Normal.value) &&
                Objects.equals(arg.getTriggerType(), ApprovalFlowTriggerType.INVALID.getTriggerTypeCode())) {
            log.info("GoodsReceivedNoteFlowCompletedAction.after minusBlockedStock, oldLifeStatus[{}], newLifeStatus[{}], productVo[{}]",
                    oldLifeStatus, newLifeStatus, productVo);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.get("_id").toString()).operateType(StockOperateTypeEnum.INVALID_FLOW_COMPLETE.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.InChange.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                    .operateResult(StockOperateResultEnum.REJECT.value).build();

            //扣减冻结库存
            goodsReceivedNoteManager.minusBlockedStock(user, objectData, productVo, stockOperateInfo);
        }
        return result;
    }
}
