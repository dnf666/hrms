package com.facishare.crm.describebuilder;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Maps;

/**
 * 以下config参数默认配置 0代表不允许,1代表允许 ，不需要的功能不用配置，会采取默认配置
 */
public class FieldConfig {
    private Map<String, Object> config = Maps.newHashMap();

    /**
     * 是否允许添加(主要针对select_one/select_many/record_type类型添加option选项)
     * @param value
     */
    public FieldConfig add(Integer value) {
        config.put("add", value);
        return this;
    }

    /**
     * 是否显示
     * @param value
     * @return
     */
    public FieldConfig dispaly(Integer value) {
        config.put("display", value);
        return this;
    }

    /**
     * 是否允许使用编辑功能
     * @param value
     * @return
     */
    public FieldConfig edit(Integer value) {
        config.put("edit", value);
        return this;
    }

    /**
     * 是否允许使用启用/禁用功能
     * @param value
     * @return
     */
    public FieldConfig enable(Integer value) {
        config.put("enable", value);
        return this;
    }

    /**
     * 是否允许使用删除功能
     * @param value
     * @return
     */
    public FieldConfig remove(Integer value) {
        config.put("remove", value);
        return this;
    }

    /**
     * 字段属性配置
     * @param fieldApiNames，value=1允许修改，value=0不允许修改
     * @param value
     * @return
     */
    public FieldConfig attrs(List<String> fieldApiNames, Integer value) {
        Map<String, Object> attrsMap;
        if (config.containsKey("attrs")) {
            attrsMap = (Map<String, Object>) config.get("attrs");
        } else {
            attrsMap = Maps.newHashMap();
        }
        if (CollectionUtils.isNotEmpty(fieldApiNames)) {
            fieldApiNames.forEach(fieldApiName -> attrsMap.put(fieldApiName, value));
        }
        config.put("attrs", attrsMap);
        return this;
    }

    public static FieldConfig builder() {
        return new FieldConfig();
    }

    public Map<String, Object> build() {
        return config;
    }
}
