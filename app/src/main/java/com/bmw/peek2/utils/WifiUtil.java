package com.bmw.peek2.utils;


import android.content.Context;

import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.service.MyIntentService;

import java.io.IOException;

/**
 * Created by admin on 2017/6/30.
 */

public class WifiUtil {

    private static String TAG = "MyIntentService";

    public static boolean isWifiConnectPing(WifiAdmin wifiAdmin, String ssid, String testIp) {
        if (!wifiAdmin.getSSID().contains(ssid)) {
            return false;
        }
        if (wifiAdmin.getIPAddress() == 0) {
            return false;
        }

        if(testIp == null)
            return true;

        if (pingHost(testIp) == 0) {
            return true;
        }
        return false;
    }

    public static boolean isWifiConnect(WifiAdmin wifiAdmin, String ssid){
        if (!wifiAdmin.getSSID().contains(ssid))
            return false;
        if(wifiAdmin.getIPAddress()==0){
            return false;
        }
        if(wifiAdmin.getLinkSpeed() == -1)
            return false;
        if(wifiAdmin.getRssi() == -127)
            return false;
        return true;

    }

    public static int pingHost(String str) {
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

    public static void startWifiService(Context context){
        MyIntentService.stopIntentService(context);
        String ssid = Login_info.getInstance().isWifiIsRepeater()?Login_info.baseRepeaterWifiSSID:Login_info.baseMainFrameWifiSSID;
        MyIntentService.startActionWifiConnect(context, ssid, Login_info.baseRepeaterWifiPassword, Login_info.getInstance().getSocket_ip());
    }


}
