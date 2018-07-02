package com.facishare.crm.electronicsign.predefine.dao;

import com.facishare.crm.electronicsign.predefine.model.SignSettingDO;


public interface SignSettingDAO extends BaseDao<SignSettingDO> {

    void delete(SignSettingDO condition);

    void createOrUpdate(SignSettingDO signSettingDO);
}
