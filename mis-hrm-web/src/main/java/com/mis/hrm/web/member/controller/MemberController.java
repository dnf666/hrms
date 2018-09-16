package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public Map deleteByNums(@RequestBody List<String> nums) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.deleteByNums(nums));
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

    @GetMapping("/count")
    public Map countMembers(){
        return ToMap.toSuccessMap(memberService.countMembers());
    }

    @GetMapping("/all")
    public Map getAllMembers(@RequestParam Integer page,
                             @RequestParam Integer size,
                             Pager<Member> pager){
        pager.setCurrentPage(page);
        pager.setPageSize(size);
        return ToMap.toSuccessMap(memberService.getAllMembers(pager));
    }

    @PostMapping("/filter")
    public Map memberFilter(@RequestBody Member member,
                      @RequestParam Integer page,
                      @RequestParam Integer size,
                      Pager<Member> pager){
        pager.setCurrentPage(page);
        pager.setPageSize(size);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.filter(pager, member));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }
}
