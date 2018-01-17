package com.bmw.peek2.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.model.PictureQueXianInfo;
import com.bmw.peek2.utils.BitmapUtils;
import com.bmw.peek2.utils.DbHelper;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.FragmentUtil;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.BatteryViewUtil;
import com.bmw.peek2.utils.PullXmlParseUtil;
import com.bmw.peek2.view.adapter.PictureEditAdapter;
import com.bmw.peek2.view.fragments.DialogBiaojiFragment;
import com.bmw.peek2.view.fragments.DialogNormalFragment;
import com.bmw.peek2.view.fragments.DialogQuexianEditFragment;
import com.bmw.peek2.view.fragments.OnDialogFragmentClickListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bmw.peek2.utils.FragmentUtil.showDialogFragment;

/**
 * Created by admin on 2017/3/2.
 */

public class PictureEditActivity extends BaseActivity {

    @Bind(R.id.capture_image)
    ImageView captureImage;
    @Bind(R.id.capture_recyclerview)
    RecyclerView recyListView;
    @Bind(R.id.pipe_id)
    EditText mPipeIdEdt;
    @Bind(R.id.tv_battery_device)

    TextView battery_device;//设备电池电量显示
    @Bind(R.id.tv_battery_device_title)
    TextView battery_device_title;

    @Bind(R.id.tv_battery_terminal)
    TextView battery_terminal;//终端电池电量显示
    @Bind(R.id.tv_battery_terminal_title)
    TextView battery_terminal_title;

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

    private Bitmap bitmap;
    private Bitmap mNewBitmap;
    private PictureEditAdapter adapter;
    private int position = -1;
    private Handler handler;
    private DbUtils dbUtils;
    private List<PictureQueXianInfo> list;

    private static final String GuanDaoHao = "GuanDaoHao";
    private static final String Distance = "Distance";
    private static final String DefectType = "DefectType";
    private static final String DefectCode = "DefectCode";
    private static final String DefectLevel = "DefectLevel";
    private static final String ClockExpression = "ClockExpression";
    private static final String DefectLength = "DefectLength";
    private static final String DefectDescription = "DefectDescription";

    private String fileName;
    private Canvas mCanvas;
    private float mDefultCeshiTextX = 30, mDefultCeshiTextY = 30;
    private String mAddString;
    private int textsize = 30;
    private String mGuanDaoStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);
        ButterKnife.bind(this);
        initBroadcastReceiver();
        initBattery();
        initHandler();
        dbUtils = DbHelper.getDbUtils();

        String path = getIntent().getStringExtra("path");
        fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

        if (!TextUtils.isEmpty(path)) {
            bitmap = BitmapFactory.decodeFile(path);
            captureImage.setImageBitmap(bitmap);
        }

        recyListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PictureEditAdapter(this);
        recyListView.setAdapter(adapter);
        adapter.setOnDataChooseListener(new PictureEditAdapter.OnDataChooseListener() {
            @Override
            public void setData(int id) {
                position = id;
                handler.sendEmptyMessage(0);
            }
        });

        initData(1);
        editTxtListener();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        mNewBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        drawNewBitmap(mNewBitmap,30,mDefultCeshiTextX,mDefultCeshiTextY);
        initGetTouch();
        mAddString = "";

    }

    private void initBattery() {
        BatteryViewUtil.setDeviceBattery(context(), getIntent(), battery_device, battery_device_title);
    }

    private void drawNewBitmap(Bitmap defaultBitmap, String str, int textsize, float x, float y) {
        if (bitmap == null)
            return;
        int width = defaultBitmap.getWidth();
        int height = defaultBitmap.getHeight();


        mCanvas = new Canvas(mNewBitmap);
        Paint imgPaint = new Paint();
        imgPaint.setDither(true);
        imgPaint.setAntiAlias(true);

        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect des = new Rect(0, 0, width, height);

        mCanvas.drawBitmap(bitmap, src, des, imgPaint);
        TextPaint textPaint = myTextPaint(textsize);
        drawText(mCanvas, textPaint, str, x, y, width);
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();

        captureImage.setImageBitmap(mNewBitmap);

    }

    private void initGetTouch() {
        final GestureDetector gestureDetector = new GestureDetector(this, new SimplGestureListenerImpl());
        captureImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    public class SimplGestureListenerImpl extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float x = -distanceX;
            float y = -distanceY;


//            textViewCeshi.setX(textViewCeshi.getX()+x);
//            textViewCeshi.setY(textViewCeshi.getY()+y);

            mCanvas = null;

            mDefultCeshiTextX += x;
            mDefultCeshiTextY += y;

            Paint paintFont = new Paint();
            paintFont.setTextSize(textsize);
            Rect rectFont = new Rect();
            paintFont.getTextBounds(mAddString, 0, mAddString.length(), rectFont);

//            float strLength = mAddString.length() * textsize;
            float strLength = rectFont.width();
            float strHeight = rectFont.height();

            if (mDefultCeshiTextX >= bitmap.getWidth() - strLength)
                mDefultCeshiTextX = bitmap.getWidth() - strLength;

            if (bitmap.getWidth() - strLength <= 0) {
                int row = (int) (strLength % bitmap.getWidth() == 0 ? (strLength / bitmap.getWidth() + 0) : (strLength / bitmap.getWidth() + 1));
                strHeight = strHeight * row * 2 - strHeight;
            }
            if (mDefultCeshiTextY >= bitmap.getHeight() - strHeight) {
                mDefultCeshiTextY = bitmap.getHeight() - strHeight;
            }
            if (mDefultCeshiTextX <= 0)
                mDefultCeshiTextX = 0;
            if (mDefultCeshiTextY <= 0)
                mDefultCeshiTextY = 0;

            drawNewBitmap(mNewBitmap, mAddString, textsize, mDefultCeshiTextX, mDefultCeshiTextY);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void releaseCanvaBitmap() {
        if (mCanvas != null) {
            mCanvas = null;
            mNewBitmap.recycle();
            mNewBitmap = null;
        }
    }

    private TextPaint myTextPaint(int textsize) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        textPaint.setTextSize(textsize);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setColor(Color.WHITE);
        return textPaint;
    }

    private void drawText(Canvas canvas, TextPaint textPaint, String string, float x, float y, int width) {
        StaticLayout staticLayout = new StaticLayout(string, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);
        canvas.translate(x, y);
        staticLayout.draw(canvas);

    }

    private void editTxtListener() {
        mPipeIdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mGuanDaoStr = mPipeIdEdt.getText().toString();
                mGuandaoId.setText(mGuanDaoStr);
            }
        });
    }

    private void initData(final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list = dbUtils.findAll(PictureQueXianInfo.class);
                    if (list != null) {
                        Collections.sort(list);
                        handler.sendEmptyMessage(i);
                        if (i != 4)
                            position = -1;
                    } else {
                        position = -1;
                    }

                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (position != -1) {
                            recyListView.smoothScrollToPosition(position);
                        }
                        setListText();
                        break;
                    case 1:
                        adapter.setList(list);
                        break;
                    case 2:
                        adapter.setList(list);
                        adapter.setChooseByPosition(0);
                        break;
                    case 3:
                        adapter.setList(list);
                        adapter.setChooseByPosition(list.size() - 1);
                        break;
                    case 4:
                        adapter.setList(list);
                        adapter.setChooseByPosition(position);
                        break;
                    case 5:
                        BaseApplication.toast(getResources().getString(R.string.please_choose_ont_from_list_first));
                        break;
                }
            }
        };
    }

    private void setListText() {
        if (list == null)
            return;
        mClock.setText(position == -1 ? "" : list.get(position).getClock());
        mDistance.setText(position == -1 ? "" : list.get(position).getDistance());
        mStyle.setText(position == -1 ? "" : list.get(position).getStyle());
        mName.setText(position == -1 ? "" : list.get(position).getName());
        mGrade.setText(position == -1 ? "" : list.get(position).getGrade());
        mLength.setText(position == -1 ? "" : list.get(position).getLength());
        mDetail.setText(position == -1 ? "" : list.get(position).getDetail());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(batteryDataReceiver);
        releaseCanvaBitmap();
        bitmap.recycle();
        bitmap = null;

    }


    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            commitAllInfo();

            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @OnClick({
            R.id.capture_image,
            R.id.capture_add,
            R.id.capture_change,
            R.id.capture_delete,
            R.id.tv_picEdit_goback,
            R.id.preview_closeApp,
            R.id.add_textToImg
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.capture_image:
                break;
            case R.id.capture_add:

                if (position != -1) {
                    DialogQuexianEditFragment dialogQuexianEditFragment = DialogQuexianEditFragment.getInstance(false, list.get(position));
                    dialogQuexianEditFragment.setOnDataChangeListener(new DialogQuexianEditFragment.OnDataChangeListener() {
                        @Override
                        public void finish() {
                            initData(3);
                        }
                    });
                    showDialogFragment(getSupportFragmentManager(), dialogQuexianEditFragment, "DialogQuexianEditFragment");
                } else {

                    DialogQuexianEditFragment dialogQuexianEditFragment = DialogQuexianEditFragment.getInstance(false, null);
                    dialogQuexianEditFragment.setOnDataChangeListener(new DialogQuexianEditFragment.OnDataChangeListener() {
                        @Override
                        public void finish() {
                            initData(3);
                        }
                    });
                    showDialogFragment(getSupportFragmentManager(), dialogQuexianEditFragment, "DialogQuexianEditFragment");
                }

                break;
            case R.id.capture_change:
                if (position == -1)
                    handler.sendEmptyMessage(5);
                else {
                    DialogQuexianEditFragment dialogQuexianEditFragment = DialogQuexianEditFragment.getInstance(true, list.get(position));
                    dialogQuexianEditFragment.setOnDataChangeListener(new DialogQuexianEditFragment.OnDataChangeListener() {
                        @Override
                        public void finish() {
                            initData(4);
                        }
                    });
                    showDialogFragment(getSupportFragmentManager(), dialogQuexianEditFragment, "DialogQuexianEditFragment");
                }

                break;
            case R.id.capture_delete:
                if (position != -1)
                    try {
                        dbUtils.delete(list.get(position));
                        LogUtil.log("数据库：删除成功", position);
                        position = -1;
                        setListText();
                        initData(2);
                    } catch (DbException e) {
                        LogUtil.error("数据库：删除失败：", e);
                        e.printStackTrace();
                    }
                break;
            case R.id.tv_picEdit_goback:
                String picName = null;
                if (!TextUtils.isEmpty(mGuanDaoStr)) {
                    picName = fileName + "_" + mGuanDaoStr + ".jpg";
                } else {
                    picName = fileName + ".jpg";
                }

                String oldName = fileName + ".jpg";
                if (!oldName.equals(picName)) {
                    FileUtil.renameFile(FileUtil.getFileSavePath() + Login_info.local_picture_path, oldName, picName);
                    FileUtil.updateSystemLibFile(FileUtil.getFileSavePath() + Login_info.local_picture_path+oldName);
                }

                if (mAddString != null && !TextUtils.isEmpty(mAddString)) {
                    try {
                        BitmapUtils.save(FileUtil.getFileSavePath() + Login_info.local_picture_path, picName, mNewBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//              FileUtil.updateSystemFileList(FileUtil.FILE_PICTURE);
                FileUtil.updateSystemLibFile(FileUtil.getFileSavePath() + Login_info.local_picture_path + picName);
                commitAllInfo();
                break;
            case R.id.preview_closeApp:
                btnClickCloseApp();
                break;
            case R.id.add_textToImg:
                DialogBiaojiFragment dialogBiaojiFragment = DialogBiaojiFragment.getInstance(mAddString);
                dialogBiaojiFragment.setOnSureClickListener(new DialogBiaojiFragment.OnSureClickListener() {
                    @Override
                    public void finish(String inputData) {
                        mAddString = inputData;
                        drawNewBitmap(mNewBitmap, inputData, textsize, mDefultCeshiTextX, mDefultCeshiTextY);
                    }
                });
                FragmentUtil.showDialogFragment(getSupportFragmentManager(), dialogBiaojiFragment, "DialogBiaojiFragment");
                break;
        }
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

/*
    private void showDialogFragment(DialogFragment dialogFragment, String tag){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null)
            fragmentTransaction.remove(fragment);

        dialogFragment.show(fragmentTransaction,tag);
    }*/

    private void commitAllInfo() {
        if (position < 0 && TextUtils.isEmpty(mPipeIdEdt.getText().toString())) {
            finish();
            return;
        }

        Map<String, String> map = null;
        if (list != null && position >= 0) {
            map = new HashMap<>();
            map.put(Distance, list == null ? "" : list.get(position).getDistance());
            map.put(DefectType, list == null ? "" : list.get(position).getStyle());
            map.put(DefectCode, list == null ? "" : list.get(position).getName());
            map.put(DefectLevel, list == null ? "" : list.get(position).getGrade());
            map.put(ClockExpression, list == null ? "" : list.get(position).getClock());
            map.put(DefectLength, list == null ? "" : list.get(position).getLength());
            map.put(DefectDescription, list == null ? "" : list.get(position).getDetail());
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FileUtil.getFileSavePath()).append(Login_info.local_picture_path).append(fileName).append("_").append(mGuanDaoStr).append(".xml");
        File file = new File(stringBuilder.toString());
        PullXmlParseUtil.writeXml(file, fileName, mPipeIdEdt.getText().toString(), new String[]{Distance, DefectType, DefectCode, DefectLevel, ClockExpression, DefectLength, DefectDescription,}, map);
        FileUtil.updateSystemLibFile(stringBuilder.toString());
        finish();
    }


    private BroadcastReceiver batteryDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryViewUtil.setSystemBattery(context(), intent, battery_terminal, battery_terminal_title);
            BatteryViewUtil.setDeviceBattery(context(), intent, battery_device, battery_device_title);
        }
    };

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("data.receiver");
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryDataReceiver, intentFilter);
    }
}
