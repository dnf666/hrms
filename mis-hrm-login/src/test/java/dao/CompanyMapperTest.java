package dao;

import com.mis.hrm.login.dao.CompanyMapper;
import com.mis.hrm.login.entity.Company;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author May
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test/spring-company-test.xml")
public class CompanyMapperTest {
    @Autowired
    private CompanyMapper companyMapper;
    private Company company;

    @Before
    public void before(){
        company = new Company("123456","123","234",
                "1234","123","123",
                "123");
    }

    @Test
    public void insertAndDeleteTest(){
        boolean flag = companyMapper.insert(company);
        Assert.assertEquals(true, flag);
        boolean deFlag = companyMapper.deleteByPrimaryKey(company);
        Assert.assertEquals(true, deFlag);
    }

    @Test
    public void selectByPrimaryKeyTest(){
        Company company = new Company();
        company.setEmail("1234");
        Company getCompany = companyMapper.selectByPrimaryKey(company);
        assertEquals("1234", getCompany.getName());
    }

    @Test
    public void updateByPrimaryKeyTest(){
        Company updateCompany = new Company();
        updateCompany.setEmail("1234");
        updateCompany.setPassword("12345");
        boolean flag = companyMapper.updateByPrimaryKey(updateCompany);
        assertEquals(true, flag);
    }
}
