package com.facishare.crm.requisitionnote.predefine.service.impl;


import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteInitManager;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteCalculateManager;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteInitManager;
import com.facishare.crm.requisitionnote.predefine.service.RequisitionNoteService;
import com.facishare.crm.requisitionnote.predefine.service.dto.RequisitionNoteType;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.enums.GoodsReceivedNoteRecordTypeEnum;
import com.facishare.crm.stock.enums.GoodsReceivedTypeEnum;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.GoodsReceivedNoteProductVO;
import com.facishare.crm.stock.model.GoodsReceivedNoteVO;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 14/03/2018
 */
@Component
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteServiceImpl implements RequisitionNoteService {

    @Resource
    private ServiceFacade serviceFacade;

    @Resource
    private StockManager stockManager;

    @Resource
    private RequisitionNoteInitManager requisitionInitManager;

    @Resource
    private OutboundDeliveryNoteInitManager outboundDeliveryNoteInitManager;

    @Resource
    private GoodsReceivedNoteManager goodsReceivedNoteManager;

    @Resource
    private RequisitionNoteCalculateManager requisitionNoteCalculateManager;

    @Override
    public RequisitionNoteType.EnableRequisitionResult enableRequisition(ServiceContext serviceContext) {
        RequisitionNoteType.EnableRequisitionResult result = new RequisitionNoteType.EnableRequisitionResult();

        Boolean isSuccess = false;

        try {
            isSuccess = requisitionInitManager.init(serviceContext.getUser());
        } catch (Exception e) {
            log.warn("RequisitionServiceImpl failed! serviceContext[{}]", serviceContext);
        }

        if (isSuccess) {
            result.setEnableStatus(RequisitionNoteType.RequisitionSwitchEnum.ENABLE.getStatus());
            result.setMessage(RequisitionNoteType.RequisitionSwitchEnum.ENABLE.getLabel());
        } else {
            result.setEnableStatus(RequisitionNoteType.RequisitionSwitchEnum.FAILED.getStatus());
            result.setMessage(RequisitionNoteType.RequisitionSwitchEnum.FAILED.getLabel());
        }
        return result;
    }

    @Override
    public RequisitionNoteType.IsConfirmedResult isConfirmed(ServiceContext serviceContext, RequisitionNoteType.IsConfirmedArg arg) {
        String requisitionNoteId = arg.getRequisitionNoteId();
        RequisitionNoteType.IsConfirmedResult result = new RequisitionNoteType.IsConfirmedResult();
        User user = serviceContext.getUser();

        //0、校验是否拥有入库单新建权限
        List<String> actionCodes = Lists.newArrayList(ObjectAction.CREATE.getActionCode());
        Map<String, Boolean> funPrivilegeMap = serviceFacade.funPrivilegeCheck(user, GoodsReceivedNoteConstants.API_NAME, actionCodes);
        if (!funPrivilegeMap.get(ObjectAction.CREATE.getActionCode())) {
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "您没有入库单的新建权限，无法操作确认入库");
        }


        //1、查询调拨单详情
        IObjectData objectData = serviceFacade.findObjectDataIncludeDeleted(user, requisitionNoteId, RequisitionNoteConstants.API_NAME);
        if (objectData.get(RequisitionNoteConstants.Field.InboundConfirmed.apiName, Boolean.class)) {
            String name = objectData.getName();
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "调拨单" + name + "已确认入库请勿重复提交");
        }
        //1.1、查询调拨单产品
        IObjectDescribe goodsReceivedNoteProductDescribe = serviceFacade.findObject(user.getTenantId(), RequisitionNoteProductConstants.API_NAME);
        List<IObjectData> objectDataList = serviceFacade.findDetailObjectDataList(goodsReceivedNoteProductDescribe, objectData, user);
        String transferInWarehouseId = objectData.get(RequisitionNoteConstants.Field.TransferInWarehouse.apiName, String.class);

        //2、创建入库单记录
        GoodsReceivedNoteVO goodsReceivedNoteVO = GoodsReceivedNoteVO.builder().goodsReceivedDate(System.currentTimeMillis()).goodsReceivedType(GoodsReceivedTypeEnum.REQUISITION.value)
                .requisitionId(requisitionNoteId).warehouseId(transferInWarehouseId).build();
        List<GoodsReceivedNoteProductVO> goodsReceivedNoteProductVOList = Lists.newArrayList();
        for (IObjectData productData : objectDataList) {
            String productId = productData.get(RequisitionNoteProductConstants.Field.Product.apiName, String.class);
            BigDecimal requisitionProductAmount = productData.get(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName, BigDecimal.class);
            GoodsReceivedNoteProductVO goodsReceivedNoteProductVO = GoodsReceivedNoteProductVO.builder().goodsReceivedAmount(requisitionProductAmount.toString()).productId(productId).build();
            goodsReceivedNoteProductVOList.add(goodsReceivedNoteProductVO);
        }
        IObjectData data = goodsReceivedNoteManager.create(user, goodsReceivedNoteVO, goodsReceivedNoteProductVOList, GoodsReceivedNoteRecordTypeEnum.RequisitionIn.apiName);

        //3、增加调入仓库的实际库存
        StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(objectData.getId()).operateType(StockOperateTypeEnum.ADD.value)
                .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                .operateObjectType(StockOperateObjectTypeEnum.GOODS_RECEIVED_NOTE.value)
                .build();
        requisitionNoteCalculateManager.insertOrUpdateStock(user, transferInWarehouseId, objectData, stockOperateInfo);

        //4、更新调拨单"是否已确认入库"字段
        objectData.set(RequisitionNoteConstants.Field.InboundConfirmed.apiName, true);
        serviceFacade.updateObjectData(user, objectData);

        result.setHasConfirmed(true);
        result.setMessage(RequisitionNoteErrorCode.OK.getMessage());
        result.setGoodsReceivedNoteId(data.getId());
        return result;
    }

    @Override
    public RequisitionNoteType.AddFieldAndDataResult addFieldAndData(ServiceContext serviceContext) {
        RequisitionNoteType.AddFieldAndDataResult result = new RequisitionNoteType.AddFieldAndDataResult();

        String enableStockTenantIds = ConfigCenter.ENABLE_STOCK_TENANT_IDS;
        if (StringUtils.isEmpty(enableStockTenantIds)) {
            return result;
        }

        List<String> tenantIds = Lists.newArrayList(enableStockTenantIds.split(";"));

        if (!ConfigCenter.SUPPER_ADMIN_ID.equals(serviceContext.getTenantId() + "." + serviceContext.getUser().getUserId())) {
            log.warn("addFieldAndData fail. user[{}]", serviceContext.getUser());
            throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "you haven't permission");
        }

        for (String tenantId : tenantIds) {
            //校验是否开启库存
            if (stockManager.isStockEnable(tenantId)) {
                log.info("--------------------- addFieldDescribeAndData tenantId:{}", tenantId);
                User user = new User(tenantId, User.SUPPER_ADMIN_USER_ID);

                try {
                    //1、库存描述增加产品分类字段
                    stockManager.addFieldDescribeAndLayout(user);

                    //2、开启调拨单和出库单
                    requisitionInitManager.init(user);
                    outboundDeliveryNoteInitManager.init(user);

                    //3、更新入库单产品描述的config
                    //4、更新入库单描述，增加"调拨单编号"和"调拨入库"的入库类型，增加业务类型"调拨入库"
                    goodsReceivedNoteManager.addFieldDescribeAndLayout(user);
                    log.info("--------------------- addFieldDescribeAndData success! tenantId:{}", tenantId);
                } catch (Exception e) {
                    log.warn("--------------------- addFieldDescribeAndData Exception! tenantId:{}", tenantId, e);
                }
            } else {
                log.info("--------------------- you should enable stock firstly. tenantId:{}", tenantId);
            }
        }
        return result;
    }



}
