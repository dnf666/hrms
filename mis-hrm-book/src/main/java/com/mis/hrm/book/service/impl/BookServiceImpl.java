package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookMapper;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import jdk.nashorn.internal.runtime.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private BookMapper bookMapper;


    /**
     * １．传入的对象为空，删除失败
     * ２．传入对象的bookid为空，删除失败
     * ３．根据数据库的结果查看。
     * @param key
     * @return 失败？０：１
     */
    @Override
    public int deleteByPrimaryKey(Book key) throws InfoNotFullyExpection {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(key);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            logger.error("deleteByPrimaryKey:key为空");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookOptional.map(Book::getBookId).isPresent();
        //如果bookId为空，抛出异常
        if (!isOk) {
            logger.error("bookId为空，删除失败");
            throw new InfoNotFullyExpection("bookId未设置");
        }
        logger.info("deleteByPrimaryKey----通过主键删除book信息");
        return bookMapper.deleteByPrimaryKey(key);
    }

    /**
     * １．插入的基本数据要满足，否则插入直接失败
     * @param record
     * @return 失败？０：１+
     */
    @Override
    public int insert(Book record) throws InfoNotFullyExpection {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(record);
        }catch (NullPointerException e){
            logger.error("insert:record为空，插入失败");
            throw new NullPointerException("传入对象为空");
        }
        //如果满足插入的基本条件，那么尝试向数据库中插入数据，否则直接失败
        bookOptional = bookOptional.filter(Book::baseRequied);
        if (!bookOptional.isPresent()){
            throw new InfoNotFullyExpection("插入book的基本信息未满足");
        }
        return bookMapper.insert(record);
    }

    /**
     * 根据传入的bookId查询书籍信息
     * @param key
     * @return success? book : null;
     */
    @Override
    public Book selectByPrimaryKey(Book key) throws InfoNotFullyExpection {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(key);
        }catch (NullPointerException e){
            logger.error("selectByPrimaryKey:key");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookOptional
                .filter(t -> StringUtil.notEmpty(t.getBookId()))
                .isPresent();
        if (!isOk){
            logger.error("bookId为空,查找停止");
            throw new InfoNotFullyExpection("bookId未设置");
        }
        return bookMapper.selectByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(Book record) throws InfoNotFullyExpection {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(record);
        }catch (NullPointerException e){
            logger.error("insert:record为空");
            throw new NullPointerException("传入对象为空");
        }
//        要求bookid不为空，否则更新失败
        boolean isOk = bookOptional
                .filter(t -> StringUtil.notEmpty(t.getBookId()))
                .isPresent();
        if (!isOk){
            logger.error("bookId is null,更新失败");
            throw new InfoNotFullyExpection("bookId为空");
        }
        return bookMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Book> selectBooksByCompanyId(Book book) throws InfoNotFullyExpection {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(book);
        }catch (NullPointerException e){
            logger.error("selectBooksByCompanyId:book为空");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookOptional
                .filter(t -> StringUtil.notEmpty(t.getCompanyId()))
                .isPresent();
        if (!isOk){
            logger.error("companyId is null,查找失败");
            throw new InfoNotFullyExpection("companyId为空");
        }
        return bookMapper.selectBooksByCompanyId(book);
    }

    @Override
    public List<Book> selectBooksByComapnyIdAndCategory(Book book) throws InfoNotFullyExpection {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(book);
        }catch (NullPointerException e){
            logger.error("selectBooksByCompanyId:book为空");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookOptional
                .filter(t -> StringUtil.notEmpty(t.getCompanyId(),t.getCategory()))
                .isPresent();
        if (!isOk){
            logger.error("companyId or category is null,查找失败");
            throw new InfoNotFullyExpection("companyId or category is null");
        }
        return bookMapper.selectBooksByComapnyIdAndCategory(book);
    }

    @Override
    public List<Book> selectBooksByCompanyIdAndBookName(Book book) throws InfoNotFullyExpection {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(book);
        }catch (NullPointerException e){
            logger.error("selectBooksByCompanyId:book为空");
            throw new NullPointerException("传入对象为空");
        }
        boolean isOk = bookOptional
                .filter(t -> StringUtil.notEmpty(book.getCompanyId(),book.getBookName()))
                .isPresent();
        if (!isOk){
            logger.error("companyId or bookname is null,查找失败");
            throw new InfoNotFullyExpection("companyId or bookname is null");
        }
        return isOk ? bookMapper.selectBooksByCompanyIdAndBookName(book) : null;
    }
}
