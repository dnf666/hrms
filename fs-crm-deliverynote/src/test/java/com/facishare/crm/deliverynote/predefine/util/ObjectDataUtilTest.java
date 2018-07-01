package com.facishare.crm.deliverynote.predefine.util;

import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteProductVO;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ObjectDataUtilTest {
    @Test
    public void parseObjectData_Success() {
        Map<String, Object> map = new HashMap<>();
        map.put(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName, "N/A");
        map.put(DeliveryNoteProductObjConstants.Field.RealStock.apiName, "100");
        map.put(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName, null);
        IObjectData objectData = ObjectDataExt.of(map);

        Assert.assertEquals(objectData.get(DeliveryNoteProductObjConstants.Field.HasDeliveredNum.apiName), "N/A");
        Assert.assertEquals(objectData.get(DeliveryNoteProductObjConstants.Field.RealStock.apiName), "100");
        Assert.assertEquals(objectData.get(DeliveryNoteProductObjConstants.Field.OrderProductAmount.apiName), null);

        DeliveryNoteProductVO deliveryNoteProductVO = ObjectDataUtil.parseObjectData(objectData, DeliveryNoteProductVO.class);
        Assert.assertEquals(deliveryNoteProductVO.getHasDeliveredNum(), null);
        Assert.assertEquals(deliveryNoteProductVO.getRealStock(), new BigDecimal("100"));
        Assert.assertEquals(deliveryNoteProductVO.getOrderProductAmount(), null);
    }
}
