package com.mis.hrm.manage.dao;

import com.mis.hrm.manage.model.Management;
import com.mis.hrm.util.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * created on 2019-02-28
 * @author dailinfu
 */

@Repository
public interface ManageMapper extends BaseMapper<Management> {
    Management selectByEmail(String email);

    Management selectByUniqueCompanyId(String email);
}
