package com.facishare.crm.stock.predefine.service.impl;

import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.predefine.service.ProductService;
import com.facishare.crm.stock.predefine.service.dto.StockType;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 23/03/2018
 */
@Slf4j(topic = "stockAccess")
@Component
public class ProductServiceImpl implements ProductService {
    @Resource
    private ServiceFacade serviceFacade;

    @Override
    public StockType.QueryProductByIdsResult queryProductByIds(ServiceContext serviceContext, StockType.QueryProductByIdsArg arg) {
        StockType.QueryProductByIdsResult result1 = new StockType.QueryProductByIdsResult();

        if (CollectionUtils.isEmpty(arg.getProductIds())) {
            return result1;
        }

        String apiName = SystemConstants.ProductApiName;
        List<IObjectData> objectDataList = serviceFacade.findObjectDataByIdsIncludeDeleted(serviceContext.getUser(), arg.getProductIds(), apiName);
        IObjectDescribe iObjectDescribe = serviceFacade.findObject(serviceContext.getTenantId(), apiName);
        Map<String, Object> objectDescribe = ObjectDescribeExt.of(iObjectDescribe).toMap();
        //<产品字段, value>
        List<Map<String, Object>> productLists = objectDataList.stream().map(objectData -> ObjectDataExt.toMap(objectData)).collect(Collectors.toList());
        result1.setValue(productLists);
        result1.setObjectDescribe(objectDescribe);
        return result1;
    }
}