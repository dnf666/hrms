package groovy.com.facishare.crm.sfa.predefine.service

import com.facishare.crm.sfa.utilities.proxy.GetHomePermissionsProxy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author cqx
 * @date 2018/1/20 15:14
 */
@ContextConfiguration(value = "classpath:applicationContext2.xml")
class TestGetMenu  extends Specification{

    static {
        System.setProperty("spring.profiles.active", "ceshi113")
    }
    @Autowired
    GetHomePermissionsProxy getHomePermissionsProxy

    def "getMenu"(){
        when:
        def a = getHomePermissionsProxy.getMenuByTenantId("2","1000")
        def b = getHomePermissionsProxy.getHomePermissionsByTenantId("2","1000")

        then:
        print(a)
        print(b)
        1==1

    }
}
