package com.facishare.crm.sfainterceptor.base;

import com.facishare.crm.stock.predefine.StockPredefineObject;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.core.model.User;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BaseTest {
    protected RequestContext requestContext;
    protected String tenantId;
    protected String fsUserId;
    protected User user;

    protected String sysEnv = null;

    public void initUser() {
        String pro = System.getProperty("spring.profiles.active");
        if ("ceshi113".equals(pro)) {
            this.tenantId = "55985";
            this.fsUserId = "1000";
        } else if ("fstest".equals(pro)) {
            this.tenantId = "71590";
            this.fsUserId = "1000";
        } else {
            throw new RuntimeException("请设置“spring.profiles.active”");
        }
    }

    @Before
    public void init() {
        initUser();
        this.user = new User(tenantId, fsUserId);
        StockPredefineObject.init();
        Optional<User> user = Optional.of(new User(tenantId, fsUserId));
        String postId = System.currentTimeMillis() + "";
        Map<Object, Object> map = new HashMap<>();
        RequestContext.RequestContextBuilder requestContextBuilder = RequestContext.builder();
        requestContextBuilder.tenantId(tenantId);
        requestContextBuilder.user(user);
        requestContextBuilder.contentType(RequestContext.ContentType.FULL_JSON);
        requestContextBuilder.postId(postId);
        requestContextBuilder.requestSource(RequestContext.RequestSource.CEP);
        requestContext = requestContextBuilder.build();
        RequestContextManager.setContext(requestContext);
    }

}
