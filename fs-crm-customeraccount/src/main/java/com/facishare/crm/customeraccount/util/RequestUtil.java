package com.facishare.crm.customeraccount.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.facishare.paas.appframework.core.model.ActionContext;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContext.RequestSource;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;

public class RequestUtil {
    public static final String FROM_INNER_KEY = "_inner_";

    public static boolean isOutUser(User user) {
        String userId = user.getUserId();
        if (Long.valueOf(userId) > 1 * 10000 * 10000) {
            return true;
        }
        return false;
    }

    public static boolean isFromInner(ActionContext actionContext) {
        RequestSource resource = actionContext.getRequestSource();
        if (resource != null && resource == RequestSource.INNER) {
            return true;
        }
        return false;
    }

    public static User getSysteomUser(String tenantId) {
        return new User(tenantId, User.SUPPER_ADMIN_USER_ID);
    }

    public static ServiceContext generateServiceContextByTenantId(String tenantId, String fsUserId) {
        Optional<User> user = Optional.of(new User(tenantId, fsUserId));
        Map<Object, Object> map = new HashMap<>();
        RequestContext.RequestContextBuilder requestContextBuilder = RequestContext.builder();
        requestContextBuilder.tenantId(tenantId);
        requestContextBuilder.user(user);
        requestContextBuilder.contentType(RequestContext.ContentType.FULL_JSON);
        requestContextBuilder.postId(String.valueOf(System.currentTimeMillis()));
        RequestContext requestContext = requestContextBuilder.build();
        RequestContextManager.setContext(requestContext);
        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        return serviceContext;
    }

    public static ActionContext generateActionContext(User user, String objectApiName, String actionCode) {
        RequestContext requestContext = RequestContext.builder().tenantId(user.getTenantId()).user(Optional.of(user)).requestSource(RequestContext.RequestSource.INNER).contentType(RequestContext.ContentType.FULL_JSON).build();
        ActionContext actionContext = new ActionContext(requestContext, objectApiName, actionCode);
        return actionContext;
    }
}
