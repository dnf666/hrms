package com.mis.hrm.book.other;

import com.alibaba.fastjson.JSON;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.po.BookLendInfo;
import org.junit.Test;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.time.LocalDate;

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
    @Test
    public void getBookInfoJson(){
        BookLendInfo bookLendInfo = BookLendInfo.builder()
                .companyId("信管工作室")
                .bookRecord("不晓得是撒子啊")
                .bookName("暗时间")
                .lendTime(LocalDate.now().toString())
                .returnTime(LocalDate.now().toString())
                .borrower("优秀的人").build();
        String bookInfoJson = JSON.toJSONString(bookLendInfo);
        System.out.println(bookInfoJson);
    }
}
