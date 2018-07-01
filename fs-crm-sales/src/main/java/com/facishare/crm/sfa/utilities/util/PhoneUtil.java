package com.facishare.crm.sfa.utilities.util;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;

import org.springframework.util.StringUtils;

/**
 * @author cqx
 * @date 2018/5/9 17:22
 */
public class PhoneUtil {

    public static void dealPhone(ObjectDataDocument objectData) {
        String tels = getAppendPhone(objectData, "tel");
        String dealTels = getPhone(tels);
        objectData.put("tel", dealTels);

        String mobiles = getAppendPhone(objectData, "mobile");
        String dealMobiles = getPhone(mobiles);
        objectData.put("mobile", dealMobiles);
    }

    public static String getPhone(String str) {
        if (";:;:;:;:".equals(str)) {
            return "";
        } else {
            byte[] value = str.getBytes();
            int len = value.length;
            int st = 0;
            while ((st < len) && (value[len - 1] == ';' || value[len - 1] == ':')) {
                len--;
            }
            return (len < value.length) ? str.substring(st, len) : str;
        }
    }

    public static String getAppendPhone(ObjectDataDocument objectData, String phone) {

        StringBuffer phones = new StringBuffer();
        for (int i = 1; i <= 5; i++) {
            Object defaultPhone = objectData.get(phone + i);
            if (i == 1) {
                phones.append(defaultPhone == null ? "" : defaultPhone);
            } else {
                phones.append(";:" + (defaultPhone == null ? "" : defaultPhone));
            }
        }

        return phones.toString();

    }

    public static void splitPhoneNumber(ObjectDataDocument data) {
        Object tel = data.get("tel");
        if (tel instanceof String) {
            String dealTel = (String) tel;
            if (!StringUtils.isEmpty(dealTel)) {
                String[] tels = dealTel.toString().split(";:");
                for (int i = 1; i <= tels.length; i++) {
                    data.put("tel" + i, tels[i - 1]);
                }
            }

        }

        Object mobile = data.get("mobile");
        if (mobile instanceof String) {
            String dealMobile = (String) mobile;
            if (!StringUtils.isEmpty(dealMobile)) {
                String[] mobiles = dealMobile.toString().split(";:");
                for (int i = 1; i <= mobiles.length; i++) {
                    data.put("mobile" + i, mobiles[i - 1]);
                }
            }
        }
    }

}
