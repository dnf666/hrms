package com.mis.hrm.util.demo.excel.impl;

import com.mis.hrm.util.demo.excel.DemoExcel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoExcelImpl implements DemoExcel {
    //表格title
    final private List<String> MEMBER_ENG = Arrays.asList("company_id","num","name","phone_number","email","grade","sex","profession","department");
    final private List<String> MEMBER_CHI = Arrays.asList("公司id","学号","成员名","电话","邮箱","年级","性别","专业","部门");
    final private List<String> WHEREABOUT_ENG = Arrays.asList("company_id","num","name","phone_number","email","grade","sex","profession","department","work_place");
    final private List<String> WHEREABOUT_CHI = Arrays.asList("公司id","学号","成员名","电话","邮箱","年级","性别","专业","部门","工作地点");
    //SQL语句
    final private String INSERT_INTO_MEMBER = "insert into member (company_id,num,name,phone_number,email,grade,sex,profession,department) values (?,?,?,?,?,?,?,?,?);";
    final private String INSERT_INTO_WHEREABOUT = "insert into whereabout (company_id,num,name,phone_number,email,grade,sex,profession,department,work_place) values (?,?,?,?,?,?,?,?,?,?);";
    final private String SELECT_FORM_MEMBER = "select * from member; ";
    final private String SELECT_FROM_WHEREABOUT = "select * from whereabout";

    /**
     * 将数据从数据库导入到Excel
     * @param tableTile 表格title(如member)
     * @param type 是否为模板下载
     */
    public byte[] importExcel(String tableTile, String type){
        //创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建工作表
        Sheet sheet = workbook.createSheet();
        //创建表头
        Row headRow = sheet.createRow(0);
        //为表头单元格填入数据
        Cell headCell;

        //设置中文表头并根据表头调整sql
        List<String> head = null;
        String sql = null;
        if(tableTile.equals("member")){
            head = MEMBER_CHI;
            sql = SELECT_FORM_MEMBER;
        } else if(tableTile.equals("whereabout")){
            head = WHEREABOUT_CHI;
            sql = SELECT_FROM_WHEREABOUT;
        }

        //将表头放入Excel
        for(int i = 0; i < head.size(); i++){
            headCell = headRow.createCell(i);
            headCell.setCellValue(head.get(i));
        }

        if (type.equals("excel")) {
            //将查询结果放进数组
            List<String> list = connMysql(head,sql,"select");
            //从第二行开始输入数据，共list.size/head.size行
            for(int i = 1; i <= list.size() / head.size(); i++){
                Row row = sheet.createRow(i);
                //每行创建head.size个单元格
                for (int j = 0; j < head.size(); j++) {
                    Cell cell = row.createCell(j);
                    //得到对应单元格的内容
                    cell.setCellValue(list.get( ((i-1)*head.size() + (j+1)) -1 ));
                }
            }
        } else if (type.equals("download")) {
            //如果为模板下载，不做操作（即只放入表头）
        }


        //将工作簿写入输出流转再化成字节组
        byte[] bytes = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            //将创建好的工作簿写入输出流
            workbook.write(outputStream);
            //将输出流转化为字节组
            bytes = outputStream.toByteArray();

        } catch (IOException e) {
             e.printStackTrace();
        } finally {
            //关闭流
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //返回字节组
        return bytes;
    }

    /**
     * 将数据从Excel导出到数据库
     * @param multipartFile
     */
    public void exportExcel(MultipartFile multipartFile){
        List<String> head;

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            //获取第一个sheet表
            Sheet sheet = workbook.getSheetAt(0);

            //sql ：根据表头调整sql语句
            String sql = null;
            //遍历每行(getLastRowNum()的返回值为最后一行的索引，会比总行数少一行)
            for(int i = 0; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                //若该行非空(过滤空行)
                if (row != null) {
                    //在每行开始时初始化head
                    head = new ArrayList<>();

                    //根据每行的单元格数确定sql语句
                    if((row.getLastCellNum()) == MEMBER_ENG.size()){
                        sql = INSERT_INTO_MEMBER;
                    } else if((row.getLastCellNum()) == WHEREABOUT_ENG.size()){
                        sql = INSERT_INTO_WHEREABOUT;
                    }

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
     * @param head 中文表头
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
                if(head.size() == MEMBER_ENG.size()){
                    title = MEMBER_ENG;
                }else if(head.size() == WHEREABOUT_ENG.size()){
                    title = WHEREABOUT_ENG;
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
