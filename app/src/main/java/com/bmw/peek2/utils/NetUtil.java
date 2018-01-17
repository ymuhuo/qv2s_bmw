package com.bmw.peek2.utils;

import android.content.Intent;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.model.Login_info;

import java.io.IOException;

/**
 * Created by yMuhuo on 2017/1/6.
 */
public class NetUtil {
    private static NetUtil instance;
    private boolean isVideoConnect;
    private boolean isControlConnect;
    private Intent mIntentBR;

    private NetUtil() {
        mIntentBR = new Intent(Constant.BROCAST_PREVIEW_SHOW);
    }

    public static NetUtil getInstance() {
        if (instance == null) {
            synchronized (NetUtil.class) {
                instance = new NetUtil();
            }
        }
        return instance;
    }

    public int pingHost(String str) {
        int resault = -1;
        try {
            // TODO: Hardcoded for now, make it UI configurable
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 3 " + str);
            int status = p.waitFor();
            if (status == 0) {
                //  mTextView.setText("success") ;
                resault = 0;
            } else {
                resault = 1;
                //  mTextView.setText("fail");
            }
        } catch (IOException e) {
            //  mTextView.setText("Fail: IOException"+"\n");
        } catch (InterruptedException e) {
            //  mTextView.setText("Fail: InterruptedException"+"\n");
        }

        return resault;
    }

    public void justNetConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.log("ping主机！！！");
                isVideoAlreadyConn();
                isControlAlreadyConn();
                /*if (listener != null)
                    listener.stateChange();*/
                sendBraocast();
            }
        }).start();
    }

    private void sendBraocast() {
        mIntentBR.putExtra(Constant.KEY_WIFI_STATE_JUST_FINISH,true);
        BaseApplication.context().sendBroadcast(mIntentBR);
    }

    private void isVideoAlreadyConn() {
        int videoPing = pingHost(Login_info.getInstance().getVideo_ip());
        synchronized (NetUtil.class) {
            isVideoConnect = videoPing == 0 ? true : false;
        }
    }

    private void isControlAlreadyConn() {
        int controlPing = pingHost(Login_info.getInstance().getSocket_ip());
        synchronized (NetUtil.class) {
            isControlConnect = controlPing == 0 ? true : false;
        }
    }

    public boolean isVideoConnect() {
        return isVideoConnect;
    }

    public void setVideoConnect(boolean videoConnect) {
        this.isVideoConnect = videoConnect;
    }

    public boolean isControlConnect() {
        return isControlConnect;
    }

    public void setControlConnect(boolean controlConnect) {
        this.isControlConnect = controlConnect;
    }

    /*private OnNetUtilConnectStateChangeListener listener;

    public void setOnNetUtilConnectStateChangeListener(OnNetUtilConnectStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnNetUtilConnectStateChangeListener {
        void stateChange();
    }*/

}
