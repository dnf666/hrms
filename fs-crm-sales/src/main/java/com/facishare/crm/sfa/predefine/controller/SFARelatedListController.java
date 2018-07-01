package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by luxin on 2018/1/26.
 */
public class SFARelatedListController extends StandardRelatedListController {

    @Override
    protected QueryResult<IObjectData> getQueryResult(SearchTemplateQuery query) {
        QueryResult<IObjectData> queryResult = super.getQueryResult(query);
        //填充产品信息
        fillDataWithProduct(this.getControllerContext().getUser(), queryResult.getData());
        return queryResult;
    }


    private void fillDataWithProduct(User user, List<IObjectData> data) {
        List<String> productIdList = data.stream().map(k -> k.get("product_id", String.class)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(data) && CollectionUtils.isNotEmpty(productIdList)) {

            Map<Object, Object> unitKey2UnitDisplay = getProductUnitInfo(user);
            data.forEach(productData -> {
                Map<String, Object> productInfo = (Map) productData.get("product_id__ro");
                if (productInfo != null && !unitKey2UnitDisplay.isEmpty()) {
                    //处理单位,将数字转换成文字
                    productInfo.put("unit", unitKey2UnitDisplay.get(productInfo.get("unit")));
                }
                productData.set("name", productData.get("product_id__r"));
            });
        }
    }

    private Map<Object, Object> getProductUnitInfo(User user) {
        IObjectDescribe describe = serviceFacade.findObject(user.getTenantId(), Utils.PRODUCT_API_NAME);
        Map<Object, Object> unitKey2UnitDisplay = Maps.newHashMap();
        if (describe != null) {
            IFieldDescribe fieldDescribe = describe.getFieldDescribe("unit");
            if (fieldDescribe != null) {
                List<Map<Object, Object>> options = fieldDescribe.get("options", List.class);
                if (options != null) {
                    for (Map option : options) {
                        unitKey2UnitDisplay.put(option.get("value"), option.get("label"));
                    }
                }
            }
        }
        return unitKey2UnitDisplay;
    }


}
