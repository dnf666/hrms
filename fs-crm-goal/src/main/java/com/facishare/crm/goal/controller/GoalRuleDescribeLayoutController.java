package com.facishare.crm.goal.controller;

import com.facishare.crm.goal.constant.GoalRuleObj;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.describe.ArrayFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOption;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GoalRuleDescribeLayoutController extends StandardDescribeLayoutController {

    @Override
    protected Result after(Arg arg, Result result) {
        super.after(arg, result);

        setCountFiscalYearOptions(result.getObjectDescribe().toObjectDescribe());

        return result;
    }

    private void setCountFiscalYearOptions(IObjectDescribe objectDescribe){
        IFieldDescribe fieldDescribe = objectDescribe.getFieldDescribe(GoalRuleObj.COUNT_FISCAL_YEAR);
        fieldDescribe.set(SelectOneFieldDescribe .OPTIONS,getFiscalYearOptions());
    }

    private List<Map<String,String>> getFiscalYearOptions(){
        List<Map<String,String>> optionList = Lists.newArrayList();

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        for (int year = currentYear-1 ; year<= currentYear + 1; year++){
            Map<String,String> option = Maps.newHashMap();
            option.put(ISelectOption.OPTION_VALUE,String.valueOf(year) );
            option.put(ISelectOption.OPTION_LABEL,String.valueOf(year) );
            optionList.add(option);
        }

        return optionList;
    }
}
