package com.bmw.peek2.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by admin on 2016/9/1.
 */
public class Vertical_seekbar extends SeekBar {


    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public Vertical_seekbar(Context context) {
        super(context);
    }

    public Vertical_seekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Vertical_seekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    //初始值从下往上
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(),0);

        super.onDraw(c);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(l);
        mOnSeekBarChangeListener = l;
    }


    void onStartTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onStopTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    void onProgressChanged(SeekBar seekBar, int i, boolean b){
        if(mOnSeekBarChangeListener != null){
            mOnSeekBarChangeListener.onProgressChanged(seekBar,i,b);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchEventOprator(event);

                break;
            case MotionEvent.ACTION_MOVE:
                touchEventOprator(event);
                onProgressChanged(this,getProgress(),true);
                break;
            case MotionEvent.ACTION_UP:
                touchEventOprator(event);
                onStopTrackingTouch();
                break;

            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }
        return true;
    }

    private void touchEventOprator(MotionEvent event){
        int i=0;
        i=getMax() - (int) (getMax() * event.getY() / getHeight());
        setProgress(i);
        Log.i("Progress",getProgress()+"");
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }


}
