package com.bmw.peek2.utils;

import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static com.bmw.peek2.utils.LogUtil.log;

/**
 * Created by admin on 2017/9/29.
 */

public class WriteInfoUtil {

    FileOutputStream out = null;
    BufferedOutputStream bout = null;
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
//    private static WriteInfoUtil writeInfoUtil;

//    public static WriteInfoUtil getInstance() {
//        if (writeInfoUtil == null) {
//            synchronized (WriteInfoUtil.class) {
//                writeInfoUtil = new WriteInfoUtil();
//            }
//        }
//        return writeInfoUtil;
//    }

    public WriteInfoUtil() {

    }


    public WriteInfoUtil(String filePath, String indexPath) {
        log("setFilePath");
        initIndexFile(indexPath);
        initFile(filePath);
    }

    public void release() {
        try {

            if (bout != null)
                bout.close();
            if (out != null)
                out.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (fileWriter != null)
                fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initIndexFile(String indexPath) {
        if(TextUtils.isEmpty(indexPath))
            return;
        File file = new File(indexPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFile(String filePath) {
        if(TextUtils.isEmpty(filePath))
            return;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out = new FileOutputStream(file, true);
            bout = new BufferedOutputStream(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeByteToFile(byte[] bytes, int len) throws IOException {
        bout.write(bytes, 0, len);
        bout.flush();
    }

    public void writeIntergeToFile(int i) throws IOException {

        String string = String.valueOf(i);
        bufferedWriter.write(string, 0, string.length());
        bufferedWriter.newLine();
        bufferedWriter.flush();
        fileWriter.flush();

    }
}
