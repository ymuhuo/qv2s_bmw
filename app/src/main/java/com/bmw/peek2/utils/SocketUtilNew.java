package com.bmw.peek2.utils;

import android.content.Intent;
import android.text.TextUtils;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.model.All_id_Info;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.utils.singleThreadUtil.FinalizableDelegatedExecutorService;
import com.bmw.peek2.utils.singleThreadUtil.RunnablePriority;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/9/19.
 */
public class SocketUtilNew {

    //    private PreviewImpl preview;
    private Socket socket;
    private OutputStream socketWriter;
    private InputStream socketReader;
    private boolean isNotFinish;
    private Login_info loginInfo;
    public ExecutorService singleThreadExecutor = new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>()));
    private long current_time;
    //    private static SocketUtilNew instance;
    private String lastActionName;

    private ArrayList<Integer> backList;
    private byte[] backByte;
    private Integer backArgLength;
    private boolean isResetSocket;
    private boolean isJustConnect;
    private WifiAdmin wifiAdmin;
    private boolean isReConnect;

    public boolean isSocketNull() {
        if (socket == null)
            return true;
        else
            return false;
    }

    public SocketUtilNew() {
        wifiAdmin = new WifiAdmin(BaseApplication.context());
        loginInfo = Login_info.getInstance();
        initSocket();
        read();
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //初始化socket
    public void initSocket() {

        if (singleThreadExecutor == null || singleThreadExecutor.isShutdown()) {
            singleThreadExecutor = new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>()));
        }
        isNotFinish = true;

    }

    public void resetSocket() {

        if (isNotFinish) {
            try {
                if (socket != null) {
                    socket.close();
                }
                socket = null;

                NetUtil.getInstance().justNetConnect();
                while (!NetUtil.getInstance().isControlConnect()) {
                    if (!isNotFinish)
                        break;
                    NetUtil.getInstance().justNetConnect();
                    sleep(2000);
                }
                socket = new Socket(loginInfo.getSocket_ip(), loginInfo.getSocket_port());
                socketWriter = socket.getOutputStream();
                socketReader = socket.getInputStream();
                socket.setSoTimeout(1000 * 6);
                if (socket != null) {
                    LogUtil.log("数据:socket连接:  连接成功!", "");
                    isResetSocket = false;
                    isJustConnect = false;
                    NetUtil.getInstance().justNetConnect();
//                    if (isReConnect) {
                    sendBrocaseReLoginHaiKang();
//                    }
//                    if (!isReConnect)
//                        isReConnect = true;
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (socket == null) {
                sleep(50);
                resetSocket();
            }
        }
           /*   }
      }).start();*/
    }

    private void sendBrocaseReLoginHaiKang() {
        Intent intent = new Intent("data.receiver");
        intent.putExtra("isReLoginHK", true);
        BaseApplication.context().sendBroadcast(intent);
    }

    //发送命令（控制方向、速度的命令）
    public void sendcmd(byte[] commands, String action_name) {
        if (socket == null) {
            error("数据:发送", action_name + "命令失败：sendcmd: socket is null");
            return;
        }
        if (socketWriter == null)
            try {
                socketWriter = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            socketWriter.write(commands);
            socketWriter.flush();
        } catch (IOException e) {
            if (socket != null && !socket.isClosed()) {
                error("数据:socketService is already closed!  ", action_name);
                socket = null;
                initSocket();
            }
            e.printStackTrace();
        }
    }

    public void release() {
        isNotFinish = false;
        if (socket != null) {
            try {
                singleThreadExecutor.shutdownNow();
                socket.shutdownInput();
                socket.shutdownOutput();
                socketReader.close();
                socketWriter.close();
                if (socket != null)
                    socket.close();
                socket = null;
//                instance = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.log("数据:socket断开连接: Socket已经释放内存！", "");
    }

    public void getReader(final byte[] commands,
                          final int priority,
                          final String action_name, final int which) {
        if (socket == null) {
            if (!isNotFinish)
                return;

            if (Login_info.getInstance().isWifi_auto()) {
                String currentSSID = Login_info.getInstance().isWifiIsRepeater() ? Login_info.baseRepeaterWifiSSID : Login_info.baseMainFrameWifiSSID;
                if (!WifiUtil.isWifiConnect(wifiAdmin,currentSSID)) {

                    if (!All_id_Info.getInstance().isWifiConnectRunning())
                        WifiUtil.startWifiService(BaseApplication.context());

                }
            }
            if (!isResetSocket) {
                synchronized (this) {
                    if (!isResetSocket) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (isNotFinish) {
                                    resetSocket();
                                }

                            }
                        }).start();
                        isResetSocket = true;
                    }
                }

            }
            return;
        }


        if (socketReader == null)
            try {
                socketReader = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }


        if (socket != null) {
            if (singleThreadExecutor != null && !singleThreadExecutor.isShutdown())
                singleThreadExecutor.execute(new RunnablePriority(priority) {
                    @Override
                    public void run() {
//                        LogUtil.log("发送："+action_name);
                        while ((action_name.contains("停止")) && System.currentTimeMillis() - current_time <= 150) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!action_name.contains("停止")) {
                            while (System.currentTimeMillis() - current_time <= 100) {
                                sleep(5);
                            }
                        }


                        sendcmd(commands, action_name);
//                    sleep(1000*10);


                        if (which == 1) {
                            sleep(20);
                        }
                        if (action_name.contains("停止")) {
                            sleep(50);
                        }
                        current_time = System.currentTimeMillis();
                        lastActionName = action_name;
                    }
                });
            else
                error("singleThreadExecutor || shutdown", "");
        }
    }

    public void read() {
        backList = new ArrayList<>();
        backByte = new byte[1];
        isNotFinish = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.log("开启socket读取线程！");
                while (isNotFinish) {
                    if (socket != null && socketReader != null) {
                        try {
                            socketReader.read(backByte);
                            if (backList.size() == 0) {
                                if ((backByte[0] & 0xff) == 0x01) {
                                    backList.add(backByte[0] & 0xff);
                                    log("socket读取，头校验成功");
                                }
                            } else {
                                backList.add(backByte[0] & 0xff);
                            }
                            if (backList.size() == 3) {
                                backArgLength = backList.get(2);
                            }
                            if (backList.size() > 3) {
                                if (backList.size() == 3 + backArgLength) {
                                    int sum = 0;
                                    for (int i = 0; i < backList.size() - 1; i++) {
                                        sum += backList.get(i);
                                    }
                                    sum = sum % 0x100;
                                    if (sum == backList.get(backList.size() - 1)) {
                                        log("socket读取，数据校验成功！");
                                        byte[] backResult = new byte[backList.size()];
                                        for (int i = 0; i < backList.size(); i++) {
                                            backResult[i] = (byte) (int) backList.get(i);
                                        }
                                        if (listener != null)
                                            listener.result(backResult);
                                        else
                                            LogUtil.error("数据校验：listener监听为null");
                                    }

                                    backList.clear();
                                    backArgLength = 0;
                                }
                            }

                        } catch (SocketTimeoutException e) {
                            LogUtil.error("socket连接超时：" + e.toString());
                            if (!isJustConnect) {
                                NetUtil.getInstance().justNetConnect();
                                isJustConnect = true;
                                if (socket != null) {
                                    try {
                                        socket.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                socket = null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtil.error("socket read error: " + e.toString());
                        }

                    }
                }
            }
        }).start();
    }


    public interface OnDataReaderListener {
        void result(byte[] bytes);
    }

    public OnDataReaderListener listener;


    public void setOnDataReaderListener(OnDataReaderListener listener) {
        this.listener = listener;
    }

    private void log(String key) {
        if (Login_info.getInstance().isPermisionLog())
            LogUtil.log(key);
    }

    private void error(String key, String msg) {
        LogUtil.error(key, msg);
    }

}

