package com.mis.hrm.web.excel.apidoc;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.ToMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class ExcelApiDoc {

    /**
     *   @api {GET} {tableTitle}/toExcel 将数据从数据库导入到Excel
     *   @apiDescription tableTitle填member或whereabout；body里的是Excel文件的字节流
     *   @apiGroup EXCEL
     *   @apiParam  {String} tableTitle 数据库表名
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       HTTP/1.1 200 OK
     *       {
     *         "msg": "success",
     *         "code": 1,
     *         "object": {
     *             "headers": {
     *                  "Content-CompanyType": [
     *                      "application/octet-stream"
     *                  ],
     *                  "Content-Disposition": [
     *                      "form-data; name=\"attachment\"; filename=\"model-member-1536216152560.xlsx\""
     *                  ]
     *             },
     *             "body": "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",
     *             "statusCode": "CREATED",
     *             "statusCodeValue": 201
     *         }
     *       }
     */
    public Map importExcel(String tableTitle){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     *   @api {POST} {tableTitle}/fromExcel 将数据从Excel导出到数据库
     *   @apiDescription 其实这个tableTitle可以瞎填，有它只是为了保持格式一致，但最好还是写member或whereabout啦
     *   @apiGroup EXCEL
     *   @apiParam  {MultipartFile} file 用户上传的Excel文件
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map exportExcel(MultipartFile multipartFile){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     *   @api {GET} {tableTitle}/download 模板下载
     *   @apiDescription tableTitle填member或whereabout；body里的是Excel模板的字节流
     *   @apiGroup EXCEL
     *   @apiParam  {String} tableTitle 数据库表名
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "msg": "success",
     *         "code": 1,
     *         "object": {
     *             "headers": {
     *                  "Content-CompanyType": [
     *                      "application/octet-stream"
     *                  ],
     *                  "Content-Disposition": [
     *                      "form-data; name=\"attachment\"; filename=\"model-member-1536216152560.xlsx\""
     *                  ]
     *             },
     *             "body": "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",
     *             "statusCode": "CREATED",
     *             "statusCodeValue": 201
     *         }
     *       }
     */
    public Map download(String tableTitle){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }
}
