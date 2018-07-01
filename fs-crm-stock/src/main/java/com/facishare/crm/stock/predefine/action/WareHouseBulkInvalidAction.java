package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.paas.appframework.core.predef.action.StandardBulkInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by linchf on 2018/1/19.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseBulkInvalidAction extends StandardBulkInvalidAction {
    private WareHouseManager wareHouseManager = SpringUtil.getContext().getBean(WareHouseManager.class);

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        Map<String, IObjectData> objectDataMap = objectDataList.stream().filter(objectData -> objectData.getDescribeApiName().equals(WarehouseConstants.API_NAME)).collect(Collectors.toMap(objectData -> objectData.getId(), objectData -> objectData));
        objectDataMap.keySet().forEach(objectDataId ->
            wareHouseManager.invalidBefore(getActionContext().getTenantId(), objectDataMap.get(objectDataId)));
    }
}
