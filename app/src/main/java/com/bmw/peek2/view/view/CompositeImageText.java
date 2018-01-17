package com.bmw.peek2.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmw.peek2.R;
import com.bmw.peek2.utils.LogUtil;

/**
 * Created by admin on 2017/4/1.
 */

public class CompositeImageText extends FrameLayout {
    private ImageView mImage;
    private TextView mTextview;
//    private FrameLayout mRootView;
    private int imgPadLeft;
    private int imgPadRight;
    private int imgPadTop;
    private int imgPadBottom;
    private int imgMarginLeft;
    private int imgMarginRight;
    private int imgMarginTop;
    private int imgMarginBottom;
    private int imgWidth;
    private int imgHeight;
    private int imgImage;
    private int textSize;
    private int textColor;
    private String textString;


    public CompositeImageText(Context context) {
        super(context);
        initView(context);
    }

    public CompositeImageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
        initView(context);
    }

    public CompositeImageText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);
        initView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*if(mRootView == null)
            mRootView = (FrameLayout) findViewById(R.id.compositeImageText_root);
        LogUtil.log("CompositeImageText: width:"+getWidth()+" height:"+getHeight());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthMeasureSpec,heightMeasureSpec);
        mRootView.setLayoutParams(params);*/
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CompositeImageText);
        imgPadBottom = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgPadBottom,0);
        imgPadTop = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgPadTop,0);
        imgPadLeft = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgPadLeft,0);
        imgPadRight = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgPadRight,0);
        imgMarginBottom = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgMarginBottom,0);
        imgMarginTop = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgMarginTop,0);
        imgMarginLeft = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgMarginLeft,0);
        imgMarginRight = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgMarginRight,0);
        imgWidth = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgWidth,20);
        imgHeight = mTypedArray.getDimensionPixelSize(R.styleable.CompositeImageText_imgHeight,20);
        imgImage = mTypedArray.getResourceId(R.styleable.CompositeImageText_imgImage,R.mipmap.ic_launcher);
        textSize = (int) mTypedArray.getDimension(R.styleable.CompositeImageText_textSize,12);
        textColor = mTypedArray.getColor(R.styleable.CompositeImageText_textColor,Color.YELLOW);
        textString = mTypedArray.getString(R.styleable.CompositeImageText_textString);
        //获取资源后要及时回收
        mTypedArray.recycle();
    }


    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.composite_image_text, this, true);
//        mRootView = (FrameLayout) findViewById(R.id.compositeImageText_root);
        mImage = (ImageView) findViewById(R.id.compositeImageText_img);
        mTextview = (TextView) findViewById(R.id.compositeImageText_tv);
        mImage.setImageResource(imgImage);
        mImage.setPadding(imgPadLeft,imgPadTop,imgPadRight,imgPadBottom);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imgWidth, imgHeight);
        layoutParams.setMargins(imgMarginLeft,imgMarginTop,imgMarginRight,imgMarginBottom);//4个参数按顺序分别是左上右下
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        mImage.setLayoutParams(layoutParams);

        /*FrameLayout.LayoutParams tvLayoutParams = new FrameLayout.LayoutParams(imgWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLayoutParams.gravity = Gravity.CENTER;
        mTextview.setLayoutParams(tvLayoutParams);*/
        mTextview.setText(textString);
        mTextview.setTextColor(textColor);
        mTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);

    }

    public void setImage(int imgId){
        if(mImage != null){
            mImage.setImageResource(imgId);
        }
    }

    public void setText(String str){
        if(mTextview != null){
            mTextview.setText(str);
        }
    }

    public void setTextSize(int size){
        if(mTextview != null){
            mTextview.setTextSize(size);
        }
    }
    public void setTextColor(int color){
        if(mTextview != null){
            mTextview.setTextColor(getResources().getColor(color));
        }
    }
}
