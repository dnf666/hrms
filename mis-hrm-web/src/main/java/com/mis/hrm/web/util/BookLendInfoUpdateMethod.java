package com.mis.hrm.web.util;

import com.mis.hrm.book.po.BookLendInfo;


@FunctionalInterface
public interface BookLendInfoUpdateMethod {
    Object getEffectCount(BookLendInfo o);
}
