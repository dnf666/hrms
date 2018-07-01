package com.facishare.crm.checkins.action;

import java.util.List;

import com.facishare.crm.checkins.exception.CheckinsErrorCode;
import com.facishare.crm.checkins.exception.CheckinsException;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckinsObjExceptionAction extends BaseObjectSaveAction {

    @Override
    protected String getIRule() {
        return null;
    }

    @Override
    protected ObjectAction getObjectAction() {
        return null;
    }

    @Override
    protected List<String> getFuncPrivilegeCodes() {
        return null;
    }

    @Override
    protected List<String> getDataPrivilegeIds(Arg arg) {
        return null;
    }

    @Override
    protected Result doAct(Arg arg) {
        return null;
    }

    @Override
    protected void before(Arg arg) {
        throw new CheckinsException(CheckinsErrorCode.GO_TO_WAIQIN.getMessage(), CheckinsErrorCode.GO_TO_WAIQIN);
        //        super.before(arg);
    }

    @Override
    protected String getButtonApiName() {
        return null;
    }

}
