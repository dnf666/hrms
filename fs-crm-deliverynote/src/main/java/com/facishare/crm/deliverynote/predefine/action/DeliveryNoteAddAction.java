package com.facishare.crm.deliverynote.predefine.action;

import com.facishare.crm.action.CommonAddAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteProductManager;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 发货单数据
 * Created by chenzs on 2018/1/8.
 */
@Slf4j
public class DeliveryNoteAddAction extends CommonAddAction {
    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    private DeliveryNoteProductManager deliveryNoteProductManager = SpringUtil.getContext().getBean(DeliveryNoteProductManager.class);;

    @Override
    protected void before(Arg arg) {
        // 发货状态默认为未发货状态
        if (Objects.isNull(arg.getObjectData().get(DeliveryNoteObjConstants.Field.Status.apiName))) {
            arg.getObjectData().put(DeliveryNoteObjConstants.Field.Status.apiName, DeliveryNoteObjStatusEnum.UN_DELIVERY.getStatus());
        }

        deliveryNoteManager.modifyArg(this.getActionContext().getTenantId(), arg);
        List<ObjectDataDocument> productObjectDocList = arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME);

        deliveryNoteManager.checkForAdd(actionContext.getUser(), arg.getObjectData().toObjectData(), ObjectDataDocument.ofDataList(productObjectDocList));

        // 设置发货单产品的本次发货金额
        String salesOrderId = (String) this.arg.getObjectData().get(DeliveryNoteObjConstants.Field.SalesOrderId.getApiName());
        deliveryNoteProductManager.setDeliveryMoney(actionContext.getUser(), salesOrderId, arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME));

        // 设置发货单发货总金额
        BigDecimal totalDeliveryMoney = BigDecimal.ZERO;
        for (ObjectDataDocument productObjectData : productObjectDocList) {
            BigDecimal deliveryMoney = productObjectData.toObjectData().get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class);
            totalDeliveryMoney = totalDeliveryMoney.add(deliveryMoney);
        }
        arg.getObjectData().put(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, totalDeliveryMoney);

        super.before(arg);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        String newLifeStatus = (String) result.getObjectData().get(SystemConstants.Field.LifeStatus.apiName);
        log.debug("newLifeStatus[{}]", newLifeStatus);

        String id = (String) result.getObjectData().get(DeliveryNoteObjConstants.Field.Id.getApiName());

        IObjectData deliveryNoteObjData = deliveryNoteManager.getObjectDataById(this.getActionContext().getUser(), id);
        if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value )) {
            User user = this.getActionContext().getUser();
            List<ObjectDataDocument> productObjectDataDocList = arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME);
            IObjectData newStatusObjectData = deliveryNoteManager.updateStatus(this.actionContext.getUser(), deliveryNoteObjData, DeliveryNoteObjStatusEnum.HAS_DELIVERED);
            result.setObjectData(ObjectDataDocument.of(newStatusObjectData));

            StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(id).operateType(StockOperateTypeEnum.ADD.value)
                    .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                    .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                    .operateObjectType(StockOperateObjectTypeEnum.DELIVERY_NOTE.value)
                    .operateResult(StockOperateResultEnum.PASS.value).build();

            deliveryNoteManager.doCreateDeliveryNoteBecomeHasDelivered(user, deliveryNoteObjData, ObjectDataDocument.ofDataList(productObjectDataDocList), stockOperateInfo);
        } else {
            // 设置发货单状态为审核中
            IObjectData newStatusObjectData = deliveryNoteManager.updateStatus(this.actionContext.getUser(), deliveryNoteObjData, DeliveryNoteObjStatusEnum.IN_APPROVAL);
            result.setObjectData(ObjectDataDocument.of(newStatusObjectData));
        }
        return result;
    }
}
