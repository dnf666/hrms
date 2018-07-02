package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.action.CommonFlowStartCallbackAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liangk
 * @date 2018/4/12
 */
@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteFlowStartCallbackAction extends CommonFlowStartCallbackAction {
    private GoodsReceivedNoteManager goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);
    private GoodsReceivedNoteProductModel.BuildProductResult productVO;
    private String oldLifeStatus;
    private String dataId;
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        log.info("GoodsReceivedNoteFlowStartCallbackAction before. arg[{}}", arg);
        dataId = arg.getDataId();
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        productVO = goodsReceivedNoteManager.buildGoodsReceivedNoteProduct(actionContext.getUser(), objectData);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        User user = actionContext.getUser();

        if (arg.isTriggerSynchronous()) {
            return result;
        }

        IObjectData objectData = goodsReceivedNoteManager.getObjectDataById(this.actionContext.getUser(), dataId);
        log.debug("GoodsReceivedNoteFlowStartCallbackAction.after, arg[{}], actionContext[{}], resultData[{}]", arg, actionContext, objectData);

        String newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName).toString();
        //无审批 扣减实际库存 normal->invalid
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Normal.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {
            log.info("GoodsReceivedNoteFlowStartCallbackAction.minusRealStock, objectData[{}]", objectData);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.INVALID.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                    .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value).build();
            //校验实际库存是否大于入库数量，如果不大于就记录日志
            goodsReceivedNoteManager.invalidAfter(user, objectData, productVO);
            goodsReceivedNoteManager.minusRealStock(user, objectData, productVO, stockOperateInfo);
        }

        //有审批 增加冻结库存 normal->inChange
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Normal.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.InChange.value)) {
            log.info("GoodsReceivedNoteFlowStartCallbackAction.addBlockedStock, objectData[{}]", objectData);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.INVALID.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.InChange.value)
                    .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                    .operateResult(StockOperateResultEnum.IN_APPROVAL.value).build();

            goodsReceivedNoteManager.addBlockedStock(user, objectData, productVO, stockOperateInfo);
        }
        return result;
    }
}
