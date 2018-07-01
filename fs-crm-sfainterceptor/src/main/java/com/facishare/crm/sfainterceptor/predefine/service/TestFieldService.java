package com.facishare.crm.sfainterceptor.predefine.service;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.TestModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * @author chenzengyong
 * @date on 2018/1/15.
 */
@ServiceModule("TestField")
public interface TestFieldService {



    @ServiceMethod("test")
    public CommonModel.Result test(ServiceContext context, TestModel.Arg arg);


}
