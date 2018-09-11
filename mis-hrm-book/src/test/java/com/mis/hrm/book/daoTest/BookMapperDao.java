package com.mis.hrm.book.daoTest;

import com.mis.hrm.book.dao.BookMapper;
import com.mis.hrm.book.po.Book;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-book-test.xml")
public class BookMapperDao {

    @Autowired
    private BookMapper bookMapper;

    private Book book;

    @Before
    public void setUp() throws Exception {
        book = Book.builder()
                .bookId("12345")
                .bookName("哈利波特")
                .category("魔法")
                .companyId("信管工作室")
                .quantity(21)
                .version("123456").build();
    }

    @Test
    public void deleteByPrimaryKey() {
        int rightResult = 1;
        book.setBookId("11112");
        int result = bookMapper.deleteByPrimaryKey(book);
        Assert.assertEquals(rightResult, result);
    }

    @Test
    public void insert() {
        int rightResult = 1;
        int result = bookMapper.insert(book);
        Assert.assertEquals(rightResult, result);
    }

    @Test
    public void selectByPrimaryKey() {
        String expectedCompanyId = "xinguan";
        book.setBookId("11111");
        String acture = bookMapper.selectByPrimaryKey(book).getCompanyId();
        Assert.assertEquals(expectedCompanyId, acture);
    }

    @Test
    public void updateByPrimaryKey() {
        book.setBookId("11111");
        book.setBookName("lalalalala");
        int rowNum = bookMapper.updateByPrimaryKey(book);
        Assert.assertEquals(1, rowNum);
    }

    @Test
    public void selectBooksByCompanyId(){
        int expectSize = 5;
        book.setCompanyId("jike");
        List<Book> list = bookMapper.selectBooksByCompanyId(book);
        Assert.assertEquals(expectSize, list.size());
    }

    @Test
    public void selectBooksByComapnyIdAndCategory(){
        int expectSize = 4;
        book.setCompanyId("jike");
        book.setCategory("siwei");
        List<Book> list = bookMapper.selectBooksByComapnyIdAndCategory(book);
        Assert.assertEquals(expectSize, list.size());
    }

    @Test
    public void selectBooksByCompanyIdAndBookName(){
        int expectSize = 2;
        book.setCompanyId("jike");
        book.setBookName("your light is shining");
        List<Book> list = bookMapper.selectBooksByCompanyIdAndBookName(book);
        Assert.assertEquals(expectSize, list.size());
    }
}
