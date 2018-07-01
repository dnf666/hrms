package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.appframework.metadata.dataconvert.FieldDataConverter;
import com.facishare.paas.appframework.metadata.dataconvert.FieldDataConverterManager;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;


import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductCommonService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private FieldDataConverterManager fieldDataConverterManager;


    /**
     * 特殊：给订单选择价目表产品时，填充产品数据
     * 给数据填充特殊产品数据
     */
    public void fillDataWithProduct(User user, List<IObjectData> data) {
        List<String> productIdList = data.stream().map(k -> k.get("product_id", String.class)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(data) && CollectionUtils.isNotEmpty(productIdList)) {
            // TODO: 2018/2/2 暂时没有处理产品中的lookup字段和引用字段
            List<IObjectData> productList = findProductList(user, productIdList);
            IObjectDescribe productDescribe = serviceFacade.findObject(user.getTenantId(), Utils.PRODUCT_API_NAME);

            transformDataForView(user, productDescribe, productList);
            Map<String, ObjectDataDocument> productMap = productList.stream().collect(Collectors.toMap(k -> k.getId(), k -> ObjectDataDocument.of(k)));
            data.forEach(k -> {
                k.set("product_id__ro", productMap.get(k.get("product_id", String.class)));
            });
        }
    }

    /**
     * 把数据转成终端或web端可展示的数据
     */
    private List<IObjectData> transformDataForView(User user, IObjectDescribe objectDescribe, List<IObjectData> productList) {
        List<IFieldDescribe> fieldDescribeList = objectDescribe.getFieldDescribes();

        //转换产品中的字段，如单选、多选、时间类型等
        for (IObjectData objectData : productList) {
            fieldDescribeList.forEach(fieldDescribe -> {
                FieldDataConverter fieldDataConverter = fieldDataConverterManager.getFieldDataConverter(fieldDescribe.getType());
                Object value = fieldDataConverter.convertFieldData(objectData, fieldDescribe, user);
                String stringValue = String.valueOf(value);
                if (IFieldType.PERCENTILE.equals(fieldDescribe.getType()) && null != value) {
                    value = stringValue + "%";
                }
                objectData.set(fieldDescribe.getApiName(), value);
            });
        }
        return productList;
    }

    private List<IObjectData> findProductList(User user, List<String> productIdList) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setLimit(productIdList.size());
        SearchUtil.fillFilterIn(searchTemplateQuery.getFilters(), IObjectData.ID, productIdList);
        searchTemplateQuery.setPermissionType(0);
        QueryResult<IObjectData> productResult = serviceFacade.findBySearchQuery(ActionContextExt.of(user).pgDbType().getContext(), Utils.PRODUCT_API_NAME, searchTemplateQuery);
        return productResult.getData();
    }
}
