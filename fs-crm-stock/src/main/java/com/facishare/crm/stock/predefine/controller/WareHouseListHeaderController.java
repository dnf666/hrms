package com.facishare.crm.stock.predefine.controller;

import com.facishare.paas.appframework.common.util.DocumentBaseEntity;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by linchf on 2018/1/24.
 */
@Slf4j(topic = "stockAccess")
public class WareHouseListHeaderController extends StandardListHeaderController {

    @Override
    protected StandardListHeaderController.Result doService(StandardListHeaderController.Arg arg) {
        StandardListHeaderController.Result ret = super.doService(arg);
        LayoutDocument layout = ret.getLayout();
        List<DocumentBaseEntity> fieldList = ret.getFieldList();
        //适用客户字段隐藏
        if (!CollectionUtils.isEmpty(fieldList)) {
            fieldList.forEach(field -> {
                if (field.get("account_range") != null) {
                    field.put("account_range", false);
                }
            });
        }

        ret.setFieldList(fieldList);
        List<Map<String, Object>> components = (List<Map<String, Object>>)layout.get("components");
        if (!CollectionUtils.isEmpty(components)) {
            List<Map<String, Object>> fieldSections = (List<Map<String, Object>>)components.get(0).get("field_section");
            if (fieldSections != null) {
                fieldSections = fieldSections.stream().filter(filedSection -> !Objects.equals(filedSection.get("api_name").toString(), "customer_range_section__c")).collect(Collectors.toList());
                components.get(0).put("field_section", fieldSections);
            }
        }

        layout.put("components", components);

        //隐藏导入按钮
        List<Map<String, String>> buttons = (List<Map<String,String>>) layout.get("buttons");
        if (buttons != null) {
            buttons.removeIf(button -> null != button.get("action") && Objects.equals(button.get("action").toString(), ObjectAction.BATCH_IMPORT.getActionCode()));
        }

        layout.put("buttons", buttons);

        ret.setLayout(layout);
        return ret;
    }
}
