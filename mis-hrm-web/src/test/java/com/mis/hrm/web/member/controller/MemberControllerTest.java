package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.model.Member;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
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
    @Ignore
    public void insertExcel() throws FileNotFoundException {
        File file = new File("/Users/demo/Desktop/book.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = null;
        try {
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            System.out.println(multipartFile.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        memberController.importMemberFromExcel(multipartFile);
    }
    @Test
    public void selectByCompanyId(){
        Member member = Member.builder().companyId("12345@qq.com").build();
        System.out.println(memberController.getAllMember(member));
    }
}