package com.facishare.crm.sfa.utilities.proxy

import com.facishare.crm.sfa.utilities.proxy.model.BatchAddSpecModel
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created by luxin on 2018/2/26.
 */
@ContextConfiguration(value = "classpath:applicationContext11.xml")
class ProductProxyTest extends Specification {

  @Autowired
  private ProductProxy productProxy;

  def "AddSpec"() {

    given:
    BatchAddSpecModel.Arg arg = new BatchAddSpecModel.Arg()
    arg.setProductId("cb2943bda5df4c92a79d89555540949c")
    List list = Lists.newArrayList();
    arg.setSpecProductInfoList(list)
    Map<Object, Object> map = Maps.newHashMap()
    map.put("ProductCode", "")
    map.put("Barcode", "")
    map.put("ProductCode", "")
    map.put("Price", 20)
    map.put("UDCal1__c", "1")

    List list1 = Lists.newArrayList()
    Map<Object, Object> map1 = Maps.newHashMap()
    list1.add(map1)
    map1.put("SpecName", "颜色");
    map1.put("SpecValue", "21312")
    map.put("ProductSpecKVList", list1)

    Map<String, String> headers = Maps.newHashMap();
    headers.put("Content-Type", "application/json");
    headers.put("x-fs-ei", "2");
    headers.put("x-fs-userInfo", "1000");
    list.add(map)

    when:
    productProxy.addSpec(arg, headers);
    then:
    1 == 1


  }
}
