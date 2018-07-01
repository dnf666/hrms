package com.facishare.crm.electronicsign.predefine.dao;

import com.facishare.crm.electronicsign.predefine.model.SignRequestDO;

public interface SignRequestDAO extends BaseDao<SignRequestDO> {

    SignRequestDO queryByContractId(String contractId);

    /**
     * 更新
     */
    int updateById(String id, SignRequestDO entity);
}
