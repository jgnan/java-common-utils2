package com.shenit.commons.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ShenDates {
    private static final Logger LOG = LoggerFactory.getLogger(ShenDates.class);
    private static final int[] FIELDS = {Calendar.YEAR,Calendar.MONTH,Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE,Calendar.SECOND, Calendar.MILLISECOND};
	/** format string "yyyy-MM-dd HH:mm:ss SSS" */
	public static final String ALL = "yyyy-MM-dd HH:mm:ss SSS";
	/** format string "yyyy-MM-dd HH:mm:ss" */
	public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
	/** format string "yyyy-MM-dd" */
	public static final String YMD = "yyyy-MM-dd";
	/** date format for cookie expire field */
	public static final String COOKIE_DATE = "EEE, dd-MMM-yyyy HH:mm:ss zzz";
	/** Milli second to one day */
	public static final long ONE_DAY_MILLISEC = 1000 * 3600 * 24;
    public static DateTimeFormatter UTC_FORMMATER = DateTimeFormat.forPattern(YMDHMS);
    public static SimpleDateFormat FORMATTER = new SimpleDateFormat(YMDHMS);
	
	//默认使用格林威治时间
    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT");
    //DAY Field
    public static final int MILLISECOND = 0;
    public static final int SECOND = 1;
    public static final int MINUTE = 2;
    public static final int HOUR = 3;
    public static final int DAY = 4;
	/**
	 * 字符串转换为date格式（默认格式化{@link #YMDHMS}）
	 * @param date 字符串时间
	 * @return {@link Date} 默认返回当前时间
	 */ 
	public static Date getNowTime(String date) {
		return getDateTime(null, date);
	}

	/**
	 * 从时间转换为字符串（默认格式化{@link #YMDHMS}）
	 * @param date 默认当前时间 传入数据转换失败返回当前时间
	 * @return {@link String}
	 */
	public static String getNowTime(Date date) {
		return getDateTimeStr(null, null == date ? new Date() : date);
	}

	/**
	 * 获取当前时间字符串（默认格式化{@link #YMDHMS}）
	 * @return {@link String}
	 */
	public static String getDate() {
		return getDateTimeStr(YMD, new Date());
	}

	/**
	 * 从日期转换为字符串
	 * @param format 格式化字符(默认为{@link #YMDHMS})
	 * @param inDate 要转换的时间, <strong>如果 inDate==null 返回 ""</strong>
	 * @return {@link String}
	 */
	public static String getDateTimeStr(String format, Date inDate) {
		format = (StringUtils.isBlank(format) ? YMDHMS : format);
		return inDate == null ? "" : new SimpleDateFormat(format).format(inDate);
	}
	
	/**
	 * Get current timestamp string.
	 * @return
	 */
	public static String getNowStr(){
	    return getDateTimeStr(YMDHMS, new Date());
	}

	/**
	 *  从字符串转成日期
	 * @param format 格式化字符(默认为{@link #YMDHMS})
	 * @param dataStr 字符串时间
	 * @return {@link Date} 转换失败返回当前时间
	 */
	public static Date getDateTime(String format, String dataStr) {
		format = (StringUtils.isBlank(format) ? YMDHMS : format);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(dataStr);
		} catch (ParseException e) {
			return new Date();
		}

    }
	
	/**
	 * 获取当天时间
	 * @param isStart=true当前开始时间,false当天结束时间
	 * @author folo 2015年5月2日下午6:26:51
	 */
	public static long getNow(Calendar calendar, boolean isStart){
        calendar.set(Calendar.HOUR_OF_DAY, isStart ? 0 : 23);  
        calendar.set(Calendar.MINUTE, isStart ? 0 : 59);  
        calendar.set(Calendar.SECOND, isStart ? 0 : 59);
        calendar.set(Calendar.MILLISECOND, isStart ? 0 : 999);
        return calendar.getTimeInMillis();
	}
	
	/**
	 * 获取数字格式的年月日 例如：19990909
	 * @author folo 2015年5月6日下午9:15:20
	 */
	public static int getDate(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year*10000 + month*100 + day;
    }

    /**
     * 根据timestamp生成
     * @param ts
     * @return
     */
    public static Date date(Long ts) {
        return ts == null ? null : new Date(ts);
    }
    
    /**
     * 判断日期对象是否在当前时间偏移指定时间量后的日期之前
     * @param date 日期
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果date在当前日期的偏移量之前，返回true，否则返回false
     */
    public static boolean noMoreThan(Date date, int range, int type){
        return date == null ? false : compare(date.getTime(), range, type, -1);
    }
    
    /**
     * 判断日期对象是否在当前时间偏移指定时间量后的日期之后
     * @param date 日期
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果date在当前日期的偏移量之后，返回true，否则返回false
     */
    public static boolean moreThan(Date date, int range, int type){
        return date == null ? false : compare(date.getTime(), range, type, -1);
    }
    
    /**
     * 判断日期对象是否和当前时间偏移指定时间量后的日期相等
     * @param ts 时间戳
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果ts在当前日期的偏移量之前，返回true，否则返回false
     */
    public static boolean noMoreThan(long ts, int range, int type){
        return compare(ts, range, type, -1);
    }
    
    /**
     * 判断日期对象是否和当前时间偏移指定时间量后的日期相等
     * @param ts 时间戳
     * @param range 对比值
     * @param type 对比类型，和对比值决定偏移范围， 取值为MILLISECOND, SECOND, MINUTE, HOUR, 或者 DAY
     * @return 如果ts在当前日期的偏移量之后，返回true，否则返回false
     */
    public static boolean moreThan(long ts, int range, int type){
        return compare(ts, range, type, 1);
    }
    
    
    /*
     * 对比时间搓和值之间的区别
     * @param ts - 要比较的时间搓
     * @param range - 要对比的值， 需要和type结合使用
     * @param compareType - 对比类型， > 0 判断ts是否大于range; <0 判断ts是否小于range
     */
    private static boolean compare(long ts, int range, int type, int compareType){
        int ms = 1;
        long now = System.currentTimeMillis();
        switch(type){
        case DAY: ms *= 24;
        case HOUR: ms *= 60;
        case MINUTE: ms *= 60;
        case MILLISECOND:
        case SECOND: ms *= 1000;
        }
        return compareType > 0 ? ts > now + range * ms : ts < now + range * ms; 
    }

    public static String format(Date date){
        if(date == null) date = new Date();
        return FORMATTER.format(date);
    }

    /**
     * Format Date
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern){
        if(date == null) date = new Date();
        if (StringUtils.isEmpty(pattern)) pattern = YMDHMS;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /**
     * Parse string to date.
     *
     * @param str
     * @param patterns
     * @return
     */
    public static Date parse(String str, String... patterns) {
        if (StringUtils.isEmpty(str)) return null;
        if (ArrayUtils.isEmpty(patterns)) patterns = new String[]{YMDHMS};
        try {
            return DateUtils.parseDate(str, patterns);
        } catch (ParseException e) {
            LOG.warn("[parse] Could not parse \"{}\" with patterns -> {}", str, patterns);
        }
        return null;
    }

    /**
     * Check whether <i>base</i> is within <i>range</i>
     *
     * @param base
     * @param minMax First is min, second is max.
     * @return
     */
    public static boolean between(Date base, Date... minMax) {
        if (base == null) return false;
        int len = ArrayUtils.getLength(minMax);
        if (len == 0) return true;
        Date min = minMax[0];
        Date max = len > 1 ? minMax[1] : null;
        boolean between = min == null ? true : base.compareTo(min) >= 0;
        if (max != null) between &= base.compareTo(max) <= 0;
        return between;
    }

    /**
     * Get minimum date in dates
     *
     * @param dates Dates to compare
     * @return
     */
    public static Date min(Date... dates) {
        dates = ShenArrays.notEmpty(dates);
        return ArrayUtils.isEmpty(dates) ? null :
                Arrays.stream(dates)
                        .filter(item -> item != null)
                        .min((a, b) -> a.compareTo(b))
                        .get();
    }

    /**
     * Get maximum date in dates
     *
     * @param dates Dates to compare
     * @return
     */
    public static Date max(Date... dates) {
        dates = ShenArrays.notEmpty(dates);
        return ArrayUtils.isEmpty(dates) ? null :
                Arrays.stream(dates)
                        .filter(item -> item != null)
                        .max((a, b) -> a.compareTo(b))
                        .get();
    }

    /**
     * Get utc date
     *
     * @param date
     * @return
     */
    public static Date utc(Date date) {
        if (date == null) return null;
        return utc(date.getTime());
    }

    /**
     * Get current utc date by timezone
     *
     * @param tz
     * @return
     */
    public static Date utc(TimeZone tz) {
        return utc(new Date(), tz);
    }

    /**
     * Get utc date
     *
     * @param date
     * @return
     */
    public static Date utc(Date date, TimeZone tz) {
        if (date == null) return null;
        return utc(date.getTime(), tz);
    }

    public static Date utc(String timeZone) {
        DateTime dt = new DateTime();
        dt.withZone(DateTimeZone.forID(timeZone));
        return dt.toDate();
    }

    /**
     * get utc date
     *
     * @param ts
     * @return
     */
    public static Date utc(long ts, TimeZone tz) {
        return new Date(ts + (tz == null ? TimeZone.getDefault() : tz).getRawOffset());
    }

    public static Date utc(Date ctime, String timezoneGtm8) {
        DateTime dt = new DateTime(ctime);
        dt.withZone(DateTimeZone.forID(timezoneGtm8));
        return dt.toDate();
    }

    /**
     * get utc date
     *
     * @param ts
     * @return
     */
    public static Date utc(long ts) {
        return new DateTime(ts,DateTimeZone.forID("UTC")).toDate();
    }

    /**
     * get utc date
     *
     * @return
     */
    public static Date utc() {
        return UTC_FORMMATER.withZone(DateTimeZone.getDefault()).parseDateTime(utcStr()).toDate();
    }

    public static String utcStr(String timeZone) {
        DateTime dt = new DateTime();
        return UTC_FORMMATER.withZone(DateTimeZone.forID(timeZone)).print(dt);
    }

    public static String utcStr() {
        return utcStr((java.util.Date)null);
    }

    public static String utcStr(java.util.Date from) {
        DateTime dt = from == null ? new DateTime() : new DateTime(from.getTime());
        return DateTimeFormat.forPattern(YMDHMS).withZone(DateTimeZone.UTC).print(dt);
    }


    public static Date local() {
        return new DateTime().toDate();
    }

    /**
     * Exchange minutes into seconds.
     *
     * @param amount
     * @return
     */
    public static int minutes(int amount) {
        return seconds(Calendar.MINUTE, amount);
    }

    /**
     * Exchange minutes into milliseconds.
     *
     * @param amount
     * @return
     */
    public static int minutesMilli(int amount) {
        return minutes(amount) * 1000;
    }

    /**
     * Exchange hours into milliseconds.
     *
     * @param amount
     * @return
     */
    public static int hoursMilli(int amount) {
        return hours(amount) * 1000;
    }

    /**
     * Exchange hours into seconds.
     *
     * @param amount
     * @return
     */
    public static int hours(int amount) {
        return seconds(Calendar.HOUR, amount);
    }

    /**
     * Exchange days into seconds.
     *
     * @param amount
     * @return
     */
    public static int days(int amount) {
        return seconds(Calendar.DATE, amount);
    }

    /**
     * 获取两个时间之间的时间差
     *
     * @param from 开始计算时间，为空表示当前时间
     * @param to   开始计算结束时间， 为空表示当前时间
     * @return
     */
    public static int distance(Date from, Date to) {
        if (from == null) from = new Date();
        if (to == null) to = new Date();
        return (int) (to.getTime() - from.getTime());
    }


    /**
     * 换算为秒.
     *
     * @param field  换算字段. 接受范围为：Calendar.DAT, Calendar.HOUR,  Calendar.MINUTE, Calendar.SECOND
     * @param amount 数值，要求 > 0
     * @return
     */
    public static int seconds(int field, int amount) {
        if (amount < 1) return 0;
        int ts = amount;
        switch (field) {
            case Calendar.DATE:
                ts *= 24;
            case Calendar.HOUR:
                ts *= 60;
            case Calendar.MINUTE:
                ts *= 60;
        }
        return ts;
    }

    /**
     * 转换为毫秒.
     *
     * @param field
     * @param amount
     * @return
     */
    public static int ts(int field, int amount) {
        return seconds(field, amount) * 1000;
    }

    public static Date date(Integer... fields) {
        Calendar cal = calendar();
        int val;
        for (int i = 0; i < FIELDS.length; i++) {
            val = ShenArrays.get(fields, i, (Integer) 0);
            if (val > 0 && i == 1) val = val - 1; //special process for month
            cal.set(FIELDS[i], val);
        }
        return cal.getTime();
    }

    /**
     * 获取指定日期类型的时间. 如果日期为空返回0
     * @param time
     */
    public static long getTime(Date time) {
        return time == null ? 0 : time.getTime();
    }

    /**
     * 判断目标时间是否在当前时间之前
     * @param val
     * @param baseline
     * @return
     */
    public static boolean isBefore(Date val, Date baseline) {
        long ts = getTime(val);
        return ts > 0 && (ts - (baseline == null ? System.currentTimeMillis() : getTime(baseline))) < 0;
    }

    /**
     * 批量设置值
     * @param date
     * @param fieldsAndValues
     * @return
     */
    public static Date setFields(Date date, int... fieldsAndValues){
        if(date == null) date = new Date();
        Calendar cal = calendar();
        cal.setTime(date);
        for(int i=0;i<fieldsAndValues.length;i+=2){
            int field = fieldsAndValues[i];
            int val = i + 1  == fieldsAndValues.length ?
                    (field == Calendar.MONTH ?  0 : 1) :
                    fieldsAndValues[i+1];
            cal.set(field,val);
        }
        return cal.getTime();
    }

    /**
     * 批量获取值
     * @param date
     * @param fields
     * @return
     */
    public static Integer[] getFields(Date date, int... fields){
        if(date == null) date = new Date();
        Calendar cal = calendar();
        cal.setTime(date);
        Integer[] vals = new Integer[fields.length];
        for(int i=0;i<fields.length;i++){
            vals[i] = cal.get(fields[i]);
        }
        return vals;
    }

    /**
     * 清除时间段的值
     * @param date
     * @return
     */
    public static Date purgeTime(Date date){
        if(date == null) date = new Date();
        return purge(date,Calendar.HOUR_OF_DAY,Calendar.MINUTE,Calendar.SECOND,Calendar.MILLISECOND);
    }

    /**
     * 获取指定日期所在周数
     * @param hourSpan
     * @return
     */
    public static Date firstDayOfWeek(Integer hourSpan){
        return firstDayOfWeek(null,hourSpan);
    }


    /**
     * 获取指定日期所在周数
     * @param date
     * @return
     */
    public static Date firstDayOfWeek(Date date){
        return firstDayOfWeek(date,null);
    }
    /**
     * 获取指定日期所在周数
     * @param date
     * @param hourSpan
     * @return
     */
    public static Date firstDayOfWeek(Date date,Integer hourSpan){
        if(hourSpan == null) hourSpan = 0;
        if(date == null) date = utcDate(hourSpan);
        else date = add(date,Calendar.HOUR_OF_DAY,hourSpan);
        return setFields(date,Calendar.DAY_OF_WEEK,calendar().getFirstDayOfWeek());
    }

    /**
     * 获取指定日期对应的指定项目中的第一天.
     * @param field 指定字段
     * @param date 指定日期，获取改日期对应的字段
     * @return
     */
    public static Date firstDateOf(Date date ,int field) {
        if(date == null) date = new Date();
        Calendar cal = calendar();
        cal.setTime(date);
        switch(field){
            case Calendar.DAY_OF_MONTH:
                cal.set(Calendar.DAY_OF_MONTH,1);
                break;
            case Calendar.DAY_OF_WEEK:
                //受calendar()影响，不再指定
//                cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                break;
            case Calendar.DAY_OF_YEAR:
                cal.set(Calendar.DAY_OF_YEAR,1);      //周日视为第一天
                break;
        }

        return purgeTime(cal.getTime());
    }

    /**
     * 获取指定日期对应的指定项目中的最后一天.
     * @param field 指定字段
     * @param date 指定日期，获取改日期对应的字段
     * @return
     */
    public static Date lastDateOf(Date date ,int field) {
        if(date == null) date = new Date();
        Calendar cal = calendar();
        cal.setTime(date);
        switch(field){
            case Calendar.DAY_OF_MONTH:
                cal.add(Calendar.MONTH,1);
                cal.set(Calendar.DAY_OF_MONTH,1);
                cal.add(Calendar.DAY_OF_YEAR,-1);
                break;
            case Calendar.DAY_OF_WEEK:
                cal.add(Calendar.WEEK_OF_MONTH,1);
                cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);      //周日视为第一天
                cal.add(Calendar.DAY_OF_YEAR,-1);
                break;
            case Calendar.DAY_OF_YEAR:
                cal.add(Calendar.YEAR,1);
                cal.set(Calendar.DAY_OF_YEAR,1);      //周日视为第一天
                cal.add(Calendar.DAY_OF_YEAR,-1);
                break;
        }

        return purgeTime(cal.getTime());
    }

    /**
     * 添加某个字段的值
     * @param date
     * @param fieldsAndValues
     * @return
     */
    public static Date add(Date date, int... fieldsAndValues){
        if(date == null) date = new Date();
        Calendar cal = calendar();
        cal.setTime(date);
        for(int i=0;i<fieldsAndValues.length;i+=2){
            int field = fieldsAndValues[i];
            int val = i + 1  == fieldsAndValues.length ? 0 :fieldsAndValues[i+1];
            cal.add(field,val);
        }
        return cal.getTime();
    }

    public static Calendar calendar() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }

    /**
     * 返回指定日期的秒数
     * @param date
     * @return
     */
    public static int seconds(Date date) {
        return (int) (getTime(date) /  1000);
    }

    /**
     * 只返回对应时区的0点对应的utc时间
     * @hourSpan 小时偏移量
     * @return
     */
    public static Date utcDate(int hourSpan) {
        return  utc(
                purgeTime(
                        add(utc(),Calendar.HOUR_OF_DAY, hourSpan)));
    }

    /**
     * 返回相对UTC而言的系统当前时区偏移量.
     * @return
     */
    public static int utcHourSpan() {
        return TimeZone.getDefault().getRawOffset() / 3600_000;
    }

    /**
     * get utc date
     * @param hourOffset 時區間隔
     * @return
     */
    public static Date utcWithHourOffset(int hourOffset) {
        return utcWithHourOffset(null,8);
    }

    /**
     * get utc date
     * @param date 非UTC時間
     * @param hourOffset 時區間隔
     * @return
     */
    public static Date utcWithHourOffset(Date date, int hourOffset) {
        date = date == null ? utc() : utc(date);
        return add(date,Calendar.HOUR_OF_DAY,hourOffset);
    }

    /**
     * 净化指定范围的值
     * @param date
     * @param fields
     * @return
     */
    public static Date purge(Date date, int... fields) {
        if(date == null) date = new Date();
        for(int field : fields){
            switch(field){
                case Calendar.MILLISECOND: date = setFields(date,Calendar.MILLISECOND,0);break;
                case Calendar.SECOND: date = setFields(date,Calendar.SECOND,0);break;
                case Calendar.MINUTE: date = setFields(date,Calendar.MINUTE,0);break;
                case Calendar.HOUR: case Calendar.HOUR_OF_DAY: date = setFields(date,Calendar.HOUR_OF_DAY,0);break;
                case Calendar.DAY_OF_MONTH: date = setFields(date,Calendar.DAY_OF_MONTH,1);break;
                case Calendar.DAY_OF_WEEK: date = setFields(date,Calendar.DAY_OF_WEEK,Calendar.MONDAY);break;
                case Calendar.MONTH: date = setFields(date,Calendar.MONTH,Calendar.JANUARY);break;
                //不支持年...
            }
        }
        return date;
    }
    
}
