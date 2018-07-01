package com.facishare.crm.sfa.predefine.manager;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceDispatcher;

@Service
public class SfaCustomerAccountManager {
    @Autowired
    private ServiceDispatcher serviceDispatcher;

    public boolean isCustomerAccountEnable(RequestContext context) throws Exception {
        ServiceContext serviceContext = new ServiceContext(context, "customer_account", "is_customer_account_enable");
        Object obj = serviceDispatcher.service(serviceContext, "");
        Boolean isEnable = getFieldValue(obj, "isEnable", Boolean.class);
        if (isEnable == null) {
            return false;
        }
        if (isEnable) {
            return true;
        }
        return false;
    }

    private <T> T getFieldValue(Object obj, String fieldName, Class<T> clazz) throws Exception {
        if (obj == null) {
            return null;
        }
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }
}
