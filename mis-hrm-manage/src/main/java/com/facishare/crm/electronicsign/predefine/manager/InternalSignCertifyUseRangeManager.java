package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.predefine.dao.InternalSignCertifyUseRangeDAO;
import com.facishare.crm.electronicsign.predefine.model.InternalSignCertifyUseRangeDO;
import com.facishare.paas.appframework.common.service.dto.GetNDeptPathByUserId;
import com.facishare.paas.appframework.common.service.dto.QueryAllSuperDeptsByDeptIds;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InternalSignCertifyUseRangeManager {
    @Resource
    private InternalSignCertifyUseRangeDAO internalSignCertifyUseRangeDAO;
    @Resource
    private ServiceFacade serviceFacade;

    public void deleteByInternalSignCertifyId(String internalSignCertifyId) {
        internalSignCertifyUseRangeDAO.deleteByInternalSignCertifyId(internalSignCertifyId);
    }

    /**
     * 更新certifyStatus
     */
    public void updateStatus(String bestSignAccount, String certifyStatus, String useStatus) {
        InternalSignCertifyUseRangeDO internalSignCertifyUseRangeDO = new InternalSignCertifyUseRangeDO();
        internalSignCertifyUseRangeDO.setCertifyStatus(certifyStatus);
        internalSignCertifyUseRangeDO.setUseStatus(useStatus);
        internalSignCertifyUseRangeDAO.updateByBestSignAccount(bestSignAccount, internalSignCertifyUseRangeDO, false);
    }

    public void updateUseStatusAndDepartmentIds(String internalSignCertifyId, String useStatus) {
        InternalSignCertifyUseRangeDO oldDo = internalSignCertifyUseRangeDAO.queryByInternalSignCertifyId(internalSignCertifyId);
        if (oldDo == null || Objects.equals(oldDo.getUseStatus(), useStatus)) {
            return;
        }

        boolean setDepartmentIdsToEmpty = false;
        InternalSignCertifyUseRangeDO internalSignCertifyUseRangeDO = new InternalSignCertifyUseRangeDO();
        internalSignCertifyUseRangeDO.setUseStatus(useStatus);
        if (Objects.equals(UseStatusEnum.OFF.getStatus(), useStatus)) {
            setDepartmentIdsToEmpty = true;
        }
        internalSignCertifyUseRangeDAO.updateByBestSignAccount(oldDo.getBestSignAccount(), internalSignCertifyUseRangeDO, setDepartmentIdsToEmpty);
    }

    /**
     * 获取用户的部门签章帐号（已认证+启用）（从用户主属部门往上找）
     */
    public Optional<String> getBestSignAccountByUserId(String tenantId, String userId) {
        List<String> parentDeptIds = getParentDeptIdsByUserId(tenantId, userId);
        List<InternalSignCertifyUseRangeDO> rangeDOList = internalSignCertifyUseRangeDAO.queryByTenantIdAndDeptIds(tenantId, parentDeptIds);
        return getBestSignAccountByUserId(parentDeptIds, rangeDOList);
    }

    /**
     * 获取指定部门签章帐号（已认证+启用）（从指定部门往上找）
     */
    public Optional<String> getBestSignAccountByDeptId(String tenantId, String deptId) {
        List<String> parentDeptIds = getParentDeptIdsByDeptId(tenantId, deptId);
        List<InternalSignCertifyUseRangeDO> rangeDOList = internalSignCertifyUseRangeDAO.queryByTenantIdAndDeptIds(tenantId, parentDeptIds);
        return getBestSignAccountByUserId(parentDeptIds, rangeDOList);
    }


    private List<String> getParentDeptIdsByUserId(String tenantId, String userId) {
        List<GetNDeptPathByUserId.DeptInfo> deptInfoList = serviceFacade.getNDeptPathByUserId(new User(tenantId, userId), userId, 10000);
        return deptInfoList.stream().map(GetNDeptPathByUserId.DeptInfo::getDeptId).collect(Collectors.toList());
    }

    private List<String> getParentDeptIdsByDeptId(String tenantId, String deptId) {
        Map<String, List<QueryAllSuperDeptsByDeptIds.DeptInfo>> deptMap = serviceFacade.getAllSuperDeptsByDeptIds(tenantId, User.SUPPER_ADMIN_USER_ID, Lists.newArrayList(deptId));
        List<QueryAllSuperDeptsByDeptIds.DeptInfo> deptInfoList = deptMap.get(deptId);
        return deptInfoList.stream().map(QueryAllSuperDeptsByDeptIds.DeptInfo::getId).collect(Collectors.toList());
    }

    private Optional<String> getBestSignAccountByUserId(List<String> parentDeptIds, List<InternalSignCertifyUseRangeDO> rangeDOList) {
        for (String deptId : parentDeptIds) {
            for (InternalSignCertifyUseRangeDO rangeDO : rangeDOList) {
                if (CollectionUtils.isNotEmpty(rangeDO.getDepartmentIds())
                        && rangeDO.getDepartmentIds().contains(deptId)
                        && Objects.equals(rangeDO.getCertifyStatus(), CertifyStatusEnum.CRTTIFIED.getStatus())
                        && Objects.equals(rangeDO.getUseStatus(), UseStatusEnum.ON.getStatus())) {
                    return Optional.of(rangeDO.getBestSignAccount());
                }
            }
        }
        return Optional.empty();
    }

    public void setRange(String tenantId, IObjectData internalSignCertifyObjectData, List<String> departmentIds) {
        InternalSignCertifyUseRangeDO internalSignCertifyUseRangeDO = new InternalSignCertifyUseRangeDO();
        if (CollectionUtils.isEmpty(departmentIds)) {
            internalSignCertifyUseRangeDO.setDepartmentIds(new ArrayList<>(0));
        } else {
            internalSignCertifyUseRangeDO.setDepartmentIds(departmentIds);
        }
        internalSignCertifyUseRangeDO.setTenantId(tenantId);
        internalSignCertifyUseRangeDO.setInternalSignCertifyId(internalSignCertifyObjectData.getId());
        internalSignCertifyUseRangeDO.setBestSignAccount(internalSignCertifyObjectData.get(InternalSignCertifyObjConstants.Field.BestSignAccount.apiName, String.class));
        internalSignCertifyUseRangeDO.setCertifyStatus(internalSignCertifyObjectData.get(InternalSignCertifyObjConstants.Field.CertifyStatus.apiName, String.class));
        internalSignCertifyUseRangeDO.setUseStatus(internalSignCertifyObjectData.get(InternalSignCertifyObjConstants.Field.UseStatus.apiName, String.class));
        long currentTime = System.currentTimeMillis();
        internalSignCertifyUseRangeDO.setCreateTime(currentTime);
        internalSignCertifyUseRangeDO.setUpdateTime(currentTime);
        internalSignCertifyUseRangeDAO.updateDepartments(internalSignCertifyUseRangeDO);
    }

}
