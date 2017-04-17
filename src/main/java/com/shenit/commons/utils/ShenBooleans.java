package com.shenit.commons.utils;

/**
 * Created by jgnan on 19/02/2017.
 */
public final class ShenBooleans {
    /**
     * 判断是否布尔类型
     * @param type
     * @return
     */
    public static boolean isBooleanType(Class<?> type){
        return Boolean.class.isAssignableFrom(type) ||
                boolean.class.equals(type);
    }


    /**
     * 尝试把对象转换为布尔值
     * @param bool
     * @return
     */
    public static boolean bool(Object bool){
        if(bool == null) return false;
        else if(bool instanceof Number) return ((Number)bool).doubleValue() != 0.0;
        else if(bool instanceof String)
            return !(ShenStrings.FALSE.equalsIgnoreCase((String) bool) ||
                    ShenStrings.NULL.equalsIgnoreCase((String)bool));
        return true;
    }
}
