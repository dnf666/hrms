package com.mis.hrm.login.service.imp;

import com.mis.hrm.login.entity.Company;
import com.mis.hrm.util.exception.ParameterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test/spring-company-test.xml")
public class CompanyServiceImpTest {
    private Company company;

    @Autowired
    private CompanyServiceImp companyService;

    @Before
    public void before(){
        company = new Company("1122","1122","1122",
                "1122","1122","1122","1122");
    }

    @Test
    public void insertAndDelete() {
        companyService.insert(company);
        companyService.deleteByPrimaryKey(company);
    }

    @Test(expected = ParameterException.class)
    public void checkCompanyNull(){
        Company var = new Company("1234","1122","1122",
                "1122","1122","1122","1122");
        companyService.insert(var);
    }

    @Test
    public void selectByPrimaryKey() {
        Company company = new Company();
        company.setEmail("1234");
        Company var = companyService.selectByPrimaryKey(company);
        assertEquals("8148f9b39472a6c94db8416e96b36a72", var.getPassword());
    }

    @Test
    public void updateByPrimaryKey() {
        Company company = new Company();
        company.setName("12345");
        company.setEmail("1234");
        companyService.updateByPrimaryKey(company);
        company.setName("1234");
        companyService.updateByPrimaryKey(company);
    }

    @Test
    public void checkCompany() {
        Company company = new Company();
        company.setEmail("1234");
        company.setPassword("8148f9b39472a6c94db8416e96b36a72");
        companyService.checkCompany(company);
    }
}