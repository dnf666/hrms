package com.mis.hrm.web.login.controller;

import com.mis.hrm.login.entity.Company;
import com.mis.hrm.login.service.imp.CompanyServiceImp;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.model.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.mis.hrm.util.validation.ValidationUtil.checkBindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author May
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("company")
public class CompanyController {

    @Autowired
    private CompanyServiceImp companyService;

    /**
     * @api {POST} /company 注册公司
     * @apiDescription 注册公司
     * @apiGroup Company
     * @apiParam (Company) {String} email 邮箱
     * @apiParam (Company) {String} name 公司名称
     * @apiParam (Company) {String} applicantName 申请人姓名
     * @apiParam (Company) {String} organizationSize 组织规模
     * @apiParam (Company) {String} mainCategory 主分类
     * @apiParam (Company) {String} viceCategory 副分类
     * @apiParam (Company) {String} password 密码
     *
     */
    @PostMapping(value = "register")
    public ResponseEntity register(@Valid Company company, BindingResult result) {
        checkBindingResult(result);
        companyService.insert(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", "");
    }

    /**
     * @api {GET} /company/login.do 登陆公司
     * @apiDescription 登陆公司
     * @apiGroup Company
     * @apiParam (Company) {String} email 邮箱
     * @apiParam (Company) {String} password 密码
     *
     */
    @PostMapping("login")
    public ResponseEntity login(Company company) {
        companyService.checkCompany(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", "");
    }

    /**
     * @api {PUT} /company/updateCompany.do 修改公司信息
     * @apiDescription 修改公司信息
     * @apiGroup Company
     * @apiParam (Company) {String} email 邮箱
     * @apiParam (Company) {String} name 公司名称
     * @apiParam (Company) {String} applicantName 申请人姓名
     * @apiParam (Company) {String} organizationSize 组织规模
     * @apiParam (Company) {String} mainCategory 主分类
     * @apiParam (Company) {String} viceCategory 副分类
     * @apiParam (Company) {String} password 密码
     *
     */
    @PutMapping("company")
    public ResponseEntity updateCompany(Company company) {
        companyService.updateByPrimaryKey(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", "");
    }

    /**
     * @api {DELETE} /company/deleteCompany.do 删除公司
     * @apiDescription 删除公司
     * @apiGroup Company
     * @apiParam (Company) {String} email 邮箱
     *
     */
    @DeleteMapping("company")
    public ResponseEntity deleteCompany(Company company) {
        companyService.deleteByPrimaryKey(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", "");
    }

    /**
     * @api {GET} /company/getCompany.do 通过邮箱得到指定公司信息
     * @apiDescription 通过邮箱得到指定公司信息
     * @apiGroup Company
     * @apiParam (Company) {String} email 邮箱
     * @apiSuccessExample Success-Response:
     *       HTTP/1.1 200 OK
     *       {
     *         "statu": "200",
     *         "msg": ""
     *         "object": {
     *             "email":"string",
     *             "name":"公司名称",
     *             "applicantName":"申请人姓名",
     *             "organizationSize":"组织规模",
     *             "mainCategory";"主分类",
     *             "viceCategory":"副分类"
     *         }
     *       }
     *
     */
    @GetMapping("company")
    public ResponseEntity getCompany(Company company) {
        Company getCompany = companyService.selectByPrimaryKey(company);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", getCompany);
    }
    @GetMapping("type")
    public ResponseEntity getType() {
     List<String> list = companyService.getMajorType();
        Map<String,List<String>> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            String majorType = list.get(i);
            List<String> viceTypeList = companyService.getViceType(majorType);
            map.put(majorType,viceTypeList);
        }
     return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(),"获取类型成功",map);
    }

}
