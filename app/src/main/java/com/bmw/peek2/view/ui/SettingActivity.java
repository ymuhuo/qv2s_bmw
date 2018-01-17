package com.bmw.peek2.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bmw.peek2.R;
import com.bmw.peek2.utils.BatteryViewUtil;
import com.bmw.peek2.view.fragments.AdvanceSetFragment;
import com.bmw.peek2.view.fragments.BasicSetFragment;
import com.bmw.peek2.view.fragments.DialogNormalFragment;
import com.bmw.peek2.view.fragments.EnvironmentSetFragment;
import com.bmw.peek2.view.fragments.OnDialogFragmentClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bmw.peek2.utils.FragmentUtil.showDialogFragment;

public class SettingActivity extends BaseActivity {



    @Bind(R.id.tv_battery_device)
    TextView battery_device;//设备电池电量显示
    @Bind(R.id.tv_battery_device_title)
    TextView battery_device_title;

    @Bind(R.id.tv_battery_terminal)
    TextView battery_terminal;//终端电池电量显示
    @Bind(R.id.tv_battery_terminal_title)
    TextView battery_terminal_title;

    @Bind(R.id.setting_containerRg)
    RadioGroup containerRg;
    @Bind(R.id.setting_fragment_contain)
    FrameLayout fragmentContain;
    private EnvironmentSetFragment environmentSetFragment;
    private AdvanceSetFragment advanceSetFragment;
    private BasicSetFragment basicSetFragment;
    private FragmentManager fragmentManager;

    private BroadcastReceiver batteryDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryViewUtil.setSystemBattery(context(),intent,battery_terminal,battery_terminal_title);
            BatteryViewUtil.setDeviceBattery(context(),intent,battery_device,battery_device_title);
        }
    };

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("data.receiver");
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryDataReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initBattery();
        initBroadcastReceiver();
        initFragment();
        initRadioGroup();
    }

    private void initBattery() {

        BatteryViewUtil.setDeviceBattery(context(),getIntent(),battery_device,battery_device_title);
    }


    private void initFragment() {
        basicSetFragment = new BasicSetFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.setting_fragment_contain, basicSetFragment);
        transaction.commit();


    }

    private void initRadioGroup() {
        containerRg.check(R.id.basic_setingRd);
        containerRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentById(R.id.setting_fragment_contain);
                switch (i) {
                    case R.id.basic_setingRd:
                        transaction1.replace(R.id.setting_fragment_contain, basicSetFragment);
                        transaction1.remove(fragment);
                        break;
                    case R.id.advance_settingRd:
                        if (advanceSetFragment == null) {
                            advanceSetFragment = new AdvanceSetFragment();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("advance", mAdvanceSetStateInfo);
//                            advanceSetFragment.setArguments(bundle);
                        }
                        transaction1.replace(R.id.setting_fragment_contain, advanceSetFragment);
                        transaction1.remove(fragment);
                        break;
                    case R.id.environment_settingRd:
                        if (environmentSetFragment == null)
                            environmentSetFragment = new EnvironmentSetFragment();
                        transaction1.replace(R.id.setting_fragment_contain, environmentSetFragment);
                        transaction1.remove(fragment);
                        break;
                }
                transaction1.commit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(batteryDataReceiver);
    }

    @OnClick({R.id.setting_goback, R.id.preview_closeApp})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_closeApp:
                btnClickCloseApp();
                break;
            case R.id.setting_goback:
                removeActivity();
                break;
        }
    }

    private void btnClickCloseApp() {

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(getString(R.string.exitingApp), getString(R.string.exitAppSure), null, null, true);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                removeALLActivity();
            }

            @Override
            public void cancel() {

            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }


    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK

            removeActivity();
            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
