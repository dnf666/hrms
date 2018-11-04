package com.mis.hrm.member.service;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.Pager;

import java.util.List;

public interface MemberService extends BaseService<Member> {
    //批量删除
    int deleteByNums(List<String> nums,String companyId);

    //统计成员总数
    Integer countMembers(Member member);

    //分页查看所有成员
    List<Member> getAllMembers(Pager<Member> pager);

    //过滤
    List<Member> filter(Pager<Member> pager, Member member);
}
