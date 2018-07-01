package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.util.PriceBookSpecImpCheckGrayTenantUtil;
import com.facishare.paas.appframework.core.predef.action.StandardInsertImportVerifyAction;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;

import java.util.List;
import java.util.Optional;

/**
 * @author cqx
 * @date 2018/5/18 15:39
 */
public class PriceBookInsertImportVerifyAction extends StandardInsertImportVerifyAction {

    @Override
    protected List<IFieldDescribe> getValidImportFields() {
        List<IFieldDescribe> fieldDescribes = super.getValidImportFields();

        if (PriceBookSpecImpCheckGrayTenantUtil.checkGrayTenant(actionContext.getTenantId())) {

            Optional<IFieldDescribe> accountRangeField = Optional
                    .of(ObjectDescribeExt.of(objectDescribe).getFieldDescribe("account_range"));

            accountRangeField.ifPresent(x -> fieldDescribes.add(0, x));

            fieldDescribes.removeIf(o -> "record_type".equals(o.getApiName())
                    || "is_standard".equals(o.getApiName())
                    || "extend_obj_data_id".equals(o.getApiName()));
        } else {
            fieldDescribes.removeIf(o ->
                    "account_range".equals(o.getApiName())
                            || "record_type".equals(o.getApiName())
                            || "is_standard".equals(o.getApiName())
                            || "extend_obj_data_id".equals(o.getApiName()));
        }
        return fieldDescribes;
    }

}
