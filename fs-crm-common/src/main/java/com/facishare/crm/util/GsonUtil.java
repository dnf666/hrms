package com.facishare.crm.util;

import com.google.gson.Gson;

/**
 * Created by xialf on 2017/06/08.
 *
 * @author xialf
 * @since 2017/06/08 5:48 PM
 */
public final class GsonUtil {
    public static final Gson GSON = new Gson();

    /**
     * json2 object
     *
     * @param json
     * @param clazz
     * @return
     */
    public static final <T> T json2object(String json, Class<T> clazz) {
        if (null == json || "".equals(json) || null == clazz) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
