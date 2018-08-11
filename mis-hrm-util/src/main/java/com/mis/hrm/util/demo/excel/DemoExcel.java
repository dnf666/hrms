package com.mis.hrm.util.demo.excel;


public interface DemoExcel {
    /**
     * 将数据从数据库导入到Excel
     * @param filePath
     * @param tableTitle
     */
    void importExcel(String filePath,String tableTitle);

    /**
     * 将数据从Excel导出到数据库
     * @param filePath
     */
    void exportExcel(String filePath);
}
