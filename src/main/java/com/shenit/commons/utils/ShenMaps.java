package com.shenit.commons.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * Map utils
 * Created by jgnan on 27/12/2016.
 */
public final class ShenMaps {
    private static final Logger LOG = LoggerFactory.getLogger(ShenMaps.class);

    /**
     * Put all key - value pairs to map.
     * @param map Map to operate on
     * @param kvs Key value pairs
     * @return
     */
    public static <K extends Comparable, V> Map<K, V> putAll(Map<K, V> map, Object...kvs){
        if(map == null || kvs == null || kvs.length == 0) return map;
        for(int i=0;i<kvs.length;i+=2){
            map.put((K) kvs[i],i + 1 < kvs.length ? (V) kvs[i+1] : null);
        }
        return map;
    }

    /**
     * 递增值
     * @param map
     * @param field
     * @param val
     * @param <K>
     * @return
     */
    public static <K> Map<K,Double> incr(Map<K,Double> map, K field, Double val){
        if(map ==null) return map;
        map.put(field, map.getOrDefault(field, 0d) + val);
        return map;
    }



    /**
     * 把单条记录转换为指定类型
     * @param row
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T convert(Map<String, Object> row, Class<T> clazz) {
        Field[] fields= clazz.getDeclaredFields();
        boolean constructorModified = false;
        Constructor<T> constructor = null;

        try {
            constructor = clazz.getDeclaredConstructor();
            if(!constructor.isAccessible()){
                constructorModified = true;
                constructor.setAccessible(true);
            }
            T instance = constructor.newInstance();
            SerializedName serializedName;
            JsonProperty jsonProp;
            Type type;
            String name,val;
            for (Field f : fields) {
                if (Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) continue;
                serializedName = f.getDeclaredAnnotation(SerializedName.class);
                jsonProp = f.getDeclaredAnnotation(JsonProperty.class);
                name = serializedName != null ? serializedName.value() :
                        (jsonProp != null ? jsonProp.value() : f.getName());
                val = (String) row.get(name);
                type = f.getGenericType();
                if (f.getType().isAssignableFrom(Integer.class) || f.getType().equals(int.class)) {
                    f.set(instance, NumberUtils.toInt(val, 0));
                } else if (f.getType().isAssignableFrom(Long.class) || f.getType().equals(long.class)) {
                    f.set(instance, NumberUtils.toLong(val, 0));
                } else if (f.getType().isAssignableFrom(Double.class) || f.getType().equals(double.class)) {
                    f.set(instance, NumberUtils.toDouble(val, 0d));
                } else if (f.getType().isAssignableFrom(Boolean.class) || f.getType().equals(boolean.class)) {
                    f.set(instance, ShenBooleans.bool(val));
                } else if (f.getType().isAssignableFrom(Byte.class) || f.getType().equals(byte.class)) {
                    f.set(instance, NumberUtils.toByte(val, (byte) 0));
                } else if (f.getType().isAssignableFrom(BigDecimal.class)) {
                    f.set(instance, new BigDecimal(val));
                } else if (f.getType().isAssignableFrom(String.class)) {
                    f.set(instance, val);
                } else if (f.getType().isAssignableFrom(Date.class)) {
                    f.set(instance, GsonUtils.parse(StringUtils.wrap(val, '\''), type));
                } else {
                    f.set(instance, GsonUtils.parse(val, type));
                }
            }
            return instance;
        }catch (Throwable t) {
            LOG.warn("[convert] Could not init object class -> {} due to exception", clazz, t);
        }finally{
            if(constructor != null && constructorModified) constructor.setAccessible(false);
        }

        return null;
    }


    /**
     * 把两个数组压缩成一个hash对象
     * @param fields 键数组
     * @param vals 值数组
     * @return
     */
    public static <K,V> Map<K,V> zip(K[] fields, V[] vals) {
        Map<K,V> result = new LinkedHashMap<>();
        for(int i = 0 ;i< fields.length;i++){
            result.put(fields[i], ShenArrays.get(vals,i,null));
        }
        return result;
    }

    /**
     * 把两个数组压缩成一个hash对象
     * @param fields 键数组
     * @param vals 值数组
     * @return
     */
    public static <K,V> Map<K,V> zip(List<K> fields, List<V> vals) {
        HashMap<K,V> result = new HashMap<>();
        for(int i = 0 ;i< fields.size();i++){
            result.put(fields.get(i), IterableUtils.get(vals,i));
        }
        return result;
    }

    /**
     * 获取一个键的值，如果没有则使用默认值，并且把最终结果回填到map中
     * @param map
     * @param key
     * @param defaultValue Default value to use if key's value not exists
     * @return
     */
    public static <K,V> V getSet(Map<K, V> map, K key, V defaultValue) {
        if(map == null) return defaultValue;
        V val = map.getOrDefault(key,defaultValue);
        if(map.get(key) == null) map.put(key,val);  //回填内容
        return val;
    }


    /**
     * 把JavaBean哈希化
     * @param bean
     * @return
     */
    public static Map<? extends String,?> hash(Object bean) {
        Map<String, Object> result = new HashMap<>();
        Class<?> clazz = bean.getClass();
        BeanDescriptor desp = new BeanDescriptor(clazz);
        for(Enumeration<String> names = desp.attributeNames();names.hasMoreElements();){
            String name = names.nextElement();
            result.put(name, desp.getValue(name));
        }
        return result;
    }
}
