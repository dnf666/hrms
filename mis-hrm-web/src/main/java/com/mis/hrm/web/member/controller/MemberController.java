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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("member")
    public Map insertOneMember(@RequestBody Member member) {
        Map<String, Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.insert(member));
        } catch (InfoNotFullyException infoNotFullyException) {
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e) {
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @DeleteMapping("member")
    public Map deleteByNums(@RequestBody List<String> nums,String companyId) {
        Map<String, Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.deleteByNums(nums,companyId));
        } catch (InfoNotFullyException infoNotFullyException) {
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e) {
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @PutMapping("member")
    public Map updateOneMember(@RequestBody Member member) {
        Map<String, Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.updateByPrimaryKey(member));
        } catch (InfoNotFullyException infoNotFullyException) {
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e) {
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping("/count")
    public Map countMembers(Member member) {
        return ToMap.toSuccessMap(memberService.countMembers(member));
    }

//    @GetMapping("/all")
//    public Map getAllMembers(@RequestParam Integer page,
//                             @RequestParam Integer size,
//                             Pager<Member> pager) {
//        pager.setCurrentPage(page);
//        pager.setPageSize(size);
//        return ToMap.toSuccessMap(memberService.getAllMembers(pager));
//    }

    @PostMapping("/filter")
    public Map memberFilter(@RequestBody Member member,
                            @RequestParam Integer page,
                            @RequestParam Integer size
    ) {
        Pager<Member> pager = new Pager<>();
        pager.setCurrentPage(page);
        pager.setPageSize(size);
        Map<String, Object> map;
        List<Member> list = memberService.filter(pager, member);
        pager.setData(list);
        try {
            map = ToMap.toSuccessMap(pager);
        } catch (InfoNotFullyException infoNotFullyException) {
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }
}
