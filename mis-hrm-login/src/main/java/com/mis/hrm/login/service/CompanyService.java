package com.mis.hrm.login.service;

import com.mis.hrm.login.entity.Company;
import com.mis.hrm.util.BaseServiceByMay;

import java.util.List;

/**
 * @author May
 */
public interface CompanyService extends BaseServiceByMay<Company> {
    List<String> getMajorType();

    List<String> getViceType(String majorType);
}
