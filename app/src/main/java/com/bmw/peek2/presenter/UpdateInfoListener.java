package com.bmw.peek2.presenter;

import com.bmw.peek2.model.UpdateInfo;

/**
 * Created by admin on 2016/9/21.
 */
public interface UpdateInfoListener {
    void setUpdateInfo(UpdateInfo updateInfo);
    void UpdateFalse();
    void finish();
}
