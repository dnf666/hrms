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
public class SalesOrderObjSearchService extends AbstractPredefinedObjSearchService {

    @Override
    public String getApiName() {
        return Utils.SALES_ORDER_API_NAME;
    }

    @Override
    protected String getSearchSql(String tenantId, String name, String accountId, boolean isFuzzySearch) {


        if (StringUtils.isBlank(accountId)) {
            return "select customer_trade_id as _id, trade_code as name ,customer_trade.customer_id as account_id,customer.name as account_id__r from "
                    + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName()) + ","
                    + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(Utils.ACCOUNT_API_NAME)
                    + String.format(" where customer.ei=customer_trade.ei and customer.ei = '%s'", tenantId)
                    + " and trade_code"
                    + String.format(getSearchConnectKey(isFuzzySearch), name)
                    + " and customer_trade.customer_id = customer.customer_id"
                    + " and customer_trade.is_deleted=FALSE and customer.is_deleted=FALSE"
                    + " and customer_trade.status<>99 and customer.status<>99"
                    + getLimitCondition(isFuzzySearch);
        } else {
            return "select customer_trade_id as _id,trade_code as name from customer_trade"
                    + String.format(" where ei = '%s'", tenantId)
                    + " and trade_code "
                    + String.format(getSearchConnectKey(isFuzzySearch), name)
                    + String.format(" and customer_id = '%s'", accountId)
                    + " and is_deleted=FALSE"
                    + " and status<>99 "
                    + getLimitCondition(isFuzzySearch);
        }
    }

    @Override
    protected String getNamesAccurateSearchSql(String tenantId, Set<String> names) {
        String salesOrderSearchStr = "";
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            salesOrderSearchStr += Strings.surround(iterator.next(), '\'', '\'');
            if (iterator.hasNext()) {
                salesOrderSearchStr += ",";
            }
        }
        return "select customer_trade_id as sales_order_id,trade_code as name, customer_id as account_id from "
                + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName())
                + String.format(" where ei = '%s'", tenantId)
                + " and trade_code "
                + String.format(" in(%s)", salesOrderSearchStr)
                + " and is_deleted=FALSE "
                + " and status<>99 ";
    }

    @Override
    protected String findByIdsSql(String tenantId, Set<String> objectIds) {
        String salesOrderSearchStr = "";
        Iterator<String> iterator = objectIds.iterator();
        while (iterator.hasNext()) {
            salesOrderSearchStr += Strings.surround(iterator.next(), '\'', '\'');
            if (iterator.hasNext()) {
                salesOrderSearchStr += ",";
            }
        }
        return "select customer_trade_id as sales_order_id,trade_code as name, customer_id as account_id from "
                + CasesConstants.REF_OBJECT_API_NAME_2_DB_NAME.get(getApiName())
                + String.format(" where ei = '%s'", tenantId)
                + " and customer_trade_id "
                + String.format(" in(%s)", salesOrderSearchStr)
                + " and is_deleted=FALSE "
                + " and status<>99 ";
    }
}
