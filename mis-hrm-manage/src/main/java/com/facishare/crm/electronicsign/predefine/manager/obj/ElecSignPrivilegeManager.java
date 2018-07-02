package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.privilege.FunctionPrivilegeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限
 */
@Slf4j
@Service
public class ElecSignPrivilegeManager {
    @Autowired
    private FunctionPrivilegeService functionPrivilegeService;

    /**
     * 权限初始化
     */
    public void initPrivilege(User user) {
        //1 给对象创建权限
        //给"内部签章认证"创建"启用"的权限
        createFuncCode(user, InternalSignCertifyObjConstants.API_NAME, InternalSignCertifyObjConstants.Button.Enable.apiName, InternalSignCertifyObjConstants.Button.Enable.label);

        //给"内部签章认证"创建"停用"的权限
        createFuncCode(user, InternalSignCertifyObjConstants.API_NAME, InternalSignCertifyObjConstants.Button.Disable.apiName, InternalSignCertifyObjConstants.Button.Disable.label);

        //给"客户签章认证"创建"启用"的权限
        createFuncCode(user, AccountSignCertifyObjConstants.API_NAME, AccountSignCertifyObjConstants.Button.Enable.apiName, AccountSignCertifyObjConstants.Button.Enable.label);

        //给"客户签章认证"创建"停用"的权限
        createFuncCode(user, AccountSignCertifyObjConstants.API_NAME, AccountSignCertifyObjConstants.Button.Disable.apiName, AccountSignCertifyObjConstants.Button.Disable.label);

        //2、给角色授权
        addFuncAccessToRole(user);
    }

    /**
     * 给对象objectApiName创建权限
     */
    private void createFuncCode(User user, String objectApiName, String actionCode, String actionDisplayName) {
        try {
            functionPrivilegeService.createFuncCode(user, objectApiName, actionCode, actionDisplayName);
            log.info("functionPrivilegeService.createFuncCode, user:{}, objectApiName:{}, actionCode:{}, actionDisplayName:{}", user, objectApiName, actionCode, actionDisplayName);
        } catch (Exception e) {
            log.error("functionPrivilegeService.createFuncCode, user:{}, objectApiName:{}, actionCode:{}, actionDisplayName:{} ", user, objectApiName, actionCode, actionDisplayName, e);
            /**
             * 如果报错是：
             * "初始化功能权限失败,原因:功能唯一标识重复启用"
             * "初始化功能权限失败,原因:功能唯一标识重复停用"
             * 则跳过
             */
            if (!e.getMessage().contains("功能唯一标识重复")) {
                throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "给对象创建" + actionDisplayName + "权限失败, " + e);
            }
        }
    }

    /**
     * 给角色添加权限
     * "内部签章认证的"启用"、"停用"权限，授权给"CRM管理员"
     * "客户签章认证的"启用"、"停用"权限，授权给"CRM管理员" "销售人员"
     */
    private void addFuncAccessToRole(User user) {
        List<String> signTenantActionCodes = Lists.newArrayList(InternalSignCertifyObjConstants.Button.Enable.apiName, InternalSignCertifyObjConstants.Button.Disable.apiName);
        List<String> signUserActionCodes = Lists.newArrayList(AccountSignCertifyObjConstants.Button.Enable.apiName, AccountSignCertifyObjConstants.Button.Disable.apiName);

        addUserDefinedFuncAccess(user, CommonConstants.CRM_MANAGER_ROLE, InternalSignCertifyObjConstants.API_NAME, signTenantActionCodes);
        addUserDefinedFuncAccess(user, CommonConstants.CRM_MANAGER_ROLE, AccountSignCertifyObjConstants.API_NAME, signUserActionCodes);
        addUserDefinedFuncAccess(user, CommonConstants.SALE_PERSON_ROLE, AccountSignCertifyObjConstants.API_NAME, signUserActionCodes);
    }

    /**
     * 给角色添加权限
     * （functionPrivilegeService.updateUserDefinedFuncAccess可以重复调用，原来有权限a、b, 要加权限c, actionCodes=[c]）
     */
    private void addUserDefinedFuncAccess(User user, String roleCode, String objectApiName, List<String> addActionCodes) {
        functionPrivilegeService.updateUserDefinedFuncAccess(user, roleCode, objectApiName, addActionCodes, null);
        log.info("functionPrivilegeService.updateUserDefinedFuncAccess, user:{}, roleCode:{}, objectApiName:{}, addActionCodes:{}, deleteActionCodes:{}", user, roleCode, objectApiName, addActionCodes, null);
    }
}