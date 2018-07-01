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
public class AccountObjSearchService extends AbstractPredefinedObjSearchService {

    @Override
    public String getApiName() {
        return Utils.ACCOUNT_API_NAME;
    }


    @Override
    protected String getSearchSql(String tenantId, String name, String accountId, boolean isFuzzySearch) {
        if (StringUtils.isBlank(accountId)) {
            return "select customer_id as _id,name from "
                    + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName())
                    + String.format(" where ei = '%s'", tenantId)
                    + " and name"
                    + String.format(getSearchConnectKey(isFuzzySearch), name)
                    + " and is_deleted=FALSE"
                    + " and status<>99 "
                    + getLimitCondition(isFuzzySearch);
        } else {
            return "select customer_id as _id,name from customer "
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
        String accountSearchStr = "";
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            accountSearchStr += Strings.surround(iterator.next(), '\'', '\'');
            if (iterator.hasNext()) {
                accountSearchStr += ",";
            }
        }
        return "select customer_id as account_id,name from "
                + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName())
                + String.format(" where ei = '%s'", tenantId)
                + " and name"
                + String.format(" in(%s)", accountSearchStr)
                + " and is_deleted=FALSE"
                + " and status<>99 ";
    }

    @Override
    protected String findByIdsSql(String tenantId, Set<String> objectIds) {
        String accountSearchStr = "";
        Iterator<String> iterator = objectIds.iterator();
        while (iterator.hasNext()) {
            accountSearchStr += Strings.surround(iterator.next(), '\'', '\'');
            if (iterator.hasNext()) {
                accountSearchStr += ",";
            }
        }
        return "select customer_id as account_id,name from "
                + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName())
                + String.format(" where ei = '%s'", tenantId)
                + " and customer_id"
                + String.format(" in(%s)", accountSearchStr)
                + " and is_deleted=FALSE"
                + " and status<>99 ";
    }


}
