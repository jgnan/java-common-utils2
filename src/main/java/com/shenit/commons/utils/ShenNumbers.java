package com.shenit.commons.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Number utilities for Meme project
 * Created by jgnan on 05/11/2016.
 */
public final class ShenNumbers {
    /**
     * 判斷是否數字類型
     * @param type
     * @return
     */
    public static boolean isNumberType(Class<?> type){
        return Number.class.isAssignableFrom(type) ||
                int.class.isAssignableFrom(type) ||
                short.class.isAssignableFrom(type) ||
                long.class.isAssignableFrom(type) ||
                byte.class.isAssignableFrom(type) ||
                float.class.isAssignableFrom(type) ||
                double.class.isAssignableFrom(type);
    }

    /**
     * 判斷是否數字類型
     * @param type
     * @return
     */
    public static boolean isIntegerType(Class<?> type){
        return Number.class.isAssignableFrom(type) ||
                int.class.isAssignableFrom(type) ||
                short.class.isAssignableFrom(type) ||
                long.class.isAssignableFrom(type) ||
                byte.class.isAssignableFrom(type);
    }

    /**
     * 判断是否浮点类型
     * @param type
     * @return
     */
    public static boolean isDoubleType(Class<?> type){
        return Number.class.isAssignableFrom(type) ||
                float.class.isAssignableFrom(type) ||
                double.class.isAssignableFrom(type);
    }

    /**
     * Check whether value within specific range
     * @param val Value to check
     * @param ranges Ranges, if only one given, check whether within size.
     * @return
     */
    public static boolean between(int val, int... ranges){
        if(ranges == null || ranges.length == 0) return true;
        if(ranges.length == 1) return val <= ranges[0];
        int max = Arrays.stream(ranges).max().getAsInt();
        int min = Arrays.stream(ranges).min().getAsInt();
        return min <= val && val <= max;
    }

    /**
     * Ensure value within [min(ranges),max(ranges))
     * @param val
     * @param ranges
     * @return
     */
    public static int fixed(int val, int... ranges) {
        int min = 0;
        int max = 0;
        if(ranges.length == 1) return max = ranges[0];
        max = Arrays.stream(ranges).max().getAsInt();
        min = Arrays.stream(ranges).min().getAsInt();
        if(val < min) val= min;
        else if(val > max) val = max;
        return val;
    }

    /**
     * 变成4位小数的值.
     * @param val
     *
     * @return
     */
    public static String currency(Number val){
        return scale(val, 4, BigDecimal.ROUND_CEILING).toPlainString();
    }

    /**
     * * 变成任意位小数的值.
     * @param val 数值
     * @param scale 小数点后位数
     * @param roundingMode 四舍五入模式
     * @return
     */
    public static BigDecimal scale(Number val, int scale, int roundingMode){
        if(val == null) return BigDecimal.ZERO;
        return (val instanceof BigDecimal ? (BigDecimal) val : new BigDecimal(val.doubleValue())).setScale(scale,roundingMode);
    }

    /**
     * 转换为整型的值
     * @param finalScore
     * @param defaultVal
     * @return
     */
    public static int intValue(Number finalScore, int defaultVal) {
        return finalScore == null ? defaultVal : finalScore.intValue();
    }

    /**
     * 转换为双精度浮点型的值
     * @param finalScore
     * @param defaultVal
     * @return
     */
    public static double doubleValue(Number finalScore, double defaultVal) {
        return finalScore == null ? defaultVal : finalScore.doubleValue();
    }

    /**
     * 转换为长整型型的值
     * @param finalScore
     * @param defaultVal
     * @return
     */
    public static long longValue(Number finalScore, long defaultVal) {
        return finalScore == null ? defaultVal : finalScore.longValue();
    }

    /**
     * 批量转Long
     * @param defaultVal
     * @param vals
     * @return
     */
    public static long[] longValues(long defaultVal,String... vals) {
        return Arrays.stream(vals)
                .mapToLong(val -> NumberUtils.toLong(val,defaultVal))
                .toArray();
    }

    public static Long toLong(Object val) {
        return toLong(val,null);
    }

    public static Long toLong(Object val, Long l) {
        if(val == null) return l;
        return val instanceof Number ?
                ((Number) val).longValue() :
                new BigDecimal(ShenStrings.str(val)).longValue();
    }

    public static Integer toInteger(Object val) {
        return toInteger(val,null);
    }

    public static Integer toInteger(Object val, Integer dval) {
        if(val == null) return dval;
        return val instanceof Number ?
                ((Number) val).intValue() :
                new BigDecimal(ShenStrings.str(val)).intValue();
    }

    public static Long[] toLongs(Iterable<?> vals) {
        List<Long> ls = Lists.newArrayList();
        for(Object val : vals) {
            Long l = toLong(val,0l);
            if(l == null) continue;
            ls.add(l);
        }
        return ls.toArray(new Long[0]);
    }

    public static Integer[] toIntegers(Iterable<?> vals) {
        List<Integer> nums = Lists.newArrayList();
        for(Object val : vals) {
            Integer i = toInteger(val,0);
            if(i == null) continue;
            nums.add(i);
        }
        return nums.toArray(new Integer[0]);
    }

    public static Integer[] toIntegers(Object[] vals) {
        List<Integer> nums = Lists.newArrayList();
        Object val;
        for(int i=0;i < vals.length;i++){
            val = toInteger(vals[i],0);
            if(val == null) continue;
             nums.add((Integer) val);
        }
        return nums.toArray(new Integer[0]);
    }

    /**
     * 尝试把对象转换为长整型
     * @param vals
     * @return
     */
    public static Long[] toLongs(Object[] vals) {
        List<Long> longs = Lists.newArrayList();
        Object val;
        for(int i=0;i < vals.length;i++){
            val = toLong(vals[i],0l);
            if(val == null) continue;
            longs.add((Long) val);
        }
        return longs.toArray(new Long[0]);
    }
}
