package com.facishare.crm.deliverynote.predefine.action;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.appframework.metadata.ObjectLifeStatus;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 批量作废
 * Created by chenzs on 2018/1/19.
 */
@Slf4j
public class DeliveryNoteInvalidAction extends StandardInvalidAction {
    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    private String oldStatus;
    @Override
    protected void before(Arg arg) {
        super.before(arg);

        IObjectData deliveryNoteObjectData = objectDataList.get(0);
        oldStatus = deliveryNoteObjectData.get(DeliveryNoteObjConstants.Field.Status.apiName, String.class);
        deliveryNoteManager.checkForInvalid(this.getActionContext().getUser(), deliveryNoteObjectData);

    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        this.deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);

        String id = this.objectDataList.get(0).getId();
        IObjectData objectData = deliveryNoteManager.getObjectDataById(actionContext.getUser(), id);

        deliveryNoteManager.doAfterInvalidAction(actionContext.getUser(), objectData, oldStatus);
        return result;
    }
}