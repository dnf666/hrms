package com.facishare.crm.outbounddeliverynote.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
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
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
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
 * @date 2018/3/16
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteBulkRecoverAction extends StandardBulkRecoverAction {

    private OutboundDeliveryNoteStockManager outboundDeliveryNoteStockManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteStockManager.class);;
    private OutboundDeliveryNoteManager outboundDeliveryManager = SpringUtil.getContext().getBean(OutboundDeliveryNoteManager.class);;
    private Map<String, Map<String, BigDecimal>> noteIdProductAmountMap = new HashMap<>();
    private List<IObjectData> objectDataList = Lists.newArrayList();
    private List<IObjectData> productList = Lists.newArrayList();

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        objectDataList = outboundDeliveryManager.findByIds(actionContext.getUser(), arg.getIdList());
        boolean existInValid = objectDataList.stream().anyMatch(objectData -> !Objects.equals(objectData.getRecordType(), OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName));
        if (existInValid) {
            throw new OutboundDeliveryNoteException(OutboundDeliveryNoteErrorCode.BUSINESS_ERROR, "恢复失败，只能恢复预设业务类型数据");
        }
        //TODO beforeLifeStatus 不正确
        objectDataList = objectDataList.stream().filter(objectData -> Objects.equals(objectData.get(SystemConstants.Field.LifeStatusBeforeInvalid.apiName, String.class), SystemConstants.LifeStatus.Normal.value)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(objectDataList)) {
            List<String> ids = objectDataList.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());
            Map<String, String> noteIdWarehouseMap = objectDataList.stream().collect(Collectors.toMap(objectData -> objectData.getId(), objectData -> objectData.get(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, String.class)));

            productList = outboundDeliveryManager.findProductByIds(actionContext.getUser(), ids);
            noteIdProductAmountMap = outboundDeliveryManager.findProductMapByIds(actionContext.getUser(), ids, productList);
            //校验库存先合并同个仓库 同种产品的数量
            Map<String, Map<String, BigDecimal>> warehouseIdProductAmountMap = buildWarehouseIdProductAmountMap(noteIdProductAmountMap, noteIdWarehouseMap);

            outboundDeliveryNoteStockManager.batchCheckAvailableStock(actionContext.getUser(), warehouseIdProductAmountMap);
        }
    }

    @Override
    public Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        if (CollectionUtils.isNotEmpty(objectDataList)) {
            //invalid -> normal  扣减实际库存
            objectDataList.forEach(objectData -> {
                String noteId = objectData.getId();
                List<IObjectData> noteProductList = productList.stream().filter(product -> Objects.equals(product.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName, String.class), noteId)).collect(Collectors.toList());
                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(noteId).operateType(StockOperateTypeEnum.RECOVER.value)
                        .beforeLifeStatus(SystemConstants.LifeStatus.Invalid.value)
                        .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                        .operateObjectType(StockOperateObjectTypeEnum.OUTBOUND_DELIVERY_NOTE.value)
                        .operateResult(StockOperateResultEnum.PASS.value)
                        .build();

                outboundDeliveryNoteStockManager.minusRealStock(actionContext.getUser(), objectData, noteProductList, stockOperateInfo);
            });
        }
        return result;
    }

    //由出库单分组改为由仓库分组合并
    private Map<String, Map<String, BigDecimal>> buildWarehouseIdProductAmountMap(Map<String, Map<String, BigDecimal>> noteIdProductAmountMap, Map<String, String> noteIdWarehouseMap) {
        Map<String, Map<String, BigDecimal>> warehouseIdProductAmountMap = new HashMap<>();

        noteIdProductAmountMap.keySet().forEach(noteId -> {
            String warehouseId = noteIdWarehouseMap.get(noteId);
            Map<String, BigDecimal> productAmountMap = warehouseIdProductAmountMap.get(warehouseId) != null ? warehouseIdProductAmountMap.get(warehouseId) : new HashMap<>();

            Map<String, BigDecimal> noteProductAmountMap = noteIdProductAmountMap.get(noteId);
            noteProductAmountMap.keySet().forEach(productId -> {
                if (productAmountMap.get(productId) == null) {
                    productAmountMap.put(productId, noteProductAmountMap.get(productId));
                } else {
                    productAmountMap.put(productId, noteProductAmountMap.get(productId).add(productAmountMap.get(productId)));
                }
            });
            warehouseIdProductAmountMap.put(warehouseId, productAmountMap);
        });
        return warehouseIdProductAmountMap;
    }


}
