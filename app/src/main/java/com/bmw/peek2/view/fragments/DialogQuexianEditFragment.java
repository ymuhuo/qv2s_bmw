package com.bmw.peek2.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.R;
import com.bmw.peek2.model.PictureQueXianInfo;
import com.bmw.peek2.model.QueXianInfo;
import com.bmw.peek2.utils.DbHelper;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.PullXmlParseUtil;
import com.bmw.peek2.view.adapter.TextSpinnerAdapter;
import com.bmw.peek2.view.dialog.ClockShow_Dialog;
import com.bmw.peek2.view.view.MySpinner;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/5/18.
 */

public class DialogQuexianEditFragment extends DialogFragment {


    @Bind(R.id.capture_quexian_distance_edt)
    EditText mDistanceEdt;
    @Bind(R.id.capture_quexian_style_sp)
    MySpinner mStyleSp;
    @Bind(R.id.capture_quexian_name_tv)
    TextView mNameTv;
    @Bind(R.id.capture_quexian_name_sp)
    MySpinner mNameSp;
    @Bind(R.id.capture_quexian_clock_tv)
    TextView mClockTv;
    @Bind(R.id.capture_quexian_clock_edt)
    EditText mClockEdt;
    @Bind(R.id.capture_quexian_grade_tv)
    TextView mGradeTv;
    @Bind(R.id.capture_quexian_grade_sp)
    MySpinner mGradeSp;
    @Bind(R.id.capture_quexian_length_tv)
    TextView mLengthTv;
    @Bind(R.id.capture_quexian_length_edt)
    EditText mLengthEdt;
    @Bind(R.id.capture_quexian_detail_tv)
    TextView mDetailTv;
    @Bind(R.id.capture_quexian_detail_edt)
    EditText mDetailEdt;
    private boolean isChangeInfo;
    private PictureQueXianInfo mPictureQueXianInfo;

    private String[] mArray_style;
    private int mStyle_id = -1;
    private int mGrade_id = -1;
    private TextSpinnerAdapter mQuexianNameAdapter, mGradeAdapter, mStyleAdapter;
    private int mName_id = -1;
    private List<QueXianInfo> mQuexianList;
    private View mView;
    private DbUtils dbUtils;
    private Handler handler = new Handler();

    public static DialogQuexianEditFragment getInstance(boolean isChange, PictureQueXianInfo pictureQueXianInfo) {
        DialogQuexianEditFragment dialogQuexianEditFragment = new DialogQuexianEditFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isChange", isChange);
        bundle.putSerializable("PictureQueXianInfo", pictureQueXianInfo);
        dialogQuexianEditFragment.setArguments(bundle);
        return dialogQuexianEditFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPictureQueXianInfo = (PictureQueXianInfo) getArguments().getSerializable("PictureQueXianInfo");
        this.isChangeInfo = getArguments().getBoolean("isChange");
        dbUtils = DbHelper.getDbUtils();
        getXmlData();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picture_quexian_edit, null);
        ButterKnife.bind(this, mView);
        AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).setView(mView).create();
        setWindowStyle(alertDialog);

        initAdapter();
        initSpinner();
        if (mPictureQueXianInfo != null)
            initData();
        return alertDialog;
    }


    private void setWindowStyle(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        /* DisplayMetrics dm = new DisplayMetrics();
       manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值

        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels*0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);*/
        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
    }

    //获取基本数据
    private void getXmlData() {
        mArray_style = getActivity().getResources().getStringArray(R.array.que_xian_style);
        try {
            mQuexianList = PullXmlParseUtil.parseQueXianXml(getActivity().getAssets().open("DefectTypes.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @OnClick({R.id.tv_quexianEdit_clockShow, R.id.quexian_cancel, R.id.quexian_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quexian_cancel:
                dismiss();
                break;
            case R.id.quexian_sure:
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(TextUtils.isEmpty(mStyleSp.getText().toString()) || mStyleSp.getText().equals("请选择")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    BaseApplication.toast(getResources().getString(R.string.chooseQuexianSort));
                                }
                            });
                            return;
                        }
                        PictureQueXianInfo pictureQueXianInfo = new PictureQueXianInfo(mDistanceEdt.getText().toString(), mStyleSp.getText().toString(),
                                mGradeSp.getText().toString(), mNameSp.getText().toString(),
                                mClockEdt.getText().toString(), mLengthEdt.getText().toString(),
                                mDetailEdt.getText().toString());
                        if (mStyle_id == -1) {
                            pictureQueXianInfo.setStyle("");
                        }
                        if (mName_id == -1) {
                            pictureQueXianInfo.setName("");
                        }
                        if (mGrade_id == -1) {
                            pictureQueXianInfo.setGrade("");
                        }

                        switch (mStyle_id) {
                            case 0:
                                pictureQueXianInfo.setGrade("");
                                pictureQueXianInfo.setName("");
                                break;
                            case 1:
                                if(mName_id< 0 )
                                    break;
                                pictureQueXianInfo.setName(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getName());
                                if(mGrade_id <0)
                                    break;
                                pictureQueXianInfo.setGrade(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel()));

                                break;
                            case 2:
                                if(mName_id < 0)
                                    break;
                                pictureQueXianInfo.setName(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getName());
                                if(mGrade_id < 0 )
                                    break;
                                pictureQueXianInfo.setGrade(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel()));

                                break;
                        }

                        try {
                            if (!isChangeInfo) {
                                dbUtils.save(pictureQueXianInfo);
                            } else {
                                dbUtils.update(pictureQueXianInfo, WhereBuilder.b("id", "=", mPictureQueXianInfo.getId()));
                            }

                            LogUtil.log("数据库：保存完成","");
                            if (listener != null)
                                listener.finish();
                            dismiss();
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.tv_quexianEdit_clockShow:
                new ClockShow_Dialog(getActivity());
                break;
        }
    }


    //初始化adapter
    private void initAdapter() {

        mQuexianNameAdapter = new TextSpinnerAdapter(getActivity());
        mGradeAdapter = new TextSpinnerAdapter(getActivity());
        mStyleAdapter = new TextSpinnerAdapter(getActivity());


        mGradeSp.setAdapter(mGradeAdapter);
        mNameSp.setAdapter(mQuexianNameAdapter);
        mStyleSp.setAdapter(mStyleAdapter);

        mStyleAdapter.setStrings(mArray_style);
    }


    private void initSpinner() {

        if (TextUtils.isEmpty(mStyleSp.getText().toString())) {
            mStyleSp.setText(getResources().getString(R.string.pleaseChoose));
        }

        mStyleSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStyleSp.setText(mArray_style[i]);
                mStyle_id = i;
                setNameAdapter(i);
                if (i != 0)
                    mNameSp.setText(getResources().getString(R.string.pleaseChoose));
                else{
                    mName_id = -1;
                    mGrade_id = -1;
                }
                mStyleSp.dismissPop();
            }
        });
        mNameSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mName_id = i;
                mNameSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(i).getName());
                setGradeAdapter(i);
                mGradeSp.setText(getResources().getString(R.string.pleaseChoose));
                mNameSp.dismissPop();
            }
        });

        mGradeSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mGrade_id = i;
                mGradeSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel() + "");
                if (mStyle_id != 0) {
                    String detailText = mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getContent();
                    mDetailEdt.setText(detailText);
                }
                mGradeSp.dismissPop();
            }
        });
    }


    private void initData() {
        mDistanceEdt.setText(mPictureQueXianInfo.getDistance());

        for (int i = 0; i < mQuexianList.size(); i++) {
            if (mQuexianList.get(i).getName().equals(mPictureQueXianInfo.getStyle())) {
                mStyleSp.setText(mQuexianList.get(i).getName());
                mStyle_id = i;
            }
        }

        if (mStyle_id > 0) {

            for (int i = 0; i < mQuexianList.get(mStyle_id).getStyleList().size(); i++) {
                if (mQuexianList.get(mStyle_id).getStyleList().get(i).getName().equals(mPictureQueXianInfo.getName())) {
                    mNameSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(i).getName());
                    mName_id = i;
                }
            }
            if (mName_id != -1)
                for (int i = 0; i < mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().size(); i++) {
                    if (!TextUtils.isEmpty(mPictureQueXianInfo.getGrade()) && mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(i).getLevel() == Integer.valueOf(mPictureQueXianInfo.getGrade())) {
                        mGradeSp.setText(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(i).getLevel()));
                        mGrade_id = i;
                    }
                }
            mClockEdt.setText(mPictureQueXianInfo.getClock());

            mLengthEdt.setText(mPictureQueXianInfo.getLength());
            mDetailEdt.setText(mPictureQueXianInfo.getDetail());
            if (mStyle_id > -1) {
                mStyleAdapter.setStrings(mArray_style);
                mStyleSp.setText(mArray_style[mStyle_id]);
                setNameAdapter(mStyle_id);
                mNameSp.setText(getResources().getString(R.string.pleaseChoose));
            }
            if (mName_id > -1) {
                setNameAdapter(mName_id);
                mNameSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getName());
                setGradeAdapter(mName_id);
                mGradeSp.setText(getResources().getString(R.string.pleaseChoose));
            }
            if (mGrade_id > -1) {
                setGradeAdapter(mGrade_id);
                mGradeSp.setText(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel()));
            }

        }

    }


    private void setGradeAdapter(int i) {
        if (mStyle_id != 0) {
            int num = mQuexianList.get(mStyle_id).getStyleList().get(i).getGradeList().size();
            String[] gradeList = new String[num];
            for (int x = 0; x < gradeList.length; x++) {
                gradeList[x] = String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(i).getGradeList().get(x).getLevel());
            }
            mGradeAdapter.setStrings(gradeList);
        }
    }

    private void setNameAdapter(int i) {
        switch (i) {
            case 0:
                setEditText(false);
                break;
            case 1:
                setEditText(true);

                String[] jiegouList = new String[mQuexianList.get(1).getStyleList().size()];
                for (int x = 0; x < jiegouList.length; x++) {
                    jiegouList[x] = mQuexianList.get(1).getStyleList().get(x).getName();
                }

                mQuexianNameAdapter.setStrings(jiegouList);
                break;
            case 2:
                setEditText(true);
                String[] gongnengList = new String[mQuexianList.get(2).getStyleList().size()];
                for (int x = 0; x < gongnengList.length; x++) {
                    gongnengList[x] = mQuexianList.get(2).getStyleList().get(x).getName();
                }
                mQuexianNameAdapter.setStrings(gongnengList);
                break;
        }
    }

    private void setEditText(boolean b) {

        mNameSp.setEnabled(b);
        mGradeSp.setEnabled(b);
        mClockEdt.setEnabled(b);
        mLengthEdt.setEnabled(b);
        mDetailEdt.setEnabled(b);
        mNameSp.setText("");
        mGradeSp.setText("");
        if (!b) {
            mNameTv.setTextColor(getActivity().getResources().getColor(R.color.gray_10));
            mClockTv.setTextColor(getActivity().getResources().getColor(R.color.gray_10));
            mGradeTv.setTextColor(getActivity().getResources().getColor(R.color.gray_10));
            mLengthTv.setTextColor(getActivity().getResources().getColor(R.color.gray_10));
            mDetailTv.setTextColor(getActivity().getResources().getColor(R.color.gray_10));
            mDetailEdt.setText("");
            mClockEdt.setText("");
            mLengthEdt.setText("");

        } else {
            mNameTv.setTextColor(getActivity().getResources().getColor(R.color.colorText));
            mClockTv.setTextColor(getActivity().getResources().getColor(R.color.colorText));
            mGradeTv.setTextColor(getActivity().getResources().getColor(R.color.colorText));
            mLengthTv.setTextColor(getActivity().getResources().getColor(R.color.colorText));
            mDetailTv.setTextColor(getActivity().getResources().getColor(R.color.colorText));
        }
    }


    public interface OnDataChangeListener {
        void finish();
    }

    private OnDataChangeListener listener;

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.listener = listener;
    }
}
