package com.mis.hrm.member.service.impl;

import com.mis.hrm.member.dao.MemberMapper;
import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MemberMapper memberMapper;

    @Override
    public int deleteByPrimaryKey(Member key) throws RuntimeException{
        if(StringUtil.notEmpty(key.getCompanyId()) && StringUtil.notEmpty(key.getNum())){
            int stateNum = memberMapper.deleteByPrimaryKey(key);
            if(stateNum > 0){
                logger.info("成员信息删除成功");
                return stateNum;
            } else {
                logger.info("成员信息删除失败");
                throw new RuntimeException("成员信息删除失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public int insert(Member record) throws RuntimeException{
        if(StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())){
            int stateNum = memberMapper.insert(record);
            if(stateNum > 0){
                logger.info("成员信息添加成功");
                return stateNum;
            } else {
                logger.info("成员信息添加失败");
                throw new RuntimeException("成员信息添加失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public Member selectByPrimaryKey(Member key) throws RuntimeException{
        Member member = memberMapper.selectByPrimaryKey(key);
        if(member != null){
            logger.info("成员信息查找成功");
            return member;
        } else {
            logger.info("成员不存在");
            throw new NullPointerException("成员不存在");
        }
    }

    @Override
    public int updateByPrimaryKey(Member record) throws RuntimeException{
        if(StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())){
            int stateNum = memberMapper.updateByPrimaryKey(record);
            if(stateNum > 0){
                logger.info("成员信息更新成功");
                return stateNum;
            } else {
                logger.info("成员信息更新失败");
                throw new RuntimeException("成员信息更新失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public Long countMembers() {
        return memberMapper.countMembers();
    }

    @Override
    public List<Member> getAllMembers(Pager<Member> pager) {
        return memberMapper.getAllMembers(pager);
    }

    @Override
    public List<Member> findByPhoneNumber(Pager<Member> pager, String phoneNumber) throws RuntimeException{
        if (StringUtil.notEmpty(phoneNumber)) {
            return memberMapper.findByPhoneNumber(pager,phoneNumber);
        } else {
            logger.info("电话信息为空");
            throw new InfoNotFullyException("电话信息为空");
        }
    }

    @Override
    public List<Member> findByEmail(Pager<Member> pager, String email) throws RuntimeException{
        if (StringUtil.notEmpty(email)) {
            return memberMapper.findByEmail(pager,email);
        } else {
            logger.info("邮箱信息为空");
            throw new InfoNotFullyException("邮箱信息为空");
        }
    }

    @Override
    public List<Member> findByName(Pager<Member> pager, String name) throws RuntimeException{
        if (StringUtil.notEmpty(name)) {
            return memberMapper.findByName(pager,name);
        } else {
            logger.info("昵称信息为空");
            throw new InfoNotFullyException("昵称信息为空");
        }
    }
}
