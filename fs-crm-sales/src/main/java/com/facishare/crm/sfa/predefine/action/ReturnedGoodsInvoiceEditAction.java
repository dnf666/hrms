package com.facishare.crm.sfa.predefine.action;

import com.facishare.crm.sfa.utilities.common.convert.ConvertUtil;
import com.facishare.crm.sfa.utilities.util.JsonUtil;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/1/2.
 */
@Slf4j
public class ReturnedGoodsInvoiceEditAction extends SFAEditAction {
    @Override
    public void before(Arg arg) {
        log.info("ReturnedGoodsInvoiceEditAction>before()>arg={}" + JsonUtil.toJsonWithNullValues(arg));

        super.before(arg);
        // TODO: 2018/1/3 修改方法名
        ConvertUtil.mergeMDForSalesOrder(arg.getObjectData(), arg.getDetails(), "ReturnedGoodsInvoiceProductObj");
    }

    @Override
    public Result doAct(Arg arg) {
        log.info("ReturnedGoodsInvoiceEditAction>act()>arg={}" + JsonUtil.toJsonWithNullValues(arg));
        super.doAct(arg);

        List<Map<String, Object>> returnProducts = (List<Map<String, Object>>) objectData.get("returnproduct");
        if (CollectionUtils.isNotEmpty(returnProducts)) {
            for (Map<String, Object> returnProduct : returnProducts) {
                returnProduct.put("_id", returnProduct.get("TradeProductID"));
            }
        }

        IObjectData updated = serviceFacade.updateObjectData(actionContext.getUser(), objectData);
        return StandardEditAction.Result.builder().objectData(ObjectDataDocument.of(updated)).build();
    }

}

