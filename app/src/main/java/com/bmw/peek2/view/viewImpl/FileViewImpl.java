package com.bmw.peek2.view.viewImpl;

import android.support.v4.app.DialogFragment;

/**
 * Created by admin on 2016/9/8.
 */
public interface FileViewImpl {
    void isPlaying(boolean isPause);
    void setSeekbarProgress(int progress);
    void showPlayFailed();
    void showAllPlayTime(String time);
    void resetPlayView();
    void capturePicture(String path,String name);
    void setSpeedTv(int speed);
    void pictureClick(String path);
    void isBtnDeleteShow(boolean isShow);
    void setEmptyPicture();
    void setEditModel(boolean isEditModel);
    void setAllChooseBtn(boolean isAll);
    void startShowCopy(int size);
    void copyWhichFile(int position);
    void copyFinish();
//    void showDialogFragment(DialogFragment dialogFragment,String tag);
}
