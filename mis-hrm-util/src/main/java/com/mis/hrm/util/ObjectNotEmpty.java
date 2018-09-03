package com.mis.hrm.util;

import java.lang.reflect.Field;

/**
 * 用来判断该对象是否为
 * 已经实例化但
 * 任意字段均未赋值
 * 或赋值均未无意义的空字符串
 * 的空对象
 */

public class ObjectNotEmpty {
    public static boolean notEmpty(Object object){
        //获取对象的所有属性
        Field[] fields = object.getClass().getDeclaredFields();
        int fieldNum = 0;
        int blankFieldNum = 0;

        for(Field field : fields) {
            fieldNum++;

            //设置该属性可操作
            field.setAccessible(true);

            try {
                if(!StringUtil.notEmpty((String)field.get(object))){
                    blankFieldNum++;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if(blankFieldNum == fieldNum){
            return false;
        } else {
            return true;
        }
    }
}
