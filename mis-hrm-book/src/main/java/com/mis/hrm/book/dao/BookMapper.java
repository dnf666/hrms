package com.mis.hrm.book.dao;

import com.mis.hrm.book.po.Book;
import com.mis.hrm.util.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Repository
public interface BookMapper extends BaseMapper<Book> {
    /**
     * 通过公司id查询书籍列表
     * @param book　保存了公司名的对象
     * @return 书籍列表
     */
    List<Book> selectBooksByCompanyId(Book book);

    /**
     * 通过公司id和分类查询书籍列表
     * @param book　保存了公司名和分类信息的对象
     * @return 书籍列表
     */
    List<Book> selectBooksByComapnyIdAndCategory(Book book);

    /**
     * 通过公司id和分类查询书籍列表
     * @param book　保存了公司名和书名信息的对象
     * @return 书籍列表
     */
    List<Book> selectBooksByCompanyIdAndBookName(Book book);
}
