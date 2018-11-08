package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.model.Member;
import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * created by dailf on 2018/11/8
 *
 * @author dailf
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-web.xml")
@Slf4j
public class MemberControllerTest {
    @Autowired
    private MemberController memberController;

    @Test
    public void insertOneMember() {
    }

    @Test
    public void deleteByNums() {
    }

    @Test
    public void updateOneMember() {
    }

    @Test
    public void countMembers() {
    }

    @Test
    public void memberFilter() {
        Member member = Member.builder().num("1").build();
        memberController.deleteByNums(member,"1204695257@qq.com");
    }
}