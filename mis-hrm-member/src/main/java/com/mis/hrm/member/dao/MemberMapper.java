package com.mis.hrm.member.dao;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.util.BaseMapper;
import com.mis.hrm.util.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemberMapper extends BaseMapper<Member> {
    //批量删除
    int deleteByNums(List<String> nums,String companyId);

    //统计成员总数
    Integer countMembers(Member member);

    //分页查看所有成员
    List<Member> getAllMembers(Pager<Member> pager);

    //过滤
    List<Member> filter(@Param("pager") Pager<Member> pager, @Param("member") Member member);

    Integer countMembersByKeys(Member member);
}
