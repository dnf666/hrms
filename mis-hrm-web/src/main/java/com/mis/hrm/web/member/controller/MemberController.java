package com.mis.hrm.web.member.controller;

import com.alibaba.fastjson.JSONArray;
import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.work.model.Whereabout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    @PostMapping("delMember")
    public Map deleteByNums(@RequestBody Member member, String companyId) {
        if (member.getNum() == null || member.getNum().length() ==0){
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK);
        }
        String nums = member.getNum();
        String[] numArray = nums.split(",");
        Map<String, Object> map;
        List<String> numList = new ArrayList<>();
        for (String b : numArray){
                numList.add(b);
        }
        try {
            map = ToMap.toSuccessMap(memberService.deleteByNums(numList, companyId));
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

    @PostMapping("/filter")
    public Map memberFilter(@RequestBody Member member,
                            int page,
                            int size
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

    @PostMapping("exit")
    public Map exitToWhere(@RequestBody Whereabout whereabout) {
        Map<String, Object> map;
        try {
            int result = memberService.exitToWhere(whereabout);
            map = ToMap.toSuccessMap(result);
        } catch (InfoNotFullyException infoNotFullyException) {
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }
    @PostMapping("Excel")
    public Map importMemberFromExcel(MultipartFile file){
        try {
            memberService.importMemberFromExcel(file);
        }catch(IOException e){
            return null;
        }
        return null;
    }
}
