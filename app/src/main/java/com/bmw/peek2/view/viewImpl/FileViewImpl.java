package com.bmw.peek2.view.viewImpl;
import com.bmw.peek2.model.PipeDefectImage;

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

    void setSeekbarMax(int max);
    void setPlayPlace(int progress);
    void showPicXmlInfo(PipeDefectImage pipeDefectImages);

}
