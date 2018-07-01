package com.facishare.crm.sfa.predefine.action;

import com.facishare.paas.appframework.core.predef.action.StandardExportAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cqx
 * @date 2018/5/23 17:03
 */
public class PriceBookExportAction extends StandardExportAction {

    @Override
    protected Map<String, List<IFieldDescribe>> findFieldMap(Map<String, List<IObjectData>> dataMap, String recordType) {
        Map<String, List<IFieldDescribe>> fieldMap = new HashMap();
        Iterator var4 = dataMap.keySet().iterator();

        while (var4.hasNext()) {
            String describeApiName = (String) var4.next();
            List<IFieldDescribe> fields = this.findFields(describeApiName, recordType);
            fields.removeIf(x -> "account_range".equals(x.getApiName()));
            fieldMap.put(describeApiName, fields);
        }

        return fieldMap;
    }
}
