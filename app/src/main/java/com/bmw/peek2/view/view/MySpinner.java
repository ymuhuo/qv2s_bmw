package com.bmw.peek2.view.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bmw.peek2.R;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.view.adapter.TextSpinnerAdapter;

/**
 * Created by admin on 2017/3/6.
 */

@SuppressLint("AppCompatCustomView")
public class MySpinner extends TextView {
    private Context mContext;
    private TextSpinnerAdapter adapter;
    private ListView popContentView;
    private AdapterView.OnItemClickListener onItemClickListener;
    private PopupWindow mDropView;
    private int mWidth;
    private boolean isCreatePopupWindow;
    private boolean isResetLayout;

    public MySpinner(Context context) {
        super(context);
        LogUtil.log("spinner MySpinner contruct!!!");
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySpinner);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.MySpinner_width_view, 0);
        typedArray.recycle();
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);

//        LogUtil.log("自定义view: ", "mwidth = " + mWidth + " width = " + getWidth());
       /* if (!isResetLayout) {
            ViewGroup.LayoutParams layoutParams = popContentView.getLayoutParams();
            layoutParams.width = mWidth;
            popContentView.setLayoutParams(layoutParams);
            popContentView.setMinimumWidth(mWidth);
            popContentView.invalidate();
        }*/
//        mWidth = getWidth();
        mWidth = getMeasuredWidth();
        if (mWidth != 0) {
            createPopupWindowOnMainThread();
        }


    }

    private void createPopupWindowOnMainThread() {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (!isCreatePopupWindow) {
                    isCreatePopupWindow = true;
                    createPopupWindow();
                    if (popContentView.getAdapter() == null && adapter != null) {
                        popContentView.setAdapter(adapter);
                    }

                    if (popContentView.getOnItemClickListener() == null && onItemClickListener != null) {
                        popContentView.setOnItemClickListener(onItemClickListener);
                    }
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        LogUtil.log("自定义view: ", "onFinishInflate mwidth = " + mWidth);

    }

    private void createPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout container = (LinearLayout) inflater.inflate(R.layout.spinner_content, null);
        popContentView = (ListView) container.findViewById(R.id.spinner_content);
        if (mWidth != 0) {
            mDropView = new PopupWindow(container, mWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            LogUtil.log("自定义view: ", mWidth);
        } else
            mDropView = new PopupWindow(container, 500, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDropView.setBackgroundDrawable(new BitmapDrawable());
        mDropView.setFocusable(true);
        mDropView.setOutsideTouchable(true);
        mDropView.setOutsideTouchable(true);
        mDropView.setTouchable(true);
        container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDropView.isShowing()) {
                    dismissPop();
                } else {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getRootView().getWindowToken(),0);
                    }
                    showPop();
                }
            }
        });
        mDropView.update();
    }

    public void setHint(String hint) {
        this.setText(hint);
    }

    public void setAdapter(TextSpinnerAdapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            if (popContentView != null)
                popContentView.setAdapter(this.adapter);
        }

    }

    public void setOnItemSelectedListener(AdapterView.OnItemClickListener listener) {
        if (listener != null) {
            this.onItemClickListener = listener;
            if (popContentView != null)
                popContentView.setOnItemClickListener(listener);
        }

    }

    public void dismissPop() {
        if (mDropView.isShowing()) {
            mDropView.dismiss();
        }
    }

    public void showPop() {
        ViewGroup.LayoutParams layoutParams = popContentView.getLayoutParams();
        int height = 0;
        if (adapter.getCount() <= 5)
            height = dip2px(getContext(), 45) * adapter.getCount();
        else
            height = dip2px(getContext(), 45) * 5;
        layoutParams.height = height;
        popContentView.setLayoutParams(layoutParams);

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int swidth = wm.getDefaultDisplay().getWidth();
        int sheight = wm.getDefaultDisplay().getHeight();

//        mDropView.showAsDropDown(this);
        int[] locations = new int[2];
        getLocationOnScreen(locations);

        if (sheight - locations[1] >= height) {
            mDropView.showAsDropDown(this);
        } else {
            mDropView.showAtLocation(this, Gravity.NO_GRAVITY, locations[0], locations[1] - height);
        }
//        mDropView.showAtLocation(this, Gravity.TOP,0,0);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
