package com.mis.hrm.web.util;

import com.mis.hrm.book.po.Book;
import com.mis.hrm.project.po.Project;

@FunctionalInterface
public interface ProjectUpdateMethod {
    Object getEffectCount(Project o);
}
