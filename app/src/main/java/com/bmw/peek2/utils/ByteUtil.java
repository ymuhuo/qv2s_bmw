package com.bmw.peek2.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by admin on 2017/4/25.
 */

public class ByteUtil {

/*
    * 将字节数组写入文件
    *
            * @param bytes
    * @param to
    * @return
            */
    public static void write(byte[] from, File to,int size) {
        if (from == null) {
            throw new NullPointerException("bytes is null");
        }
        if (to == null) {
            throw new NullPointerException("file is null");
        }

        DataOutputStream bos = null;

        try {
            bos = new DataOutputStream(new FileOutputStream(to,true));
            bos.write(from,0,size);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bos);
        }
    }

    public static void writeString(String msg,File file){
        if (msg == null) {
            throw new NullPointerException("bytes is null");
        }
        if (file == null) {
            throw new NullPointerException("file is null");
        }

        BufferedWriter bos = null;

        try {
            bos = new BufferedWriter(new FileWriter(file,true));
            bos.write(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bos);
        }
    }

    public static void printBuffer(byte[] pDataBuffer,int iDataSize,File file){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < iDataSize; i++) {
            if(i==0)
                builder.append("\n\n");
            builder.append(" ");
            int num = pDataBuffer[i] & 0xff;
            if (num >= 0 && num < 16)
                builder.append("0");
            builder.append(Integer.toHexString(num));
            builder.append(" ");
            if (i > 0 && (i+1) % 25 == 0)
                builder.append("\n");
        }
                LogUtil.log("海康数据：", builder.toString());
        if(file != null){
            writeString(builder.toString(),file);
        }
    }

    /**
     * 关闭流
     *
     * @param stream
     */
    public static void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
