package com.mis.hrm.book.dao;

import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.util.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookLendInfoMapper extends BaseMapper<BookLendInfo> {

    List<BookLendInfo> selectBookLendInfosByBorrower(String borrower);

    List<BookLendInfo> selectBookLendInfosByCompanyId(String companyId);

    List<BookLendInfo> selectBookLendInfosByCompanyIdAndBookName(@Param("companyId") String companyId, @Param("bookName") String bookName);

    List<BookLendInfo> selectAll();
}
