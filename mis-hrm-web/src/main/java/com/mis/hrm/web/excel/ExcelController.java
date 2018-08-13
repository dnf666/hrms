package com.mis.hrm.web.excel;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.demo.excel.DemoExcel;
import com.mis.hrm.util.demo.excel.impl.DemoExcelImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hrms")
public class ExcelController {
    private DemoExcel demoExcel = new DemoExcelImpl();

    /**
     * 将数据从数据库导入到Excel
     * @param filePath
     * @param tableTitle
     */
    @GetMapping("/{tableTitle}/excel/import")
    public Map importExcel(@PathVariable String tableTitle,
                           String filePath){
        demoExcel.importExcel(filePath,tableTitle);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     * 将数据从Excel导出到数据库
     * @param filePath
     */
    @GetMapping("/{tableTitle}/excel/export")
    public Map exportExcel(@PathVariable String tableTitle,
                           String filePath){
        demoExcel.exportExcel(filePath);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }
}
