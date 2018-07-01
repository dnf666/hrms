package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.predefine.dao.InternalSignCertifyUseRangeDAO;
import com.facishare.crm.electronicsign.predefine.manager.InternalSignCertifyUseRangeManager;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.crm.electronicsign.predefine.model.InternalSignCertifyUseRangeDO;
import com.facishare.crm.electronicsign.predefine.model.vo.InternalSignCertifyUseRangeVO;
import com.facishare.crm.electronicsign.predefine.service.InternalSignCertifyUseRangeService;
import com.facishare.crm.electronicsign.predefine.service.dto.InternalSignCertifyUseRangeType;
import com.facishare.crm.electronicsign.util.CopyUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InternalSignCertifyUseRangeServiceImpl implements InternalSignCertifyUseRangeService {
    @Resource
    private InternalSignCertifyObjManager internalSignCertifyObjManager;
    @Resource
    private InternalSignCertifyUseRangeDAO internalSignCertifyUseRangeDAO;
    @Resource
    private InternalSignCertifyUseRangeManager internalSignCertifyUseRangeManager;

    @Override
    public InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.Result getInternalSignCertifyUseRangeSettingList(ServiceContext serviceContext) {
        List<InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.SignAccountData> signAccountDataList = Lists.newArrayList();

        List<IObjectData> objectDataList = internalSignCertifyObjManager.queryByTenantId(serviceContext.getUser());
        Map<String, InternalSignCertifyUseRangeVO> internalSignCertifyId2VOMap = getInternalSignCertifyId2RangeVOMap(serviceContext.getTenantId());
        objectDataList.forEach(objectData -> {
            if (!Objects.equals(objectData.get(InternalSignCertifyObjConstants.Field.UseStatus.apiName, String.class), UseStatusEnum.ON.getStatus())) {
                return;
            }
            InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.SignAccountData signAccountData = new InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.SignAccountData();
            signAccountData.setInternalSignCertifyId(objectData.getId());
            signAccountData.setEnterpriseName(objectData.get(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName, String.class));
            signAccountData.setInternalSignCertifyUseRange(internalSignCertifyId2VOMap.get(objectData.getId()));
            signAccountDataList.add(signAccountData);
        });

        InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.Result result = new InternalSignCertifyUseRangeType.GetInternalSignCertifyUseRangeSettingList.Result();
        result.setSignAccountDataList(signAccountDataList);
        return result;
    }

    private Map<String, InternalSignCertifyUseRangeVO> getInternalSignCertifyId2RangeVOMap(String tenantId) {
        InternalSignCertifyUseRangeDO condition = new InternalSignCertifyUseRangeDO();
        condition.setTenantId(tenantId);
        List<InternalSignCertifyUseRangeDO> allUseRangeDOList = internalSignCertifyUseRangeDAO.queryList(condition);
        List<InternalSignCertifyUseRangeVO> allUseRangeVOList = CopyUtil.copyMany(InternalSignCertifyUseRangeVO.class, allUseRangeDOList);
        return allUseRangeVOList.stream().collect(Collectors.toMap(InternalSignCertifyUseRangeVO::getInternalSignCertifyId, Function.identity(), (o, n) -> n));
    }

    @Override
    public InternalSignCertifyUseRangeType.SetUseRange.Result setUseRange(ServiceContext serviceContext, InternalSignCertifyUseRangeType.SetUseRange.Arg arg) {
        Preconditions.checkNotNull(arg, "arg is null");
        Preconditions.checkNotNull(arg.getSettingMap(), "arg.settingMap is null");
        // todo 校验用户CRM管理员权限

        // 部门重复校验
        checkRepeatDepartment(serviceContext.getTenantId(), arg.getSettingMap());

        // 设置部门范围
        arg.getSettingMap().forEach((key, value) -> {
            IObjectData objectData = internalSignCertifyObjManager.getObjectDataById(serviceContext.getUser(), key);
            internalSignCertifyUseRangeManager.setRange(serviceContext.getTenantId(), objectData, value);
        });

        return new InternalSignCertifyUseRangeType.SetUseRange.Result();
    }

    private void checkRepeatDepartment(String tenantId, Map<String, List<String>> settingMap) {
        Set<String> existsDeptIdSet = new HashSet<>();
        Map<String, InternalSignCertifyUseRangeVO> internalSignCertifyId2VOMap = getInternalSignCertifyId2RangeVOMap(tenantId);
        internalSignCertifyId2VOMap.forEach((internalSignCertifyId, rangeVO) -> {
            if (!settingMap.containsKey(internalSignCertifyId)) {
                if (Objects.nonNull(rangeVO) && !CollectionUtils.isEmpty(rangeVO.getDepartmentIds())) {
                    existsDeptIdSet.addAll(rangeVO.getDepartmentIds());
                }
            }
        });

        // 校验是否有同部门在不同认证帐号的范围内
        settingMap.values().forEach(departmentIds -> {
            if (!CollectionUtils.isEmpty(departmentIds)) {
                departmentIds.forEach(departmentId -> {
                    if (!existsDeptIdSet.add(departmentId)) {
                        throw new ValidateException("部门["+departmentId+"]存在于不同的认证帐户");
                    }
                });
            }
        });
    }

}
