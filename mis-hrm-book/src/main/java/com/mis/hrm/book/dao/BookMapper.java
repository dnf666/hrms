package com.mis.hrm.book.dao;

import com.mis.hrm.book.po.Book;
import com.mis.hrm.util.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Repository
public interface BookMapper extends BaseMapper<Book> {

    List<Book> selectByMultiKey(Book book);

    int deleteByIds(@Param("ids") List<Integer> ids, @Param("companyId") String companyId);
}
