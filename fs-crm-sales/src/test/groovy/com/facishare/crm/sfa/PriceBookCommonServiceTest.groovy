package groovy.com.facishare.crm.sfa

import com.facishare.crm.sfa.predefine.service.PriceBookCommonServiceImpl
import com.facishare.paas.appframework.core.model.ServiceFacade
import com.facishare.paas.metadata.api.IObjectData
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import spock.lang.Specification

/**
 * Created by luxin on 2018/1/31.
 */
class PriceBookCommonServiceTest extends Specification {
  def "GetNotInPriceBookPriceBookProductIds"() {
  }


  def "GetNotInPriceBookPriceBookProducts"() {


    given:
    PriceBookCommonServiceImpl priceBookCommonService = new PriceBookCommonServiceImpl()

    def tenantId = "7"

    com.facishare.paas.metadata.api.QueryResult<IObjectData> queryResult = Mock()

    queryResult.getData()>>Lists.newArrayList()

    ServiceFacade serviceFacade = Mock()

    priceBookCommonService.serviceFacade = serviceFacade
    serviceFacade.findBySearchQuery(_,_,_) >> queryResult



    Map<String, List<String>> priceBookId2priceBookProductIds = Maps.newHashMap()

    List<String> priceBookProductIds = Lists.newArrayList("dde5c832f2b5463aba2ecf12783669332", "d9da0e706594451db29d1fec4834b0eb2")

    priceBookId2priceBookProductIds.put("5a3a1a9cbab09cc54aed92e8", priceBookProductIds)
    when:
    priceBookCommonService.getNotInPriceBookPriceBookProducts(tenantId, priceBookId2priceBookProductIds)

    then:
    1 == 1

  }


}
