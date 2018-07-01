package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;

import java.util.ArrayList;
import java.util.List;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by yuanjl on 2018/4/17.
 */
public class OpportunityDescribeLayoutController extends SFADescribeLayoutController {
    @Override
    protected void handleLayout(Arg arg, Result result) {
        super.handleLayout(arg, result);
        if (arg.getLayout_type() == null) {
            return;
        }
        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                //特殊处理销售订单中的客户字段(仅支持订单)--LAYOUT_TYPE_EDIT的时候需要处理

                //主对象中需要设置为只读的字段名称
                List<String> readonlyFieldNames = new ArrayList<String>();
                readonlyFieldNames.add("account_id");
                readonlyFieldNames.add("sales_process_name");

                PreDefLayoutUtil.setFormComponentFieldsReadOnly(formComponent, readonlyFieldNames);

                break;
        }
    }
}
