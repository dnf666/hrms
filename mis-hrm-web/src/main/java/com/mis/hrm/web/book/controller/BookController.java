package com.mis.hrm.web.book.controller;

import com.google.common.base.Strings;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;

import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.constant.PageConstant;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.web.util.ControllerUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author demo
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PutMapping("book")
    public Map updateBookByBookId(@RequestBody Book book) {
        Map<String, Object> result;
        result = ControllerUtil.getResult(bookService::updateByPrimaryKey, book);
        return result;
    }

    @PostMapping("book")
    public Map addBookInfo(@RequestBody Book book) {
        Map<String, Object> result;
        if (Strings.isNullOrEmpty(book.getCompanyId())
                || Strings.isNullOrEmpty(book.getBookName())
                || Strings.isNullOrEmpty(book.getVersion())) {
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        result = ControllerUtil.getResult(bookService::insert, book);
        return result;
    }

    @PostMapping("delBook")
    public Map deleteBookInfoByBookId(@RequestBody Book book) {
        if (book.getBookId() == null || book.getBookId().length() == 0) {
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK);
        }
        String ids = book.getBookId();
        String companyId = book.getCompanyId();
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String b : idArray) {
            Integer b1 = Integer.parseInt(b);
            idList.add(b1);
        }
        int result = bookService.deleteByids(idList, companyId);
        return ToMap.toSuccessMap(result);
    }

    @PostMapping("filter")
    public Map searchBook(@RequestBody Book book, Integer currentPage, Integer size) {
        if (currentPage == null) {
            currentPage = PageConstant.DEFUALT_PAGE;
        }
        if (size == null) {
            size = PageConstant.DEFUALT_SIZE;
        }
        Pager<Book> pager = new Pager<>();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(size);
        if (Strings.isNullOrEmpty(book.getCompanyId())) {
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        try {
            List<Book> bookList = bookService.selectByPrimaryKeyAndPage(book, pager);
            pager.setData(bookList);
            return ToMap.toSuccessMap(pager);
        } catch (Exception e) {
            return ToMap.toFalseMap(e.getMessage());
        }

    }

    @PostMapping("excel")
    public Map importBookFromExcel(@RequestParam("file") MultipartFile file, @RequestParam("companyId") String companyId) {
        String fileName = file.getOriginalFilename();
        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            try {
                bookService.importBookFromExcel(file, companyId);
            } catch (IOException e) {
                return ToMap.toFalseMap("io异常");
            }
        } else {
            return ToMap.toFalseMap("文件格式不匹配");
        }
        return ToMap.toSuccessMap(null);
    }

    @PostMapping("/createExcel")
    public ResponseEntity<byte[]> createExcel(@RequestBody Book book) {
        List<Book> lists = bookService.selectByMultiKey(book);
        HSSFWorkbook workbook = bookService.exportExcel(lists);
        byte[] bytes = workbook.getBytes();
        HttpHeaders header = new HttpHeaders();
        header.setContentDispositionFormData("attachment", "book.xls");
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(bytes, header, HttpStatus.CREATED);

    }

}
