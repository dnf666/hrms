package com.facishare.crm.sfa.predefine.controller;

public class PriceBookProductRelatedController extends SFARelatedController {
    
    @Override
    protected Result doService(Arg arg) {
        // TODO: 2018/3/12 这个参数让终端传,不能这样写死
        arg.setIsIncludeLayout(Boolean.TRUE);
        return super.doService(arg);
    }
}
