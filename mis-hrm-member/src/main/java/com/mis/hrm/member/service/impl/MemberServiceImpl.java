package com.mis.hrm.member.service.impl;

import com.mis.hrm.member.dao.MemberMapper;
import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class MemberServiceImpl implements MemberService {
    //字符常量替换
    private static final String BLANK = "";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MemberMapper memberMapper;

    @Override
    public int deleteByPrimaryKey(Member key) {
        if(key.getCompanyId() != null && key.getNum() != null
                && !key.getCompanyId().equals(BLANK) && !key.getNum().equals(BLANK)){
            int stateNum = memberMapper.deleteByPrimaryKey(key);
            if(stateNum > 0){
                logger.info("成员信息删除成功");
                return stateNum;
            } else {
                logger.info("成员信息删除失败");
            }
        } else {
            logger.info("主键信息为空");
        }
        return 0;
    }

    @Override
    public int insert(Member record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals(BLANK) && !record.getNum().equals(BLANK)){
            int stateNum = memberMapper.insert(record);
            if(stateNum > 0){
                logger.info("成员信息添加成功");
                return stateNum;
            } else {
                logger.info("成员信息添加失败");
            }
        } else {
            logger.info("主键信息为空");
        }
        return 0;
    }

    @Override
    public Member selectByPrimaryKey(Member key) {
        Member member = memberMapper.selectByPrimaryKey(key);
        if(member != null){
            logger.info("成员信息查找成功");
            return member;
        } else {
            logger.info("成员不存在");
        }
        return null;
    }

    @Override
    public int updateByPrimaryKey(Member record) {
        if(record.getCompanyId() != null && record.getNum() != null
                && !record.getCompanyId().equals(BLANK) && !record.getNum().equals(BLANK)){
            int stateNum = memberMapper.updateByPrimaryKey(record);
            if(stateNum > 0){
                logger.info("成员信息更新成功");
                return stateNum;
            } else {
                logger.info("成员信息更新失败");
            }
        } else {
            logger.info("主键信息为空");
        }
        return 0;
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
    public List<Member> findByPhoneNumber(Pager<Member> pager, String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.equals(BLANK)) {
            return memberMapper.findByPhoneNumber(pager,phoneNumber);
        } else {
            logger.info("电话信息为空");
        }
        return null;
    }

    @Override
    public List<Member> findByEmail(Pager<Member> pager, String email) {
        if (email != null && !email.equals(BLANK)) {
            return memberMapper.findByEmail(pager,email);
        } else {
            logger.info("邮箱信息为空");
        }
        return null;
    }

    @Override
    public List<Member> findByName(Pager<Member> pager, String name) {
        if (name != null && !name.equals(BLANK)) {
            return memberMapper.findByName(pager,name);
        } else {
            logger.info("昵称信息为空");
        }
        return null;
    }

}
