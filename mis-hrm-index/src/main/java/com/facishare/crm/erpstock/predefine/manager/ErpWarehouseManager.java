package com.facishare.crm.erpstock.predefine.manager;

import com.facishare.crm.erpstock.constants.ErpWarehouseConstants;
import com.facishare.crm.erpstock.enums.ErpWarehouseEnableEnum;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author liangk
 * @date 09/05/2018
 */
@Service
@Slf4j(topic = "erpStockAccess")
public class ErpWarehouseManager extends CommonManager {

    public IObjectData buildObjectData(User user, IObjectData objectData) {
        IObjectDescribe objectDescribe = findDescribe(user.getTenantId(), ErpWarehouseConstants.API_NAME);
        objectData.set(ErpWarehouseConstants.Field.Is_Enable.apiName, ErpWarehouseEnableEnum.ENABLE.value);

        objectData.setTenantId(user.getTenantId());
        objectData.setCreatedBy(user.getUserId());
        objectData.setLastModifiedBy(user.getUserId());

        objectData.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        objectData.set(IObjectData.DESCRIBE_ID, objectDescribe.getId());
        objectData.set(IObjectData.DESCRIBE_API_NAME, ErpWarehouseConstants.API_NAME);
        objectData.set(IObjectData.PACKAGE, "CRM");
        objectData.set(IObjectData.VERSION, objectDescribe.getVersion());

        //相关团队
        TeamMember teamMember = new TeamMember(user.getUserId(), TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);

        ObjectDataExt objectDataExt = ObjectDataExt.of(objectData);
        objectDataExt.setTeamMembers(Lists.newArrayList(teamMember));

        return objectDataExt.getObjectData();
    }
}
