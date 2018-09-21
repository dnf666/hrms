package com.mis.hrm.work.dao;

import com.mis.hrm.util.Pager;
import com.mis.hrm.work.model.Whereabout;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-work.xml")
public class WorkMapperTest {
    @Autowired
    private WorkMapper workMapper;

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
                .workPlace("阿里").build();

        blankWhereabout = Whereabout.builder().build();

        pager = new Pager<>();
        pager.setPageSize(2);
        pager.setCurrentPage(2);
    }

    @Test
    public void testDeleteByPrimaryKey() {
        whereabout.setNum("2017210001");
        Assert.assertEquals(1,workMapper.deleteByPrimaryKey(whereabout));
    }

    @Test
    public void testInsert() {
        Assert.assertEquals(1,workMapper.insert(whereabout));
    }

    @Test
    public void testSelectByPrimaryKey() {
        whereabout.setNum("2017210004");
        Assert.assertEquals(null,workMapper.selectByPrimaryKey(whereabout));

        whereabout.setNum("2017210002");
        Assert.assertEquals("李四",workMapper.selectByPrimaryKey(whereabout).getName());
    }

    @Test
    public void testUpdateByPrimaryKey() {
        whereabout.setNum("2017210002");
        Assert.assertEquals(1,workMapper.updateByPrimaryKey(whereabout));

        whereabout.setNum("2017210004");
        Assert.assertEquals(0,workMapper.updateByPrimaryKey(whereabout));
    }

    @Test
    public void testDeleteByNums() {
        List<String> numList = Arrays.asList(
                "2017210001","2017210002","2017210003"
        );

        Assert.assertEquals(3,workMapper.deleteByNums(numList));
    }

    @Test
    public void countWorkers() {
        Assert.assertEquals((Long)(long)5,workMapper.countWorkers());
    }

    @Test
    public void getAllGraduates() {
        pager.setCurrentPage(3);
        Assert.assertEquals("小五",workMapper.getAllGraduates(pager,"11").get(0).getName());
    }

    @Test
    public void testFilter() {
        blankWhereabout.setName("王");
        blankWhereabout.setPhoneNumber("10");
        pager.setCurrentPage(1);
        Assert.assertEquals("王二",workMapper.filter(pager,blankWhereabout).get(0).getName());

        blankWhereabout.setCompanyId("极客工作室");
        Assert.assertEquals(new ArrayList<>(),workMapper.filter(pager,blankWhereabout));
    }
}