package com.mis.hrm.project.service.impl;

import com.mis.hrm.project.dao.ProjectMapper;
import com.mis.hrm.project.po.Project;
import com.mis.hrm.project.service.ProjectService;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectMapper projectMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public int deleteByPrimaryKey(Project key) throws InfoNotFullyExpection {
        try {
            Optional<Project> projectOptional = Optional.of(key);
        } catch (NullPointerException n){
            logger.error("项目为空，删除失败");
            throw new NullPointerException("删除的prject对象为空");
        }
        if (!StringUtil.notEmpty(key.getCompanyId())){
            logger.error("公司id不存在，删除失败");
            throw new InfoNotFullyExpection("公司id为空");
        }
        return projectMapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(Project record) throws InfoNotFullyExpection {
        Optional<Project> projectOptional;
        try {
            projectOptional = Optional.of(record);
        } catch (NullPointerException n){
            logger.error("项目为空，删除失败");
            throw new NullPointerException("删除的prject对象为空");
        }
        boolean isOk = projectOptional
                .filter(t -> t.baseRequired())
                .isPresent();
        if (!isOk){
            logger.error("无法满足插入的基本条件");
            throw new InfoNotFullyExpection("插入的数据项条件缺失");
        }
        return projectMapper.insert(record);
    }

    @Override
    public Project selectByPrimaryKey(Project key) throws InfoNotFullyExpection {
        try {
            Optional<Project> projectOptional = Optional.of(key);
        } catch (NullPointerException n){
            logger.error("项目为空，查找失败");
            throw new NullPointerException("传入的prject对象为空");
        }
        if (!StringUtil.notEmpty(key.getCompanyId())){
            logger.error("公司id不存在，查询失败");
            throw new InfoNotFullyExpection("公司id为空");
        }
        return projectMapper.selectByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(Project record) throws InfoNotFullyExpection {
        try {
            Optional<Project> projectOptional = Optional.of(record);
        } catch (NullPointerException n){
            logger.error("项目为空，更新失败");
            throw new NullPointerException("传入的prject对象为空");
        }
        if (!StringUtil.notEmpty(record.getCompanyId())){
            logger.error("公司id不存在，更新失败");
            throw new InfoNotFullyExpection("公司id为空");
        }
        return projectMapper.updateByPrimaryKey(record);
    }
}