package com.bmw.peek2.view.view;

/**
 * Created by admin on 2018/1/26.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.StringUtils;

import java.util.ArrayList;


public class CustomPotSeekBar extends View {
    private final String TAG = "CustomSeekBar";
    private int width;
    private int height;
    private int downX = 0;
    private int downY = 0;
    private int upX = 0;
    private int upY = 0;
    private int moveX = 0;
    private int moveY = 0;
    private float scale = 0;
    private int perWidth = 0;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint buttonPaint;
    private Canvas canvas;
    private Bitmap bitmap;
    private Bitmap thumb;
    private Bitmap spot;
    private Bitmap spot_on;
    private int hotarea = 100;//点击的热区
    private int cur_sections = -1;
    private ResponseOnTouch responseOnTouch;
    private int bitMapHeight = 38;//第一个点的起始位置起始，图片的长宽是76，所以取一半的距离
    private int textMove = 60;//字与下方点的距离，因为字体字体是40px，再加上10的间隔
    private int[] colors = new int[]{0xffff9900, 0x33000000};//进度条的橙色,进度条的灰色,字体的灰色
    private int textSize;
    private int circleRadius;
    private ArrayList<String> section_title;
    private int mMax = 100;
    private ArrayList<Integer> mPotPlaces;
    private int mCurrentPlace = 0;
    private int mThumbColorPre = Color.GRAY;
    private int mThumbColorCur = Color.RED;
    private int mThumbColorOld = 0xffff9900;
    private int mTextColor = 0xffff9900;
    private int mThumbColorMove = 0xffff9900;
    private int mCurBallWidth = 25;


    public interface ResponseOnTouch {
        void onTouchResponse(int progress);

        void onPlayToPot(int position);
    }

    public CustomPotSeekBar(Context context) {
        super(context);
    }

    public CustomPotSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }


    /*  //升序排列；
   Collections.sort(arr);

    //逆序输出
      Collections.reverse(arr);*/

    public CustomPotSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cur_sections = -1;
        thumb = getDotBitmap(mCurBallWidth, mCurBallWidth, mThumbColorCur);
        spot = getDotBitmap(16, 16, mThumbColorPre);
        spot_on = getDotBitmap(16, 16, mThumbColorOld);
        bitMapHeight = 24;
        textMove = bitMapHeight + 22;
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(mTextColor);
        buttonPaint = new Paint(Paint.DITHER_FLAG);
        buttonPaint.setAntiAlias(true);
        buttonPaint.setColor(mThumbColorCur);
    }

    private Bitmap getDotBitmap(int width, int height, int color) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        Canvas c = new Canvas(b);
        c.drawCircle(width / 2, height / 2, Math.min(width / 2, height / 2), paint);
        return b;
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    /**
     * 实例化后调用，设置bar的段数和文字
     */
    public void initData(ArrayList<String> section, ArrayList<Integer> potPlaceList) {
        if (section != null && potPlaceList != null) {
            section_title = section;
            mPotPlaces = potPlaceList;
        } else {
            String[] str = new String[]{"低", "中", "高"};
            Integer[] pots = new Integer[]{0, 50, 100};
            section_title = new ArrayList<String>();
            mPotPlaces = new ArrayList<>();
            for (int i = 0; i < str.length; i++) {
                section_title.add(str[i]);
                mPotPlaces.add(pots[i]);
            }
        }
        invalidate();
    }


    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        width = widthSize;
        float scaleX = widthSize / 1080;
        float scaleY = heightSize / 1920;
        scale = Math.max(scaleX, scaleY);
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62, getResources().getDisplayMetrics());
        setMeasuredDimension(width, height);
        width = width;

    }

//    private int mLastCur_sections = -1;
//    private boolean mIsPotShowing;
//    private boolean mIsNeedShowPot = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(0);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        mPaint.setAlpha(255);
        mPaint.setColor(colors[1]);
        canvas.drawLine(bitMapHeight
                , height * 2 / 3
                , width - bitMapHeight / 2
                , height * 2 / 3
                , mPaint);
        int section = 0;


        if (mCurrentPlace != 0) {
            mPaint.setColor(colors[0]);
            int iCurrentX = (int) ((mCurrentPlace / (double) mMax) * (width - bitMapHeight) + bitMapHeight / 2);
            canvas.drawLine(bitMapHeight / 2
                    , height * 2 / 3
                    , iCurrentX
                    , height * 2 / 3
                    , mPaint);
        }


        if (mPotPlaces != null && mPotPlaces.size() > 0) {

            int iChoosePlace = 0;
            if (cur_sections != -1)
                iChoosePlace = (int) ((width - bitMapHeight) * (mPotPlaces.get(cur_sections) / (double) mMax)) + bitMapHeight / 2;
            for (int i = 0; i < mPotPlaces.size(); i++) {
                int iPlace = (int) ((width - bitMapHeight) * (mPotPlaces.get(i) / (double) mMax)) + bitMapHeight / 2;

                if (mCurrentPlace < mPotPlaces.get(i)) {
                    mPaint.setAlpha(255);
                    canvas.drawBitmap(spot
                            , iPlace - spot_on.getWidth() / 2
                            , height * 2 / 3 - spot.getHeight() / 2
                            , mPaint);
                } else {
                    mPaint.setColor(colors[0]);

                    canvas.drawBitmap(spot_on
                            , iPlace - spot_on.getWidth() / 2
                            , height * 2 / 3 - spot_on.getHeight() / 2
                            , mPaint);
                }
                if (i == cur_sections) {
                    buttonPaint.setColor(mThumbColorCur);
                    canvas.drawBitmap(thumb
                            , iPlace - thumb.getWidth() / 2
                            , height * 2 / 3 - mCurBallWidth / 2
                            , buttonPaint);
                }


                String text = section_title.get(i);
                if (!StringUtils.isStringEmpty(text))
                    canvas.drawText(text
                            , iPlace - textSize / 4
                            , height * 2 / 3 - textMove
                            , mTextPaint);

            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int doNum = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
//                thumb = getDotBitmap(mCurBallWidth, mCurBallWidth, mThumbColorMove);
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
//                thumb = getDotBitmap(mCurBallWidth, mCurBallWidth, mThumbColorCur);
                upX = (int) event.getX();
                upY = (int) event.getY();
                doNum = responseTouch(moveX, moveY);
//                responseTouch(upX, upY);
                if (responseOnTouch != null && doNum != -1)
                    responseOnTouch.onTouchResponse(cur_sections);
//                mIsUpdatePot = true;
                break;
        }
        invalidate();
        return true;
    }

    private int responseTouch(int x, int y) {

        if (x >= bitMapHeight / 2 && x <= width - bitMapHeight / 2) {
            int currentPlace = x - bitMapHeight / 2;

            int num = getNearestNum(currentPlace);
            if (num != -1) {
                cur_sections = num;
            }
            if (cur_sections >= 0)
                mCurrentPlace = mPotPlaces.get(cur_sections);
            return num;
        }

        return -1;
    }

    private int getNearestNum(int currentPlace) {
        if (mPotPlaces == null || mPotPlaces.size() == 0)
            return -1;


        for (int i = 0; i < mPotPlaces.size(); i++) {

            int potPlace = (int) (((width - bitMapHeight) * (mPotPlaces.get(i) / (double) mMax)) + bitMapHeight / 2);

            LogUtil.log("currentPlace: " + currentPlace + " potPlace = " + potPlace);
            if (Math.abs(currentPlace - potPlace) <= 100) {
                return i;
            }
        }

        return -1;
    }

    public void setResponseOnTouch(ResponseOnTouch response) {
        responseOnTouch = response;
    }

    public void setProgress(int progress) {
        mCurrentPlace = progress;
        if (mPotPlaces != null && mPotPlaces.size() > 0 && mPotPlaces.get(0) > progress) {
            cur_sections = -1;
        }

        if (mPotPlaces != null && mPotPlaces.size() > 0) {
            boolean isClickOne = false;
            for (int i = 0; i < mPotPlaces.size(); i++) {
                if (Math.abs(mPotPlaces.get(i) - progress) <= 1500) {
                    cur_sections = i;
                    if (responseOnTouch != null)
                        responseOnTouch.onPlayToPot(cur_sections);
                    isClickOne = true;
                } else
                    continue;
            }
            if (!isClickOne) {
                cur_sections = -1;
            }
        }
        if (progress == 0) {
            cur_sections = -1;
        }
        postInvalidate();
//        invalidate();
    }
}