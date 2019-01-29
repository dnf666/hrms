package com.mis.hrm.web.member.controller;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author demo
 */
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
        if (member.getNum() == null || member.getNum().length() == 0) {
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK);
        }
        String nums = member.getNum();
        String[] numArray = nums.split(",");
        Map<String, Object> map;
        List<String> numList = Arrays.asList(numArray);
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
    @GetMapping("member")
    public Map getAllMember(Member member){
        return ToMap.toSuccessMap(memberService.selectByCompanyId(member));
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

    @PostMapping("excel")
    public Map importMemberFromExcel(@RequestParam("file") MultipartFile file,@RequestParam("companyId") String companyId) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            try {
                memberService.importMemberFromExcel(file, companyId);
            } catch (IOException e) {
               return ToMap.toFalseMap("io异常");
            }
        }else {
            return ToMap.toFalseMap("文件格式不匹配");
        }
        return ToMap.toSuccessMap(null);
    }

    @PostMapping("/createExcel")
    public ResponseEntity<byte[]> createExcel(@RequestBody Member member) {
        List<Member> lists = memberService.selectByMultiKey(member);
       HSSFWorkbook workbook = memberService.exportExcel(lists);
        byte[] bytes = workbook.getBytes();
        HttpHeaders header = new HttpHeaders();
        header.setContentDispositionFormData("attachment", "member.xls");
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(bytes, header, HttpStatus.CREATED);

    }
}
