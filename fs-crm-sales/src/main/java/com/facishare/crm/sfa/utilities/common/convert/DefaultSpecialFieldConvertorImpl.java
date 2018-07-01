package com.facishare.crm.sfa.utilities.common.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.utilities.util.JsonUtil;

/**
 * Created by lilei on 2017/7/28.
 */
public class DefaultSpecialFieldConvertorImpl implements SpecialFieldConvertor {

    @Override
    public String specialFieldConvert(String dataJson) {

        JSONObject jsonObject = JSON.parseObject(dataJson);
        jsonObject = ConvertUtil.processOwnerID(jsonObject, "OwnerID");
        jsonObject = ConvertUtil.processOwnerID(jsonObject, "BelongerID");

        return JsonUtil.toJsonWithNullValues(jsonObject);
    }

}
