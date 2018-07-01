package com.facishare.crm.sfainterceptor.utils;

import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by linchf on 2018/1/25.
 */
public class SfainterceptorUtils {
    private static boolean isOutUser(User user) {
        String userId = user.getUserId();
        if (Long.valueOf(userId) > 1 * 10000 * 10000) {
            return true;
        }
        return false;
    }

    public static ServiceContext outUser2Admin(ServiceContext serviceContext) {
        User user = serviceContext.getUser();
        if (isOutUser(serviceContext.getUser())) {
            user = new User(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
            RequestContext newRequestContext = RequestContext.builder().requestSource(RequestContext.RequestSource.INNER).postId(serviceContext.getPostId()).tenantId(user.getTenantId()).user(Optional.of(user)).build();
            return new ServiceContext(newRequestContext, serviceContext.getServiceMethod(), serviceContext.getServiceName());
        }
        return serviceContext;
    }
}
