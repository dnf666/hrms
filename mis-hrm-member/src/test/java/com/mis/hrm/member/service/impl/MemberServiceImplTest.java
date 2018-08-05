package com.mis.hrm.member.service.impl;


import com.mis.hrm.member.pojos.Member;
import com.mis.hrm.member.service.MemberService;
import org.junit.Assert;
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

    @Test
    public void testDeleteByPrimaryKey() {
        Member member = new Member();
        member.setCompanyId("82318");
        member.setNum("2794179");
        Assert.assertEquals(1,memberService.deleteByPrimaryKey(member));
    }

    @Test
    public void testInsert() {
        Member member = new Member("384u1091","20143215","gasgf","26542345","ojgerjqofj43","2017fa","a","vdardgvae","asfwe");
        Assert.assertEquals(1,memberService.insert(member));
    }

    @Test
    public void testSelectByPrimaryKey() {
        Member member = new Member();
        member.setCompanyId("82318");
        member.setNum("2794179");
        Member selectMember = memberService.selectByPrimaryKey(member);
        Assert.assertEquals("158158158",selectMember.getPhoneNumber());
        Assert.assertEquals("74901hudh",selectMember.getEmail());
    }

    @Test
    public void testUpdateByPrimaryKey() {
        Member member = new Member("713975120","3425","chocolate","2123123123","orange43","2017fa","a","tomato","juice");
        Assert.assertEquals(1,memberService.updateByPrimaryKey(member));
    }
}