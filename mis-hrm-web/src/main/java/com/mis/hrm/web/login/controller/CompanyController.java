package com.mis.hrm.web.login.controller;

import com.mis.hrm.login.entity.Company;
import com.mis.hrm.login.service.imp.CompanyServiceImp;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.model.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author May
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("company")
public class CompanyController {

    @Autowired
    private CompanyServiceImp companyService;

    @PostMapping(value = "register")
    public ResponseEntity register(@RequestBody Company company) {
        companyService.insert(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "register success", "");
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody Company company) {
        Company company1 = companyService.checkCompany(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "登录成功", company1);
    }

    @PutMapping("company")
    public ResponseEntity updateCompany(Company company) {
        companyService.updateByPrimaryKey(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDescription(), "");
    }

    @GetMapping("company")
    public ResponseEntity getCompany(Company company) {
        Company getCompany = companyService.selectByPrimaryKey(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", getCompany);
    }
    @GetMapping("type")
    public ResponseEntity getType() {
     List<String> list = companyService.getMajorType();
        Map<String,List<String>> map = new HashMap<>(16);
        for (int i = 0; i < list.size(); i++) {
            String majorType = list.get(i);
            List<String> viceTypeList = companyService.getViceType(majorType);
            map.put(majorType,viceTypeList);
        }
     return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(),"获取类型成功",map);
    }
}
