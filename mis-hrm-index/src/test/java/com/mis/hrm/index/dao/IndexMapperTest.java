package com.mis.hrm.index.dao;

import com.mis.hrm.index.entity.Index;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test/spring-index-test.xml")
public class IndexMapperTest {

    private Index index;

    @Autowired
    private IndexMapper indexMapper;

    @Before
    public void before(){
        index = new Index("2233","2233","2233");
    }


    @Test
    public void insertAndDelete(){
        boolean flag = indexMapper.insert(index);
        assertTrue(flag);
        boolean flag2 = indexMapper.deleteByPrimaryKey(index);
        assertTrue(flag2);
    }

    @Test
    public void selectByPrimaryKey(){
        Index var = new Index();
        var.setCompanyId("1234");
        Index var1 = indexMapper.selectByPrimaryKey(var);
        assertEquals("1234", var1.getOutline());
    }

    @Test
    public void updateByPrimaryKey(){
        Index index = new Index();
        index.setCompanyId("1234");
        index.setOutline("12345");
        boolean flag = indexMapper.updateByPrimaryKey(index);
        assertTrue(flag);
        index.setOutline("1234");
        indexMapper.updateByPrimaryKey(index);
    }

}