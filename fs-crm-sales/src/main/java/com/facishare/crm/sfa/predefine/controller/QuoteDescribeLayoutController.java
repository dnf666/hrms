package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.predefine.exception.SFAErrorCode;
import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.crm.sfa.utilities.util.VersionUtil;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.facishare.paas.common.util.UdobjConstants.*;

@Slf4j
public class QuoteDescribeLayoutController extends SFADescribeLayoutController {

    @Override
    protected void promptUpgrade(Arg arg,Result result) {
        super.promptUpgrade(arg,result);

        //低于6.2版本的终端不允许使用报价单
        if(VersionUtil.isVersionEarlierEqualThan620(controllerContext.getRequestContext())){
            throw new MetaDataBusinessException(SFAErrorCode.CLIENT_UPGRADE_PROMPT.getMessage());
        }
    }

    @Override
    protected void handelDescribe(Arg arg, Result result) {
        super.handelDescribe(arg, result);
        IObjectDescribe describe = result.getObjectDescribe().toObjectDescribe();
        setCascadeParentApiName(describe);
    }

    @Override
    protected void handleLayout(Arg arg, Result result) {
        super.handleLayout(arg, result);
        if(arg.getLayout_type() ==null){
            return;
        }
        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                PreDefLayoutUtil.setFormComponentFieldReadOnly(formComponent, Arrays.asList("account_id"));
                break;
        }
    }

    /**
     * 将价目表ID和商机ID的cascade_parent_api_name设置成account_id
     * @param describe
     */
    private void setCascadeParentApiName(IObjectDescribe describe){
        List<IFieldDescribe> fields =ObjectDescribeExt.of(describe)
                .getFieldDescribesSilently().stream().filter(field->
                field.getApiName().equals(QuoteConstants.QuoteField.PRICEBOOKID.getApiName()) ||
                        field.getApiName().equals(QuoteConstants.QuoteField.OPPORTUNITYID.getApiName()))
                .collect(Collectors.toList());

        for (IFieldDescribe field: fields) {
            field.set("cascade_parent_api_name",Arrays.asList("account_id"));
        }
    }
}
