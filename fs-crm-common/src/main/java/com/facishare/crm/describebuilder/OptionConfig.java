package com.facishare.crm.describebuilder;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 以下config参数默认配置 0代表不允许,1代表允许 ，不需要的功能不用配置，会采取默认配置
 */
public class OptionConfig {
    private Map<String, Object> config = Maps.newHashMap();

    public static OptionConfig builder() {
        return new OptionConfig();
    }

    public Map<String, Object> build() {
        return config;
    }

    /**
     * 是否允许修改option.value
     * @param value
     * @return
     */
    public OptionConfig edit(Integer value) {
        config.put("edit", value);
        return this;
    }

    /**
     * 是否允许删除option选项
     * @param value
     * @return
     */
    public OptionConfig remove(Integer value) {
        config.put("remove", value);
        return this;
    }

    /**
     * 是否允许修改option的启用禁用功能
     * @param value
     * @return
     */
    public OptionConfig enable(Integer value) {
        config.put("enable", value);
        return this;
    }
}
