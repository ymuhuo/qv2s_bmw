package com.bmw.peek2.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.view.adapter.SpinnerAdapter;
import com.bmw.peek2.view.ui.RecordHeadEditActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/5/17.
 */

public class DialogRecordSetFragment extends DialogFragment {

    @Bind(R.id.tv_advance_recordName)
    Switch switchRecordName;
    @Bind(R.id.tv_advance_recordPause)
    Switch switchRecordPause;
    @Bind(R.id.tv_advance_recordAlwayShowHeader)
    Switch switchShowRecordHeader;
    @Bind(R.id.tv_advance_recordKanban)
    Switch switchKanban;
    @Bind(R.id.sp_file_name_set_model)
    Spinner spinnerResetName;

    private View mView;


    @Override
    public void onStart() {
        super.onStart();
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();  //获取对话框当前的参数值

//        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.6);    //宽度设置为全屏
        //设置生效
        getDialog().getWindow().setAttributes(p);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_record_setting, null);
        ButterKnife.bind(this, mView);

        SpinnerAdapter arr_adapter = new SpinnerAdapter(this.getContext(),
                android.R.layout.simple_spinner_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.file_name_set_model));
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResetName.setAdapter(arr_adapter);
        int position = BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET,0);
        spinnerResetName.setSelection(position,true);
        spinnerResetName.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_FILE_NAME_MODEL_MINESET, position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initSwitch();

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        dialog.setCanceledOnTouchOutside(true);
        setWindowStyle(dialog);

        return dialog;
    }

    private void initSwitch() {
        initSwitchRecordName();
        initSwitchRecordPause();
        initSwitchRecordHeader();
        initSwitchRecordKanban();
    }

    private void initSwitchRecordKanban() {
        switchKanban.setChecked(BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_OPEN_RECORD_KANBAN, false));
        switchKanban.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BaseApplication.getSharedPreferences().edit().putBoolean(Constant.KEY_IS_OPEN_RECORD_KANBAN, b).commit();
            }
        });
    }

    private void initSwitchRecordHeader() {
        switchShowRecordHeader.setChecked(BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_RECORDHEADER_ALWAYS_SHOW, Constant.IS_RECORDHEADER_ALWAYS_SHOW_DEFAULT));
        switchShowRecordHeader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BaseApplication.getSharedPreferences().edit().putBoolean(Constant.KEY_IS_RECORDHEADER_ALWAYS_SHOW, b).commit();
            }
        });
    }

    private void initSwitchRecordPause() {
        switchRecordPause.setChecked(BaseApplication.getSharedPreferences().getBoolean(Constant.RECORD_CAN_PAUSE, false));
        switchRecordPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BaseApplication.getSharedPreferences().edit().putBoolean(Constant.RECORD_CAN_PAUSE, b).commit();
            }
        });
    }

    private void initSwitchRecordName() {
        switchRecordName.setChecked(Login_info.getInstance().isShowFirstName());
        switchRecordName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Login_info.getInstance().setShowFirstName(b);
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


    @OnClick({R.id.title, R.id.tv_advance_recordHeadEdit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_advance_recordHeadEdit:
                Intent intent = new Intent(this.getActivity(), RecordHeadEditActivity.class);
                intent.putExtra(Constant.RECORD_IS_EDIT_MODEL, true);
                startActivity(intent);
                break;
        }
    }
}
