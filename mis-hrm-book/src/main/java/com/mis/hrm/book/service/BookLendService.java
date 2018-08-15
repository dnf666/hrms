package com.mis.hrm.book.service;

import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookLendService extends BaseService<BookLendInfo> {
    List<BookLendInfo> selectBookLendInfosByBorrower(String borrower) throws InfoNotFullyExpection;

    List<BookLendInfo> selectBookLendInfosByCompanyId(String companyId) throws InfoNotFullyExpection;

    List<BookLendInfo> selectBookLendInfosByCompanyIdAndBookName(@Param("companyId") String companyId, @Param("bookName") String bookName) throws InfoNotFullyExpection;

    List<BookLendInfo> selectAll();
}
