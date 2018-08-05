package com.mis.hrm.work.dao;

import com.mis.hrm.work.pojos.Whereabout;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-work.xml")
public class WorkMapperTest {
    @Autowired
    private WorkMapper workMapper;

    @Test
    public void testDeleteByPrimaryKey() {
        Whereabout whereabout = new Whereabout();
        whereabout.setCompanyId("2345314");
        whereabout.setNum("321421");
        Assert.assertEquals(1,workMapper.deleteByPrimaryKey(whereabout));
    }

    @Test
    public void testInsert() {
        Whereabout whereabout = new Whereabout("234","5231","gesFW","48931","GVEAOJE","fhwei583","o","jfiwoe","fhwioaj","jfwi");
        Assert.assertEquals(1,workMapper.insert(whereabout));
    }

    @Test
    public void testSelectByPrimaryKey() {
        Whereabout whereabout = new Whereabout();
        whereabout.setCompanyId("2345314");
        whereabout.setNum("321421");
        Whereabout selectOne = workMapper.selectByPrimaryKey(whereabout);
        Assert.assertEquals("gerwaf",selectOne.getName());
    }

    @Test
    public void testUpdateByPrimaryKey() {
        Whereabout whereabout = new Whereabout("78923","6783168","gesFW","48931","GVEAOJE","fhwei583","o","jfiwoe","fhwioaj","jfwi");
        Assert.assertEquals(1,workMapper.updateByPrimaryKey(whereabout));
    }

}