package com.mis.hrm.member.dao;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.util.Pager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/spring-member.xml")
public class MemberMapperTest {
    @Autowired
    private MemberMapper memberMapper;

    private Member member;

    private Pager<Member> pager;

    @Before
    public void setUp() throws Exception{
        member = Member.builder()
                .companyId("信管工作室")
                .num("2014211009")
                .name("刘西")
                .phoneNumber("1111")
                .email("1111@126.com")
                .grade("2014级")
                .sex("男")
                .profession("信息管理与信息系统")
                .department("后台").build();

        pager = new Pager<>();
        pager.setPageSize(2);
        pager.setCurrentPage(2);
    }

    @Test
    @Ignore
    public void testDeleteByPrimaryKey() {
        member.setNum("2017210001");
        Assert.assertEquals(1,memberMapper.deleteByPrimaryKey(member));

        member.setNum("2017210004");
        Assert.assertEquals(0,memberMapper.deleteByPrimaryKey(member));
    }

    @Test
    @Ignore
    public void testInsert() {
        Assert.assertEquals(1,memberMapper.insert(member));
    }

    @Test
    public void testSelectByPrimaryKey() {
        member.setNum("2017210003");
        Member selectedMember = memberMapper.selectByPrimaryKey(member);
        Assert.assertEquals("王二",selectedMember.getName());
        Assert.assertEquals("1010",selectedMember.getPhoneNumber());

        member.setNum("2017210004");
        Assert.assertEquals(null,memberMapper.selectByPrimaryKey(member));
    }

    @Test
    @Ignore
    public void testUpdateByPrimaryKey() {
        member.setNum("2017210002");
        Assert.assertEquals(1,memberMapper.updateByPrimaryKey(member));

        member.setNum("2017210004");
        Assert.assertEquals(0,memberMapper.updateByPrimaryKey(member));
    }

    @Test
    public void testCountMembers(){
        Assert.assertEquals((Long)(long)5,memberMapper.countMembers());
    }

    @Test
    public void testGetAllMembers(){
        Assert.assertEquals(2,memberMapper.getAllMembers(pager).size());
    }

    @Test
    public void testFindByPhoneNumber(){
        String phoneNumber = "10";
        Assert.assertEquals("1011",memberMapper.findByPhoneNumber(pager,phoneNumber).get(1).getPhoneNumber());
    }

    @Test
    public void testFindByEmail(){
        String email = "10";
        Assert.assertEquals("1010@qq.com",memberMapper.findByEmail(pager,email).get(0).getEmail());
    }

    @Test
    public void testFindByName(){
        String name = "张";
        pager.setCurrentPage(1);
        Assert.assertEquals("张三",memberMapper.findByName(pager,name).get(0).getName());
    }
}