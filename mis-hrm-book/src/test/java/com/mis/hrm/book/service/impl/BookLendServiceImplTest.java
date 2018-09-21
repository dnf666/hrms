package com.mis.hrm.book.service.impl;


import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.book.service.BookLendService;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Date;

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
                .lendTime(new Date().toString())
                .borrower("小明").build();
    }

    @Test
    public void selectBookLendInfosByBorrower(){
        String borrower = "liudong";
        try {
            Assert.assertEquals(6, bookLendService.selectBookLendInfosByBorrower(borrower).size());
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }

        borrower = null;
        String msg = "";
        try {
            bookLendService.selectBookLendInfosByBorrower(borrower);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("借书者信息不全", msg);

        borrower = " ";
        try {
            bookLendService.selectBookLendInfosByBorrower(borrower);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("借书者信息不全", msg);
    }

    @Test
    public void selectBookLendInfosByCompanyId(){
        String companyId = "111";
        try {
            Assert.assertEquals(3, bookLendService.selectBookLendInfosByCompanyId(companyId).size());
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }
        companyId = null;
        String msg = "";
        try {
            bookLendService.selectBookLendInfosByCompanyId(companyId);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId信息不全", msg);

        companyId = " ";
        try {
            bookLendService.selectBookLendInfosByCompanyId(companyId);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId信息不全", msg);
    }

    @Test
    public void selectBookLendInfosByCompanyIdAndBookName(){
        String bookName = "abcd";
        String companyId = "123";
        try {
            Assert.assertEquals(1,bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName).size());
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }
        String msg = "";
        bookName = "abcd";
        companyId = " ";
        try {
            bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);
        bookName = "abcd";
        companyId = " ";
        try {
            bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);

        bookName = " ";
        companyId = "123";
        try {
            bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);

        bookName = "";
        companyId = " ";
        try {
            bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);

        bookName = null;
        companyId = null;
        try {
            bookLendService.selectBookLendInfosByCompanyIdAndBookName(companyId,bookName);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);
    }

    @Test
    public void selectAll(){
        Assert.assertEquals(6, bookLendService.selectAll().size());
    }

    @Test
    public void deleteByPrimaryKey() {
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("223");
        //先把要删的保存下来，以便后续的单元测试
        BookLendInfo saveBundle = null;
        try {
            saveBundle = bookLendService.selectByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }
        try {
            Assert.assertEquals(1, bookLendService.deleteByPrimaryKey(bookLendInfo));
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }
        try {
            bookLendService.insert(saveBundle);
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }

        String msg = "";
        bookLendInfo.setCompanyId(" ");
        bookLendInfo.setBookRecord("223");
        try {
            bookLendService.deleteByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("bookrecord or companyid is null", msg);

        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord(" ");
        try {
            bookLendService.deleteByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("bookrecord or companyid is null", msg);

        bookLendInfo.setCompanyId(null);
        bookLendInfo.setBookRecord(null);
        try {
            bookLendService.deleteByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("bookrecord or companyid is null", msg);

        bookLendInfo = null;
        try {
            bookLendService.deleteByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", msg);
    }

    @Test
    public void insert() {
//        bookLendInfo.setCompanyId("12321");
//        bookLendInfo.setBookRecord("22322");
//        try {
//            Assert.assertEquals(1, bookLendService.insert(bookLendInfo));
//        } catch (InfoNotFullyException infoNotFullyException) {
//            infoNotFullyException.printStackTrace();
//        }

        bookLendInfo.setCompanyId("12021");
        bookLendInfo.setBookRecord("22022");
        bookLendInfo.setLendTime(new Date().toString()
        );
        bookLendInfo.setBookName("java虚拟机");
        String msg = "";
        try {
            bookLendService.insert(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
//        Assert.assertEquals("传入的基本信息不全", msg);

//        bookLendInfo = null;
//        try {
//            bookLendService.insert(bookLendInfo);
//        } catch (InfoNotFullyException infoNotFullyException) {
//            msg = infoNotFullyException.getMessage();
//        } catch (NullPointerException n){
//            msg = n.getMessage();
//        }
//        Assert.assertEquals("传入对象为空", msg);
    }

    @Test
    public void selectByPrimaryKey() {
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("223");
        try {
            Assert.assertEquals("2018-08-05 08:24:23.0", bookLendService.selectByPrimaryKey(bookLendInfo).getLendTime());
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }

        String msg = "";
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord(" ");
        try {
            bookLendService.selectByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyid or bookrecord is null", msg);

        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord(null);
        try {
            bookLendService.selectByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyid or bookrecord is null", msg);
        bookLendInfo = null;
        try {
            bookLendService.selectByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", msg);
    }

    @Test
    public void updateByPrimaryKey() {
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("223");
        bookLendInfo.setBookName("大主宰");
        try {
            Assert.assertEquals(1, bookLendService.updateByPrimaryKey(bookLendInfo));
        } catch (InfoNotFullyException infoNotFullyException) {
            infoNotFullyException.printStackTrace();
        }

        String msg = "";
        bookLendInfo.setCompanyId(" ");
        bookLendInfo.setBookRecord("223");
        try {
            bookLendService.updateByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId or bookrecorder is null", msg);

        bookLendInfo.setCompanyId(null);
        bookLendInfo.setBookRecord("223");
        try {
            bookLendService.updateByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        }
        Assert.assertEquals("companyId or bookrecorder is null", msg);
        bookLendInfo = null;
        try {
            bookLendService.updateByPrimaryKey(bookLendInfo);
        } catch (InfoNotFullyException infoNotFullyException) {
            msg = infoNotFullyException.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", msg);
    }

}
