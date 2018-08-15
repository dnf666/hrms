package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


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
        try {
            Assert.assertEquals(1,bookService.deleteByPrimaryKey(book));
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }

        //bookid为空的情况
        book.setBookId(null);
        String message = "";
        try {
            bookService.deleteByPrimaryKey(book);
        }catch (InfoNotFullyExpection e){
            message = e.getMessage();
        }
        Assert.assertEquals("bookId未设置",message);
        //book为空的情况
        book = null;
        try {
            bookService.deleteByPrimaryKey(book);
        }catch (NullPointerException e){
            message = e.getMessage();
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
        Assert.assertEquals("传入对象为空",message);
    }

    @Test
    public void insert() {
        //一切正常的情况
        try {
            Assert.assertEquals(1, bookService.insert(book));
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
        //有一个不满足基本条件的情况
        book.setCompanyId(null);
        String message = "";
        try {
            bookService.insert(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("插入book的基本信息未满足", message);
        //book为空的情况
        book = null;
        try {
            bookService.insert(book);
        } catch (NullPointerException n) {
            message = n.getMessage();
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
        Assert.assertEquals("传入对象为空", message);
    }

    @Test
    public void selectByPrimaryKey(){
//        一切正常的情况
        book.setBookId("22222226");
        try {
            Assert.assertEquals("your light is shining3", bookService.selectByPrimaryKey(book).getBookName());
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
//        bookid不存在的时候
        book.setBookId(null);
        String message = "";
        try {
            bookService.selectByPrimaryKey(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            message = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("bookId未设置", message);
//        boo不存在的时候
        book = null;
        try {
            bookService.selectByPrimaryKey(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }catch (NullPointerException n){
            message = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", message);
    }

    @Test
    public void updateByPrimaryKey() {
//        一切正常的情况
        book.setBookId("11111");
        book.setCategory("mathdd");
        book.setCompanyId(null);
        book.setQuantity(22);
        book.setVersion("1.0.0.1");
        try {
            Assert.assertEquals(1, bookService.updateByPrimaryKey(book));
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
//        bookid为空的时候
        book.setBookId(null);
        String msg = "";
        try {
            bookService.updateByPrimaryKey(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("bookId为空", msg);
        book.setBookId(" ");
        try {
            bookService.updateByPrimaryKey(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("bookId为空", msg);
//        book为空的时候
        book = null;
        try {
            bookService.updateByPrimaryKey(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", msg);
    }

    @Test
    public void selectBooksByCompanyId() {
//　　　　　一切正常的情况下
        book.setCompanyId("jike");
        try {
            Assert.assertEquals(5, bookService.selectBooksByCompanyId(book).size());
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
//        bookcompanyid为空的情况下
        book.setCompanyId(null);
        String msg = "";
        try {
            bookService.selectBooksByCompanyId(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId为空", msg);
//        bookcompanyid为" "的情况下
        book.setCompanyId("    ");
        try {
            bookService.selectBooksByCompanyId(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId为空", msg);
//        book为空的情况下
        book = null;
        try {
            bookService.selectBooksByCompanyId(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", msg);
    }

    @Test
    public void selectBooksByComapnyIdAndCategory() {
        //　　　　　一切正常的情况下
        book.setCompanyId("jike");
        book.setCategory("siwei");
        try {
            Assert.assertEquals(4, bookService.selectBooksByComapnyIdAndCategory(book).size());
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
        String msg = "";
//        bookcompanyid为空的情况下
        book.setCompanyId(null);
        try {
            bookService.selectBooksByComapnyIdAndCategory(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId or category is null", msg);
//        bookcompanyid为" "的情况下
        book.setCompanyId(" ");
        try {
            bookService.selectBooksByComapnyIdAndCategory(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId or category is null", msg);
//        category为" "的情况下
        book.setCompanyId("jike");
        book.setCategory("  ");
        try {
            bookService.selectBooksByComapnyIdAndCategory(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId or category is null", msg);
//        book为空的情况下
        book = null;
        try {
            bookService.selectBooksByComapnyIdAndCategory(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", msg);
    }

    @Test
    public void selectBooksByComapnyIdAndBookName(){
        //　　　　　一切正常的情况下
        book.setCompanyId("jike");
        book.setBookName("your light is shining3");
        try {
            Assert.assertEquals(2, bookService.selectBooksByCompanyIdAndBookName(book).size());
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            infoNotFullyExpection.printStackTrace();
        }
        String msg = "";
//        //bookcompanyid为空的情况下
        book.setCompanyId(null);
        try {
            bookService.selectBooksByCompanyIdAndBookName(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);

        book.setCompanyId("jike");
        book.setBookName(null);
        try {
            bookService.selectBooksByCompanyIdAndBookName(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);


        book.setCompanyId(" ");
        try {
            bookService.selectBooksByCompanyIdAndBookName(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);


        book.setCompanyId("jike");
        book.setBookName(" ");
        try {
            bookService.selectBooksByCompanyIdAndBookName(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        }
        Assert.assertEquals("companyId or bookname is null", msg);

//        book为空的情况下
        book = null;
        try {
            bookService.selectBooksByCompanyIdAndBookName(book);
        } catch (InfoNotFullyExpection infoNotFullyExpection) {
            msg = infoNotFullyExpection.getMessage();
        } catch (NullPointerException n){
            msg = n.getMessage();
        }
        Assert.assertEquals("传入对象为空", msg);

    }

}