package com.facishare.crm.sfa.predefine.service

import com.facishare.paas.metadata.impl.ObjectData
import spock.lang.Specification

class PriceBookCommonServiceImplTest extends Specification {

    def validatePriceBookIsExpiry() {
        def priceBookService = new PriceBookCommonServiceImpl();
        when:
        def priceObjectData = new ObjectData()
        priceObjectData.set("start_date", startTime)
        priceObjectData.set("end_date", endTime)
        then:
        boolean result = priceBookService.validatePriceBookUsableByExpiry(priceObjectData)
        result == flag
        where:
        //测试日期：2018/5/21
        startTime     || endTime       || flag
        null          || 1526832000000 || true //2018-05-21 00:00:00
        1526832000000 || null          || true //2018-05-21 00:00:00
        1526918400000 || 1526918400000 || false //2018-05-22 00:00:00
        1526659200000 || 1526659200000 || false //2018-05-19 00:00:00
        1526659200000 || 1526832000000 || true //2018-05-19 00:00:00、2018-05-21 00:00:00
        1526659200000 || 1526918399000 || true //2018-05-19 00:00:00、2018-05-21 23:59:59
    }
}
