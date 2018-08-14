package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookLendInfoMapper;
import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.book.service.BookLendService;
import com.mis.hrm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookLendServiceImpl implements BookLendService {
    @Autowired
    private BookLendInfoMapper bookLendInfoMapper;
    /**
     * 根据借书的人获取资料
     * @param borrower
     * @return
     */
    @Override
    public List<BookLendInfo> selectBookLendInfosByBorrower(String borrower) {
        boolean notEmpty = StringUtil.notEmpty(borrower);
        return notEmpty ? bookLendInfoMapper.selectBookLendInfosByBorrower(borrower) : null;
    }

    @Override
    public List<BookLendInfo> selectBookLendInfosByCompanyId(String companyId) {
        boolean notEmpty = StringUtil.notEmpty(companyId);
        return notEmpty ? bookLendInfoMapper.selectBookLendInfosByCompanyId(companyId) : null;
    }

    @Override
    public List<BookLendInfo> selectBookLendInfosByCompanyIdAndBookName(String companyId, String bookName) {
        boolean isOk = StringUtil.notEmpty(companyId,bookName);
        return isOk ? bookLendInfoMapper.selectBookLendInfosByCompanyIdAndBookName(companyId, bookName) : null;
    }

    @Override
    public List<BookLendInfo> selectAll() {
        return bookLendInfoMapper.selectAll();
    }

    @Override
    public int deleteByPrimaryKey(BookLendInfo key) {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(key);
        }catch (NullPointerException e){
            return 0;
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(t.getBookRecord(), t.getCompanyId()))
                .isPresent();
        return isOk ? bookLendInfoMapper.deleteByPrimaryKey(key) : 0;
    }

    @Override
    public int insert(BookLendInfo record) {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(record);
        }catch (NullPointerException e){
            return 0;
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> t.baseRequired())
                .isPresent();
        return isOk ? bookLendInfoMapper.insert(record) : 0;
    }

    @Override
    public BookLendInfo selectByPrimaryKey(BookLendInfo key) {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(key);
        }catch (NullPointerException e){
            return null;
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(key.getCompanyId(), key.getBookRecord()))
                .isPresent();
        return isOk?bookLendInfoMapper.selectByPrimaryKey(key):null;
    }

    @Override
    public int updateByPrimaryKey(BookLendInfo record) {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(record);
        }catch (NullPointerException e){
            return 0;
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(record.getCompanyId(), record.getBookRecord()))
                .isPresent();
        return isOk ? bookLendInfoMapper.updateByPrimaryKey(record):0;
    }
}
