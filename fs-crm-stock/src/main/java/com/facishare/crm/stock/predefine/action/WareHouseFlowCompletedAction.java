package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.action.CommonFlowCompletedAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Created by linchf on 2018/1/18.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseFlowCompletedAction extends CommonFlowCompletedAction {

    private WareHouseManager wareHouseManager = SpringUtil.getContext().getBean(WareHouseManager.class);

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), arg.getDataId(), arg.getDescribeApiName());
        String newLifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

        if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            wareHouseManager.flowCompleteAfter(getActionContext().getUser(), objectData);
        }
        return result;
    }

}
