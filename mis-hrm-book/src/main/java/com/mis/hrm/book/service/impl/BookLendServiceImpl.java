package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookLendInfoMapper;
import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.book.service.BookLendService;
import com.mis.hrm.util.ConstantValue;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class BookLendServiceImpl implements BookLendService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private BookLendInfoMapper bookLendInfoMapper;
    /**
     * 根据借书的人获取资料
     * @param borrower
     * @return
     */
    @Override
    public List<BookLendInfo> selectBookLendInfosByBorrower(String borrower) {
        if (!StringUtil.notEmpty(borrower)){
            logger.info("borrower为空");
            throw new InfoNotFullyException("借书者信息不全");
        }
        logger.info("根据borrower借书");
        return bookLendInfoMapper.selectBookLendInfosByBorrower(borrower);
    }

    @Override
    public List<BookLendInfo> selectBookLendInfosByCompanyId(String companyId) {
        if (!StringUtil.notEmpty(companyId)){
            logger.error("companyId信息不全，查询停止");
            throw new InfoNotFullyException("companyId信息不全");
        }
        logger.info("根据companyid借书");
        return bookLendInfoMapper.selectBookLendInfosByCompanyId(companyId);
    }

    @Override
    public List<BookLendInfo> selectBookLendInfosByCompanyIdAndBookName(String companyId, String bookName) {
        boolean isOk = StringUtil.notEmpty(companyId,bookName);
        if (!isOk){
            logger.error("companyId or bookname is null,查询停止");
            throw new InfoNotFullyException("companyId or bookname is null");
        }
        return bookLendInfoMapper.selectBookLendInfosByCompanyIdAndBookName(companyId, bookName);
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
            logger.error(ConstantValue.GET_NULL_DEL_FLAUT);
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(t.getBookRecord(), t.getCompanyId()))
                .isPresent();
        if (!isOk){
            logger.error("bookrecord or companyid is null,删除失败");
            throw new InfoNotFullyException("bookrecord or companyid is null");
        }
        return bookLendInfoMapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(BookLendInfo record) {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(record);
        }catch (NullPointerException e){
            logger.error(ConstantValue.GET_NULL_OBJECT + "，插入不成功");
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
        boolean isOk = bookLendInfoOptional
                .filter(BookLendInfo::baseRequired)
                .isPresent();
        if (!isOk){
            logger.error("传入的基本信息不全，插入失败");
            throw new InfoNotFullyException("传入的基本信息不全");
        }
        record.setBookRecord(UUID.randomUUID().toString());
        return bookLendInfoMapper.insert(record);
    }

    @Override
    public BookLendInfo selectByPrimaryKey(BookLendInfo key) {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(key);
        }catch (NullPointerException e){
            logger.error(ConstantValue.GET_NULL_OBJECT);
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(key.getCompanyId(), key.getBookRecord()))
                .isPresent();
        if (!isOk){
            logger.error("companyid or bookrecord is null，查询失败");
            throw new InfoNotFullyException("companyid or bookrecord is null");
        }
        return bookLendInfoMapper.selectByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(BookLendInfo record) {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(record);
        }catch (NullPointerException e){
            logger.error(ConstantValue.GET_NULL_OBJECT + "，更新失败");
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(record.getCompanyId(), record.getBookRecord()))
                .isPresent();
        if (!isOk){
            logger.error("companyid or bookrecorder is null,更新失败");
            throw new InfoNotFullyException("companyId or bookrecorder is null");
        }
        return bookLendInfoMapper.updateByPrimaryKey(record);
    }
}
