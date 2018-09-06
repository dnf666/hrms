package com.mis.hrm.util.demo.excel;


import org.springframework.web.multipart.MultipartFile;

public interface DemoExcel {
    /**
     * 将数据从数据库导入到Excel
//     * @param filePath Excel所在位置
     * @param tableTitle 表名
     * @param type 是否为模板下载
     */
    byte[] importExcel(String tableTitle, String type);

    /**
     * 将数据从Excel导出到数据库
//     * @param filePath
     */
    void exportExcel(MultipartFile multipartFile);
}
