package com.bmw.peek2.view.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bmw.peek2.jna.SystemTransformByJNA;
import com.bmw.peek2.jna.SystemTransformJNAInstance;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.view.ui.PreviewActivity;

public class AutoStartReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    public AutoStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Intent intentStart = new Intent(context, PreviewActivity.class);
            intentStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intentStart);
        }
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
            SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
            LogUtil.log("正在关机！");
        }
    }
}
