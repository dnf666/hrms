package com.facishare.crm.checkins.action;

import com.facishare.crm.checkins.exception.CheckinsErrorCode;
import com.facishare.crm.checkins.exception.CheckinsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckinsImgAddAction extends CheckinsObjExceptionAction {
    @Override
    protected void before(Arg arg) {
        throw new CheckinsException(CheckinsErrorCode.GO_TO_630_WAIQIN.getMessage(), CheckinsErrorCode.GO_TO_630_WAIQIN);
    }
}

