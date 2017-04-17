/***********************************************************************************************************************
 * 
 * Copyright (C) 2013, 2014 by huanju (http://www.yy.com)
 * http://www.yy.com/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
package com.shenit.commons.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.BoundType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.shenit.commons.annos.IgnoreField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON压缩工具
 * @author jiangnan
 *
 */
public final class GsonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(GsonUtils.class);

	private static final TypeToken<Map<String,Object>> MAP_TYPE = new TypeToken<Map<String,Object>>(){};
	private static Map<Class<?>,Field<?>[]> REGISTERED_FIELDS = new HashMap<>();
	private static final Gson CUSTOMIZED;
	static {
        CUSTOMIZED = createBuilder()
                .registerTypeAdapter(Boolean.class, new BooleanDeserializer())
                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                }.getType(), new MapDeserializerDoubleAsIntFix())
                .registerTypeAdapter(boolean.class, new BooleanDeserializer())
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(Range.class, new RangeCodec())
                .create();
    }

	private static final GsonBuilder createBuilder(){
		return new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.disableHtmlEscaping()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);
	}

	/**
	 * Format object to json
	 * @param obj Object to format
	 * @return
	 */
	public static String format(Object obj){
		return obj instanceof String ? (String) obj : CUSTOMIZED.toJson(obj);
	}

	/**
	 * Parse json to instance
	 * @param str
	 * @param _class 类型
	 * @param <T>
	 * @return
	 */
	public static <T> T parse(String str, Class<T> _class){
		return StringUtils.isEmpty(str) ? null : CUSTOMIZED.fromJson(str,_class);
	}


	/**
	 * Parse to json
	 * @param str Json string
	 * @param type Type to cast
	 * @param <T>
	 * @return
	 */
	public static <T> T parse(String str, TypeToken<T> type){
		try {
			return StringUtils.isEmpty(str) ? null : CUSTOMIZED.fromJson(str, type.getType());
		}catch(Throwable t){
			if(LOG.isInfoEnabled()) LOG.info("[parse] Could not parse string -> {}",str);
		}
		return null;
	}


	/**
	 * 转换为Map类型.
	 * @param data
	 * @return
	 */
	public static Map<String, Object> map(String data) {
		Map<String,Object> map = parse(data,MAP_TYPE);
		return map == null ? Maps.newHashMap() : map;
	}

	/**
	 * 获取序列化时使用的字段集合.
	 * @param clazz
	 * @return
	 */
	public static String[] serializeFieldNames(Class<?> clazz){
		if(REGISTERED_FIELDS.containsKey(clazz)) {
			List<String> fieldNames = Lists.newArrayList();
			for(Field field : REGISTERED_FIELDS.get(clazz)){
				fieldNames.add(field.name);
			}
			return fieldNames.toArray(new String[0]);
		}

		serializeFields(clazz); //register first
		return serializeFieldNames(clazz);
	}


	/**
	 * 获取序列化时使用的字段集合.
	 * @param clazz
	 * @return
	 */
	public static Field<?>[] serializeFields(Class<?> clazz){
		if(REGISTERED_FIELDS.containsKey(clazz)) return REGISTERED_FIELDS.get(clazz);
		java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
		List<Field<?>> fs = Lists.newArrayList();
		SerializedName serializedName;
		JsonProperty jsonProp;
		IgnoreField ignore;
		for(java.lang.reflect.Field field : fields) {
			if (field == null) continue;
			serializedName = field.getAnnotation(SerializedName.class);
			jsonProp = field.getAnnotation(JsonProperty.class);
			ignore = field.getAnnotation(IgnoreField.class);
			if(ignore != null) continue;
			String name = null;
			if (serializedName != null) {
				name = serializedName.value();
			} else if (jsonProp != null) {
				name = jsonProp.value();
			} else {
				name = Modifier.isStatic(field.getModifiers()) ? null : field.getName();
			}
			if(name == null) continue;


			Default defVal = field.getDeclaredAnnotation(Default.class);
			fs.add(new Field(name, field.getName(),field.getType(), defVal == null ? null : defVal.value()));
		}
		Field<?>[] fsArray = fs.toArray(new Field<?>[0]);
		REGISTERED_FIELDS.put(clazz,fsArray);
		return fsArray;
	}

	public static <T> T parse(String val, Type type) {
		return CUSTOMIZED.fromJson(val,type);
	}

	public static class Field<T>{
		public Field(String name, String originName, Class<T> type,String defaultValue){
			this.name = name;
			this.originName = originName;
			this.type=  type;
			this.defaultValue = defaultValue;
		}
		public String name;
		public String originName;
		public Class<T> type;
		public String defaultValue;

		@Override
		public boolean equals(Object obj) {
			return obj != null && Field.class.isInstance(obj) &&
					(name != null && name.equals(((Field)obj).name)) ||
					(originName != null && name.equals(((Field)obj).originName));
		}

		@Override
		public String toString() {
			return StringUtils.wrap(name,'`');
		}
	}


	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface Default{
		String value();
	}

	/**
	 * For boolean fields
	 */
	private static class BooleanDeserializer implements JsonDeserializer<Boolean> {
		public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			String value = json.getAsString();
			Boolean result = Boolean.valueOf(value);
			if(StringUtils.isNumeric(value)) result = Integer.parseInt(value) != 0;
			return result;
		}
	}

	/**
	 * For Date/Time/Datetime fields
	 */
	private static class DateDeserializer implements JsonDeserializer<Date> {
		private static final String EMPTY_ZERO_DATE = "0000-00-00";
		private static final String[] COMMON_DATE_PATTERNS = {
				"yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss","yyyy-MM-dd HH:mm","yyyy/MM/dd HH:mm",  //for datetime
				"yyyy-MM-dd","yyyy/MM/dd",  //for date only
				"HH:mm:ss","HH:mm"};    //for time only
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			String value = json.getAsJsonPrimitive().getAsString();
			if(StringUtils.isNumeric(value)) {
				long ts = ShenNumbers.toLong(value, 0l);
				if (ts > 0) return new Date(value.length() == 10 ? ts * 1000 : ts);
			}
			if(value != null && value.startsWith(EMPTY_ZERO_DATE)) return null;
			try {
				return DateUtils.parseDate(value,COMMON_DATE_PATTERNS);
			} catch (ParseException e) {
				if(LOG.isInfoEnabled()) LOG.info("[DateDeserializer.deserialize] Deserialize date string [{}] failed with exception",value,e);
			}
			return null;
		}
	}


	/**
	 * For Date/Time/Datetime fields
	 */
	private static class RangeCodec implements JsonDeserializer<Range>{
		public Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			String value = null;
			if(json.isJsonArray()){
				//对于数组，作为闭区间解释
				JsonArray array = json.getAsJsonArray();
				StringBuilder sb = new StringBuilder();
				sb.append('[')
						.append(array.get(0));
				if(array.size() > 1) sb.append(',').append(array.get(1));
				sb.append(']');
				value = sb.toString();
			} else {
				value = json.getAsString();
			}
			boolean leftOpen, rightOpen;
			leftOpen = value.startsWith("(");
			rightOpen = value.startsWith(")");
			//因为用的少，无需缓存
			Pattern pattern = Pattern.compile("(?:\\[|\\()([\\d.]+)[,:]([\\d.]+)(?:\\]|\\))");
			Matcher m = pattern.matcher(value);
			if(!m.find()) return null;
			if(m.groupCount() == 0){
				//格式不正确，返回null
				LOG.warn("[RangeDeserializer.deserialize] Illegal format for range -> {}",value);
				return null;
			}
			Comparable start = NumberUtils.toDouble(m.group(1),0.0d);
			Comparable end = m.groupCount() > 1 ? NumberUtils.toDouble(m.group(2),0.0d) : start;
			return Range.range(
					start, leftOpen ? BoundType.OPEN : BoundType.CLOSED,
					end, rightOpen ? BoundType.OPEN : BoundType.CLOSED);
		}
	}

	private static class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>>{

		@Override  @SuppressWarnings("unchecked")
		public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return (Map<String, Object>) read(json);
		}

		public Object read(JsonElement in) {

			if(in.isJsonArray()){
				List<Object> list = new ArrayList<Object>();
				JsonArray arr = in.getAsJsonArray();
				for (JsonElement anArr : arr) {
					list.add(read(anArr));
				}
				return list;
			}else if(in.isJsonObject()){
				Map<String, Object> map = new LinkedTreeMap<String, Object>();
				JsonObject obj = in.getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
				for(Map.Entry<String, JsonElement> entry: entitySet){
					map.put(entry.getKey(), read(entry.getValue()));
				}
				return map;
			}else if( in.isJsonPrimitive()){
				JsonPrimitive prim = in.getAsJsonPrimitive();
				if(prim.isBoolean()){
					return prim.getAsBoolean();
				}else if(prim.isString()){
					return prim.getAsString();
				}else if(prim.isNumber()){
					Number num = prim.getAsNumber();
					// here you can handle double int/long values
					// and return any type you want
					// this solution will transform 3.0 float to long values
					if(Math.ceil(num.doubleValue())  == num.longValue())
						return num.longValue();
					else{
						return num.doubleValue();
					}
				}
			}
			return null;
		}
	}
}
