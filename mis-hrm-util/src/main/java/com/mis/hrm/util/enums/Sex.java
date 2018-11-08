package com.mis.hrm.util.enums;

import java.util.HashSet;
import java.util.Set;

/**
 * created by dailf on 2018/10/27
 *
 * @author dailf
 */
public enum Sex {
    /**
     * 男
     */
    MAN("男"),
    /**
     * 女
     */
    WOMAN("女");
    String sex;

        Sex(String sex) {
        this.sex = sex;
    }
    private static Set<String> sexSet = createSex();

    public String getSex() {
        return sex;
    }

    private static Set<String> createSex() {
        Set<String> strings = new HashSet<>();
        Sex[] arrays = values();
        for (Sex sex:arrays
             ) {
           strings.add(sex.getSex());
        }
        return strings;
    }
    public static boolean judgeSex(String value) {
        return sexSet.contains(value);
    }

}
