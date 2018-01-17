package com.bmw.peek2.presenter.impl;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.presenter.VideoPlayerPresenter;
import com.bmw.peek2.utils.BitmapUtils;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.UrlUtil;
import com.bmw.peek2.view.viewImpl.FileViewImpl;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.File;
import java.io.IOException;

import static com.bmw.peek2.utils.TimeUtil.formatTime;

/**
 * Created by admin on 2017/5/2.
 */

public class VideoPlayerPresentImpl implements VideoPlayerPresenter {
    private final int iPort;
    private boolean isStartPlay;
    private boolean isPlaying;
    private FileViewImpl fileView;
    private String mPath;
    private SeekBar seekBar;

    private VideoView videoView;
    private long allVideoTime;


    public VideoPlayerPresentImpl(FileViewImpl fileView, String mPath, VideoView videoView, SeekBar seekBar) {
        iPort = Player.getInstance().getPort();
        this.fileView = fileView;
        this.mPath = mPath;
        this.videoView = videoView;
        this.seekBar = seekBar;
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long time = mp.getDuration();
                allVideoTime = time;
                VideoPlayerPresentImpl.this.fileView.showAllPlayTime(formatTime(time, false));
                VideoPlayerPresentImpl.this.seekBar.setMax((int) allVideoTime);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playStop();
            }
        });

    }

    @Override
    public void setPlayPath(String path) {
        mPath = path;
    }

    @Override
    public void playStart() {

        if (isStartPlay)
            if (!isPlaying) {
                videoView.start();
                isPlaying = true;
                fileView.isPlaying(true);
            } else {
                videoView.pause();
                isPlaying = false;
                fileView.isPlaying(false);
            }
        else {
            if (mPath != null) {
                fileView.setSeekbarProgress(0);
                startPlayVideo();
            }
        }
    }


    public void startPlayVideo() {

        if (mPath == null)
            return;

        if (isStartPlay) {
            videoView.stopPlayback();
        }

        videoView.setVideoURI(Uri.parse(mPath));


        videoView.setAlpha(1);


        videoView.start();

//        setFileAllTime();
        isStartPlay = true;
        fileView.isPlaying(true);
        isPlaying = true;
    }


   /* private void setFileAllTime() {
        long allTime = videoView.getDuration();
        LogUtil.log(" time = "+ allTime);

    }*/


    @Override
    public void playStop() {
//        if (isStartPlay) {
        videoView.stopPlayback();
        fileView.resetPlayView();
        isStartPlay = false;
        isPlaying = false;
//        }
    }

    @Override
    public String getPlayedTimeEx() {
        long time = videoView.getCurrentPosition();
        return formatTime(time, false);
    }

    public long getAllTime() {
        return allVideoTime;
    }

    @Override
    public void setVideoViewAlpha0() {
        if (videoView == null)
            return;
        videoView.setAlpha(0);
    }


    public void playCutPicture() {
        FileUtil.capturePathIsExist();
        if (isStartPlay) {
            playStart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaMetadataRetriever rev = new MediaMetadataRetriever();

                    rev.setDataSource(BaseApplication.context(), Uri.parse(mPath)); //这里第一个参数需要Context，传this指针

                    Bitmap saveBitmap = rev.getFrameAtTime(videoView.getCurrentPosition() * 1000,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                    StringBuilder saveNameBuilder = new StringBuilder();
                    if (mPath != null && mPath.contains(".")) {
                        String nameFromPaht = mPath.substring(mPath.lastIndexOf("/") + 1, mPath.length());
                        if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 1) {
                            saveNameBuilder.append(getCountName()).append("-");
                        }
                        saveNameBuilder.append("cut(");
                        saveNameBuilder.append(nameFromPaht.substring(0, nameFromPaht.lastIndexOf(".")));

                    } else {
                        LogUtil.error("截图路径错误！");
                        return;
                    }


                    final String savePath = FileUtil.getFileSavePath() + Login_info.local_picture_path;


                    File file = new File(savePath + saveNameBuilder.toString() + ").jpg");
                    if (file.exists()) {
                        int num = 0;
                        while (file.exists()) {
                            num++;
                            file = new File(savePath + saveNameBuilder.toString() + "-" + num + ").jpg");
                        }
                        saveNameBuilder.append("-").append(num);
                    }
//                    saveNameBuilder.append(".jpg");
                    saveNameBuilder.append(")");
                    final String saveName = saveNameBuilder.toString();
                    try {
                        BitmapUtils.save(savePath, saveName + ".jpg", saveBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (saveBitmap != null)
                            saveBitmap.recycle();

                        saveBitmap = null;
                    }

                    if (BaseApplication.getSharedPreferences().getInt(Constant.KEY_FILE_NAME_MODEL_MINESET, 0) == 1) {
                        int count = BaseApplication.getSharedPreferences().getInt(Constant.KEY_VIDEO_FILE_COUNT, 1);
                        BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_PICTURE_FILE_COUNT, count + 1).commit();
                    }

                    fileView.capturePicture(savePath, saveName);

                }
            }).start();

        }
    }


    private String getCountName() {
        int count = BaseApplication.getSharedPreferences().getInt(Constant.KEY_PICTURE_FILE_COUNT, 1);

        if (count == 10000) {
            BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_PICTURE_FILE_COUNT, 1).commit();
            return "00";
        }


        if (count < 10) {
            return "0" + count;
        }

        return String.valueOf(count);
    }


    public void setPlayPlace(float place) {
        int currentTime = (int) (place);
        videoView.seekTo(currentTime);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public boolean isStartPlay() {
        return isStartPlay;
    }

    public void release() {
        isStartPlay = false;
        isPlaying = false;
        videoView.destroyDrawingCache();
    }
}
