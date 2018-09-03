package com.mis.hrm.book.service;

import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.util.BaseService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookLendService extends BaseService<BookLendInfo> {
    List<BookLendInfo> selectBookLendInfosByBorrower(String borrower);

    List<BookLendInfo> selectBookLendInfosByCompanyId(String companyId);

    List<BookLendInfo> selectBookLendInfosByCompanyIdAndBookName(@Param("companyId") String companyId, @Param("bookName") String bookName);

    List<BookLendInfo> selectAll();
}
