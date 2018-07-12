package com.mis.hrm.book.controller;

import com.mis.hrm.book.dao.DemoMapper;
import com.mis.hrm.book.model.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@ContextConfiguration("classpath:spring/spring-book.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class DemoControllerTest {
    @Resource
    private DemoMapper demoMapper;

    @Test
    public void addDemo() {
        Demo demo = Demo.builder().name("12131").password("123").build();
        System.out.println(demoMapper.saveDemo(demo));
    }

    @Test
    public void findDemo() {
        log.info("111111111111");
        Demo demo = demoMapper.findDemoByPrimaryKey("12131");
        System.out.println(demo);
    }


}