package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.RecordTypeAuthProxy;
import com.facishare.paas.appframework.metadata.dto.auth.*;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chenzs on 2018/5/11.
 */
@Slf4j
@Service
public class ElecSignRecordAndLayoutManager {
    @Autowired
    private RecordTypeAuthProxy recordTypeAuthApi;

    public void initAssignRecordAndLayout(User user) {
        RoleInfoModel.Arg roleInfoModelArg = new RoleInfoModel.Arg();
        roleInfoModelArg.setAuthContext(user);
        RoleInfoModel.Result roleInfoModelResult = roleInfo(roleInfoModelArg);
        if (roleInfoModelResult.isSuccess()) {
            //添加角色与业务类型的关系
            List<RoleInfoPojo> roleInfoPojos = roleInfoModelResult.getResult().getRoles();
            initAssignRecord(user, roleInfoPojos, AccountSignCertifyObjConstants.API_NAME, Lists.newArrayList("enterprise_record_type__c"), "default__c");

            //业务类型 与 layout
            List<RecordViewVo> recordViewVos = Lists.newArrayList();
            recordViewVos.add(new RecordViewVo("default__c", AccountSignCertifyObjConstants.INDIVIDUAL_LAYOUT_API_NAME));
            recordViewVos.add(new RecordViewVo("enterprise_record_type__c", AccountSignCertifyObjConstants.ENTERPRISE_LAYOUT_API_NAME));
            initAssignLayout(user, roleInfoPojos, AccountSignCertifyObjConstants.API_NAME, recordViewVos);
        } else {
            log.warn("roleInfo error,roleInfo:{}", roleInfoModelResult);
        }
    }

    public RoleInfoModel.Result roleInfo(RoleInfoModel.Arg arg) {
        RoleInfoModel.Result result = recordTypeAuthApi.roleInfo(arg);
        return result;
    }

    public AddRoleViewModel.Result saveLayoutAssign(AddRoleViewModel.Arg arg) {
        return recordTypeAuthApi.addRoleView(arg);
    }

    /**
     * 添加角色与业务类型的关系
     */
    public AddRoleRecordTypeModel.Result addRoleRecordType(AddRoleRecordTypeModel.Arg arg) {
        return recordTypeAuthApi.addRoleRecordType(arg);
    }

    /**
     * 添加角色与业务类型的关系
     */
    private int initAssignRecord(User user, List<RoleInfoPojo> roleInfoPojos, String entityId, List<String> recordTypeIds, String defaultRecordTypeId) {
        AddRoleRecordTypeModel.Result result;
        RecordTypePojo recordTypePojo;
        for (String recordTypeId : recordTypeIds) {
            List<RecordTypePojo> recordTypePojos = Lists.newArrayList();
            for (RoleInfoPojo roleInfoPojo : roleInfoPojos) {
                recordTypePojo = new RecordTypePojo();
                recordTypePojo.setAppId("CRM");
                recordTypePojo.setEntityId(entityId);
                recordTypePojo.setTenantId(user.getTenantId());
                recordTypePojo.setRecordTypeId(recordTypeId);
                recordTypePojo.setRoleCode(roleInfoPojo.getRoleCode());
                recordTypePojo.setDefaultType(recordTypeId.equals(defaultRecordTypeId));
                recordTypePojos.add(recordTypePojo);
            }
            AddRoleRecordTypeModel.Arg arg = new AddRoleRecordTypeModel.Arg();
            arg.setRecordTypePojos(recordTypePojos);
            arg.setRecordTypeId(recordTypeId);
            arg.setEntityId(entityId);
            arg.setAuthContext(user);
            result = addRoleRecordType(arg);
            if (recordTypeId.equals(recordTypeIds.get(recordTypeIds.size() - 1))) {
                return result.isSuccess() ? 0 : 1;
            }
        }
        return CollectionUtils.isNotEmpty(recordTypeIds) ? 1 : 0;
    }

    private AddRoleViewModel.Result initAssignLayout(User user, List<RoleInfoPojo> roleInfoPojos, String entityId, List<RecordViewVo> recordViewVos) {
        List<RoleViewPojo> roleViewPojos = Lists.newArrayList();
        RoleViewPojo roleViewPojo;
        for (RoleInfoPojo roleInfoPojo : roleInfoPojos) {
            for (RecordViewVo recordViewVo : recordViewVos) {
                roleViewPojo = new RoleViewPojo();
                roleViewPojo.setAppId("CRM");
                roleViewPojo.setEntityId(entityId);
                roleViewPojo.setTenantId(user.getTenantId());
                roleViewPojo.setRecordTypeId(recordViewVo.getRecordTypeId());
                roleViewPojo.setRoleCode(roleInfoPojo.getRoleCode());
                roleViewPojo.setViewId(recordViewVo.getViewId());
                roleViewPojos.add(roleViewPojo);
            }
        }
        AddRoleViewModel.Arg arg = new AddRoleViewModel.Arg();
        arg.setRoleViewPojos(roleViewPojos);
        arg.setAuthContext(user);
        AddRoleViewModel.Result result = saveLayoutAssign(arg);
        return result;
    }

    @Data
    private class RecordViewVo {
        private String recordTypeId;
        private String viewId;

        public RecordViewVo(String recordTypeId, String viewId) {
            this.recordTypeId = recordTypeId;
            this.viewId = viewId;
        }
    }
}