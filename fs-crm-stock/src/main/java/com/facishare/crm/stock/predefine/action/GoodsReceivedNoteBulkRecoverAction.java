package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 18/01/2018
 */
@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteBulkRecoverAction extends StandardBulkRecoverAction{

    private GoodsReceivedNoteManager goodsReceivedNoteManager;

    @Override
    public void before(Arg arg) {
        //"调拨入库"类型的入库单不允许恢复
        List<String> ids = arg.getIdList();
        String objApiName = arg.getObjectDescribeAPIName();
        List<IObjectData> objectDataList = serviceFacade.findObjectDataByIds(actionContext.getTenantId(), ids, objApiName);

        for (IObjectData objectData : objectDataList) {
            String recordType = objectData.getRecordType();
            if (recordType.equals(GoodsReceivedNoteRecordTypeEnum.RequisitionIn.apiName)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "入库单" + objectData.getName() + "为调拨入库类型，不允许恢复");
            }
        }
        super.before(arg);
    }

    @Override
    public Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);

        List<String> ids = arg.getIdList();
        String objApiName = arg.getObjectDescribeAPIName();
        List<IObjectData> goodsReceivedNotes = serviceFacade.findObjectDataByIds(actionContext.getTenantId(), ids, objApiName);

        //保留正常状态的数据
        List<IObjectData> goodsReceivedNoteDatas = goodsReceivedNotes.stream().filter(objectData ->
                SystemConstants.LifeStatus.Normal.value.equals(objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class))).collect(Collectors.toList());

        log.debug("GoodsReceivedNoteBulkRecoverAction.after, objectDataToNormal[{}], arg[{}], result[{}]", goodsReceivedNoteDatas, arg, result);

        //批量恢复产品库存
        for (IObjectData objectData : goodsReceivedNoteDatas) {
            GoodsReceivedNoteProductModel.BuildProductResult productVo =
                    goodsReceivedNoteManager.buildGoodsReceivedNoteProduct(actionContext.getUser(), objectData);
            if (null == productVo) {
                log.warn("goodsReceivedNoteManager.minusRealStock, productVo is null");
                continue;
            }
            log.debug("goodsReceivedNoteManager.minusRealStock, objectData[{}], productVo[{}]", objectData, productVo);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.RECOVER.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value).build();

            goodsReceivedNoteManager.insertOrUpdateStock(actionContext.getUser(), objectData, productVo, stockOperateInfo);
        }
        return result;
    }
}
