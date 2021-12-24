package com.example.xingliansdk.view;

import android.text.TextUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 功能:IF增强工具
 */

public class IF {

    /**
     * 全类型 非空判断
     * isEmpty 它们含有isEmpty 吗?
     * @param objs 任意类型
     * @return
     */
    public static boolean isEmpty(Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return true;
            }
            if (obj instanceof Object[]) {
                return ((Object[]) obj).length == 0;
            }
            if (obj instanceof String) {
                return TextUtils.isEmpty((String) obj)||TextUtils.isEmpty(((String) obj).trim());
            }
            if (obj instanceof List) {
                return ((List) obj).isEmpty();
            }
            if (obj instanceof Map) {
                return ((Map) obj).isEmpty();
            }
            if (obj instanceof Collection) {
                return ((Collection) obj).isEmpty();
            }

            if (obj instanceof int[]) {
                return ((int[]) obj).length == 0;
            }
            if (obj instanceof long[]) {
                return ((long[]) obj).length == 0;
            }
            if (obj instanceof float[]) {
                return ((float[]) obj).length == 0;
            }
            if (obj instanceof double[]) {
                return ((double[]) obj).length == 0;
            }
            if (obj instanceof byte[]) {
                return ((byte[]) obj).length == 0;
            }
            if (obj instanceof char[]) {
                return ((char[]) obj).length == 0;
            }
            if (obj instanceof short[]) {
                return ((short[]) obj).length == 0;
            }
            if (obj instanceof boolean[]) {
                return ((boolean[]) obj).length == 0;
            }
            //这个性能最差  放最后
            try {
                if (Integer.parseInt(String.valueOf(obj)) == 0) {
                    return true;
                }
            } catch (Exception ignored) {
            }

        }
        return false;
    }

    /**
     * @param ints 它们都相等吗
     * @return
     */
    public static boolean isEquals(int... ints) {
        int f = 0;
        for (int i = 0; i < ints.length; i++) {
            int obj = ints[i];
            if (i == 0) {
                f = obj;
                continue;
            }
            if (f != obj) {
                return false;
            }
        }
        return true;
    }

    /**
     * 这些字符串都包含在 原src字符串里面吗?
     * @param src 原字符串
     * @param args 字符串集合
     * @return
     */
    public static boolean isContains(String src,String... args) {
        if (IF.isEmpty(src)||args==null||args.length==0) {
            return false;
        }
        for (String arg : args) {
            boolean contains = arg != null && src.contains(arg);
            if(!contains){
                return false;
            }
        }
        return true;
    }


}
