package com.bmw.peek2.presenter;

/**
 * Created by admin on 2017/5/2.
 */

public interface VideoPlayerPresenter {

    void setPlayPath(String path);
    void playStart();
    void startPlayVideo();
    void playStop();
    String getPlayedTimeEx();
    void playCutPicture();
    void setPlayPlace(float place);
    boolean isPlaying();
    boolean isStartPlay();
    void release();
    long getAllTime();
    void setVideoViewAlpha0();
}
