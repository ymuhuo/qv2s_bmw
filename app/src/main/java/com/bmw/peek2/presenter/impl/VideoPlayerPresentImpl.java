package com.bmw.peek2.presenter.impl;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.model.VideoInfo;
import com.bmw.peek2.model.CapturePicture;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.model.PipeDefectImage;
import com.bmw.peek2.presenter.VideoPlayerPresenter;
import com.bmw.peek2.utils.BitmapUtils;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.PullXmlParseUtil;
import com.bmw.peek2.utils.StringUtils;
import com.bmw.peek2.view.view.CustomPotSeekBar;
import com.bmw.peek2.view.viewImpl.FileViewImpl;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    private CustomPotSeekBar mCustomPotSeekBar;
    private VideoInfo mVideoInfo;
    private ArrayList<String> mQuexianNames;
    private ArrayList<Integer> mQuexianPlaces;
    private ArrayList<PipeDefectImage> mPipeDefectImages;

    public VideoPlayerPresentImpl(final FileViewImpl fileView, String mPath, VideoView iVideoView, SeekBar seekBar, CustomPotSeekBar customPotSeekBar) {
        iPort = Player.getInstance().getPort();
        this.fileView = fileView;
        this.mPath = mPath;
        this.videoView = iVideoView;
        this.seekBar = seekBar;
        mCustomPotSeekBar = customPotSeekBar;
        mQuexianNames = new ArrayList<>();
        mQuexianPlaces = new ArrayList<>();
        mPipeDefectImages = new ArrayList<>();
        initCustomPotSeekbar();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long time = mp.getDuration();
                allVideoTime = time;
                VideoPlayerPresentImpl.this.fileView.showAllPlayTime(formatTime(time, false));
//                VideoPlayerPresentImpl.this.seekBar.setMax((int) allVideoTime);
                fileView.setSeekbarMax((int) allVideoTime);
                if (mCustomPotSeekBar != null)
                    mCustomPotSeekBar.post(new Runnable() {
                        @Override
                        public void run() {

//                            LogUtil.log("xml适当放松的立法 mQuexianNames = " + mQuexianNames.get(1) + " " + mQuexianPlaces.get(1));
                            mCustomPotSeekBar.initData(mQuexianNames, mQuexianPlaces);
//                            mCustomPotSeekBar.initData(null, null);
                        }
                    });

            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()

        {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playStop();
            }
        });
    }

    private void initCustomPotSeekbar() {
        mCustomPotSeekBar.setResponseOnTouch(new CustomPotSeekBar.ResponseOnTouch() {
            @Override
            public void onTouchResponse(int progress) {
                fileView.setPlayPlace(mQuexianPlaces.get(progress));
                fileView.showPicXmlInfo(mPipeDefectImages.get(progress));
                seekBar.setProgress(mQuexianPlaces.get(progress));
            }

            @Override
            public void onPlayToPot(int position) {
                fileView.showPicXmlInfo(mPipeDefectImages.get(position));
            }
        });
    }

    @Override
    public void setPlayPath(String path) {
        mPath = path;

        initPotPlace(mPath);

    }

    private void initPotPlace(String mPath) {
        mQuexianPlaces.clear();
        mQuexianNames.clear();
        mPipeDefectImages.clear();
        String xmlPath = FileUtil.replacePostFix(mPath, ".xml");
        LogUtil.log("xml适当放松的立法 path = " + xmlPath);
        final File file = new File(xmlPath);
        if (file.exists()) {
            mCustomPotSeekBar.setVisibility(View.VISIBLE);
            BaseApplication.MAIN_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    mVideoInfo = PullXmlParseUtil.getVideoRecordXml(file.getAbsolutePath());
                    ArrayList<CapturePicture> capturePictures = mVideoInfo.getCapturePictures();
                    for (int i = 0; i < capturePictures.size(); i++) {
                        CapturePicture capturePicture = capturePictures.get(i);
                        if (!StringUtils.isStringEmpty(capturePicture.getDefectFilename())) {
                            mQuexianNames.add(capturePicture.getPipedefectCode());
                            int time = Integer.valueOf(capturePicture.getTimestamp());
                            if (time >= 0)
                                time = time - 1;
                            mQuexianPlaces.add(time * 1000);
                            PipeDefectImage pipeDefectImage = PullXmlParseUtil.getPicQueXianXml(capturePicture.getDefectFilename());
                            mPipeDefectImages.add(pipeDefectImage);

                        }
                    }

                }
            });
        }else {
            mCustomPotSeekBar.setVisibility(View.GONE);
        }
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
