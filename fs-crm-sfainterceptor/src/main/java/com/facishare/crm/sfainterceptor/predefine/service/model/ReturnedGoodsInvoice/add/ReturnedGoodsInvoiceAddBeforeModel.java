package com.facishare.crm.sfainterceptor.predefine.service.model.ReturnedGoodsInvoice.add;

import com.facishare.crm.sfainterceptor.predefine.service.model.CommonModel;
import com.facishare.crm.sfainterceptor.predefine.service.model.add.AddBeforeModel;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhongxing
 * @date on 2018/1/9.
 */
@Data
public class ReturnedGoodsInvoiceAddBeforeModel extends CommonModel {

    @Data
    @ToString
    public static class Arg {
        private ReturnedGoodsInvoiceVo returnedGoodsInvoiceVo;
    }

    @Data
    @ToString
    public static class ReturnedGoodsInvoiceVo {
        private String customerId;
        private String warehouseId;
        private String tradeId;
    }


}
