package com.soustock.model_train.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xuyufei on 2018/8/26.
 */
public class DateUtity {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

    public final static long MS_OF_DAY = 24 * 3600 * 1000;

    public static String dateToStr(Date dt){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(dt);
    }

    public static Date parseStr(String dtStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.parse(dtStr);
    }

    public static String getPreYear(String dtStr, int yearCount) throws ParseException {
        Date dt = parseStr(dtStr);
        Date dtOfPreYear = new Date(dt.getTime() - MS_OF_DAY * 365 * yearCount);
        return dateToStr(dtOfPreYear);
    }

    public static String getNextYear(String dtStr, int yearCount) throws ParseException {
        Date dt = parseStr(dtStr);
        Date dtOfPreYear = new Date(dt.getTime() + MS_OF_DAY * 365 * yearCount);
        return dateToStr(dtOfPreYear);
    }

    public static String getPreDay(String dtStr, int dayCount) throws ParseException {
        Date dt = parseStr(dtStr);
        Date dtOfPreYear = new Date(dt.getTime() - MS_OF_DAY * dayCount);
        return dateToStr(dtOfPreYear);
    }

    public static String getNextDay(String dtStr, int dayCount) throws ParseException {
        Date dt = parseStr(dtStr);
        Date dtOfPreYear = new Date(dt.getTime() + MS_OF_DAY * dayCount);
        return dateToStr(dtOfPreYear);
    }
}
