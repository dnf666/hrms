package com.mis.hrm.login.service.imp;

import com.mis.hrm.index.dao.IndexMapper;
import com.mis.hrm.index.entity.Index;
import com.mis.hrm.login.dao.CompanyMapper;
import com.mis.hrm.login.dao.TypeMapper;
import com.mis.hrm.login.entity.Company;
import com.mis.hrm.login.exception.AuthorizationException;
import com.mis.hrm.login.service.CompanyService;
import com.mis.hrm.manage.dao.ManageMapper;
import com.mis.hrm.manage.model.Management;
import com.mis.hrm.util.EncryptionUtil;
import static com.mis.hrm.util.EncryptionUtil.md5;
import static com.mis.hrm.util.ServiceUtil.checkSqlExecution;
import com.mis.hrm.util.exception.ParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

/**
 * @author May
 */
@Service
public class CompanyServiceImp implements CompanyService {
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private IndexMapper indexMapper;
    @Resource
    private TypeMapper typeMapper;
    @Resource
    private ManageMapper manageMapper;

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

    private void insertIndex(Company record) {
        Index index = new Index();
        index.setCompanyId(record.getEmail());
        checkSqlExecution(indexMapper.insert(index));
    }

    private void checkCompanyNull(Company company) {
        if (company != null) {
            throw new ParameterException("邮箱已经被注册过了");
        }
    }

    private void getEncodePasswordCompany(Company company) {
        Objects.requireNonNull(company);
        String encryptionPassword = md5(company.getPassword());
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

    public Management checkCompany(Management management) {
        Integer permission = management.getPermission();
        String email = management.getEmail();
        String password = management.getPassword();
        String ecrypt = EncryptionUtil.md5(password);
        //成员 默认只能加一个部门
        if (permission == 2) {
            Management result = manageMapper.selectByEmail(email);
            if (result == null) {
                throw new AuthorizationException("不存在该用户");
            }
            if (!result.getPassword().equals(ecrypt)) {
                throw new AuthorizationException("密码错误");
            }
            return result;
        } else if (permission == 1) {
            Company company = Company.builder().email(email).build();
            Company getCompany = selectByPrimaryKey(company);
            checkCompanyNotNull(getCompany);
            checkPassword(ecrypt, getCompany);
            Management result = Management.builder().companyId(getCompany.getEmail()).permission(1).build();
            return result;
        } else {
            throw new AuthorizationException("不识别的角色");
        }

    }

    private void checkCompanyNotNull(Company getCompany) {
        if (getCompany == null) {
            throw new AuthorizationException("用户名不存在");
        }
    }

    private void checkPassword(String encrpt, Company getCompany) {
        if (!getCompany.getPassword().equals(encrpt)) {
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
