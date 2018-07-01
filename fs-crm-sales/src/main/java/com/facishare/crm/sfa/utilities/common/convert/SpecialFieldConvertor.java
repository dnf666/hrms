package com.facishare.crm.sfa.utilities.common.convert;

/**
 * Created by lilei on 2017/7/28.
 */
public interface SpecialFieldConvertor {
    /***
     * 特殊字段的处理方法（各个预置对象都会有自己的实现。
     * 如果没有对应的实现，则默认使用DefaultSpecialFieldConvertor
     * 即：不处理
     * @param dataJson 对应IObjectData中的object_data
     * @return
     */
    String specialFieldConvert(String dataJson);
}
