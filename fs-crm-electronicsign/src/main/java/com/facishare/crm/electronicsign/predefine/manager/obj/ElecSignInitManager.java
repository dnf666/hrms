package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SignRecordObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SignerObjConstants;
import com.facishare.crm.electronicsign.enums.status.ElecSignInitStatusEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.ElecSignConfigManager;
import com.facishare.crm.electronicsign.predefine.manager.GrayReleaseManager;
import com.facishare.crm.electronicsign.predefine.manager.TenantQuotaManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 对象初始化
 */
@Service
@Slf4j
public class ElecSignInitManager {
    @Autowired
    private ElecSignConfigManager elecSignConfigManager;
    @Autowired
    private ElecSignObjectDescribeManager elecSignObjectDescribeManager;
    @Resource
    private DescribeLogicService describeLogicService;
    @Autowired
    private ElecSignPrivilegeManager elecSignPrivilegeManager;
    @Autowired
    private ElecSignRecordAndLayoutManager elecSignRecordAndLayoutManager;
    @Autowired
    private TenantQuotaManager tenantQuotaManager;
    @Autowired
    private GrayReleaseManager grayReleaseManager;

    /**
     * 初始化
     * @param user
     */
    public void init(User user) {
        //灰度
        if (!grayReleaseManager.isInitSwitchGrayed(user.getTenantId())) {
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, "该企业不在灰度范围内");
        }

        //是否已经初始化
        ElecSignInitStatusEnum elecSignInitStatusEnum = elecSignConfigManager.getElecSignInitStatus(user.getTenantId());
        if (Objects.equals(elecSignInitStatusEnum, ElecSignInitStatusEnum.OPENED)) {
            return;
        }

        //displayName是否被使用了，被使用了则不能创建
        Set<String> existDisplayNames = elecSignObjectDescribeManager.getExistDisplayName(user.getTenantId());
        if (CollectionUtils.isNotEmpty(existDisplayNames)) {
            String errorMsg = Joiner.on(",").join(existDisplayNames).concat("名称已存在");
            throw new ElecSignBusinessException(ElecSignErrorCode.BUSINESS_ERROR, errorMsg);
        }

        //初始化
        try {
            initObjAndPrivilege(user);
            elecSignConfigManager.updateElecSignInitStatus(user, ElecSignInitStatusEnum.OPENED);
        } catch (Exception e) {
            log.warn("initObjAndPrivilege failed, ", e);
            elecSignConfigManager.updateElecSignInitStatus(user, ElecSignInitStatusEnum.OPEN_FAIL);
            throw new ElecSignBusinessException(ElecSignErrorCode.INIT_ELEC_SIGN_FAILED, ElecSignErrorCode.INIT_ELEC_SIGN_FAILED.getMessage() + e);
        }

        //配额初始化
        tenantQuotaManager.initForTenant(user);
    }

    /**
     * 初始化对象和权限
     */
    private void initObjAndPrivilege(User user) {
        String tenantId = user.getTenantId();
        String fsUserId = user.getUserId();

        //创建定义、layout
        Set<String> apiNames = Sets.newHashSet(InternalSignCertifyObjConstants.API_NAME, AccountSignCertifyObjConstants.API_NAME, SignRecordObjConstants.API_NAME, SignerObjConstants.API_NAME);
        Map<String, IObjectDescribe> describeMap = describeLogicService.findObjects(tenantId, apiNames);
        log.info("initObjAndPrivilege, describeLogicService.findObjects, describeMap[{}]", describeMap);
        if (!describeMap.containsKey(InternalSignCertifyObjConstants.API_NAME)) {
            elecSignObjectDescribeManager.createInternalSignCertifyDescribeAndLayout(tenantId, fsUserId);
        }
        if (!describeMap.containsKey(AccountSignCertifyObjConstants.API_NAME)) {
            elecSignObjectDescribeManager.createAccountSignCertifyDescribeAndLayout(user, fsUserId);

            //角色&业务类型、业务类型&layout
            elecSignRecordAndLayoutManager.initAssignRecordAndLayout(user);
        }
        if (!describeMap.containsKey(SignRecordObjConstants.API_NAME)) {
            elecSignObjectDescribeManager.createSignRecordDescribeAndLayout(tenantId, fsUserId);
        }
        if (!describeMap.containsKey(SignerObjConstants.API_NAME)) {
            elecSignObjectDescribeManager.createSignerDescribeAndLayout(tenantId, fsUserId);
        }

        //初始化权限
        elecSignPrivilegeManager.initPrivilege(user);
    }
}