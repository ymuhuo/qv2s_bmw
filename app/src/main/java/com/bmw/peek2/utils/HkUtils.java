package com.bmw.peek2.utils;

import android.media.audiofx.LoudnessEnhancer;
import android.util.Log;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.jna.HCNetSDKByJNA;
import com.bmw.peek2.jna.HCNetSDKJNAInstance;
import com.bmw.peek2.model.All_id_Info;
import com.bmw.peek2.model.Login_info;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CAMERAPARAMCFG_EX;
import com.hikvision.netsdk.NET_DVR_ELECTRONICSTABILIZATION;
import com.hikvision.netsdk.NET_DVR_HIDEALARM_V30;
import com.hikvision.netsdk.NET_DVR_NOISEREMOVE;
import com.hikvision.netsdk.NET_DVR_PICCFG_V30;
import com.hikvision.netsdk.NET_DVR_TIME;
import com.hikvision.netsdk.NET_DVR_VIDEOEFFECT;
import com.hikvision.netsdk.NET_DVR_WDR;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.UnsupportedEncodingException;

/**
 * Created by admin on 2017/9/4.
 */

public class HkUtils {


    public static NET_DVR_TIME getTime() {
        if (All_id_Info.getInstance().getM_iLogID() < 0)
            return null;

        NET_DVR_TIME net_dvr_time = new NET_DVR_TIME();
        HCNetSDK.getInstance().NET_DVR_GetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_GET_TIMECFG,
                All_id_Info.getInstance().getM_iChanNum(),
                net_dvr_time);
        return net_dvr_time;
    }


    public static boolean setTime(int year, int month, int day, int hour, int min, int second) {
        if (All_id_Info.getInstance().getM_iLogID() < 0) {
            BaseApplication.toast("未连接主机！");
            return false;
        }

        NET_DVR_TIME net_dvr_time = new NET_DVR_TIME();
        net_dvr_time.dwYear = year;
        net_dvr_time.dwMonth = month;
        net_dvr_time.dwDay = day;
        net_dvr_time.dwHour = hour;
        net_dvr_time.dwMinute = min;
        net_dvr_time.dwSecond = second;
        boolean isSuccess = HCNetSDK.getInstance().NET_DVR_SetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_SET_TIMECFG,
                All_id_Info.getInstance().getM_iChanNum(),
                net_dvr_time);
        if (!isSuccess) {
            LogUtil.error("海康时间设置出错，e = " + HCNetSDK.getInstance().NET_DVR_GetLastError());
            BaseApplication.toast("主机时间设置失败！");
            return isSuccess;
        }

        BaseApplication.toast("主机时间设置成功！");

        return isSuccess;
    }


    public static void setTongDaoName(String name, boolean isShowOsd) {
        if (All_id_Info.getInstance().getM_iLogID() < 0 || Login_info.getInstance().isBanTouShow())
            return;

        NET_DVR_PICCFG_V30 piccfg_v30 = new NET_DVR_PICCFG_V30();
        HCNetSDK.getInstance().NET_DVR_GetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_GET_PICCFG_V30,
                All_id_Info.getInstance().getM_iChanNum(),
                piccfg_v30);


        byte[] tongdaoNames = null;
        try {
            tongdaoNames = name.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (tongdaoNames != null) {
            for (int i = 0; i < piccfg_v30.sChanName.length; i++) {
                if (i < tongdaoNames.length)
                    piccfg_v30.sChanName[i] = tongdaoNames[i];
                else
                    piccfg_v30.sChanName[i] = 0;
            }
            piccfg_v30.dwShowChanName = 1;
            piccfg_v30.wShowNameTopLeftX = 32;
            piccfg_v30.wShowNameTopLeftY = 544;

            boolean isNotShowTime = BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_OSD_IS_NOT_SHOW_TIME_ON_DEVICE, false);

            if (isShowOsd) {
                if (isNotShowTime)
                    piccfg_v30.dwShowOsd = 0;
                else
                    piccfg_v30.dwShowOsd = 1;

            } else
                piccfg_v30.dwShowOsd = 0;

            piccfg_v30.byFontSize = 1;
            piccfg_v30.wOSDTopLeftX = 512;
            piccfg_v30.wOSDTopLeftY = 544;
        }

        boolean b = HCNetSDK.getInstance().NET_DVR_SetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_SET_PICCFG_V30,
                All_id_Info.getInstance().getM_iChanNum(),
                piccfg_v30);
        if (!b) {
            LogUtil.error("字符叠加标记：e= ", HCNetSDK.getInstance().NET_DVR_GetLastError());
        }

    }


    private static NET_DVR_CAMERAPARAMCFG_EX getNET_DVR_CAMERAPARAMCFG_EX() {
        All_id_Info all_id_info = All_id_Info.getInstance();
        NET_DVR_CAMERAPARAMCFG_EX net_dvr_cameraparamcfg_ex = new NET_DVR_CAMERAPARAMCFG_EX();
        boolean isGEt = false;
        try {
            isGEt = HCNetSDK.getInstance().NET_DVR_GetDVRConfig(all_id_info.getM_iLogID()
                    , 3368
                    , all_id_info.getM_iChanNum()
                    , net_dvr_cameraparamcfg_ex);
        } catch (Exception e) {
            LogUtil.error("Exception e = " + e.toString());
        }

        if (isGEt)
            return net_dvr_cameraparamcfg_ex;
        else
            return null;
    }


    private static boolean isSetNET_DVR_SetDVRConfig(NET_DVR_CAMERAPARAMCFG_EX ex) {
        boolean isSet = HCNetSDK.getInstance().NET_DVR_SetDVRConfig(All_id_Info.getInstance().getM_iLogID()
                , 3369
                , All_id_Info.getInstance().getM_iChanNum()
                , ex);
        return isSet;
    }


    public static boolean getKuanDongTai() {
        All_id_Info all_id_info = All_id_Info.getInstance();
        if (all_id_info.getM_iLogID() < 0) {
            return false;
        }

        NET_DVR_CAMERAPARAMCFG_EX net_dvr_cameraparamcfg_ex = getNET_DVR_CAMERAPARAMCFG_EX();
        if (net_dvr_cameraparamcfg_ex != null) {

//            getQiangguangyizhi(net_dvr_cameraparamcfg_ex);
            return getKuanDongTAi(net_dvr_cameraparamcfg_ex);
//            getFangDou(net_dvr_cameraparamcfg_ex);

        }


        return false;
    }

    private static void getFangDou(NET_DVR_CAMERAPARAMCFG_EX net_dvr_cameraparamcfg_ex) {
        NET_DVR_ELECTRONICSTABILIZATION net_dvr_electronicstabilization = net_dvr_cameraparamcfg_ex.struElectronicStabilization;
        LogUtil.log(" byEnable " + net_dvr_electronicstabilization.byEnable
                + "\n byLevel " + net_dvr_electronicstabilization.byLevel
                + "\n byRes " + net_dvr_electronicstabilization.byRes);
    }

    private static boolean getKuanDongTAi(NET_DVR_CAMERAPARAMCFG_EX net_dvr_cameraparamcfg_ex) {
        int enable = net_dvr_cameraparamcfg_ex.struWdr.byWDREnabled;
        if (enable == 1) {
            LogUtil.log("获取宽动态为启动状态");
            return true;
        } else {
            LogUtil.log("获取宽动态为关闭状态");
            return false;
        }
    }

    private static boolean getQiangguangyizhi(NET_DVR_CAMERAPARAMCFG_EX net_dvr_cameraparamcfg_ex) {
        NET_DVR_VIDEOEFFECT net_dvr_videoeffect = net_dvr_cameraparamcfg_ex.struVideoEffect;
        int enable = net_dvr_videoeffect.byEnableFunc;
        if ((enable & 0x0100) == 4) {
            LogUtil.log("获取强光抑制为启动状态");
            return true;
        } else {
            LogUtil.log("获取强光抑制为关闭状态");
            return false;
        }
    }

    public static boolean setKuanDongTai(boolean isOpen) {
        All_id_Info all_id_info = All_id_Info.getInstance();
        if (all_id_info.getM_iLogID() < 0) {
            return false;
        }
        NET_DVR_CAMERAPARAMCFG_EX net_dvr_cameraparamcfg_ex = getNET_DVR_CAMERAPARAMCFG_EX();
        if (net_dvr_cameraparamcfg_ex == null) {
            return false;
        }
        NET_DVR_WDR net_dvr_wdr = net_dvr_cameraparamcfg_ex.struWdr;
        if (isOpen) {
            net_dvr_wdr.byWDREnabled = 1;
            net_dvr_wdr.byWDRLevel1 = 50;
        } else {
            net_dvr_wdr.byWDREnabled = 0;
            net_dvr_wdr.byWDRLevel1 = 0;
        }
        net_dvr_cameraparamcfg_ex.struWdr = net_dvr_wdr;
        return isSetNET_DVR_SetDVRConfig(net_dvr_cameraparamcfg_ex);

//        NET_DVR_FOCUSMODE_CFG
    }

    public static boolean setDigtitalZoomMax(boolean isOpen) {
        All_id_Info all_id_info = All_id_Info.getInstance();
        if (all_id_info.getM_iLogID() < 0) {
            return false;
        }

        HCNetSDKByJNA hcNetSDKByJNA = HCNetSDKJNAInstance.getInstance();
        HCNetSDKByJNA.NET_DVR_FOCUSMODE_CFG net_dvr_focusmode_cfg = new HCNetSDKByJNA.NET_DVR_FOCUSMODE_CFG();

        IntByReference intByReference = new IntByReference();
        intByReference.setValue(76);
        boolean isGET = false;
        isGET = hcNetSDKByJNA.NET_DVR_GetDVRConfig(all_id_info.getM_iLogID()
                , HCNetSDKByJNA.NET_DVR_GET_FOCUSMODECFG
                , all_id_info.getM_iChanNum()
                , net_dvr_focusmode_cfg.getPointer()
                , 76
                , intByReference);
        net_dvr_focusmode_cfg.read();

        LogUtil.log("digtital = " + net_dvr_focusmode_cfg.byDigtitalZoom + " size " + net_dvr_focusmode_cfg.dwSize);
        if (isOpen)
            net_dvr_focusmode_cfg.byDigtitalZoom = 8;
        else
            net_dvr_focusmode_cfg.byDigtitalZoom = 1;
        if (!isGET)
            return false;
        boolean isSet = false;
        if (isGET) {
            net_dvr_focusmode_cfg.write();
            net_dvr_focusmode_cfg.setAutoWrite(true);
            LogUtil.log("digtital = " + net_dvr_focusmode_cfg.byDigtitalZoom);
            isSet = hcNetSDKByJNA.NET_DVR_SetDVRConfig(all_id_info.getM_iLogID()
                    , HCNetSDKByJNA.NET_DVR_SET_FOCUSMODECFG
                    , all_id_info.getM_iChanNum()
                    , net_dvr_focusmode_cfg.getPointer()
                    , net_dvr_focusmode_cfg.dwSize);
            net_dvr_focusmode_cfg.write();
        }


        LogUtil.log("digtital = " + net_dvr_focusmode_cfg.byDigtitalZoom);
        return isSet;
    }


    public static boolean getDigtitalZoomMax() {
        All_id_Info all_id_info = All_id_Info.getInstance();
        if (all_id_info.getM_iLogID() < 0) {
            return false;
        }

        HCNetSDKByJNA hcNetSDKByJNA = HCNetSDKJNAInstance.getInstance();
        HCNetSDKByJNA.NET_DVR_FOCUSMODE_CFG net_dvr_focusmode_cfg = new HCNetSDKByJNA.NET_DVR_FOCUSMODE_CFG();

        IntByReference intByReference = new IntByReference();
        intByReference.setValue(76);
        boolean isGET = false;
        isGET = hcNetSDKByJNA.NET_DVR_GetDVRConfig(all_id_info.getM_iLogID()
                , HCNetSDKByJNA.NET_DVR_GET_FOCUSMODECFG
                , all_id_info.getM_iChanNum()
                , net_dvr_focusmode_cfg.getPointer()
                , 76
                , intByReference);
        net_dvr_focusmode_cfg.read();
        if (net_dvr_focusmode_cfg.byDigtitalZoom == 8) {
            return true;
        } else {
            return false;
        }
    }


//        NET_DVR_WDR 宽动态
//        VIDEOEFFECT
//        NET_DVR_VIDEOEFFECT 强光抑制
//        ELECTRONICSTABILIZATION
//        NET_DVR_ELECTRONICSTABILIZATION 防抖
        /*HCNetSDK.getInstance().NET_DVR_GetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                16024,
                All_id_Info.getInstance().getM_iChanNum(),net_dvr_wdr
                );*/
}

