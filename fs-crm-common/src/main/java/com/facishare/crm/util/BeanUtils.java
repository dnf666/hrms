package com.facishare.crm.util;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;

public abstract class BeanUtils {
    public BeanUtils() {
    }

    public static <D, S> void copyProperties(D d, S s) {
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperties(d, s);
        } catch (Exception var3) {
            throw new RuntimeException("Copy Properties failed,DestClass:" + d.getClass().getName() + " SrcClass:" + s.getClass().getName());
        }
    }

    /**
     * 把p对象相同的属性赋予其
     *
     * @param destClazz   目标对象
     * @param originalObj 源对象
     * @return
     */
    public static <P, R> P copyProperties(Class<P> destClazz, R originalObj) {
        if (originalObj == null) {
            return null;
        }

        P entity = null;
        try {
            entity = destClazz.newInstance();
            PropertyUtils.copyProperties(entity, originalObj);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException("Copy Properties failed, destClazz:" + destClazz.getClass().getName() + " originalClass:" + originalObj.getClass().getName());
        }
        return entity;
    }
}
