package com.mis.hrm.web.manage.controller;

import com.mis.hrm.manage.model.Management;
import com.mis.hrm.manage.service.ManageService;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.model.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * created on 2019-02-28
 *
 * @author dailinfu
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/manage")
public class ManageController {
    @Resource
    private ManageService manageService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 添加成员
     *
     * @param manage 成员信息
     * @return 结果
     */
    @PostMapping("manage")
    public ResponseEntity insertOneManage(@RequestBody Management manage) {
        int result = manageService.insert(manage);
        logger.info("insert permission success");
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDescription(), result);

    }

    @PutMapping("manage")
    public ResponseEntity updatePasswordOrPermission(Management management) {
        int result = manageService.updateByPrimaryKey(management);
        logger.info("update success");
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDescription(), result);

    }

    @GetMapping("manage")
    public ResponseEntity findManagementByCompanyIdAndEmail(Management management) {
        Management management1 = manageService.selectByPrimaryKey(management);
        logger.info("find success");
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDescription(), management1);
    }

    @PostMapping("permission")
    public ResponseEntity setPermission(@RequestBody Management management) {
        int result = manageService.updateByPrimaryKey(management);
        logger.info("update success");
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getDescription(), result);

    }
}
