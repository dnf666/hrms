package com.mis.hrm.work.service.impl;

import com.mis.hrm.work.dao.WorkMapper;
import com.mis.hrm.work.pojos.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WorkServiceImpl implements WorkService {
    @Resource
    private WorkMapper workMapper;

    @Override
    public int deleteByPrimaryKey(Whereabout key) {
        if(key.getCompanyId() != null && key.getNum() != null
                && !key.getCompanyId().equals("") && !key.getNum().equals("")){
            try{
                int stateNum = workMapper.deleteByPrimaryKey(key);
                if(stateNum > 0){
                    return stateNum;
                } else {
                    throw new RuntimeException("成员去向删除失败");
                }
            } catch (RuntimeException e){
                throw new RuntimeException("成员去向删除失败" + e.getMessage());
            }
        } else {
            throw new RuntimeException("主键信息不能为空");
        }
    }

    @Override
    public int insert(Whereabout record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals("") && !record.getNum().equals("")){
            try{
                int stateNum = workMapper.insert(record);
                if(stateNum > 0){
                    return stateNum;
                } else {
                    throw new RuntimeException("成员去向添加失败");
                }
            } catch (RuntimeException e){
                throw new RuntimeException("成员去向添加失败" + e.getMessage());
            }
        } else {
            throw new RuntimeException("主键信息不能为空");
        }
    }

    @Override
    public Whereabout selectByPrimaryKey(Whereabout key) {
        try{
            Whereabout whereabout = workMapper.selectByPrimaryKey(key);
            if(whereabout != null){
                return whereabout;
            } else {
                throw new RuntimeException("该成员不存在");
            }
        } catch (RuntimeException e){
            throw new RuntimeException("成员去向查找失败" + e.getMessage());
        }
    }

    @Override
    public int updateByPrimaryKey(Whereabout record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals("") && !record.getNum().equals("")){
            try{
                int stateNum = workMapper.updateByPrimaryKey(record);
                if(stateNum > 0){
                    return stateNum;
                } else {
                    throw new RuntimeException("成员去向更新失败");
                }
            } catch (RuntimeException e){
                throw new RuntimeException("成员去向更新失败" + e.getMessage());
            }
        } else {
            throw new RuntimeException("主键信息不能为空");
        }
    }
}
