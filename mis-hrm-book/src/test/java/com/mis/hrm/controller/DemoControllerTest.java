package com.mis.hrm.controller;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.beans.Beans;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@ContextConfiguration("classpath:spring/spring-book.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class DemoControllerTest {

}