package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.service.MemberService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.annotation.Resource;

/**
 * created by dailf on 2018/10/27
 *
 * @author dailf
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-web.xml")
public class MemberControllerTest {
    @Resource
    private MemberController memberController;
    @Resource
    private MemberService memberService;
    @Before
    public void setUp() throws Exception {
    }

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
    }

    @Test
    public void exitToWhere() {
    }

    @Test
    public void importMemberFromExcel() throws IOException {
        File file = new File("/Users/demo/Desktop/member.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        memberController.importMemberFromExcel(multipartFile);
    }
}