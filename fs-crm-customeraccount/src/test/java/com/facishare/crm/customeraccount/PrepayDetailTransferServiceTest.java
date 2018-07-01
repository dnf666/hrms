package com.facishare.crm.customeraccount;

import com.facishare.crm.common.BaseServiceTest;
import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.predefine.CustomerAccountPredefineObject;
import com.facishare.crm.customeraccount.service.PrepayDetailTransferService;
import com.facishare.crm.customeraccount.service.RebateOutcomeDetailTransferService;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by xujf on 2018/1/24.
 */
/**
 * Created by xujf on 2017/10/12.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-test/applicationContext.xml")
public class PrepayDetailTransferServiceTest extends BaseServiceTest {
    @Autowired
    PrepayDetailTransferService prepayDetailTransferService;

    @Autowired
    RebateOutcomeDetailTransferService rebateOutcomeDetailTransferService;

    public PrepayDetailTransferServiceTest() {
        super(PrepayDetailConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    /**
     * 2018-1-29 15:48:03测试通过<br>
     */
    @Test
    public void testProcessTransfer() {
        ServiceContext serviceContext = getServiceContext();
        prepayDetailTransferService.processTransfer(serviceContext);
    }

    /**
     * 2018-1-29 15:47:49 测试通过<br>
     */
    @Test
    public void testProcessTransferRebate() {
        ServiceContext serviceContext = getServiceContext();
        rebateOutcomeDetailTransferService.processTransfer(serviceContext);
    }

    @Test
    public void testQueyData() {
        ServiceContext serviceContext = getServiceContext();
        User user = serviceContext.getUser();
        String tenantId = serviceContext.getTenantId();
        List<IObjectData> prepayDetailLists = prepayDetailTransferService.getOnePageRequestData(user, tenantId, 0, 1000);

        log.info("prepayDetailLists:{}", prepayDetailLists);
    }

    /**
     *  2018年1月29日 invalid和is_deleted状态都可以查到,多页面也一样<br>
     */
    @Test
    public void testQueryOnePageRequest() {
        ServiceContext serviceContext = getServiceContext();
        User user = serviceContext.getUser();
        String tenantId = serviceContext.getTenantId();
        List<IObjectData> prepayList = prepayDetailTransferService.getOnePageRequestData(user, tenantId, 1, 1000);
        log.info("prepayList:====>{}", prepayList);
    }

    @Test
    public void testQueryOnePageRequestByPaymentAndOrderPaymentCondition() {
        ServiceContext serviceContext = getServiceContext();
        User user = serviceContext.getUser();
        String tenantId = serviceContext.getTenantId();
        List<IObjectData> prepayList = prepayDetailTransferService.getOnePageRequestDataByPaymentAndOrderPaymentCondition(user, tenantId, 1, 1000);
        log.info("testQueryOnePageRequestByPaymentAndOrderPaymentCondition->tenatId:{},user:{},prepayList:====>{}", tenantId, user, prepayList);
    }

    private ServiceContext getServiceContext() {
        initUser();
        this.user = new User(tenantId, fsUserId);
        CustomerAccountPredefineObject.init();
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

        ServiceContext serviceContext = new ServiceContext(requestContext, null, null);
        return serviceContext;
    }
}
