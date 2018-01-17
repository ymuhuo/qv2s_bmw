package com.bmw.peek2.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/30.
 */
public class UrlUtil {


    public static String getFileName() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static String getTimeYMd(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static String getTimeYMdhms(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日 HH:mm:ss SSS");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
/*
    public static String getSDPath() {

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }*/


    //得到文件夹下所有文件列表
    public static List<File> getFileUtils(String path) {
        File file = new File(path);
        if (!file.exists() || file.isFile())
            return null;
        File[] files = file.listFiles();
        if (files.length > 0) {
            List<File> list = new ArrayList<File>();
            for (File f : files) {
                if (f.isFile())
                    list.add(f);
            }
            file = null;
            files = null;
            return list;
        } else {
            return null;
        }
    }


    public String formatTime(Long ms) {
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
            if (hour < 10) {
                sb.append("0").append(hour).append(":");
            } else
                sb.append(hour).append(":");
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
}
