package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.utilities.constant.CasesConstants;
import com.facishare.paas.metadata.api.IObjectData;
import joptsimple.internal.Strings;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by luxin on 2018/5/10.
 */
@Service
public class CasesObjSearchService extends AbstractPredefinedObjSearchService {

    @Override
    protected String getSearchSql(String tenantId, String name, String accountId, boolean isFuzzySearch) {
        return null;
    }

    @Override
    protected String getNamesAccurateSearchSql(String tenantId, Set<String> names) {
        String casesSearchStr = "";
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            casesSearchStr += Strings.surround(iterator.next(), '\'', '\'');
            if (iterator.hasNext()) {
                casesSearchStr += ",";
            }
        }
        return "select name,account_id, contact_id,sales_order_id from "
                + "cases"
                + String.format(" where tenant_id = '%s'", tenantId)
                + " and name "
                + String.format(" in(%s)", casesSearchStr)
                + " and is_deleted=0";
    }

    @Override
    protected String findByIdsSql(String tenantId, Set<String> objectIds) {
        return null;
    }

    @Override
    public String getApiName() {
        return Utils.CASES_API_NAME;
    }
}
