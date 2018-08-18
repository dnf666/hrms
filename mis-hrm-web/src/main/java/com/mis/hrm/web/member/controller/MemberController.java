package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public Map insertOneMember(@RequestBody Member member) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.insert(member));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @DeleteMapping
    public Map deleteOneMember(@RequestBody Member member) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.deleteByPrimaryKey(member));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @PutMapping
    public Map updateOneMember(@RequestBody Member member) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.updateByPrimaryKey(member));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping
    public Map selectOneMember(@RequestParam String companyId,
                                  @RequestParam String num) {
        Member member = new Member(companyId,num);
        member.setNum(num);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.selectByPrimaryKey(member));
        } catch (NullPointerException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping("/count")
    public Map countMembers(){
        return ToMap.toSuccessMap(memberService.countMembers());
    }

    @GetMapping("/all/{page}")
    public Map getAllMembers(@PathVariable Integer page,
                             Pager<Member> pager){
        pager.setCurrentPage(page);
        return ToMap.toSuccessMap(memberService.getAllMembers(pager));
    }

    @GetMapping("/byPhone/{page}")
    public Map findByPhoneNumber(@RequestParam String phoneNumber,
                                 @PathVariable Integer page,
                                 Pager<Member> pager){
        pager.setCurrentPage(page);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.findByPhoneNumber(pager, phoneNumber));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }

    @GetMapping("/byEmail/{page}")
    public Map findByEmail(@RequestParam String email,
                                 @PathVariable Integer page,
                                 Pager<Member> pager){
        pager.setCurrentPage(page);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.findByEmail(pager, email));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }

    @GetMapping("/byName/{page}")
    public Map findByName(@RequestParam String name,
                                 @PathVariable Integer page,
                                 Pager<Member> pager){
        pager.setCurrentPage(page);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.findByName(pager, name));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }
}
