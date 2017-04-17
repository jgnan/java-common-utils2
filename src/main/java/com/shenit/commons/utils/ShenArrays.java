package com.shenit.commons.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Array utils.
 * @author jiangnan
 *
 */
public final class ShenArrays {

    /**
     * 随机排列数组中元素
     * @param array
     */
    public static void shuffle(int[] array) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    /**
     * 转换为长整型的数组
     * @param array 原数组
     * @param <T> 源类型
     * @return
     */
    public static <T> String[] strs(T[] array){
        return transfer(array,String::valueOf, String[]::new);
    }


    /**
     * 转换为整形的数组
     * @param array 原数组
     * @param <T> 源类型
     * @return
     */
    public static <T> Integer[] ints(T[] array){
        return transfer(array,
                v -> {
                    if(v == null) return 0;
                    else if(v instanceof Number) return ((Number)v).intValue();
                    else return NumberUtils.toInt(ShenStrings.str(v),0);
                }, Integer[]::new);
    }

    /**
     * 转换为字符串的数组
     * @param array 原数组
     * @param <T> 源类型
     * @return
     */
    public static <T> Long[] longs(T[] array){
        return transfer(array,
                v -> {
                    if(v == null) return 0l;
                    else if(v instanceof Number) return ((Number)v).longValue();
                    else return NumberUtils.toLong(ShenStrings.str(v),0l);
                }, Long[]::new);
    }

    /**
     * 转换为另一个类型的数组
     * @param array 原数组
     * @param func 转换函数
     * @param generator 数组生成函数
     * @param <T> 源类型
     * @param <V> 目标类型
     * @return
     */
    public static <T,V> V[] transfer(T[] array, Function<T,V> func, IntFunction<V[]> generator){
        return Arrays.stream(array).map(func).toArray(generator);
    }

    public static void shuffle(byte[] array) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            byte a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }
    /**
     * Shuffle an array directly.
     * @param array
     * @param <T>
     */
    public static <T> void shuffle(T[] array){
        Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    /**
     * Filter not empty array items.
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T[] notEmpty(T[] array){
        if(ArrayUtils.isEmpty(array)) return array;
        List<T> items = new ArrayList<>();
        for(int i=0;i<array.length; i++){
            if(array[i] != null) items.add(array[i]);
        }
        return items.toArray((T[]) Array.newInstance(
                array.getClass().getComponentType(),
                items.size()));
    }



    /**
     * Get data from index.
     * @param data
     * @param index
     * @return
     */
    public static byte get(byte[] data, int index) {
        return data == null || data.length <= index ? -1 :  data[index];
    }

    /**
     * 获取数组中第i个元素.如果超出范围返回默认值，如果为负数尝试进行一次修正.
     * @param fields
     * @param i
     * @param <T>
     * @return
     */
    public static <T> T get(T[] fields, int i) {
        return get(fields, i, null);
    }

    /**
     * 获取数组中第i个元素.如果超出范围返回默认值，如果为负数尝试进行一次修正.
     * @param fields
     * @param i
     * @param defaultVal
     * @param <T>
     * @return
     */
    public static <T> T get(T[] fields, int i, T defaultVal) {
        if(ArrayUtils.isEmpty(fields)) return defaultVal;
        //处理i的范围
        if(i < 0) i = Math.abs(fields.length + i);    //如果继续是负数就让他去吧。。。。
        return i >= fields.length ? defaultVal : fields[i];
    }

    /**
     * 检查数组的所有内容是否都为空
     * @param vals
     * @return
     */
    public static <T> boolean isEmpty(T[] vals) {
        if(ArrayUtils.isEmpty(vals)) return true;
        boolean result = true;
        for(Object val : vals){
            result &= val == null;
        }
        return result;
    }

    /**
     * 获取Long值
     * @param result
     * @param i 索引
     * @param l 默认值
     */
    public static Long getLong(Object[] result, int i, Long l) {
        return NumberUtils.toLong(ShenStrings.str(get(result,i)),l);
    }

    /**
     * 获取字符串
     * @param result
     * @param i
     * @return
     */
    public static String getString(Object[] result, int i) {
        return ShenStrings.str(get(result,i));
    }
}
