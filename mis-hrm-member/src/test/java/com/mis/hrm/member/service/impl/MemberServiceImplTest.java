package com.mis.hrm.member.service.impl;


import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.exception.InfoNotFullyException;
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
public class MemberServiceImplTest {
    @Autowired
    private MemberService memberService;

    private Member member;
    private Member blankMember;

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

        blankMember = Member.builder().build();

        pager = new Pager<>();
        pager.setPageSize(2);
        pager.setCurrentPage(2);
    }

    @Test(expected = RuntimeException.class)
//    @Ignore
    public void testDeleteByPrimaryKey() {
        member.setNum("2017210001");
        Assert.assertEquals(1,memberService.deleteByPrimaryKey(member));

        member.setNum("2017210004");
        Assert.assertEquals(0,memberService.deleteByPrimaryKey(member));

        Assert.assertEquals(0,memberService.deleteByPrimaryKey(blankMember));
    }

    @Test(expected = RuntimeException.class)
//    @Ignore
    public void testInsert() {
        Assert.assertEquals(1,memberService.insert(member));

        Assert.assertEquals(0,memberService.insert(blankMember));
    }

    @Test(expected = NullPointerException.class)
    public void testSelectByPrimaryKey() {
        member.setNum("2017210003");
        Member selectedMember = memberService.selectByPrimaryKey(member);
        Assert.assertEquals("王二",selectedMember.getName());
        Assert.assertEquals("1010",selectedMember.getPhoneNumber());

        member.setNum("2017210004");
        Assert.assertEquals(null,memberService.selectByPrimaryKey(member));

        Assert.assertEquals(null,memberService.selectByPrimaryKey(blankMember));
    }

    @Test(expected = RuntimeException.class)
//    @Ignore
    public void testUpdateByPrimaryKey() {
        member.setNum("2017210002");
        Assert.assertEquals(1,memberService.updateByPrimaryKey(member));

        member.setNum("2017210004");
        Assert.assertEquals(0,memberService.updateByPrimaryKey(member));

        Assert.assertEquals(0,memberService.updateByPrimaryKey(blankMember));
    }

    @Test
    public void testCountMembers(){
        Assert.assertEquals((Long)(long)5,memberService.countMembers());
    }

    @Test
    public void testGetAllMembers(){
        Assert.assertEquals(2,memberService.getAllMembers(pager).size());
    }

    @Test(expected = InfoNotFullyException.class)
    public void testFindByPhoneNumber(){
        String phoneNumber = "10";
        Assert.assertEquals("1011",memberService.findByPhoneNumber(pager,phoneNumber).get(1).getPhoneNumber());

        phoneNumber = "";
        Assert.assertEquals(null,memberService.findByPhoneNumber(pager,phoneNumber));
    }

    @Test(expected = InfoNotFullyException.class)
    public void testFindByEmail(){
        String email = "10";
        Assert.assertEquals("1010@qq.com",memberService.findByEmail(pager,email).get(0).getEmail());

        email = "";
        Assert.assertEquals(null,memberService.findByPhoneNumber(pager,email));
    }

    @Test(expected = InfoNotFullyException.class)
    public void testFindByName(){
        String name = "张";
        pager.setCurrentPage(1);
        Assert.assertEquals("张三",memberService.findByName(pager,name).get(0).getName());

        name = "";
        Assert.assertEquals(null,memberService.findByPhoneNumber(pager,name));
    }
}