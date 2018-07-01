package com.facishare.crm.sfainterceptor.predefine.service.impl;

import com.facishare.crm.deliverynote.predefine.manager.ObjectDescribeManager;
import com.facishare.crm.sfainterceptor.predefine.service.TestFieldService;
import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.TestModel;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author chenzengyong
 * @date on 2018/1/15.
 */
@Service
@Slf4j
public class TestFieldServiceImpl implements TestFieldService {

    @Resource
    private ObjectDescribeManager objectDescribeManager;

    @Override
    public CommonModel.Result test(ServiceContext context, TestModel.Arg a) {


//        objectDescribeManager.addField(context.getUser());

        return new CommonModel.Result();
    }
}
