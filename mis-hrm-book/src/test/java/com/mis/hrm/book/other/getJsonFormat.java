package com.mis.hrm.book.other;

import com.alibaba.fastjson.JSON;
import com.mis.hrm.book.po.Book;
import org.junit.Test;

public class getJsonFormat {
    @Test
    public void getBookJson(){
        Book book = Book.builder()
                .companyId("信管工作室")
                .bookName("你的灯亮着吗？")
                .category("思维")
                .quantity(2)
                .version("2.0")
                .bookId("dddddfffffsssss").build();
        String bookJson = JSON.toJSONString(book);
        System.out.println(bookJson);
    }
}
