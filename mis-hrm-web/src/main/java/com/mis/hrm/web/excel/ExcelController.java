package com.mis.hrm.web.excel;

import com.mis.hrm.util.demo.excel.DemoExcel;
import com.mis.hrm.util.demo.excel.impl.DemoExcelImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hrms")
public class ExcelController {
    private DemoExcel demoExcel = new DemoExcelImpl();
    
    
}
