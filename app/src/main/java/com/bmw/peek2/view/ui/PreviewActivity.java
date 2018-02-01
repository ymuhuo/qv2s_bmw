package com.bmw.peek2.view.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.model.VideoInfo;
import com.bmw.peek2.model.All_id_Info;
import com.bmw.peek2.model.CapturePicture;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.model.RecordTaskInfo;
import com.bmw.peek2.presenter.BaiduMapLocatePresenter;
import com.bmw.peek2.presenter.CharacterOverLapingPresenter;
import com.bmw.peek2.presenter.ControlPresenter;
import com.bmw.peek2.presenter.HCNetSdkLogin;
import com.bmw.peek2.presenter.VideoCutPresenter;
import com.bmw.peek2.presenter.impl.BaiduMapLocateImpl;
import com.bmw.peek2.presenter.impl.CharacterOverLapingImpl;
import com.bmw.peek2.presenter.impl.ControlPresentImpl2;
import com.bmw.peek2.presenter.impl.HCNetSdkLoginImpl;
import com.bmw.peek2.presenter.impl.VideoCutPresentImpl;
import com.bmw.peek2.utils.DbHelper;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.FragmentUtil;
import com.bmw.peek2.utils.BatteryViewUtil;
import com.bmw.peek2.utils.HkUtils;
import com.bmw.peek2.utils.NetUtil;
import com.bmw.peek2.utils.PullXmlParseUtil;
import com.bmw.peek2.utils.StringUtils;
import com.bmw.peek2.utils.SystemUtil;
import com.bmw.peek2.utils.TimeUtil;
import com.bmw.peek2.utils.UrlUtil;
import com.bmw.peek2.utils.WifiAdmin;
import com.bmw.peek2.utils.WifiUtil;
import com.bmw.peek2.view.dialog.Capture_tishi_Dialog;
import com.bmw.peek2.view.dialog.ConnectStateDialog;
import com.bmw.peek2.view.fragments.DialogBiaojiMutiFragment;
import com.bmw.peek2.view.fragments.DialogEdtNormalFragment;
import com.bmw.peek2.view.fragments.DialogKanbanShowFragment;
import com.bmw.peek2.view.fragments.DialogNormalFragment;
import com.bmw.peek2.view.fragments.DialogRecordPauseFragment;
import com.bmw.peek2.view.fragments.OnDialogFragmentClickListener;
import com.bmw.peek2.view.view.CompositeImageText;
import com.bmw.peek2.view.view.Vertical_seekbar;
import com.bmw.peek2.view.viewImpl.PreviewImpl;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_TIME;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.bmw.peek2.utils.FragmentUtil.showDialogFragment;
import static com.bmw.peek2.utils.Manufacturer_FileUtil.readFileMessage;

public class PreviewActivity extends BaseActivity implements PreviewImpl {

    @Bind(R.id.main_surface)
    SurfaceView surfaceView;

    @Bind(R.id.tv_battery_device)
    TextView battery_device;//设备电池电量显示
    @Bind(R.id.tv_battery_device_title)
    TextView battery_device_title;

    @Bind(R.id.tv_battery_terminal)
    TextView battery_terminal;//终端电池电量显示
    @Bind(R.id.tv_battery_terminal_title)
    TextView battery_terminal_title;

    @Bind(R.id.lights_adjust)
    Vertical_seekbar lightAdjust;//灯光调节
    @Bind(R.id.lights_show)
    TextView light_text;
    @Bind(R.id.Records)
    CompositeImageText recordComposite;
    @Bind(R.id.ranging)
    CompositeImageText rangingComposite;
    @Bind(R.id.preview_clearFog)
    CompositeImageText clearFogComposite;
    @Bind(R.id.preview_locate)
    CompositeImageText locateComposite;
    @Bind(R.id.preview_moveAngel)
    CompositeImageText moveAngle;
    @Bind(R.id.preview_connectState)
    CompositeImageText connectState;

    private TextView tv_manufacturer;
    private TextView tv_manufacturer_title;
    private ImageView img_logo;
    private TextView tvDeviceName;

    private ControlPresenter cPresenter;//socket控制执行者
    private boolean isHighBeam;
    private int high_num; //记录远光灯
    private int low_num;  //记录近光灯
    private HCNetSdkLogin hcNetSdkLogin;
    private VideoCutPresenter videoCutPresenter;
    private boolean isRecordOpen;
    private boolean isRanging;  //测距
    private String[] mBiaojiTexts;
    private CharacterOverLapingPresenter mCharOverPresent;
    private boolean isClearFog;
    private boolean isOpenLocate;

    private double mLocation_jingdu;
    private double mLocation_weidu;
    private String mLaserRanging;
    private int mBatteryNum;
    private BaiduMapLocatePresenter baiduMapLocatePresenter;

    //版头任务名称、监测位置
    private String mTaskName, mTaskPlace;
    private Timer recordTimer;
    private TimerTask recordTimerTask;
    private long recordTime;
    private long recordLastTime;
    private boolean isAddNewRecordKanBan;

    private boolean isResetRanging;
    private String mStartWell;
    private String mEndWell;
    private RecordTaskInfo mRecordTaskInfo;
    private boolean mIsRecordHasData;
    private boolean isKanbanAddFinish;
    private boolean mIsCanShowBantou;
    private boolean mIsStop;
    private boolean mIsLowStorage;
    private VideoInfo mVideoInfo;
    private CapturePicture mCapturePicture;
    private long mCaptureTime;

//    private boolean isErrorPause;


    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float angle = intent.getFloatExtra("angle", -10000);
            if (angle != -10000) {
                if (moveAngle != null)
                    moveAngle.setText(angle + "°");
            }

            String rangingSize = intent.getStringExtra("rangingSize");
            if (rangingSize != null)
                setRanging(rangingSize);

//            String order = intent.getStringExtra("order");
//            if (order != null) {
//                orderFromBrocadcast(order);
//            }

            double latitude = intent.getDoubleExtra("latitude", -10000);   //纬度
            if (latitude != -10000) {
                latitude = (double) Math.round(latitude * 10000) / 10000;
                mLocation_weidu = latitude;
                if (mCharOverPresent != null) {
                    mCharOverPresent.setGpsAndRang(mLaserRanging, mLocation_jingdu, mLocation_weidu);
                }
            }

            double lontitude = intent.getDoubleExtra("lontitude", -10000); //经度
            if (lontitude != -10000) {
                lontitude = (double) Math.round(lontitude * 10000) / 10000;
                mLocation_jingdu = lontitude;
                if (mCharOverPresent != null) {
                    mCharOverPresent.setGpsAndRang(mLaserRanging, mLocation_jingdu, mLocation_weidu);
                }

            }

            boolean isGetLocate = intent.getBooleanExtra("isNotGetLocate", false);
            if (isGetLocate) {
                btnClickLocateSet();
            }

            boolean isReLoginHaikang = intent.getBooleanExtra("isReLoginHK", false);
            if (isReLoginHaikang)
                hcNetSdkLogin.reLoginHaiKang();


            BatteryViewUtil.setSystemBattery(context(), intent, battery_terminal, battery_terminal_title);
            mBatteryNum = BatteryViewUtil.setDeviceBattery(context(), intent, battery_device, battery_device_title);

            int isHasDataRecord = intent.getIntExtra(Constant.KEY_IS_RECORD_HAS_DATA, -1);
            if (isHasDataRecord != -1) {
                if (isHasDataRecord == 0) {
                } else {
                    mIsRecordHasData = true;
                    recordLastTime = System.currentTimeMillis();
                }
            }

            boolean isError = intent.getBooleanExtra(Constant.KEY_ERROR_PAUSE_CLOSE, false);
            if (isError) {
//                isErrorPause = isError;
            }

            boolean isWifiJustFinish = intent.getBooleanExtra(Constant.KEY_WIFI_STATE_JUST_FINISH, false);
            if (isWifiJustFinish) {
                wifiJustFinish();
            }

        }
    };
    private String kanbanPathDes;
    private ProgressDialog mProgressDialog;


    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("data.receiver");
        intentFilter.addAction(Constant.BROCAST_IS_RECORD_HAS_DATA);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(dataReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.welcome);
        if (Constant.IS_NEUTRAL_VERSION) {
            tv_manufacturer = (TextView) findViewById(R.id.welcom_manufacturer);
            tv_manufacturer_title = (TextView) findViewById(R.id.welcom_manufacturer_title);
            tvDeviceName = (TextView) findViewById(R.id.tv_deviceName);
            tvDeviceName.setText("");
            img_logo = (ImageView) findViewById(R.id.bominwell_logo);
            img_logo.setVisibility(View.GONE);
            tv_manufacturer.setVisibility(View.GONE);
            tv_manufacturer_title.setVisibility(View.GONE);
            String manufacturerName = readFileMessage("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt");
            String deviceName = readFileMessage("mnt/sdcard/Android/obj/com.bmw.peek2s", "device.txt");
            if (manufacturerName == null || manufacturerName.isEmpty()) {
            } else {
                tv_manufacturer.setText(manufacturerName);
                tv_manufacturer.setVisibility(View.VISIBLE);
                tv_manufacturer_title.setVisibility(View.VISIBLE);
            }
            if (deviceName == null || TextUtils.isEmpty(deviceName)) {
//                tvDeviceName.setText(getString(R.string.welcome_word));
            } else {
                tvDeviceName.setText(deviceName);
            }

            showLogoImage();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initOnCreate();
                    }
                });
            }
        }, 2000);

    }

    private void showLogoImage() {
        if (img_logo == null)
            return;
        File file = new File(Constant.LOGO_PATH);
        if (!file.exists())
            img_logo.setVisibility(View.GONE);
        else {
            Glide
                    .with(context())
                    .load(Constant.LOGO_PATH)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .crossFade()
                    .into(img_logo);
            img_logo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (mCharOverPresent != null && All_id_Info.getInstance().getM_iLogID() >= 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mCharOverPresent.setTongDaoName("", true);
                }
            }).start();
        }*/
    }

    private void initOnCreate() {
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        mBiaojiTexts = new String[4];
        initPresenter();

        initWifiConnect();
        initBroadcastReceiver();
        initLightAdjust();


//        hcNetSdkLogin.reLoginHaiKang();
        initShortCut();
    }

    @Override//回调改变提示信息
    public void iToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast(msg);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        mIsStop = true;
//        FileUtil.updateSystemFileList(null);
        log("previewActivity finish start!");
        releasePresenter();
        super.onDestroy();
//        System.exit(0);
    }

    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            /*if ((System.currentTimeMillis() - key_back_time) >= 5000) {
                key_back = 0;
            }
            if (key_back != 1) {
//               toast("再按一次返回键退出预览！");
                mToast.show();
                mToast.show();

                key_back_time = System.currentTimeMillis();
            }
            if (key_back == 1) {
                mToast.cancel();
                finish();
            }
            key_back++;*/

            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initRecordTimer() {
        recordTimer = new Timer();
        recordTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!Login_info.isPause) {

                    if (mIsLowStorage && (recordTime % 30) == 0) {
                        if (judgeIsLowStorageAndStopRecord())
                            return;
                    }

                    //断线情况下，中断录像计时
                    if (mIsRecordHasData && (System.currentTimeMillis() - recordLastTime > 1500)) {
                        mIsRecordHasData = false;
                        log("录像计时中断！");
                    }

                    if (recordComposite != null) {
                        recordComposite.post(new Runnable() {
                            @Override
                            public void run() {
                                recordComposite.setText(TimeUtil.formatTime(recordTime * 1000, true));
                            }
                        });
                    }

                    if (mIsRecordHasData)
                        recordTime++;

                    if (isAddNewRecordKanBan && recordTime >= 30) {
                        stopRecord();
                    }


                    if (recordTime != 0 && (recordTime % 1800) == 0) {
                        judgeIsAlarmStorage();
                    }
                }
            }
        };
        recordTimer.schedule(recordTimerTask, 0, 1000);
    }

    private void judgeIsAlarmStorage() {
        List<Float> diskSizeList = FileUtil.getDiskCapacity();
        float freeDisk = diskSizeList.get(1);
        log("录像判断剩余存储空间为：" + freeDisk);
        if (freeDisk <= 0.5)
            mIsLowStorage = true;
    }

    private boolean judgeIsLowStorageAndStopRecord() {

        List<Float> diskSizeList = FileUtil.getDiskCapacity();
        float freeDisk = diskSizeList.get(1);
        log("低存储空间时判断剩余量：" + freeDisk);
        if (freeDisk <= 0.12) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnClickRecords();
                    toast("存储空间不足，录像已停止！");
                }
            });
            return true;
        }
        return false;
    }

    private void initShortCut() {
        if (SystemUtil.isAddShortCut(this)) {
            log("检测到快捷方式已经创建");
            return;
        }
        Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.mipmap.peek2s_logo);
        ComponentName comp = new ComponentName(this.getPackageName(), this.getPackageName() + "." + this.getLocalClassName());
        Intent myIntent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
        myIntent.setClassName(this, getClass().getName());
        myIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
        sendBroadcast(addIntent);
        log("创建快捷方式");
    }

    private void initPresenter() {
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//        surfaceView.setZOrderMediaOverlay(true);
//        surfaceView.setZOrderOnTop(true);
//        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        hcNetSdkLogin = new HCNetSdkLoginImpl(getApplicationContext(), this, surfaceView);
        Login_info.getInstance().initLoginInfo(this);
//        SocketUtilNew.getInstance();
        cPresenter = new ControlPresentImpl2();
        videoCutPresenter = new VideoCutPresentImpl(this);
        mCharOverPresent = new CharacterOverLapingImpl();
        baiduMapLocatePresenter = new BaiduMapLocateImpl(this);

        justConnStateFromDevice();
        mToast = Toast.makeText(this, R.string.clickAgainToExit, Toast.LENGTH_SHORT);
    }

    private void justConnStateFromDevice() {
        /*NetUtil.getInstance().setOnNetUtilConnectStateChangeListener(new NetUtil.OnNetUtilConnectStateChangeListener() {
            @Override
            public void stateChange() {
              wifiJustFinish();
            }
        });*/
        NetUtil.getInstance().justNetConnect();
    }

    private void wifiJustFinish() {
        PreviewActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log("netUtil result!!!");
                if (connectState != null) {
                    boolean isVideoConnect = NetUtil.getInstance().isVideoConnect();

                    boolean isConnect = isVideoConnect &&
                            NetUtil.getInstance().isControlConnect();
                    WifiAdmin wifiAdmin = new WifiAdmin(BaseApplication.context());
                    String ssid = wifiAdmin.getSSID();
                    connectState.setImage(isConnect ? R.mipmap.connect : R.mipmap.disconnect);
                    String device = null;
                    if (!isConnect) {
                        device = getString(R.string.disconnect);
                    } else {
                        if (ssid.contains(Login_info.baseRepeaterWifiSSID))
                            device = getResources().getString(R.string.repeaterConnect);
                        else if (ssid.contains(Login_info.baseMainFrameWifiSSID))
                            device = getResources().getString(R.string.mainFrameConnect);
                        else
                            device = getResources().getString(R.string.alreadyConnect);
                    }
                    connectState.setText(device);
                    connectState.setTextColor(isConnect ? R.color.colorText : R.color.imageRed);

                    if (isVideoConnect) {
                        if (!surfaceView.isShown()) {
//                                    hcNetSdkLogin.logout();
                            surfaceView.setVisibility(View.VISIBLE);
//                                    hcNetSdkLogin.reLoginHaiKang();
                        }
                       /* if (mCharOverPresent != null)
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mCharOverPresent.setTongDaoName("", true);
                                }
                            }).start();*/
                    } else {
                        if (All_id_Info.getInstance().getM_iLogID() >= 0) {
                            hcNetSdkLogin.logout();
                        }
                        surfaceView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void releasePresenter() {
        videoCutPresenter.release();
        if (recordTimerTask != null)
            recordTimerTask.cancel();
        if (recordTimer != null)
            recordTimer.cancel();
        recordTimerTask = null;
        recordTimer = null;
        DbHelper.getDbUtils().close();
        if (baiduMapLocatePresenter != null)
            baiduMapLocatePresenter.destroy();
        unregisterReceiver(dataReceiver);

        mCharOverPresent.release();
        mToast.cancel();

        cPresenter.release();
        cPresenter = null;
        hcNetSdkLogin.release();
        new Thread(new Runnable() {
            @Override
            public void run() {
                hcNetSdkLogin.release();
            }
        }).start();
        Login_info.getInstance().release();
        ButterKnife.unbind(this);
        log("onDestroy:finish ");
    }

    private void initLightAdjust() {

        lightAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                log("seekbar: onProgressChanged: running");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                log("seekbar:  onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                log("seekbar: onStopTrackingTouch: ");
                if (isHighBeam) {
                    high_num = progress;
                    cPresenter.high_beam_open(high_num);
                } else {
                    low_num = progress;
                    cPresenter.low_beam_open(low_num);
                }
                if (progress == 0) {
                    if (isHighBeam)
                        cPresenter.high_beam_close();
                    else
                        cPresenter.low_beam_close();
                }
            }
        });
    }

    private void initWifiConnect() {
        if (Login_info.getInstance().isWifi_auto()) {
            WifiUtil.startWifiService(this);
        }

    }


    @OnClick({R.id.lights_switch,
            R.id.Records,
            R.id.screenShot,
            R.id.autoHorizontal,
            R.id.biaoji,
            R.id.ranging,
            R.id.preview_closeApp,
            R.id.preview_setting,
            R.id.preview_picture,
            R.id.preview_video,
            R.id.preview_connectState,
            R.id.preview_clearFog,
            R.id.preview_locate
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lights_switch:
                btnClickLight();

                break;
            case R.id.Records: //录像
                FileUtil.recordPathIsExist();
                btnClickRecords();
                break;
            case R.id.screenShot://截图
                FileUtil.capturePathIsExist();
                String captureName = getDesPathName(mTaskName, mTaskPlace, mStartWell, mEndWell);
                if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 2)
                    resetFileName(captureName, false);
                else {
                    String capturePath = getDesPath(captureName, false);
                    videoCutPresenter.capture(capturePath);
                }

                break;
            case R.id.autoHorizontal://自动水平
                mCharOverPresent.setOSD();
                cPresenter.autoHorizontal();

                break;
            case R.id.biaoji:
                btnClickBiaoji();
                break;
            case R.id.ranging:
                btnClickRanging();
                break;
            case R.id.preview_closeApp:
//                if (!isErrorPause)
                btnClickCloseApp();
                break;
            case R.id.preview_setting:
                btnClickSetting();
                break;
            case R.id.preview_picture:
                btnClickPicture();
                break;
            case R.id.preview_video:
                btnClickVideo();
                break;
            case R.id.preview_connectState:
                new ConnectStateDialog(this);
                break;
            case R.id.preview_clearFog:
                btnClickClearFog();
                autoCloseClearFog();
                break;
            case R.id.preview_locate:
                btnClickLocateSet();
                break;

        }
    }

    private void resetFileName(final String oldName, final boolean isRecord) {
        DialogEdtNormalFragment dialogEdtNormalFragment = DialogEdtNormalFragment.getInstance("请输入自定义文件名：", oldName, null, null, false);
        dialogEdtNormalFragment.setOnEdtDialogItemClickListener(new DialogEdtNormalFragment.OnEdtDialogItemClickListener() {
            @Override
            public void next_step(String msg) {
                String path = null;
                if (!TextUtils.isEmpty(msg) && !msg.equals(oldName))
                    path = getDesPath(msg, isRecord);
                else {
                    path = getDesPath(oldName, isRecord);
                }
                if (isRecord) {
                    videoCutPresenter.record(path, isAddNewRecordKanBan, kanbanPathDes);
                    mIsCanShowBantou = true;
                } else {
                    videoCutPresenter.capture(path);
                }
            }

            @Override
            public void cancel() {
                String path = getDesPath(oldName, isRecord);
                if (isRecord) {
                    videoCutPresenter.record(path, isAddNewRecordKanBan, kanbanPathDes);
                    mIsCanShowBantou = true;
                } else {
                    videoCutPresenter.capture(path);
                }
//                startActivity(new Intent(context(),SettingActivity.class));
            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogEdtNormalFragment, "DialogEdtNormalFragment");
    }


    private void btnClickCloseApp() {

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(getString(R.string.exitingApp),
                getString(R.string.exitAppSure), null, null, true);
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

    private void btnClickVideo() {
        log("回放按钮！");
        Intent intent2 = new Intent(this, FileShowNewActivity.class);
        intent2.putExtra("picture", false);
        intent2.putExtra("batteryNum", mBatteryNum);
        startActivity(intent2);
    }

    private void btnClickPicture() {
        log("图片按钮！");
        Intent intent = new Intent(this, FileShowNewActivity.class);
        intent.putExtra("picture", true);
        intent.putExtra("batteryNum", mBatteryNum);
        startActivity(intent);
    }

    private void btnClickSetting() {
        log("设置按钮！");
        Intent intentSeting = new Intent(this, SettingActivity.class);
        intentSeting.putExtra("batteryNum", mBatteryNum);
        cPresenter.isGetEnvironment(true);
        startActivityForResult(intentSeting, 0);
    }

    private void btnClickRanging() {
        log("测距按钮！");
        if (isRanging) {
            isRanging = false;
            rangingComposite.setImage(R.mipmap.ruler_gray);
            rangingComposite.setTextColor(R.color.white);
            cPresenter.isGetRanging(false);

        } else {
            isRanging = true;
            rangingComposite.setImage(R.mipmap.ruler);
            rangingComposite.setTextColor(R.color.colorText);
            cPresenter.isGetRanging(true);

        }
    }

    private void btnClickBiaoji() {
        log("标记按钮！");
        DialogBiaojiMutiFragment dialogBiaojiFragment = DialogBiaojiMutiFragment.getInstance(
                mBiaojiTexts[0]
                , mBiaojiTexts[1]
                , mBiaojiTexts[2]
                , mBiaojiTexts[3]
        );
        dialogBiaojiFragment.setOnMutlSureClickListener(new DialogBiaojiMutiFragment.OnMutlSureClickListener() {
            @Override
            public void finish(
                    String value1
                    , String value2
                    , String value3
                    , String value4
            ) {
                mBiaojiTexts[0] = value1;
                mBiaojiTexts[1] = value2;
                mBiaojiTexts[2] = value3;
                mBiaojiTexts[3] = value4;
                mCharOverPresent.setSingleSign(mBiaojiTexts);
            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogBiaojiFragment, "DialogBiaojiMutiFragment");

    }

    private void btnClickRecords() {
        log("录像按钮！");


        //获取可用内存大小
        if (!isRecordOpen) {
            List<Float> diskSizeList = FileUtil.getDiskCapacity();
            float freeDisk = diskSizeList.get(1);
            if (freeDisk > 0.15f && freeDisk < 0.5f) {
                showLowDiskAlarm(freeDisk);
            } else if (freeDisk <= 0.15f) {
                toast("无法录像，剩余存储空间严重不足！");
            } else {
                if (mIsLowStorage)
                    mIsLowStorage = false;
                recordOperator();
            }
        } else {
            recordOperator();
        }
    }

    private void showLowDiskAlarm(float freeDisk) {
        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance("存储警告！剩余存储空间过低！"
                , "警告，当前剩余存储空间为 " + (int) (freeDisk * 1024) + " M! 请及时删除无用文件，释放存储空间！"
                , "继续录像", "取消录像", false);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                recordOperator();
                if (!mIsLowStorage)
                    mIsLowStorage = true;
            }

            @Override
            public void cancel() {

            }
        });
        FragmentUtil.showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }

    private void recordOperator() {
        if (!isRecordOpen) {

            boolean isOpenRecordKanBan = BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_IS_OPEN_RECORD_KANBAN, false);
            if (isOpenRecordKanBan) {
                openRecordKanBan();
            } else {
                addRecordBanTou();
            }

        } else {
            boolean canPause = BaseApplication.getSharedPreferences().getBoolean(Constant.RECORD_CAN_PAUSE, false);
            if (canPause) {
                if (!Login_info.isPause) {
                    showRecordPauseDialog();
                } else {

//                    NET_DVR_MakeKeyFrame
                    HCNetSDK.getInstance().NET_DVR_MakeKeyFrame(All_id_Info.getInstance().getM_iLogID(), All_id_Info.getInstance().getM_iChanNum());

                    Login_info.isPause = false;
                    toast(getString(R.string.record_continue));
                }
            } else {
                stopRecord();
            }

        }
    }

    private void stopRecord() {
        videoCutPresenter.record(null, isAddNewRecordKanBan, null);
        mTaskName = null;
        mTaskPlace = null;
    }

    private void openRecordKanBan() {

        isAddNewRecordKanBan = false;
        kanbanPathDes = null;
        isKanbanAddFinish = false;
        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance("录像提示", "请选择是否添加看板录制", "开始录像", "添加看板录制", false);

        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {

                //正常录像
                addRecordBanTou();


            }

            @Override
            public void cancel() {
                //添加看板录制

//                StringBuilder sb = new StringBuilder();
//                sb.append(FileUtil.getFileSavePath());
//                sb.append(Login_info.local_kanban_path);
//                sb.append("001");
                DialogKanbanShowFragment dialogKanbanShowFragment = DialogKanbanShowFragment.getInstance();
                dialogKanbanShowFragment.setListener(new DialogKanbanShowFragment.OnKanbanChooseItemListener() {
                    @Override
                    public void result(String path) {
                        kanbanPathDes = path;
                        addRecordBanTou();
                    }

                    @Override
                    public void addNewKanban() {
                        //录制新的看板
                        kanbanPathDes = null;
                        isAddNewRecordKanBan = true;
                        int kanbanItem = getKanbanExistNum() / 3;
                        if (kanbanItem >= 5) {
                            //达到看板上限
                            toast(getString(R.string.kanban_shangxian));

                        } else {
//                    addRecordBanTou();
                            setKanbanName();
                        }
                    }

                    @Override
                    public void goback() {
                        openRecordKanBan();
                    }


                });

                showDialogFragment(getSupportFragmentManager(), dialogKanbanShowFragment, "DialogKanbanShowFragment");
//                kanbanPathDes = sb.toString();


            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }

    private int getKanbanExistNum() {

        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.getFileSavePath());
        sb.append(Login_info.local_kanban_path);

        return FileUtil.getFileExistNum(sb.toString());
    }

    private void addRecordBanTou() {

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance("录像提示", "是否添加版头？", "是", "否", false);

        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                Intent intent = new Intent(PreviewActivity.this, RecordHeadEditActivity.class);
                startActivityForResult(intent, 1);
            }

            @Override
            public void cancel() {
                String recordName = getDesPathName(null, null, null, null);
                if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 2 && !isAddNewRecordKanBan)
                    resetFileName(recordName, true);
                else {
                    String recordPath = getDesPath(recordName, true);
                    videoCutPresenter.record(recordPath, isAddNewRecordKanBan, kanbanPathDes);

                }
            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }


    private void setKanbanName() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.getFileSavePath());
        sb.append(Login_info.local_kanban_path);
        final String kanbanBaseName = FileUtil.getFileItemExit(sb.toString());
        log("kanban baseName = " + kanbanBaseName);
        DialogEdtNormalFragment dialogEdtNormalFragment = DialogEdtNormalFragment.getInstance("请输入看板名：", kanbanBaseName, null, null, false);
        dialogEdtNormalFragment.setOnEdtDialogItemClickListener(new DialogEdtNormalFragment.OnEdtDialogItemClickListener() {
            @Override
            public void next_step(String msg) {
                if (!TextUtils.isEmpty(msg))
                    BaseApplication.context().setKanbanName(msg);
                else
                    BaseApplication.context().setKanbanName(kanbanBaseName);
                addRecordBanTou();
            }

            @Override
            public void cancel() {
//                startActivity(new Intent(context(),SettingActivity.class));
            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogEdtNormalFragment, "DialogEdtNormalFragment");

    }

    private void showRecordPauseDialog() {
        DialogRecordPauseFragment fragment = new DialogRecordPauseFragment();
        fragment.setOnFragmentItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_info.isPause = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recordComposite.setText(getString(R.string.alreadyPause));
                        toast(getString(R.string.record_pause));
                    }
                });
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                videoCutPresenter.record(null, isAddNewRecordKanBan, null);
                mTaskName = null;
                mTaskPlace = null;
            }
        });
        FragmentUtil.showDialogFragment(getSupportFragmentManager(), fragment, "DialogRecordPauseFragment");
    }

    private void btnClickClearFog() {
        log("除雾按钮！");
        if (isClearFog) {
            isClearFog = false;
            clearFogComposite.setImage(R.mipmap.chuwu_off);
            clearFogComposite.setTextColor(R.color.white);
            cPresenter.clearFog_off();
        } else {
            isClearFog = true;
            clearFogComposite.setImage(R.mipmap.chuwu_on);
            clearFogComposite.setTextColor(R.color.colorText);
            cPresenter.clearFog_on();
        }
    }

    private void autoCloseClearFog() {
        if (isClearFog) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    while (isClearFog && System.currentTimeMillis() - currentTime <= 1000 * 60 * 2) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    log("关闭除雾：" + isClearFog);
                    if (isClearFog) {
                        if (clearFogComposite != null) {
                            clearFogComposite.post(new Runnable() {
                                @Override
                                public void run() {
                                    btnClickClearFog();
                                }
                            });
                        }
                    }
                }
            }).start();
        }
    }

    private void btnClickLight() {
        log("灯光按钮！");
        if (isHighBeam) {
            light_text.setText("近光灯");
            isHighBeam = false;
            lightAdjust.setProgress(low_num);
        } else {
            light_text.setText("远光灯");
            isHighBeam = true;
            lightAdjust.setProgress(high_num);
        }
    }

    private void btnClickLocateSet() {

        log("定位按钮！");
        if (isOpenLocate) {
            isOpenLocate = false;
            locateComposite.setImage(R.mipmap.locate_off);
            locateComposite.setTextColor(R.color.white);
            baiduMapLocatePresenter.stopLocate();
            mLocation_jingdu = 0;
            mLocation_weidu = 0;
            if (mCharOverPresent != null) {
                mCharOverPresent.setGpsAndRang(mLaserRanging, 0, 0);
            }

        } else {
            isOpenLocate = true;
            locateComposite.setImage(R.mipmap.locate_on);
            locateComposite.setTextColor(R.color.colorText);
            baiduMapLocatePresenter.startLocate();
            mLocation_jingdu = 0;
            mLocation_weidu = 0;

        }
    }


    @OnTouch({R.id.zoom_add, R.id.zoom_sub, R.id.size_add, R.id.size_sub, R.id.lights_switch, R.id.moveUp, R.id.moveDown})
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.zoom_add: //聚焦近

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.zoom_add();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.zoom_sub://聚焦远

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.zoom_sub();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.size_add://变倍变长
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.size_add();
                    cPresenter.isGetBoom(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                    cPresenter.isGetBoom(false);
                }
                break;
            case R.id.size_sub://变倍变短
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.size_sub();
                    cPresenter.isGetBoom(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                    cPresenter.isGetBoom(false);
                }
                break;
            case R.id.moveUp://控制摄像头向上

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.up();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.moveDown://控制摄像头向下

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.down();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
        }
        return false;
    }

    @Override
    public void record(int i, boolean isRecord, String recordPath) {
        switch (i) {
            case 0:
                if (isRecord) {

                    isRecordOpen = true;
                    if (isAddNewRecordKanBan) {
                        toast("开始录制看板");
                    } else {
                        toast("开始录像");
                        mVideoInfo = new VideoInfo();
                        mVideoInfo.setVideoFilename(recordPath);
                        mVideoInfo.setRecordTaskInfo(mRecordTaskInfo);
                    }
                    if (recordComposite != null)
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recordComposite.setImage(R.mipmap.recordpress);
                                recordComposite.setText(TimeUtil.formatTime((long) 0, true));
                                initRecordTimer();
                            }
                        });
                } else {
                    toast(R.string.startRecordFalse);
                    mCharOverPresent.setNoRecordHead();
                    mTaskName = null;
                    mTaskPlace = null;
                }
                break;
            case 1:
                if (isRecord) {
                    kanbanPathDes = null;
                    isRecordOpen = false;
                    mCharOverPresent.setNoRecordHead();
                    if (isAddNewRecordKanBan) {
                        toast(getString(R.string.record_kanban_stop));
                    } else {
                        toast(getString(R.string.record_stop));
                        if (mVideoInfo != null) {
                            log("xml = " + mVideoInfo.toString());
                            BaseApplication.MAIN_EXECUTOR.execute(new Runnable() {
                                @Override
                                public void run() {
                                    PullXmlParseUtil.writeVideoRecordXml(mVideoInfo);
                                    FileUtil.updateSystemLibFile(FileUtil.replacePostFix(mVideoInfo.getVideoFilename(), ".xml"));

//                                    VideoInfo videoInfo = PullXmlParseUtil.getVideoRecordXml(FileUtil.replacePostFix(mVideoInfo.getVideoFilename(), ".xml"));
//                                    log("获取到到：videoInfo = " + videoInfo);
                                    mVideoInfo = null;
                                }
                            });
                        }
                    }
                    mCharOverPresent.recordFinish();
                    if (recordComposite != null)
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recordComposite.setImage(R.mipmap.record);
                                if (recordTimerTask != null)
                                    recordTimerTask.cancel();
                                if (recordTimer != null)
                                    recordTimer.cancel();
                                recordComposite.setText(getResources().getString(R.string.recording));
                                recordTime = 0;

                            }
                        });


                    if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 1) {
                        int count = BaseApplication.getSharedPreferences().getInt(Constant.KEY_VIDEO_FILE_COUNT, 1);
                        BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_VIDEO_FILE_COUNT, count + 1).commit();
                    }


                } else {
                    toast(R.string.stopRecordFalse);
                }
                mTaskName = null;
                mTaskPlace = null;
                if (mIsCanShowBantou == true)
                    mIsCanShowBantou = false;
                if (isAddNewRecordKanBan)
                    isAddNewRecordKanBan = false;
                if (kanbanPathDes != null)
                    kanbanPathDes = null;
                mTaskName = null;
                mTaskPlace = null;
                mStartWell = null;
                mEndWell = null;
                mRecordTaskInfo = null;

                break;
        }
    }

    @Override
    public void capture(final String path) {
        mCaptureTime = recordTime;
        mCapturePicture = new CapturePicture();
        mCapturePicture.setTimestamp(String.valueOf(mCaptureTime));

        if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 1) {
            int count = BaseApplication.getSharedPreferences().getInt(Constant.KEY_PICTURE_FILE_COUNT, 1);
            BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_PICTURE_FILE_COUNT, count + 1).commit();
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Capture_tishi_Dialog dialog = new Capture_tishi_Dialog(PreviewActivity.this, getResources().getString(R.string.isEditPicture), path);

                dialog.setSureOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(PreviewActivity.this, PictureEditActivity.class);
                        intent.putExtra("path", path);
                        intent.putExtra("batteryNum", mBatteryNum);
                        startActivityForResult(intent, 2);
                        dialog.dismiss();
                    }
                });

                dialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mVideoInfo != null) {
                            mCapturePicture.setFilename(path);
                            mCapturePicture.setDefectFilename(null);
                            if (mVideoInfo != null)
                                mVideoInfo.addCapturePic(mCapturePicture);
                        }

                        dialog.dismiss();
//                        FileUtil.updateSystemLibFile(path);
                    }
                });
            }
        });

    }

    @Override
    public void initHCNetSdkFaild() {

        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(getString(R.string.noSurpportHaikang),
                getString(R.string.isExitApp), getString(R.string.yes), getString(R.string.no), false);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                System.exit(0);
            }

            @Override
            public void cancel() {

            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");
    }

    @Override
    public void loginSuccessful() {

        if (All_id_Info.getInstance().getM_iLogID() >= 0) {
            if (mCharOverPresent != null) {
                mBiaojiTexts[0] = "";
                mBiaojiTexts[1] = "";
                mBiaojiTexts[2] = "";
                mBiaojiTexts[3] = "";
                mCharOverPresent.setSingleSign(mBiaojiTexts);
                mCharOverPresent.setTongDaoName("", true);
                mCharOverPresent.setGpsAndRang("00.00", 0, 0);
            }
        }
    }

    @Override
    public void kanbanAdding() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(context());
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setMessage("正在添加看板...");
                }
                mProgressDialog.show();
            }
        });

    }

    @Override
    public void kanbanAddFinish() {
        isKanbanAddFinish = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    public void setRanging(String laserRanging) {
        if (isRanging) {
            mCharOverPresent.setGpsAndRang(laserRanging, mLocation_jingdu, mLocation_weidu);
            mLaserRanging = laserRanging;
            if (!isResetRanging)
                isResetRanging = true;
        } else {
            if (isResetRanging) {
                mLaserRanging = "00.00";
                mCharOverPresent.setGpsAndRang("00.00", mLocation_jingdu, mLocation_weidu);
                if (isResetRanging)
                    isResetRanging = false;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (cPresenter != null)
                    cPresenter.isGetEnvironment(false);
                break;
            case 1:
                if (data != null) {
                    final RecordTaskInfo recordTaskInfo = (RecordTaskInfo) data.getSerializableExtra("record_head");

                    mRecordTaskInfo = recordTaskInfo;
                    if (kanbanPathDes == null) {
                        if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) != 2 || isAddNewRecordKanBan) {
                            setZiFuDieJia(recordTaskInfo);
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (!mIsCanShowBantou) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (mIsStop)
                                            break;
                                    }
                                    if (!mIsStop)
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setZiFuDieJia(recordTaskInfo);
                                                mIsCanShowBantou = false;
                                            }
                                        });
                                }
                            }).start();
                        }
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!isKanbanAddFinish) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (mIsStop)
                                        break;
                                }
                                if (!mIsStop)
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setZiFuDieJia(recordTaskInfo);
                                        }
                                    });
                            }
                        }).start();
                    }
                    mTaskName = recordTaskInfo.getTask_name();
                    mTaskPlace = recordTaskInfo.getTask_place();
                    mStartWell = recordTaskInfo.getTask_start();
                    mEndWell = recordTaskInfo.getTask_end();


                }
                String recordName = getDesPathName(mTaskName, mTaskPlace, mStartWell, mEndWell);
                if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 2 && !isAddNewRecordKanBan)
                    resetFileName(recordName, true);
                else {
                    String recordPath = getDesPath(recordName, true);
                    videoCutPresenter.record(recordPath, isAddNewRecordKanBan, kanbanPathDes);
                }
                break;
            case 2:
                if (data != null) {
                    String picPath = data.getStringExtra(Constant.KEY_ACTIVITY_RESULT_PIC_PATH);
                    String xmlPath = data.getStringExtra(Constant.KEY_ACTIVITY_RESULT_XML_PATH);
                    String quexianName = data.getStringExtra(Constant.KEY_ACTIVITY_RESULT_PIC_QUEXIAN_CODE);
                    log("picPath = " + picPath + " \n xmlPath = " + xmlPath);
                    if (mCapturePicture != null) {
                        mCapturePicture.setFilename(picPath);
                        mCapturePicture.setDefectFilename(xmlPath);
                        mCapturePicture.setPipedefectCode(quexianName);
                        if (mVideoInfo != null)
                            mVideoInfo.addCapturePic(mCapturePicture);
                    }
                }

                break;
        }
    }

    private void setZiFuDieJia(RecordTaskInfo recordTaskInfo) {

        if (recordTaskInfo != null) {
            StringBuilder taskNameAId = new StringBuilder();
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_name()) || !StringUtils.isStringEmpty(recordTaskInfo.getTask_id())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskNameAId.append(getResources().getString(R.string.task_name_id));
                taskNameAId.append(recordTaskInfo.getTask_name());
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_id()))
                    taskNameAId.append("/").append(recordTaskInfo.getTask_id());
            }

            StringBuilder taskPlace = new StringBuilder();
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_place())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskPlace.append(getResources().getString(R.string.record_task_place_e));
                taskPlace.append(recordTaskInfo.getTask_place()).
                        append(" ");
            }

            boolean isNotShowTime = BaseApplication.getSharedPreferences().getBoolean(Constant.KEY_OSD_IS_NOT_SHOW_TIME_ON_DEVICE, false);
            if (!isNotShowTime) {
                NET_DVR_TIME net_dvr_time = HkUtils.getTime();
                if (net_dvr_time != null) {
                    StringBuilder timeBuilder = new StringBuilder();
                    timeBuilder.append(net_dvr_time.dwYear).append("年")
                            .append(net_dvr_time.dwMonth).append("月")
                            .append(net_dvr_time.dwDay).append("日");
                    mRecordTaskInfo.setTask_date(timeBuilder.toString());
                    if (Login_info.getInstance().isShowFirstName()) {
                        taskPlace.append(getResources().getString(R.string.task_date));
                    }
                    taskPlace.append(timeBuilder.toString());
                }
            }

            StringBuilder taskWellStar2End = new StringBuilder();
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_start()) || !StringUtils.isStringEmpty(recordTaskInfo.getTask_end())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskWellStar2End.append(getResources().getString(R.string.task_well_start_end));
                taskWellStar2End.append(recordTaskInfo.getTask_start()).
                        append("-").
                        append(recordTaskInfo.getTask_end()).append(" ");
            }

            StringBuilder taskDirectAGuancai = new StringBuilder();
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_direction())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskDirectAGuancai.append(getResources().getString(R.string.record_task_direction_e));
                taskDirectAGuancai.append(recordTaskInfo.getTask_direction()).
                        append(" ");
            }
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_guancai())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskDirectAGuancai.append(getResources().getString(R.string.record_task_guancai_e));
                taskDirectAGuancai.append(recordTaskInfo.getTask_guancai()).
                        append(" ");
            }
            StringBuilder taskSortADiameter = new StringBuilder();
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_sort())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskSortADiameter.append(getResources().getString(R.string.record_task_sort_e));
                taskSortADiameter.append(recordTaskInfo.getTask_sort()).
                        append(" ");
            }
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_diameter())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskSortADiameter.append(getResources().getString(R.string.task_diameter));
                taskSortADiameter.append(recordTaskInfo.getTask_diameter()).
                        append("mm");
            }

            StringBuilder taskPlaceAPeople = new StringBuilder();

            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_computer())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskPlaceAPeople.append(getResources().getString(R.string.record_task_computer_e));
                taskPlaceAPeople.append(recordTaskInfo.getTask_computer()).
                        append(" ");
            }
            if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_people())) {
                if (Login_info.getInstance().isShowFirstName())
                    taskPlaceAPeople.append(getResources().getString(R.string.record_task_people_e));
                taskPlaceAPeople.append(recordTaskInfo.getTask_people());
            }

            String[] mulSignStrs = new String[]{
                    taskNameAId.toString(),
                    taskPlace.toString(),
                    taskWellStar2End.toString(),
                    taskDirectAGuancai.toString(),
                    taskSortADiameter.toString(),
                    taskPlaceAPeople.toString()
            };
            String[] sendStrs = new String[6];
            int i = 0;
            for (String str : mulSignStrs) {
                if (!TextUtils.isEmpty(str)) {
                    sendStrs[i] = str;
                    i++;
                }
            }


            mCharOverPresent.setMultSign(sendStrs);
            mCharOverPresent.setAlwaysShowInfo(recordTaskInfo.getTask_name()
                    , recordTaskInfo.getTask_id()
                    , recordTaskInfo.getTask_start()
                    , recordTaskInfo.getTask_end()
                    , recordTaskInfo.getTask_guancai()
                    , recordTaskInfo.getTask_diameter()
            );
        }
    }


    private String getDesPathName(String name, String place, String startWell, String endWell) {
        StringBuilder recordName = new StringBuilder();

        if (name != null && !TextUtils.isEmpty(name))
            recordName.append(name).append("_");
        if (place != null && !TextUtils.isEmpty(place))
            recordName.append(place).append("_");
        if (startWell != null && !TextUtils.isEmpty(startWell))
            recordName.append(startWell).append("_");
        if (endWell != null && !TextUtils.isEmpty(endWell))
            recordName.append(endWell).append("_");


        NET_DVR_TIME net_dvr_time = HkUtils.getTime();
        if (net_dvr_time != null) {
            recordName.append(net_dvr_time.dwYear)
                    .append(net_dvr_time.dwMonth < 10 ? 0 : "")
                    .append(net_dvr_time.dwMonth)
                    .append(net_dvr_time.dwDay < 10 ? 0 : "")
                    .append(net_dvr_time.dwDay)
                    .append(net_dvr_time.dwHour < 10 ? 0 : "")
                    .append(net_dvr_time.dwHour)
                    .append(net_dvr_time.dwMinute < 10 ? 0 : "")
                    .append(net_dvr_time.dwMinute)
                    .append(net_dvr_time.dwSecond < 10 ? 0 : "")
                    .append(net_dvr_time.dwSecond);
        } else {
            recordName.append(UrlUtil.getFileName());
        }


        return recordName.toString();
    }

    private String getDesPath(String fileName, boolean isRecord) {
        StringBuilder recordPath = new StringBuilder();
        recordPath.append(FileUtil.getFileSavePath());
        if (isRecord) {
            recordPath.append(Login_info.local_video_path);
            if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 1) {
                int videoCount = BaseApplication.getSharedPreferences().getInt(Constant.KEY_VIDEO_FILE_COUNT, 1);
                String countName = getCountName(videoCount, true);
                recordPath.append(countName).append("-");
            }
        } else {
            recordPath.append(Login_info.local_picture_path);
            if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 1) {
                int picCount = BaseApplication.getSharedPreferences().getInt(Constant.KEY_PICTURE_FILE_COUNT, 1);
                String countName = getCountName(picCount, false);
                recordPath.append(countName).append("-");
            }
        }
        recordPath.append(fileName);

//        if (isRecord)
//            recordPath.append(".avi");
//        else
//            recordPath.append(".jpg");

        return recordPath.toString();
    }


    private String getCountName(int count, boolean isVideo) {
        if (count == 10000) {
            if (isVideo) {
                BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_VIDEO_FILE_COUNT, 1).commit();
            } else {
                BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_PICTURE_FILE_COUNT, 1).commit();
            }
            return "00";
        }


        if (count < 10) {
            return "0" + count;
        }

        return String.valueOf(count);
    }


}
