package com.bmw.peek2.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.annotation.RequiresPermission;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.android.AVIUtil;
import com.android.PsToH264Callback;
import com.android.PsToH264Util;
import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.jna.SystemTransformByJNA;
import com.bmw.peek2.jna.SystemTransformJNAInstance;
import com.bmw.peek2.model.All_id_Info;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.utils.ByteUtil;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.UrlUtil;
import com.bmw.peek2.utils.WriteInfoUtil;
import com.bmw.peek2.view.view.PlaySurfaceView;
import com.bmw.peek2.presenter.PreviewPresenter;
import com.bmw.peek2.view.ui.PreviewActivity;
import com.bmw.peek2.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by admin on 2016/9/29.
 */
public class PreviewPresentImpl implements PreviewPresenter {

    private SurfaceView surfaceView;
    private Context context;
    private All_id_Info all_id_info;
    private Login_info loginInfo;
    private long currentTime;
    private int errorTime;
    private boolean isAddSufaceHolder;
    private Intent intentBrocast;
    private WriteInfoUtil writeInfoUtil;


    public PreviewPresentImpl(Context context, PreviewImpl preview, SurfaceView surfaceView, OnPlayFailedListener listener) {
        this.context = context;
        loginInfo = Login_info.getInstance();
        this.surfaceView = surfaceView;
        all_id_info = All_id_Info.getInstance();
        this.listener = listener;

        intentBrocast = new Intent();
        intentBrocast.setAction(Constant.BROCAST_IS_RECORD_HAS_DATA);
    }

    private OnPlayFailedListener listener;

    public interface OnPlayFailedListener {
        void playFailed();
    }

    @Override
    public void surfaceAddCallback() {
        isAddSufaceHolder = true;
        surfaceView.getHolder().addCallback(new surfaceCallBackHolder());
    }

    @Override
    public void startSingle() {
        int m_iLogID = all_id_info.getM_iLogID();
        if (m_iLogID < 0) {
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            LogUtil.error("海康：PreviewPresentImpl: fRealDataCallBack object is failed!");
            return;
        }
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = all_id_info.getM_iStartChan();
        if (loginInfo.isVideo_zimaliu()) {
            previewInfo.dwStreamType = 1; // substream选择子码流；1
        } else {
            previewInfo.dwStreamType = 0;
        }
        previewInfo.bBlocked = 1;
//        previewInfo.byProtoType = 1;
//        previewInfo.dwLinkMode = 5;

        /*
        * lChannel
            通道号，目前设备模拟通道号从 1 开始，数字通道的起始通道号一般从 33 开始，具体取值在登录接口
            返回
        dwStreamType
            码流类型： 0-主码流， 1-子码流， 2-码流 3， 3-虚拟码流，以此类推
        dwLinkMode
            连接方式： 0- TCP 方式， 1- UDP 方式， 2- 多播方式， 3- RTP 方式， 4-RTP/RTSP， 5-RSTP/HTTP
        bBlocked
            0- 非阻塞取流， 1- 阻塞取流
        bPassbackRecord
            0-不启用录像回传， 1-启用录像回传。 ANR 断网补录功能，客户端和设备之间网络异常恢复之后自动将
            前端数据同步过来，需要设备支持。
        byPreviewMode
            预览模式： 0- 正常预览， 1- 延迟预览
        byProtoType
            应用层取流协议： 0- 私有协议， 1- RTSP 协议
        nRTSPPort
            RTSP 端口
        * */
//         NET_DVR_CLIENTINFO struClienInfo = new NET_DVR_CLIENTINFO();
//         struClienInfo.lChannel = m_iStartChan;
//         struClienInfo.lLinkMode = 0;
        // HCNetSDK start preview
        int m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID,
                previewInfo, fRealDataCallBack);
//         m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V30(m_iLogID,
//         struClienInfo, fRealDataCallBack, false);
        if (m_iPlayID < 0) {
            LogUtil.error("海康：PreviewPresentImpl: NET_DVR_RealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            listener.playFailed();
            return;
        }
        all_id_info.setM_iPlayID(m_iPlayID);
        LogUtil.log("海康：PreviewPresentImpl: NetSdk Play sucess" + m_iPlayID);

        if (!isAddSufaceHolder)
            surfaceAddCallback();


        LogUtil.log("海康：预览完成！");

    }

    @Override
    public void stopSingle() {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (m_iPlayID < 0) {
            LogUtil.error("海康：PreviewPresentImpl: stopSingle:m_iPlayID < 0");
//            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            LogUtil.error("海康：PreviewPresentImpl: StopRealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
//            return;
        }

        all_id_info.setM_iPlayID(-1);
        stopSinglePlayer();

        LogUtil.log("海康：退出预览成功！");
    }

    @Override
    public void release() {

    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType,
                                          byte[] pDataBuffer, int iDataSize) {

                long time = System.currentTimeMillis() - currentTime;
                if (time > 500)
                    LogUtil.log("海康时间：" + time);

                currentTime = System.currentTimeMillis();

                // player channel 1
//                if(time<500)
                processRealData(1, iDataType, pDataBuffer,
                        iDataSize, Player.STREAM_REALTIME);

            }
        };
        return cbf;
    }


    public void processRealData(int iPlayViewNo, int iDataType,
                                byte[] pDataBuffer, int iDataSize, int iStreamMode) {

        int m_iPort = all_id_info.getM_iPort();

        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) {
                return;
            }
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                LogUtil.error("海康：PreviewPresentImpl: getPort is failed with: "
                        + Player.getInstance().getLastError(m_iPort));
                if (listener != null)
                    listener.playFailed();
                return;
            }
//            LogUtil.log("海康：PreviewPresentImpl: getPort succ with: " + m_iPort);
            all_id_info.setM_iPort(m_iPort);
            if (iDataSize > 0) {
                if (!Player.getInstance().setStreamOpenMode(m_iPort,
                        iStreamMode)) // set stream mode
                {
                    LogUtil.log("海康：PreviewPresentImpl: setStreamOpenMode failed");
//                    return;
                }
                /*if (!Player.getInstance().openStream(m_iPort, pDataBuffer,
                        iDataSize, 600 * 1024)) // startPlayVideo stream
                {
                    preview.ierror("PreviewPresentImpl: openStream failed");
                    return;
                }*/

                if (!Player.getInstance().openStream(m_iPort, pDataBuffer,
                        iDataSize, 720 * 1280)) // startPlayVideo stream
                {
                    LogUtil.error("海康：PreviewPresentImpl: openStream failed");
                    if (listener != null)
                        listener.playFailed();
                    return;
                }

                if (!Player.getInstance().setDisplayBuf(m_iPort, 1)) {
                    LogUtil.error("海康：设置播放缓冲区最大缓冲帧数！" + Player.getInstance().getLastError(m_iPort));
                }

//                LogUtil.log("海康：缓冲区剩余数据：" + Player.getInstance().getSourceBufferRemain(m_iPort));
                if (Player.getInstance().resetSourceBuffer(m_iPort)) {
//                    LogUtil.log("海康：清空缓冲区所有剩余数据！");
                }

//                LogUtil.log("海康：获取播放缓冲区：" + Player.getInstance().getDisplayBuf(m_iPort));
                if (Login_info.getInstance().isYingJieMa()) {
                    if (Player.getInstance().setMaxHardDecodePort(0)) {
                        LogUtil.log("设置最大硬解码路数为0！");

                    }
                    if (Player.getInstance().setHardDecode(m_iPort, 1)) {
                        LogUtil.log("启用硬解码优先！");
                    }
                }

//                if (Player.getInstance().setDisplayBuf(m_iPort, 30)) {
//
//                    preview.ilog("设置播放缓冲区最大缓冲帧数20帧！");
//                }
                if (!Player.getInstance().play(m_iPort,
                        surfaceView.getHolder())) {
                    LogUtil.error("海康：PreviewPresentImpl: play failed,error code:" + Player.getInstance().getLastError(m_iPort));
                    if (listener != null) {
                        listener.playFailed();
                    }
                    return;
                }
                if (!Player.getInstance().playSound(m_iPort)) {
                    LogUtil.error("海康：PreviewPresentImpl: playSound failed with ierror code:"
                            + Player.getInstance().getLastError(m_iPort));
//                    return;
                }
            }
        } else {
            if (m_iPort == -1)
                return;

            if (!Login_info.isPause)
                recordInputData(pDataBuffer, iDataSize);
            if (Login_info.isAddKanban)
                kanbanInputData(pDataBuffer, iDataSize);
            else if (writeInfoUtil != null) {
                kanbanRelease();
            }

            playerInputData(m_iPort, pDataBuffer, iDataSize);

        }

    }

    private void kanbanRelease() {
        writeInfoUtil.release();
        writeInfoUtil = null;
    }

    private void kanbanInputData(byte[] pDataBuffer, int iDataSize) {
        if (writeInfoUtil == null)
            initWriteInfoUtil();
        try {
            writeInfoUtil.writeByteToFile(pDataBuffer, iDataSize);
            writeInfoUtil.writeIntergeToFile(iDataSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*int state = SystemTransformJNAInstance.
                getInstance().
                SYSTRANS_InputData(
                        SystemTransformByJNA.handle.getValue(),
                        0,
                        pDataBuffer,
                        iDataSize
                );
        LogUtil.log("aaaaaa record state = "+ state);*/

//        sendBrocastInputData();
    }

    private void initWriteInfoUtil() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.getFileSavePath());
        sb.append(Login_info.local_kanban_path);
        String fileName = BaseApplication.context().getKanbanName();
        int fileAdd = 1;
        while (FileUtil.isFileExist(sb.toString(), fileName)) {
            fileName += "_" + fileAdd;
            fileAdd++;
        }
        sb.append(fileName);
        String baseName = sb.toString();
        writeInfoUtil = new WriteInfoUtil(baseName, baseName + "_index");
        sendBrocastInputData();
    }

    private void playerInputData(int m_iPort, byte[] pDataBuffer, int iDataSize) {
        if (!Player.getInstance().inputData(m_iPort, pDataBuffer,
                iDataSize) && Player.getInstance()
                .getLastError(m_iPort) == 11) {

            for (int i = 0; i < 500; i++) {

                if (Player.getInstance().resetSourceBuffer(m_iPort)) {
                    LogUtil.log("海康：清空缓冲区所有剩余数据！");
                }

                if (Player.getInstance().inputData(m_iPort,
                        pDataBuffer, iDataSize)) {
                    break;
                }


                if (i % 100 == 0) {
                    LogUtil.error("海康：PreviewPresentImpl: inputData failed with: "
                            + Player.getInstance()
                            .getLastError(m_iPort) + ", i:" + i);
                    errorTime++;
                    if (errorTime > 3 && listener != null) {
                        listener.playFailed();
                        errorTime = 0;
                        break;
                    }
                }


                   /* try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }*/
            }
        } else {
//                recordBySelf(pDataBuffer, iDataSize);

        }
    }

    private void recordInputData(byte[] pDataBuffer, int iDataSize) {
        int state = SystemTransformJNAInstance.
                getInstance().
                SYSTRANS_InputData(
                        SystemTransformByJNA.handle.getValue(),
                        0,
                        pDataBuffer,
                        iDataSize
                );

        sendBrocastInputData();
//                LogUtil.log(" input_state = " + Integer.toHexString(state));
    }

    private void sendBrocastInputData() {
        intentBrocast.putExtra(Constant.KEY_IS_RECORD_HAS_DATA, 1);
        context.sendBroadcast(intentBrocast);
    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        int m_iPort = all_id_info.getM_iPort();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            LogUtil.error("海康：PreviewPresentImpl: stopSinglePlayer is failed!,error code:" + Player.getInstance().getLastError(m_iPort));
//            return;
        }

        if (!Player.getInstance().setHardDecode(m_iPort, 0)) {
            LogUtil.error("海康：PreviewPresentImpl: stopHardDecode is failed!");
//            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            LogUtil.error("海康：PreviewPresentImpl: stopSinglePlayer closeStream is failed!");
//            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            LogUtil.error("海康：PreviewPresentImpl: stopSinglePlayer freePort is failed!" + m_iPort);
//            return;
        }

        all_id_info.setM_iPort(-1);
    }

    private class surfaceCallBackHolder implements SurfaceHolder.Callback {

        // @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            int m_iPort = all_id_info.getM_iPort();
            LogUtil.log("海康：PreviewPresentImpl: surface is created" + m_iPort);
            if (-1 == m_iPort) {
                return;
            }
            Surface surface = holder.getSurface();
            if (true == surface.isValid()) {
                if (false == Player.getInstance()
                        .setVideoWindow(m_iPort, 0, holder)) {
                    LogUtil.error("海康：PreviewPresentImpl: Player setVideoWindow failed!" + Player.getInstance().getLastError(m_iPort));
                }
            }
        }

        // @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        // @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            int m_iPort = all_id_info.getM_iPort();
            LogUtil.log("海康：PreviewPresentImpl: Player setVideoWindow release!" + m_iPort);
            if (-1 == m_iPort) {
                return;
            }
            if (true == holder.getSurface().isValid()) {
                if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                    LogUtil.error("海康：PreviewPresentImpl: Player setVideoWindow failed!");
                }
            }
        }
    }
}

