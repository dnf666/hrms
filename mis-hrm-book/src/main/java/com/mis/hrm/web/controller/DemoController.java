package com.mis.hrm.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * created by dailf on 2018/7/6
 *
 * @author dailf
 */
@Controller
@Slf4j
@RequestMapping("book")
public class DemoController {

    @RequestMapping(value = "book",method = RequestMethod.POST)
    @ResponseBody
    public String saveDemo(){
        log.error("成功");
        return "index";
    }

}
