package com.bmw.peek2.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.utils.LogUtil;

/**
 * 获取服务器IP地址
 */

public class Login_info {

    public static boolean isPause =true;
    public static boolean isAddKanban = false;
    public static String manufacturer = null;

    public static final String ALL_URL_INFOMATION = "ALL_URL_INFOMATION";
    public static final String VIDEO_IP = "VIDEO_IP";
    public static final String VIDEO_PORT = "VIDEO_PORT";
    public static final String VIDEO_ACCOUNT = "VIDEO_ACCOUNT";
    public static final String VIDEO_PASSWORD = "VIDEO_PASSWORD";
    public static final String VIDEO_ZIMALIU = "VIDEO_ZIMALIU";
    public static final String SOCKET_IP = "SOCKET_IP";
    public static final String SOCKET_PORT = "SOCKET_PORT";
    public static final String WIFI_SSID = "WIFI_SSID";
    public static final String WIFI_PASSWORD = "WIFI_PASSWORD";
    public static final String WIFI_AUTO = "WIFI_AUTO";
    public static final String WIFI_IS_REPEATER = "WIFI_IS_REPEATER";
    public static final String YINGJIEMA = "YINGJIEMA";
    public static final String FILE_IS_SAVE_SDCARD = "FILE_IS_SAVE_SDCARD";
    public static final String RECORDHEAD_IS_SHOW_FIRSTNAME = "RECORDHEAD_IS_SHOW_FIRSTNAME";

    public static final String base_video_ip = "172.169.10.64";
    public static final int base_video_port = 8000;
    public static final String base_video_account = "admin";
    public static final String base_video_password = "bmw12345";
    public static final boolean base_video_zimaliu = false;
    public static final String base_socket_ip = "172.169.10.1";
    public static final int base_socket_port = 50001;

    public static final String baseMainFrameWifiSSID = "Peek2S_AP";
    public static final String baseRepeaterWifiSSID = "Peek2S_Relay_AP";

//    public static final String baseMainFrameWifiSSID = "Peek2S_RD_AP";
//    public static final String baseRepeaterWifiSSID = "Peek2S_Relay_RD_AP";

    public static final String baseMainFrameWifiPassword = "bmwpeek2shost";
    public static final String baseRepeaterWifiPassword = "bmwpeek2shost";

    public static final boolean base_wifi_auto = true;
    public static final boolean baseWifiIsRepeater = true;
    public static final boolean baseIsYingJieMa = false;
    public static final boolean baseIsSaveToExSdcard = false;
    public static final boolean baseIsRecordHeadShowFirstName = true;
    

    public static String local_picture_path = "/peek2s_data/capture/";
    public static String local_video_path = "/peek2s_data/video/";
    public static String local_kanban_path = "/peek2s_data/kanban/";
    public static String local_peek2s_path = "/peek2s_data/";
    public static String url = "http://192.168.191.1:8080/Banben";
    private String video_ip;
    private int video_port;
    private String video_account;
    private String video_password;
    private boolean video_zimaliu;
    private String socket_ip;
    private int socket_port;
    private String wifi_SSID;
    private String wifi_Password;
    private boolean wifi_auto;
    private boolean wifiIsRepeater;
    private boolean isPermisionLog;
    private boolean isYingJieMa;
    private boolean isSaveToExSdcard;
    private boolean isShowFirstName;

    private SharedPreferences sharedPreferences;
//	private static String videoUrl1 = "rtsp://admin:bmwadmin@192.168.2.13:554/media/video2";
//	private static String videoUrl2 = "rtsp://admin:bmwadmin@192.168.2.13:554/media/video2";
//	public  static final String SSID = "Peek2_Test_AP";
//	public static final String PASSWORD = "bmwpeek2";
//  private static final String ADDRESS = "192.168.2.7";
//  private static final String ADDRESS="192.168.191.1";
//	private static final int PORT = 20108;


    private static Login_info instance = null;
    private SharedPreferences.Editor editor;
    private boolean isBanTouShow;

    public boolean isBanTouShow() {
        return isBanTouShow;
    }

    public void setBanTouShow(boolean banTouShow) {
        isBanTouShow = banTouShow;
    }

    private Login_info() {
    }

    public void release(){
        instance = null;
    }

    public static Login_info getInstance() {
        if (instance == null) {
            synchronized (All_id_Info.class) {
                if (instance == null)
                    instance = new Login_info();
            }
        }
        return instance;
    }


    public void initLoginInfo(Context context) {
        sharedPreferences = BaseApplication.getSharedPreferences();
        editor = sharedPreferences.edit();
        initData();
    }

    private void initData() {

        /*SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(WIFI_IS_REPEATER,baseWifiIsRepeater);
        editor.commit();*/

        video_ip = sharedPreferences.getString(VIDEO_IP, base_video_ip);
        video_port = sharedPreferences.getInt(VIDEO_PORT, base_video_port);
        video_account = sharedPreferences.getString(VIDEO_ACCOUNT, base_video_account);
        video_password = sharedPreferences.getString(VIDEO_PASSWORD, base_video_password);
        socket_ip = sharedPreferences.getString(SOCKET_IP, base_socket_ip);
        socket_port = sharedPreferences.getInt(SOCKET_PORT, base_socket_port);
        wifi_SSID = sharedPreferences.getString(WIFI_SSID, baseRepeaterWifiSSID);
        wifi_Password = sharedPreferences.getString(WIFI_PASSWORD, baseRepeaterWifiPassword);
        wifi_auto = sharedPreferences.getBoolean(WIFI_AUTO, base_wifi_auto);
        video_zimaliu = sharedPreferences.getBoolean(VIDEO_ZIMALIU,base_video_zimaliu);
        wifiIsRepeater = sharedPreferences.getBoolean(WIFI_IS_REPEATER,baseWifiIsRepeater);
        isYingJieMa = sharedPreferences.getBoolean(YINGJIEMA,baseIsYingJieMa);
        isSaveToExSdcard = sharedPreferences.getBoolean(FILE_IS_SAVE_SDCARD,baseIsSaveToExSdcard);
        isShowFirstName = sharedPreferences.getBoolean(RECORDHEAD_IS_SHOW_FIRSTNAME,baseIsRecordHeadShowFirstName);


    }

    public void setData(String vIp, String vPort,
                        String vAccount, String vPassword,
                        String sIp, int sPort,
                        String wSsid, String wPassword,
                        boolean isAuto,boolean isZimaliu
    ) {
        editor = sharedPreferences.edit();
        editor.putString(VIDEO_IP, vIp);
        editor.putInt(VIDEO_PORT, Integer.valueOf(vPort));
        editor.putString(VIDEO_ACCOUNT, vAccount);
        editor.putString(VIDEO_PASSWORD, vPassword);
        editor.putString(SOCKET_IP, sIp);
        editor.putInt(SOCKET_PORT, sPort);
        editor.putString(WIFI_SSID, wSsid);
        editor.putString(WIFI_PASSWORD, wPassword);
        editor.putBoolean(WIFI_AUTO, isAuto);
        editor.putBoolean(VIDEO_ZIMALIU,isZimaliu);
        editor.commit();
        initData();
    }

    public String getVideo_ip() {
        return video_ip;
    }

    public int getVideo_port() {
        return video_port;
    }

    public String getVideo_account() {
        return video_account;
    }

    public String getVideo_password() {
        return video_password;
    }

    public String getSocket_ip() {
        return socket_ip;
    }

    public int getSocket_port() {
        return socket_port;
    }

    public String getWifi_SSID() {
        return wifi_SSID;
    }

    public String getWifi_Password() {
        return wifi_Password;
    }

    public boolean isWifi_auto() {
        return wifi_auto;
    }


    public void setWifi_auto(boolean wifi_auto,boolean isChangeBase) {
        this.wifi_auto = wifi_auto;
        if(isChangeBase) {
            editor.putBoolean(WIFI_AUTO, wifi_auto);
            editor.commit();
            initData();
        }
    }

    public boolean isVideo_zimaliu() {
        return video_zimaliu;
    }

    public boolean isWifiIsRepeater() {
        return wifiIsRepeater;
    }

    public void setWifiIsRepeater(boolean wifiIsRepeater) {
        this.wifiIsRepeater = wifiIsRepeater;
        editor.putBoolean(WIFI_IS_REPEATER, wifiIsRepeater);
        editor.commit();
    }

    public boolean isPermisionLog() {
        return isPermisionLog;
    }

    public void setPermisionLog(boolean permisionLog) {
        isPermisionLog = permisionLog;
    }

    public boolean isYingJieMa() {
        return isYingJieMa;
    }

    public void setYingJieMa(boolean yingJieMa) {
        isYingJieMa = yingJieMa;
        editor.putBoolean(YINGJIEMA, yingJieMa);
        editor.commit();
        initData();
    }

    public boolean isSaveToExSdcard() {
        return isSaveToExSdcard;
    }

    public void setSaveToExSdcard(boolean saveToExSdcard) {
        isSaveToExSdcard = saveToExSdcard;
        editor.putBoolean(FILE_IS_SAVE_SDCARD, isSaveToExSdcard);
        editor.commit();
    }

    public boolean isShowFirstName() {
        return isShowFirstName;
    }

    public void setShowFirstName(boolean showFirstName) {
        isShowFirstName = showFirstName;
        editor.putBoolean(RECORDHEAD_IS_SHOW_FIRSTNAME, showFirstName);
        editor.commit();
    }
}
