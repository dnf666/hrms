package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import com.facishare.paas.metadata.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by linchf on 2018/1/17.
 */
@Slf4j(topic = "stockAccess")
public class StockListHeaderController extends StandardListHeaderController {
    private StockManager stockManager = (StockManager) SpringUtil.getContext().getBean("stockManager");

    @Override
    protected StandardListHeaderController.Result doService(StandardListHeaderController.Arg arg) {
        StandardListHeaderController.Result ret = super.doService(arg);
        LayoutDocument layout = ret.getLayout();
        List<Map<String, String>> buttons = (List<Map<String, String>>)layout.get("buttons");

        if (buttons != null) {
            if (stockManager.checkGoodsReceivedNoteAddPrivilege(controllerContext.getUser())) {
                Map buttonMap = new HashMap<>();
                buttonMap.put("action_type", "default");
                buttonMap.put("api_name", "Add_button_default");
                buttonMap.put("action", "Add");
                buttonMap.put("label", "新建入库单");
                buttons.add(buttonMap);
            }

            if (stockManager.checkGoodsReceivedNoteViewListPrivilege(controllerContext.getUser())) {
                Map buttonMap = new HashMap<>();
                buttonMap.put("action_type", "default");
                buttonMap.put("api_name", "list_button_default");
                buttonMap.put("action", "List");
                buttonMap.put("label", "查看入库单");
                buttons.add(buttonMap);
            }
        }

        ObjectDescribeDocument objectDescribeDocument = ret.getObjectDescribe();
        Map<String, Map<String, Object>> fields = (Map<String, Map<String, Object>>) objectDescribeDocument.get("fields");
        if (fields != null) {
            fields.keySet().forEach(key -> {
                    Map<String, Object> fieldMap = fields.get(key);
                    if (Objects.equals(fieldMap.get("type").toString(), "quote")) {
                        fieldMap.put("is_index", false);
                    }
                });
        }

        return ret;
    }
}
