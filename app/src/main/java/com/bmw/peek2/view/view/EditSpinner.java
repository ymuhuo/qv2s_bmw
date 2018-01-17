package com.bmw.peek2.view.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bmw.peek2.R;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.view.adapter.TextSpinnerAdapter;

/**
 * Created by admin on 2017/11/1.
 */

public class EditSpinner extends TextInputEditText {

    private Context mContext;
    private TextSpinnerAdapter adapter;
    private ListView popContentView;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;
    private PopupWindow mDropView;
    private int mWidth;
    private boolean isCreatePopupWindow;
    private boolean isResetLayout;

    public EditSpinner(Context context) {
        super(context);
    }

    public EditSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        /*TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySpinner);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.MySpinner_width_view, 0);
        typedArray.recycle();*/
    }

    public EditSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);

        mWidth = getWidth();
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

                    if (popContentView.getOnItemLongClickListener() == null && onItemLongClickListener != null) {
                        popContentView.setOnItemLongClickListener(onItemLongClickListener);
                    }
                }
            }
        });
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
//        mDropView.setFocusable(true);
        mDropView.setOutsideTouchable(true);
        mDropView.setOutsideTouchable(true);
        mDropView.setTouchable(true);
        container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (TextUtils.isEmpty(getText().toString())) {
                        if (mDropView.isShowing()) {
                            dismissPop();
                        } else {
                            showPop();
                        }
                    }
                }
                return false;
            }
        });
        mDropView.update();
    }


    public void setAdapter(TextSpinnerAdapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            if (popContentView != null)
                popContentView.setAdapter(this.adapter);
        }

    }


    public void showPop() {
        if (adapter == null)
            return;
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

    public void dismissPop() {
        if (mDropView.isShowing()) {
            mDropView.dismiss();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public void setOnItemSelectedListener(AdapterView.OnItemClickListener listener) {
        if (listener != null) {
            this.onItemClickListener = listener;
            if (popContentView != null)
                popContentView.setOnItemClickListener(listener);
        }

    }

    public void setOnItemLongSelectecListener(AdapterView.OnItemLongClickListener listener) {
        if (listener != null) {
            this.onItemLongClickListener = listener;
            if (popContentView != null)
                popContentView.setOnItemLongClickListener(listener);
        }
    }

}
