package com.bmw.peek2.view.viewImpl;


import android.support.v4.app.DialogFragment;

/**
 * Created by admin on 2016/8/29.
 */
public interface PreviewImpl {
    void iToast(String msg);
    void record(int which,boolean isOk);
    void capture(String path);
    void initHCNetSdkFaild();
    void loginSuccessful();
    void kanbanAdding();
    void kanbanAddFinish();

}
