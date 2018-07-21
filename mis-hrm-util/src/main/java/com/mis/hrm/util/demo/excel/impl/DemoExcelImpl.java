package com.mis.hrm.util.demo.excel.impl;

import com.mis.hrm.util.demo.excel.DemoExcel;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoExcelImpl implements DemoExcel {
    //在这里统一设置一下sql语句，便于修改
    final private String INSERT_INTO_MEMBERS = "insert into members (name,sex,age) values (?,?,?);";
    final private String INSERT_INTO_BOOKS = "insert into books (id,name,user) values (?,?,?);";
    final private String SELECT_FORM_MEMBERS = "select * from members; ";
    final private String SELECT_FROM_BOOKS = "select * from books";

    /**
     * 创将数据从数据库导入到Excel
     * @param filePath 文件路径
     * @param head 表头
     */
    public void importExcel(String filePath,List<String> head){
        //创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建工作表
        Sheet sheet = workbook.createSheet();
        //创建表头
        Row headRow = sheet.createRow(0);
        //为表头单元格填入数据
        Cell headCell;
        for(int i = 0; i < head.size(); i++){
            headCell = headRow.createCell(i);
            headCell.setCellValue(head.get(i));
        }

        //根据表头调整sql语句
        String sql = null;
        if(head.get(0).equals("姓名")){
            sql = SELECT_FORM_MEMBERS;
        }else if(head.get(0).equals("编号")){
            sql = SELECT_FROM_BOOKS;
        }

        //将查询结果放进数组
        List<String> list = connMysql(head,sql,"select");
        //从第二行开始输入数据，共list.size/head.size行
        for(int i = 1; i <= list.size() / head.size(); i++){
            Row row = sheet.createRow(i);
            //每行创建head.size个单元格
            for (int j = 0; j < head.size(); j++) {
                Cell cell = row.createCell(j);
                //得到对应单元格的内容（没错这是个数学题）
                cell.setCellValue(list.get( ((i-1)*head.size() + (j+1)) -1 ));
            }
        }

        //创建文件和文件流
        File file = new File(filePath);
        FileOutputStream stream = null;
        try {
            file.createNewFile();

            //将创建好的工作簿写入文件流
            stream = FileUtils.openOutputStream(file);
            workbook.write(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将数据从Excel导出到数据库
     * @param filePath 文件路径
     */
    public void exportExcel(String filePath){
        List<String> head;

        //需要解析的Excel文件
        File file = new File(filePath);
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(FileUtils.openInputStream(file));
            //获取第一个sheet表
            Sheet sheet = workbook.getSheetAt(0);

            //flag ：获取表头第一个非空字段
            boolean flag = true;
            //sql ：根据表头调整sql语句
            String sql = null;
            //遍历每行(getLastRowNum()的返回值为最后一行的索引，会比总行数少一行)
            for(int i = 0; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                //若该行非空(过滤空行)
                if (row != null) {
                    //在每行开始时初始化head
                    head = new ArrayList<>();
                    //遍历每个单元格(getLastCellNum()的返回值值即为该行总列数)
                    for(int j = 0; j < row.getLastCellNum(); j++){
                        Cell cell = row.getCell(j);
                        //若单元格不为空（过滤空列）
                        if (cell != null) {
                            //将单元格类型转为String
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            //获取每个单元格的值
                            String value = cell.getStringCellValue();
                            //若单元格内容不为空（过滤空白单元格）
                            if (value != null) {

                                //得到第一个非空单元格的内容（即表头第一个字段）
                                if(flag){
                                    //根据表头处理sql语句
                                    if(value.equals("姓名")){
                                        sql = INSERT_INTO_MEMBERS;
                                    } else if(value.equals("编号")){
                                        sql = INSERT_INTO_BOOKS;
                                    }
                                }
                                //flag置为假
                                flag = false;

                                //添加该单元格的值
                                head.add(value);

                            }else{
                                //若该单元格为空，置为""
                                head.add("");
                            }
                        }else{
                            //若该单元格为空，置为""
                            head.add("");
                        }
                    }

                    //若该行末尾出现空单元格，用""填充
                    while((head.size() < sheet.getRow(0).getLastCellNum())){
                        head.add("");
                    }

                    //存储单元格内容（不存储表头）
                    if (i > 0) {
                        //连接数据库，根据sql语句注入值
                        connMysql(head,sql,"insert");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 连接数据库
     * @param head 传入表头
     * @param sql 查询语句
     */
    public List<String> connMysql(List<String> head, String sql, String sqlType){
        //定义MYSQL连接对象
        Connection connection = null;
        //创建声明
        PreparedStatement preparedStatement = null;
        //创建数据集
        ResultSet resultSet = null;
        //创建返回的数组对象
        List<String> list = null;

        try {
            //注入驱动
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //连接本地MYSQL
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hrms" +
                    "?useUnicode=true&characterEncoding=utf-8&useSSL=false", "root", "root");

            //创建sql语句并装载
            preparedStatement = connection.prepareStatement(sql);

            if (sqlType.equals("insert")) {
                //读取Excel时，类型为插入
                //动态注入数据
                for(int i = 0; i < head.size(); i++){
                    preparedStatement.setString(i+1, head.get(i));
                }
                //执行SQL语句
                preparedStatement.execute();
            } else if(sqlType.equals("select")) {
                //创建Excel时，类型为查询
                //实例化数据集
                resultSet = preparedStatement.executeQuery();

                //将表头转化为数据库中对应的存储字段
                List<String> title = null;
                if(head.get(0).equals("姓名")){
                    title = Arrays.asList("name","sex","age");
                }else if(head.get(0).equals("编号")){
                    title = Arrays.asList("id","name","user");
                }

                //将数据集转化为数组
                list = new ArrayList<>();
                while (resultSet.next()) {
                    for(int i = 0; i < title.size(); i++){
                        list.add(resultSet.getString(title.get(i)));
                    }
                }

                //关闭连接
                resultSet.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭各连接
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
