package com.mis.hrm.web.book.controller;

import com.mis.hrm.book.service.BookService;
import com.mis.hrm.util.model.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.xml.crypto.Data;

import static org.junit.Assert.*;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-web.xml")
@Slf4j
public class BookControllerTest {
    @Resource
    private BookService bookService;
    @Test
    public void insert(){
        Demo demo = Demo.builder().name("111111").password("1111").build();
        int i = bookService.insert(demo);
        log.info(""+i);
    }
    @Test
    public void select(){
        Demo demo = Demo.builder().name("111111").password("1111").build();
        Demo demo1 = bookService.selectByPrimaryKey(demo);
        log.info(demo1.toString());
    }
}