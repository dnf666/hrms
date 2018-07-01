package com.facishare.crm.electronicsign.predefine.dao;

import com.facishare.crm.electronicsign.predefine.model.CertifyRecordDO;
import com.facishare.crm.electronicsign.predefine.model.vo.CertifyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;

import java.util.List;


public interface CertifyRecordDAO extends BaseDao<CertifyRecordDO> {

    /**
     * 分页获取符合条件的认证记录
     * @param certifyRecordDO 条件
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pager 分页条件
     * @return 认证记录集合
     */
    List<CertifyRecordDO> getCertifyRecordByConditionAndPage(CertifyRecordDO certifyRecordDO, Long startTime, Long endTime, Pager<CertifyRecordVO> pager);

    /**
     * 查询符合条件的记录总数
     * @param certifyRecordDO 条件
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 记录
     */
    int getCertifyRecordByConditionCount(CertifyRecordDO certifyRecordDO, Long startTime, Long endTime);

    /**
     * 根据taskId
     */
    CertifyRecordDO queryByTaskId(String taskId);

    int updateById(String id, CertifyRecordDO entity);
}
