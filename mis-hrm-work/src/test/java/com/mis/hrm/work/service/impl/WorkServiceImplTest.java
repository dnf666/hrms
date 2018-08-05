package com.mis.hrm.work.service.impl;

import com.mis.hrm.work.pojos.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-work.xml")
public class WorkServiceImplTest {
    @Autowired
    private WorkService workService;

    @Test
    public void testDeleteByPrimaryKey() {
        Whereabout whereabout = new Whereabout();
        whereabout.setCompanyId("2345314");
        whereabout.setNum("321421");
        Assert.assertEquals(1,workService.deleteByPrimaryKey(whereabout));
    }

    @Test
    public void testInsert() {
        Whereabout whereabout = new Whereabout("234","5231","gesFW","48931","GVEAOJE","fhwei583","o","jfiwoe","fhwioaj","jfwi");
        Assert.assertEquals(1,workService.insert(whereabout));
    }

    @Test
    public void testSelectByPrimaryKey() {
        Whereabout whereabout = new Whereabout();
        whereabout.setCompanyId("78923");
        whereabout.setNum("6783168");
        Whereabout selectOne = workService.selectByPrimaryKey(whereabout);
        Assert.assertEquals("fjkh",selectOne.getName());
    }

    @Test
    public void testUpdateByPrimaryKey() {
        Whereabout whereabout = new Whereabout("78923","6783168","gesFW","48931","GVEAOJE","fhwei583","o","jfiwoe","fhwioaj","jfwi");
        Assert.assertEquals(1,workService.updateByPrimaryKey(whereabout));
    }
}