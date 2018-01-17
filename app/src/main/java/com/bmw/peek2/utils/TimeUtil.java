package com.bmw.peek2.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by admin on 2017/6/3.
 */

public class TimeUtil {

    public static String formatTime(Long ms,boolean isShowHour) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day).append(":");
        }
        if (hour >= 0) {
            if(isShowHour || hour!=0) {
                if (hour < 10) {
                    sb.append("0").append(hour).append(":");
                } else
                    sb.append(hour).append(":");
            }
        }
        if (minute >= 0) {
            if (minute < 10) {
                sb.append("0").append(minute).append(":");
            } else
                sb.append(minute).append(":");
        }
        if (second >= 0) {
            if (second < 10) {
                sb.append("0").append(second);
            } else
                sb.append(second);
        }
        /*if(milliSecond > 0) {
            sb.append(milliSecond).append(":");
        }*/
        return sb.toString();
    }


    public static  void setAutoDateTime(Context context,int checked){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.Global.putInt(context.getContentResolver(),
                    Settings.Global.AUTO_TIME, checked);
        }
    }

    public static  void setSysDate(Context context,int year,int month,int day){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        long when = c.getTimeInMillis();

        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    public static void setSysTime(Context context,int hour,int minute,int second){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();

        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    public static String getSecond(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }


}
