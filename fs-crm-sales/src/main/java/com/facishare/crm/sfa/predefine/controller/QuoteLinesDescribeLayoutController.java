package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.predefine.exception.SFAErrorCode;
import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.crm.sfa.utilities.util.VersionUtil;
import com.facishare.crm.sfa.utilities.validator.QuoteValidator;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_ADD;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

@Slf4j
public class QuoteLinesDescribeLayoutController extends SFADescribeLayoutController {

    @Override
    protected void promptUpgrade(Arg arg,Result result) {
        super.promptUpgrade(arg,result);
        //低于6.2版本的终端不允许使用报价单
        if(VersionUtil.isVersionEarlierEqualThan620(controllerContext.getRequestContext())){
            throw new MetaDataBusinessException(SFAErrorCode.CLIENT_UPGRADE_PROMPT.getMessage());
        }
    }

    @Override
    protected void handleLayout(Arg arg, Result result) {
        super.handleLayout(arg, result);
        if(arg.getLayout_type() ==null){
            return;
        }
        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
            case LAYOUT_TYPE_ADD:
                IObjectDescribe describe = result.getObjectDescribe().toObjectDescribe();
                if(QuoteValidator.enablePriceBook(describe)){
                    PreDefLayoutUtil.setFormComponentFieldReadOnly(formComponent,Arrays.asList("product_id"));
                }
                break;
        }
    }
}
