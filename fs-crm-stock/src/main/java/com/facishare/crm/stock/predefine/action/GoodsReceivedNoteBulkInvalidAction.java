package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 19/01/2018
 */
@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteBulkInvalidAction extends StandardBulkInvalidAction {
    private GoodsReceivedNoteManager goodsReceivedNoteManager;

    private Map<String, GoodsReceivedNoteProductModel.BuildProductResult> productMap = Maps.newHashMap();

    private List<IObjectData> iObjectDataList = Lists.newArrayList();

    private static String id = "_id";

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        this.goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);

        //"调拨入库"类型的入库单不允许作废
        for (IObjectData objectData : objectDataList) {
            String recordType = objectData.getRecordType();
            if (recordType.equals(GoodsReceivedNoteRecordTypeEnum.RequisitionIn.apiName)) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "入库单" + objectData.getName() + "为调拨入库类型，不允许作废");
            }
        }

        //保留所有正常状态的入库单数据
        iObjectDataList = objectDataList.stream().filter(iObjectData ->
                iObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class).equals(SystemConstants.LifeStatus.Normal.value)).
                collect(Collectors.toList());

        if (CollectionUtils.isEmpty(iObjectDataList)) {
            return;
        }

        //校验可用库存
        for (IObjectData objectData : iObjectDataList)
        {
            GoodsReceivedNoteProductModel.BuildProductResult productResult =
                    goodsReceivedNoteManager.buildGoodsReceivedNoteProduct(actionContext.getUser(), objectData);
            if (null != productResult) {
                productMap.put(objectData.get(id).toString(), productResult);
                goodsReceivedNoteManager.invalidBefore(actionContext.getUser(), objectData, productResult);
            }
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        if (this.startApprovalFlowAsynchronous) {
            return result;
        }

        User user = actionContext.getUser();
        log.debug("GoodsReceivedNoteBulkInvalidAction.after, arg[{}], actionContext[{}], result[{}]", arg, actionContext, result);

        if (!CollectionUtils.isEmpty(iObjectDataList)) {
            List<String> goodsReceivedNoteIds = iObjectDataList.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());

            List<IObjectData> newObjectDataList = goodsReceivedNoteManager.findByIds(actionContext.getUser(), goodsReceivedNoteIds, GoodsReceivedNoteConstants.API_NAME);

            log.debug("GoodsReceivedNoteBulkInvalidAction.after, arg[{}], actionContext[{}], resultDataList[{}]", arg, actionContext, newObjectDataList);

            GoodsReceivedNoteProductModel.BuildProductResult productVO;
            for (IObjectData objectData : newObjectDataList) {
                String objectDataId = objectData.get(id).toString();
                String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName).toString();
                if (productMap.containsKey(objectDataId)) {
                    productVO = productMap.get(objectDataId);
                    //无审批 扣减实际库存 normal->invalid
                    if (lifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {
                        log.info("goodsReceivedNoteManager.minusRealStock, objectData[{}]", objectData);

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
                    if (lifeStatus.equals(SystemConstants.LifeStatus.InChange.value)) {
                        log.info("goodsReceivedNoteManager.addBlockedStock, objectData[{}]", objectData);

                        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.INVALID.value)
                                .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                                .afterLifeStatus(SystemConstants.LifeStatus.InChange.value)
                                .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                                .operateResult(StockOperateResultEnum.IN_APPROVAL.value).build();

                        goodsReceivedNoteManager.addBlockedStock(user, objectData, productVO, stockOperateInfo);
                    }
                }
            }
        }
        return result;
    }
}
