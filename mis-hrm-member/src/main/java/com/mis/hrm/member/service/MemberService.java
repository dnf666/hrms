package com.mis.hrm.member.service;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.Pager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberService extends BaseService<Member> {
    //批量删除
    int deleteByNums(List<String> nums);

    //统计成员总数
    Long countMembers();

    //分页查看所有成员
    List<Member> getAllMembers(Pager<Member> pager);

    //过滤
    List<Member> filter(@Param("pager") Pager<Member> pager, @Param("member") Member member);
}
