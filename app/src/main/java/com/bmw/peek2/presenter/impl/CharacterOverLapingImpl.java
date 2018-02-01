package com.bmw.peek2.presenter.impl;

import android.text.TextUtils;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.jna.HCNetSDKByJNA;
import com.bmw.peek2.jna.HCNetSDKJNAInstance;
import com.bmw.peek2.model.All_id_Info;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.presenter.CharacterOverLapingPresenter;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.singleThreadUtil.FinalizableDelegatedExecutorService;
import com.bmw.peek2.utils.singleThreadUtil.RunnablePriority;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CONFIG;
import com.hikvision.netsdk.NET_DVR_PICCFG_V30;
import com.hikvision.netsdk.NET_DVR_SHOWSTRING_V30;
import com.hikvision.netsdk.NET_DVR_XML_CONFIG_INPUT;
import com.hikvision.netsdk.NET_DVR_XML_CONFIG_OUTPUT;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2017/3/13.
 */

public class CharacterOverLapingImpl implements CharacterOverLapingPresenter {

    private Executor mCacheThreadPool;
    private static NET_DVR_SHOWSTRING_V30 mShowStringV30;
    private NET_DVR_PICCFG_V30 piccfg_v30;
    private boolean isNotStop;
    private boolean isSetHead;
    private String[] mTitles;
    //    private int mBoom;
    private String mRanging;
    private boolean isStop;
    public ExecutorService singleThreadExecutor = new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>()));
    private double mJingdu;
    private double mWeidu;
//    private String mTastNameId;


    public CharacterOverLapingImpl() {
        isNotStop = true;
        mCacheThreadPool = Executors.newCachedThreadPool();
        piccfg_v30 = new NET_DVR_PICCFG_V30();
        mShowStringV30 = new NET_DVR_SHOWSTRING_V30();
        mRanging = "0.00";
        mTitles = new String[6];
        mRanging = "";
    }

    private void setMulNoHeader() {
        String[] items = new String[6];
        int j = 0;
        for (int i = 0; i < mTitles.length - 1; i++) {
            if (!TextUtils.isEmpty(mTitles[i])) {
                items[j] = mTitles[i];
                j++;
            }
        }
        items[5] = mTitles[5];
        setAllZiFuDieJia(items, new int[]{32, 64, 96, 128, 160, 544}, new int[]{1, 1, 1, 1, 0, 1});
    }

    @Override
    public void recordFinish() {
        if (BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_RECORDHEADER_ALWAYS_SHOW, Constant.IS_RECORDHEADER_ALWAYS_SHOW_DEFAULT)) {
            mTitles[0] = "";
            mTitles[1] = "";
        }
    }

    @Override
    public void setAlwaysShowInfo(String taskName, String taskId, String startWell, String endWell, String task_guancai, String task_diameter) {

        if (BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_RECORDHEADER_ALWAYS_SHOW, Constant.IS_RECORDHEADER_ALWAYS_SHOW_DEFAULT)) {
            StringBuilder firstBuilder = new StringBuilder();
            StringBuilder secondBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(taskName))
                firstBuilder.append(taskName);
            if (!TextUtils.isEmpty(taskId))
                firstBuilder.append("/").append(taskId);
            if (!TextUtils.isEmpty(startWell))
                secondBuilder.append(startWell);
            if (!TextUtils.isEmpty(endWell))
                secondBuilder.append("-").append(endWell);
            if (!TextUtils.isEmpty(task_guancai))
                secondBuilder.append(" ").append(task_guancai);
            if (!TextUtils.isEmpty(task_diameter))
                secondBuilder.append(" ").append(task_diameter).append("mm");
            mTitles[0] = firstBuilder.toString();
            mTitles[1] = secondBuilder.toString();
        }

    }

    @Override
    public void setSingleSign(String[] msg) {
        if (All_id_Info.getInstance().getM_iLogID() < 0 || msg == null || msg.length != 4)
            return;

        if (BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_RECORDHEADER_ALWAYS_SHOW, Constant.IS_RECORDHEADER_ALWAYS_SHOW_DEFAULT)) {
            mTitles[2] = msg[0];
            mTitles[3] = msg[1];
        } else {
            for (int i = 0; i < msg.length; i++) {
                mTitles[i] = msg[i];
            }
        }

        if (!isSetHead)
            mCacheThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    if (isNotStop) {
                        if (!isSetHead) {
                            setMulNoHeader();
//                            LogUtil.log("字符叠加标记已执行 ");
                        }
                    }
                }
            });
    }


    @Override
    public void setMultSign(final String[] strings) {
        if (All_id_Info.getInstance().getM_iLogID() < 0)
            return;
        isSetHead = true;
        isStop = false;
        singleThreadExecutor.execute(new RunnablePriority(4) {
            @Override
            public void run() {

                if (isNotStop) {
                    Login_info.getInstance().setBanTouShow(true);
                    setTongDaoName("", false);
                    setAllZiFuDieJia(strings, null, null);
                    long time = System.currentTimeMillis();
                    while (System.currentTimeMillis() - time < 9 * 1000) {
                        sleep(10);
                        if (isStop) {
                            break;
                        }
                    }
                    if (!isStop)
                        setNoRecordHead();

                }
            }
        });
    }


    @Override
    public void setNoRecordHead() {
        if (All_id_Info.getInstance().getM_iLogID() < 0)
            return;

        isStop = true;
        isSetHead = false;
        singleThreadExecutor.execute(new RunnablePriority(6) {
            @Override
            public void run() {
//                setAllZiFuDieJia(mTitles, null, null);
                sleep(50);
//                setSingleSign(mTitles);
                setMulNoHeader();
                setGpsAndRang(mRanging, mJingdu, mWeidu);
                setTongDaoName("", true);
                Login_info.getInstance().setBanTouShow(false);
//                setGpsAndRang(mRanging, mJingdu, mWeidu);
//                LogUtil.log("字符叠加清空版头已执行 ");
            }
        });
    }

    @Override
    public void setGpsAndRang(String ranging, double jingdu, double weidu) {
        if (All_id_Info.getInstance().getM_iLogID() < 0)
            return;
//        if (boom >= 0)
//            mBoom = boom;
        if (jingdu >= 0)
            mJingdu = jingdu;
        if (weidu >= 0)
            mWeidu = weidu;
        if (ranging != null)
            mRanging = ranging;
        if (!isSetHead)
            singleThreadExecutor.execute(new RunnablePriority(4) {
                @Override
                public void run() {
                    if (isNotStop) {
                        if (!isSetHead) {
                            setBottomBoomAndRanging(mRanging, mJingdu, mWeidu);
//                            LogUtil.log("字符叠加机芯测距已执行");
                        }
                    }
                }
            });

    }

    @Override
    public void release() {
        isNotStop = false;
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void setTongDaoName(String name, boolean isShowOsd) {
        if (All_id_Info.getInstance().getM_iLogID() < 0 )
            return;
        HCNetSDK.getInstance().NET_DVR_GetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_GET_PICCFG_V30,
                All_id_Info.getInstance().getM_iChanNum(),
                piccfg_v30);
      /*  LogUtil.log("标记：dwShowOsd = ",piccfg_v30.dwShowOsd);
        LogUtil.log("标记：dwShowChanName = ",piccfg_v30.dwShowChanName);
        LogUtil.log("标记：wShowNameTopLeftX = ",piccfg_v30.wShowNameTopLeftX);
        LogUtil.log("标记：wShowNameTopLeftY = ",piccfg_v30.wShowNameTopLeftY);
        LogUtil.log("标记：sChanName = ",piccfg_v30.sChanName.toString());
        LogUtil.log("标记：sChanName = ",piccfg_v30.sChanName.toString());*/


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


    public void setAllZiFuDieJia(String[] strings, int[] topLeftYs, int[] isShowIts) {
        if (strings == null)
            return;
//        LogUtil.log("字符叠加：添加字符叠加！");
        //如果版头不持续显示，标记置顶
       /* if (strings != null && TextUtils.isEmpty(strings[0])) {
            for (int i = 0; i < 2; i++) {
                if (TextUtils.isEmpty(strings[0])) {
                    for (int j = 0; j < strings.length - 2; j++) {
                        strings[j] = strings[j + 1];
                    }
                }
            }
        }*/

        for (int i = 0; i < 6; i++) {
            try {
                byte[] bytes = null;
                if (strings.length > i) {
                    if (strings[i] != null)
                        bytes = strings[i].getBytes("gb2312");
                    else
                        bytes = "".getBytes("gb2312");
                } else
                    bytes = new byte[44];
                for (int x = 0; x < mShowStringV30.struStringInfo[i].sString.length; x++) {
                    if (x < bytes.length)
                        mShowStringV30.struStringInfo[i].sString[x] = bytes[x];
                    else
                        mShowStringV30.struStringInfo[i].sString[x] = 0;

                }
                if (bytes.length <= 44)
                    mShowStringV30.struStringInfo[i].wStringSize = bytes.length;
                else
                    mShowStringV30.struStringInfo[i].wStringSize = 44;
                if (i == 5 && strings[5] != null && (strings[5].contains("距离") || strings[5].contains("经度")))
                    mShowStringV30.struStringInfo[i].wShowStringTopLeftX = 32;
                else
                    mShowStringV30.struStringInfo[i].wShowStringTopLeftX = 64;

                if (isShowIts != null && isShowIts.length > i)
                    mShowStringV30.struStringInfo[i].wShowString = isShowIts[i];
                else
                    mShowStringV30.struStringInfo[i].wShowString = 1;

                if (topLeftYs != null && topLeftYs.length > i)
                    mShowStringV30.struStringInfo[i].wShowStringTopLeftY = topLeftYs[i];
                else
                    mShowStringV30.struStringInfo[i].wShowStringTopLeftY = (1 * i + 1) * 32;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        boolean b = HCNetSDK.getInstance().NET_DVR_SetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_SET_SHOWSTRING_V30,
                All_id_Info.getInstance().getM_iChanNum(),
                mShowStringV30);
        if (!b) {
            LogUtil.error("字符叠加添加版头：NET_DVR_GET_SHOWSTRING_V30 faild!  err: ", HCNetSDK.getInstance().NET_DVR_GetLastError());
        }

    }


    public void setBottomBoomAndRanging(String ranging, double jingdu, double weidu) {

        if (ranging == null || TextUtils.isEmpty(ranging)) {
            ranging = "00.00";
        }

        StringBuilder builder = new StringBuilder();
        if (!ranging.equals("00.00"))
            builder.append("距离:").append(ranging).append("M");
        if (jingdu > 0 || weidu > 0) {
            builder.append(" 经度:").append(jingdu).append(" 纬度:").append(weidu);
        }

        final String msg = builder.toString();

//        LogUtil.log(" tong :" + msg);
//        setAllZiFuDieJia(new String[]{msg},new int[]{544},new int[]{1,0,0,0,0,0});
//        setTongDaoName(msg,true);
        mTitles[5] = msg;
        if (!isSetHead)
            mCacheThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    if (isNotStop) {
                        if (!isSetHead) {
//                            setAllZiFuDieJia(mTitles, new int[]{64, 128, 192, 256, 350, 544}, new int[]{1, 1, 1, 1, 0, 1});
//                            setTongDaoName(msg, true);
                            setMulNoHeader();
//                            LogUtil.log("字符叠加标记已执行 ");
                        }
                    }
                }
            });
    }

    public static void getAllZiFuDieJia() {
        HCNetSDK.getInstance().NET_DVR_GetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                HCNetSDK.getInstance().NET_DVR_GET_SHOWSTRING_V30,
                All_id_Info.getInstance().getM_iChanNum(),
                mShowStringV30);

        for (int i = 0; i < mShowStringV30.struStringInfo.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            String string = null;
            try {
                string = new String(mShowStringV30.struStringInfo[i].sString, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            stringBuilder.append("是否显示: ").
                    append(mShowStringV30.struStringInfo[i].wShowString).
                    append("\n长度：").
                    append(mShowStringV30.struStringInfo[i].wStringSize).
                    append("\nX: ").
                    append(mShowStringV30.struStringInfo[i].wShowStringTopLeftX).
                    append("\nY：").
                    append(mShowStringV30.struStringInfo[i].wShowStringTopLeftY).
                    append("\nstring: ").
                    append(string);
//            LogUtil.log("海康字符叠加：" + stringBuilder.toString());
        }
    }


    public void setOSD() {
        if (All_id_Info.getInstance().getM_iLogID() < 0)
            return;

        NET_DVR_XML_CONFIG_INPUT input = new NET_DVR_XML_CONFIG_INPUT();
        NET_DVR_XML_CONFIG_OUTPUT output = new NET_DVR_XML_CONFIG_OUTPUT();
        String requesturl = "PUT /ISAPI/System/Video/inputs/channels/1/GPSoverlays";


        String inBuffer = "<GPSoverlays>\r\n" +
                "<enabled>distance</enabled>\r\n" +
                "<name>20.36</name>\r\n" +
                "</GPSoverlays>";

        try {
            byte[] requestUrlBytes = requesturl.getBytes("ascii");
            byte[] inBufferBytes = inBuffer.getBytes("ascii");
            for (int i = 0; i < requestUrlBytes.length; i++) {
                input.lpRequestUrl[i] = requestUrlBytes[i];
            }

            for (int i = 0; i < inBufferBytes.length; i++) {
                input.lpInBuffer[i] = inBufferBytes[i];
            }

            input.dwRequestUrlLen = requestUrlBytes.length;
            input.dwInBufferSize = inBufferBytes.length;
//            LogUtil.log("1: " + requestUrlBytes.length + " 2: " + inBufferBytes.length);
//            HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(All_id_Info.getInstance().getM_iLogID(),input,input);


            if (!HCNetSDK.getInstance().NET_DVR_STDXMLConfig(All_id_Info.getInstance().getM_iLogID(), input, output)) {
                LogUtil.error("字符叠加添加版头：NET_DVR_STDXMLConfig faild!  err: ", HCNetSDK.getInstance().NET_DVR_GetLastError());
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        HCNetSDK.getInstance().NET_DVR_STDXMLConfig();
    }


}
