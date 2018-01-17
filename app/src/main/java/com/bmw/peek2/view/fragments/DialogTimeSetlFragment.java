package com.bmw.peek2.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.utils.HkUtils;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.TimeUtil;
import com.hikvision.netsdk.NET_DVR_TIME;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/5/17.
 */

public class DialogTimeSetlFragment extends DialogFragment {


    @Bind(R.id.sys_title)
    TextView sysTitle;
    @Bind(R.id.new_act_date_picker)
    DatePicker newActDatePicker;
    @Bind(R.id.new_act_time_picker)
    TimePicker newActTimePicker;
    @Bind(R.id.tv_current_jixin_time)
    TextView tv_current_time;
    @Bind(R.id.sw_osd_time_show)
    Switch sw_osd_show;
    @Bind(R.id.tv_osd_time_show)
    TextView tv_osd_show;

    private View mView;
    private boolean m_isShowTime;
//    private HkTimeSetPresent hkTimeSetPresent;


    public static DialogTimeSetlFragment getInstance() {
        DialogTimeSetlFragment dialogNormalFragment = new DialogTimeSetlFragment();
        Bundle bundle = new Bundle();

        dialogNormalFragment.setArguments(bundle);
        return dialogNormalFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.time_set, null);
        ButterKnife.bind(this, mView);
//        hkTimeSetPresent = new HkTimeSetPresentImpl();

        newActTimePicker.setIs24HourView(true);


        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        dialog.setCanceledOnTouchOutside(true);
        setWindowStyle(dialog);

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                NET_DVR_TIME net_dvr_time = HkUtils.getTime();
                StringBuilder stringBuilder = null;
                if (net_dvr_time != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(net_dvr_time.dwYear)
                            .append("-")
                            .append(net_dvr_time.dwMonth)
                            .append("-").append(net_dvr_time.dwDay < 10 ? 0 : "")
                            .append(net_dvr_time.dwDay)
                            .append(" ").append(net_dvr_time.dwHour < 10 ? 0 : "")
                            .append(net_dvr_time.dwHour)
                            .append(":").append(net_dvr_time.dwMinute < 10 ? 0 : "")
                            .append(net_dvr_time.dwMinute);
                }

                if (tv_current_time != null) {
                    final StringBuilder finalStringBuilder = stringBuilder;
                    tv_current_time.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalStringBuilder != null) {
                                tv_current_time.setText(finalStringBuilder.toString());

                            } else {
                                tv_current_time.setText(getString(R.string.no_get));
                            }
                        }
                    });
                }

            }
        }).start();


        boolean isNotShowTime = BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_OSD_IS_NOT_SHOW_TIME_ON_DEVICE, false);
        m_isShowTime = !isNotShowTime;
        if (isNotShowTime) {
            sw_osd_show.setChecked(false);
            tv_osd_show.setText(getString(R.string.close));
        } else {
            sw_osd_show.setChecked(true);
            tv_osd_show.setText(getString(R.string.open));
        }

        sw_osd_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_osd_show.setText(getString(R.string.open));
                } else {
                    tv_osd_show.setText(getString(R.string.close));
                }
                m_isShowTime = isChecked;
            }
        });

    }


    private void setWindowStyle(Dialog dialog) {
        Window window = dialog.getWindow();

        window.setWindowAnimations(R.style.dialog_anim);
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值

        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);
        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(mView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.tv_cancel, R.id.tv_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                break;
            case R.id.tv_sure:

                if(m_isShowTime) {
                    LogUtil.log("time = " + newActDatePicker.getYear() + " " +
                            newActDatePicker.getMonth() + " " +
                            newActDatePicker.getDayOfMonth() + " " +
                            newActTimePicker.getCurrentHour() + " " + newActTimePicker.getCurrentMinute() + " " + TimeUtil.getSecond());
                    HkUtils.setTime(newActDatePicker.getYear(),
                            newActDatePicker.getMonth() + 1,
                            newActDatePicker.getDayOfMonth(),
                            newActTimePicker.getCurrentHour(),
                            newActTimePicker.getCurrentMinute(),
                            Integer.valueOf(TimeUtil.getSecond()));
                }
                BaseApplication.getSharedPreferences().edit().putBoolean(Constant.KEY_OSD_IS_NOT_SHOW_TIME_ON_DEVICE, !m_isShowTime).commit();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HkUtils.setTongDaoName("", true);
                    }
                }).start();
                break;
        }
        dismiss();
    }


}
