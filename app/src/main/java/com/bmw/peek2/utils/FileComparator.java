package com.bmw.peek2.utils;

import java.io.File;
import java.util.Comparator;

/**
 * Created by admin on 2017/4/14.
 */

public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File file, File t1) {
        if(file.lastModified() >= t1.lastModified())
        {

            return -1;
        }else/* if(file.lastModified() < t1.lastModified())*/
        {
            return 1;
        }
      /*  else {
            String fileName1 = file.getName();
            String fileName2 = t1.getName();
            fileName1 = fileName1.substring(0, fileName1.lastIndexOf("."));
            fileName2 = fileName2.substring(0, fileName2.lastIndexOf("."));
            try {
                int flag = Long.valueOf(fileName2).compareTo(Long.valueOf(fileName1));
                return flag;
            } catch (NumberFormatException e) {
                *//*if(fileName1.contains("_") && fileName2.contains("_")) {
                    int resultDataCompare = lastNumberCompare(fileName1, fileName2);

                    if (resultDataCompare != -100)
                        return resultDataCompare;
                }*//*
                LogUtil.error("文件排序：error: " + e);
            }
            return fileName2.compareTo(fileName1);
        }*/

    }

    private int lastNumberCompare(String fileName1, String fileName2) {
        try {
            int flag = Long.valueOf(
                    fileName1.substring(fileName1.lastIndexOf("_"),fileName1.length())
            ).compareTo(
                    Long.valueOf(
                           fileName2.substring(fileName2.lastIndexOf("_"),fileName2.length())
                    )
            );
            return flag;
        }catch (NumberFormatException e){
            LogUtil.error("日期排序 Error："+e.toString());
        }
        return -100;
    }
}
