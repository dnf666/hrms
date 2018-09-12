package com.mis.hrm.index.service.impl;

import com.mis.hrm.index.entity.Index;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.mail.Session;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.FileSystem;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test/spring-index-test.xml")
@EnableWebMvc
public class IndexServiceImplTest {
    @Autowired
    private IndexServiceImpl indexService;

    @Autowired
    private ApplicationContext ac;

    private WebApplicationContext wac;

    private Index index;

    @Before
    public void before(){
        index = new Index("1122", "1122", "1122");
    }

    @Test
    public void insertAndDelete(){
        indexService.insert(index);
        indexService.deleteByPrimaryKey(index);
    }

    @Test
    public void selectByPrimaryKey() {
        Index index = new Index();
        index.setCompanyId("1234");
        Index var = indexService.selectByPrimaryKey(index);
        assertEquals("1234", var.getOutline());
    }

    @Test
    public void updateByPrimaryKey() {
        Index index = new Index();
        index.setCompanyId("1234");
        index.setOutline("12345");
        indexService.updateByPrimaryKey(index);
        index.setOutline("1234");
        indexService.updateByPrimaryKey(index);
    }

    @Test
    public void updatePhoto() throws IOException {
    }
}