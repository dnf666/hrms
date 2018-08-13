package com.mis.hrm.web.excel.apidoc;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.ToMap;

import java.util.Map;

public class ExcelApiDoc {

    /**
     *   @api {GET} hrms/{tableTitle}/toExcel 将数据从数据库导入到Excel
     *   @apiDescription 目前可填的tableTitle只有member和whereabout啦
     *   @apiGroup EXCEL
     *   @apiParam  {String} tableTitle 数据库表名
     *   @apiParam  {String} filePath Excel文件的具体路径
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map importExcel(String tableTitle, String filePath){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }

    /**
     *   @api {GET} hrms/{tableTitle}/fromExcel 将数据从Excel导出到数据库
     *   @apiDescription 其实这个tableTitle可以瞎填，有它只是为了保持格式一致，但最好还是写member或whereabout啦
     *   @apiGroup EXCEL
     *   @apiParam  {String} filePath Excel文件的具体路径
     *   @apiSuccessExample {json} Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "code": "1",
     *         "msg": "success"
     *         "object": null
     *       }
     */
    public Map exportExcel(String filePath){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
    }
}
