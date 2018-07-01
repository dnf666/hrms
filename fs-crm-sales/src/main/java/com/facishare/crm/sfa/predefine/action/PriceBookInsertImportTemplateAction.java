package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.util.PriceBookSpecImpCheckGrayTenantUtil;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportTemplateAction;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by luxin on 2018/4/2.
 */
public class PriceBookInsertImportTemplateAction extends StandardInsertImportTemplateAction {

    @Override
    protected Result doAct(Arg arg) {
        //根据权限获取人员可以查看的字段列表
        return super.doAct(arg);
    }

    @Override
    protected List<IFieldDescribe> sortHeader(List<IFieldDescribe> validFieldList) {
        String tenantId = actionContext.getTenantId();

        List<IFieldDescribe> fieldDescribes = super.sortHeader(validFieldList);
        if (PriceBookSpecImpCheckGrayTenantUtil.checkGrayTenant(tenantId)) {
            Optional<IFieldDescribe> accountRangeField = Optional
                    .of(ObjectDescribeExt.of(objectDescribe).getFieldDescribe("account_range"));
            accountRangeField.ifPresent(x -> fieldDescribes.add(0,x));
        }

        return fieldDescribes;
    }

    @Override
    protected List<List<String>> customSampleList(List<List<String>> sampleList) {
        List<List<String>> sampleListResult = super.customSampleList(sampleList);

        String tenantId = actionContext.getTenantId();
        if (PriceBookSpecImpCheckGrayTenantUtil.checkGrayTenant(tenantId)) {
            sampleListResult.stream().findFirst().ifPresent(x -> x.set(0, "示例客户1；示例客户2"));
        }
        return sampleListResult;
    }

}
