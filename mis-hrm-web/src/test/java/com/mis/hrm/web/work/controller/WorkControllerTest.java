package com.mis.hrm.web.work.controller;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

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
public class WorkControllerTest {
    @Resource
    private WorkController workController;

    @Test
    public void insertOneWorker() {
    }

    @Test
    public void deleteByNums() {
    }

    @Test
    public void updateOneWorker() {
    }

    @Test
    public void countWorkers() {
    }

    @Test
    public void getAllWorkers() {
    }

    @Test
    public void workFilter() {
    }

    @Test
    public void importMemberFromExcel() throws IOException {
        File file = new File("/Users/demo/Desktop/member.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        workController.importMemberFromExcel(multipartFile);
    }
}