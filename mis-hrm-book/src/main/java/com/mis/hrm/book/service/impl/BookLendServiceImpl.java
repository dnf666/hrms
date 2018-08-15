package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookLendInfoMapper;
import com.mis.hrm.book.po.BookLendInfo;
import com.mis.hrm.book.service.BookLendService;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
    public List<BookLendInfo> selectBookLendInfosByBorrower(String borrower) throws InfoNotFullyExpection {
        if (!StringUtil.notEmpty(borrower)){
            logger.info("borrower为空");
            throw new InfoNotFullyExpection("借书者信息不全");
        }
        logger.info("根据borrower借书");
        return bookLendInfoMapper.selectBookLendInfosByBorrower(borrower);
    }

    @Override
    public List<BookLendInfo> selectBookLendInfosByCompanyId(String companyId) throws InfoNotFullyExpection {
        if (!StringUtil.notEmpty(companyId)){
            logger.error("companyId信息不全，查询停止");
            throw new InfoNotFullyExpection("companyId信息不全");
        }
        logger.info("根据companyid借书");
        return bookLendInfoMapper.selectBookLendInfosByCompanyId(companyId);
    }

    @Override
    public List<BookLendInfo> selectBookLendInfosByCompanyIdAndBookName(String companyId, String bookName) throws InfoNotFullyExpection {
        boolean isOk = StringUtil.notEmpty(companyId,bookName);
        if (!isOk){
            logger.error("companyId or bookname is null,查询停止");
            throw new InfoNotFullyExpection("companyId or bookname is null");
        }
        return bookLendInfoMapper.selectBookLendInfosByCompanyIdAndBookName(companyId, bookName);
    }

    @Override
    public List<BookLendInfo> selectAll() {
        return bookLendInfoMapper.selectAll();
    }

    @Override
    public int deleteByPrimaryKey(BookLendInfo key) throws InfoNotFullyExpection {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(key);
        }catch (NullPointerException e){
            logger.error("传入对象为空，删除失败");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(t.getBookRecord(), t.getCompanyId()))
                .isPresent();
        if (!isOk){
            logger.error("bookrecord or companyid is null,删除失败");
            throw new InfoNotFullyExpection("bookrecord or companyid is null");
        }
        return bookLendInfoMapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(BookLendInfo record) throws InfoNotFullyExpection {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(record);
        }catch (NullPointerException e){
            logger.error("传入对象为空，删除失败");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> t.baseRequired())
                .isPresent();
        if (!isOk){
            logger.error("传入的基本信息不全，插入失败");
            throw new InfoNotFullyExpection("传入的基本信息不全");
        }
        return bookLendInfoMapper.insert(record);
    }

    @Override
    public BookLendInfo selectByPrimaryKey(BookLendInfo key) throws InfoNotFullyExpection {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(key);
        }catch (NullPointerException e){
            logger.error("传入对象为空，删除失败");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(key.getCompanyId(), key.getBookRecord()))
                .isPresent();
        if (!isOk){
            logger.error("companyid or bookrecord is null，查询失败");
            throw new InfoNotFullyExpection("companyid or bookrecord is null");
        }
        return bookLendInfoMapper.selectByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(BookLendInfo record) throws InfoNotFullyExpection {
        Optional<BookLendInfo> bookLendInfoOptional;
        try {
            bookLendInfoOptional = Optional.of(record);
        }catch (NullPointerException e){
            logger.error("传入对象为空，删除失败");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookLendInfoOptional
                .filter(t -> StringUtil.notEmpty(record.getCompanyId(), record.getBookRecord()))
                .isPresent();
        if (!isOk){
            logger.error("companyid or bookrecorder is null,更新失败");
            throw new InfoNotFullyExpection("companyId or bookrecorder is null");
        }
        return bookLendInfoMapper.updateByPrimaryKey(record);
    }
}
