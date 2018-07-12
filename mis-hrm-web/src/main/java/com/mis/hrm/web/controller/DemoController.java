package com.mis.hrm.web.controller;

import com.mis.hrm.book.service.DemoService;
import com.mis.hrm.index.service.Demo1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * created by dailf on 2018/7/12
 *
 * @author dailf
 */
    @Controller
    @Slf4j
    @RequestMapping("/book")
    public class DemoController {
        @Resource
        private Demo1Service demo1Service;
        @RequestMapping(value = "/book1",method = RequestMethod.POST)
        @ResponseBody
        public String saveDemo(){
            demo1Service.saveDemo();
            log.error("成功");
            return "index";
        }
}
