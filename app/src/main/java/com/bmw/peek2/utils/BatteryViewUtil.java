package com.bmw.peek2.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.bmw.peek2.R;


/**
 * Created by admin on 2017/7/22.
 */

public class BatteryViewUtil {

    public static int getBatteryId(int batery){
        if(batery == 0){
            return R.drawable.ic_battery_0;
        }else if(batery<=10){
            return R.drawable.ic_battery_1;
        }else if(batery<=20){
            return R.drawable.ic_battery_2;
        }else if(batery<=30){
            return R.drawable.ic_battery_3;
        }else if(batery<=40){
            return R.drawable.ic_battery_4;
        }else if(batery<=50){
            return R.drawable.ic_battery_5;
        }else if(batery<=60){
            return R.drawable.ic_battery_6;
        }else if(batery<=70){
            return R.drawable.ic_battery_7;
        }else if(batery<=80){
            return R.drawable.ic_battery_8;
        }else if(batery<=90){
            return R.drawable.ic_battery_9;
        }else {
            return R.drawable.ic_battery_10;
        }
    }


    public static void setSystemBattery(final Context context, Intent intent, final TextView textView, final TextView title){
        if(textView == null)
            return;
        int rowLevel = intent.getIntExtra("level", -1);
        int scale = intent.getIntExtra("scale", -1);

        if (rowLevel != -1 && scale != -1) {
            final float sysBattery = ((float) rowLevel / scale) * 100;
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setBackgroundResource(getBatteryId((int) sysBattery));
                    textView.setText((int)sysBattery+"%");
                    if(sysBattery<=10){
                        textView.setTextColor(context.getResources().getColor(R.color.imageRed));
                        title.setTextColor(context.getResources().getColor(R.color.imageRed));
                    }else {

                        textView.setTextColor(context.getResources().getColor(R.color.white));
                        title.setTextColor(context.getResources().getColor(R.color.colorText));
                    }

                }
            });
        }
    }

    public static int setDeviceBattery(final Context context, Intent intent, final TextView textView, final TextView title){
        final int battery_num = intent.getIntExtra("batteryNum", -10000);
        if(battery_num != -10000 && textView!=null){
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setBackgroundResource(getBatteryId(battery_num));
                    textView.setText(battery_num+"%");
                    if(battery_num<=10){
                        textView.setTextColor(context.getResources().getColor(R.color.imageRed));
                        title.setTextColor(context.getResources().getColor(R.color.imageRed));
                    }else {

                        textView.setTextColor(context.getResources().getColor(R.color.white));
                        title.setTextColor(context.getResources().getColor(R.color.colorText));
                    }
                }
            });
        }
        return battery_num;
    }

}
