package com.mis.hrm.util.demo.excel;

import java.util.List;

public interface DemoExcel {
    /**
     * 将数据从数据库导入到Excel
     * @param filePath
     * @param head
     */
    void importExcel(String filePath,List<String> head);

    /**
     * 将数据从Excel导出到数据库
     * @param filePath
     */
    void exportExcel(String filePath);
}
