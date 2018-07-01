package com.facishare.crm.sfa.predefine.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(value = "classpath:applicationContext.xml")
class PriceBookStandardServiceTest extends Specification {
    static {
        System.setProperty("spring.profiles.active", "ceshi113")
    }
    @Autowired
    PriceBookStandardService standardService
    def "UpdateStandardPriceBookProduct"() {
        given:
//        PriceBookStandardService standardService = new PriceBookStandardService();
//        PriceBookService priceBookService = Mock()
//        standardService.priceBookService = priceBookService
//        standardService.getStandardPriceBook(_) >> null
//        ServiceFacade serviceFacade = Mock()
//        standardService.serviceFacade=serviceFacade
//        IObjectData objectData = new ObjectData()
//        QueryResult queryResult = Mock()
//        queryResult.getData() >> Arrays.asList(objectData)
//        serviceFacade.findBySearchQuery(_, _, _) >> queryResult
        when:
        def result = standardService.updateStandardPriceBookProduct(tenantId, productId, actionCode)
        then:
        result == flag
        where:
        tenantId | productId | actionCode | flag
        "55732"  | "22"      | "Add"      | true
    }
}
