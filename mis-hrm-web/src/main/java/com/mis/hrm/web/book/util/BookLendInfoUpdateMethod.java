package com.mis.hrm.web.book.util;

import com.mis.hrm.book.po.BookLendInfo;


@FunctionalInterface
public interface BookLendInfoUpdateMethod {
    Object getEffectCount(BookLendInfo o);
}
