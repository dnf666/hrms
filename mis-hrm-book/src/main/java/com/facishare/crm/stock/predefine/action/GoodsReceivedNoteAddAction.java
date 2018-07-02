package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.action.CommonAddAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.service.model.GoodsReceivedNoteProductModel;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteAddAction extends CommonAddAction {

    private GoodsReceivedNoteManager goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);

    @Override
    protected void before(Arg arg) {
        goodsReceivedNoteManager.modifyArg(actionContext.getTenantId(), arg);
        log.debug("GoodsReceivedNoteAddAction.before! arg[{}]", arg);
        super.before(arg);
        //校验入库单产品是否为空、入库单产品数量是否为负数
        checkGoodsReceivedNoteProduct(arg);
    }

    private void checkGoodsReceivedNoteProduct(Arg arg) {
        List<ObjectDataDocument> productDocList = arg.getDetails().get(GoodsReceivedNoteProductConstants.API_NAME);

        if (CollectionUtils.isEmpty(productDocList)) {
            log.warn("checkGoodsReceivedNoteProduct failed, arg[{}]", arg);
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "请选择入库产品");
        }

        for (ObjectDataDocument product : productDocList) {
            int isNegative = product.toObjectData().get(GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName, BigDecimal.class).compareTo(BigDecimal.ZERO);

            if (0 > isNegative) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "入库数量不允许为负数");
            }
        }

        if (ConfigCenter.GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM < productDocList.size()) {
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "入库单产品数量不能超过" + ConfigCenter.GOODS_RECEIVED_NOTE_PRODUCT_MAX_NUM + "个");
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        log.debug("GoodsReceivedNoteAddActionAfter. result[{}]", result);
        result = super.after(arg, result);
        log.debug("GoodsReceivedNoteAddAction.after! arg[{}], result[{}]", arg, result);
        String lifeStatus = result.getObjectData().toObjectData().get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("GoodsReceivedNoteAddAction.after! LifeStatus[{}], objectData[{}], result[{}]", lifeStatus, objectData, result);

        //正常状态 加库存
        if (lifeStatus.equals(ObjectLifeStatus.NORMAL.getCode())) {
            GoodsReceivedNoteProductModel.BuildProductResult productVo = goodsReceivedNoteManager.buildGoodsReceivedNoteProduct(actionContext.getUser(), objectData);

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.ADD.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value).build();

            goodsReceivedNoteManager.insertOrUpdateStock(actionContext.getUser(), result.getObjectData().toObjectData(), productVo, stockOperateInfo);
        }
        return result;
    }
}
