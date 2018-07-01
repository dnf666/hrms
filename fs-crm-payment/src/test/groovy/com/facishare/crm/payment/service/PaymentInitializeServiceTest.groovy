package com.facishare.crm.payment.service

import com.facishare.crm.payment.initialize.ObjectDescribeInitializer
import com.facishare.crm.payment.service.dto.PaymentInitialize
import com.facishare.crm.payment.service.dto.PaymentInitialize.Arg
import spock.lang.Specification

class PaymentInitializeServiceTest extends Specification {

    def "Test Fails"() {
        setup:
        def service = new PaymentInitializeService()
        service.describeInitializer = Mock(ObjectDescribeInitializer)
        def arg = new Arg()
        arg.mode = PaymentInitialize.InitializeMode.ALL
        arg.setTenantIds("1,2,3,4,5,5")

        when:
        def result = service.initializeAll(arg)

        then:
        result.fails.size() == 5
    }
}
