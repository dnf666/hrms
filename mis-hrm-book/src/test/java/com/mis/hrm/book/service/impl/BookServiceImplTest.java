package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookMapper;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * created by dailf on 2018/7/13
 *
 * @author dailf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-book-test.xml")
public class BookServiceImplTest {
    @Autowired
    private BookService bookService;
    private Book book;

    @Before
    public void setUp() throws Exception {
        book = Book.builder()
                .bookId("666")
                .bookName("how to solve it")
                .category("mathdd")
                .companyId("baidud")
                .quantity(22)
                .version("1.0.0.1")
                .build();
    }

    @Test
    public void deleteByPrimaryKey(){
        //一切正常的情况下
        book.setBookId("11112");
        Assert.assertEquals(1,bookService.deleteByPrimaryKey(book));
        //bookid为空的情况
        book.setBookId(null);
        Assert.assertEquals(0,bookService.deleteByPrimaryKey(book));
        //book为空的情况
        book = null;
        Assert.assertEquals(0,bookService.deleteByPrimaryKey(book));
    }

    @Test
    public void insert(){
        //一切正常的情况
        Assert.assertEquals(1, bookService.insert(book));
        //有一个不满足基本条件的情况
        book.setCompanyId(null);
        Assert.assertEquals(0, bookService.insert(book));
        //book为空的情况
        book = null;
        Assert.assertEquals(0, bookService.insert(book));
    }

    @Test
    public void selectByPrimaryKey(){
//        一切正常的情况
        book.setBookId("22222226");
        Assert.assertEquals("your light is shining3", bookService.selectByPrimaryKey(book).getBookName());
//        bookid不存在的时候
        book.setBookId(null);
        Assert.assertEquals(null, bookService.selectByPrimaryKey(book));
//        boo不存在的时候
        book = null;
        Assert.assertEquals(null, bookService.selectByPrimaryKey(book));
    }

    @Test
    public void updateByPrimaryKey(){
//        一切正常的情况
        book.setBookId("11111");
        book.setCategory("mathdd");
        book.setCompanyId(null);
        book.setQuantity(22);
        book.setVersion("1.0.0.1");
        Assert.assertEquals(1, bookService.updateByPrimaryKey(book));
//        bookid为空的时候
        book.setBookId(null);
        Assert.assertEquals(0, bookService.updateByPrimaryKey(book));
//        book为空的时候
        book = null;
        Assert.assertEquals(0, bookService.updateByPrimaryKey(book));
    }

    @Test
    public void selectBooksByCompanyId(){
//　　　　　一切正常的情况下
        book.setCompanyId("jike");
        Assert.assertEquals(5, bookService.selectBooksByCompanyId(book).size());
//        //bookcompanyid为空的情况下
        book.setCompanyId(null);
        Assert.assertEquals(null, bookService.selectBooksByCompanyId(book));
//        book为空的情况下
        book = null;
        Assert.assertEquals(null, bookService.selectBooksByCompanyId(book));
    }

    @Test
    public void selectBooksByComapnyIdAndCategory(){
        //　　　　　一切正常的情况下
        book.setCompanyId("jike");
        book.setCategory("siwei");
        Assert.assertEquals(4, bookService.selectBooksByComapnyIdAndCategory(book).size());
//        //bookcompanyid为空的情况下
        book.setCompanyId(null);
        Assert.assertEquals(null, bookService.selectBooksByComapnyIdAndCategory(book));
//        book为空的情况下
        book = null;
        Assert.assertEquals(null, bookService.selectBooksByComapnyIdAndCategory(book));
    }

    @Test
    public void selectBooksByComapnyIdAndBookName(){
        //　　　　　一切正常的情况下
        book.setCompanyId("jike");
        book.setBookName("your light is shining3");
        Assert.assertEquals(2, bookService.selectBooksByCompanyIdAndBookName(book).size());
//        //bookcompanyid为空的情况下
        book.setCompanyId(null);
        Assert.assertEquals(null, bookService.selectBooksByCompanyIdAndBookName(book));
//        book为空的情况下
        book = null;
        Assert.assertEquals(null, bookService.selectBooksByCompanyIdAndBookName(book));
    }

}