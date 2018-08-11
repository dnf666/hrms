package com.mis.hrm.login.controller;

import com.mis.hrm.login.entity.Company;
import com.mis.hrm.login.entity.ResponseEntity;
import com.mis.hrm.login.service.CompanyService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.mis.hrm.util.validation.ValidationUtil.checkBindingResult;

/**
 * @author May
 */
@Api(value = "company",description = "Login 板块接口文档")
@RestController
@RequestMapping("company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    @ApiOperation(value = "注册公司")
    public ResponseEntity register(@Validated @ApiParam(value = "公司信息", required = true)
                                               Company company, BindingResult result){
        checkBindingResult(result);
        companyService.insert(company);
        return new ResponseEntity<>(200, null, null);
    }

    @ApiOperation(value = "登陆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "body"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "body")
    })
    @GetMapping("login")
    public ResponseEntity login(Company company){
        return new ResponseEntity<>(200, null, null);
    }

    @ApiOperation(value = "修改公司信息")
    @PutMapping("updateCompany")
    public ResponseEntity updateCompany(@ApiParam(value = "公司信息", required = true)
                                                    Company company){
        companyService.updateByPrimaryKey(company);
        return new ResponseEntity<>(200, null, null);
    }

    @ApiOperation(value = "删除公司")
    @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "body")
    @DeleteMapping("deleteCompany")
    public ResponseEntity deleteCompany(Company company){
        companyService.deleteByPrimaryKey(company);
        return new ResponseEntity<>(200, null, null);
    }

    @ApiOperation(value = "获取公司信息")
    @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "query")
    @GetMapping("getCompany")
    public ResponseEntity getCompany(Company company){
        Company getCompany = companyService.selectByPrimaryKey(company);
        return new ResponseEntity<>(200, null, getCompany);
    }
}
