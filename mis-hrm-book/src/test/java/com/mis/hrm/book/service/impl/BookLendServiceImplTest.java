package com.mis.hrm.book.service.impl;


import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.book.service.BookLendService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-book-test.xml")
public class BookLendServiceImplTest {
    @Autowired
    private BookLendService bookLendService;
    private BookLendInfo bookLendInfo;

    @Before
    public void setUp() throws Exception {
        bookLendInfo = BookLendInfo.builder()
                .companyId("ddd")
                .bookRecord(LocalDateTime.now().toString())
                .bookName("java虚拟机")
                .lendTime(LocalDateTime.now().toString())
                .borrower("小明").build();
    }

    @Test
    public void selectBookLendInfosByBorrower(){
        String borrower = "liudong";
        Assert.assertEquals(6, bookLendService.selectBookLendInfosByBorrower(borrower).size());
        borrower = null;
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByBorrower(borrower));
        borrower = " ";
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByBorrower(borrower));
    }

    @Test
    public void selectBookLendInfosByCompanyId(){
        String companyId = "111";
        Assert.assertEquals(3, bookLendService.selectBookLendInfosByCompanyId(companyId).size());
        companyId = null;
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByCompanyId(companyId));
        companyId = " ";
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByCompanyId(companyId));
    }

    @Test
    public void selectBookLendInfosByCompanyIdAndBookName(){
        String bookName = "abcd";
        String companyId = "123";
        Assert.assertEquals(1,bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName).size());
        bookName = "abcd";
        companyId = " ";
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName));
        bookName = "abcd";
        companyId = " ";
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName));

        bookName = " ";
        companyId = "123";
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName));

        bookName = "";
        companyId = " ";
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName));

        bookName = null;
        companyId = null;
        Assert.assertEquals(null, bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName));
    }

    @Test
    public void selectAll(){
        Assert.assertEquals(6, bookLendService.selectAll().size());
    }

    @Test
    public void deleteByPrimaryKey(){
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("223");
        //先把要删的保存下来，以便后续的单元测试
        BookLendInfo saveBundle = bookLendService.selectByPrimaryKey(bookLendInfo);
        Assert.assertEquals(1, bookLendService.deleteByPrimaryKey(bookLendInfo));
        bookLendService.insert(saveBundle);

        bookLendInfo.setCompanyId(" ");
        bookLendInfo.setBookRecord("223");
        Assert.assertEquals(0, bookLendService.deleteByPrimaryKey(bookLendInfo));

        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord(" ");
        Assert.assertEquals(0, bookLendService.deleteByPrimaryKey(bookLendInfo));

        bookLendInfo.setCompanyId(null);
        bookLendInfo.setBookRecord(null);
        Assert.assertEquals(0, bookLendService.deleteByPrimaryKey(bookLendInfo));
    }

    @Test
    public void insert(){
        bookLendInfo.setCompanyId("12321");
        bookLendInfo.setBookRecord("22322");
        Assert.assertEquals(1, bookLendService.insert(bookLendInfo));

        bookLendInfo.setCompanyId("12021");
        bookLendInfo.setBookRecord("22022");
        bookLendInfo.setLendTime(null);
        Assert.assertEquals(0, bookLendService.insert(bookLendInfo));
    }

    @Test
    public void selectByPrimaryKey(){
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("223");
        Assert.assertEquals("2018-08-05 08:24:23.0", bookLendService.selectByPrimaryKey(bookLendInfo).getLendTime());

        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord(" ");
        Assert.assertEquals(null, bookLendService.selectByPrimaryKey(bookLendInfo));

        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord(null);
        Assert.assertEquals(null, bookLendService.selectByPrimaryKey(bookLendInfo));
    }

    @Test
    public void updateByPrimaryKey(){
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("223");
        bookLendInfo.setBookName("大主宰");
        Assert.assertEquals(1, bookLendService.updateByPrimaryKey(bookLendInfo));

        bookLendInfo.setCompanyId(" ");
        bookLendInfo.setBookRecord("223");
        Assert.assertEquals(0, bookLendService.updateByPrimaryKey(bookLendInfo));

        bookLendInfo.setCompanyId(null);
        bookLendInfo.setBookRecord("223");
        Assert.assertEquals(0, bookLendService.updateByPrimaryKey(bookLendInfo));
    }

}
