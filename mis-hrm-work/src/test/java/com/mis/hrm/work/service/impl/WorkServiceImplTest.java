package com.mis.hrm.work.service.impl;

import com.mis.hrm.util.Pager;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.work.model.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.apache.ibatis.jdbc.Null;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-work.xml")
public class WorkServiceImplTest {
    @Autowired
    private WorkService workService;

    private Whereabout whereabout;
    private Whereabout blankWhereabout;

    private Pager<Whereabout> pager;

    @Before
    public void setUp() throws Exception {
        whereabout = Whereabout.builder()
                .companyId("信管工作室")
                .num("2014211009")
                .name("刘西")
                .phoneNumber("1111")
                .email("1111@126.com")
                .grade("2014级")
                .sex("男")
                .profession("信息管理与信息系统")
                .department("后台")
                .workPlace("头条").build();

        blankWhereabout = Whereabout.builder().build();

        pager = new Pager<>();
        pager.setPageSize(2);
        pager.setCurrentPage(2);
    }

    @Test(expected = RuntimeException.class)
//    @Ignore
    public void deleteByPrimaryKey() {
        whereabout.setNum("2017210002");
        Assert.assertEquals(1,workService.deleteByPrimaryKey(whereabout));

        whereabout.setNum("2017210004");
        Assert.assertEquals(0,workService.deleteByPrimaryKey(whereabout));

        Assert.assertEquals(0,workService.deleteByPrimaryKey(blankWhereabout));
    }

    @Test(expected =RuntimeException.class)
//    @Ignore
    public void insert() {
        Assert.assertEquals(1,workService.insert(whereabout));

        Assert.assertEquals(0,workService.insert(blankWhereabout));
    }

    @Test(expected = NullPointerException.class)
    public void selectByPrimaryKey() {
        whereabout.setNum("2017210003");
        Whereabout selectedOne = workService.selectByPrimaryKey(whereabout);
        Assert.assertEquals("王二",selectedOne.getName());
        Assert.assertEquals("1010",selectedOne.getPhoneNumber());

        whereabout.setNum("2017210004");
        Assert.assertEquals(null,workService.selectByPrimaryKey(whereabout));

        Assert.assertEquals(null,workService.selectByPrimaryKey(blankWhereabout));
    }

    @Test(expected = RuntimeException.class)
//    @Ignore
    public void updateByPrimaryKey() {
        whereabout.setNum("2017210001");
        Assert.assertEquals(1,workService.updateByPrimaryKey(whereabout));

        whereabout.setNum("2017210004");
        Assert.assertEquals(0,workService.updateByPrimaryKey(whereabout));

        Assert.assertEquals(0,workService.updateByPrimaryKey(blankWhereabout));
    }

    @Test(expected = RuntimeException.class)
//    @Ignore
    public void testDeleteByNums() {
//        Assert.assertEquals(3,workService.deleteByNums(
//                Arrays.asList("2017210001","2017210002","2017210003")
//        ));

//        Assert.assertEquals(0,workService.deleteByNums(new ArrayList<>()));
    }

    @Test
    public void countWorkers() {
        Assert.assertEquals((Long)(long)5,workService.countWorkers());
    }

    @Test
    public void getAllGraduates() {
        Assert.assertEquals(2,workService.getAllGraduates(pager,"111").size());

        pager.setCurrentPage(3);
        Assert.assertEquals(1,workService.getAllGraduates(pager,"111").size());
    }

    @Test
    public void testFilter() {
        blankWhereabout.setName("王");
        blankWhereabout.setPhoneNumber("101");
        pager.setCurrentPage(1);
        Assert.assertEquals("王二", workService.filter(pager, blankWhereabout).get(0).getName());

        blankWhereabout.setCompanyId("极客工作室");
        Assert.assertEquals(new ArrayList<>(), workService.filter(pager, blankWhereabout));
    }

    @Test(expected = InfoNotFullyException.class)
    public void testNullFilter() {
        workService.filter(pager, blankWhereabout);
    }
}