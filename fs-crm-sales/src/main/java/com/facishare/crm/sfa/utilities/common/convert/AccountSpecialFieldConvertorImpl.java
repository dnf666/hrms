package com.facishare.crm.sfa.utilities.common.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.utilities.util.JsonUtil;

/**
 * Created by lilei on 2017/7/28.
 */
public class AccountSpecialFieldConvertorImpl implements SpecialFieldConvertor {

    @Override
    public String specialFieldConvert(String dataJson) {
        JSONObject jsonObject = JSON.parseObject(dataJson);
        String country = JsonUtil.getStringWithoutNPE("Country", jsonObject);
        String province = JsonUtil.getStringWithoutNPE("Province", jsonObject);
        String city = JsonUtil.getStringWithoutNPE("City", jsonObject);
        String distinct = JsonUtil.getStringWithoutNPE("District", jsonObject);
        String area = country + "/" + province + "/" + city + "/" + distinct;
        jsonObject.put("Area", area);
        String industryLevel2 = JsonUtil.getStringWithoutNPE("SubIndustry", jsonObject);
        if (org.apache.commons.lang.StringUtils.isNotBlank(industryLevel2)) {
            jsonObject.put("Industry", industryLevel2);
        }

        jsonObject = ConvertUtil.processOwnerID(jsonObject, "OwnerID");

        return JsonUtil.toJsonWithNullValues(jsonObject);
    }

}
