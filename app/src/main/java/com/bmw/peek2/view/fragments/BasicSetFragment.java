package com.bmw.peek2.view.fragments;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.model.UpdateInfo;
import com.bmw.peek2.presenter.impl.ControlPresentImpl2;
import com.bmw.peek2.presenter.impl.UpdatePresentImpl;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.FragmentUtil;
import com.bmw.peek2.utils.Manufacturer_FileUtil;
import com.bmw.peek2.utils.NetWorkUtil;
import com.bmw.peek2.view.dialog.ScreenLightDialog;
import com.bmw.peek2.view.dialog.SettingDialog;
import com.bmw.peek2.view.dialog.SystemMsgDialog;
import com.bmw.peek2.view.ui.SettingActivity;
import com.bmw.peek2.view.ui.UpdateActivity;
import com.bmw.peek2.view.viewImpl.UpdateImpl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.bmob.v3.Bmob;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.bmw.peek2.utils.Manufacturer_FileUtil.readFileMessage;

/**
 * Created by admin on 2017/4/7.
 */

public class BasicSetFragment extends Fragment {
    private View root;
    private boolean isInitBmob;
    @Bind(R.id.rl_basic_set_systemSet)
    RelativeLayout rl_sys_set;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_basic_setting, container, false);
        ButterKnife.bind(this, root);

        if (Login_info.baseMainFrameWifiSSID.contains("RD")) {
            rl_sys_set.setVisibility(View.VISIBLE);
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(!isInitBmob)
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                isInitBmob = true;
//                //默认初始化
//                Bmob.initialize(getActivity(), "2733df1cda91841bbac921e164dc70d4");
//            }
//        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(root);
    }

    private void setScreenLight() {
        //取得当前亮度
        int normal = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);
//        AdvancedSetDialog dialog = new AdvancedSetDialog(this,normal);
//        ScreenPopupWindow dialog = new ScreenPopupWindow(this,normal);
        ScreenLightDialog dialog = new ScreenLightDialog(getActivity(), normal);
//        dialog.showPopupWindow(main);
        dialog.setDialogSeeekbarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //取得当前进度
                int tmpInt = seekBar.getProgress();

                //当进度小于80时，设置成40，防止太黑看不见的后果。
                if (tmpInt < 40) {
                    tmpInt = 40;
                }

                //根据当前进度改变亮度
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, tmpInt);
                tmpInt = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, -1);
                WindowManager.LayoutParams wl = getActivity().getWindow()
                        .getAttributes();

                float tmpFloat = (float) tmpInt / 255;
                if (tmpFloat > 0 && tmpFloat <= 1) {
                    wl.screenBrightness = tmpFloat;
                }
                getActivity().getWindow().setAttributes(wl);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void updateApk(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), name)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({
            R.id.systemTest_set
            , R.id.light_set
            , R.id.sys_stat
            , R.id.sys_update
            , R.id.tv_jixin_time
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_jixin_time:
                DialogTimeSetlFragment dialogTimeSetlFragment = new DialogTimeSetlFragment();
                if (getActivity() != null) {
                    WeakReference<SettingActivity> activityWeakReference = new WeakReference<SettingActivity>((SettingActivity) getActivity());
                    FragmentUtil.showDialogFragment(activityWeakReference.get().getSupportFragmentManager(), dialogTimeSetlFragment, "DialogTimeSetlFragment");
                }


                break;

            case R.id.systemTest_set:
                SettingDialog settingDialog = new SettingDialog(getActivity());
                settingDialog.setOnSettingChangeListener(new SettingDialog.OnSettingChangeListener() {
                    @Override
                    public void changeReporter(boolean isChange) {
                        if (isChange)
                            new ControlPresentImpl2().resetSocket();
                    }
                });
                break;
            case R.id.light_set:
                setScreenLight();
                break;
            case R.id.sys_stat:
                new SystemMsgDialog(this.getActivity(), getVersion());
                break;
            case R.id.sys_update:
                DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(
                        getString(R.string.isUpdateApp),
                        getString(R.string.updateAppAndClosePreview),
                        getString(R.string.sure),
                        getString(R.string.cancel), true);
                dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
                    @Override
                    public void sure() {
                        startActivity(new Intent(BasicSetFragment.this.getActivity(), UpdateActivity.class));
                    }

                    @Override
                    public void cancel() {

                    }
                });
                FragmentUtil.showDialogFragment(getActivity().getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");

                break;
        }
    }

    @OnLongClick({
            R.id.sys_stat
    })
    public boolean onLongClick(View v) {
        if (Constant.IS_NEUTRAL_VERSION) {
            final DialogEdtNormalFragment dialogEdtNormalFragment = DialogEdtNormalFragment.getInstance("正在修改系统参数，请输入修改密码！"
                    , "", null, null, false);
            dialogEdtNormalFragment.setOnEdtDialogItemClickListener(new DialogEdtNormalFragment.OnEdtDialogItemClickListener() {
                @Override
                public void next_step(String msg) {
                    if (msg != null && msg.equals(Constant.VERSION_PASS)) {
                        setManufacturerName();
                    } else {
                        BaseApplication.toast(getString(R.string.error_pass));
                        dialogEdtNormalFragment.dismiss();
                    }
                }

                @Override
                public void cancel() {
                    dialogEdtNormalFragment.dismiss();
                }
            });
            FragmentUtil.showDialogFragment(getActivity().getSupportFragmentManager(), dialogEdtNormalFragment, "DialogEdtNormalFragment");
        }
        return true;
    }


    private void setManufacturerName() {
        String manufacturerName = Manufacturer_FileUtil.readFileMessage("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt");
        String deviceName = readFileMessage("mnt/sdcard/Android/obj/com.bmw.peek2s", "device.txt");
        if (manufacturerName == null)
            manufacturerName = "";
        if (deviceName == null)
            deviceName = "";
        DialogManufacturerFragment dialogEdtNormalFragment = DialogManufacturerFragment.getInstance("正在修改生产商名称！"
                , manufacturerName, Constant.LOGO_PATH, deviceName, null, null, false);
        dialogEdtNormalFragment.setOnEdtDialogItemClickListener(new DialogManufacturerFragment.OnManufactureFinishListener() {
            @Override
            public void finish(final String manufacturerName, final String imgPath, final String devicePath) {
                BaseApplication.MAIN_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveData(manufacturerName, imgPath, devicePath);
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });
        FragmentUtil.showDialogFragment(getActivity().getSupportFragmentManager(), dialogEdtNormalFragment, "DialogManufacturerFragment");
    }

    private void saveData(String manufacturerName, String imgPath, String deviceName) {
        boolean isSet = false;
        if (manufacturerName == null || manufacturerName.isEmpty()) {
            File file = new File("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt");
            if (file.exists())
                isSet = file.delete();
            else
                isSet = true;
        } else {
            isSet = Manufacturer_FileUtil.writeFile("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt", manufacturerName);
        }

        if (deviceName == null || deviceName.isEmpty()) {
            File file = new File("mnt/sdcard/Android/obj/com.bmw.peek2s", "device.txt");
            if (file.exists())
                isSet = file.delete();
            else
                isSet = true;
        } else {
            isSet = Manufacturer_FileUtil.writeFile("mnt/sdcard/Android/obj/com.bmw.peek2s", "device.txt", deviceName);
        }

        if (imgPath != null && !imgPath.equals(Constant.LOGO_PATH)) {
            isSet = FileUtil.replaceImage(imgPath, Constant.LOGO_PATH);
        } else {
            File file = new File(Constant.LOGO_PATH);
            if (file.exists()) {
                file.delete();
                FileUtil.updateSystemLibFile(Constant.LOGO_PATH);
            }
        }


        final boolean isSetFinal = isSet;

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isSetFinal) {
                    BaseApplication.toast("设置成功");
                } else {
                    BaseApplication.toast("设置失败，请稍后重试！");
                }
            }
        });
    }

    Handler mainHandler = new Handler(Looper.getMainLooper());


    private void setDateAndTime() {
        View view = View.inflate(getActivity().getApplicationContext(), R.layout.time_set, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.new_act_time_picker);

        // Init DatePicker
        int year;
        int month;
        int day;

        year = 2017;
        month = 9;
        day = 1;

        datePicker.init(year, month, day, null);

        // Init TimePicker
        int hour;
        int minute;

        hour = 20;
        minute = 0;

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        // Build DateTimeDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("time");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                arrive_year = datePicker.getYear();
//                arrive_month = datePicker.getMonth();
//                arrive_day = datePicker.getDayOfMonth();
//                String dateStr = DateUtil.formatDate(arrive_year, arrive_month, arrive_day);
//                arriveDateBtn.setText(dateStr);
//
//                arrive_hour = timePicker.getCurrentHour();
//                arrive_min = timePicker.getCurrentMinute();
//                String timeStr = DateUtil.formatTime(arrive_hour, arrive_min);
//                arriveTimeBtn.setText(timeStr);
            }
        });
        builder.show();
    }


    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getActivity().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getString(R.string.noVersion);
        }
    }


}
