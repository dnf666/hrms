package com.facishare.crm.electronicsign.predefine.dao;

import com.facishare.crm.electronicsign.predefine.model.BuyRecordDO;
import com.facishare.crm.electronicsign.predefine.model.vo.BuyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;

import java.util.List;

/**
 * @author dailf
 */
public interface BuyRecordDAO extends BaseDao<BuyRecordDO> {

    /**
     * 按条件查看租户的购买信息
     * @param buyRecordDO 查询条件
     * @param startTime 开始时间
     * @param endTime 终止时间
     * @return 购买信息
     */
    List<BuyRecordDO> getTenantBuyQuotaRecordsByConditionAndPage(BuyRecordDO buyRecordDO, Long startTime, Long endTime, Pager<BuyRecordVO> pager);

    int getTenantBuyQuotaRecordsByConditionCount(BuyRecordDO buyRecordDO, Long startTime, Long endTime);
}
