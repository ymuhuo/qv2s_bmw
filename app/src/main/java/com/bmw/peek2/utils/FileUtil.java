package com.bmw.peek2.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.jna.SystemTransformJNAInstance;
import com.bmw.peek2.model.FileInfo;
import com.bmw.peek2.model.Login_info;
import com.lidroid.xutils.util.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.bmw.peek2.utils.LogUtil.log;

/**
 * Created by admin on 2017/4/27.
 */

public class FileUtil {


    public static void updateSystemLibFile(String path) {
        log("updateSystemLibFile path = " + path);
        MediaScannerConnection.scanFile(BaseApplication.context(), new String[]{path}, null, null);
    }


    public static int getFileExistNum(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return 0;
        File file = new File(filePath);
        if (!file.exists())
            return 0;
        File[] files = file.listFiles();
        return files.length;
    }

    public static String getFileItemExit(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return null;
        int num = 1;
        while (isFileExist(filePath, "00" + num)) {
            num++;
        }
        return "00" + num;
    }

    public static boolean isFileExist(String filePath, String fileName) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(fileName))
            return false;
        File file = new File(filePath);
        if (!file.exists())
            return false;
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().equals(fileName))
                return true;
        }
        return false;
    }

    public static String getFileSavePath() {
        List<String> list = getRealExtSDCardPath(BaseApplication.context());
        if (!Login_info.getInstance().isSaveToExSdcard()) {
            return list.get(0);
        } else {
            if (list.size() > 1)
                return list.get(list.size() - 1);
            Login_info.getInstance().setSaveToExSdcard(false);
            return list.get(0);
        }
    }


    /**
     * 路径是否存在，不能存在则创建
     */
    /*public static   void pathIsExist() {
        recordPathIsExist();

    }*/

    public static void recordPathIsExist(){
        File file = new File(getFileSavePath() + Login_info.local_video_path);
        if (!file.exists()) {
            file.mkdirs();
            BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_VIDEO_FILE_COUNT,1).commit();
        }else if(file.listFiles()!=null && file.listFiles().length == 0){
            BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_VIDEO_FILE_COUNT,1).commit();
        }

        File file2 = new File(getFileSavePath() + Login_info.local_kanban_path);
        if (!file2.exists())
            file2.mkdirs();
    }

    public static void capturePathIsExist(){
        File file1 = new File(getFileSavePath() + Login_info.local_picture_path);
        if (!file1.exists()) {
            file1.mkdirs();
            BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_PICTURE_FILE_COUNT,1).commit();
        }else if(file1.listFiles()!=null && file1.listFiles().length == 0){
            BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_PICTURE_FILE_COUNT,1).commit();
        }
    }


    public static List<String> getRealExtSDCardPath(Context context) {
        List<String> sdcardList = new ArrayList<>();
        String[] allSdcard = getExtSDCardPath(context);

        String inlaySDcard = getSDPath();
        sdcardList.add(inlaySDcard);

        if (allSdcard.length == 0 || allSdcard.length == 1)
            return sdcardList;

        for (String sdPath : allSdcard) {
            if (sdPath.contains(inlaySDcard))
                continue;
            if (canSdcardWrite(sdPath)) {
                sdcardList.add(sdPath);
            } else {
                File[] sdcardDataLogFiles = getSdcardDataLog(context);
                if (sdcardDataLogFiles.length <= 1)
                    continue;
                String sdcardDataLog = getSDcardDataLog(sdPath, sdcardDataLogFiles);
                if (sdcardDataLog != null) {
                    sdcardList.add(sdcardDataLog);
                }
            }

        }

        return sdcardList;
    }

    private static String getSDcardDataLog(String sdPath, File[] sdcardDataLogFiles) {
        for (File file : sdcardDataLogFiles) {
            if (file != null)
                if (file.toString().contains(sdPath) && canSdcardWrite(file.toString()))
                    return file.toString();
        }
        return null;
    }


    private static boolean canSdcardWrite(String sdPath) {
        File file = new File(sdPath + "/a.txt");
        try {
            file.createNewFile();
            file.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String[] getExtSDCardPath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context
                .STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[]) invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getSDPath() {

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }

    public static String getDataPath() {
        return Environment.getDataDirectory().getPath();
    }

    private static File[] getSdcardDataLog(Context context) {
        File[] filearray = new File[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            filearray = context.getExternalFilesDirs(null);
        }
        return filearray;
    }

    public static List<Float> getDiskCapacity() {
        String path = getFileSavePath();
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<Float> diskSizeList = new ArrayList<>();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlockCount = stat.getBlockCount();
        long feeBlockCount = stat.getAvailableBlocks();
        float totleSize = (float) (blockSize * totalBlockCount) / (1024 * 1024 * 1024);
        float freeSize = (float) (blockSize * feeBlockCount) / (1024 * 1024 * 1024);
        float usedSize = totleSize - freeSize;
        totleSize = (float) (Math.round(totleSize * 100)) / 100;
        freeSize = (float) (Math.round(freeSize * 100)) / 100;
        usedSize = (float) (Math.round(usedSize * 100)) / 100;

        diskSizeList.add(totleSize);
        diskSizeList.add(freeSize);
        diskSizeList.add(usedSize);
        return diskSizeList;
    }

    public final static String KEY_SD = "EXT";
    public final static String KEY_USB = "USB";

    /**
     * 6.0获取外置sdcard和U盘路径，并区分
     *
     * @param mContext
     * @param keyword  SD = "内部存储"; EXT = "SD卡"; USB = "U盘"
     * @return
     */
    public static String getStoragePath(Context mContext, String keyword) {
        String targetpath = "";
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");

            Method getPath = storageVolumeClazz.getMethod("getPath");

            Object result = getVolumeList.invoke(mStorageManager);

            final int length = Array.getLength(result);

            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");


            for (int i = 0; i < length; i++) {

                Object storageVolumeElement = Array.get(result, i);

                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);

                String path = (String) getPath.invoke(storageVolumeElement);

                log(" userLabel = " + userLabel);
                if (userLabel != null && userLabel.contains(keyword)) {
                    targetpath = path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return targetpath;
    }


    public static boolean writeStringToFile(File file, boolean isAddToEnd, String string) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file, isAddToEnd);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(string, 0, string.length());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            fileWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void renameFile(String path, String oldname, String newname) {
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(path + oldname);
            File newfile = new File(path + newname);
            if (!oldfile.exists()) {
                return;//重命名文件不存在
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
            {
                int i = 1;
                do {
                    File file = new File(path + newname + "_" + i);
                    if (file.exists()) {
                        i++;
                    } else {
                        break;
                    }
                } while (true);
//                newfile = new File(path + newname + "_" + i);
//                oldfile.renameTo(newfile);

            } else {
                oldfile.renameTo(newfile);
            }
        } else {
            BaseApplication.toast("新文件名和旧文件名相同...");
        }
    }




    /**
     * 存储卡获取 指定文件
     *
     * @param context
     * @param extension
     * @return
     */
    public static List<FileInfo> getSpecificTypeFiles(Context context, String[] extension) {
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

        //内存卡文件的Uri
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };

        //构造筛选条件语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间降序条件（升序" ASC"）
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

        Cursor cursor = context.getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(0);
                    int logLayer = StringUtils.countMatches(data, '/');
                    if (logLayer <= 7) {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFilePath(data);

                        long size = 0;
                        try {
                            File file = new File(data);
                            size = file.length();
                            fileInfo.setSize(size);
                        } catch (Exception e) {

                        }
                        fileInfoList.add(fileInfo);
                    }
                } catch (Exception e) {
                    Log.i("FileUtils", "------>>>" + e.getMessage());
                }

            }
        }
        return fileInfoList;
    }


    /**
     * 转化完整信息的FileInfo
     *
     * @param context
     * @param fileInfoList
     * @param type
     * @return
     */
    public static List<FileInfo> getDetailFileInfos(Context context, List<FileInfo> fileInfoList, int type) {

        if (fileInfoList == null || fileInfoList.size() <= 0) {
            return fileInfoList;
        }

        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo != null) {
                fileInfo.setName(getFileName(fileInfo.getFilePath()));
                fileInfo.setSizeDesc(getFileSize(fileInfo.getSize()));

                fileInfo.setFileType(type);
            }
        }
        return fileInfoList;
    }

    public static String getFileName(String filePath) {
        if (filePath == null || filePath.equals("")) return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }


    /**
     * 小数的格式化
     */
    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");
    public static final DecimalFormat FORMAT_ONE = new DecimalFormat("####.#");

    public static String getFileSize(long size) {
        if (size < 0) { //小于0字节则返回0
            return "0B";
        }

        double value = 0f;
        if ((size / 1024) < 1) { //0 ` 1024 byte
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {//0 ` 1024 kbyte

            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {                  //0 ` 1024 mbyte
            value = (size * 100l / (1024l * 1024l * 1024l)) / 100f;
            return FORMAT.format(value) + "GB";
        }
    }

    public static boolean copyFile(String sourPath, String desPath) {
        if (sourPath == null || desPath == null)
            return false;
        File sourFile = new File(sourPath);
        if (!sourFile.exists())
            return false;
        File desFile = new File(desPath);
        try {

            if (!desFile.exists())
                desFile.createNewFile();
            FileOutputStream out = new FileOutputStream(desFile);
            FileInputStream in = new FileInputStream(sourFile);
            byte[] buf = new byte[1024];
            int length = -1;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            in.close();
            out.close();
            updateSystemLibFile(desPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean replaceImage(String sourPath, String desPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(sourPath);
        try {
            BitmapUtils.save(desPath, bitmap);
            updateSystemLibFile(desPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
