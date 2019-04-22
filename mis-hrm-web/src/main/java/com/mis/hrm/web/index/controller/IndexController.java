package com.mis.hrm.web.index.controller;

import com.mis.hrm.index.entity.Index;
import com.mis.hrm.index.service.impl.IndexServiceImpl;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.model.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author May
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("index")
public class IndexController {

    private static final int CHARACTER_COUNT_LIMIT = 100;
    @Autowired
    private IndexServiceImpl indexServiceImpl;

    /**
     * @api {GET} /index/getIndex.do 获取主页信息
     * @apiDescription 通过公司email获取其主页需要展示的内容
     * @apiGroup Index
     * @apiParam (Index) {String} companyId 公司邮箱
     * @apiSuccessExample Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "statu": "200",
     * "msg": ""
     * "object": {
     * "companyId":"string",
     * "outline":"公司简介"
     * "photoPath":"公司头像地址"
     * }
     * }
     */
    @GetMapping("index")
    public ResponseEntity getIndex(Index index) {
        Index selectIndex = indexServiceImpl.selectByPrimaryKey(index);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", selectIndex);
    }

    @PutMapping("index")
    public ResponseEntity updateIndex(Index index) {
        indexServiceImpl.updateByPrimaryKey(index);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "修改成功", "");

    }

    /**
     * @api {Patch} /index/updateOutline.do 修改公司简介
     * @apiDescription 修改公司简介
     * @apiGroup Index
     * @apiParam (Index) {String} companyId 邮箱
     * @apiParam (Index) {String} outline 公司简介
     */

    @PostMapping("outline")
    public ResponseEntity updateOutline(@RequestBody Index index) {
        if (index.getOutline().length() > CHARACTER_COUNT_LIMIT) {
            return new ResponseEntity<>(ErrorCode.HOT_LENGTH_LIMITED.getCode(), ErrorCode.HOT_LENGTH_LIMITED.getDescription(), "");
        }
        indexServiceImpl.updateByPrimaryKey(index);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "update outline success", "");
    }

    /**
     * @api {PUT} /company/updatePhoto.do 修改公司头像
     * @apiDescription 修改公司头像
     * @apiGroup Index
     * @apiParam (Index) {String} companyId 公司邮箱
     */

    @PostMapping("photo")
    public ResponseEntity updatePhoto(Index index, @RequestParam("file") MultipartFile file,
                                      HttpServletRequest request) throws IOException {
        indexServiceImpl.updatePhoto(index, file, request);
        return new ResponseEntity<>(ErrorCode.SUCCESS.getCode(), "", "");
    }
}
