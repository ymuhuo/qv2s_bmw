package com.bmw.peek2.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bmw.peek2.R;
import com.bmw.peek2.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2017/3/3.
 */

public class TextSpinnerAdapter extends BaseAdapter {

    public Context mContext;
    private String[] strings;
    private int mViewHeight;

    public TextSpinnerAdapter(Context mContext) {
        this.mContext = mContext;
        strings = new String[0];
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
        notifyDataSetChanged();
    }



    public String getString(int position) {
        if (strings != null && position >= 0 && position < strings.length) {
            return strings[position];
        }
        return null;
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int i) {
        return strings[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public int getmViewHeight() {
        return mViewHeight;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = null;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item, viewGroup, false);
            textView = (TextView) view.findViewById(R.id.spinner_itemName);
            view.setTag(textView);
        } else {
            textView = (TextView) view.getTag();
        }

        textView.setText(strings[i]);
        return view;
    }

   /* private void measureView(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        mViewHeight = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();
    }*/


}
