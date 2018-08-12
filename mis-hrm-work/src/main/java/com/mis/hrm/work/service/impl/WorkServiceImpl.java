package com.mis.hrm.work.service.impl;

import com.mis.hrm.util.Pager;
import com.mis.hrm.work.dao.WorkMapper;
import com.mis.hrm.work.model.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorkServiceImpl implements WorkService {
    //字符常量替换
    private static final String BLANK = "";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private WorkMapper workMapper;

    @Override
    public int deleteByPrimaryKey(Whereabout key) {
        if(key.getCompanyId() != null && key.getNum() != null
                && !key.getCompanyId().equals(BLANK) && !key.getNum().equals(BLANK)){
            int stateNum = workMapper.deleteByPrimaryKey(key);
            if(stateNum > 0){
                logger.info("去向信息删除成功");
                return stateNum;
            } else {
                logger.info("去向信息删除失败");
            }
        } else {
            logger.info("主键信息为空");
        }
        return 0;
    }

    @Override
    public int insert(Whereabout record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals(BLANK) && !record.getNum().equals(BLANK)){
            int stateNum = workMapper.insert(record);
            if(stateNum > 0){
                logger.info("去向信息添加成功");
                return stateNum;
            } else {
                logger.info("去向信息添加失败");
            }
        } else {
            logger.info("主键信息为空");
        }
        return 0;
    }

    @Override
    public Whereabout selectByPrimaryKey(Whereabout key) {
        Whereabout selectOne = workMapper.selectByPrimaryKey(key);
        if(selectOne != null){
            logger.info("成员信息查找成功");
            return selectOne;
        } else {
            logger.info("成员不存在");
        }
        return null;
    }

    @Override
    public int updateByPrimaryKey(Whereabout record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals(BLANK) && !record.getNum().equals(BLANK)){
            int stateNum = workMapper.updateByPrimaryKey(record);
            if(stateNum > 0){
                logger.info("去向信息更新成功");
                return stateNum;
            } else {
                logger.info("去向信息更新失败");
            }
        } else {
            logger.info("主键信息为空");
        }
        return 0;
    }

    @Override
    public Long countWorkers() {
        return workMapper.countWorkers();
    }

    @Override
    public List<Whereabout> findByGrade(Pager<Whereabout> pager, String grade) {
        if(grade != null && !grade.equals(BLANK)){
            return workMapper.findByGrade(pager,grade);
        } else {
            logger.info("年级信息为空");
            return null;
        }
    }

    @Override
    public List<Whereabout> findByName(Pager<Whereabout> pager, String name) {
        if (name != null && !name.equals(BLANK)) {
            return workMapper.findByName(pager,name);
        } else {
            logger.info("昵称信息为空");
            return null;
        }
    }

    @Override
    public List<Whereabout> getAllGraduates(Pager<Whereabout> pager) {
        return workMapper.getAllGraduates(pager);
    }
}
