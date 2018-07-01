package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by linchf on 2018/1/18.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseInvalidAction extends StandardInvalidAction {

    private WareHouseManager wareHouseManager = SpringUtil.getContext().getBean(WareHouseManager.class);


    @Override
    protected void before(Arg arg) {
        super.before(arg);
        IObjectData tempObjectData = objectDataList.stream().filter(objectData -> objectData.getId().equals(arg.getObjectDataId())).findFirst().get();
        wareHouseManager.invalidBefore(getActionContext().getTenantId(), tempObjectData);
    }
}
