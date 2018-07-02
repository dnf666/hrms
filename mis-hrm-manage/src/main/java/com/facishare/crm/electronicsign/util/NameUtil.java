package com.facishare.crm.electronicsign.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xialf on 2017/06/17.
 * 将一堆数字按照驼峰规则输出成字符串。但是传入的数据必须要规范
 * @author xialf
 * @since 2017/06/17 11:45 AM
 */
public final class NameUtil {
    public static String camelCase(List<String> words) {
        if (words.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(lowerCap(words.get(0)));

        for (int i = 1; i < words.size(); i++) {
            sb.append(capitalize(words.get(i)));
        }
        return sb.toString();
    }

    public static String capitalize(final String word) {
        final char firstChar = word.charAt(0);
        final char capital = Character.toUpperCase(firstChar);

        return capital + word.substring(1);
    }

    public static String lowerCap(final String word) {
        final char firstChar = word.charAt(0);
        final char lowerCap = Character.toLowerCase(firstChar);

        return lowerCap + word.substring(1);
    }


}
