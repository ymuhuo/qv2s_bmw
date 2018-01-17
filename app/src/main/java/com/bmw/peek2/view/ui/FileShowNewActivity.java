package com.bmw.peek2.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.presenter.VideoPlayerPresenter;
import com.bmw.peek2.presenter.impl.FilePresenterImpl;
import com.bmw.peek2.presenter.impl.VideoPlayerPresentImpl;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.ImageLoader;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.BatteryViewUtil;
import com.bmw.peek2.utils.NumberUtil;
import com.bmw.peek2.utils.PullXmlParseUtil;
import com.bmw.peek2.view.adapter.FileListAdapter;
import com.bmw.peek2.view.dialog.Capture_tishi_Dialog;
import com.bmw.peek2.view.fragments.DialogEdtNormalFragment;
import com.bmw.peek2.view.fragments.DialogNormalFragment;
import com.bmw.peek2.view.fragments.OnDialogFragmentClickListener;
import com.bmw.peek2.view.view.CompositeImageText;
import com.bmw.peek2.view.viewImpl.FileViewImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

import static com.bmw.peek2.utils.FileUtil.getRealExtSDCardPath;
import static com.bmw.peek2.utils.FragmentUtil.showDialogFragment;

public class FileShowNewActivity extends BaseActivity implements FileViewImpl {

    @Bind(R.id.photoView_pic)
    PhotoView photoViewPic;
    @Bind(R.id.recyclerView_pic)
    RecyclerView recyclerViewPic;
    @Bind(R.id.pic_show_guandaoId)
    TextView mGuandaoId;
    @Bind(R.id.pic_show_style)
    TextView mStyle;
    @Bind(R.id.pic_show_name)
    TextView mName;
    @Bind(R.id.pic_show_grade)
    TextView mGrade;
    @Bind(R.id.pic_show_distance)
    TextView mDistance;
    @Bind(R.id.pic_show_clock)
    TextView mClock;
    @Bind(R.id.pic_show_length)
    TextView mLength;
    @Bind(R.id.pic_show_detail)
    TextView mDetail;
    @Bind(R.id.pic_show_bottom_container)
    LinearLayout picShowBottomContainer;
    @Bind(R.id.search_edit)
    EditText searchEdit;

    @Bind(R.id.tv_battery_device)
    TextView battery_device;//设备电池电量显示
    @Bind(R.id.tv_battery_device_title)
    TextView battery_device_title;

    @Bind(R.id.tv_battery_terminal)
    TextView battery_terminal;//终端电池电量显示
    @Bind(R.id.tv_battery_terminal_title)
    TextView battery_terminal_title;

    @Bind(R.id.container_picturePlayer)
    RelativeLayout containerPicturePlayer;
    @Bind(R.id.container_video_player)
    RelativeLayout containerVideoPlayer;
    @Bind(R.id.player_surface)
    VideoView playerSurface;
    @Bind(R.id.player_seekbar)
    SeekBar playerSeekbar;
    @Bind(R.id.tv_video_allTime)
    TextView tvVideoAllTime;
    @Bind(R.id.tv_video_currentTime)
    TextView tvVideoCurrentTime;
    @Bind(R.id.tv_video_playSpeed)
    TextView tvVideoPlaySpeed;
    @Bind(R.id.play_start)
    ImageView playStart;
    @Bind(R.id.tv_fileActivty_diskSize)
    TextView tvDiskSize;

    @Bind(R.id.rl_file_edit_normal)
    RelativeLayout mRl_file_edit_normal;
    @Bind(R.id.ll_file_edit)
    LinearLayout mLl_file_edit;
    @Bind(R.id.cit_file_all_choose)
    CompositeImageText cit_file_all_choose;
    @Bind(R.id.cit_file_copy)
    CompositeImageText cit_file_copy;

    private FilePresenterImpl filePresenter;
    //    private Bitmap mBitmap;
    private boolean isStop;
    private boolean mSeekbarStartTouch;
    private VideoPlayerPresenter videoPlayerPresenter;
    private ImageLoader imageLoader;
    private int speedfast = 1;
    private int speedSlow = 1;
    private boolean isChooseAll;
    private PopupWindow pwChooseCopyPath;
    private int pwStart_x;
    private int pwStart_y;
    private String sourCopyPlace;
    private String targetCopyPlace;


    private BroadcastReceiver batteryDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryViewUtil.setSystemBattery(context(), intent, battery_terminal, battery_terminal_title);
            BatteryViewUtil.setDeviceBattery(context(), intent, battery_device, battery_device_title);
        }
    };
    private String copyPath;
    private boolean isPicture;
    private TextView tv_usb;


    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("data.receiver");
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryDataReceiver, intentFilter);
    }


    private void initBattery() {
        BatteryViewUtil.setDeviceBattery(context(), getIntent(), battery_device, battery_device_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_show_new);
        ButterKnife.bind(this);
        imageLoader = new ImageLoader();

        initBattery();
        isPicture = getIntent().getBooleanExtra("picture", true); //判断图片还是视频文件
        initView(isPicture);

        initBroadcastReceiver();
        FileListAdapter adapter = initRecyclerView(isPicture);

        initSearch();

        if (!isPicture) {
            videoPlayerPresenter = new VideoPlayerPresentImpl(this, null, playerSurface, playerSeekbar);

            setSeekbarMove();


        }

        filePresenter = new FilePresenterImpl(adapter, isPicture, this, context(), videoPlayerPresenter); //初始化presenter
        initAdapter();

    }


    private void initPopupWindow() {
        View view = View.inflate(context(), R.layout.save_copy_path_choose, null);
        TextView tv_sd = (TextView) view.findViewById(R.id.tv_sdcard);
        tv_usb = (TextView) view.findViewById(R.id.tv_usb);
        tv_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetCopyPlace = context().getString(R.string.sdCard);
                copyPath = FileUtil.getStoragePath(context(), FileUtil.KEY_SD);
                setRealCopyPath();
                startCopyFile();
            }
        });
        tv_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_info.getInstance().isSaveToExSdcard()) {
                    targetCopyPlace = context().getString(R.string.localSave);
                    copyPath = FileUtil.getRealExtSDCardPath(context()).get(0);
                } else {
                    targetCopyPlace = context().getString(R.string.usb);
                    copyPath = FileUtil.getStoragePath(context(), FileUtil.KEY_USB);
                }
                setRealCopyPath();
                startCopyFile();
            }
        });

        int width = NumberUtil.dip2px(context(), 100);
        int height = NumberUtil.dip2px(context(), 70);

        pwChooseCopyPath = new PopupWindow(view, width, height);
        pwChooseCopyPath.setBackgroundDrawable(new BitmapDrawable());
        pwChooseCopyPath.setFocusable(true);
        pwChooseCopyPath.setOutsideTouchable(true);
        pwChooseCopyPath.setOutsideTouchable(true);
        pwChooseCopyPath.setTouchable(true);
        initLocation();
    }

    private void initLocation() {
        int[] btnLoc = new int[2];
        recyclerViewPic.getLocationOnScreen(btnLoc);
        pwStart_x = btnLoc[0];
        pwStart_y = btnLoc[1] + recyclerViewPic.getHeight() - NumberUtil.dip2px(context(), 70);

//        log("lx = " + btnLoc[0] + " ly = " + btnLoc[1] + " px = " + pwStart_x + " py = " + pwStart_y);
    }

    private void initView(boolean isPicture) {
        if (isPicture) {
            containerPicturePlayer.setVisibility(View.VISIBLE);
            containerVideoPlayer.setVisibility(View.GONE);
        } else {
            containerPicturePlayer.setVisibility(View.GONE);
            containerVideoPlayer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        initPopupWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = true;
        ButterKnife.unbind(this);
        unregisterReceiver(batteryDataReceiver);
        imageLoader.clearBitmapCache();
        if (videoPlayerPresenter != null)
            videoPlayerPresenter.release();

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

    @OnClick({R.id.play_start,
            R.id.play_stop,
            R.id.play_last,
            R.id.play_next,
            R.id.play_slow,
            R.id.play_fast,
            R.id.compositeImgTv_pic_goBack,
            R.id.compositeImgTv_pic_edit,
            R.id.preview_closeApp,
            R.id.play_cut,
            R.id.cit_file_delete,
            R.id.cit_file_copy,
            R.id.cit_file_all_choose,
            R.id.cit_file_edit_cancel
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.compositeImgTv_pic_edit:
                mRl_file_edit_normal.setVisibility(View.GONE);
                mLl_file_edit.setVisibility(View.VISIBLE);
                filePresenter.setEditModel(true);
                break;
            case R.id.cit_file_edit_cancel:
                mRl_file_edit_normal.setVisibility(View.VISIBLE);
                mLl_file_edit.setVisibility(View.GONE);
                filePresenter.setEditModel(false);
                cit_file_all_choose.setImage(R.mipmap.file_all_choose_normal);
                isChooseAll = false;
                filePresenter.cancelAll();
                break;
            case R.id.cit_file_copy:

                String usbPath = FileUtil.getStoragePath(context(), FileUtil.KEY_USB);
                String sdPath = FileUtil.getStoragePath(context(), FileUtil.KEY_SD);
                String localPath = FileUtil.getRealExtSDCardPath(context()).get(0);

                if (TextUtils.isEmpty(sdPath) && TextUtils.isEmpty(usbPath)) {
                    toast(getString(R.string.noOutSdcard));
                    break;
                }

                if (!TextUtils.isEmpty(sdPath) && !TextUtils.isEmpty(usbPath)) {
                    sourCopyPlace = context().getString(R.string.localSave);
                    if (Login_info.getInstance().isSaveToExSdcard()) {
                        sourCopyPlace = context().getString(R.string.usb);
                        if (tv_usb != null)
                            tv_usb.setText("本机");
                    }
                    pwChooseCopyPath.showAtLocation(cit_file_all_choose, Gravity.NO_GRAVITY, pwStart_x, pwStart_y);
                    break;
                }

                if (!TextUtils.isEmpty(sdPath) || !TextUtils.isEmpty(usbPath)) {

                    if (!TextUtils.isEmpty(sdPath)) {
                        if (Login_info.getInstance().isSaveToExSdcard()) {
                            sourCopyPlace = context().getString(R.string.sdCard);
                            targetCopyPlace = context().getString(R.string.localSave);
                            copyPath = localPath;
                        } else {
                            sourCopyPlace = context().getString(R.string.localSave);
                            targetCopyPlace = context().getString(R.string.sdCard);
                            copyPath = sdPath;
                        }
                    } else {
                        if (Login_info.getInstance().isSaveToExSdcard()) {
                            sourCopyPlace = context().getString(R.string.usb);
                            targetCopyPlace = context().getString(R.string.localSave);
                            copyPath = localPath;
                        } else {
                            sourCopyPlace = context().getString(R.string.localSave);
                            targetCopyPlace = context().getString(R.string.usb);
                            copyPath = usbPath;
                        }
                    }
                    setRealCopyPath();
                    startCopyFile();


                }
                break;
            case R.id.cit_file_all_choose:
                if (!isChooseAll) {
                    filePresenter.chooseAll();
                    isChooseAll = true;
                    cit_file_all_choose.setImage(R.mipmap.file_all_choose);
                } else {
                    filePresenter.cancelAll();
                    isChooseAll = false;
                    cit_file_all_choose.setImage(R.mipmap.file_all_choose_normal);
                }
                break;
            case R.id.compositeImgTv_pic_goBack:
                removeActivity();
                break;
            case R.id.cit_file_delete:
                filePresenter.deleteFile();
                break;
            case R.id.preview_closeApp:
                btnClickCloseApp();
                break;
            case R.id.play_start:
                videoPlayerPresenter.playStart();
                break;
            case R.id.play_stop:
                videoPlayerPresenter.playStop();
                speedfast = 1;
                speedSlow = 1;
                break;
            case R.id.play_last:
                int position_last = filePresenter.lastItem();
                if (position_last >= 0)
                    recyclerViewPic.smoothScrollToPosition(position_last);
                break;
            case R.id.play_next:
                int position_next = filePresenter.nextItem();
                if (position_next >= 0)
                    recyclerViewPic.smoothScrollToPosition(position_next);
                break;
            case R.id.play_slow:
                if (filePresenter.isGetShowFile()) {
                    if (speedfast != 1) {
                        playerSurface.pause();
                        speedfast = speedfast / 2;
                        setSpeedTv(speedfast);
                        if (speedfast == 1) {
                            playerSurface.start();
                        }
                    } else {
                        playerSurface.start();
                        if (speedSlow < 16) {
                            speedSlow = speedSlow * 2;
                        } else {
                            speedSlow = 16;
                        }
                        setSpeedTv(-speedSlow);
                    }
                }
//                videoPlayerPresenter.playSlow();
                break;
            case R.id.play_fast:
                if (filePresenter.isGetShowFile()) {
                    if (speedSlow != 1) {
                        speedSlow = speedSlow / 2;
                        setSpeedTv(-speedSlow);
                        if (speedSlow == 1)
                            playerSurface.start();
                    } else {
                        playerSurface.pause();
                        if (speedfast < 16) {
                            speedfast = speedfast * 2;
                        } else {
                            speedfast = 16;
                        }
                        setSpeedTv(speedfast);
                    }
                }
//                videoPlayerPresenter.playFast();
                break;
            case R.id.play_cut:
                videoPlayerPresenter.playCutPicture();
                break;
        }
    }

    private void setRealCopyPath() {
        List<String> pathList = FileUtil.getRealExtSDCardPath(context());
        for (String path : pathList) {
            if (path.contains(copyPath))
                copyPath = path;
        }
    }


    private void startCopyFile() {
        if (isPicture) {
            copyPath += Login_info.local_picture_path;
        } else {
            copyPath += Login_info.local_video_path;
        }


        filePresenter.copyFile(copyPath);
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


    private void initAdapter() {

        filePresenter.initAdapter();
        initDiskSize();

    }

    private void initDiskSize() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Float> diskSizeList = FileUtil.getDiskCapacity();
        stringBuilder.append(Login_info.getInstance().isSaveToExSdcard() ?
                getResources().getString(R.string.outStorage) : getResources().getString(R.string.inStorage)).
                append("\n").append(diskSizeList.get(2)).append("G").append("/").append(diskSizeList.get(0)).append("G");
        tvDiskSize.setText(stringBuilder.toString());
    }

    private void setEmptyBitmap() {
//        recyleBitmap();
        Bitmap mBitmap = imageLoader.getBitmap("empty");
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
            imageLoader.putBitmap(mBitmap, "empty");
        }
        photoViewPic.setImageBitmap(mBitmap);
    }


    public void capturePicture(final String path, final String name) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) != 2)
                    editPicture(path + name + ".jpg");
                else {
                    setPictureName(path, name);
                }
            }
        });


    }

    private void setPictureName(final String path, final String oldName) {
        DialogEdtNormalFragment dialogEdtNormalFragment = DialogEdtNormalFragment.getInstance("请输入自定义文件名：", oldName, null, null, false);
        dialogEdtNormalFragment.setOnEdtDialogItemClickListener(new DialogEdtNormalFragment.OnEdtDialogItemClickListener() {
            @Override
            public void next_step(String msg) {
                if (!TextUtils.isEmpty(msg) && !msg.equals(oldName)) {
                    File file = new File(path + msg + ".jpg");
                    if (file.exists()) {
                        int fileCount = 0;
                        while (file.exists()) {
                            fileCount++;
                            file = new File(path + msg + "_" + fileCount + ".jpg");
                        }
                        msg = msg + "_" + fileCount;
                    }
                    FileUtil.renameFile(path, oldName + ".jpg", msg + ".jpg");
                    FileUtil.updateSystemLibFile(path + oldName + ".jpg");
                    FileUtil.updateSystemLibFile(path + msg + ".jpg");
                    editPicture(path + msg + ".jpg");
                } else {
                    editPicture(path + oldName + ".jpg");
                }
            }

            @Override
            public void cancel() {
                editPicture(path + oldName + ".jpg");
            }
        });
        showDialogFragment(getSupportFragmentManager(), dialogEdtNormalFragment, "DialogEdtNormalFragment");
    }

    private void editPicture(final String path) {
        final Capture_tishi_Dialog dialog = new Capture_tishi_Dialog(context(), getResources().getString(R.string.isEditPicture), path);

        dialog.setSureOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context(), PictureEditActivity.class);
                intent.putExtra("path", path);
                startActivityForResult(intent, 0);
                dialog.dismiss();
            }
        });

        dialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtil.updateSystemLibFile(path);
                dialog.dismiss();
            }
        });
    }


    public void setSpeedTv(int playSpeed) {
        switch (playSpeed) {
            case -16:
                tvVideoPlaySpeed.setText("1/16X");
                break;
            case -8:
                tvVideoPlaySpeed.setText("1/8X");
                break;
            case -4:
                tvVideoPlaySpeed.setText("1/4X");
                break;
            case -2:
                tvVideoPlaySpeed.setText("1/2X");
                break;
            case 1:
                tvVideoPlaySpeed.setText("1X");
                break;
            case 2:
                tvVideoPlaySpeed.setText("2X");
                break;
            case 4:
                tvVideoPlaySpeed.setText("4X");
                break;
            case 8:
                tvVideoPlaySpeed.setText("8X");
                break;
            case 16:
                tvVideoPlaySpeed.setText("16X");
                break;

        }
    }


    public void isBtnDeleteShow(boolean isShow) {

    }

    @Override
    public void setEmptyPicture() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setEmptyBitmap();
                getDataFromXml(null);
            }
        });
    }

    @Override
    public void setEditModel(boolean isEditModel) {
        if (isEditModel) {
            mRl_file_edit_normal.setVisibility(View.GONE);
            mLl_file_edit.setVisibility(View.VISIBLE);
        } else {
            mRl_file_edit_normal.setVisibility(View.VISIBLE);
            mLl_file_edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAllChooseBtn(boolean isAll) {
        if (isAll) {
            cit_file_all_choose.setImage(R.mipmap.file_all_choose);
            isChooseAll = true;
        } else {
            cit_file_all_choose.setImage(R.mipmap.file_all_choose_normal);
            isChooseAll = false;
        }
    }

    @Override
    public void startShowCopy(int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast("开始复制！");
            }
        });
    }

    @Override
    public void copyWhichFile(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                toast("正在复制第 " + position + "个文件！");
            }
        });
    }

    @Override
    public void copyFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast("复制完成！");
            }
        });
    }


    private void initSearch() {
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String msg = searchEdit.getText().toString();
                filePresenter.searching(msg);
            }
        });
    }

    private FileListAdapter initRecyclerView(boolean isPicture) {
        GridLayoutManager gManager = new GridLayoutManager(context(), 3);
        recyclerViewPic.setLayoutManager(gManager);
        FileListAdapter adapter = new FileListAdapter(context(), isPicture);
        recyclerViewPic.setAdapter(adapter);
        return adapter;

    }


    public void pictureClick(final String path) {
        if (path != null) {

            Bitmap mBitmap = imageLoader.getBitmap(path);
            if (mBitmap == null) {
                mBitmap = BitmapFactory.decodeFile(path);
                imageLoader.putBitmap(mBitmap, path);
            }
            photoViewPic.setImageBitmap(mBitmap);
            getDataFromXml(path);
        } else {
            photoViewPic.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        }
    }

    private void getDataFromXml(String xmlPath) {
        mClock.setText("");
        mDetail.setText("");
        mDistance.setText("");
        mGrade.setText("");
        mGuandaoId.setText("");
        mLength.setText("");
        mName.setText("");
        mStyle.setText("");
        if (xmlPath != null) {
            xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf(".")) + ".xml";

            File xmlFile = new File(xmlPath);
            if (xmlFile.exists()) {
                Map<String, String> map = PullXmlParseUtil.parsePicXml(xmlFile);
                mClock.setText(map.get(PullXmlParseUtil.ClockExpression));
                mDetail.setText(map.get(PullXmlParseUtil.DefectDescription));
                mDistance.setText(map.get(PullXmlParseUtil.Distance));
                mGrade.setText(map.get(PullXmlParseUtil.DefectLevel));
                mGuandaoId.setText(map.get(PullXmlParseUtil.PipeSection));
                mLength.setText(map.get(PullXmlParseUtil.DefectLength));
                mName.setText(map.get(PullXmlParseUtil.DefectCode));
                mStyle.setText(map.get(PullXmlParseUtil.DefectType));
            }
        }
    }


    @Override
    public void isPlaying(boolean isPlaying) {
        if (playStart == null)
            return;
        if (!isPlaying)
            playStart.setImageResource(R.mipmap.play_start);
        else
            playStart.setImageResource(R.mipmap.play_pause);
    }

    @Override
    public void setSeekbarProgress(int progress) {
        playerSeekbar.setProgress(progress);
    }

    public void showPlayFailed() {

        new AlertDialog.Builder(context()).setTitle(getResources().getString(R.string.playVideoFalse)).
                setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

    }

    @Override
    public void showAllPlayTime(String time) {
        tvVideoAllTime.setText(time);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setSeekbarMove() {
        playerSeekbar.setOnSeekBarChangeListener(change);
        playerSeekbar.setMax(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {


                    if (!mSeekbarStartTouch) {
//                        final int currentPlace = videoPlayerPresenter.getCurrentPlayPlace();
                        int currentPlace = 0;
                        try {
                            if (playerSurface.isPlaying())
                                currentPlace = playerSurface.getCurrentPosition();
                        } catch (IllegalStateException e) {
                            LogUtil.error("获取当前播放位置异常：Error：" + e.toString());
                        }

                        if (speedfast != 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    sleep(500);
                                    playerSurface.pause();
                                    int currentP = playerSurface.getCurrentPosition();
                                    setPlayPlace(currentP + speedfast / 2 * 600);
                                    playerSeekbar.setProgress(currentP + speedfast / 2 * 1100);
                                    if (playerSeekbar.getMax() <= currentP + speedfast / 2 * 1100) {
                                        videoPlayerPresenter.playStop();
                                    }

                                }
                            });
                        } else if (speedSlow != 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playerSurface.pause();
                                }
                            });
                            sleep(speedSlow * 15);
                            final int finalCurrentPlace = currentPlace;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playerSurface.start();
                                    playerSeekbar.setProgress(finalCurrentPlace);

                                }
                            });
                        }

                        if (speedfast == 1 && speedSlow == 1 && videoPlayerPresenter.isPlaying()) {
                            playerSeekbar.setProgress(currentPlace);
                        }
                        tvVideoCurrentTime.post(new Runnable() {
                            @Override
                            public void run() {
                                if (tvVideoCurrentTime != null)
                                    tvVideoCurrentTime.setText(videoPlayerPresenter.getPlayedTimeEx());
                            }
                        });
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void resetPlayView() {
        if (tvVideoAllTime == null)
            return;
        speedfast = 1;
        speedSlow = 1;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvVideoCurrentTime.setText("00:00");
                tvVideoPlaySpeed.setText("1X");
                playStart.setImageResource(R.mipmap.play_start);
                playerSeekbar.setProgress(0);
            }
        });
    }


    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();

            setPlayPlace(progress);
            log("stopTouch seekbar!");

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            mSeekbarStartTouch = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    private void setPlayPlace(int progress) {
        videoPlayerPresenter.setPlayPlace(progress);
        getCurrentTime();

        mSeekbarStartTouch = false;
    }

    private void getCurrentTime() {
        tvVideoCurrentTime.setText(videoPlayerPresenter.getPlayedTimeEx());
    }

}
