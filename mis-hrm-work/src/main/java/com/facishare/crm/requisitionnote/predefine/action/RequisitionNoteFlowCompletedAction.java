package com.facishare.crm.requisitionnote.predefine.action;

import com.facishare.crm.action.CommonFlowCompletedAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteCalculateManager;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardFlowCompletedAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 14/03/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteFlowCompletedAction extends CommonFlowCompletedAction {
    private String oldLifeStatus = null;
    private String newLifeStatus = null;
    private List<ObjectDataDocument> productDocList = Lists.newArrayList();
    private RequisitionNoteManager requisitionNoteManager = SpringUtil.getContext().getBean(RequisitionNoteManager.class);
    private RequisitionNoteCalculateManager requisitionNoteCalculateManager;
    @Override
    protected void before(Arg arg) {
        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        oldLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
        List<IObjectData> detailObjectDataList = requisitionNoteManager.findDetailObjectDataIncludeInvalid(this.actionContext.getUser(), objectData);
        productDocList = detailObjectDataList.stream().map(ObjectDataDocument::of).collect(Collectors.toList());
        super.before(arg);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        this.requisitionNoteCalculateManager = SpringUtil.getContext().getBean(RequisitionNoteCalculateManager.class);
        //查询数据，获取新生命状态
        User user = actionContext.getUser();
        IObjectData objectData = this.serviceFacade.findObjectDataIncludeDeleted(user, arg.getDataId(), RequisitionNoteConstants.API_NAME);
        log.debug("RequisitionNoteFlowCompletedAction.after, arg[{}], result[{}], IObjectData[{}]", arg, result, objectData);
        newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

        String transferOutWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);

        //增加库存操作记录
        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId())
                .beforeLifeStatus(oldLifeStatus)
                .afterLifeStatus(newLifeStatus)
                .operateObjectType(StockOperateObjectTypeEnum.REQUISITION_NOTE.value)
                .build();

        //状态流转：underReview -> ineffective
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.UnderReview.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Ineffective.value)) {
            //扣减调出仓库冻结库存
            stockOperateInfo.setOperateType(StockOperateTypeEnum.ADD_FLOW_COMPLETE.value);
            stockOperateInfo.setOperateResult(StockOperateResultEnum.REJECT.value);
            requisitionNoteCalculateManager.minusBlockedStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
        }

        //状态流转：underReview -> normal
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.UnderReview.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Normal.value)) {
            //1、创建一条出库单记录
            requisitionNoteManager.createOutboundDeliveryNote(user, objectData, transferOutWarehouseId, productDocList);

            //扣减调出仓库的冻结库存和实际库存、调入仓库的实际库存通过接口增加
            stockOperateInfo.setOperateType(StockOperateTypeEnum.ADD_FLOW_COMPLETE.value);
            stockOperateInfo.setOperateResult(StockOperateResultEnum.PASS.value);
            requisitionNoteCalculateManager.minusBlockedRealStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
        }

        //状态流转：inChange -> invalid
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.InChange.value) &&
                newLifeStatus.equals(SystemConstants.LifeStatus.Invalid.value)) {

            //作废出库单
            requisitionNoteManager.invalidOutboundDeliveryNote(user, objectData);

            //增加调出仓库实际库存
            stockOperateInfo.setOperateType(StockOperateTypeEnum.INVALID_FLOW_COMPLETE.value);
            stockOperateInfo.setOperateResult(StockOperateResultEnum.PASS.value);
            requisitionNoteCalculateManager.insertOrUpdateStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
        }
        return result;
    }
}
