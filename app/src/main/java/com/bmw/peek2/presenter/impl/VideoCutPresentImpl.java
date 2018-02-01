package com.bmw.peek2.presenter.impl;

import android.text.TextUtils;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.jna.SystemTransformByJNA;
import com.bmw.peek2.jna.SystemTransformJNAInstance;
import com.bmw.peek2.model.All_id_Info;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.presenter.VideoCutPresenter;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.HkUtils;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.NetUtil;
import com.bmw.peek2.utils.UrlUtil;
import com.bmw.peek2.utils.WriteInfoUtil;
import com.bmw.peek2.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_TIME;

import org.MediaPlayer.PlayM4.Player;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/30.
 */
public class VideoCutPresentImpl implements VideoCutPresenter {

    private PreviewImpl preview;
    private boolean isRecord;
    private All_id_Info all_id_info;
    private String lastRecordPath;
    private SystemTransformByJNA.SYS_TRANS_PARA sys_trans_para;
    private boolean mIsAddKanban;
    private String videoPath;

    public VideoCutPresentImpl(PreviewImpl preview) {
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
        FileUtil.recordPathIsExist();
        FileUtil.capturePathIsExist();
        initStructure();
    }

    @Override
    public void record(String recordName, boolean isAddKanban, String kanbanPath) {
        mIsAddKanban = isAddKanban;
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (!isRecord && m_iPlayID < 0) {
            BaseApplication.toast("主机未连接！");
            return;
        }
        if (!isRecord) {
            if (!NetUtil.getInstance().isVideoConnect()) {
                if (preview != null) {
                    preview.record(0, false, null);
                }
                return;
            }
            LogUtil.log("自定义接口录像！");

            if (isAddKanban) {
                //开始看板录制
                //...
                StringBuilder sb = new StringBuilder();
                sb.append(FileUtil.getFileSavePath());
                sb.append(Login_info.local_kanban_path);
                String kanbanName = BaseApplication.context().getKanbanName();

                File file = new File(sb.toString() + kanbanName);
                if (file.exists()) {
                    int file_count = 0;
                    while (file.exists()) {
                        file_count++;
                        file = new File(sb.toString() + kanbanName + "_" + file_count);
                    }

                    kanbanName = kanbanName + "_" + file_count;
                    BaseApplication.context().setKanbanName(kanbanName);
                }

                sb.append(kanbanName);
                sb.append(".avi");
                videoPath = sb.toString();


                initRecordParam(sb.toString());
                Login_info.isAddKanban = true;
                startRecordBack(null);
                /*if (preview != null) {
                    preview.record(0, true);
                }
                isRecord = true;*/
            } else {
//                String desPath = getDesPath(name, place, startWell, endWell, true);
                File file = new File(recordName + ".avi");
                if (file.exists()) {
                    int file_count = 0;
                    while (file.exists()) {
                        file_count++;
                        file = new File(recordName + "_" + file_count + ".avi");
                    }

                    recordName = recordName + "_" + file_count;
                }
                recordName = recordName + ".avi";
                videoPath = recordName;
                startRecord(recordName, kanbanPath);
            }

        } else {
            if (isAddKanban) {
                //结束看板录制
                //...
                Login_info.isAddKanban = false;
//                endRecordBack(true);
                endRecord();
            } else
                endRecord();
        }
    }


    private void endRecord() {
        Login_info.isPause = true;
        int stop_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
        int release_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
        if (stop_state != 0 || release_state != 0) {
            //结束录制失败
            endRecordBack(false);
        } else {
            endRecordBack(true);
        }

        FileUtil.updateSystemLibFile(videoPath);
//        FileUtil.updateSystemFileList(FileUtil.FILE_VIDEO);
    }

    private void endRecordBack(boolean b) {
        if (preview != null) {
            preview.record(1, b, null);
        }
        isRecord = false;
    }

    private void startRecordBack(String kanbanPathDes) {

        if (preview != null) {
            preview.record(0, true,videoPath);
        }
        isRecord = true;
        if (kanbanPathDes == null) {
            Login_info.isPause = false;
        } else {
            addKanbanToRecordFirst(kanbanPathDes);
        }
    }

    private void addKanbanToRecordFirst(final String kanbanPathDes) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file_index = new File(kanbanPathDes + "_index");

                File file = new File(kanbanPathDes);
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    BufferedInputStream bin = new BufferedInputStream(inputStream);

                    FileReader read_index = new FileReader(file_index);
                    BufferedReader bReader = new BufferedReader(read_index);

                    List<Integer> list = null;
//                    list = FileUtil.list;

                    list = new ArrayList<Integer>();
                    String str_index = null;
                    while ((str_index = bReader.readLine()) != null) {
                        list.add(Integer.valueOf(str_index));
                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (preview != null)
                            preview.kanbanAdding();
                        int a = list.get(i);
                        byte[] bytes = new byte[a];
                        bin.read(bytes);
                        int state = SystemTransformJNAInstance.
                                getInstance().
                                SYSTRANS_InputData(
                                        SystemTransformByJNA.handle.getValue(),
                                        0,
                                        bytes,
                                        bytes.length
                                );
//                        LogUtil.log("inputdata : " + Integer.toHexString(state) + "  " + bytes.length);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (preview != null)
                    preview.kanbanAddFinish();

                HCNetSDK.getInstance().NET_DVR_MakeKeyFrame(All_id_Info.getInstance().getM_iLogID(), All_id_Info.getInstance().getM_iChanNum());
                Login_info.isPause = false;
            }
        }).start();

    }


    private void startRecord(String desPath, String kanbanPathDes) {
        initRecordParam(desPath);
        //开始录制
        startRecordBack(kanbanPathDes);

    }

    private void initRecordParam(String desPath) {
        int create_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Create(SystemTransformByJNA.handle, sys_trans_para);
        if (create_state != 0) {
            startFailed();
            return;
        }

        int start_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Start(SystemTransformByJNA.handle.getValue(), null, desPath);
        if (start_state != 0) {
            startFailed();
            return;
        }
    }


    private void startFailed() {
        LogUtil.error("海康：抓拍：开始录制失败：", HCNetSDK.getInstance().NET_DVR_GetLastError());
        if (preview != null) {
            preview.record(0, false, null);
        }
    }


//
//    public void reRecord(int count) {
//        int m_iPlayID = all_id_info.getM_iPlayID();
//        HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID);
//        int clibrary = HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID,
//                2
//                , lastRecordPath + "_" + count + ".mp4");
//    }

    @Override
    public void capture(String captureName) {
        int m_iPort = all_id_info.getM_iPort();
        if (m_iPort < 0) {
            LogUtil.error("海康：抓拍：截图失败，未登录！", "");
            preview.iToast("截图失败！");
            return;
        }
        Player.MPInteger stWidth = new Player.MPInteger();
        Player.MPInteger stHeight = new Player.MPInteger();
        if (!Player.getInstance().getPictureSize(m_iPort, stWidth,
                stHeight)) {
            LogUtil.error("海康：抓拍：截图失败，getPictureSize failed with error code:"
                    , Player.getInstance().getLastError(m_iPort));
            return;
        }
        int nSize = 5 * stWidth.value * stHeight.value;
        final byte[] picBuf = new byte[nSize];
        final Player.MPInteger stSize = new Player.MPInteger();
        if (!Player.getInstance()
                .getJPEG(m_iPort, picBuf, nSize, stSize)) {
            LogUtil.error("海康：抓拍：截图失败，未登录！getBMP failed with error code:"
                    , Player.getInstance().getLastError(m_iPort));
            return;
        }

        File file = new File(captureName + ".jpg");
        int file_count = 0;
        if (file.exists()) {
            while (file.exists()) {
                file_count++;
                file = new File(captureName + "_" + file_count + ".jpg");
            }
            captureName = captureName + "_" + file_count;
        }
        final String captureNameFinal = captureName + ".jpg";

//        final String path = getDesPath(name, place, startWell, endWell, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream file = new FileOutputStream(captureNameFinal);
                    file.write(picBuf, 0, stSize.value);
                    file.close();
//                    MediaScannerConnection.scanFile(BaseApplication.context(), new String[]{path}, null, null);
                    preview.capture(captureNameFinal);
                    FileUtil.updateSystemLibFile(captureNameFinal);
//                    preview.iToast("截图已保存");
                } catch (Exception err) {
                    LogUtil.error("海康：抓拍：截图失败:", err.toString());
                }
            }
        }).start();


    }

    @Override
    public void release() {
        SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
        SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
        if (isRecord) {
            LogUtil.log("海康：抓拍：视图退出，停止录像", "");
//            preview = null;
            record(null, mIsAddKanban, null);
        }
    }


    public void initStructure() {

        sys_trans_para = new SystemTransformByJNA.SYS_TRANS_PARA();
        sys_trans_para.dwSrcInfoLen = 40;
        sys_trans_para.dwTgtPackSize = 8 * 1024;
        sys_trans_para.dwSrcDemuxSize = 0;
        sys_trans_para.enTgtType = 0x07;

        SystemTransformByJNA.PLAYSDK_MEDIA_INFO.ByReference media_info = SystemTransformByJNA.media_info;

        media_info.media_fourcc = 0x484B4D49;
        media_info.media_version = 0x0101;
        media_info.device_id = 0;
        media_info.audio_bits_per_sample = 16;                 //16bit
        media_info.audio_bitrate = 8000;
        media_info.system_format = 4;
        media_info.video_format = 0x0100;
        media_info.audio_format = 0x1000;
        media_info.audio_channels = 0;      //single channel
        media_info.audio_samplesrate = 0;
        media_info.write();
        sys_trans_para.pSrcInfo = media_info;
        sys_trans_para.write();


    }


}
