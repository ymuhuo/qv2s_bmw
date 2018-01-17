package com.bmw.peek2.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.model.BZInfo;
import com.bmw.peek2.utils.BiaoZhuDBUtil;
import com.bmw.peek2.view.adapter.TextSpinnerAdapter;
import com.bmw.peek2.view.view.EditSpinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/5/18.
 */

public class DialogBiaojiMutiFragment extends DialogFragment {

    @Bind(R.id.tiEdt_row1)
    EditSpinner tiEdtRow1;
    @Bind(R.id.tiEdt_row2)
    EditSpinner tiEdtRow2;
    @Bind(R.id.tiEdt_row3)
    EditSpinner tiEdtRow3;
    @Bind(R.id.tiEdt_row4)
    EditSpinner tiEdtRow4;
    @Bind(R.id.tl_row3)
    TextInputLayout tl_row3;
    @Bind(R.id.tl_row4)
    TextInputLayout tl_row4;
    private View mView;
    private TextSpinnerAdapter text1Adapter, text2Adapter, text3Adapter, text4Adapter;
    private BiaoZhuDBUtil mBiaoZhuUtil;

    public static DialogBiaojiMutiFragment getInstance(
            String biaojiText1
            , String biaojiText2
            , String biaojiText3
            , String biaojiText4
    ) {
        DialogBiaojiMutiFragment fragment = new DialogBiaojiMutiFragment();
        Bundle bundle = new Bundle();
        bundle.putString("biaojiText1", biaojiText1);
        bundle.putString("biaojiText2", biaojiText2);
        bundle.putString("biaojiText3", biaojiText3);
        bundle.putString("biaojiText4", biaojiText4);
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
        p.width = (int) (dm.widthPixels * 0.6);    //宽度设置为全屏
        //设置生效
        getDialog().getWindow().setAttributes(p);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_biaoji_four, null);
        ButterKnife.bind(this, mView);
        if (BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_RECORDHEADER_ALWAYS_SHOW, false)) {
            tl_row3.setVisibility(View.GONE);
            tl_row4.setVisibility(View.GONE);
        }

        tiEdtRow1.setText(getArguments().getString("biaojiText1"));
        tiEdtRow2.setText(getArguments().getString("biaojiText2"));
        tiEdtRow3.setText(getArguments().getString("biaojiText3"));
        tiEdtRow4.setText(getArguments().getString("biaojiText4"));

        mBiaoZhuUtil = new BiaoZhuDBUtil();

        initSpinnerAdapter();
        initSpinnerData();
        initText1HistoryText();

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        setWindowStyle(alertDialog);
        return alertDialog;
    }


    private void initSpinnerAdapter() {
        text1Adapter = new TextSpinnerAdapter(getContext());
        tiEdtRow1.setAdapter(text1Adapter);
        text2Adapter = new TextSpinnerAdapter(getContext());
        tiEdtRow2.setAdapter(text2Adapter);
        text3Adapter = new TextSpinnerAdapter(getContext());
        tiEdtRow3.setAdapter(text3Adapter);
        text4Adapter = new TextSpinnerAdapter(getContext());
        tiEdtRow4.setAdapter(text4Adapter);

    }

    private void initSpinnerData() {
        text1Adapter.setStrings(mBiaoZhuUtil.getRowDataString(1));
        text2Adapter.setStrings(mBiaoZhuUtil.getRowDataString(2));
        text3Adapter.setStrings(mBiaoZhuUtil.getRowDataString(3));
        text4Adapter.setStrings(mBiaoZhuUtil.getRowDataString(4));

    }

    private void initText1HistoryText() {
        tiEdtRow1.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tiEdtRow1.setText(text1Adapter.getString(position));
                tiEdtRow1.dismissPop();
            }
        });

        tiEdtRow1.setOnItemLongSelectecListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mBiaoZhuUtil.delete(text1Adapter.getString(position), 1);
                text1Adapter.setStrings(mBiaoZhuUtil.getRowDataString(1));
                tiEdtRow1.dismissPop();
                tiEdtRow1.showPop();

                return true;
            }
        });

        tiEdtRow2.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tiEdtRow2.setText(text2Adapter.getString(position));
                tiEdtRow2.dismissPop();
            }
        });

        tiEdtRow2.setOnItemLongSelectecListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mBiaoZhuUtil.delete(text2Adapter.getString(position), 2);
                text2Adapter.setStrings(mBiaoZhuUtil.getRowDataString(2));
                tiEdtRow2.dismissPop();
                tiEdtRow2.showPop();

                return true;
            }
        });

        tiEdtRow3.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tiEdtRow3.setText(text3Adapter.getString(position));
                tiEdtRow3.dismissPop();
            }
        });

        tiEdtRow3.setOnItemLongSelectecListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mBiaoZhuUtil.delete(text3Adapter.getString(position), 3);
                text3Adapter.setStrings(mBiaoZhuUtil.getRowDataString(3));
                tiEdtRow3.dismissPop();
                tiEdtRow3.showPop();

                return true;
            }
        });

        tiEdtRow4.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tiEdtRow4.setText(text4Adapter.getString(position));
                tiEdtRow4.dismissPop();
            }
        });

        tiEdtRow4.setOnItemLongSelectecListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mBiaoZhuUtil.delete(text4Adapter.getString(position), 4);
                text4Adapter.setStrings(mBiaoZhuUtil.getRowDataString(4));
                tiEdtRow4.dismissPop();
                tiEdtRow4.showPop();

                return true;
            }
        });

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
        p.width = (int) (dm.widthPixels * 0.6);    //宽度设置为全屏
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

    @OnClick({R.id.biaoji_sure, R.id.biaoji_cancel, R.id.biaoji_clear, R.id.biaoji_clear_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.biaoji_clear_history:
                mBiaoZhuUtil.deleteAll();
                text1Adapter.setStrings(mBiaoZhuUtil.getRowDataString(1));
                text2Adapter.setStrings(mBiaoZhuUtil.getRowDataString(2));
                text3Adapter.setStrings(mBiaoZhuUtil.getRowDataString(3));
                text4Adapter.setStrings(mBiaoZhuUtil.getRowDataString(4));
                break;
            case R.id.biaoji_sure:
                dismiss();
                if (listener != null)
                    listener.finish(tiEdtRow1.getText().toString()
                            , tiEdtRow2.getText().toString()
                            , tiEdtRow3.getText().toString()
                            , tiEdtRow4.getText().toString()
                    );

                if (!TextUtils.isEmpty(tiEdtRow1.getText().toString())) {
                    mBiaoZhuUtil.add(new BZInfo(0, tiEdtRow1.getText().toString(), System.currentTimeMillis(), 1), null);
                }
                if (!TextUtils.isEmpty(tiEdtRow2.getText().toString())) {
                    mBiaoZhuUtil.add(new BZInfo(0, tiEdtRow2.getText().toString(), System.currentTimeMillis(), 2), null);
                }
                if (!TextUtils.isEmpty(tiEdtRow3.getText().toString())) {
                    mBiaoZhuUtil.add(new BZInfo(0, tiEdtRow3.getText().toString(), System.currentTimeMillis(), 3), null);
                }
                if (!TextUtils.isEmpty(tiEdtRow4.getText().toString())) {
                    mBiaoZhuUtil.add(new BZInfo(0, tiEdtRow4.getText().toString(), System.currentTimeMillis(), 4), null);
                }
                break;
            case R.id.biaoji_cancel:
                dismiss();
                break;
            case R.id.biaoji_clear:
                tiEdtRow1.post(new Runnable() {
                    @Override
                    public void run() {
                        tiEdtRow1.setText("");
                        tiEdtRow2.setText("");
                        tiEdtRow3.setText("");
                        tiEdtRow4.setText("");
                    }
                });
                break;
        }
    }

    private OnMutlSureClickListener listener;

    public void setOnMutlSureClickListener(OnMutlSureClickListener listener) {
        this.listener = listener;
    }

    public interface OnMutlSureClickListener {
        void finish(
                String value1
                , String value2
                , String value3
                , String value4
        );
    }
}
