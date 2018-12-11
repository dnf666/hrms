package com.mis.hrm.work.service.impl;

import com.mis.hrm.util.ExcelUtil;
import com.mis.hrm.util.ObjectNotEmpty;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.enums.Sex;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.work.dao.WorkMapper;
import com.mis.hrm.work.model.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkServiceImpl implements WorkService {

    private static final int WORK_PARAMTER_COUNT = 9;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private WorkMapper workMapper;

    @Override
    public int deleteByPrimaryKey(Whereabout key) {
        if(StringUtil.notEmpty(key.getCompanyId()) && StringUtil.notEmpty(key.getNum())){
            int stateNum = workMapper.deleteByPrimaryKey(key);
            if(stateNum > 0){
                logger.info("去向信息删除成功");
                return stateNum;
            } else {
                logger.info("去向信息删除失败");
                throw new RuntimeException("去向信息删除失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public int insert(Whereabout record) {
        if(StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())){
            int stateNum = workMapper.insert(record);
            if(stateNum > 0){
                logger.info("去向信息添加成功");
                return stateNum;
            } else {
                logger.info("去向信息添加失败");
                throw new RuntimeException("去向信息添加失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public Whereabout selectByPrimaryKey(Whereabout key) {
        Whereabout selectOne = workMapper.selectByPrimaryKey(key);
        if(selectOne != null){
            logger.info("成员信息查找成功");
            return selectOne;
        } else {
            logger.info("该成员不存在");
            throw new NullPointerException("该成员不存在");
        }
    }

    @Override
    public int updateByPrimaryKey(Whereabout record) {
        if(StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())){
            int stateNum = workMapper.updateByPrimaryKey(record);
            if(stateNum > 0){
                logger.info("去向信息更新成功");
                return stateNum;
            } else {
                logger.info("去向信息更新失败");
                throw new RuntimeException("去向信息更新失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public List<Whereabout> selectByPrimaryKeyAndPage(Whereabout key, Pager<Whereabout> pager) {
        return null;
    }

    @Override
    public int deleteByNums(List<String> nums,String companyId) {
        if(!nums.equals(new ArrayList<>())){
            int stateNum = workMapper.deleteByNums(nums,companyId);
            if(stateNum > 0){
                logger.info("成功删除" + stateNum + "名成员信息");
                return stateNum;
            } else {
                logger.info("信息删除失败");
                throw new RuntimeException("信息删除失败");
            }
        } else {
            logger.info("传入学号为空");
            throw new InfoNotFullyException("传入学号为空");
        }
    }

    @Override
    public Long countWorkers() {
        return workMapper.countWorkers();
    }

    @Override
    public List<Whereabout> getAllGraduates(Pager<Whereabout> pager,String conpanyId) {
        return workMapper.getAllGraduates(pager,conpanyId);
    }

    @Override
    public List<Whereabout> filter(Pager<Whereabout> pager, Whereabout whereabout) throws RuntimeException{
        Integer total = workMapper.getCountByKeys(whereabout);
        pager.setPageTotal(total);
        if (ObjectNotEmpty.notEmpty(whereabout)) {
            return workMapper.filter(pager,whereabout);
        } else {
            logger.info("未填写过滤条件");
            throw new InfoNotFullyException("未填写过滤条件");
        }
    }

    @Override
    public void importWorkerFromExcel(MultipartFile file) throws IOException {
//        Sheet sheet = ExcelUtil.getSheet(file);
//        List<Row> rows = ExcelUtil.getRowFromSheet(sheet);
//        List<Whereabout> list = new ArrayList<>();
//        for (int i = 1;i<rows.size();i++){
//            List<Cell> cells = ExcelUtil.getCellFromRow(rows.get(i));
//            if (cells.size()!= WORK_PARAMTER_COUNT){
//                throw new IOException(ErrorCode.MESSAGE_NOT_COMPLETE.getDescription());
//            }
//            //todo 没想到更好的方法。这段代码复用性太差。败笔啊
//            String num =ExcelUtil.getStringByIndex(cells,0);
//            String name = ExcelUtil.getStringByIndex(cells,1);
//            String phoneNumber = ExcelUtil.getStringByIndex(cells,2);
//            String email = ExcelUtil.getStringByIndex(cells,3);
//            String grade = ExcelUtil.getStringByIndex(cells,4);
//            String sex = ExcelUtil.getStringByIndex(cells,5);
//            if (!Sex.judgeSex(sex)){
//                throw new IOException("性别不合法");
//            }
//            String profession = ExcelUtil.getStringByIndex(cells,6);
//            String department = ExcelUtil.getStringByIndex(cells,7);
//            String workPlace = ExcelUtil.getStringByIndex(cells,8);
//            //todo companyId没传进来
//            Whereabout whereabout = Whereabout.builder().companyId("").num(num).name(name).phoneNumber(phoneNumber).email(email).grade(grade).sex(sex).profession(profession).department(department).workPlace(workPlace).build();
//            logger.info("whereAbout {}",whereabout.toString());
//            list.add(whereabout);
//        }
//        workMapper.insertMany(list);
    }
}
