package com.mis.hrm.book.service;

import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookLendService extends BaseService<BookLendInfo> {
    List<BookLendInfo> selectBookLendInfosByBorrower(String borrower) throws InfoNotFullyException;

    List<BookLendInfo> selectBookLendInfosByCompanyId(String companyId) throws InfoNotFullyException;

    List<BookLendInfo> selectBookLendInfosByCompanyIdAndBookName(@Param("companyId") String companyId, @Param("bookName") String bookName) throws InfoNotFullyException;

    List<BookLendInfo> selectAll();
}
