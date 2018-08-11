package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.ToMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hrms")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/member")
    public Map insertOneMember(@RequestParam Member member){
        if (memberService.insert(member) > 0) {
            return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
        } else {
            return ToMap.toMap(ConstantValue.FALSE_CODE,ConstantValue.FALSE,null);
        }
    }

    @GetMapping("/member")
    public Member selectOneMember(@RequestParam String companyId,
                                  @RequestParam String num){
//        Member member = new Member(companyId,num);
        Member member = new Member();
        member.setCompanyId(companyId);
        member.setNum(num);
        return memberService.selectByPrimaryKey(member);
    }

//    @GetMapping("/say")
//    public String hello(){
//        return "hello world";
//    }



}
