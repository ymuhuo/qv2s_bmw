package com.bmw.peek2.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.utils.NetUtil;
import com.bmw.peek2.utils.WifiAdmin;
import com.bmw.peek2.utils.WifiUtil;


public class ConnectStateDialog {

    private AlertDialog dialog;
    //    private TextView videoIp,controlIp;
    private ImageView videoImg, controlImg;
    private boolean isFinish;
    private boolean isVideoConn, isControlConn;
    private RadioGroup rg_wifiChoose;
    private RadioButton rd_mainframe, rd_repeater;
    private Context context;

    public ConnectStateDialog(final Context context) {
        this.context = context;
        dialog = new AlertDialog.Builder(context).create();
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
//        dialog.setView(new EditText(context));//实现弹出虚拟键盘
        dialog.show();
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
//p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels*0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.dialog_connect_state);
//        videoIp = (TextView) window.findViewById(R.id.connect_video_ip);
        videoImg = (ImageView) window.findViewById(R.id.connect_video_connect);
//        controlIp = (TextView) window.findViewById(R.id.connect_control_ip);
        controlImg = (ImageView) window.findViewById(R.id.connect_control_connect);
        rg_wifiChoose = (RadioGroup) window.findViewById(R.id.radioGroup_dialog_connectStatic);
        rd_mainframe = (RadioButton) window.findViewById(R.id.rb_connect_mainFrame);
        rd_repeater = (RadioButton) window.findViewById(R.id.rb_connect_repeater);

        WifiAdmin wifiAdmin = new WifiAdmin(context);

        if(Login_info.getInstance().isWifi_auto()){
            rg_wifiChoose.setVisibility(View.VISIBLE);
        }else {
            rg_wifiChoose.setVisibility(View.INVISIBLE);
        }
        initConnect();
        initRgWifiChoose();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isFinish = true;
//                connectState = null;
                videoImg = null;
                controlImg = null;
                dialog = null;
                if (listener != null) {
                    listener.afterDismiss();
                }
            }
        });

        justConnFromDevice();

    }

    private void initRgWifiChoose() {
        if(Login_info.getInstance().isWifiIsRepeater()){
            rg_wifiChoose.check(R.id.rb_connect_repeater);
        }else {
            rg_wifiChoose.check(R.id.rb_connect_mainFrame);
        }
        rg_wifiChoose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                WifiAdmin wifiAdmin = new WifiAdmin(context);
                switch (checkedId){
                    case R.id.rb_connect_mainFrame:
                        Login_info.getInstance().setWifiIsRepeater(false);
                        removeSSID(wifiAdmin, Login_info.baseMainFrameWifiSSID);
                        initWifiConnect();
                        break;
                    case R.id.rb_connect_repeater:
                        Login_info.getInstance().setWifiIsRepeater(true);
                        removeSSID(wifiAdmin, Login_info.baseRepeaterWifiSSID);
                        initWifiConnect();
                        break;
                }
            }
        });
    }

    private void removeSSID(WifiAdmin wifiAdmin,String currentSSID){
        if(Login_info.getInstance().isWifi_auto()) {
            String ssid = wifiAdmin.getSSID();
            if (!TextUtils.isEmpty(ssid) && !ssid.contains(currentSSID)) {
                wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
            }
        }
    }

    private void initWifiConnect() {
        if (Login_info.getInstance().isWifi_auto()) {
            WifiUtil.startWifiService(context);
           /* WifiConnect wifiConnect = WifiConnect.getInstance();
            if (!wifiConnect.getIsOpenThread())
                wifiConnect.starConnect();*/
        }

    }

    private void initConnect() {
        videoImg.setImageResource(NetUtil.getInstance().isVideoConnect()? R.mipmap.connect : R.mipmap.disconnect);
        controlImg.setImageResource(NetUtil.getInstance().isControlConnect()? R.mipmap.connect : R.mipmap.disconnect);
    }

    private void justConnFromDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish) {

                    NetUtil.getInstance().justNetConnect();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (videoImg != null)
                        videoImg.post(new Runnable() {
                            @Override
                            public void run() {
                                videoImg.setImageResource(NetUtil.getInstance().isVideoConnect() ? R.mipmap.connect : R.mipmap.disconnect);
                                controlImg.setImageResource(NetUtil.getInstance().isControlConnect() ? R.mipmap.connect : R.mipmap.disconnect);
                            }
                        });
                }
            }
        }).start();
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }

    private OnDismissAfterListener listener;

    public void setOnDismissAfterListener(OnDismissAfterListener listener) {
        this.listener = listener;
    }

    public interface OnDismissAfterListener {
        public void afterDismiss();
    }

}