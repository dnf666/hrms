package com.facishare.crm.deliverynote.predefine.manager;

import com.facishare.crm.stock.predefine.service.model.IsStockEnableModel;
import com.facishare.crm.stock.util.ConfigCenter;
import com.facishare.crm.stock.util.HttpUtil;
import com.facishare.crm.stock.util.StockUtils;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * ERP库存开关判断
 * @author linchf
 * @date 2018/5/10
 */
@Slf4j
@Service
public class ErpStockSwitchManager {
    public Boolean isErpStockEnable(String tenantId) {
        String checkErpStockEnableUrl = ConfigCenter.PAAS_FRAMEWORK_URL + "erp_stock_biz/service/is_erp_stock_enable";
        try {
            Map<String, String> headers = StockUtils.getHeaders(tenantId, User.SUPPER_ADMIN_USER_ID);
            headers.put("Content-Type", "application/json");
            IsStockEnableModel.ResultVO resultVO = HttpUtil.post(checkErpStockEnableUrl, headers, null, IsStockEnableModel.ResultVO.class);
            if (resultVO != null && resultVO.getResult().getIsEnable()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
