package com.bmw.peek2.view.viewImpl;

import com.bmw.peek2.model.UpdateInfo;

/**
 * Created by admin on 2017/4/7.
 */

public interface UpdateImpl {

    void update(String apkName);
    void getUpdateInfo(UpdateInfo updateInfo);
    void showUpdateProgress(int progress);
    void downloadSuccess(String apkName);
    void connectNetFalse();
    void noData();
}
