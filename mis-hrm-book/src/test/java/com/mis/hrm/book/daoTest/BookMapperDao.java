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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-book.xml")
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
        int rightResult = 0;
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
    }

    @Test
    public void updateByPrimaryKey() {
    }
}
