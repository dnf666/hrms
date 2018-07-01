package com.facishare.crm.electronicsign.predefine.action;

import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.predefine.base.BaseActionTest;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class AccountSignCertifyBulkRecoverActionTest extends BaseActionTest {
    static {
        System.setProperty("process.profile", "ceshi113");
    }

    public AccountSignCertifyBulkRecoverActionTest() {
        super(AccountSignCertifyObjConstants.API_NAME);
    }

    public void initUser() {
        this.tenantId = "55988";
        this.fsUserId = "1000";
    }

    @Test
    public void bulkRecoverTest() {
        StandardBulkRecoverAction.Arg arg = new StandardBulkRecoverAction.Arg();
        arg.setObjectDescribeAPIName("AccountSignCertifyObj");
        arg.setIdList(Lists.newArrayList("5b0cfb07bab09ccb7625a138"));
        this.execute(StandardAction.BulkRecover.name(), arg);
}
}
