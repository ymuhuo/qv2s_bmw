package com.bmw.peek2.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bmw.peek2.R;

import java.util.ArrayList;

/**
 * Created by admin on 2017/11/22.
 */

public class SpinnerAdapter extends ArrayAdapter {

    private LayoutInflater infalter;
    private int resource;
    private int textViewResourceId;

    private String[]  target;

    public SpinnerAdapter(Context context, int resource,
                          int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);

        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        target = objects;

        infalter = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = infalter.inflate(resource, null);
        TextView text = (TextView) convertView
                .findViewById(textViewResourceId);
        text.setText(target[position]);
        text.setTextColor(getContext().getResources().getColor(R.color.colorText));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        if (convertView == null)
            convertView = infalter.inflate(
                    android.R.layout.simple_list_item_1, null);
        TextView text = (TextView) convertView
                .findViewById(android.R.id.text1);
        text.setText(target[position]);
        text.setTextColor(getContext().getResources().getColor(R.color.colorText));
        return convertView;
    }


}