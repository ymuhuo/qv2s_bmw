package com.bmw.peek2.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.FragmentUtil;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.WifiUtil;
import com.bmw.peek2.view.dialog.AdvancedSetDialog;
import com.bmw.peek2.view.ui.SettingActivity;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/4/7.
 */

public class AdvanceSetFragment extends Fragment {

    @Bind(R.id.switch_advance_yingjiema)
    Switch switchYingjiema;
    @Bind(R.id.tv_advance_fileSavePath)
    Switch switchSavePath;
    @Bind(R.id.switch_advance_autoWifi)
    Switch switchAutoWifi;
    private View root;
//    private AdvanceSetStateInfo advanceSetStateInfo;
    private Intent mIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_advance_setting, container, false);
        ButterKnife.bind(this, root);

        initBroadcastSender();
        initSwitch();
        initYingJieMa();
        initFileSavePath();
        initSwitchAutoWifi();
        return root;
    }

    private void initSwitchAutoWifi() {
        switchAutoWifi.setChecked(Login_info.getInstance().isWifi_auto());
        switchAutoWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Login_info.getInstance().setWifi_auto(b, true);
                if(b){
                   /* if(!WifiConnect.getInstance().getIsOpenThread()){
                        WifiConnect.getInstance().starConnect();
                    }*/
                    WifiUtil.startWifiService(getContext());
                }
            }
        });
    }

    private void initFileSavePath() {
        switchSavePath.setChecked(Login_info.getInstance().isSaveToExSdcard());
        switchSavePath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    sureCheckSDcard();
                else
                    Login_info.getInstance().setSaveToExSdcard(b);
            }
        });
    }

    private void sureCheckSDcard() {
        List<String> sdcardList = FileUtil.getRealExtSDCardPath(this.getActivity());
        if(sdcardList.size() <=1){
            onleInlaySdcardExist();
            return;
        }
        if(!sdcardList.get(sdcardList.size()-1).contains("Android/data")){
            Login_info.getInstance().setSaveToExSdcard(true);
            FileUtil.recordPathIsExist();
            FileUtil.capturePathIsExist();
            return;
        }

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(getString(R.string.alarm),getString(R.string.fileSaveDangerousIn4)+"\n\n  "+getString(R.string.fileSaveTo)+"\n"+
                sdcardList.get(1)+getString(R.string.canMove)+" \n\n "+getString(R.string.fileDeleteAfterAppDelete) +"\n\n "+getString(R.string.sureSaveToSdcard),null,null,true);
        dialogNormalFragment.isNeedDismissCancel(true);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                Login_info.getInstance().setSaveToExSdcard(true);
                FileUtil.recordPathIsExist();
                FileUtil.capturePathIsExist();
                LogUtil.log("save to sdcard sure");
            }

            @Override
            public void cancel() {
                if(!Login_info.getInstance().isSaveToExSdcard())
                    switchSavePath.setChecked(false);
            }
        });
        showDialogFragment(dialogNormalFragment,"DialogNormalFragment");
    }


    private void showDialogFragment(DialogFragment dialogFragment, String tag){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null)
            fragmentTransaction.remove(fragment);

        dialogFragment.show(fragmentTransaction,tag);
    }

    private void onleInlaySdcardExist() {
        BaseApplication.toast(getString(R.string.noOutSdcard));
        switchSavePath.setChecked(false);
    }

    private void initYingJieMa() {
        switchYingjiema.setChecked(Login_info.getInstance().isYingJieMa());
        switchYingjiema.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Login_info.getInstance().setYingJieMa(b);
                reLoginHK();
            }
        });
    }

    private void reLoginHK() {
        Intent intent = new Intent();
        intent.setAction("data.receiver");
        intent.putExtra("isReLoginHK",true);
        this.getActivity().sendBroadcast(intent);
    }

    private void initSwitch() {
//        Bundle bundle = getArguments();
//        advanceSetStateInfo = (AdvanceSetStateInfo) bundle.getSerializable("advance");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(root);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void initBroadcastSender() {
        mIntent = new Intent();
        mIntent.setAction("data.receiver");
    }


    @OnClick({
            R.id.tv_advance_cameraSet
            ,R.id.tv_advance_recordSet
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_advance_cameraSet:
                new AdvancedSetDialog(getActivity());
                break;
            case R.id.tv_advance_recordSet:
                DialogRecordSetFragment dialogRecordSetFragment = new DialogRecordSetFragment();
                if(getActivity()!=null){
                    WeakReference<SettingActivity> activityWeakReference = new WeakReference<SettingActivity>((SettingActivity) getActivity());
                    FragmentUtil.showDialogFragment(activityWeakReference.get().getSupportFragmentManager(),dialogRecordSetFragment,"DialogRecordSetFragment");
                }
                break;
           /* case R.id.tv_advance_linkSet:
//                getActivity().startActivity(new Intent(getActivity(), WifiActivity.class));
                new WifiLinkSetDialog(getActivity());
                break;*/
        }
    }

}
