package com.bmw.peek2.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.bmw.peek2.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/5/18.
 */

public class DialogBiaojiFragment extends DialogFragment {

    @Bind(R.id.biaoji_editext)
    EditText input;
    private View mView;

    public static DialogBiaojiFragment getInstance(String biaojiText) {
        DialogBiaojiFragment fragment = new DialogBiaojiFragment();
        Bundle bundle = new Bundle();
        bundle.putString("biaojiText", biaojiText);
        fragment.setArguments(bundle);
        return fragment;
    }


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
        p.width = (int) (dm.widthPixels * 0.5);    //宽度设置为全屏
        //设置生效
        getDialog().getWindow().setAttributes(p);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_biaoji, null);
        ButterKnife.bind(this, mView);
        input.setText(getArguments().getString("biaojiText"));

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        setWindowStyle(alertDialog);
        return alertDialog;
    }


    private void setWindowStyle(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
         DisplayMetrics dm = new DisplayMetrics();
       manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值

        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels*0.6);    //宽度设置为全屏
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

    @OnClick({R.id.biaoji_sure, R.id.biaoji_cancel, R.id.biaoji_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.biaoji_sure:
                dismiss();
                if(listener != null)
                    listener.finish(input.getText().toString());
                break;
            case R.id.biaoji_cancel:
                dismiss();
                break;
            case R.id.biaoji_clear:
                input.post(new Runnable() {
                    @Override
                    public void run() {
                        input.setText("");
                    }
                });
                break;
        }
    }
    private OnSureClickListener listener;

    public void setOnSureClickListener(OnSureClickListener listener){
        this.listener = listener;
    }

    public interface OnSureClickListener{
        void finish(String inputData);
    }
}
