package com.mis.hrm.login.service.imp;

import com.mis.hrm.login.dao.CompanyMapper;
import com.mis.hrm.login.entity.Company;
import com.mis.hrm.login.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import static com.mis.hrm.util.ServiceUtil.*;
import static com.mis.hrm.util.EncryptionUtil.mac;

/**
 * @author May
 */
public class CompanyServieImp implements CompanyService {
    private static String KEY = "may";

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public void deleteByPrimaryKey(Company key) {
        checkSqlExecution(companyMapper.deleteByPrimaryKey(key));
    }

    @Override
    public void insert(Company record) {
        getEncodePasswordCompany(record);
        checkSqlExecution(companyMapper.insert(record));
    }

    private void getEncodePasswordCompany(Company company){
        String encryptionPassword = mac(company.getPassword(), KEY);
        company.setPassword(encryptionPassword);
    }

    @Override
    public List<Company> selectByPrimaryKey(Company key) {
        return companyMapper.selectByPrimaryKey(key);
    }

    @Override
    public void updateByPrimaryKey(Company record) {
        checkSqlExecution(companyMapper.updateByPrimaryKey(record));
    }
}
