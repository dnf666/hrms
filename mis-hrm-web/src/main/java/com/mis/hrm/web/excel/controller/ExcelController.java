package com.mis.hrm.web.excel.controller;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.demo.excel.DemoExcel;
import com.mis.hrm.util.demo.excel.impl.DemoExcelImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping
public class ExcelController {
    private DemoExcel demoExcel = new DemoExcelImpl();

    /**
     * 将数据从数据库导入到Excel
     * @param tableTitle
     */
    @GetMapping("/{tableTitle}/toExcel")
    public Map importExcel(@PathVariable String tableTitle){
        byte[] bytes = demoExcel.importExcel(tableTitle, "excel");

        //设置文件名（表名-时间戳.xlsx）
        String fileName = tableTitle + "-" + System.currentTimeMillis() + ".xlsx";

        //响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        //响应体
        ResponseEntity<byte[]> responseEntity =  new ResponseEntity<>(bytes,headers,HttpStatus.CREATED);

        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,responseEntity);
    }

    /**
     * 将数据从Excel导出到数据库
     * @param file 用户上传的文件
     */
    @PostMapping("/{tableTitle}/fromExcel")
    public Map exportExcel(@PathVariable String tableTitle,
                           MultipartFile file){
        demoExcel.exportExcel(tableTitle,file);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     * 模板下载
     * @param tableTitle 表头
     */
    @GetMapping("/{tableTitle}/download")
    public Map fileLoad(@PathVariable String tableTitle){
        byte[] bytes = demoExcel.importExcel(tableTitle,"download");

        //设置文件名（model-表名-时间戳.xlsx）
        String fileName = "model-" + tableTitle + "-" + System.currentTimeMillis() + ".xlsx";

        //响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        ResponseEntity<byte[]> responseEntity =  new ResponseEntity<>(bytes,headers,HttpStatus.CREATED);

        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,responseEntity);
    }
}
