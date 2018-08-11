package com.mis.hrm.member.dao;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.util.BaseMapper;
import com.mis.hrm.util.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemberMapper extends BaseMapper<Member> {
    //统计成员总数
    Long countMembers();

    //分页查看所有成员
    List<Member> getAllMembers(Pager<Member> pager);

    //根据电话的模糊分页查找
    List<Member> findByPhoneNumber(@Param("pager") Pager<Member> pager,@Param("phoneNumber") String phoneNumber);

    //根据邮箱的模糊分页查找
    List<Member> findByEmail(@Param("pager") Pager<Member> pager,@Param("email") String email);

    //根据昵称的模糊分页查找
    List<Member> findByName(@Param("pager") Pager<Member> pager,@Param("name") String name);

}
