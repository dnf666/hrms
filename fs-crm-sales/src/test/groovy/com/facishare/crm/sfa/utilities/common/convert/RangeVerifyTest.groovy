package com.facishare.crm.sfa.utilities.common.convert

import com.alibaba.fastjson.JSON
import com.facishare.paas.metadata.impl.ObjectData
import spock.lang.Specification

class RangeVerifyTest extends Specification {
    def "VerifyCondition"() {
        given:
        ObjectData objectData = new ObjectData()
        objectData.set("name", "a1")
        objectData.set("sales_process_name", "23v123")
        List list = new LinkedList();
        list.add("01")
        list.add("0")
        objectData.set("owner",list)
        objectData.set("last_followed_time",1507737600000)

        RangeVerify rangeVerify = new RangeVerify(objectData);
        when:
        def rangeObj = JSON.parseObject(range)
        boolean flag = rangeVerify.verifyConditions(rangeObj.getJSONObject("value"));
        then:
        flag == result
        where:
        range                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | result
        "{\"type\":\"hasCondition\",\"value\":{\"conditions\":[{\"conditions\":[{\"left\":{\"expression\":\"name\"},\"right\":{\"type\":{\"name\":\"text\"},\"value\":\"a\"},\"type\":\"equals\"},{\"left\":{\"expression\":\"sales_process_name\"},\"right\":{\"type\":{\"name\":\"text\"},\"value\":\"v\"},\"type\":\"contains\"}],\"type\":\"and\"},{\"conditions\":[{\"left\":{\"expression\":\"owner\"},\"right\":{\"type\":{\"name\":\"text\"},\"value\":\"0\"},\"type\":\"hasValue\"},{\"left\":{\"expression\":\"last_followed_time\"},\"right\":{\"type\":{\"name\":\"number\"},\"value\":1507737600000},\"type\":\"lessThanOrEqual\"}],\"type\":\"and\"}],\"type\":\"or\"}}" | true

    }
}
