package com.facishare.crm.electronicsign.predefine.dao;

import com.facishare.crm.electronicsign.predefine.model.InternalSignCertifyUseRangeDO;
import org.mongodb.morphia.query.UpdateResults;

import java.util.List;

public interface InternalSignCertifyUseRangeDAO extends BaseDao<InternalSignCertifyUseRangeDO> {

    /**
     * 更新部门（若不存则插入）
     */
    UpdateResults updateDepartments(InternalSignCertifyUseRangeDO entity);

    int updateByBestSignAccount(String bestSignAccount, InternalSignCertifyUseRangeDO entity, boolean setDepartmentIdsToEmpty);

    List<InternalSignCertifyUseRangeDO> queryByTenantIdAndDeptIds(String tenantId, List<String> deptIds);

    InternalSignCertifyUseRangeDO queryByInternalSignCertifyId(String internalSignCertifyId);

    void deleteByInternalSignCertifyId(String internalSignCertifyId);
}
