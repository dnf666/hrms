package com.facishare.crm.requisitionnote.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteCalculateManager;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 14/03/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteBulkInvalidAction extends StandardBulkInvalidAction {

    private List<IObjectData> iObjectDataList = Lists.newArrayList();
    private RequisitionNoteCalculateManager requisitionNoteCalculateManager;
    private RequisitionNoteManager requisitionNoteManager;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        iObjectDataList = objectDataList.stream().filter(iObjectData ->
                iObjectData.get(SystemConstants.Field.LifeStatus.apiName, String.class).equals(SystemConstants.LifeStatus.Normal.value)).
                collect(Collectors.toList());

        for (IObjectData objectData : iObjectDataList) {
            //如果已经确认入库，不允许作废
            Boolean hasTransferredIn = objectData.get(RequisitionNoteConstants.Field.InboundConfirmed.apiName, Boolean.class);
            if (hasTransferredIn) {
                String requisitionName = objectData.getName();
                throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "调拨单" + requisitionName + "已确认入库, 不允许作废");
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

        log.debug("RequisitionNoteBulkInvalidAction.after, arg[{}], actionContext[{}], result[{}]", arg, actionContext, result);

        if (!CollectionUtils.isEmpty(iObjectDataList)) {
            List<String> ids = iObjectDataList.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());

            requisitionNoteCalculateManager = SpringUtil.getContext().getBean(RequisitionNoteCalculateManager.class);
            List<IObjectData> newObjectDataList = requisitionNoteCalculateManager.findByIds(actionContext.getUser(), ids, RequisitionNoteConstants.API_NAME);

            log.debug("RequisitionNoteBulkInvalidAction.after, arg[{}], actionContext[{}], resultDataList[{}]", arg, actionContext, newObjectDataList);

            for (IObjectData objectData : newObjectDataList) {
                String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName).toString();

                //无审批 增加调出仓库实际库存 normal->invalid
                if (lifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {
                    log.info("goodsReceivedNoteManager.minusRealStock, objectData[{}]", objectData);
                    String transferOutWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);

                    //作废出库单
                    requisitionNoteManager = SpringUtil.getContext().getBean(RequisitionNoteManager.class);
                    requisitionNoteManager.invalidOutboundDeliveryNote(user, objectData);

                    //库存操作记录
                    StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.INVALID.value)
                            .beforeLifeStatus(SystemConstants.LifeStatus.Normal.value)
                            .afterLifeStatus(lifeStatus)
                            .operateResult(StockOperateResultEnum.PASS.value)
                            .operateObjectType(StockOperateObjectTypeEnum.REQUISITION_NOTE.value)
                            .build();

                    //增加调出仓库实际库存
                    requisitionNoteCalculateManager.insertOrUpdateStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
                }
            }
        }
        return result;
    }
}
