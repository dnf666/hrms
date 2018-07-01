package com.facishare.crm.sfa.predefine.service

import com.facishare.crm.sfa.predefine.service.model.PriceBookProdResult
import com.facishare.crm.sfa.predefine.service.model.PriceBookResult
import com.facishare.paas.appframework.core.model.RequestContext
import com.facishare.paas.appframework.core.model.RequestContextManager
import com.facishare.paas.appframework.core.model.ServiceContext
import com.facishare.paas.appframework.core.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(value = "classpath:applicationContext.xml")
class PriceBookServiceTest extends Specification {
    static {
        System.setProperty("spring.profiles.active", "ceshi113")
    }
    @Autowired
    PriceBookService priceBookService;
    def setup() {
        RequestContextManager.setContext(RequestContext.builder().build())
    }
    def "Findbyaccountid"() {
        given:
        def context = getContext(tenantId, userId)
        PriceBookResult.Arg arg = new PriceBookResult.Arg();
        arg.setLimit(10)
        arg.setOffset(0)
        arg.setAccount_id(accountId)
        when:
        def result = priceBookService.findbyaccountid(arg, context)
        then:
        result.getDataList().size() == size
        where:
        tenantId | userId | accountId                          | size
        "55732"  | "1000" | "b339f6eb97c1450cb54432ea94761487" | 3
    }

    def "FindPriceBookProdListByQueryCondition"() {
        given:
        def context = getContext(tenantId, userId)
        PriceBookProdResult.Arg arg = new PriceBookProdResult.Arg();
        arg.setLimit(10)
        arg.setOffset(0)
        when:
        def result = priceBookService.findPriceBookProdListByQueryCondition(arg, context)
        then:
        result.getDataList().size() == size
        where:
        tenantId | userId | size
        "55732"  | "1000" | 3
    }

    def getContext(tenantId, userId) {
        ServiceContext context = Mock(ServiceContext)
        context.getTenantId() >> tenantId
        def user = Mock(User)
        context.getUser() >> user
        user.getUserId() >> userId
        return context
    }

}
