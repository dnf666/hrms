package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hrms")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/member")
    public Map insertOneMember(@RequestBody Member member) throws InfoNotFullyExpection {
        if (memberService.insert(member) > 0) {
            return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
        } else {
            return ToMap.toMap(ConstantValue.FALSE_CODE,ConstantValue.FALSE,null);
        }
    }

    @DeleteMapping("/member")
    public Map deleteOneMember(@RequestBody Member member) throws InfoNotFullyExpection {
        if(memberService.deleteByPrimaryKey(member) > 0){
            return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
        } else {
            return ToMap.toMap(ConstantValue.FALSE_CODE,ConstantValue.FALSE,null);
        }
    }

    @PutMapping("/member")
    public Map updateOneMember(@RequestBody Member member) throws InfoNotFullyExpection {
        if (memberService.updateByPrimaryKey(member) > 0) {
            return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
        } else {
            return ToMap.toMap(ConstantValue.FALSE_CODE,ConstantValue.FALSE,null);
        }
    }

    @GetMapping("/member")
    public Map selectOneMember(@RequestParam String companyId,
                                  @RequestParam String num) throws InfoNotFullyExpection {
        Member member = new Member(companyId,num);
        member.setNum(num);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,memberService.selectByPrimaryKey(member));
    }

    @GetMapping("/member/count")
    public Map countMembers(){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,memberService.countMembers());
    }

    @GetMapping("/member/all/{page}")
    public Map getAllMembers(@PathVariable Integer page,
                             Pager<Member> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,memberService.getAllMembers(pager));
    }

    @GetMapping("/member/byPhone/{page}")
    public Map findByPhoneNumber(@RequestParam String phoneNumber,
                                 @PathVariable Integer page,
                                 Pager<Member> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,memberService.findByPhoneNumber(pager,phoneNumber));
    }

    @GetMapping("/member/byEmail/{page}")
    public Map findByEmail(@RequestParam String email,
                                 @PathVariable Integer page,
                                 Pager<Member> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,memberService.findByEmail(pager,email));
    }

    @GetMapping("/member/byName/{page}")
    public Map findByName(@RequestParam String name,
                                 @PathVariable Integer page,
                                 Pager<Member> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,memberService.findByName(pager,name));
    }

}
