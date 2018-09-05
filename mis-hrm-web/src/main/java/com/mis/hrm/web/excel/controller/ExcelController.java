package com.mis.hrm.web.excel.controller;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.demo.excel.DemoExcel;
import com.mis.hrm.util.demo.excel.impl.DemoExcelImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class ExcelController {
    private DemoExcel demoExcel = new DemoExcelImpl();

    /**
     * 将数据从数据库导入到Excel
     * @param filePath
     * @param tableTitle
     */
    @GetMapping("/{tableTitle}/toExcel")
    public Map importExcel(@PathVariable String tableTitle,
                           String filePath){
        demoExcel.importExcel(filePath,tableTitle,"excel");
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     * 将数据从Excel导出到数据库
     * @param filePath
     */
    @GetMapping("/{tableTitle}/fromExcel")
    public Map exportExcel(@PathVariable String tableTitle,
                           String filePath){
        demoExcel.exportExcel(filePath);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     * 模板下载
     * @param filePath 文件下载后保存的位置
     * @param tableTitle 表头
     */
    @GetMapping("/{tableTitle}/download")
    public Map fileLoad(@PathVariable String tableTitle,
                           String filePath){
        demoExcel.importExcel(filePath,tableTitle,"download");
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }
}
