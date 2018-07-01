package com.facishare.crm.checkins.action;

import com.facishare.crm.checkins.exception.CheckinsErrorCode;
import com.facishare.crm.checkins.exception.CheckinsException;
import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckinsImgBulkDeleteDataAction extends StandardBulkDeleteAction {
    @Override
    protected void before(Arg arg) {
        throw new CheckinsException(CheckinsErrorCode.GO_TO_WAIQIN.getMessage(),CheckinsErrorCode.GO_TO_WAIQIN);
    }
}

