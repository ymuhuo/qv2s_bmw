package com.bmw.peek2.presenter.impl;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.jna.SystemTransformJNAInstance;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.presenter.ControlPresenter;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.SocketUtilNew;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/9/5.
 */
public class ControlPresentImpl2 implements ControlPresenter {

    private SocketUtilNew socketUtil;
    private String actionName;
    private boolean isNotFinish;
    private ScheduledExecutorService scheduledExecutorService;
    private Executor cacheThreadPool;
    private boolean isGEtRanging;
    private boolean isGetEnvironment;
    private Intent mIntent;

    public ControlPresentImpl2() {
        mIntent = new Intent();
        mIntent.setAction("data.receiver");
        scheduledExecutorService = Executors.newScheduledThreadPool(3);
        cacheThreadPool = Executors.newCachedThreadPool();
        socketUtil = new SocketUtilNew();
        socketUtil.setOnDataReaderListener(new SocketUtilNew.OnDataReaderListener() {
            @Override
            public void result(byte[] bytes) {
                Message msg = new Message();
                msg.obj = bytes;
                handler.sendMessage(msg);
            }
        });

        isNotFinish = true;

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getAngle();
            }
        }, 0, 1500, TimeUnit.MILLISECONDS);

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isGetEnvironment)
                    getEnvironment();
            }
        }, 0, 2, TimeUnit.SECONDS);

        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (isNotFinish) {
                    if (isGEtRanging)
                        getLaserRanging();
                    sleep(800);
                }
            }
        });


        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getBattery();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void loginoutSocket() {
//        socketUtil.release();

    }

    @Override
    public void isGetBoom(final boolean isTrue) {

    }

    @Override
    public void isGetRanging(Boolean isTrue) {
        isGEtRanging = isTrue;
        mIntent.putExtra("rangingSize", "00.00");
        BaseApplication.context().sendBroadcast(mIntent);
        mIntent.removeExtra("rangingSize");
    }

    @Override
    public void
    resetSocket() {
        socketUtil.resetSocket();
    }


    private void set_commands(String name, int command, int arg_count, int[] args, boolean isSend) {
        actionName = name;

        int sum_arg = 0;
        for (int i = 0; i < arg_count - 1; i++) {
            sum_arg += args[i];
        }
        int sum = 0;
        sum = (0x01 + command + arg_count + sum_arg) % 0x100;

        byte[] commands = new byte[arg_count + 3];

        commands[0] = (byte) 0x01;
        commands[1] = (byte) command;
        commands[2] = (byte) arg_count;
        commands[arg_count + 2] = (byte) sum;

        for (int i = 3; i < arg_count + 2; i++) {
            commands[i] = (byte) args[i - 3];
        }

        if (isSend)
            sendCommands(commands);
        else
            getReader(commands);


    }

    @Override
    public void up() {//上命令
        set_commands("上命令", 0x10, 0x02, new int[]{0x00, 0x13}, true);
    }

    @Override
    public void down() { //下命令
        set_commands("下命令", 0x11, 0x02, new int[]{0x00, 0x14}, true);
    }

    @Override
    public void size_add() {//变倍变长
        set_commands("变倍变长", 0x14, 0x02, new int[]{0x00, 0x17}, true);
    }

    @Override
    public void size_sub() {//变倍变短
        set_commands("变倍变短", 0x15, 0x02, new int[]{0x00, 0x18}, true);
    }

    @Override
    public void zoom_add() {//聚焦近
        set_commands("聚焦近", 0x17, 0x02, new int[]{0x00, 0x1a}, true);
    }

    @Override
    public void zoom_sub() {//聚焦远
        set_commands("聚焦远", 0x16, 0x02, new int[]{0x00, 0x19}, true);
    }

    @Override
    public void stop() {//停止命令
        set_commands("停止命令", 0x1c, 0x02, new int[]{0x00, 0x1f}, true);
    }

    @Override
    public void low_beam_open(int strength) {//近光灯开
        set_commands("近光灯开", 0x19, 0x02, new int[]{strength}, true);
    }

    @Override
    public void low_beam_close() {//近光灯关
        set_commands("近光灯关", 0x18, 0x02, new int[]{0x00}, true);
    }

    @Override
    public void high_beam_open(int strength) {//远光灯开
        set_commands("远光灯开", 0x1B, 0x02, new int[]{strength}, true);
    }

    @Override
    public void high_beam_close() {//远光灯关
        set_commands("远光灯关", 0x1a, 0x02, new int[]{0x00}, true);
    }

    @Override
    public void getAngle() {
        set_commands("获取俯仰角", 0x1f, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void autoHorizontal() {//自动水平
        set_commands("自动水平", 0x21, 0x02, new int[]{0x00}, true);
    }

    @Override
    public boolean isSocketNull() {
        if (socketUtil != null)
            return socketUtil.isSocketNull();
        else
            return true;
    }

    private void sendCommands(byte[] commands) {
        socketUtil.getReader(commands, 1, actionName, 0);
//       new SocketReaderCallBack(socketUtil,null).execute(commands);
    }


   /* @Override
    public void getAngle() {//获取俯仰角
        commands = new byte[]{(byte) 0x01, (byte) 0x1F, (byte) 0x02,
                (byte) 0x00, (byte) 0x22};
        socketUtil.sendcmd(commands);
        byte[] angle = socketUtil.getReader();
        Log.d(TAG, "getAngle: " + angle.toString());
    }*/

    @Override
    public void getBattery() {//获取电量；
        set_commands("获取电量", 0x23, 0x02, new int[]{0x00}, false);

    }

    @Override
    public void getLaserRanging() {
        set_commands("激光测距", 0x20, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void getEnvironment() {
        set_commands("获取环境数据", 0x1e, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void isGetEnvironment(boolean isGetEnvironment) {
        this.isGetEnvironment = isGetEnvironment;
    }
/*

    @Override
    public void getBoom() {
        set_commands("获取机芯倍数", 0x22, 0x02, new int[]{0x00}, false);
    }
*/

    @Override
    public void clearFog_on() {
        set_commands("开启除雾", 0x25, 0x02, new int[]{0x01}, true);
    }

    @Override
    public void clearFog_off() {
        set_commands("关闭除雾", 0x25, 0x02, new int[]{0x00}, true);
    }

    @Override
    public void open_picfangdou() {
        set_commands("open_picfangdou", 0x26, 0x02, new int[]{0x01}, false);
    }

    @Override
    public void close_picfangdou() {
        set_commands("close_picfangdou", 0x26, 0x02, new int[]{0x00}, false);

    }

    @Override
    public void open_kuandongtai() {
        set_commands("open_kuandongtai", 0x27, 0x02, new int[]{0x01}, false);
    }

    @Override
    public void close_kuandongtai() {
        set_commands("close_kuandongtai", 0x27, 0x02, new int[]{0x00}, false);

    }

    @Override
    public void open_lightyizhi() {
        set_commands("open_lightyizhi", 0x28, 0x02, new int[]{0x03}, false);

    }

    @Override
    public void close_lightyizhi() {
        set_commands("close_lightyizhi", 0x28, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void open_touwu() {
        set_commands("open_touwu", 0x29, 0x02, new int[]{0x03}, false);
    }

    @Override
    public void close_touwu() {
        set_commands("close_touwu", 0x29, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void open_gaoganguang() {
        set_commands("open_gaoganguang", 0x2A, 0x02, new int[]{0x01}, false);

    }

    @Override
    public void close_gaoganguang() {
        set_commands("close_gaoganguang", 0x2A, 0x02, new int[]{0x00}, false);
    }


    private void getReader(byte[] commands) {
        socketUtil.getReader(commands, 5, "接收" + actionName, 1);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] bytes = (byte[]) msg.obj;
            if (bytes != null)
                switch (bytes[1]) {
                    case 0x23:  //获取电量
                        if (bytes.length == 5 && bytes[1] == 0x23 && bytes[4] != 0) {
                            int batteryNum = Integer.valueOf(bytes[3]);
                            mIntent.putExtra("batteryNum", batteryNum);
                            BaseApplication.context().sendBroadcast(mIntent);
                            mIntent.removeExtra("batteryNum");

                        }
                        break;
                    case 0x22:  //获取机芯倍数
                       /* LogUtil.log("机芯获取成功：", (bytes[3] & 0xff));

                        boomSize = (bytes[3] & 0xff);*/
                        break;
                    case 0x20:  //激光测距

                        byte[] bytes1 = new byte[]{(byte) (bytes[3] & 0xff), (byte) (bytes[4] & 0xff), (byte) (bytes[5] & 0xff), (byte) (bytes[6] & 0xff), (byte) (bytes[7] & 0xff)};
                        try {
                            String str = new String(bytes1, "ascii");
                            mIntent.putExtra("rangingSize", str);
                            BaseApplication.context().sendBroadcast(mIntent);
                            mIntent.removeExtra("rangingSize");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 0x1f:

                        ByteArrayInputStream in = new ByteArrayInputStream(new byte[]{bytes[6], bytes[5], bytes[4], bytes[3]});
                        DataInputStream dIn = new DataInputStream(in);
                        try {
                            float angle = dIn.readFloat();
                            angle = (float) Math.round(angle * 10) / 10;
                            mIntent.putExtra("angle", angle);
                            BaseApplication.context().sendBroadcast(mIntent);
                            mIntent.removeExtra("angle");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (dIn != null)
                                dIn.close();
                            if (in != null)
                                in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 0x1e:
                        int gaoba = Integer.valueOf(bytes[3] & 0xff);
                        int diba = Integer.valueOf(bytes[4] & 0xff);
                        int qiya_num = ((gaoba << 8) | diba);
                        float environmentNum = getQiya(qiya_num);
                        if (environmentNum <= 0)
                            environmentNum = 0;
                        if (lastQiyaNum != 0) {
                            if (Math.abs(lastQiyaNum - environmentNum) <= 5) {
                                lastQiyaNum = environmentNum;
                                m_qiya_right_count++;
                                System.out.println("aaaaaaaaa right count  = "+m_qiya_right_count);
                                if (m_qiya_right_count >= 3) {
                                    m_qiya_right_count = 0;
                                    m_qiya_error_count = 0;
                                    sendEnvironmentBrocast(environmentNum);
                                }

                            } else {
                                m_qiya_error_count++;
                                System.out.println("aaaaaaaaa error count  = "+m_qiya_error_count);
                                if (m_qiya_error_count >= 4) {
                                    m_qiya_error_count = 0;
                                    lastQiyaNum = environmentNum;
                                }
                            }
                        } else {
                            lastQiyaNum = environmentNum;
                            sendEnvironmentBrocast(environmentNum);
                        }

                        break;

                }


        }
    };
    private float lastQiyaNum;
    private int m_qiya_right_count;
    private int m_qiya_error_count;


    private void sendEnvironmentBrocast(float environmentNum) {
        mIntent.putExtra("environment_qiya", environmentNum);
        BaseApplication.context().sendBroadcast(mIntent);
    }

    private float getQiya(int qiya) {

        if ((qiya & 0x8000) != 0) {
            qiya = 0 - (qiya & 0x7fff);
        } else
            qiya = (qiya & 0x7fff);

        float result = (float) ((qiya * 10.0f - 101325.0) * 0.1450377F / 1000.0f);
        return (float) (Math.round(result * 100)) / 100;

    }

    @Override
    public void release() {//释放资源
        socketUtil.release();
        scheduledExecutorService.shutdownNow();
        scheduledExecutorService = null;
        LogUtil.log("release: ControlPresentImpl");
    }

    private void log(String msg) {
        if (Login_info.getInstance().isPermisionLog())
            LogUtil.log(msg);
    }

}
