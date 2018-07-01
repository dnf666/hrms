package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.action.CommonEditAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Created by linchf on 2018/1/18.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseEditAction extends CommonEditAction {
    private WareHouseManager wareHouseManager = SpringUtil.getContext().getBean(WareHouseManager.class);

    @Override
    protected void before(Arg arg) {
        wareHouseManager.modifyArg(actionContext.getTenantId(), arg);
        super.before(arg);
        arg.setObjectData(fillArg(arg.getObjectData()));
        wareHouseManager.editBefore(getActionContext().getTenantId(), arg.getObjectData().toObjectData());
    }

    @Override
    protected Result after(Arg arg, Result result) {

        Result ret = super.after(arg, result);
        IObjectData objectData = ret.getObjectData().toObjectData();
        String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

        log.info("WareHouseEditAction after. ret[{}], lifeStatus[{}]", ret, lifeStatus);
        if (Objects.equals(lifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            wareHouseManager.editAfterChange(getActionContext().getUser(), objectData);
        }
        return ret;
    }

    private ObjectDataDocument fillArg(ObjectDataDocument objectDataDocument) {
        if (objectDataDocument.get(WarehouseConstants.Field.Is_Default.apiName) == null || objectDataDocument.get(WarehouseConstants.Field.Is_Enable.apiName) == null) {
            IObjectData findObjectData = serviceFacade.findObjectData(actionContext.getUser(), objectData.getId(), WarehouseConstants.API_NAME);

            if (objectDataDocument.get(WarehouseConstants.Field.Is_Default.apiName) == null) {
                objectDataDocument.put(WarehouseConstants.Field.Is_Default.apiName, findObjectData.get(WarehouseConstants.Field.Is_Default.apiName, Boolean.class));
            }

            if (objectDataDocument.get(WarehouseConstants.Field.Is_Enable.apiName) == null) {
                objectDataDocument.put(WarehouseConstants.Field.Is_Enable.apiName, findObjectData.get(WarehouseConstants.Field.Is_Enable.apiName, String.class));
            }
        }
        return objectDataDocument;
    }
}
