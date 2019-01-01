package com.mis.hrm.web.login.controller;

import com.mis.hrm.login.entity.Company;
import com.mis.hrm.login.service.imp.CompanyServiceImp;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.model.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);
    @Autowired
    private CompanyServiceImp companyService;

    /**
     * 注册
     * @param company 数据
     * @return 注册结果
     */
    @PostMapping(value = "register")
    public ResponseEntity register(@RequestBody Company company) {
        companyService.insert(company);
        LOGGER.info("register success {}",company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "register success", "");
    }

    /**
     * 登录
     * @param company id和密码
     * @return 登录结果
     */
    @PostMapping("login")
    public ResponseEntity login(@RequestBody Company company) {
        Company company1 = companyService.checkCompany(company);
        LOGGER.info("login success {}",company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "登录成功", company1);
    }

    /**
     * 更新公司的信息
     * @param company 一系列的公司信息
     * @return 更新结果
     */
    @PutMapping("company")
    public ResponseEntity updateCompany(Company company) {
        companyService.updateByPrimaryKey(company);
        LOGGER.info("update success {}",company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDescription(), "");
    }

    /**
     * 展示公司信息
     * @param company 公司id
     * @return 公司信息
     */
    @GetMapping("company")
    public ResponseEntity getCompany(Company company) {
        Company getCompany = companyService.selectByPrimaryKey(company);
        LOGGER.info("get success {}",company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", getCompany);
    }

    /**
     * 暂时没用。取得公司类型
     * @return 公司类型
     */
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
