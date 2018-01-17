package com.bmw.peek2.utils;

import android.content.Context;
import static com.bmw.peek2.view.ui.BaseActivity.log;

/**
 * Created by admin on 2017/6/9.
 */

public class NumberUtil {

    public static float getDecimalDit(float num, int ditNum) {
        return (float) Math.round(num * ditNum) / ditNum;
    }

    public static double getDecimalDit(double num, int ditNum) {
        return (double) Math.round(num * ditNum) / ditNum;
    }

    public static int getIntFromBytes(byte b) {
        return b & 0xff;
    }

    public static void printBytes(byte[] bytes, int[] enters) {
        if (bytes == null)
            return;
        for (int i = 0; i < bytes.length; i++) {
//            log("byte[" + i + "] = " + bytes[i]);
            for (int j = 0; j < enters.length; j++) {
                if (enters[j] == i) {
//                    log("\n");
                }
            }
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dipId2px(Context context, int dimenId) {
        float pxValue = context.getResources().getDimension(dimenId);
        return (int)pxValue;
    }

}
