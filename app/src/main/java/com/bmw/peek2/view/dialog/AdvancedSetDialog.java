package com.bmw.peek2.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.R;
import com.bmw.peek2.utils.HkUtils;


public class AdvancedSetDialog {

    private Switch fangdou;
    private Switch kuandongtai;
    private Switch qiangguangyizhi;
    private Switch touwu;
    private Switch gaoganguang;
    private Switch digtitalMax;
    private AlertDialog dialog;
    private boolean isInit;

    public AdvancedSetDialog(Context context) {


        dialog = new AlertDialog.Builder(context).create();
//        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
        dialog.show();
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
//        p.height = (int) (dm.heightPixels() * 0.3);   //高度设置为屏幕的0.3
//        p.width = (int) (dm.widthPixels);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

//        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.advance_setting);
        fangdou = (Switch) window.findViewById(R.id.fangdou_switch);
        kuandongtai = (Switch) window.findViewById(R.id.kuandongtai_switch);
        qiangguangyizhi = (Switch) window.findViewById(R.id.qiangguangyizhi_switch);
        touwu = (Switch) window.findViewById(R.id.touwu_switch);
        gaoganguang = (Switch) window.findViewById(R.id.gaoganguang_switch);
        digtitalMax = (Switch) window.findViewById(R.id.sw_digtital_setMax);
        initSwitch();
        window.findViewById(R.id.advance_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setClicListener();
    }

    private void setClicListener() {
        kuandongtai.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                boolean isSet = HkUtils.setKuanDongTai(isChecked);
                if (isSet && isInit) {
                    BaseApplication.toast("设置成功！");
                }

                if (!isSet && kuandongtai != null)
                    kuandongtai.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isChecked) {
                                kuandongtai.setChecked(false);
                            }
                            BaseApplication.toast("设置失败，请检查设备是否连接成功！");
                        }
                    });
            }
        });

        digtitalMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                boolean isSet = HkUtils.setDigtitalZoomMax(isChecked);
                if (isSet && isInit) {
                    BaseApplication.toast("设置成功！");
                }
                if (!isSet && digtitalMax != null)
                    digtitalMax.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isChecked) {
                                digtitalMax.setChecked(false);
                            }
                            BaseApplication.toast("设置失败，请检查设备是否连接成功！");
                        }
                    });
            }
        });
    }

    private void initSwitch() {
//        fangdou.setChecked(advanceSetStateInfo.isFangdou_checked());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isGet = HkUtils.getKuanDongTai();
                final boolean isDigtitalMaxSet = HkUtils.getDigtitalZoomMax();
                if (kuandongtai != null)
                    kuandongtai.post(new Runnable() {
                        @Override
                        public void run() {
                            kuandongtai.setChecked(isGet);
                            digtitalMax.setChecked(isDigtitalMaxSet);
                            isInit = true;
                        }
                    });
            }
        }).start();


//        qiangguangyizhi.setChecked(advanceSetStateInfo.isQiangguangyizhi_checked());
//        touwu.setChecked(advanceSetStateInfo.isTouwu_checked());
//        gaoganguang.setChecked(advanceSetStateInfo.isGaoganguang_checked());
    }


    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }


}