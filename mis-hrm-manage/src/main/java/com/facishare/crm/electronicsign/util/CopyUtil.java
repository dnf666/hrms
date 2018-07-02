package com.facishare.crm.electronicsign.util;

import com.facishare.crm.electronicsign.predefine.model.SignSettingDO;
import com.facishare.crm.electronicsign.predefine.model.vo.SignSettingVO;
import com.facishare.open.app.center.common.utils.BeanUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * created by dailf on 2018/4/24
 *  转换do为vo
 * @author dailf
 */
public class CopyUtil {
    public static <T, I> T copyOne (Class<T> tClass,I i){
        return BeanUtil.copyProperties(tClass,i);
    }


    public static <T, I> List<T> copyMany (Class<T> tClass, List<I> list){
        List<T> list1 = new ArrayList<>();
        for (I i: list
             ) {
          list1.add(BeanUtil.copyProperties(tClass,i));
        }
        return list1;
    }

}
