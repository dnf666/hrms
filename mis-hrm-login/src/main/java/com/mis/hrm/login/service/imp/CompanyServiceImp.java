package com.mis.hrm.login.service.imp;

import com.mis.hrm.index.dao.IndexMapper;
import com.mis.hrm.index.entity.Index;
import com.mis.hrm.login.dao.CompanyMapper;
import com.mis.hrm.login.dao.TypeMapper;
import com.mis.hrm.login.entity.Company;
import com.mis.hrm.login.exception.AuthorizationException;
import com.mis.hrm.login.service.CompanyService;
import com.mis.hrm.util.exception.ParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.mis.hrm.util.EncryptionUtil.mac;
import static com.mis.hrm.util.ServiceUtil.checkSqlExecution;

import javax.annotation.Resource;

/**
 * @author May
 */
@Service
public class CompanyServiceImp implements CompanyService {
    private static String KEY = "may";

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private IndexMapper indexMapper;
    @Resource
    private TypeMapper typeMapper;
    @Override
    public void deleteByPrimaryKey(Company key) {
        checkSqlExecution(companyMapper.deleteByPrimaryKey(key));
    }

    @Override
    public void insert(Company record) {
        Company company = selectByPrimaryKey(record);
        checkCompanyNull(company);
        getEncodePasswordCompany(record);
        insertIndex(record);
        checkSqlExecution(companyMapper.insert(record));
    }

    private void insertIndex(Company record){
        Index index = new Index();
        index.setCompanyId(record.getEmail());
        checkSqlExecution(indexMapper.insert(index));
    }

    private void checkCompanyNull(Company company){
        if (company != null){
            throw new ParameterException("邮箱已经被注册过了");
        }
    }

    private void getEncodePasswordCompany(Company company){
        Objects.requireNonNull(company);
        String encryptionPassword = mac(company.getPassword(), KEY);
        company.setPassword(encryptionPassword);
    }

    @Override
    public Company selectByPrimaryKey(Company key) {
        return companyMapper.selectByPrimaryKey(key);
    }

    @Override
    public void updateByPrimaryKey(Company record) {
        checkSqlExecution(companyMapper.updateByPrimaryKey(record));
    }

    public void checkCompany(Company company){
        Company getCompany = selectByPrimaryKey(company);
        checkCompanyNotNull(getCompany);
        checkPassword(company, getCompany);
    }

    private void checkCompanyNotNull(Company getCompany){
        if (getCompany == null){
            throw new AuthorizationException("用户名不存在");
        }
    }

    private void checkPassword(Company company, Company getCompany){
        String encryptionPassword = mac(getCompany.getPassword(), KEY);
        if (company.getPassword().equals(encryptionPassword)){
            throw new AuthorizationException("密码错误");
        }
    }

    @Override
    public List<String> getMajorType() {
        return typeMapper.getMajorType();
    }

    @Override
    public List<String> getViceType(String majorType) {
        return typeMapper.getViceType(majorType);
    }
}
