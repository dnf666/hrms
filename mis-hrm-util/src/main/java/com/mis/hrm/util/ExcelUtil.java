package com.mis.hrm.util;

import com.google.common.base.Strings;
import com.mis.hrm.util.enums.ErrorCode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * created by dailf on 2018/10/19
 *
 * @author dailf
 */
public class ExcelUtil {
    private ExcelUtil() throws Exception {
        throw new Exception("can't construction");
    }

    public static Row getHeadRow() {
        //创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建工作表
        Sheet sheet = workbook.createSheet();
        //创建表头
        return sheet.createRow(0);
    }

    public static Sheet getSheet(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        //获取第一个sheet表
        return workbook.getSheetAt(0);
    }

    public static List<Row> getRowFromSheet(Sheet sheet) {
        List<Row> rows = new ArrayList<>();
        int count = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i <= count; i++) {
            Row row = sheet.getRow(i);
            if ((row != null)) {
                rows.add(row);
            }
        }
        return rows;
    }
    public static List<Cell> getCellFromRow(Row row) throws IOException {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0;i<row.getLastCellNum();i++){
            Cell cell = row.getCell(i);
            if (cell == null){
                throw new IOException(ErrorCode.MESSAGE_NOT_COMPLETE.getDescription());
            }
            cells.add(cell);
        }
        return cells;
    }

    public static String getValueByIndex(List<Cell> cells ,int i) throws IOException{
        Cell hssfCell = cells.get(i);
        if (hssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            Object inputValue = null;
            Long longVal = Math.round(hssfCell.getNumericCellValue());
            Double doubleVal = hssfCell.getNumericCellValue();
            if(Double.parseDouble(longVal + ".0") == doubleVal){
                inputValue = longVal;
            }
            else{
                inputValue = doubleVal;
            }
            DecimalFormat df = new DecimalFormat("#");
            return String.valueOf(df.format(inputValue));
        } else {
            // 返回字符串类型的值
            String value = hssfCell.getStringCellValue();
            if (Strings.isNullOrEmpty(value)){
                throw new IOException(ErrorCode.MESSAGE_NOT_COMPLETE.getDescription());
            }
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}
