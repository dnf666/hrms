package com.mis.hrm.book.service;

import com.mis.hrm.book.po.Book;
import com.mis.hrm.util.BaseService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
public interface BookService extends BaseService<Book> {
    int importBookFromExcel(MultipartFile file,String companyId) throws IOException;

    List<Book> selectByMultiKey(Book book);

    HSSFWorkbook exportExcel(List<Book> lists);
}
