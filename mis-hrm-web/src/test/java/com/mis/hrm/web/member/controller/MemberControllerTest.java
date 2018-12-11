package com.mis.hrm.web.member.controller;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.annotation.Resource;

/**
 * created by dailf on 2018/12/8
 *
 * @author dailf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-web.xml")
public class MemberControllerTest {
    @Resource
    private MemberController memberController;
    @Test
    public void insertExcel() throws FileNotFoundException {
        File file = new File("/Users/demo/Desktop/member.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = null;
        try {
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        memberController.importMemberFromExcel(multipartFile);
    }
}