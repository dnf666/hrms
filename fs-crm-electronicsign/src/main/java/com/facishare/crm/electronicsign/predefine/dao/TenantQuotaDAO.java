package com.facishare.crm.electronicsign.predefine.dao;

import com.facishare.crm.electronicsign.predefine.model.TenantQuotaDO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.model.vo.TenantQuotaVO;

import org.mongodb.morphia.query.UpdateResults;

import java.util.List;

public interface TenantQuotaDAO extends BaseDao<TenantQuotaDO> {
    /**
     * 查询
     */
    List<TenantQuotaDO> getByTenantId(String tenantId);

    /**
     * 更新usedQuota
     */
    int updateUsedQuota(String id, Integer usedQuota, Long lastUpdateTime);

    /**
     * 分配配额
     * @return 更新结果
     */
    int updateBuyQuotaByTenantIdAndQuotaType(TenantQuotaDO tenantQuotaDO) ;

    /**
     * 根据quotaType 分页查找租户配额
     * @param tenantQuotaDO 配额类型的对象
     * @return
     */
    List<TenantQuotaDO> getTenantQuotaByQuotaTypeAndPage(TenantQuotaDO tenantQuotaDO,Pager<TenantQuotaVO> pager);


    int getTenantQuotaByQuotaTypeCounts(TenantQuotaDO tenantQuotaDO);
}
