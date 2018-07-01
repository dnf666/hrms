package com.facishare.crm.stock.predefine.action;

import com.facishare.crm.action.CommonAddAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.WarehouseConstants;
import com.facishare.crm.stock.predefine.manager.WareHouseManager;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * Created by linchf on 2018/1/18.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseAddAction extends CommonAddAction {

    private WareHouseManager wareHouseManager = SpringUtil.getContext().getBean(WareHouseManager.class);


    @Override
    protected void before(Arg arg) {
        checkArg(arg);
        wareHouseManager.modifyArg(actionContext.getTenantId(), arg);
        super.before(arg);
        IObjectData objectData = arg.getObjectData().toObjectData();
        wareHouseManager.addBeforeCheck(getActionContext().getTenantId(), objectData);
    }

    @Override
    protected Result after(Arg arg, Result result) {

        Result ret = super.after(arg, result);
        if (!CollectionUtils.isEmpty(resultDataList)) {
            String lifeStatus = resultDataList.get(0).get(SystemConstants.Field.LifeStatus.apiName, String.class);

            log.info("WareHouseAddAction after. resultDataList[{}], lifeStatus[{}]", resultDataList, lifeStatus);
            if (Objects.equals(lifeStatus, SystemConstants.LifeStatus.Normal.value)) {
                IObjectData objectData = ret.getObjectData().toObjectData();
                wareHouseManager.addAfterChange(getActionContext().getUser(), objectData);
            }
        }

        return ret;
    }

    private void checkArg(Arg arg) {
        ObjectDataDocument objectDataDocument = arg.getObjectData();

        if (objectDataDocument.get(WarehouseConstants.Field.Is_Default.apiName) == null) {
            throw new ValidateException("必填字段[是否默认仓]，[是否默认仓]未填写，不可进行当前操作");
        }

        if (objectDataDocument.get(WarehouseConstants.Field.Is_Enable.apiName) == null) {
            throw new ValidateException("必填字段[启用状态]，[启用状态]未填写，不可进行当前操作");
        }
    }
}
