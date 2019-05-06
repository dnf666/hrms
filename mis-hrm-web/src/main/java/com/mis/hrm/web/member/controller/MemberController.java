package com.mis.hrm.web.member.controller;

import com.mis.hrm.manage.model.Management;
import com.mis.hrm.manage.service.ManageService;
import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

/**
 * @author demo
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Resource
    private ManageService manageService;
    private  Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 添加成员
     * @param member 成员信息
     * @return 结果
     */
    @PostMapping("member")
    public Map insertOneMember(@RequestBody Member member) {
        Map<String, Object> map;
        try {
            memberService.insert(member);
            logger.info("添加成员成功");
            String companyId = member.getCompanyId();
            String email = member.getEmail();
            Management management = new Management();
            management.setCompanyId(companyId);
            management.setEmail(email);
           int result = manageService.insert(management);
            logger.info("添加管理成功");
            map = ToMap.toSuccessMap(result);
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
        String emails = member.getEmail();
        List<String> numList = stringToList(nums);
        List<String> emailList = stringToList(emails);
        Map<String, Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.deleteByNums(numList,emailList, companyId));
            logger.info("删除成员成功");
        } catch (InfoNotFullyException infoNotFullyException) {
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e) {
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    private List<String> stringToList(String nums) {
        String[] numArray = nums.split(",");
        return Arrays.asList(numArray);
    }

    @PutMapping("member")
    public Map updateOneMember(@RequestBody Member member) {
        Map<String, Object> map;
        try {
            map = ToMap.toSuccessMap(memberService.updateByPrimaryKey(member));
            logger.info("更改成员成功");
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
