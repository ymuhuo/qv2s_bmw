package com.bmw.peek2.presenter.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.bmw.peek2.Constant;
import com.bmw.peek2.model.All_id_Info;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.presenter.HCNetSdkLogin;
import com.bmw.peek2.presenter.PreviewPresenter;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.NetUtil;
import com.bmw.peek2.utils.singleThreadUtil.FinalizableDelegatedExecutorService;
import com.bmw.peek2.utils.singleThreadUtil.RunnablePriority;
import com.bmw.peek2.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.lidroid.xutils.task.PriorityRunnable;

import org.MediaPlayer.PlayM4.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/9/28.
 */
public class HCNetSdkLoginImpl implements HCNetSdkLogin {

    private final Toast mToast;
    private PreviewImpl viewImpl;
    private All_id_Info all_id_info;
    private PreviewPresenter previewPresenter;
    private boolean isStop;
    private Login_info loginInfo;
    private ExecutorService cachedThreadPool;
    private boolean isRecord;


    public HCNetSdkLoginImpl(final Context context, PreviewImpl viewImpl, final SurfaceView surfaceView) {
        this.viewImpl = viewImpl;
        initSDK();
        loginInfo = Login_info.getInstance();
        all_id_info = All_id_Info.getInstance();
        cachedThreadPool = Executors.newCachedThreadPool();
        mToast = Toast.makeText(context, "连接失败！请检查wifi是否连接！", Toast.LENGTH_SHORT);
        previewPresenter = new PreviewPresentImpl(context, viewImpl, surfaceView, new PreviewPresentImpl.OnPlayFailedListener() {
            @Override
            public void playFailed() {
                reLoginHaiKang();
                if (surfaceView != null && !surfaceView.isShown() && Player.getInstance().getLastError(All_id_Info.getInstance().getM_iPort()) == 0){
                    surfaceView.post(new Runnable() {
                        @Override
                        public void run() {
                            surfaceView.setVisibility(View.VISIBLE);
                            NetUtil.getInstance().justNetConnect();
                            LogUtil.log("justNetConnect after preview false!");
                            Intent intent = new Intent(Constant.BROCAST_PREVIEW_SHOW);
                            intent.putExtra(Constant.KEY_ERROR_PAUSE_CLOSE,true);
                            context.sendBroadcast(intent);
                        }
                    });
                }
            }
        });

    }

    public void reLoginHaiKang() {

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (All_id_Info.getInstance().getM_iLogID() >= 0) {
                    logout();
                    isStop = true;
                    sleep(5000);
                }
                isStop = false;
                connectDeviceOperator();
            }
        });
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initSDK() {
        // setFilePath net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            LogUtil.error("海康：HCNetSDK setFilePath is failed!");
            viewImpl.initHCNetSdkFaild();
            return;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",
                true);
        LogUtil.log("海康：HCNetSDK setFilePath is success!");
    }


    public boolean login() {
        int m_iLogID = loginNormalDevice();
        if (m_iLogID < 0) {
            return false;
        }
        mToast.cancel();
        LogUtil.log("海康：登录成功！");
        all_id_info.setM_iLogID(m_iLogID);
        if (viewImpl != null)
            viewImpl.loginSuccessful();


        return true;

    }

    @Override
    public void logout() {
        previewPresenter.stopSingle();
        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(all_id_info.getM_iLogID())) {
            LogUtil.error("海康： 退出登录失败!");
            return;
        }
        LogUtil.log("海康：退出登录成功!");
        all_id_info.setM_iLogID(-1);
        all_id_info.resetData();
    }

    @Override
    public void connectDevice() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                connectDeviceOperator();
            }
        });

    }

    private void connectDeviceOperator() {
        while (All_id_Info.getInstance().getM_iLogID() < 0 && !isStop) {
            if (NetUtil.getInstance().pingHost(Login_info.base_video_ip) == 0) {
                synchronized (this) {
                    if (All_id_Info.getInstance().getM_iLogID() >= 0)
                        break;
                    if (login()) {
                        sleep(100);
                        previewPresenter.startSingle();
                    }
                }
            } else {
                sleep(3000);
            }
        }
        LogUtil.log("预览线程结束");
    }

    @Override
    public void release() {
        // release net SDK resource
        if (cachedThreadPool != null)
            cachedThreadPool.shutdownNow();
        mToast.cancel();
        isStop = true;
        logout();
        HCNetSDK.getInstance().NET_DVR_Cleanup();
        LogUtil.log("海康：release");
    }


    private int loginNormalDevice() {

        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            LogUtil.error("海康：HKNetDvrDeviceInfoV30对象创建失败!");
            return -1;
        }

        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(loginInfo.getVideo_ip(), loginInfo.getVideo_port(),
                loginInfo.getVideo_account(), loginInfo.getVideo_password(), m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            LogUtil.error("海康：登录失败!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError() + " " + iLogID);
            return -1;
        }

        all_id_info.setM_oNetDvrDeviceInfoV30(m_oNetDvrDeviceInfoV30);
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            all_id_info.setM_iStartChan(m_oNetDvrDeviceInfoV30.byStartChan);
            all_id_info.setM_iChanNum(m_oNetDvrDeviceInfoV30.byChanNum);
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            all_id_info.setM_iStartChan(m_oNetDvrDeviceInfoV30.byStartDChan);
            all_id_info.setM_iChanNum(m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256);
        }


        return iLogID;
    }
}
