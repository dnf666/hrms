package com.facishare.crm.payment.transfer

import com.facishare.paas.appframework.core.model.RequestContext
import com.facishare.paas.appframework.core.model.RequestContextManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created on 2018/2/7.
 */
@ContextConfiguration(value = "classpath:applicationContext.xml")
class DataTransferProxyTest extends Specification {

    static {
        System.setProperty("spring.profiles.active", "ceshi113")
    }
    @Autowired
    DataTransferProxy proxy

    def setup() {
        RequestContextManager.setContext(RequestContext.builder().build())
    }

    def "CreateJob"() {
        DataTransferModel.Arg arg = new DataTransferModel.Arg();
        arg.setBiz("ceshi113")
        arg.setEidsAll("55910")
        arg.setHook("172.29.0.145:8086/crm/API/v1/inner/object/attachmenttransfer/service/transfer")
        arg.setSql("SELECT \n" +
                "data_id,\n" +
                "array_to_string(array_agg(attach_path),'|') as attach_path,\n" +
                "array_to_string(array_agg(attach_name),'|') as attach_name,\n" +
                "array_to_string(array_agg(create_time),'|') as create_time,\n" +
                "array_to_string(array_agg(attach_size),'|') as attach_size,\n" +
                "array_to_string(array_agg(field_name),'|') as field_name\n" +
                "from attach\n" +
                "WHERE source = 10\n" +
                "and ei = \$!{ei}\n" +
                "GROUP BY data_id")
        proxy.createJob(arg)
    }
}
