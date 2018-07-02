package com.facishare.crm.requisitionnote.predefine.action;

import com.facishare.crm.action.CommonAddAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteCalculateManager;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 14/03/2018
 */
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteAddAction extends CommonAddAction {
    private RequisitionNoteCalculateManager requisitionNoteCalculateManager;

    private RequisitionNoteManager requisitionNoteManager= SpringUtil.getContext().getBean(RequisitionNoteManager.class);

    private StockManager stockManager = SpringUtil.getContext().getBean(StockManager.class);

    private List<ObjectDataDocument> productDocList = Lists.newArrayList();

    @Override
    protected void before(Arg arg) {
        requisitionNoteManager.modifyArg(actionContext.getTenantId(), arg);
        super.before(arg);

        //1、校验是否有出库单的创建权限
        User user = this.actionContext.getUser();
        List<String> actionCodes = Lists.newArrayList(ObjectAction.CREATE.getActionCode());
        Map<String, Boolean> funPrivilegeMap = serviceFacade.funPrivilegeCheck(user, OutboundDeliveryNoteConstants.API_NAME, actionCodes);
        if (!funPrivilegeMap.get(ObjectAction.CREATE.getActionCode())) {
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "新建调拨单需要同时拥有出库单的新建权限，请联系CRM管理员添加");
        }

        //2、校验调拨单
        checkRequisitionNote(arg);

        //3、校验调拨单产品是否为空、调拨单产品数量是否为负数、调拨数量小于可用库存
        checkRequisitionNoteProduct(arg);
    }

    private void checkRequisitionNote(Arg arg) {
        IObjectData objectData = arg.getObjectData().toObjectData();
        String transferInWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferInWarehouse.apiName, String.class);
        String transferOutWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);
        if (StringUtils.equals(transferInWarehouseId, transferOutWarehouseId)) {
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "调出仓库与调入仓库不能相同");
        }
    }

    private void checkRequisitionNoteProduct(Arg arg) {
        productDocList = arg.getDetails().get(RequisitionNoteProductConstants.API_NAME);
        log.info("checkRequisitionNoteProduct arg[{}]", arg);
        if (CollectionUtils.isEmpty(productDocList)) {
            log.warn("checkGoodsReceivedNoteProduct failed, arg[{}]", arg);
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "请选择调拨产品");
        }

        String transferOutWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);
        Map<String, BigDecimal> productId2AmountMap = productDocList.stream().collect(Collectors.toMap(product -> product.toObjectData().get(RequisitionNoteProductConstants.Field.Product.apiName, String.class),
                product -> product.toObjectData().get(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName, BigDecimal.class)));
        List<String> stockIdList = productDocList.stream().map(product -> product.toObjectData().get(RequisitionNoteProductConstants.Field.Stock.apiName, String.class)).collect(Collectors.toList());
        List<IObjectData> stockList = requisitionNoteManager.findByIds(this.actionContext.getUser(), stockIdList, StockConstants.API_NAME);

        productId2AmountMap.forEach((productId, amount) -> {
            if (0 >= amount.compareTo(BigDecimal.ZERO)) {
                throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "调拨产品数量必须大于零");
            }
        });
        //校验可用库存
        stockManager.checkAvailableStock(this.actionContext.getUser(), stockList, productId2AmountMap, transferOutWarehouseId);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        User user = actionContext.getUser();

        String lifeStatus = result.getObjectData().toObjectData().get(SystemConstants.Field.LifeStatus.apiName, String.class);
        log.info("RequisitionNoteAddAction.after! LifeStatus[{}], objectData[{}], result[{}]", lifeStatus, objectData, result);

        String transferOutWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferOutWarehouse.apiName, String.class);

        requisitionNoteCalculateManager = SpringUtil.getContext().getBean(RequisitionNoteCalculateManager.class);

        //库存操作记录
        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.ADD.value)
                .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                .afterLifeStatus(lifeStatus)
                .operateObjectType(StockOperateObjectTypeEnum.REQUISITION_NOTE.value)
                .build();

        //无流程 正常状态
        if (lifeStatus.equals(ObjectLifeStatus.NORMAL.getCode())) {
            //1、创建一条出库单记录
            requisitionNoteManager.createOutboundDeliveryNote(user, objectData, transferOutWarehouseId, productDocList);
            //2、扣减调出仓库的实际库存
            stockOperateInfo.setOperateResult(StockOperateResultEnum.PASS.value);
            requisitionNoteCalculateManager.minusRealStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
        }

        //有流程 审批中状态
        if (lifeStatus.equals(ObjectLifeStatus.UNDER_REVIEW.getCode())) {
            //、增加调出仓库的冻结库存
            stockOperateInfo.setOperateResult(StockOperateResultEnum.IN_APPROVAL.value);
            requisitionNoteCalculateManager.addBlockedStock(user, transferOutWarehouseId, objectData, stockOperateInfo);
        }
        return result;
    }
}
