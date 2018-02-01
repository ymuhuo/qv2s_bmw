package com.bmw.peek2.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.bmw.peek2.utils.LogUtil;

/**
 * Created by admin on 2018/1/22.
 */

public class MyVideoView extends VideoView {


    private int mVideoWidth;
    private int mVideoHeight;

    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMeasure(int width00, int height00) {
        this.mVideoWidth = width00;
        this.mVideoHeight = height00;
//        LogUtil.log(" setMeasure size  width = " + mVideoWidth + " height = " + mVideoHeight);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       /* if(mVideoHeight !=0 && mVideoWidth!=0) {
            int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
            int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

            setMeasuredDimension(width, height);
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }*/
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width;
//        LogUtil.log(" default size  width = " + widthMeasureSpec + " height = " + heightMeasureSpec);
//        LogUtil.log(" onMeasure size  width = " + mVideoWidth + " height = " + mVideoHeight);


        if (this.mVideoWidth > 0 && this.mVideoHeight > 0) {
            width = this.mVideoWidth;
            height = this.mVideoHeight;
        }
        setMeasuredDimension(width, height);

    }
}
