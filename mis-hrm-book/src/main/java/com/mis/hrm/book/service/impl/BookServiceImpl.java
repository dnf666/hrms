package com.mis.hrm.book.service.impl;

import com.mis.hrm.book.dao.BookMapper;
import com.mis.hrm.book.po.Book;
import com.mis.hrm.book.service.BookService;
import com.mis.hrm.util.ConstantValue;
import com.mis.hrm.util.ExcelUtil;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.enums.Sex;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private static final int Book_PARAMTER_COUNT = 4;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private BookMapper bookMapper;

    /**
     * １．传入的对象为空，删除失败
     * ２．传入对象的bookid为空，删除失败
     * ３．根据数据库的结果查看。
     *
     * @param key
     * @return 失败？０：１
     */
    @Override
    public int deleteByPrimaryKey(Book key) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(key);
        } catch (NullPointerException e) {
            //传入的对象为空,直接返回，不用再去数据库
            logger.error("deleteByPrimaryKey:key为空");
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
        boolean isOk = bookOptional.map(Book::getBookId).isPresent();
        //如果bookId为空，抛出异常
        if (!isOk) {
            logger.error("bookId为空，删除失败");
            throw new InfoNotFullyException("bookId未设置");
        }
        logger.info("deleteByPrimaryKey----通过主键删除book信息");
        return bookMapper.deleteByPrimaryKey(key);
    }

    /**
     * １．插入的基本数据要满足，否则插入直接失败
     *
     * @param record
     * @return 失败？０：１+
     */
    @Override
    public int insert(Book record) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(record);
        } catch (NullPointerException e) {
            logger.error("insert:record为空，插入失败");
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
        //如果满足插入的基本条件，那么尝试向数据库中插入数据，否则直接失败
        if (!bookOptional.filter(Book::baseRequied).isPresent()) {
            throw new InfoNotFullyException("插入book的基本信息未满足");
        }
        return bookMapper.insert(record);
    }

    /**
     * 根据传入的bookId查询书籍信息
     *
     * @param key
     * @return success? book : null;
     */
    @Override
    public Book selectByPrimaryKey(Book key) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(key);
        } catch (NullPointerException e) {
            logger.error("selectByPrimaryKey:key");
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
        boolean isOk = bookOptional
                .filter(t -> StringUtil.notEmpty(t.getBookId()))
                .isPresent();
        if (!isOk) {
            logger.error("bookId为空,查找停止");
            throw new InfoNotFullyException("bookId未设置");
        }
        return bookMapper.selectByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(Book record) {
        Optional<Book> bookOptional;
        try {
            bookOptional = Optional.of(record);
        } catch (NullPointerException e) {
            logger.error("insert:record为空");
            throw new NullPointerException(ConstantValue.GET_NULL_OBJECT);
        }
//        要求bookid不为空，否则更新失败
        boolean isOk = bookOptional
                .filter(t -> StringUtil.notEmpty(t.getBookId()))
                .isPresent();
        if (!isOk) {
            logger.error("bookId is null,更新失败");
            throw new InfoNotFullyException("bookId为空");
        }
        return bookMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Book> selectByPrimaryKeyAndPage(Book book, Pager<Book> pager) {
        int offset = pager.getOffset();
        int size = pager.getPageSize();
        int total = bookMapper.getCountByKeys(book);
        pager.setRecordSize(total);
        return bookMapper.selectByPrimaryKeyAndPage(book,offset,size);
    }

    @Override
    public int deleteByids(List<Integer> ids, String companyId) {
        if (ids.size() != 0) {
            int stateNum = bookMapper.deleteByIds(ids, companyId);
            if (stateNum > 0) {
                logger.info("成功删除" + stateNum + "本书");
                return stateNum;
            } else {
                logger.debug("图书删除失败");
                throw new RuntimeException("图书删除失败");
            }
        } else {
            logger.debug("编号为空");
            throw new InfoNotFullyException("编号为空");
        }
    }

    @Override
    public int importBookFromExcel(MultipartFile file,String companyId) throws IOException {
        Sheet sheet = ExcelUtil.getSheet(file);
        List<Row> rows = ExcelUtil.getRowFromSheet(sheet);
        List<Book> list = new ArrayList<>();
        for (int i = 1;i<rows.size();i++){
            List<Cell> cells = ExcelUtil.getCellFromRow(rows.get(i));
            if (cells.size()!= Book_PARAMTER_COUNT){
                throw new IOException(ErrorCode.MESSAGE_NOT_COMPLETE.getDescription());
            }
            //todo 没想到更好的方法。这段代码复用性太差。败笔啊
            String bookName =ExcelUtil.getValueByIndex(cells,0);
            String type = ExcelUtil.getValueByIndex(cells,1);
            Integer nums;
            try {
                String num  = ExcelUtil.getValueByIndex(cells,2);
                nums = Integer.parseInt(num);
            }catch (NumberFormatException e){
                throw new NumberFormatException("数量不是数字");
            }
            String version  = ExcelUtil.getValueByIndex(cells,3);
            Book book = Book.builder().companyId(companyId).bookName(bookName).category(type).quantity(nums).version(version).build();
            logger.info("book {}",book.toString());
            list.add(book);
        }
         return bookMapper.insertMany(list);

    }

    @Override
    public List<Book> selectByMultiKey(Book book) {
        return bookMapper.selectByMultiKey(book);

    }

    @Override
    public HSSFWorkbook exportExcel(List<Book> lists) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.addMergedRegion(new CellRangeAddress(0, lists.size() + 1, 0, 5));
        Row row0 = sheet.createRow(0);
        //todo 如果从零开始就会把版本这一列消掉。。。这是问题
        row0.createCell(1).setCellValue("书名");
        row0.createCell(2).setCellValue("类别");
        row0.createCell(3).setCellValue("数量");
        row0.createCell(4).setCellValue("版本");
        for (int i = 0; i < lists.size(); i++) {
            Row row3 = sheet.createRow(i + 1);
            Book book1 = lists.get(i);
            row3.createCell(1).setCellValue(book1.getBookName());
            row3.createCell(2).setCellValue(book1.getCategory());
            row3.createCell(3).setCellValue(book1.getQuantity());
            row3.createCell(4).setCellValue(book1.getVersion());
        }
        logger.info("excel生成完毕");
        return workbook;
    }
}
