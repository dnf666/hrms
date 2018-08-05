package com.mis.hrm.member.service.impl;

import com.mis.hrm.member.dao.MemberMapper;
import com.mis.hrm.member.pojos.Member;
import com.mis.hrm.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional  //开启事务管理
public class MemberServiceImpl implements MemberService {
    @Resource
    private MemberMapper memberMapper;

    @Override
    public int deleteByPrimaryKey(Member key) {
        if(key.getCompanyId() != null && key.getNum() != null
                && !key.getCompanyId().equals("") && !key.getNum().equals("")){
            try{
                int stateNum = memberMapper.deleteByPrimaryKey(key);
                if(stateNum > 0){
                    return stateNum;
                } else {
                    throw new RuntimeException("删除添加失败");
                }
            } catch (RuntimeException e){
                throw new RuntimeException("成员删除失败" + e.getMessage());
            }
        } else {
            throw new RuntimeException("主键信息不能为空");
        }
    }

    @Override
    public int insert(Member record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals("") && !record.getNum().equals("")){
            try{
                int stateNum = memberMapper.insert(record);
                if(stateNum > 0){
                    return stateNum;
                } else {
                    throw new RuntimeException("成员添加失败");
                }
            } catch (RuntimeException e){
                throw new RuntimeException("成员添加失败" + e.getMessage());
            }
        } else {
            throw new RuntimeException("主键信息不能为空");
        }
    }

    @Override
    public Member selectByPrimaryKey(Member key) {
        try{
            Member member = memberMapper.selectByPrimaryKey(key);
            if(member != null){
                return member;
            } else {
                throw new RuntimeException("该成员不存在");
            }
        } catch (RuntimeException e){
            throw new RuntimeException("成员查找失败" + e.getMessage());
        }
    }

    @Override
    public int updateByPrimaryKey(Member record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals("") && !record.getNum().equals("")){
            try{
                int stateNum = memberMapper.updateByPrimaryKey(record);
                if(stateNum > 0){
                    return stateNum;
                } else {
                    throw new RuntimeException("成员更新失败");
                }
            } catch (RuntimeException e){
                throw new RuntimeException("成员更新失败" + e.getMessage());
            }
        } else {
            throw new RuntimeException("主键信息不能为空");
        }
    }
}
