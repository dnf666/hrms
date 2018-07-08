package com.mis.hrm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * created by dailf on 2018/7/6
 *
 * @author dailf
 */
@Controller
@Slf4j
public class DemoController {

    @RequestMapping(value = "book",method = RequestMethod.POST)
    public void saveDemo(){


    }

}
