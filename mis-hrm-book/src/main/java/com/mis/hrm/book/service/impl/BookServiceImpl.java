package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookMapper;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Service
public class BookServiceImpl implements BookService {

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
    public int deleteByPrimaryKey(Book key) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(key);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            return 0;
        }
        boolean isOk = bookOptional.map(Book::getBookId).isPresent();
        //如果bookId为空，删除失败
        return isOk ? bookMapper.deleteByPrimaryKey(key) : 0;
    }

    /**
     * １．插入的基本数据要满足，否则插入直接失败
     * @param record
     * @return 失败？０：１+
     */
    @Override
    public int insert(Book record) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(record);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            return 0;
        }
        //如果满足插入的基本条件，那么尝试向数据库中插入数据，否则直接失败
        bookOptional = bookOptional.filter(Book::baseRequied);
        return bookOptional.isPresent() ? bookMapper.insert(record) : 0;
    }

    /**
     * 根据传入的bookId查询书籍信息
     * @param key
     * @return success? book : null;
     */
    @Override
    public Book selectByPrimaryKey(Book key) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(key);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            return null;
        }
        boolean isOk = bookOptional.map(Book::getBookId).isPresent();
        //如果bookId为空，查找失败
        return isOk ? bookMapper.selectByPrimaryKey(key) : null;
    }

    @Override
    public int updateByPrimaryKey(Book record) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(record);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            return 0;
        }
//        要求bookid不为空，否则更新失败
        boolean isOk = bookOptional.filter(t -> t.getBookId() != null).isPresent();
        return isOk ? bookMapper.updateByPrimaryKey(record) : 0;
    }

    @Override
    public List<Book> selectBooksByCompanyId(Book book) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(book);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            return null;
        }
        boolean isOk = bookOptional.filter(t -> t.getCompanyId() != null).isPresent();
        return isOk ? bookMapper.selectBooksByCompanyId(book) : null;
    }

    @Override
    public List<Book> selectBooksByComapnyIdAndCategory(Book book) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(book);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            return null;
        }
        boolean isOk = bookOptional
                .filter(t -> t.getCompanyId() != null && t.getCategory() != null)
                .isPresent();
        return isOk ? bookMapper.selectBooksByComapnyIdAndCategory(book) : null;
    }

    @Override
    public List<Book> selectBooksByCompanyIdAndBookName(Book book) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(book);
        }catch (NullPointerException e){
            //传入的对象为空,直接返回，不用再去数据库
            return null;
        }
        boolean isOk = bookOptional
                .filter(t -> t.getCompanyId() != null && t.getBookName() != null)
                .isPresent();
        return isOk ? bookMapper.selectBooksByCompanyIdAndBookName(book) : null;
    }
}
