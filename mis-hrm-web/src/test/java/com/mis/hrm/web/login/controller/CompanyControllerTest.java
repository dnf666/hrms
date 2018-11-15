package com.mis.hrm.web.login.controller;

import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by dailf on 2018/11/4
 *
 * @author dailf
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-web.xml")
@Slf4j
public class CompanyControllerTest {
    @Autowired
    private CompanyController companyController;

    @Test
    public void register() {
    }

    @Test
    public void login() {
    }

    @Test
    public void updateCompany() {
    }

    @Test
    public void deleteCompany() {
    }

    @Test
    public void getCompany() {
    }

    @Test
    public void getType() {
        System.out.println( companyController.getType());
    }
}