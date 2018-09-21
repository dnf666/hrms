package com.mis.hrm.book.daoTest;

import com.mis.hrm.book.dao.BookLendInfoMapper;
import com.mis.hrm.book.po.BookLendInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-book-test.xml")
public class BookLendInfoMapperDao {
    private BookLendInfo bookLendInfo;
    @Autowired
    private BookLendInfoMapper blim;

    @Before
    public void setUp() throws Exception {
        bookLendInfo = BookLendInfo.builder()
                .bookName("你的灯亮着吗？")
                .bookRecord("aaaaa")
                .borrower("liudong")
                .companyId("信管工作室")
                .lendTime(new Date().toString())
                .build();
    }

    @Test
    public void deleteByPrimaryKey() {
        int rightResult = 1;
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("234");
        int result = blim.deleteByPrimaryKey(bookLendInfo);
        Assert.assertEquals(rightResult, result);
    }

    @Test
    public void insert() {
        int rightResult = 1;
        int result = blim.insert(bookLendInfo);
        Assert.assertEquals(rightResult, result);
    }

    @Test
    public void selectByPrimaryKey() {
        String expectedCompanyId = "you light is open";
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("234");
        String acture = blim.selectByPrimaryKey(bookLendInfo).getBookName();
        Assert.assertEquals(expectedCompanyId, acture);
    }

    @Test
    public void updateByPrimaryKey() {
        bookLendInfo.setCompanyId("123");
        bookLendInfo.setBookRecord("234");
        bookLendInfo.setBookName("ok ok");
        int rowNum = blim.updateByPrimaryKey(bookLendInfo);
        Assert.assertEquals(1, rowNum);
    }

    @Test
    public void selectBookLendInfosByBorrower(){
        String borrower = "liudong";
        List<BookLendInfo> bookLendInfos = blim.selectBookLendInfosByBorrower(borrower);
        Assert.assertEquals(2, bookLendInfos.size());
    }

    @Test
    public void selectBookLendInfosByCompanyId(){
        String companyId = "123";
        List<BookLendInfo> bookLendInfos = blim.selectBookLendInfosByCompanyId(companyId);
        Assert.assertEquals(1, bookLendInfos.size());
    }

    @Test
    public void selectBookLendInfosByCompanyIdAndBookName(){
        String companyId = "111";
        String bookname = "abcd";
        List<BookLendInfo> bookLendInfos = blim.selectBookLendInfosByCompanyIdAndBookName(companyId, bookname);
        Assert.assertEquals(1, bookLendInfos.size());
    }

    @Test
    public void selectAll(){
        List<BookLendInfo> bookLendInfos = blim.selectAll();
        Assert.assertEquals(2, bookLendInfos.size());
    }

}
