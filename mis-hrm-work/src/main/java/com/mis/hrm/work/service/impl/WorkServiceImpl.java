package com.mis.hrm.work.service.impl;

import com.mis.hrm.util.ObjectNotEmpty;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.exception.InfoNotFullyException;
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
    public Long countWorkers() {
        return workMapper.countWorkers();
    }

    @Override
    public List<Whereabout> findByGrade(Pager<Whereabout> pager, String grade) {
        if(StringUtil.notEmpty(grade)){
            return workMapper.findByGrade(pager,grade);
        } else {
            logger.info("年级信息为空");
            throw new InfoNotFullyException("年级信息为空");
        }
    }

    @Override
    public List<Whereabout> findByName(Pager<Whereabout> pager, String name) {
        if (StringUtil.notEmpty(name)) {
            return workMapper.findByName(pager,name);
        } else {
            logger.info("昵称信息为空");
            throw new InfoNotFullyException("昵称信息为空");
        }
    }

    @Override
    public List<Whereabout> getAllGraduates(Pager<Whereabout> pager) {
        return workMapper.getAllGraduates(pager);
    }

    @Override
    public List<Whereabout> filter(Pager<Whereabout> pager, Whereabout whereabout) throws RuntimeException{
        if (ObjectNotEmpty.notEmpty(whereabout)) {
            return workMapper.filter(pager,whereabout);
        } else {
            logger.info("未填写过滤条件");
            throw new InfoNotFullyException("未填写过滤条件");
        }
    }
}
