package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.utilities.constant.CasesConstants;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import joptsimple.internal.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by luxin on 2018/5/2.
 */
@Service
public class ContactObjSearchService extends AbstractPredefinedObjSearchService {
    @Override
    public String getApiName() {
        return Utils.CONTACT_API_NAME;
    }


    @Override
    protected String getSearchSql(String tenantId, String name, String accountId, boolean isFuzzySearch) {
        if (StringUtils.isBlank(accountId)) {
            return null;
        } else {
            return "select contact_id as _id,name from contact "
                    + String.format(" where ei = '%s'", tenantId)
                    + " and name "
                    + String.format(getSearchConnectKey(isFuzzySearch), name)
                    + String.format(" and customer_id = '%s'", accountId)
                    + " and is_deleted=FALSE "
                    + " and status<>99 "
                    + getLimitCondition(isFuzzySearch);

        }
    }

    @Override
    protected String getNamesAccurateSearchSql(String tenantId, Set<String> names) {
        String contactSearchStr = "";
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            contactSearchStr += Strings.surround(iterator.next(), '\'', '\'');
            if (iterator.hasNext()) {
                contactSearchStr += ",";
            }
        }
        return "select contact_id ,name, customer_id as account_id from  "
                + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName())
                + String.format(" where ei = '%s'", tenantId)
                + " and name "
                + String.format(" in(%s)", contactSearchStr)
                + " and is_deleted=FALSE "
                + " and status<>99 ";
    }

    @Override
    protected String findByIdsSql(String tenantId, Set<String> objectIds) {
        String contactSearchStr = "";
        Iterator<String> iterator = objectIds.iterator();
        while (iterator.hasNext()) {
            contactSearchStr += Strings.surround(iterator.next(), '\'', '\'');
            if (iterator.hasNext()) {
                contactSearchStr += ",";
            }
        }
        return "select contact_id ,name, customer_id as account_id from  "
                + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName())
                + String.format(" where ei = '%s'", tenantId)
                + " and contact_id "
                + String.format(" in(%s)", contactSearchStr)
                + " and is_deleted=FALSE "
                + " and status<>99 ";
    }


}
